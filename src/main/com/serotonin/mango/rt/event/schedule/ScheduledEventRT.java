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
package com.serotonin.mango.rt.event.schedule;

import java.text.ParseException;
import java.util.Date;

import org.joda.time.DateTime;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.SimpleEventDetector;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.ScheduledEventType;
import com.serotonin.mango.util.timeout.ModelTimeoutClient;
import com.serotonin.mango.util.timeout.ModelTimeoutTask;
import com.serotonin.mango.vo.event.ScheduledEventVO;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.timer.OneTimeTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTrigger;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 * 
 */
public class ScheduledEventRT extends SimpleEventDetector implements ModelTimeoutClient<Boolean> {
    private final ScheduledEventVO vo;
    private ScheduledEventType eventType;
    private boolean eventActive;
    private TimerTask activeTask;
    private TimerTask inactiveTask;

    public ScheduledEventRT(ScheduledEventVO vo) {
        this.vo = vo;
    }

    public ScheduledEventVO getVo() {
        return vo;
    }

    private void raiseEvent(long time) {
        Common.ctx.getEventManager().raiseEvent(eventType, time, vo.isReturnToNormal(), vo.getAlarmLevel(),
                getMessage(), null);
        eventActive = true;
        fireEventDetectorStateChanged(time);
    }

    private void returnToNormal(long time) {
        Common.ctx.getEventManager().returnToNormal(eventType, time);
        eventActive = false;
        fireEventDetectorStateChanged(time);
    }

    public LocalizableMessage getMessage() {
        return new LocalizableMessage("event.schedule.active", vo.getDescription());
    }

    public boolean isEventActive() {
        return eventActive;
    }

    @Override
    synchronized public void scheduleTimeout(Boolean active, long fireTime) {
        if (active)
            raiseEvent(fireTime);
        else
            returnToNormal(fireTime);
    }

    //
    //
    // /
    // / Lifecycle interface
    // /
    //
    //
    public void initialize() {
        eventType = new ScheduledEventType(vo.getId());
        if (!vo.isReturnToNormal())
            eventType.setDuplicateHandling(EventType.DuplicateHandling.ALLOW);

        // Schedule the active event.
        TimerTrigger activeTrigger = createTrigger(true);
        activeTask = new ModelTimeoutTask<Boolean>(activeTrigger, this, true);

        if (vo.isReturnToNormal()) {
            TimerTrigger inactiveTrigger = createTrigger(false);
            inactiveTask = new ModelTimeoutTask<Boolean>(inactiveTrigger, this, false);

            if (vo.getScheduleType() != ScheduledEventVO.TYPE_ONCE) {
                // Check if we are currently active.
                if (inactiveTrigger.getNextExecutionTime() < activeTrigger.getNextExecutionTime())
                    raiseEvent(System.currentTimeMillis());
            }
        }
    }

    @Override
    public void terminate() {
        fireEventDetectorTerminated();
        if (activeTask != null)
            activeTask.cancel();
        if (inactiveTask != null)
            inactiveTask.cancel();
        returnToNormal(System.currentTimeMillis());
    }

    public void joinTermination() {
        // no op
    }

    private static final String[] weekdays = { "", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN" };

    public TimerTrigger createTrigger(boolean activeTrigger) {
        if (!activeTrigger && !vo.isReturnToNormal())
            return null;

        if (vo.getScheduleType() == ScheduledEventVO.TYPE_CRON) {
            try {
                if (activeTrigger)
                    return new CronTimerTrigger(vo.getActiveCron());
                return new CronTimerTrigger(vo.getInactiveCron());
            }
            catch (ParseException e) {
                // Should never happen, so wrap and rethrow
                throw new ShouldNeverHappenException(e);
            }
        }

        if (vo.getScheduleType() == ScheduledEventVO.TYPE_ONCE) {
            DateTime dt;
            if (activeTrigger)
                dt = new DateTime(vo.getActiveYear(), vo.getActiveMonth(), vo.getActiveDay(), vo.getActiveHour(),
                        vo.getActiveMinute(), vo.getActiveSecond(), 0);
            else
                dt = new DateTime(vo.getInactiveYear(), vo.getInactiveMonth(), vo.getInactiveDay(),
                        vo.getInactiveHour(), vo.getInactiveMinute(), vo.getInactiveSecond(), 0);
            return new OneTimeTrigger(new Date(dt.getMillis()));
        }

        int month = vo.getActiveMonth();
        int day = vo.getActiveDay();
        int hour = vo.getActiveHour();
        int minute = vo.getActiveMinute();
        int second = vo.getActiveSecond();
        if (!activeTrigger) {
            month = vo.getInactiveMonth();
            day = vo.getInactiveDay();
            hour = vo.getInactiveHour();
            minute = vo.getInactiveMinute();
            second = vo.getInactiveSecond();
        }

        StringBuilder expression = new StringBuilder();
        expression.append(second).append(' ');
        expression.append(minute).append(' ');
        if (vo.getScheduleType() == ScheduledEventVO.TYPE_HOURLY)
            expression.append("* * * ?");
        else {
            expression.append(hour).append(' ');
            if (vo.getScheduleType() == ScheduledEventVO.TYPE_DAILY)
                expression.append("* * ?");
            else if (vo.getScheduleType() == ScheduledEventVO.TYPE_WEEKLY)
                expression.append("? * ").append(weekdays[day]);
            else {
                if (day > 0)
                    expression.append(day);
                else if (day == -1)
                    expression.append('L');
                else
                    expression.append(-day).append('L');

                if (vo.getScheduleType() == ScheduledEventVO.TYPE_MONTHLY)
                    expression.append(" * ?");
                else
                    expression.append(' ').append(month).append(" ?");
            }
        }

        CronTimerTrigger cronTrigger;
        try {
            cronTrigger = new CronTimerTrigger(expression.toString());
        }
        catch (ParseException e) {
            // Should never happen, so wrap and rethrow
            throw new ShouldNeverHappenException(e);
        }
        return cronTrigger;
    }
}
