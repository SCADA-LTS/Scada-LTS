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
package com.serotonin.mango.rt.dataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.web.i18n.LocalizableMessage;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.timeout.TimeoutClient;
import com.serotonin.mango.util.timeout.TimeoutTask;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.timer.FixedRateTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.web.taglib.DateFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

abstract public class PollingDataSource extends DataSourceRT implements TimeoutClient {
    private final Logger LOG = LogManager.getLogger(PollingDataSource.class);

    private final DataSourceVO<?> vo;
    private final List<DataPointRT> dataPoints = new CopyOnWriteArrayList<>();
    protected boolean pointListChanged = false;
    private long pollingPeriodMillis = 300000; // Default to 5 minutes just to have something here
    private boolean quantize;
    private TimerTask timerTask;
    private volatile Thread jobThread;
    private long jobThreadStartTime;
    private static volatile boolean markAsTerminating = false;
    private static final ThreadLocal<Long> DO_RAISE_EVENT = ThreadLocal.withInitial(() -> 0L);

    private final AtomicInteger lock = new AtomicInteger(0);

    public PollingDataSource(DataSourceVO<?> vo) {
        super(vo);
        this.vo = vo;
    }

    public int getPointReadExceptionEvent(){
        return 1;
    }

    public void setPollingPeriod(int periodType, int periods, boolean quantize) {
        pollingPeriodMillis = Common.getMillis(periodType, periods);
        this.quantize = quantize;
    }

    @Deprecated(since = "2.8.0")
    public void scheduleTimeout2(long fireTime) {
        if(isMarkAsTerminating()) {
            return;
        }
        if (jobThread != null) {
            // There is another poll still running, so abort this one.
            LOG.warn(vo.getName() + ": poll at " + DateFunctions.getFullSecondTime(fireTime)
                    + " aborted because a previous poll started at "
                    + DateFunctions.getFullSecondTime(jobThreadStartTime) + " is still running");
            return;
        }

        try {
            jobThread = Thread.currentThread();
            jobThreadStartTime = fireTime;

            // Check if there were changes to the data points list.
            synchronized (pointListChangeLock) {
                updateChangedPoints();
                doPoll(fireTime);
            }
        }
        finally {
            jobThread = null;
        }
    }

    public void scheduleTimeout(long fireTime) {
        if(isMarkAsTerminating()) {
            return;
        }

        if(lock.getAndIncrement() == 0) {
            long startTime = System.currentTimeMillis();
            try {
                jobThreadStartTime = fireTime;
                updateChangedPoints();
                doPoll(fireTime);
            } finally {
                DO_RAISE_EVENT.set(System.currentTimeMillis() - startTime);
                lock.getAndSet(0);
            }
        } else {
            LOG.warn(LoggingUtils.dataSourceInfo(vo) + ": poll at " + DateFunctions.getFullSecondTime(fireTime)
                    + " aborted because a previous poll started at "
                    + DateFunctions.getFullSecondTime(jobThreadStartTime) + " is still running");
            return;
        }


        long executedMillis = DO_RAISE_EVENT.get();
        doRaiseEvent(fireTime, executedMillis);
    }

    abstract protected void doPoll(long time);

    protected List<DataPointRT> updateChangedPoints() {
        getDataPointsLock().writeLock().lock();
        try {
            if (addedChangedPoints.size() > 0) {
                // Remove any existing instances of the points.
                dataPoints.removeAll(addedChangedPoints);
                dataPoints.addAll(addedChangedPoints);
                addedChangedPoints.clear();
                pointListChanged = true;
            }
            if (removedPoints.size() > 0) {
                dataPoints.removeAll(removedPoints);
                removedPoints.clear();
                pointListChanged = true;
            }
            return new ArrayList<>(dataPoints);
        } finally {
            getDataPointsLock().writeLock().unlock();
        }
    }

    //
    //
    // Data source interface
    //
    @Override
    public void beginPolling() {
        // Quantize the start.
        long delay = 0;
        if (quantize)
            delay = pollingPeriodMillis - (System.currentTimeMillis() % pollingPeriodMillis);
        timerTask = new TimeoutTask(new FixedRateTrigger(delay, pollingPeriodMillis), this);
        super.beginPolling();
    }

    @Override
    public void terminate() {
        if (timerTask != null)
            timerTask.cancel();
        super.terminate();
    }

    @Override
    public void joinTermination() {
        super.joinTermination();

        Thread localThread = jobThread;
        if (localThread != null) {
            try {
                localThread.join(30000); // 30 seconds
            }
            catch (InterruptedException e) { /* no op */
            }
            if (jobThread != null) {
                throw new ShouldNeverHappenException("Timeout waiting for data source to stop: id=" + getId()
                        + ", type=" + getClass() + ", stackTrace=" + Arrays.toString(localThread.getStackTrace()));
            }
        }
    }

    @Override
    public List<DataPointRT> getDataPoints() {
        getDataPointsLock().readLock().lock();
        try {
            return new ArrayList<>(dataPoints);
        } finally {
            getDataPointsLock().readLock().unlock();
        }
    }
  
    public boolean isMarkAsTerminating() {
        if(markAsTerminating) {
            LOG.info(LoggingUtils.dataSourceInfo(this) + " is terminating.");
            return true;
        } else {
            return false;
        }
    }

    public static void markAsTerminating() {
        markAsTerminating = true;
    }

    protected abstract int getUpdateTimeExceededUpdatePeriodEventId();

    private void doRaiseEvent(long fireTime, long executedMillis) {
        if(isInitialized() && !isMarkAsTerminating() && executedMillis > pollingPeriodMillis) {
            LocalizableMessage msg = new LocalizableMessage("event.ds.updateTimeExceededUpdatePeriodAttention",
                    executedMillis, pollingPeriodMillis, LoggingUtils.dataSourceInfo(vo));
            LOG.warn(msg.getLocalizedMessage(Common.getBundle()));
            raiseEvent(getUpdateTimeExceededUpdatePeriodEventId(), fireTime, true, msg);
        } else {
            returnToNormal(getUpdateTimeExceededUpdatePeriodEventId(), fireTime);
        }
    }

    @Override
    @Deprecated(since = "2.8.1")
    public boolean doSetUnreliableDataPoint(int eventId) {
        return eventId != getUpdateTimeExceededUpdatePeriodEventId();
    }
}
