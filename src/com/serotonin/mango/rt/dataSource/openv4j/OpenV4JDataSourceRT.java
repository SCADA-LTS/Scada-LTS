/*
 *   Mango - Open Source M2M - http://mango.serotoninsoftware.com
 *   Copyright (C) 2010 Arne Pl\u00f6se
 *   @author Arne Pl\u00f6se
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.dataSource.openv4j;

import gnu.io.SerialPort;

import java.util.Date;

import net.sf.openv4j.CycleTimes;
import net.sf.openv4j.ErrorListEntry;
import com.serotonin.mango.vo.dataSource.openv4j.OpenV4JDataSourceVO;
import net.sf.openv4j.protocolhandlers.ProtocolHandler;
import net.sf.openv4j.protocolhandlers.SegmentedDataContainer;
import net.sf.openv4j.protocolhandlers.SimpleDataContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * 
 * TODO datatype NUMERIC_INT is missing TODO Starttime for timpepoints ???
 * 
 */
public class OpenV4JDataSourceRT extends PollingDataSource {

    private final static Log LOG = LogFactory.getLog(OpenV4JDataSourceRT.class);
    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int POINT_READ_EXCEPTION_EVENT = 2;
    public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
    private final OpenV4JDataSourceVO vo;
    // private final long nextRescan = 0;
    private SerialPort sPort;
    private final ProtocolHandler protocolHandler = new ProtocolHandler();

    public OpenV4JDataSourceRT(OpenV4JDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    public void initialize() {
        LOG.info("INITIALIZE");
        super.initialize();
    }

    @Override
    public void terminate() {
        LOG.info("TERMINATE");
        super.terminate();
    }

    @Override
    protected synchronized void doPoll(long time) {
        final SegmentedDataContainer dc = new SegmentedDataContainer();
        for (DataPointRT point : dataPoints) {
            final OpenV4JPointLocatorRT locator = point.getPointLocator();
            dc.addToDataContainer(locator.getDataPoint());
        }

        if (openSerialPort()) {
            try {
                protocolHandler.setReadRequest(dc);
                synchronized (dc) {
                    try {
                        dc.wait(4000 * dc.getDataBlockCount());
                    }
                    catch (InterruptedException ex) {
                        raiseEvent(POINT_READ_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                                new LocalizableMessage("event.exception2", vo.getName(), ex.getMessage(), "HALLO"));
                    }
                }
                for (DataPointRT point : dataPoints) {
                    final OpenV4JPointLocatorRT locator = point.getPointLocator();
                    final Object decodedValue = locator.getDataPoint().decode(dc);
                    try {
                        if (decodedValue == null) {
                            throw new ShouldNeverHappenException("Got null value from "
                                    + locator.getVo().getDataPointName());
                        }
                        else if (decodedValue instanceof Number) {
                            if (decodedValue instanceof Double) {
                                point.updatePointValue(new PointValueTime((Double) decodedValue, time));
                            }
                            else if (decodedValue instanceof Byte) {
                                point.updatePointValue(new PointValueTime(((Byte) decodedValue).doubleValue(), time));
                            }
                            else if (decodedValue instanceof Short) {
                                point.updatePointValue(new PointValueTime(((Short) decodedValue).doubleValue(), time));
                            }
                            else if (decodedValue instanceof Integer) {
                                point.updatePointValue(new PointValueTime(((Integer) decodedValue).doubleValue(), time));
                            }
                        }
                        else if (decodedValue instanceof Boolean) {
                            point.updatePointValue(new PointValueTime((Boolean) decodedValue, time));
                        }
                        else if (decodedValue instanceof CycleTimes) {
                            point.updatePointValue(new PointValueTime(decodedValue.toString(), time));
                        }
                        else if (decodedValue instanceof ErrorListEntry) {
                            point.updatePointValue(new PointValueTime(decodedValue.toString(), time));
                        }
                        else if (decodedValue instanceof Date) {
                            point.updatePointValue(new PointValueTime(decodedValue.toString(), time));
                        }

                    }
                    catch (Exception ex) {
                        LOG.fatal("Error during saving: " + locator.getDataPoint(), ex);
                    }

                }
                returnToNormal(POINT_READ_EXCEPTION_EVENT, time);
                returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);

            }
            finally {
                closePort();
            }
        }
    }

    @Override
    public synchronized void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        final SimpleDataContainer dc = new SimpleDataContainer();
        final OpenV4JPointLocatorRT locator = dataPoint.getPointLocator();

        dc.addToDataContainer(locator.getDataPoint());
        if (locator.getVo().getDataTypeId() == DataTypes.NUMERIC) {
            locator.getDataPoint().encode(dc, valueTime.getValue().getDoubleValue());
            dataPoint.setPointValue(valueTime, source);
        }
        else {
            throw new IllegalArgumentException("Only Numeric datatypes are supported");
        }

        if (openSerialPort()) {
            try {
                protocolHandler.setWriteRequest(dc);
                synchronized (dc) {
                    try {
                        dc.wait(5000 * dc.getDataBlockCount());
                    }
                    catch (InterruptedException ex) {
                        raiseEvent(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                                new LocalizableMessage("openv4j.interrupted"));
                    }
                }
                returnToNormal(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis());
                returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
            }
            finally {
                closePort();
            }
        }

    }

    private boolean openSerialPort() {
        try {
            LOG.warn("OpenV4J Try open serial port");
            sPort = ProtocolHandler.openPort(vo.getCommPortId());
            protocolHandler.setStreams(sPort.getInputStream(), sPort.getOutputStream());
            return true;
        }
        catch (Exception ex) {
            LOG.fatal("OpenV4J Open serial port exception", ex);
            // Raise an event.
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    getSerialExceptionMessage(ex, vo.getCommPortId()));
            return false;
        }
    }

    private void closePort() {
        try {
            protocolHandler.close();
        }
        catch (InterruptedException ex) {
            LOG.fatal("Close port", ex);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                    "event.exception2", vo.getName(), ex.getMessage(), "HALLO3"));
        }
        if (sPort != null) {
            sPort.close();
            sPort = null;
        }
    }
}
