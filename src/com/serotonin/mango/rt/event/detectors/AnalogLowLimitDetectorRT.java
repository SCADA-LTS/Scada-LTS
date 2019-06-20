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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * The AnalogLowLimitDetector is used to detect occurances of point values below the given low limit for a given
 * duration. For example, a user may need to have an event raised when a temperature sinks below some value for 10
 * minutes or more.
 * 
 * The configuration fields provided are static for the lifetime of this detector. The state fields vary based on the
 * changing conditions in the system. In particular, the lowLimitActive field describes whether the point's value is
 * currently below the low limit or not. The eventActive field describes whether the point's value has been below the
 * low limit for longer than the tolerance duration.
 * 
 * @author Matthew Lohbihler
 */
public class AnalogLowLimitDetectorRT extends TimeDelayedEventDetectorRT {
    private final Log log = LogFactory.getLog(AnalogLowLimitDetectorRT.class);

    /**
     * State field. Whether the low limit is currently active or not. This field is used to prevent multiple events
     * being raised during the duration of a single low limit event.
     */
    private boolean lowLimitActive;

    private long lowLimitActiveTime;
    private long lowLimitInactiveTime;

    /**
     * State field. Whether the event is currently active or not. This field is used to prevent multiple events being
     * raised during the duration of a single low limit event.
     */
    private boolean eventActive;

    public AnalogLowLimitDetectorRT(PointEventDetectorVO vo) {
        this.vo = vo;
    }

    @Override
    public LocalizableMessage getMessage() {
        LocalizableMessage durationDescription = getDurationDescription();
        String name = vo.njbGetDataPoint().getName();
        String prettyLimit = vo.njbGetDataPoint().getTextRenderer().getText(vo.getLimit(), TextRenderer.HINT_SPECIFIC);

        if (durationDescription == null)
            return new LocalizableMessage("event.detector.lowLimit", name, prettyLimit);
        return new LocalizableMessage("event.detector.lowLimitPeriod", name, prettyLimit, durationDescription);
    }

    public boolean isEventActive() {
        return eventActive;
    }

    /**
     * This method is only called when the low limit changes between being active or not, i.e. if the point's value is
     * currently below the low limit, then it should never be called with a value of true.
     * 
     * @param b
     */
    private void changeLowLimitActive() {
        lowLimitActive = !lowLimitActive;

        if (lowLimitActive)
            // Schedule a job that will call the event active if it runs.
            scheduleJob();
        else
            unscheduleJob(lowLimitInactiveTime);
    }

    @Override
    synchronized public void pointChanged(PointValueTime oldValue, PointValueTime newValue) {
        double newDouble = newValue.getDoubleValue();
        if (newDouble < vo.getLimit()) {
            if (!lowLimitActive) {
                lowLimitActiveTime = newValue.getTime();
                changeLowLimitActive();
            }
        }
        else {
            if (lowLimitActive) {
                lowLimitInactiveTime = newValue.getTime();
                changeLowLimitActive();
            }
        }
    }

    @Override
    protected long getConditionActiveTime() {
        return lowLimitActiveTime;
    }

    /**
     * This method is only called when the event changes between being active or not, i.e. if the event currently is
     * active, then it should never be called with a value of true. That said, provision is made to ensure that the low
     * limit is active before allowing the event to go active.
     * 
     * @param b
     */
    @Override
    synchronized public void setEventActive(boolean b) {
        eventActive = b;
        if (eventActive) {
            // Just for the fun of it, make sure that the low limit is active.
            if (lowLimitActive)
                // Ok, things are good. Carry on...
                // Raise the event.
                raiseEvent(lowLimitActiveTime + getDurationMS(), createEventContext());
            else {
                // Perhaps the job wasn't successfully unscheduled. Write a log entry and ignore.
                log.warn("Call to set event active when low limit is not active. Ignoring.");
                eventActive = false;
            }
        }
        else
            // Deactivate the event.
            returnToNormal(lowLimitInactiveTime);
    }
}
