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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.dataSource.meta.MetaDataSourceVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Matthew Lohbihler
 */
public class MetaDataSourceRT extends DataSourceRT {
    public static final int EVENT_TYPE_CONTEXT_POINT_DISABLED = 1;
    public static final int EVENT_TYPE_SCRIPT_ERROR = 2;
    public static final int EVENT_TYPE_RESULT_TYPE_ERROR = 3;
    public static final int EVENT_TYPE_RECURSIVE_ERROR = 4;
    public static final int EVENT_TYPE_CONTEXT_POINT_UNAVAILABLE = 5;
    public static final int EVENT_TYPE_CONTEXT_POINT_MISSING = 6;

    private static final Log LOG = LogFactory.getLog(MetaDataSourceRT.class);

    private final List<DataPointRT> points = new CopyOnWriteArrayList<DataPointRT>();
    @Deprecated(since = "2.8.0")
    private boolean contextPointDisabledEventActive;

    public MetaDataSourceRT(MetaDataSourceVO vo) {
        super(vo);
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        dataPoint.setPointValue(valueTime, source);
    }

    @Override
    public void addDataPoint(DataPointRT dataPoint) {

        terminate(dataPoint);

        addPoint(dataPoint);
        MetaPointLocatorRT locator = dataPoint.getPointLocator();
        locator.initialize(Common.timer, this, dataPoint);
    }



    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        terminate(dataPoint);
    }

    private void terminate(DataPointRT dataPoint) {
        MetaPointLocatorRT locator = dataPoint.getPointLocator();
        locator.terminate();
        removePoint(dataPoint);
    }

    @Deprecated(since = "2.8.0")
    synchronized void checkForDisabledPoints() {
        DataPointRT problemPoint = null;

        for (DataPointRT dp : points) {
            MetaPointLocatorRT locator = dp.getPointLocator();
            if (!locator.isContextCreated()) {
                problemPoint = dp;
                break;
            }
        }

        if (contextPointDisabledEventActive != (problemPoint != null)) {
            contextPointDisabledEventActive = problemPoint != null;
            if (contextPointDisabledEventActive)
                // A context point has been terminated, was never enabled, or not longer exists.
                raiseEvent(EVENT_TYPE_CONTEXT_POINT_DISABLED, System.currentTimeMillis(), true, new LocalizableMessage(
                        "event.meta.pointUnavailable", problemPoint.getVO().getName()), problemPoint);
            else
                // Everything is good
                returnToNormal(EVENT_TYPE_CONTEXT_POINT_DISABLED, System.currentTimeMillis());
        }
    }

    public void raiseScriptError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        raiseEvent(EVENT_TYPE_SCRIPT_ERROR, runtime, true, new LocalizableMessage("event.meta.scriptError", dataPoint
                .getVO().getName(), message), dataPoint);
    }

    public void raiseRecursiveError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        raiseEvent(EVENT_TYPE_RECURSIVE_ERROR, runtime, true, new LocalizableMessage("event.meta.recursiveError", dataPoint
                .getVO().getName(), message), dataPoint);
    }

    @Deprecated(since = "2.8.1")
    public void raiseContextError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        if(isNone(EVENT_TYPE_CONTEXT_POINT_DISABLED)) {
            return;
        }
        raiseEvent(EVENT_TYPE_CONTEXT_POINT_DISABLED, runtime, true, message, dataPoint);
    }

    public void returnToNormalScript(long runtime, DataPointRT dataPoint) {
        returnToNormal(EVENT_TYPE_SCRIPT_ERROR, runtime, dataPoint);
    }

    public void returnToNormalRecursive(long runtime, DataPointRT dataPoint) {
        returnToNormal(EVENT_TYPE_RECURSIVE_ERROR, runtime, dataPoint);
    }

    @Deprecated(since = "2.8.1")
    public void returnToNormalContext(long runtime, DataPointRT dataPoint) {
        if(isNone(EVENT_TYPE_CONTEXT_POINT_DISABLED)) {
            return;
        }
        returnToNormal(EVENT_TYPE_CONTEXT_POINT_DISABLED, runtime, dataPoint);
    }

    public void raiseResultTypeError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        raiseEvent(EVENT_TYPE_RESULT_TYPE_ERROR, runtime, true, new LocalizableMessage("event.meta.typeError",
                dataPoint.getVO().getName(), message), dataPoint);
    }

    public void returnToNormalType(long runtime, DataPointRT dataPoint) {
        returnToNormal(EVENT_TYPE_RESULT_TYPE_ERROR, runtime, dataPoint);
    }

    private boolean isNone(int type) {
        DataSourceEventType object = getEventType(type);
        if(object == null)
            return true;
        return object.getAlarmLevel() == AlarmLevels.NONE;
    }

    @Override
    public List<DataPointRT> getDataPoints() {
        getDataPointsLock().readLock().lock();
        try {
            return new ArrayList<>(points);
        } finally {
            getDataPointsLock().readLock().unlock();
        }
    }

    public void returnToNormalContextPointDisabled(long runtime, DataPointRT dataPoint, LocalizableMessage onlyWithThisMessage) {
        returnToNormal(EVENT_TYPE_CONTEXT_POINT_DISABLED, runtime, dataPoint, onlyWithThisMessage);
    }

    public void returnToNormalContextPointUnavailable(long runtime, DataPointRT dataPoint, LocalizableMessage onlyWithThisMessage) {
        returnToNormal(EVENT_TYPE_CONTEXT_POINT_UNAVAILABLE, runtime, dataPoint, onlyWithThisMessage);
    }

    public void raiseContextErrorPointDisabled(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        raiseEvent(EVENT_TYPE_CONTEXT_POINT_DISABLED, runtime, true, message, dataPoint);
    }


    public void raiseContextErrorPointUnavailable(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        raiseEvent(EVENT_TYPE_CONTEXT_POINT_UNAVAILABLE, runtime, true, message, dataPoint);
    }

    public void returnToNormalContextPointMissing(long runtime, DataPointRT dataPoint, LocalizableMessage onlyWithThisMessage) {
        returnToNormal(EVENT_TYPE_CONTEXT_POINT_MISSING, runtime, dataPoint, onlyWithThisMessage);
    }

    public void raiseContextErrorPointMissing(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        raiseEvent(EVENT_TYPE_CONTEXT_POINT_MISSING, runtime, true, message, dataPoint);
    }

    @Override
    public void forcePointRead(DataPointRT dataPoint) {
        if(dataPoint.isInitialized()) {
            MetaPointLocatorRT metaPointLocatorRT = dataPoint.getPointLocator();
            if(metaPointLocatorRT != null)
                metaPointLocatorRT.execute(System.currentTimeMillis(), new ArrayList<>());
            else
                LOG.warn("Failed forcePointRead for: " + LoggingUtils.dataPointInfo(dataPoint));
        }
    }

    private void addPoint(DataPointRT dataPoint) {
        getDataPointsLock().writeLock().lock();
        try {
            points.add(dataPoint);
        } finally {
            getDataPointsLock().writeLock().unlock();
        }
    }

    private void removePoint(DataPointRT dataPoint) {
        getDataPointsLock().writeLock().lock();
        try {
            points.remove(dataPoint);
        } finally {
            getDataPointsLock().writeLock().unlock();
        }
    }


}
