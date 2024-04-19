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
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.util.timeout.TimeoutTask;
import com.serotonin.mango.view.stats.AnalogStatistics;
import com.serotonin.mango.view.stats.IValueTime;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.timer.FixedRateTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.util.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;

import java.util.ArrayList;
import java.util.List;

import static com.serotonin.mango.rt.dataImage.PointValueState.newState;
import static org.scada_lts.utils.PointValueStateUtils.isBackdated;

public class DataPointNonSyncRT extends DataPointRT implements IDataPointRT {
    private static final Log LOG = LogFactory.getLog(DataPointNonSyncRT.class);

    // Runtime data.
    private volatile PointValueTime pointValue;

    // Interval logging data.
    private PointValueTime intervalValue;
    private long intervalStartTime = -1;
    private List<IValueTime> averagingValues;
    private final Object intervalLoggingLock = new Object();
    private TimerTask intervalLoggingTask;

    /**
     * This is the value around which tolerance decisions will be made when
     * determining whether to log numeric values.
     */
    private volatile double toleranceOrigin;

    public DataPointNonSyncRT(DataPointVO vo, PointLocatorRT pointLocator) {
        super(vo, pointLocator);
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

        PointValueTime oldValue = getOldAndSetNew(newValue, source);

        boolean backdated = isBackdated(newValue, newState(oldValue, PointValueState.empty(), getVO()), source);

        // Determine whether the new value qualifies for logging.
        boolean logValue;
        // ... or even saving in the cache.
        boolean saveValue = true;
        switch (getVO().getLoggingType()) {
            case DataPointVO.LoggingTypes.ON_CHANGE:
                if (oldValue == null) {
                    logValue = true;
                    if(newValue.getValue() instanceof NumericValue) {
                        updateToleranceOrigin(newValue, true);
                    }
                } else if (backdated)
                    // Backdated. Ignore it
                    logValue = false;
                else {
                    if (newValue.getValue() instanceof NumericValue) {
                        // Get the new double
                        logValue = updateToleranceOrigin(newValue, false);
                    } else
                        logValue = !ObjectUtils.isEqual(newValue.getValue(), oldValue.getValue());
                }

                saveValue = logValue;
                break;
            case DataPointVO.LoggingTypes.ALL:
                logValue = true;
                break;
            case DataPointVO.LoggingTypes.ON_TS_CHANGE:
                if (oldValue == null)
                    logValue = true;
                else
                    logValue = newValue.getTime() != oldValue.getTime();

                saveValue = logValue;
                break;
            case DataPointVO.LoggingTypes.INTERVAL:
                if (!backdated)
                    intervalSave(newValue);
                //Always is 'logValue = false' because in INTERVAL Logging Mode individual values are not saved before aggregation
                logValue = false;
                break;
            default:
                logValue = false;
        }

        if (saveValue){
            notifyWebSocketSubscribers(newValue.getValue());
            getPointValueCache().savePointValueIntoDaoAndCacheUpdate(newValue, source, logValue, async);
        }


        // Ignore historical values.
        if (!backdated) {
            fireEvents(oldValue, newValue, source != null, false);
        } else
            fireEvents(null, newValue, false, true);
    }

    @Override
    public PointValueTime getPointValue() {
        return getOldAndSetNew(null, null);
    }

    private PointValueTime getOldAndSetNew(PointValueTime newValue, SetPointSource source) {
        if(newValue == null) {
            return pointValue;
        }
        if(!isBackdated(newValue, newState(pointValue, PointValueState.empty(), getVO()), source)) {
            PointValueTime oldValue = pointValue;
            pointValue = newValue;
            return oldValue;
        }
        return pointValue;
    }

    private boolean updateToleranceOrigin(PointValueTime newValue, boolean updateForce) {
        if(updateForce) {
            this.toleranceOrigin = newValue.getDoubleValue();
            return true;
        }
        boolean logValue;
        double newd = newValue.getDoubleValue();
        // See if the new value is outside of the tolerance.
        double diff = this.toleranceOrigin - newd;
        if (diff < 0)
            diff = -diff;

        if (diff > getVO().getTolerance()) {
            this.toleranceOrigin = newd;
            logValue = true;
        } else
            logValue = false;
        return logValue;
    }

    @Override
    public void initialize() {
        // Get the latest value for the point from the database.
        pointValue = getPointValueCache().getLatestPointValue();

        // Set the tolerance origin if this is a numeric
        if (pointValue != null && pointValue.getValue() instanceof NumericValue)
            toleranceOrigin = pointValue.getDoubleValue();

        super.initialize();
    }

    @Override
    public void resetValues() {
        getPointValueCache().reset();
        if (getVO().getLoggingType() != DataPointVO.LoggingTypes.NONE)
            pointValue = getPointValueCache().getLatestPointValue();
    }

    //
    // / Interval logging
    //
    @Override
    protected void initializeIntervalLogging() {
        synchronized (intervalLoggingLock) {
            DataPointVO vo = getVO();
            if (vo.getLoggingType() != DataPointVO.LoggingTypes.INTERVAL)
                return;

            intervalLoggingTask = new TimeoutTask(new FixedRateTrigger(0,
                    Common.getMillis(vo.getIntervalLoggingPeriodType(),
                            vo.getIntervalLoggingPeriod())), this);

            intervalValue = pointValue;
            if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.AVERAGE) {
                intervalStartTime = System.currentTimeMillis();
                averagingValues = new ArrayList<IValueTime>();
            }
        }
    }

    @Override
    protected void terminateIntervalLogging() {
        synchronized (intervalLoggingLock) {
            DataPointVO vo = getVO();
            if (vo.getLoggingType() != DataPointVO.LoggingTypes.INTERVAL)
                return;

            intervalLoggingTask.cancel();
        }
    }

    @Override
    protected void intervalSave(PointValueTime pvt) {
        synchronized (intervalLoggingLock) {
            DataPointVO vo = getVO();
            if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.MAXIMUM) {
                if (intervalValue == null)
                    intervalValue = pvt;
                else if (pvt != null) {
                    if (intervalValue.getDoubleValue() < pvt.getDoubleValue())
                        intervalValue = pvt;
                }
            } else if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.MINIMUM) {
                if (intervalValue == null)
                    intervalValue = pvt;
                else if (pvt != null) {
                    if (intervalValue.getDoubleValue() > pvt.getDoubleValue())
                        intervalValue = pvt;
                }
            } else if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.AVERAGE)
                averagingValues.add(pvt);
        }
    }

    @Override
    public void scheduleTimeout(long fireTime) {
        synchronized (intervalLoggingLock) {
            DataPointVO vo = getVO();
            MangoValue value;
            if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.INSTANT)
                value = PointValueTime.getValue(pointValue);
            else if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.MAXIMUM
                    || vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.MINIMUM) {
                value = PointValueTime.getValue(intervalValue);
                intervalValue = pointValue;
            } else if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.AVERAGE) {
                AnalogStatistics stats = new AnalogStatistics(intervalValue,
                        averagingValues, intervalStartTime, fireTime);
                value = new NumericValue(stats.getAverage());

                intervalValue = pointValue;
                averagingValues.clear();
                intervalStartTime = fireTime;
            } else
                throw new ShouldNeverHappenException(
                        "Unknown interval logging type: "
                                + vo.getIntervalLoggingType());

            if (value != null)
                getPointValueCache().logPointValueAsync(new PointValueTime(value,
                        fireTime), null);
        }
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
        final DataPointNonSyncRT other = (DataPointNonSyncRT) obj;
        return getId() == other.getId();
    }

    @Override
    public String toString() {
        return "DataPointNonSyncRT(id=" + getId() + ", name=" + getVO().getName() + ")";
    }
}
