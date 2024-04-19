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
package com.serotonin.mango.rt.dataImage;


import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.utils.PointValueStateUtils;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataPointSynchronizedRT extends DataPointRT implements IDataPointRT {

    private static final Log LOG = LogFactory.getLog(DataPointSynchronizedRT.class);
    private static final String POINT_VALUE_INTERVAL_IS_NOT_INITIALIZED = "PointValueIntervalLogging is not initialized!";

    // Runtime data.
    private PointValueState pointValueState;
    private PointValueIntervalLogging pointValueIntervalLogging;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public DataPointSynchronizedRT(DataPointVO vo, PointLocatorRT pointLocator) {
        super(vo, pointLocator);
        this.pointValueState = PointValueState.empty();
    }

    @Override
    protected void savePointValue(PointValueTime newValue, SetPointSource source,
                                  boolean async) {
        // Null values are not very nice, and since they don't have a specific
        // meaning they are hereby ignored.
        if (newValue == null)
            return;

        // Check the data type of the value against that of the locator, just
        // for fun.
        int valueDataType = DataTypes.getDataType(newValue.getValue());
        if (valueDataType != DataTypes.UNKNOWN
                && valueDataType != getVO().getPointLocator().getDataTypeId())
            // This should never happen, but if it does it can have serious
            // downstream consequences. Also, we need
            // to know how it happened, and the stack trace here provides the
            // best information.
            throw new ShouldNeverHappenException(
                    "Data type mismatch between new value and point locator: newValue="
                            + DataTypes.getDataType(newValue.getValue())
                            + ", locator="
                            + getVO().getPointLocator().getDataTypeId());

        // Check if this value qualifies for discardation.
        if (getVO().isDiscardExtremeValues()
                && DataTypes.getDataType(newValue.getValue()) == DataTypes.NUMERIC) {
            double newd = newValue.getDoubleValue();
            if (newd < getVO().getDiscardLowLimit()
                    || newd > getVO().getDiscardHighLimit())
                // Discard the value
                return;
        }

        if (newValue.getTime() > System.currentTimeMillis()
                + SystemSettingsDAO.getFutureDateLimit()) {
            // Too far future dated. Toss it. But log a message first.
            LOG.warn(
                    "Future dated value detected: pointId=" + getVO().getId()
                            + ", value=" + newValue.getStringValue()
                            + ", type=" + getVO().getPointLocator().getDataTypeId()
                            + ", ts=" + newValue.getTime(), new Exception());
            return;
        }

        createAndUpdateState(newValue, getVO(), source).ifPresent(state -> {
            try {
                savePointValue(source, async, state);
            } catch (Exception ex) {
                LOG.warn(ex.getMessage(), ex);
            }
        });
    }

    @Override
    public PointValueTime getPointValue() {
        lock.readLock().lock();
        try {
            return pointValueState.getNewValue();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void initialize() {
        // Get the latest value for the point from the database.
        PointValueTime lastValue = getPointValueCache().getLatestPointValue();
        createAndUpdateState(lastValue, getVO(), null);
        super.initialize();
    }

    @Override
    public void resetValues() {
        getPointValueCache().reset();
        if (getVO().getLoggingType() != DataPointVO.LoggingTypes.NONE) {
            PointValueTime lastValue = getPointValueCache().getLatestPointValue();
            createAndUpdateState(lastValue, getVO(), null);
        }
    }

    @Override
    protected void initializeIntervalLogging() {
        if(pointValueIntervalLogging != null) {
            terminateIntervalLogging();
        }
        pointValueIntervalLogging = new PointValueIntervalLogging(getVO(), getPointValueCache());
        pointValueIntervalLogging.initializeIntervalLogging(getPointValue(), this);
    }

    @Override
    protected void terminateIntervalLogging() {
        if(pointValueIntervalLogging != null)
            pointValueIntervalLogging.terminateIntervalLogging();
        else
            LOG.error(POINT_VALUE_INTERVAL_IS_NOT_INITIALIZED);
    }

    @Override
    protected void intervalSave(PointValueTime pvt) {
        if(pointValueIntervalLogging != null)
            pointValueIntervalLogging.intervalSave(pvt);
        else
            LOG.error(POINT_VALUE_INTERVAL_IS_NOT_INITIALIZED);
    }

    @Override
    public void scheduleTimeout(long fireTime) {
        if(pointValueIntervalLogging != null)
            pointValueIntervalLogging.scheduleTimeout(fireTime, getPointValue());
        else
            LOG.error(POINT_VALUE_INTERVAL_IS_NOT_INITIALIZED);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + getId();
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
        final DataPointSynchronizedRT other = (DataPointSynchronizedRT) obj;
        return getId() == other.getId();
    }

    @Override
    public String toString() {
        return "DataPointSynchronizedRT(id=" + getId() + ", name=" + getVO().getName() + ")";
    }

    private Optional<PointValueState> createAndUpdateState(PointValueTime newValue, DataPointVO vo, SetPointSource source) {
        lock.writeLock().lock();
        try {
            PointValueState state = PointValueState.newState(newValue, pointValueState, vo, source);
            if(!state.isBackdated())
                pointValueState = state;
            return Optional.of(state);
        } catch(Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void savePointValue(SetPointSource source, boolean async, PointValueState state) {
        PointValueTime oldValue = state.getOldValue();
        PointValueTime newValue = state.getNewValue();

        boolean logValue = state.isLogValue();
        boolean saveValue = state.isSaveValue();

        if (PointValueStateUtils.isLoggingTypeIn(getVO(), DataPointVO.LoggingTypes.INTERVAL) && !state.isBackdated()) {
            intervalSave(newValue);
        }

        if (saveValue) {
            notifyWebSocketSubscribers(newValue.getValue());
            getPointValueCache().savePointValueIntoDaoAndCacheUpdate(newValue, source, logValue, async);
        }

        if (!state.isBackdated()) {
            fireEvents(oldValue, newValue, source != null, false);
        } else
            fireEvents(null, newValue, false, true);
    }
}
