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
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.detectors.PointEventDetectorRT;
import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.util.timeout.TimeoutTask;
import com.serotonin.mango.view.stats.AnalogStatistics;
import com.serotonin.mango.view.stats.IValueTime;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.timer.FixedRateTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.util.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.web.ws.ScadaWebSocketListener;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataPointNonSyncRT extends DataPointRT implements IDataPointRT {
    private static final Log LOG = LogFactory.getLog(DataPointNonSyncRT.class);

    private static final PvtTimeComparator pvtTimeComparator = new PvtTimeComparator();

    // Configuration data.
    private final DataPointVO vo;
    private final PointLocatorRT pointLocator;

    // Runtime data.
    private volatile PointValueTime pointValue;
    private final PointValueCache valueCache;
    private RuntimeManager rm;
    private List<PointEventDetectorRT> detectors;
    private final Map<String, Object> attributes = new HashMap<String, Object>();

    // Interval logging data.
    private PointValueTime intervalValue;
    private long intervalStartTime = -1;
    private List<IValueTime> averagingValues;
    private final Object intervalLoggingLock = new Object();
    private TimerTask intervalLoggingTask;

    // WebSocket notification
    private final List<ScadaWebSocketListener<String, Integer>> scadaWebSocketListeners = new CopyOnWriteArrayList<>();

    /**
     * This is the value around which tolerance decisions will be made when
     * determining whether to log numeric values.
     */
    private volatile double toleranceOrigin;

    public DataPointNonSyncRT(DataPointVO vo, PointLocatorRT pointLocator) {
        super(vo, pointLocator);
        this.vo = vo;
        this.pointLocator = pointLocator;
        valueCache = new PointValueCache(vo.getId(), vo.getDefaultCacheSize());
    }
    public DataPointNonSyncRT(DataPointVO vo, PointLocatorRT pointLocator, int cacheSize, int maxSize) {
        super(vo, pointLocator, cacheSize, maxSize);
        this.vo = vo;
        this.pointLocator = pointLocator;
        valueCache = new PointValueCache(cacheSize);
        valueCache.setMaxSize(maxSize);
    }

    public DataPointNonSyncRT(DataPointVO vo) {
        super(vo);
        this.vo = vo;
        this.pointLocator = null;
        valueCache = new PointValueCache();
    }

    @Override
    public PointValueCache getPointValueCache(){
        return this.valueCache;
    }

    @Override
    public List<PointValueTime> getLatestPointValues(int limit) {
        return valueCache.getLatestPointValues(limit);
    }

    @Override
    public List<PointValueTime> getLatestPointValuesUsedForJunitTest(int limit) {
        return valueCache.getLatestPointValuesUsedForTest(limit);
    }

    @Override
    public void addCollectionIntoCache(PointValueTime pvt){
        valueCache.addPointValueTimeIntoCacheForTest(pvt);
    }

    @Override
    public PointValueTime getPointValueBefore(long time) {
        for (PointValueTime pvt : valueCache.getCacheContents()) {
            if (pvt.getTime() < time)
                return pvt;
        }

        return new PointValueDao().getPointValueBefore(vo.getId(), time);
    }

    @Override
    public PointValueTime getPointValueAt(long time) {
        for (PointValueTime pvt : valueCache.getCacheContents()) {
            if (pvt.getTime() == time)
                return pvt;
        }

        return new PointValueDao().getPointValueAt(vo.getId(), time);
    }

    @Override
    public List<PointValueTime> getPointValues(long since) {
        List<PointValueTime> result = new PointValueDao().getPointValues(
                vo.getId(), since);

        for (PointValueTime pvt : valueCache.getCacheContents()) {
            if (pvt.getTime() >= since) {
                int index = Collections.binarySearch(result, pvt,
                        pvtTimeComparator);
                if (index < 0)
                    result.add(-index - 1, pvt);
            }
        }

        return result;
    }

    @Override
    public List<PointValueTime> getPointValuesBetween(long from, long to) {
        List<PointValueTime> result = new PointValueDao()
                .getPointValuesBetween(vo.getId(), from, to);

        for (PointValueTime pvt : valueCache.getCacheContents()) {
            if (pvt.getTime() >= from && pvt.getTime() < to) {
                int index = Collections.binarySearch(result, pvt,
                        pvtTimeComparator);
                if (index < 0)
                    result.add(-index - 1, pvt);
            }
        }

        return result;
    }

    /**
     * This method should only be called by the data source. Other types of
     * point setting should include a set point source object so that the
     * annotation can be logged.
     *
     * @param newValue
     */

    @Override
    public void updatePointValue(PointValueTime newValue) {
        savePointValue(newValue, null, true);
    }

    @Override
    public void updatePointValue(PointValueTime newValue, boolean async) {
        savePointValue(newValue, null, async);
    }

    /**
     * Use this method to update a data point for reasons other than just data
     * source update.
     *
     * @param newValue
     *            the value to set
     * @param source
     *            the source of the set. This can be a user object if the point
     *            was set from the UI, or could be a program run by schedule or
     *            on event.
     */
    @Override
    public void setPointValue(PointValueTime newValue, SetPointSource source) {
        if (source == null)
            savePointValue(newValue, source, true);
        else
            savePointValue(newValue, source, false);
    }

    private void savePointValue(PointValueTime newValue, SetPointSource source,
                                boolean async) {
        // Null values are not very nice, and since they don't have a specific
        // meaning they are hereby ignored.
        if (newValue == null)
            return;

        // Check the data type of the value against that of the locator, just
        // for fun.
        int valueDataType = DataTypes.getDataType(newValue.getValue());
        if (valueDataType != DataTypes.UNKNOWN
                && valueDataType != vo.getPointLocator().getDataTypeId())
            // This should never happen, but if it does it can have serious
            // downstream consequences. Also, we need
            // to know how it happened, and the stack trace here provides the
            // best information.
            throw new ShouldNeverHappenException(
                    "Data type mismatch between new value and point locator: newValue="
                            + DataTypes.getDataType(newValue.getValue())
                            + ", locator="
                            + vo.getPointLocator().getDataTypeId());

        // Check if this value qualifies for discardation.
        if (vo.isDiscardExtremeValues()
                && DataTypes.getDataType(newValue.getValue()) == DataTypes.NUMERIC) {
            double newd = newValue.getDoubleValue();
            if (newd < vo.getDiscardLowLimit()
                    || newd > vo.getDiscardHighLimit())
                // Discard the value
                return;
        }

        if (newValue.getTime() > System.currentTimeMillis()
                + SystemSettingsDAO.getFutureDateLimit()) {
            // Too far future dated. Toss it. But log a message first.
            LOG.warn(
                    "Future dated value detected: pointId=" + vo.getId()
                            + ", value=" + newValue.getStringValue()
                            + ", type=" + vo.getPointLocator().getDataTypeId()
                            + ", ts=" + newValue.getTime(), new Exception());
            return;
        }

        PointValueTime oldValue = getOldAndSetNew(newValue);

        boolean backdated = oldValue != null
                && newValue.getTime() < oldValue.getTime();

        // Determine whether the new value qualifies for logging.
        boolean logValue;
        // ... or even saving in the cache.
        boolean saveValue = true;
        switch (vo.getLoggingType()) {
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
            default:
                logValue = false;
        }

        if (saveValue){
            this.notifyWebSocketListeners(newValue.getValue().toString());
            valueCache.savePointValueIntoDaoAndCacheUpdate(newValue, source, logValue, async);
        }


        // Ignore historical values.
        if (oldValue == null || newValue.getTime() >= oldValue.getTime()) {
            fireEvents(oldValue, newValue, source != null, false);
        } else
            fireEvents(null, newValue, false, true);
    }

    //
    // / Interval logging
    //
    private void initializeIntervalLogging() {
        PointValueTime currentValue = getPointValue();
        synchronized (intervalLoggingLock) {
            if (vo.getLoggingType() != DataPointVO.LoggingTypes.INTERVAL)
                return;

            intervalLoggingTask = new TimeoutTask(new FixedRateTrigger(0,
                    Common.getMillis(vo.getIntervalLoggingPeriodType(),
                            vo.getIntervalLoggingPeriod())), this);

            intervalValue = currentValue;
            if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.AVERAGE) {
                intervalStartTime = System.currentTimeMillis();
                averagingValues = new ArrayList<IValueTime>();
            }
        }
    }

    private void terminateIntervalLogging() {
        synchronized (intervalLoggingLock) {
            if (vo.getLoggingType() != DataPointVO.LoggingTypes.INTERVAL)
                return;

            intervalLoggingTask.cancel();
        }
    }

    private void intervalSave(PointValueTime pvt) {
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

    @Override
    public void scheduleTimeout(long fireTime) {
        PointValueTime currentValue = getPointValue();
        synchronized (intervalLoggingLock) {
            MangoValue value;
            if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.INSTANT)
                value = PointValueTime.getValue(currentValue);
            else if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.MAXIMUM
                    || vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.MINIMUM) {
                value = PointValueTime.getValue(intervalValue);
                intervalValue = currentValue;
            } else if (vo.getIntervalLoggingType() == DataPointVO.IntervalLoggingTypes.AVERAGE) {
                AnalogStatistics stats = new AnalogStatistics(intervalValue,
                        averagingValues, intervalStartTime, fireTime);
                value = new NumericValue(stats.getAverage());

                intervalValue = currentValue;
                averagingValues.clear();
                intervalStartTime = fireTime;
            } else
                throw new ShouldNeverHappenException(
                        "Unknown interval logging type: "
                                + vo.getIntervalLoggingType());

            if (value != null)
                valueCache.logPointValueAsync(new PointValueTime(value,
                        fireTime), null);
        }
    }

    //
    // / Purging
    //
    @Override
    public void resetValues() {
        valueCache.reset();
        if (vo.getLoggingType() != DataPointVO.LoggingTypes.NONE)
            getOldAndSetNew(valueCache.getLatestPointValue());
    }

    //
    // /
    // / Properties
    // /
    //
    @Override
    public int getId() {
        return vo.getId();
    }

    @Override
    public PointValueTime getPointValue() {
        return getOldAndSetNew(null);
    }

    public PointValueTime getOldAndSetNew(PointValueTime newValue) {
        if(newValue == null) {
            return pointValue;
        }
        if(pointValue == null || newValue.getTime() >= pointValue.getTime()) {
            PointValueTime oldValue = pointValue;
            pointValue = newValue;
            return oldValue;
        }
        return pointValue;
    }

    public boolean updateToleranceOrigin(PointValueTime newValue, boolean setForce) {
        if(setForce) {
            this.toleranceOrigin = newValue.getDoubleValue();
            return true;
        }
        boolean logValue;
        double newd = newValue.getDoubleValue();
        // See if the new value is outside of the tolerance.
        double diff = this.toleranceOrigin - newd;
        if (diff < 0)
            diff = -diff;

        if (diff > vo.getTolerance()) {
            this.toleranceOrigin = newd;
            logValue = true;
        } else
            logValue = false;
        return logValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends PointLocatorRT> T getPointLocator() {
        return (T) pointLocator;
    }

    @Override
    public int getDataSourceId() {
        return vo.getDataSourceId();
    }

    @Override
    public DataPointVO getVO() {
        return vo;
    }

    @Override
    public int getDataTypeId() {
        return vo.getPointLocator().getDataTypeId();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
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
        if (getId() != other.getId())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DataPointNonSyncRT(id=" + getId() + ", name=" + vo.getName() + ")";
    }

    //
    // /
    // / Listeners
    // /
    //
    private void fireEvents(PointValueTime oldValue, PointValueTime newValue,
                            boolean set, boolean backdate) {
        DataPointListener l = rm.getDataPointListeners(vo.getId());
        if (l != null)
            Common.ctx.getBackgroundProcessing().addWorkItem(
                    new EventNotifyWorkItem(l, oldValue, newValue, set,
                            backdate));
    }

    @Override
    public void addWebSocketListener(ScadaWebSocketListener listener) {
        scadaWebSocketListeners.add(listener);
    }

    @Override
    public void removeWebSocketListener(ScadaWebSocketListener listener) {
        scadaWebSocketListeners.remove(listener);
    }

    @Override
    public void notifyWebSocketListeners(String message) {
        if(!scadaWebSocketListeners.isEmpty()) {
            scadaWebSocketListeners.forEach(observer -> {
                observer.sendWebSocketMessage(message, this.vo.getId());
            });
        }
    }

    static class EventNotifyWorkItem implements WorkItem {
        private final DataPointListener listener;
        private final PointValueTime oldValue;
        private final PointValueTime newValue;
        private final boolean set;
        private final boolean backdate;

        EventNotifyWorkItem(DataPointListener listener,
                            PointValueTime oldValue, PointValueTime newValue, boolean set,
                            boolean backdate) {
            this.listener = listener;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.set = set;
            this.backdate = backdate;
        }

        @Override
        public void execute() {
            if (backdate)
                listener.pointBackdated(newValue);
            else {
                // Always fire this.
                listener.pointUpdated(newValue);

                // Fire if the point has changed.
                if (!PointValueTime.equalValues(oldValue, newValue))
                    listener.pointChanged(oldValue, newValue);

                // Fire if the point was set.
                if (set)
                    listener.pointSet(oldValue, newValue);
            }
        }

        @Override
        public int getPriority() {
            return WorkItem.PRIORITY_MEDIUM;
        }
    }

    //
    //
    // Lifecycle
    //
    @Override
    public void initialize() {
        rm = Common.ctx.getRuntimeManager();

        // Get the latest value for the point from the database.
        PointValueTime lastValue = valueCache.getLatestPointValue();
        getOldAndSetNew(lastValue);

        // Set the tolerance origin if this is a numeric
        if (lastValue != null && lastValue.getValue() instanceof NumericValue)
            updateToleranceOrigin(lastValue, true);

        // Add point event listeners
        for (PointEventDetectorVO ped : vo.getEventDetectors()) {
            if (detectors == null)
                detectors = new ArrayList<PointEventDetectorRT>();

            PointEventDetectorRT pedRT = ped.createRuntime();
            detectors.add(pedRT);
            rm.addPointEventDetector(pedRT);
            rm.addDataPointListener(vo.getId(), pedRT);
        }

        initializeIntervalLogging();
    }
    @Override
    public void terminate() {
        terminateIntervalLogging();

        if (detectors != null) {
            for (PointEventDetectorRT pedRT : detectors) {
                rm.removeDataPointListener(vo.getId(), pedRT);
                rm.removePointEventDetector(pedRT.getEventDetectorKey());
            }
        }
        Common.ctx.getEventManager().cancelEventsForDataPoint(vo.getId());
    }

    @Override
    public void joinTermination() {
        // no op
    }

    @Override
    public void initializeHistorical() {
        rm = Common.ctx.getRuntimeManager();
        initializeIntervalLogging();
    }

    @Override
    public void terminateHistorical() {
        terminateIntervalLogging();
    }
}
