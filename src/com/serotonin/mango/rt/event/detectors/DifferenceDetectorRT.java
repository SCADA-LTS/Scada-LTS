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
 * @author Matthew Lohbihler
 */
abstract public class DifferenceDetectorRT extends TimeDelayedEventDetectorRT {
    /**
     * State field. Whether the event is currently active or not. This field is used to prevent multiple events being
     * raised during the duration of a single state detection.
     */
    protected boolean eventActive;

    protected long lastChange;

    public boolean isEventActive() {
        return eventActive;
    }

    synchronized protected void pointData() {
        if (!eventActive)
            unscheduleJob(System.currentTimeMillis());
        else
            setEventActive(false);
        lastChange = System.currentTimeMillis();
        scheduleJob();
    }

    @Override
    public void initializeState() {
        // Get historical data for the point out of the database.
        int pointId = vo.njbGetDataPoint().getId();
        PointValueTime latest = Common.ctx.getRuntimeManager().getDataPoint(pointId).getPointValue();
        if (latest != null)
            lastChange = latest.getTime();
        else
            // The point may be new or not logged, so don't go active immediately.
            lastChange = System.currentTimeMillis();

        if (lastChange + getDurationMS() < System.currentTimeMillis())
            // Nothing has happened in the time frame, so set the event active.
            setEventActive(true);
        else
            // Otherwise, set the timeout.
            scheduleJob();
    }

    @Override
    protected long getConditionActiveTime() {
        return lastChange;
    }

    @Override
    synchronized public void setEventActive(boolean b) {
        eventActive = b;
        if (eventActive)
            // Raise the event.
            raiseEvent(lastChange + getDurationMS(), createEventContext());
        else
            // Deactivate the event.
            returnToNormal(lastChange);
    }
}
