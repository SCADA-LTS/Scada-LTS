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
 * The NegativeCusumDetectorRT is used to detect occurances of point values below the given CUSUM limit for a given
 * duration. For example, a user may need to have an event raised when a temperature CUSUM sinks below some value for 10
 * minutes or more.
 * 
 * @author Matthew Lohbihler
 */
public class NegativeCusumDetectorRT extends TimeDelayedEventDetectorRT {
    /**
     * State field. The current negative CUSUM for the point.
     */
    private double cusum;

    /**
     * State field. Whether the negative CUSUM is currently active or not. This field is used to prevent multiple events
     * being raised during the duration of a single negative CUSUM exceed.
     */
    private boolean negativeCusumActive;

    private long negativeCusumActiveTime;
    private long negativeCusumInactiveTime;

    /**
     * State field. Whether the event is currently active or not. This field is used to prevent multiple events being
     * raised during the duration of a single negative CUSUM exceed.
     */
    private boolean eventActive;

    public NegativeCusumDetectorRT(PointEventDetectorVO vo) {
        this.vo = vo;
    }

    @Override
    public LocalizableMessage getMessage() {
        String name = vo.njbGetDataPoint().getName();
        String prettyLimit = vo.njbGetDataPoint().getTextRenderer().getText(vo.getLimit(), TextRenderer.HINT_SPECIFIC);
        LocalizableMessage durationDescription = getDurationDescription();
        if (durationDescription == null)
            return new LocalizableMessage("event.detector.negCusum", name, prettyLimit);
        return new LocalizableMessage("event.detector.negCusumPeriod", name, prettyLimit, durationDescription);
    }

    public boolean isEventActive() {
        return eventActive;
    }

    /**
     * This method is only called when the negative CUSUM changes between being active or not, i.e. if the point's CUSUM
     * is currently above the limit, then it should never be called with a value of true.
     * 
     * @param b
     */
    private void changeNegativeCusumActive() {
        negativeCusumActive = !negativeCusumActive;

        if (negativeCusumActive)
            // Schedule a job that will call the event active if it runs.
            scheduleJob();
        else
            unscheduleJob(negativeCusumInactiveTime);
    }

    @Override
    synchronized public void pointUpdated(PointValueTime newValue) {
        double newDouble = newValue.getDoubleValue();

        cusum += newDouble - vo.getWeight();
        if (cusum > 0)
            cusum = 0;

        if (cusum < vo.getLimit()) {
            if (!negativeCusumActive) {
                negativeCusumActiveTime = newValue.getTime();
                changeNegativeCusumActive();
            }
        }
        else {
            if (negativeCusumActive) {
                negativeCusumInactiveTime = newValue.getTime();
                changeNegativeCusumActive();
            }
        }
    }

    @Override
    protected long getConditionActiveTime() {
        return negativeCusumActiveTime;
    }

    /**
     * This method is only called when the event changes between being active or not, i.e. if the event currently is
     * active, then it should never be called with a value of true. That said, provision is made to ensure that the
     * negative CUSUM limit is active before allowing the event to go active.
     * 
     * @param b
     */
    @Override
    synchronized public void setEventActive(boolean b) {
        eventActive = b;
        if (eventActive) {
            // Just for the fun of it, make sure that the negative CUSUM is active.
            if (negativeCusumActive)
                // Ok, things are good. Carry on...
                // Raise the event.
                raiseEvent(negativeCusumActiveTime + getDurationMS(), createEventContext());
            else
                eventActive = false;
        }
        else
            // Deactive the event.
            returnToNormal(negativeCusumInactiveTime);
    }
}
