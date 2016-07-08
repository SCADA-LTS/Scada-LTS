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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.timeout.TimeoutClient;
import com.serotonin.mango.util.timeout.TimeoutTask;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.timer.FixedRateTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.web.taglib.DateFunctions;

abstract public class PollingDataSource extends DataSourceRT implements TimeoutClient {
    private final Log LOG = LogFactory.getLog(PollingDataSource.class);

    private final DataSourceVO<?> vo;
    protected List<DataPointRT> dataPoints = new ArrayList<DataPointRT>();
    protected boolean pointListChanged = false;
    private long pollingPeriodMillis = 300000; // Default to 5 minutes just to have something here
    private boolean quantize;
    private TimerTask timerTask;
    private volatile Thread jobThread;
    private long jobThreadStartTime;

    public PollingDataSource(DataSourceVO<?> vo) {
        super(vo);
        this.vo = vo;
    }

    public void setPollingPeriod(int periodType, int periods, boolean quantize) {
        pollingPeriodMillis = Common.getMillis(periodType, periods);
        this.quantize = quantize;
    }

    public void scheduleTimeout(long fireTime) {
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

    abstract protected void doPoll(long time);

    protected void updateChangedPoints() {
        synchronized (pointListChangeLock) {
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
}
