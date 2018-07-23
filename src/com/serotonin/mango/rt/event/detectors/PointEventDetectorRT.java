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
package com.serotonin.mango.rt.event.detectors;

import java.util.HashMap;
import java.util.Map;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointListener;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.event.SimpleEventDetector;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;

abstract public class PointEventDetectorRT extends SimpleEventDetector implements DataPointListener {
    protected PointEventDetectorVO vo;

    protected EventType getEventType() {
        DataPointEventType et = new DataPointEventType(vo.njbGetDataPoint().getId(), vo.getId());
        if (!vo.isRtnApplicable())
            et.setDuplicateHandling(EventType.DuplicateHandling.ALLOW);
        return et;
    }

    protected void raiseEvent(long time, Map<String, Object> context) {
        LocalizableMessage msg;
        if (!StringUtils.isEmpty(vo.getAlias()))
            msg = new LocalizableMessage("common.default", vo.getAlias());
        else
            msg = getMessage();

        Common.ctx.getEventManager().raiseEvent(getEventType(), time, vo.isRtnApplicable(), vo.getAlarmLevel(), msg,
                context);
        fireEventDetectorStateChanged(time);
    }

    protected void returnToNormal(long time) {
        Common.ctx.getEventManager().returnToNormal(getEventType(), time);
        fireEventDetectorStateChanged(time);
    }

    protected Map<String, Object> createEventContext() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("pointEventDetector", vo);
        context.put("point", vo.njbGetDataPoint());
        return context;
    }

    abstract protected LocalizableMessage getMessage();

    public String getEventDetectorKey() {
        return vo.getEventDetectorKey();
    }

    //
    //
    // Lifecycle interface
    //
    public void initialize() {
        // no op
    }

    @Override
    public void terminate() {
        fireEventDetectorTerminated();
    }

    public void joinTermination() {
        // no op
    }

    //
    //
    // Point listener interface
    //
    public void pointChanged(PointValueTime oldValue, PointValueTime newValue) {
        // no op
    }

    public void pointSet(PointValueTime oldValue, PointValueTime newValue) {
        // no op
    }

    public void pointUpdated(PointValueTime newValue) {
        // no op
    }

    public void pointBackdated(PointValueTime value) {
        // no op
    }

    public void pointInitialized() {
        // no op
    }

    public void pointTerminated() {
        // no op
    }
}
