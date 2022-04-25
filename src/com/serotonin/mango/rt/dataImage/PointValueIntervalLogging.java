package com.serotonin.mango.rt.dataImage;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.util.timeout.TimeoutTask;
import com.serotonin.mango.view.stats.AnalogStatistics;
import com.serotonin.mango.view.stats.IValueTime;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.timer.FixedRateTrigger;
import com.serotonin.timer.TimerTask;

import java.util.ArrayList;
import java.util.List;

public class PointValueIntervalLogging {

    // Interval logging data.
    private PointValueTime intervalValue;
    private long intervalStartTime = -1;
    private List<IValueTime> averagingValues;
    private TimerTask intervalLoggingTask;

    private final Object intervalLoggingLock = new Object();

    private final DataPointVO vo;
    private final PointValueCache pointValueCache;

    public PointValueIntervalLogging(DataPointVO vo, PointValueCache pointValueCache) {
        this.vo = vo;
        this.pointValueCache = pointValueCache;
    }

    //
    // / Interval logging
    //
    protected void initializeIntervalLogging(PointValueTime pointValue, IDataPointRT dataPointRT) {
        synchronized (intervalLoggingLock) {
            if (vo.getLoggingType() != DataPointVO.LoggingTypes.INTERVAL)
                return;

            intervalLoggingTask = new TimeoutTask(new FixedRateTrigger(0,
                    Common.getMillis(vo.getIntervalLoggingPeriodType(),
                            vo.getIntervalLoggingPeriod())), dataPointRT);

            intervalValue = pointValue;
            if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.AVERAGE) {
                intervalStartTime = System.currentTimeMillis();
                averagingValues = new ArrayList<IValueTime>();
            }
        }
    }

    protected void terminateIntervalLogging() {
        synchronized (intervalLoggingLock) {
            if (vo.getLoggingType() != DataPointVO.LoggingTypes.INTERVAL)
                return;

            intervalLoggingTask.cancel();
        }
    }

    protected void intervalSave(PointValueTime pvt) {
        synchronized (intervalLoggingLock) {
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

    public void scheduleTimeout(long fireTime, PointValueTime pointValue) {
        synchronized (intervalLoggingLock) {
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
                pointValueCache.logPointValueAsync(new PointValueTime(value,
                        fireTime), null);
        }
    }
}
