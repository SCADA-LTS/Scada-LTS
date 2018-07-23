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

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * The AnalogHighLimitDetector is used to detect occurrences of point values exceeding the given high limit for a given
 * duration. For example, a user may need to have an event raised when a temperature exceeds some value for 10 minutes
 * or more.
 * 
 * The configuration fields provided are static for the lifetime of this detector. The state fields vary based on the
 * changing conditions in the system. In particular, the highLimitActive field describes whether the point's value is
 * currently above the high limit or not. The eventActive field describes whether the point's value has been above the
 * high limit for longer than the tolerance duration.
 * 
 * @author Matthew Lohbihler
 */
public class AnalogHighLimitDetectorRT extends TimeDelayedEventDetectorRT {
    /**
     * State field. Whether the high limit is currently active or not. This field is used to prevent multiple events
     * being raised during the duration of a single high limit exceed.
     */
    private boolean highLimitActive;

    private long highLimitActiveTime;
    private long highLimitInactiveTime;

    /**
     * State field. Whether the event is currently active or not. This field is used to prevent multiple events being
     * raised during the duration of a single high limit exceed.
     */
    private boolean eventActive;

    public AnalogHighLimitDetectorRT(PointEventDetectorVO vo) {
        this.vo = vo;
    }

    @Override
    public LocalizableMessage getMessage() {
        String name = vo.njbGetDataPoint().getName();
        String prettyLimit = vo.njbGetDataPoint().getTextRenderer().getText(vo.getLimit(), TextRenderer.HINT_SPECIFIC);
        LocalizableMessage durationDescription = getDurationDescription();
        if (durationDescription == null)
            return new LocalizableMessage("event.detector.highLimit", name, prettyLimit);
        return new LocalizableMessage("event.detector.highLimitPeriod", name, prettyLimit, durationDescription);
    }

    public boolean isEventActive() {
        return eventActive;
    }

    /**
     * This method is only called when the high limit changes between being active or not, i.e. if the point's value is
     * currently above the high limit, then it should never be called with a value of true.
     * 
     * @param b
     */
    private void changeHighLimitActive() {
        highLimitActive = !highLimitActive;

        if (highLimitActive)
            // Schedule a job that will call the event active if it runs.
            scheduleJob();
        else
            unscheduleJob(highLimitInactiveTime);
    }

    @Override
    synchronized public void pointChanged(PointValueTime oldValue, PointValueTime newValue) {
        double newDouble = newValue.getDoubleValue();
        if (newDouble > vo.getLimit()) {
            if (!highLimitActive) {
                highLimitActiveTime = newValue.getTime();
                changeHighLimitActive();
            }
        }
        else {
            if (highLimitActive) {
                highLimitInactiveTime = newValue.getTime();
                changeHighLimitActive();
            }
        }
    }

    @Override
    protected long getConditionActiveTime() {
        return highLimitActiveTime;
    }

    /**
     * This method is only called when the event changes between being active or not, i.e. if the event currently is
     * active, then it should never be called with a value of true. That said, provision is made to ensure that the high
     * limit is active before allowing the event to go active.
     * 
     * @param b
     */
    @Override
    synchronized public void setEventActive(boolean b) {
        eventActive = b;
        if (eventActive) {
            // Just for the fun of it, make sure that the high limit is active.
            if (highLimitActive)
                // Ok, things are good. Carry on...
                // Raise the event.
                raiseEvent(highLimitActiveTime + getDurationMS(), createEventContext());
            else
                eventActive = false;
        }
        else
            // Deactive the event.
            returnToNormal(highLimitInactiveTime);
    }
}
