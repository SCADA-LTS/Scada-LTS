/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.dataSource.viconics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.EventDataSource;
import com.serotonin.mango.view.chart.ImageChartRenderer;
import com.serotonin.mango.view.conversion.Conversions;
import com.serotonin.mango.view.text.AnalogRenderer;
import com.serotonin.mango.view.text.BinaryTextRenderer;
import com.serotonin.mango.view.text.MultistateRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.viconics.ViconicsDataSourceVO;
import com.serotonin.mango.vo.dataSource.viconics.ViconicsPointLocatorVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.StringUtils;
import com.serotonin.viconics.RequestFailureException;
import com.serotonin.viconics.ViconicsConfigurationException;
import com.serotonin.viconics.ViconicsDevice;
import com.serotonin.viconics.ViconicsNetwork;
import com.serotonin.viconics.ViconicsNetworkListener;
import com.serotonin.viconics.ViconicsTransportException;
import com.serotonin.viconics.config.BinaryPoint;
import com.serotonin.viconics.config.MultistatePoint;
import com.serotonin.viconics.config.NumericPoint;
import com.serotonin.viconics.config.StatPoint;
import com.serotonin.viconics.config.Thermostat;
import com.serotonin.viconics.io.ViconicsIncomingResponse;
import com.serotonin.viconics.io.ViconicsOutgoingRequest;
import com.serotonin.web.i18n.LocalizableMessage;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.*;

/**
 * @author Matthew Lohbihler
 */
public class ViconicsDataSourceRT extends EventDataSource implements
		ViconicsNetworkListener {
	private final Object newDeviceLock = new Object();

	public static final int INITIALIZATION_EXCEPTION_EVENT = 1; // init,
																// configuration
	public static final int MESSAGE_EXCEPTION_EVENT = 2; // request failure,
															// timeout,
															// transport
	public static final int DEVICE_OFFLINE_EVENT = 3;
	public static final int NETWORK_OFFLINE_EVENT = 4;
	public static final int DUPLICATE_COMM_ADDRESS_EVENT = 5;

	private final Log log = LogFactory.getLog(ViconicsDataSourceRT.class);
	private final ViconicsDataSourceVO vo;
	private ViconicsNetwork network;
	private final Map<PointKey, DataPointRT> pointLookup = new ConcurrentHashMap<PointKey, DataPointRT>();

	public ViconicsDataSourceRT(ViconicsDataSourceVO vo) {
		super(vo);
		this.vo = vo;
	}

	//
	// Lifecycle
	//
	@Override
	public void initialize() {
		try {
			network = new ViconicsNetwork(vo.getCommPortId(), Common.timer);
		} catch (ViconicsConfigurationException e) {
			raiseEvent(INITIALIZATION_EXCEPTION_EVENT,
					System.currentTimeMillis(), true, new LocalizableMessage(
							"event.initializationError", e.getMessage()));
			return;
		}

		network.setTimeout(vo.getTimeout());
		network.setRetries(vo.getRetries());
		network.setNetworkWarningExpiry(vo.getNetworkTimeoutSeconds() * 1000);
		network.setDeviceWarningExpiry(vo.getDeviceWarningTimeoutSeconds() * 1000);
		network.setDeviceRemoveExpiry(vo.getDeviceRemoveTimeoutSeconds() * 1000);
		network.setPointValueMinimumFreshness(vo
				.getPointValueMinimumFreshnessSeconds() * 1000);
		network.addListener(this);

		try {
			network.init();
			network.startNetwork(vo.getPanId(), vo.getChannel());
		} catch (Exception e) {
			raiseEvent(INITIALIZATION_EXCEPTION_EVENT,
					System.currentTimeMillis(), true, new LocalizableMessage(
							"event.initializationError", e.getMessage()));
			return;
		}

		// If we've made it this far, things should be pretty much ok.
		// Deactivate any existing event.
		returnToNormal(INITIALIZATION_EXCEPTION_EVENT,
				System.currentTimeMillis());

		super.initialize();
	}

	@Override
	public void addDataPoint(DataPointRT dataPoint) {
		super.addDataPoint(dataPoint);

		// Mark the point as unreliable.
		setUnreliableDataPoint(dataPoint);

		// Add the point to the lookup map.
		pointLookup.put(new PointKey(dataPoint), dataPoint);
	}

	@Override
	public void removeDataPoint(DataPointRT dataPoint) {
		super.removeDataPoint(dataPoint);

		// Remove the point from the lookup map.
		pointLookup.remove(new PointKey(dataPoint));
	}

	@Override
	public void terminate() {
		if (network != null)
			network.removeListener(this);

		super.terminate();

		if (network != null)
			network.destroy();
	}

	//
	// / ViconicsNetworkListener
	//
	public void viconicsNetworkStatus(boolean online) {
		if (online)
			returnToNormal(NETWORK_OFFLINE_EVENT, System.currentTimeMillis());
		else
			raiseEvent(NETWORK_OFFLINE_EVENT, System.currentTimeMillis(), true,
					new LocalizableMessage("event.viconics.networkOffline"));
	}

	public void viconicsDeviceAdded(ViconicsDevice device) {
		log.info("Device added: " + device.getIeeeString());

		synchronized (newDeviceLock) {
			Thermostat tstat = device.getConfiguration();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String watchListName = tstat.getModel() + "_"
					+ device.getCommAddress();
			String folderName = watchListName + " - added "
					+ sdf.format(new Date());

			// Get a list of all existing points.
			DataPointDao dataPointDao = new DataPointDao();
			UserDao userDao = new UserDao();
			WatchListDao watchListDao = new WatchListDao();
			List<DataPointVO> points = dataPointDao.getDataPoints(vo.getId(),
					null);

			// Add a point for each address if it doesn't already exist.
			int folderId = -1;
			List<WatchList> watchlists = null;

			for (int address : tstat.getAddresses()) {
				boolean found = false;
				for (DataPointVO pointVO : points) {
					ViconicsPointLocatorVO locator = pointVO.getPointLocator();
					if (Arrays
							.equals(locator.getDeviceIeee(), device.getIeee())
							&& locator.getPointAddress() == address) {
						found = true;
						break;
					}
				}

				if (found)
					// Point already exists. Skip it.
					continue;

				//
				// Point hierarchy folder
				if (folderId == -1) {
					PointHierarchy pointHierarchy = dataPointDao
							.getPointHierarchy();

					PointFolder root = pointHierarchy.getRoot();
					PointFolder folder = null;

					// See if a folder with this name already exists.
					for (PointFolder f : root.getSubfolders()) {
						if (folderName.equals(f.getName())) {
							folder = f;
							break;
						}
					}

					if (folder == null) {
						// Create a hierarchy folder in which to put this stuff.
						folder = new PointFolder();
						folder.setName(folderName);

						pointHierarchy.addPointFolder(folder, pointHierarchy
								.getRoot().getId());
						dataPointDao.savePointHierarchy(pointHierarchy
								.getRoot());
					}

					folderId = folder.getId();
				}

				// Create the point.
				StatPoint pointConfig = tstat.getPoint(address);

				DataPointVO dp = new DataPointVO();
				dp.setXid(tstat.getModel() + "_"
						+ StatPoint.addressToString(address) + "_"
						+ System.currentTimeMillis());
				dp.setName(pointConfig.getName() + " ("
						+ device.getCommAddress() + ")");
				dp.setDataSourceId(vo.getId());
				dp.setEnabled(true);
				dp.setPointFolderId(folderId);
				dp.setLoggingType(LoggingTypes.ON_CHANGE);
				dp.setChartRenderer(new ImageChartRenderer(
						Common.TimePeriods.HOURS, 1));
				dp.setEventDetectors(new ArrayList<PointEventDetectorVO>());

				ViconicsPointLocatorVO locator = new ViconicsPointLocatorVO();
				locator.setDeviceIeee(device.getIeee());
				locator.setDeviceCommAddress(device.getCommAddress());
				locator.setPointAddress(address);
				locator.setSettable(pointConfig.isWritable());
				dp.setPointLocator(locator);

				if (pointConfig instanceof NumericPoint) {
					NumericPoint numericConfig = (NumericPoint) pointConfig;

					int precision = numericConfig.getPrecision();
					String format;
					if (precision == 0)
						format = "#";
					else
						format = "#." + StringUtils.pad("", '#', precision);

					locator.setDataTypeId(DataTypes.NUMERIC);

					if (numericConfig.isFahrenheit()) {
						dp.setEngineeringUnits(EngineeringUnits.degreesFahrenheit
								.intValue());
						if (vo.isConvertToCelsius())
							dp.setTextRenderer(new AnalogRenderer(format,
									" &deg;C"));
						else
							dp.setTextRenderer(new AnalogRenderer(format,
									" &deg;F"));
					} else
						dp.setTextRenderer(new AnalogRenderer(format, " "
								+ numericConfig.getUnits()));
				} else if (pointConfig instanceof BinaryPoint) {
					BinaryPoint binaryConfig = (BinaryPoint) pointConfig;

					dp.setTextRenderer(new BinaryTextRenderer(binaryConfig
							.getFalseText(), "#222222", binaryConfig
							.getTrueText(), "#000000"));

					locator.setDataTypeId(DataTypes.BINARY);
				} else if (pointConfig instanceof MultistatePoint) {
					MultistatePoint multistateConfig = (MultistatePoint) pointConfig;

					MultistateRenderer r = new MultistateRenderer();
					int i = 0;
					for (String label : multistateConfig.getLabels())
						r.addMultistateValue(i++, label, "#000000");
					dp.setTextRenderer(r);

					locator.setDataTypeId(DataTypes.MULTISTATE);
				} else
					throw new ShouldNeverHappenException("Unknown point type: "
							+ pointConfig.getClass());

				// Add the point.
				Common.ctx.getRuntimeManager().saveDataPoint(dp);
				log.info("Point added: " + dp.getXid());

				//
				// Watchlists
				if (pointConfig.isWatchlist()) {
					if (watchlists == null) {
						// Initialize the list of watchlists
						watchlists = new ArrayList<WatchList>();

						for (User user : userDao.getActiveUsers()) {
							if (!Permissions.hasDataSourcePermission(user,
									vo.getId()))
								continue;

							// Look for an existing watchlist with the same name
							// as the folder
							WatchList watchList = null;
							for (WatchList wl : watchListDao.getWatchLists(
									user.getId(), user.getUserProfile())) {
								if (watchListName.equals(wl.getName())) {
									watchList = wl;
									break;
								}
							}

							if (watchList == null) {
								// Didn't find an existing watchlist, so create
								// one
								watchList = new WatchList();
								watchList.setName(watchListName);
								watchListDao.createNewWatchList(watchList,
										user.getId());
							}

							watchlists.add(watchList);
						}
					}

					// Add the point to all of the watchlists.
					for (WatchList watchList : watchlists)
						watchList.getPointList().add(dp);
				}
			}

			// Save the watchlists
			if (watchlists != null) {
				for (WatchList watchList : watchlists)
					watchListDao.saveWatchList(watchList);
			}
		}
	}

	public void viconicsDeviceStatus(ViconicsDevice device, boolean online) {
		if (online)
			returnToNormal(DEVICE_OFFLINE_EVENT, System.currentTimeMillis());
		else {
			raiseEvent(DEVICE_OFFLINE_EVENT, System.currentTimeMillis(), true,
					new LocalizableMessage("event.viconics.deviceOffline",
							device.getIeeeString()));
		}
	}

	public void viconicsDeviceRemoved(ViconicsDevice device) {
		log.info("Device removed: " + device.getIeeeString());

		// Don't disable the points.

		// // Make a local copy of the point list so that the point can be
		// removed from the real list.
		// List<DataPointRT> points = new ArrayList<DataPointRT>(dataPoints);
		//
		// // Disable all points under this device.
		// for (DataPointRT rt : points) {
		// DataPointVO dp = rt.getVO();
		// ViconicsPointLocatorVO locator = dp.getPointLocator();
		// if (Arrays.equals(locator.getDeviceIeee(), device.getIeee())) {
		// dp.setEnabled(false);
		// Common.ctx.getRuntimeManager().saveDataPoint(dp);
		// }
		// }
	}

	public void viconicsReceivedException(Exception e) {
		raiseEvent(
				MESSAGE_EXCEPTION_EVENT,
				System.currentTimeMillis(),
				true,
				new LocalizableMessage("event.viconics.messagingException", e
						.getMessage()));
	}

	public void viconicsDevicePointUpdated(ViconicsDevice device,
			StatPoint point, int deviceValue, long time) {
		DataPointRT rt = pointLookup.get(new PointKey(device.getIeee(), point
				.getAddress()));
		if (rt == null)
			// Not active. Ignore the update.
			return;

		MangoValue mangoValue;
		int dataTypeId = rt.getVO().getPointLocator().getDataTypeId();
		if (point instanceof NumericPoint) {
			NumericPoint numericConfig = (NumericPoint) point;
			if (dataTypeId != DataTypes.NUMERIC)
				throw new ShouldNeverHappenException("Data type mismatch: "
						+ point.getClass() + ", type=" + dataTypeId);

			double d = numericConfig.fromDeviceFormat(deviceValue);
			if (vo.isConvertToCelsius() && numericConfig.isFahrenheit())
				d = Conversions.fahrenheitToCelsius(d);
			mangoValue = new NumericValue(d);
		} else if (point instanceof BinaryPoint) {
			BinaryPoint binaryConfig = (BinaryPoint) point;
			if (dataTypeId != DataTypes.BINARY)
				throw new ShouldNeverHappenException("Data type mismatch: "
						+ point.getClass() + ", type=" + dataTypeId);

			mangoValue = new BinaryValue(
					binaryConfig.fromDeviceFormat(deviceValue));
		} else if (point instanceof MultistatePoint) {
			MultistatePoint multistateConfig = (MultistatePoint) point;
			if (dataTypeId != DataTypes.MULTISTATE)
				throw new ShouldNeverHappenException("Data type mismatch: "
						+ point.getClass() + ", type=" + dataTypeId);

			mangoValue = new MultistateValue(
					multistateConfig.fromDeviceFormat(deviceValue));
		} else
			throw new ShouldNeverHappenException("Unknown point type: "
					+ point.getClass());

		rt.updatePointValue(new PointValueTime(mangoValue, time));
		resetUnreliableDataPoint(rt);
	}

	public void viconicsDuplicateCommAddressDetected(int commAddress) {
		raiseEvent(DUPLICATE_COMM_ADDRESS_EVENT, System.currentTimeMillis(),
				true, new LocalizableMessage(
						"event.viconics.messagingException", commAddress));
	}

	//
	// Misc
	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime pvt,
			SetPointSource source) {
		ViconicsPointLocatorVO locator = dataPoint.getVO().getPointLocator();

		Object value = pvt.getValue().getObjectValue();

		if (vo.isConvertToCelsius()) {
			ViconicsDevice device = network.getDeviceWithIeee(locator
					.getDeviceIeee());
			StatPoint pointConfig = device.getConfiguration().getPoint(
					locator.getPointAddress());
			if (pointConfig instanceof NumericPoint) {
				NumericPoint numericPoint = (NumericPoint) pointConfig;
				if (numericPoint.isFahrenheit())
					// Need to convert back to fahrenheit.
					value = Conversions.celsiusToFahrenheit((Double) value);
			}
		}

		try {
			network.writeValue(locator.getDeviceIeee(),
					locator.getPointAddress(), value);
			dataPoint.setPointValue(pvt, source);
			returnToNormal(MESSAGE_EXCEPTION_EVENT, System.currentTimeMillis(), dataPoint);
		} catch (Exception e) {
			raiseEvent(
					MESSAGE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.setPointFailed", e
							.getMessage()), dataPoint);
		}
	}

	@Override
	public void forcePointRead(DataPointRT dataPoint) {
		ViconicsPointLocatorVO locator = dataPoint.getVO().getPointLocator();

		try {
			// Ignore the result. It will be sent via the listener anyway.
			network.readValue(locator.getDeviceIeee(),
					locator.getPointAddress());
			returnToNormal(MESSAGE_EXCEPTION_EVENT, System.currentTimeMillis(), dataPoint);
		} catch (Exception e) {
			raiseEvent(
					MESSAGE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.readPointFailed", e
							.getMessage()), dataPoint);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends ViconicsIncomingResponse> T send(
			ViconicsOutgoingRequest request) throws ViconicsTransportException,
			RequestFailureException {
		return (T) network.send(request);
	}

	public List<ViconicsDevice> getDevices() {
		return network.getDevices();
	}

	class PointKey {
		private final byte[] deviceIeee;
		private final int address;

		public PointKey(DataPointRT rt) {
			ViconicsPointLocatorVO locator = rt.getVO().getPointLocator();
			deviceIeee = locator.getDeviceIeee();
			address = locator.getPointAddress();
		}

		public PointKey(byte[] deviceIeee, int address) {
			this.deviceIeee = deviceIeee;
			this.address = address;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + address;
			result = prime * result + Arrays.hashCode(deviceIeee);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final PointKey other = (PointKey) obj;
			if (address != other.address)
				return false;
			if (!Arrays.equals(deviceIeee, other.deviceIeee))
				return false;
			return true;
		}
	}
}
