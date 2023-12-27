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

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import com.serotonin.mango.vo.dataSource.modbus.ModbusIpDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusIpDataSourceVO.TransportType;
import com.serotonin.mango.vo.dataSource.modbus.ModbusPointLocatorVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.cache.DataSourcePointsCache;

import java.util.ArrayList;
import java.util.List;

public class ModbusIpDataSource extends ModbusDataSource {
	private final ModbusIpDataSourceVO configuration;
	private ModbusMaster modbusMaster;
	public static final Log LOG = LogFactory.getLog(ModbusIpDataSource.class);
	private DataPointRT socketMonitor;

	public ModbusIpDataSource(ModbusIpDataSourceVO configuration) {
		super(configuration);
		this.configuration = configuration;
	}

	@Override
	public void addDataPoint(DataPointRT dataPoint) {
		LOG.trace("Super.add");
		super.addDataPoint(dataPoint);
		ModbusPointLocatorVO locatorVO = dataPoint.getVO().getPointLocator();

		// Slave monitor points.
		if (configuration.isCreateSocketMonitorPoint() && socketMonitor == null) {
			if (locatorVO.isSocketMonitor()) {
				// The monitor for this slave. Set it in the map.
				socketMonitor = dataPoint;
				LOG.trace("socketMonitor was found!");
			} else {
				// Check if a monitor point already exists.
				LOG.trace("Check if monitor already exists!");
				DataPointDao dataPointDao = new DataPointDao();
				boolean found = false;

				List<DataPointVO> points;
				if (DataSourcePointsCache.getInstance().isCacheEnabled()) {
					points = DataSourcePointsCache.getInstance().getDataPoints((long) configuration.getId());
				} else {
					points = dataPointDao.getDataPoints(
						configuration.getId(), null);
				}
				for (DataPointVO dp : points) {
					ModbusPointLocatorVO loc = dp.getPointLocator();
					LOG.trace("current dp: " + dp.getName());
					LOG.trace("current dp is lock? " + loc.isSocketMonitor());
					if (loc.isSocketMonitor()) {
						found = true;
						break;
					}
				}

				if (!found) {
					// A monitor was not found, so create one
					LOG.trace("socketMonitor not found! Create one!");

					DataPointVO dp = new DataPointVO();
					dp.setXid(dataPointDao.generateUniqueXid());
					dp.setName(Common
							.getMessage("dsEdit.modbusIp.socketPointName"));
					dp.setDataSourceId(configuration.getId());
					dp.setEnabled(true);
					dp.setLoggingType(LoggingTypes.ON_CHANGE);
					dp.setEventDetectors(new ArrayList<PointEventDetectorVO>());

					ModbusPointLocatorVO locator = new ModbusPointLocatorVO();
					locator.setSlaveId(0);
					locator.setSocketMonitor(true);
					dp.setPointLocator(locator);

					Common.ctx.getRuntimeManager().saveDataPoint(dp);
					LOG.info("Socket Monitor point added: " + dp.getXid());
				}
			}
		}
	}

	@Override
	protected void doPoll(long time) {
		// TODO: Update Socket Monitor
		LOG.trace("Polling...");
		if (!modbusMaster.isInitialized()) {
			if (configuration.isCreateSocketMonitorPoint()) {
				// Set the socket monitor to offline
				if (socketMonitor != null) {
					PointValueTime oldValue = socketMonitor.getPointValue();
					if (oldValue == null || oldValue.getBooleanValue())
						socketMonitor.setPointValue(new PointValueTime(false,
								time), null);
				}
			}
		} else {
			if (configuration.isCreateSocketMonitorPoint()) {
				// Set the socket monitor to offline
				LOG.trace("Socket Monitor!");
				if (socketMonitor != null) {
					LOG.trace("socketMonitor value: "
							+ modbusMaster.isConnected());
					socketMonitor
							.setPointValue(
									new PointValueTime(modbusMaster
											.isConnected(), time), null);
				}
			}
		}

		super.doPoll(time);
	}

	@Override
	public void removeDataPoint(DataPointRT dataPoint) {
		synchronized (pointListChangeLock) {
			super.removeDataPoint(dataPoint);

			// If this is a socket monitor point being removed, also remove it
			// from the map.
			ModbusPointLocatorVO locatorVO = dataPoint.getVO()
					.getPointLocator();
			if (locatorVO.isSocketMonitor())
				socketMonitor = null;
		}
	}

	//
	//
	// Lifecycle
	//
	//
	@Override
	public void initialize() {
		IpParameters params = new IpParameters();
		params.setHost(configuration.getHost());
		params.setPort(configuration.getPort());
		params.setEncapsulated(configuration.isEncapsulated());

		if (configuration.getTransportType() == TransportType.TCP_LISTENER) {
			LOG.trace(toString() + " Create ModbusMaster - TCP_Listener!");
			modbusMaster = new ModbusFactory().createTcpListener(params);
		} else if (configuration.getTransportType() == TransportType.UDP)
			modbusMaster = new ModbusFactory().createUdpMaster(params);
		else
			modbusMaster = new ModbusFactory()
					.createTcpMaster(
							params,
							configuration.getTransportType() == TransportType.TCP_KEEP_ALIVE);

		super.initialize(modbusMaster);
	}

}
