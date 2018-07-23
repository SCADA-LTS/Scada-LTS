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
package com.serotonin.mango.rt.dataSource.mbus;

import java.io.IOException;

import net.sf.mbus4j.dataframes.datablocks.BigDecimalDataBlock;
import net.sf.mbus4j.dataframes.datablocks.IntegerDataBlock;
import net.sf.mbus4j.dataframes.datablocks.LongDataBlock;
import net.sf.mbus4j.dataframes.datablocks.RealDataBlock;
import net.sf.mbus4j.dataframes.datablocks.ShortDataBlock;
import net.sf.mbus4j.dataframes.datablocks.StringDataBlock;
import com.serotonin.mango.vo.dataSource.mbus.MBusDataSourceVO;
import net.sf.mbus4j.master.MBusMaster;
import net.sf.mbus4j.master.ValueRequest;
import net.sf.mbus4j.master.ValueRequestPointLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.web.i18n.LocalizableMessage;
import net.sf.mbus4j.dataframes.datablocks.BcdValue;

/**
 * TODO datatype NUMERIC_INT is missing TODO Starttime for timpepoints ???
 */
public class MBusDataSourceRT extends PollingDataSource {

    private final static Log LOG = LogFactory.getLog(MBusDataSourceRT.class);
    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int POINT_READ_EXCEPTION_EVENT = 2;
    public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
    private final MBusDataSourceVO vo;
    private final MBusMaster master = new MBusMaster();

    public MBusDataSourceRT(MBusDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    public void initialize() {
        LOG.fatal("INITIALIZE MBusaDataSourceRT" + Thread.getAllStackTraces().get(Thread.currentThread()));
        super.initialize();
    }

    @Override
    public void terminate() {
        LOG.fatal("TERMINATE MBusaDataSourceRT" + Thread.getAllStackTraces().get(Thread.currentThread()));
        super.terminate();
    }

    public double calcCorretedValue(DataPointRT point, double value) {
        MBusPointLocatorRT mPoint = (MBusPointLocatorRT) point.getPointLocator();
        return mPoint.getVo().getCorrectionFactor() * value;
    }

    @Override
    protected synchronized void doPoll(long time) {
        boolean pointError = false;
        boolean dsError = false;

        ValueRequest<DataPointRT> request = new ValueRequest<DataPointRT>();
        for (DataPointRT point : dataPoints) {
            final MBusPointLocatorRT locator = point.getPointLocator();
            request.add(locator.createValueRequestPointLocator(point));
        }

        if (openConnection()) {
            try {
                master.readValues(request);
                for (ValueRequestPointLocator<DataPointRT> vr : request) {
                    MBusPointLocatorRT locator = vr.getReference().getPointLocator();
                    try {
                        if (vr.getDb() == null) {

                            pointError = true;

                            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
                                    new LocalizableMessage("event.exception2", vo.getName(),
                                    "No datablock found for: ", vr.getReference().getVO().getName()));

                        } else if ((vr.getDb() instanceof BcdValue) && ((BcdValue) vr.getDb()).isBcdError()) {
                            pointError = true;
                            LOG.fatal("BCD Error : " + ((BcdValue) vr.getDb()).getBcdError());
                            raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true,
                                    new LocalizableMessage("event.exception2", vo.getName(),
                                    String.format("BCD error %s value: ", vr.getReference().getVO().getName()), ((BcdValue) vr.getDb()).getBcdError()));
                        } else if (vr.getDb() instanceof ShortDataBlock) {
                            vr.getReference().updatePointValue(
                                    new PointValueTime(calcCorretedValue(vr.getReference(), ((ShortDataBlock) vr.getDb()).getValue()), time));
                        } else if (vr.getDb() instanceof IntegerDataBlock) {
                            vr.getReference().updatePointValue(
                                    new PointValueTime(calcCorretedValue(vr.getReference(), ((IntegerDataBlock) vr.getDb()).getValue()), time));
                        } else if (vr.getDb() instanceof LongDataBlock) {
                            vr.getReference().updatePointValue(
                                    new PointValueTime(calcCorretedValue(vr.getReference(), ((LongDataBlock) vr.getDb()).getValue()), time));
                        } else if (vr.getDb() instanceof RealDataBlock) {
                            vr.getReference().updatePointValue(
                                    new PointValueTime(calcCorretedValue(vr.getReference(), ((RealDataBlock) vr.getDb()).getValue()), time));
                        } else if (vr.getDb() instanceof BigDecimalDataBlock) {
                            vr.getReference().updatePointValue(
                                    new PointValueTime(calcCorretedValue(vr.getReference(), ((BigDecimalDataBlock) vr.getDb()).getValue().doubleValue()), time));
                        } else if (vr.getDb() instanceof StringDataBlock) {
                            vr.getReference().updatePointValue(
                                    new PointValueTime(((StringDataBlock) vr.getDb()).getValue(), time));
                        } else {
                            LOG.fatal("Dont know how to save: " + vr.getReference().getVO().getName());
                            raiseEvent(POINT_READ_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                                    new LocalizableMessage("event.exception2", vo.getName(),
                                    "Dont know how to save: ", vr.getReference().getVO().getName()));

                        }
                    } catch (Exception ex) {
                        LOG.fatal("Error during saving: " + vo.getName(), ex);
                        raiseEvent(POINT_READ_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                                new LocalizableMessage("event.exception2", vo.getName(),
                                "Dont know how to save : ", ex));
                    }

                }

                if (!pointError) {
                    returnToNormal(POINT_READ_EXCEPTION_EVENT, time);
                }


                returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);
            } catch (InterruptedException ex) {
                LOG.error("doPoll() interrupted", ex);
                raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                        "event.exception2", vo.getName(), ex.getMessage(), "doPoll() Interrupted"));
            } catch (IOException ex) {
                LOG.error("doPoll() IO Ex", ex);
                raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                        "event.exception2", vo.getName(), ex.getMessage(), "doPoll() IO Ex"));
            } finally {
                closeConnection();
            }
        }
    }

    @Override
    public synchronized void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        // no op
    }

    private boolean openConnection() {
        try {
            LOG.warn("MBus Try open serial port");
            master.setConnection(vo.getConnection());
            master.open();
            return true;
        } catch (Exception ex) {
            LOG.fatal("MBus Open serial port exception", ex);
            master.setConnection(null);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                    "event.exception2", vo.getName(), ex.getMessage(), "openConnection() Failed"));
            return false;
        }
    }

    private void closeConnection() {
        try {
            master.close();
        } catch (IOException ex) {
            LOG.fatal("Close port", ex);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                    "event.exception2", vo.getName(), ex.getMessage(), "closeConnection() Failed"));
        } catch (InterruptedException ex) {
            LOG.fatal("Close port", ex);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                    "event.exception2", vo.getName(), ex.getMessage(), "closeConnection() Failed"));
        } finally {
            master.setConnection(null);
        }
    }
}
