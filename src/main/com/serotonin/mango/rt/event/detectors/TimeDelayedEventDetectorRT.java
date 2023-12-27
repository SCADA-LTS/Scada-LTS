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

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;

/**
 * This is a base class for all subclasses that need to schedule timeouts for them to become active.
 * 
 * @author Matthew Lohbihler
 */
abstract public class TimeDelayedEventDetectorRT extends TimeoutDetectorRT {
    synchronized protected void scheduleJob() {
        if (getDurationMS() > 0)
            scheduleJob(System.currentTimeMillis() + getDurationMS());
        else
            // Otherwise call the event active immediately.
            setEventActive(true);
    }

    synchronized protected void unscheduleJob(long conditionInactiveTime) {
        // Reset the eventActive if it is on
        if (isEventActive())
            setEventActive(false);
        // Check whether there is a tolerance duration.
        else if (getDurationMS() > 0) {
            if (isJobScheduled()) {
                unscheduleJob();

                // There is an existing job scheduled. It's fire time is likely past when the event is to actually fire,
                // so check if the event activation time 
                long eventActiveTime = getConditionActiveTime() + getDurationMS();

                if (eventActiveTime < conditionInactiveTime) {
                    // The event should go active.
                    raiseEvent(eventActiveTime, createEventContext());
                    // And then go inactive
                    returnToNormal(conditionInactiveTime);
                }
            }
        }
    }

    abstract protected long getConditionActiveTime();

    abstract void setEventActive(boolean b);

    @Override
    public void initialize() {
        super.initialize();
        initializeState();
    }

    protected void initializeState() {
        int pointId = vo.njbGetDataPoint().getId();
        PointValueTime latest = Common.ctx.getRuntimeManager().getDataPoint(pointId).getPointValue();

        if (latest != null)
            pointChanged(null, latest);
    }

    @Override
    public void scheduleTimeoutImpl(long fireTime) {
        setEventActive(true);
    }
}
