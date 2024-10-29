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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.vo.dataSource.meta.MetaDataSourceVO;
import com.serotonin.web.i18n.LocalizableMessage;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.*;

/**
 * @author Matthew Lohbihler
 */
public class MetaDataSourceRT extends DataSourceRT {
    public static final int EVENT_TYPE_CONTEXT_POINT_DISABLED = 1;
    public static final int EVENT_TYPE_SCRIPT_ERROR = 2;
    public static final int EVENT_TYPE_RESULT_TYPE_ERROR = 3;

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
        synchronized (pointListChangeLock) {
            remove(dataPoint);

            MetaPointLocatorRT locator = dataPoint.getPointLocator();
            points.add(dataPoint);
            locator.initialize(Common.timer, this, dataPoint);
        }
    }

    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        synchronized (pointListChangeLock) {
            remove(dataPoint);
        }
    }

    private void remove(DataPointRT dataPoint) {
        MetaPointLocatorRT locator = dataPoint.getPointLocator();
        locator.terminate();
        points.remove(dataPoint);
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
        if(isNone(EVENT_TYPE_SCRIPT_ERROR)) {
            setUnreliableDataPoint(dataPoint);
            return;
        }
        raiseEvent(EVENT_TYPE_SCRIPT_ERROR, runtime, true, new LocalizableMessage("event.meta.scriptError", dataPoint
                .getVO().getName(), message), dataPoint);
    }

    public void raiseContextError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        if(isNone(EVENT_TYPE_CONTEXT_POINT_DISABLED)) {
            setUnreliableDataPoint(dataPoint);
            return;
        }
        raiseEvent(EVENT_TYPE_CONTEXT_POINT_DISABLED, runtime, true, message, dataPoint);
    }

    public void returnToNormalScript(long runtime, DataPointRT dataPoint) {
        if(isNone(EVENT_TYPE_SCRIPT_ERROR)) {
            resetUnreliableDataPoint(dataPoint);
            return;
        }
        returnToNormal(EVENT_TYPE_SCRIPT_ERROR, runtime, dataPoint);
    }

    public void returnToNormalContext(long runtime, DataPointRT dataPoint) {
        if(isNone(EVENT_TYPE_CONTEXT_POINT_DISABLED)) {
            resetUnreliableDataPoint(dataPoint);
            return;
        }
        returnToNormal(EVENT_TYPE_CONTEXT_POINT_DISABLED, runtime, dataPoint);
    }

    public void raiseResultTypeError(long runtime, DataPointRT dataPoint, LocalizableMessage message) {
        if(isNone(EVENT_TYPE_RESULT_TYPE_ERROR)) {
            setUnreliableDataPoint(dataPoint);
            return;
        }
        raiseEvent(EVENT_TYPE_RESULT_TYPE_ERROR, runtime, true, new LocalizableMessage("event.meta.typeError",
                dataPoint.getVO().getName(), message), dataPoint);
    }

    public void returnToNormalType(long runtime, DataPointRT dataPoint) {
        if(isNone(EVENT_TYPE_RESULT_TYPE_ERROR)) {
            resetUnreliableDataPoint(dataPoint);
            return;
        }
        returnToNormal(EVENT_TYPE_RESULT_TYPE_ERROR, runtime, dataPoint);
    }

    private boolean isNone(int type) {
        DataSourceEventType object = getEventType(type);
        if(object == null)
            return true;
        return object.getAlarmLevel() == AlarmLevels.NONE;
    }

    @Override
    protected List<DataPointRT> getDataPoints() {
        return points;
    }
}
