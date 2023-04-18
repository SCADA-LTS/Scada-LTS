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
package com.serotonin.mango.rt.dataSource.modbus;

import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.serotonin.mango.vo.dataSource.modbus.ModbusPointLocatorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import com.serotonin.mango.vo.dataSource.modbus.ModbusDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusPointLocatorVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.sero.messaging.MessagingExceptionHandler;
import com.serotonin.modbus4j.sero.messaging.TimeoutException;
import com.serotonin.web.i18n.LocalizableMessage;

abstract public class ModbusDataSource extends PollingDataSource implements
		MessagingExceptionHandler {
	private final Log LOG = LogFactory.getLog(ModbusDataSource.class);
	private boolean eventRaised = false;

	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int POINT_WRITE_EXCEPTION_EVENT = 2;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 3;

	private ModbusMaster modbusMaster;
	private BatchRead<ModbusPointLocatorRT> batchRead;
	private final ModbusDataSourceVO<?> vo;
	private final Map<Integer, DataPointRT> slaveMonitors = new HashMap<Integer, DataPointRT>();

	public ModbusDataSource(ModbusDataSourceVO<?> vo) {
		super(vo);
		this.vo = vo;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(),
				vo.isQuantize());
	}

	@Override
	public void addDataPoint(DataPointRT dataPoint) {
		super.addDataPoint(dataPoint);

		// Mark the point as unreliable.
		ModbusPointLocatorVO locatorVO = dataPoint.getVO().getPointLocator();
		if (!locatorVO.isSlaveMonitor() && !locatorVO.isSocketMonitor())
			dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, true);

		// Slave monitor points.
		if (vo.isCreateSlaveMonitorPoints()) {
			int slaveId = locatorVO.getSlaveId();

			if (locatorVO.isSlaveMonitor()) {
				// The monitor for this slave. Set it in the map.
				slaveMonitors.put(slaveId, dataPoint);
			} else if (locatorVO.isSocketMonitor()) {
				// Do nothing
			} else if (!slaveMonitors.containsKey(slaveId)) {
				// A new slave. Add null to the map to ensure we don't do this
				// check again.
				slaveMonitors.put(slaveId, null);

				// Check if a monitor point already exists.
				DataPointDao dataPointDao = new DataPointDao();
				boolean found = false;

				List<DataPointVO> points = dataPointDao.getDataPoints(
						vo.getId(), null);
				for (DataPointVO dp : points) {
					ModbusPointLocatorVO loc = dp.getPointLocator();
					if (loc.getSlaveId() == slaveId && loc.isSlaveMonitor()) {
						found = true;
						break;
					}
				}

				if (!found) {
					// A monitor was not found, so create one
					DataPointVO dp = new DataPointVO();
					dp.setXid(dataPointDao.generateUniqueXid());
					dp.setName(Common.getMessage(
							"dsEdit.modbus.monitorPointName", slaveId));
					dp.setDataSourceId(vo.getId());
					dp.setEnabled(true);
					dp.setLoggingType(LoggingTypes.ON_CHANGE);
					dp.setEventDetectors(new ArrayList<PointEventDetectorVO>());

					ModbusPointLocatorVO locator = ModbusPointLocatorFactory.locator(this.vo);
					locator.setSlaveId(slaveId);
					locator.setSlaveMonitor(true);
					dp.setPointLocator(locator);

					Common.ctx.getRuntimeManager().saveDataPoint(dp);
					LOG.info("Monitor point added: " + dp.getXid());
				}
			}
		}
	}

	@Override
	public void removeDataPoint(DataPointRT dataPoint) {
		synchronized (pointListChangeLock) {
			super.removeDataPoint(dataPoint);

			// If this is a slave monitor point being removed, also remove it
			// from the map.
			ModbusPointLocatorVO locatorVO = dataPoint.getVO()
					.getPointLocator();
			if (locatorVO.isSlaveMonitor())
				slaveMonitors.put(locatorVO.getSlaveId(), null);
		}
	}

	@Override
	protected void doPoll(long time) {
		
		

		if (!modbusMaster.isInitialized()) {
			if (vo.isCreateSlaveMonitorPoints()) {
				// Set the slave monitors to offline
				for (DataPointRT monitor : slaveMonitors.values()) {
					if (monitor != null) {
						PointValueTime oldValue = monitor.getPointValue();
						if (oldValue == null || oldValue.getBooleanValue())
							monitor.setPointValue(new PointValueTime(false,
									time), null);
					}
				}
			}

			return;
		}

		ModbusPointLocatorRT locator;
		BaseLocator<?> modbusLocator;
		Object result;

		try {
			if (batchRead == null || pointListChanged) {
				pointListChanged = false;
				batchRead = new BatchRead<ModbusPointLocatorRT>();
				batchRead.setContiguousRequests(vo.isContiguousBatches());
				batchRead.setErrorsInResults(true);
				batchRead.setExceptionsInResults(true);

				for (DataPointRT dataPoint : dataPoints) {
					locator = dataPoint.getPointLocator();
					if (!locator.getVO().isSlaveMonitor()
							&& !locator.getVO().isSocketMonitor()) {
						modbusLocator = createModbusLocator(locator.getVO());
						batchRead.addLocator(locator, modbusLocator);
					}
				}
			}
		} catch (Exception e) {
		}

		try {
			BatchResults<ModbusPointLocatorRT> results = modbusMaster
					.send(batchRead);
			Map<Integer, Boolean> slaveStatuses = new HashMap<Integer, Boolean>();
			boolean dataSourceExceptions = false;

			for (DataPointRT dataPoint : dataPoints) {
				locator = dataPoint.getPointLocator();
				if (locator.getVO().isSlaveMonitor()
						|| locator.getVO().isSocketMonitor())
					continue;

				result = results.getValue(locator);

				if (result instanceof ExceptionResult) {
					ExceptionResult exceptionResult = (ExceptionResult) result;
					LOG.trace("Point: " + locator.getVO().getOffset()
							+ " Exception: "
							+ exceptionResult.getExceptionMessage());
					if (exceptionResult.getExceptionMessage().contains(
							"no active connection")) {
						LOG.trace("Cannot reach source, setting monitors to false");
						slaveStatuses.put(locator.getVO().getSlaveId(), false);
					} else {
						// Raise an event.
						raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true,
								new LocalizableMessage("event.exception2",
										dataPoint.getVO().getName(),
										exceptionResult.getExceptionMessage()));

						dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, true);

						// A response, albeit an undesirable one, was received
						// from
						// the slave, so it is online.

						slaveStatuses.put(locator.getVO().getSlaveId(), true);
					}
				} else if (result instanceof ModbusTransportException) {
					ModbusTransportException e = (ModbusTransportException) result;
					if (e.getMessage().contains("no active connection")) {
						// Raise an event.
						raiseEvent(
								DATA_SOURCE_EXCEPTION_EVENT,
								time,
								true,
								new LocalizableMessage(
										"event.modbus.noConnection", dataPoint
												.getVO().getDataSourceName(), e
												.getMessage()));
					} else {
						// Raise an event.
						raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
								getLocalExceptionMessage(e));
					}

					// Update the slave status. Only set to false if it is
					// not
					// true already.
					if (!slaveStatuses
							.containsKey(locator.getVO().getSlaveId()))
						slaveStatuses.put(locator.getVO().getSlaveId(), false);

					dataSourceExceptions = true;

					dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, true);
				} else {
					/*
					 * When an event is raised from the Callback
					 * receivedException() interface, points are updated with
					 * false or 0; The event is usually a NoKeyFound in the
					 * WaitingRoom, due to previous timeout.
					 * 
					 * The eventRaised flag prevents data being writen with
					 * invalid values in those cases.
					 * 
					 * Note: This usually avoids first poll if previous
					 * DataSource Exception Event was raised, but afterwards
					 * it´s effective. TODO: Treat this type of exception
					 * only... but how?!
					 */
					LOG.trace("Point: " + locator.getVO().getOffset()
							+ " eventRaised: " + eventRaised);
					if (!eventRaised) {
						returnToNormal(POINT_READ_EXCEPTION_EVENT, time);
						dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, false);
						updatePointValue(dataPoint, locator, result, time);
						slaveStatuses.put(locator.getVO().getSlaveId(), true);
					}
				}
			}

			eventRaised = false;

			if (vo.isCreateSlaveMonitorPoints()) {
				LOG.trace("Checking Monitors...");
				for (Map.Entry<Integer, Boolean> status : slaveStatuses
						.entrySet()) {
					DataPointRT monitor = slaveMonitors.get(status.getKey());
					if (monitor != null) {
						LOG.trace("Monitor: "
								+ monitor.getVO().getDataSourceName());
						boolean oldOnline = false;
						boolean newOnline = status.getValue();

						PointValueTime oldValue = monitor.getPointValue();
						if (oldValue != null)
							oldOnline = oldValue.getBooleanValue();
						else {
							// Make sure it gets set.
							oldOnline = !newOnline;
						}

						if (oldOnline != newOnline) {
							LOG.trace("Monitor.setPointValue(): " + newOnline);
							monitor.setPointValue(new PointValueTime(newOnline,
									time), null);
						}
					}
				}
			}

			if (!dataSourceExceptions)
				// Deactivate any existing event.
				returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);
		} catch (ErrorResponseException e) {
			// Should never happen because we set "errorsInResults" to true.
			throw new ShouldNeverHappenException(e);
		} catch (ModbusTransportException e) {
			// Should never happen because we set "exceptionsInResults" to true.
			throw new ShouldNeverHappenException(e);
		}
	}

	protected void initialize(ModbusMaster modbusMaster) {
		this.modbusMaster = modbusMaster;
		modbusMaster.setTimeout(vo.getTimeout());
		modbusMaster.setRetries(vo.getRetries());
		modbusMaster.setMaxReadBitCount(vo.getMaxReadBitCount());
		modbusMaster.setMaxReadRegisterCount(vo.getMaxReadRegisterCount());
		modbusMaster.setMaxWriteRegisterCount(vo.getMaxWriteRegisterCount());

		// Add this as a listener to exceptions that occur in the
		// implementation.
		modbusMaster.setExceptionHandler((MessagingExceptionHandler) this);

		try {
			modbusMaster.init();

			// Deactivate any existing event.
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis());
		} catch (Exception e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
					true, getLocalExceptionMessage(e));
			return;
		}

		super.initialize();
	}

	@Override
	public void forcePointRead(DataPointRT dataPoint) {

		if (!modbusMaster.isInitialized()) {
			// terminate();
			//
			// vo.setEnabled(false);
			//
			// RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
			//
			// for (DataPointRT dP : dataPoints) {
			// dP.getVO().setEnabled(false);
			// }
			//
			// for (DataPointRT dP : dataPoints) {
			// dP.getVO().setEnabled(true);
			// runtimeManager.saveDataPoint(dP.getVO());
			// }
			//
			// vo.setEnabled(true);
			// runtimeManager.saveDataSource(vo);
		}

		ModbusPointLocatorRT pl = dataPoint.getPointLocator();
		if (pl.getVO().isSlaveMonitor() || pl.getVO().isSocketMonitor())
			// Nothing to do
			return;

		BaseLocator<?> ml = createModbusLocator(pl.getVO());
		long time = System.currentTimeMillis();

		synchronized (pointListChangeLock) {
			try {
				Object value = modbusMaster.getValue(ml);

				returnToNormal(POINT_READ_EXCEPTION_EVENT, time);
				dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, false);
				updatePointValue(dataPoint, pl, value, time);
			} catch (ErrorResponseException e) {
				raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true,
						new LocalizableMessage("event.exception2", dataPoint
								.getVO().getName(), e.getMessage()));
				dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, true);
			} catch (ModbusTransportException e) {
				// Don't raise a data source exception. Polling should do that.
				LOG.warn("Error during forcePointRead", e);
				dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, true);
			}
		}
	}

	private void updatePointValue(DataPointRT dataPoint,
			ModbusPointLocatorRT pl, Object value, long time) {
		LOG.debug("Update Point: " + dataPoint.getVO().getDataSourceName()
				+ " Value: " + value);
		if (pl.getVO().getDataTypeId() == DataTypes.BINARY)
			dataPoint
					.updatePointValue(new PointValueTime((Boolean) value, time));
		else if (pl.getVO().getDataTypeId() == DataTypes.ALPHANUMERIC)
			dataPoint
					.updatePointValue(new PointValueTime((String) value, time));
		else {
			// Apply arithmetic conversions.
			double newValue = ((Number) value).doubleValue();
			newValue *= pl.getVO().getMultiplier();
			newValue += pl.getVO().getAdditive();
			dataPoint.updatePointValue(new PointValueTime(newValue, time));
		}
	}

	@Override
	public void terminate() {
		super.terminate();
		modbusMaster.destroy();
	}

	//
	//
	// Data source interface
	//
	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {
		LOG.debug("ModbusDataSourceRT::setPointValue");
		ModbusPointLocatorRT pl = dataPoint.getPointLocator();
		BaseLocator<?> ml = createModbusLocator(pl.getVO());

		try {
			// See if this is a numeric value that needs to be converted.
			if (dataPoint.getDataTypeId() == DataTypes.NUMERIC) {
				double convertedValue = valueTime.getDoubleValue();
				convertedValue -= pl.getVO().getAdditive();
				convertedValue /= pl.getVO().getMultiplier();
				LOG.debug("Setting value: " + convertedValue);
				modbusMaster.setValue(ml, convertedValue);
			} else if (dataPoint.getDataTypeId() == DataTypes.ALPHANUMERIC) {
				LOG.debug("Setting value: " + valueTime.getStringValue());
				modbusMaster.setValue(ml, valueTime.getStringValue());
			} else {
				LOG.debug("Setting value: " + valueTime.getBooleanValue());
				modbusMaster.setValue(ml, valueTime.getBooleanValue());
			}
			dataPoint.setPointValue(valueTime, source);

			// Deactivate any existing event.
			returnToNormal(POINT_WRITE_EXCEPTION_EVENT, valueTime.getTime());
		} catch (ModbusTransportException e) {
			if (e.getMessage().contains("no active connection")) {
				// Raise an event.
				raiseEvent(POINT_WRITE_EXCEPTION_EVENT, valueTime.getTime(),
						true, new LocalizableMessage(
								"event.modbus.noConnection", dataPoint.getVO()
										.getDataSourceName(), e.getMessage()));
			} else {
				// Raise an event.
				raiseEvent(POINT_WRITE_EXCEPTION_EVENT, valueTime.getTime(),
						true, new LocalizableMessage("event.exception2",
								dataPoint.getVO().getName(), e.getMessage()));
				LOG.info("Error setting point value", e);
			}
		} catch (ErrorResponseException e) {
			raiseEvent(POINT_WRITE_EXCEPTION_EVENT, valueTime.getTime(), true,
					new LocalizableMessage("event.exception2", dataPoint
							.getVO().getName(), e.getErrorResponse()
							.getExceptionMessage()));
			LOG.info("Error setting point value", e);
		}
	}

	public static BaseLocator<?> createModbusLocator(ModbusPointLocatorVO vo) {
		return BaseLocator.createLocator(vo.getSlaveId(), vo.getRange(),
				vo.getOffset(), vo.getModbusDataType(), vo.getBit(),
				vo.getRegisterCount(), Charset.forName(vo.getCharset()));
	}

	public static LocalizableMessage localExceptionMessage(Exception e) {
		if (e instanceof ModbusTransportException) {
			Throwable cause = e.getCause();
			if (cause instanceof TimeoutException)
				return new LocalizableMessage("event.modbus.noResponse",
						((ModbusTransportException) e).getSlaveId());
			if (cause instanceof ConnectException)
				return new LocalizableMessage("common.default", e.getMessage());
		}

		return DataSourceRT.getExceptionMessage(e);
	}

	protected LocalizableMessage getLocalExceptionMessage(Exception e) {
		return localExceptionMessage(e);
	}

	//
	//
	// MessagingConnectionListener interface
	//
	public void receivedException(Exception e) {
		LOG.debug("Modbus exception: " + e.getLocalizedMessage());
		// eventRaised = true; // DataPoint protection against invalid values.
		// If it´s used, DS with unstable connections won´t communicate at all!
		raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
				true,
				new LocalizableMessage("event.modbus.master", e.getMessage()));
	}
}
