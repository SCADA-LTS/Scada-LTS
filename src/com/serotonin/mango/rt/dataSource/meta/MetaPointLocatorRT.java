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
package com.serotonin.mango.rt.dataSource.meta;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.script.ScriptException;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointListener;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.IDataPoint;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.util.DateUtils;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.timer.AbstractTimer;
import com.serotonin.timer.CronExpression;
import com.serotonin.timer.OneTimeTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.util.ObjectUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.serotonin.mango.util.LoggingScriptUtils.generateContext;
import static com.serotonin.mango.util.LoggingScriptUtils.infoErrorExecutionScript;
import static com.serotonin.mango.util.LoggingScriptUtils.infoErrorInitializationScript;

/**
 * @author Matthew Lohbihler
 */
public class MetaPointLocatorRT extends PointLocatorRT implements DataPointListener {
    private static final ThreadLocal<List<Integer>> threadLocal = new ThreadLocal<List<Integer>>();
    private static final int MAX_RECURSION = 10;

    final Object LOCK = new Object();

    final MetaPointLocatorVO vo;
    AbstractTimer timer;
    private MetaDataSourceRT dataSource;
    protected DataPointRT dataPoint;
    protected volatile Map<String, IDataPoint> context;
    boolean initialized;
    TimerTask timerTask;

    private final AtomicInteger pointInitializedSafe = new AtomicInteger(MAX_RECURSION);
    private final AtomicInteger pointTerminatedSafe = new AtomicInteger(MAX_RECURSION);

    private static final Log LOG = LogFactory.getLog(MetaPointLocatorRT.class);

    public MetaPointLocatorRT(MetaPointLocatorVO vo) {
        this.vo = vo;
    }

    @Override
    public boolean isSettable() {
        return vo.isSettable();
    }

    public MetaPointLocatorVO getPointLocatorVO() {
        return vo;
    }

    @Deprecated(since = "2.8.0")
    boolean isContextCreated() {
        return context != null;
    }

    //
    // Lifecycle stuff. Note, this class does not implement the lifecycle interface because it is not controlled by
    // the RuntimeManager. The owning data source calls these methods then the point is added and removed from the
    // data source.
    //
    public void initialize(AbstractTimer timer, MetaDataSourceRT dataSource, DataPointRT dataPoint) {
        this.timer = timer;
        this.dataSource = dataSource;
        this.dataPoint = dataPoint;

        this.context = createContext(dataPoint);

        // Add listener registrations
        RuntimeManager rm = Common.ctx.getRuntimeManager();
        for (IntValuePair contextKey : vo.getContext()) {
            // Points shouldn't listen for their own updates.
            if (dataPoint.getId() != contextKey.getKey())
                rm.addDataPointListener(contextKey.getKey(), this);
        }

        initializeTimerTask();

        initialized = true;

        if(dataPoint.isInitialized() && (vo.getUpdateEvent() == MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE
                || vo.getUpdateEvent() == MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE) && !vo.getContext().isEmpty()) {
            execute(System.currentTimeMillis(), new ArrayList<>(), true, dataPoint);
        }
    }

    protected void initializeTimerTask() {
        int updateEventId = vo.getUpdateEvent();
        if (updateEventId != MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE
                && updateEventId != MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE)
            // Scheduled update. Create the timeout that will update this point.
            timerTask = new ScheduledUpdateTimeout(calculateTimeout(timer.currentTimeMillis()));
    }

    public void terminate() {
        synchronized (LOCK) {
            // Remove listener registrations
            RuntimeManager rm = Common.ctx.getRuntimeManager();
            for (IntValuePair contextKey : vo.getContext())
                rm.removeDataPointListener(contextKey.getKey(), this);

            // Cancel scheduled job
            if (timerTask != null)
                timerTask.cancel();

            initialized = false;
        }
    }

    //
    // / DataPointListener
    //
    public void pointChanged(PointValueTime oldValue, PointValueTime newValue) {
        // No op. Events are covered in pointUpdated.
        if (vo.getUpdateEvent() == MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE) {
            execute(newValue);
        }
    }

    public void pointSet(PointValueTime oldValue, PointValueTime newValue) {
        // No op. Events are covered in pointUpdated.
    }

    public void pointUpdated(PointValueTime newValue) {
        // Ignore if this is not a context update.
        if (vo.getUpdateEvent() == MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE) {
            execute(newValue);
        }
    }

    public void pointBackdated(PointValueTime value) {
        // No op.
    }

    public void pointInitialized() {

        context = createContext(dataPoint);

        if(context == null) {
            return;
        }

        if(pointInitializedSafe.getAndDecrement() < 0) {
            LOG.error("Exceeded recursive level: " + LoggingUtils.dataPointInfo(dataPoint));
            pointInitializedSafe.set(MAX_RECURSION);
            return;
        }

        if(dataPoint.getPointLocator() instanceof MetaPointLocatorRT) {
            DataPointListener dataPointListener = Common.ctx.getRuntimeManager().getDataPointListeners(dataPoint.getId());
            if(dataPointListener != null) {
                dataPointListener.pointInitialized();
            }
        }
    }

    public void pointTerminated() {

        context = createContext(dataPoint);

        if(pointTerminatedSafe.getAndDecrement() < 0) {
            LOG.error("Exceeded recursive level: " + LoggingUtils.dataPointInfo(dataPoint));
            pointTerminatedSafe.set(MAX_RECURSION);
            return;
        }

        if(dataPoint.getPointLocator() instanceof MetaPointLocatorRT) {
            DataPointListener dataPointListener = Common.ctx.getRuntimeManager().getDataPointListeners(dataPoint.getId());
            if(dataPointListener != null) {
                dataPointListener.pointTerminated();
            }
        }
    }

    //
    //
    // TimeoutClient
    //
    class ScheduledUpdateTimeout extends TimerTask {
        ScheduledUpdateTimeout(long fireTime) {
            super(new OneTimeTrigger(new Date(fireTime)));
            timer.schedule(this);
        }

        @Override
        public void run(long fireTime) {
            // Execute the update
            execute(fireTime - vo.executionDelayMs(), new ArrayList<Integer>());

            // Schedule the next timeout.
            synchronized (LOCK) {
                if (initialized)
                    timerTask = new ScheduledUpdateTimeout(calculateTimeout(fireTime));
            }
        }
    }

    long calculateTimeout(long time) {
        int updateEventId = vo.getUpdateEvent();
        long timeout;
        if (updateEventId == MetaPointLocatorVO.UPDATE_EVENT_CRON) {
            try {
                CronExpression ce = new CronExpression(vo.getUpdateCronPattern());
                timeout = ce.getNextValidTimeAfter(new Date(time)).getTime();
            }
            catch (ParseException e) {
                throw new ShouldNeverHappenException(e);
            }
        }
        else
            timeout = DateUtils.next(time, updateEventId);
        return timeout + vo.executionDelayMs();
    }

    class ExecutionDelayTimeout extends TimerTask {
        private final long updateTime;
        private final List<Integer> sourceIds;

        public ExecutionDelayTimeout(long updateTime, List<Integer> sourceIds) {
            super(new OneTimeTrigger(new Date(updateTime + vo.executionDelayMs())));
            this.updateTime = updateTime;
            this.sourceIds = sourceIds;
            timer.schedule(this);
        }

        @Override
        public void run(long fireTime) {
            // When this runs it is because there were no context updates within the delay period. Still, we want the
            // reading to be published with the time of the context update, not the timeout fire time.
            execute(updateTime, sourceIds);
        }
    }
    private void execute(long runtime, List<Integer> sourceIds) {
        execute(runtime, sourceIds, false, dataPoint);
    }

    private void execute(long runtime, List<Integer> sourceIds, boolean initializeMode, DataPointRT dataPoint) {
        this.context = createContext(dataPoint);
        if(context == null) {
            LOG.warn("MetaPointLocatorRT.context is null, Context: " + generateContext(dataPoint, dataSource));
            return;
        }

        // Check if we've reached the maximum number of recursions for this point
        int count = 0;
        for (Integer id : sourceIds) {
            if (id.intValue() == dataPoint.getId())
                count++;
        }

        if (count > MAX_RECURSION) {
            handleScriptError(runtime, dataPoint, new LocalizableMessage("event.meta.recursionFailure"));
            String msg = MessageFormat.format("Recursion failure: exceeded MAX_RECURSION: expected <= {0} but was {1}, Context: {2}",
                    String.valueOf(MAX_RECURSION), count, generateContext(dataPoint, dataSource));
            LOG.warn(msg);
            return;
        } else {
            returnToNormal(runtime, dataPoint);
        }

        sourceIds.add(dataPoint.getId());
        threadLocal.set(sourceIds);
        try {
            ScriptExecutor executor = new ScriptExecutor();
            try {
                PointValueTime valueTime = executor.execute(vo.getScript(), context, timer.currentTimeMillis(),
                        vo.getDataTypeId(), runtime);
                returnToNormalType(System.currentTimeMillis(), dataPoint);
                PointValueTime previousValueTime = dataPoint.getPointValue();
                if (valueTime.getValue() == null)
                    handleScriptError(runtime, dataPoint, new LocalizableMessage("event.meta.nullResult"));
                else if(isUpdatePoint(initializeMode, valueTime, previousValueTime, vo))
                    doUpdate(valueTime, dataPoint);
            } catch (ScriptException e) {
                handleScriptError(runtime, dataPoint, new LocalizableMessage("common.default", e.getLocalizedMessage()));
                LOG.warn(infoErrorExecutionScript(e, dataPoint, dataSource));
            } catch (ResultTypeException e) {
                handleTypeError(runtime, dataPoint, e.getLocalizableMessage());
                LOG.warn(infoErrorExecutionScript(e, dataPoint, dataSource));
            } catch (Exception e) {
                handleScriptError(runtime, dataPoint, new LocalizableMessage("common.default", e.getMessage()));
                LOG.warn(infoErrorExecutionScript(e, dataPoint, dataSource));
            }
        }
        finally {
            threadLocal.remove();
        }
    }

    protected Map<String, IDataPoint> createContext(DataPointRT dataPoint) {
        Map<String, IDataPoint> context;
        try {
            ScriptExecutor scriptExecutor = new ScriptExecutor();
            context = scriptExecutor.convertContext(vo.getContext(), dataPoint.getId());
            returnToNormalContext(System.currentTimeMillis(), dataPoint);
            return context;
        } catch (DataPointStateException e) {
            handleContextError(System.currentTimeMillis(), dataPoint, e.getLocalizableMessage());
            LOG.warn(infoErrorInitializationScript(e, dataPoint, dataSource));
            return null;
        } catch (Exception e) {
            handleContextError(System.currentTimeMillis(), dataPoint, new LocalizableMessage("event.meta.pointUnavailable", dataPoint.getVO().getName()));
            LOG.warn(infoErrorInitializationScript(e, dataPoint, dataSource));
            return null;
        }
    }

    private void execute(PointValueTime value) {
        execute(value, false);
    }

    private void execute(PointValueTime newValue, boolean initializeMode) {
        // Check for infinite loops
        List<Integer> sourceIds;
        if (threadLocal.get() == null)
            sourceIds = new ArrayList<Integer>();
        else
            sourceIds = threadLocal.get();

        long time = newValue.getTime();
        if (vo.getExecutionDelaySeconds() == 0)
            execute(time, sourceIds, initializeMode, dataPoint);
        else {
            synchronized (LOCK) {
                if (initialized) {
                    if (timerTask != null)
                        timerTask.cancel();
                    timerTask = new ExecutionDelayTimeout(time, sourceIds);
                }
            }
        }
    }

    protected void doUpdate(PointValueTime pvt, DataPointRT dataPoint) {
        dataPoint.updatePointValue(pvt);
        returnToNormal(System.currentTimeMillis(), dataPoint);
    }

    protected void handleScriptError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        dataSource.raiseScriptError(runtime, dataPoint, message);
    }

    protected void handleContextError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        dataSource.raiseContextError(runtime, dataPoint, message);
    }

    protected void returnToNormal(long runtime, DataPointRT dataPoint) {
        dataSource.returnToNormalScript(runtime, dataPoint);
    }

    protected void returnToNormalContext(long runtime, DataPointRT dataPoint) {
        dataSource.returnToNormalContext(runtime, dataPoint);
    }

    protected void handleTypeError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        dataSource.raiseResultTypeError(runtime, dataPoint, message);
    }

    protected void returnToNormalType(long runtime, DataPointRT dataPoint) {
        dataSource.returnToNormalType(runtime, dataPoint);
    }

    private static boolean isUpdatePoint(boolean initializeMode, PointValueTime valueTime, PointValueTime previousValueTime, MetaPointLocatorVO metaPointLocator) {
        return !initializeMode || (metaPointLocator.getUpdateEvent() != MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE
                && metaPointLocator.getUpdateEvent() != MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE)
                || (previousValueTime == null || !ObjectUtils.isEqual(valueTime.getValue(), previousValueTime.getValue()));
    }
}
