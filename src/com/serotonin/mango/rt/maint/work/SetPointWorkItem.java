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
package com.serotonin.mango.rt.maint.work;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class SetPointWorkItem extends AbstractBeforeAfterWorkItem {
    private static final ThreadLocal<List<String>> threadLocal = new ThreadLocal<List<String>>();
    private static final int MAX_RECURSION = 10;

    private final int targetPointId;
    private final PointValueTime pvt;
    private final SetPointSource source;
    private final List<String> sourceIds;

    private final EventType eventType;

    public SetPointWorkItem(int targetPointId, PointValueTime pvt, SetPointSource source, EventType eventType) {
        this.targetPointId = targetPointId;
        this.pvt = pvt;
        this.source = source;

        if (threadLocal.get() == null)
            sourceIds = new ArrayList<String>();
        else
            sourceIds = threadLocal.get();

        this.eventType = eventType;
    }

    @Override
    public void work() {
        String sourceId = Integer.toString(source.getSetPointSourceType()) + "-"
                + Integer.toString(source.getSetPointSourceId());

        // Check if we've reached the maximum number of hits for this point
        int count = 0;
        for (String id : sourceIds) {
            if (id.equals(sourceId))
                count++;
        }

        if (count > MAX_RECURSION) {
            source.raiseRecursionFailureEvent();
            return;
        }

        sourceIds.add(sourceId);
        threadLocal.set(sourceIds);
        try {
            Common.ctx.getRuntimeManager().setDataPointValue(targetPointId, pvt, source);
        }
        finally {
            threadLocal.remove();
        }
    }

    @Override
    public void workSuccess() {
        SystemEventType.returnToNormal(eventType, System.currentTimeMillis());
    }

    @Override
    public void workFail(Throwable exception) {
        SystemEventType.raiseEvent(eventType, System.currentTimeMillis(), true, new LocalizableMessage("common.default", exception.getMessage()));
    }

    @Override
    public WorkItemPriority getPriorityType() {
        return WorkItemPriority.HIGH;
    }

    @Override
    public String toString() {
        return "SetPointWorkItem{" +
                "targetPointId=" + targetPointId +
                ", pvt=" + pvt +
                ", source=" + source +
                ", sourceIds=" + sourceIds +
                '}';
    }

    @Override
    public String getDetails() {
        return this.toString();
    }
}
