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
package com.serotonin.mango.vo.event;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.schedule.ScheduledEventRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import com.serotonin.web.taglib.DateFunctions;

/**
 * @author Matthew Lohbihler
 * 
 */
@JsonRemoteEntity
public class ScheduledEventVO extends SimpleEventDetectorVO implements ChangeComparable<ScheduledEventVO>,
        JsonSerializable {
    public static final String XID_PREFIX = "SE_";

    public static String getEventDetectorKey(int id) {
        return SimpleEventDetectorVO.SCHEDULED_EVENT_PREFIX + id;
    }

    public static final int TYPE_HOURLY = 1;
    public static final int TYPE_DAILY = 2;
    public static final int TYPE_WEEKLY = 3;
    public static final int TYPE_MONTHLY = 4;
    public static final int TYPE_YEARLY = 5;
    public static final int TYPE_ONCE = 6;
    public static final int TYPE_CRON = 7;

    public static ExportCodes TYPE_CODES = new ExportCodes();
    static {
        TYPE_CODES.addElement(TYPE_HOURLY, "HOURLY", "scheduledEvents.type.hour");
        TYPE_CODES.addElement(TYPE_DAILY, "DAILY", "scheduledEvents.type.day");
        TYPE_CODES.addElement(TYPE_WEEKLY, "WEEKLY", "scheduledEvents.type.week");
        TYPE_CODES.addElement(TYPE_MONTHLY, "MONTHLY", "scheduledEvents.type.month");
        TYPE_CODES.addElement(TYPE_YEARLY, "YEARLY", "scheduledEvents.type.year");
        TYPE_CODES.addElement(TYPE_ONCE, "ONCE", "scheduledEvents.type.once");
        TYPE_CODES.addElement(TYPE_CRON, "CRON", "scheduledEvents.type.cron");
    }

    public boolean isNew() {
        return id == Common.NEW_ID;
    }

    private int id = Common.NEW_ID;
    private String xid;
    @JsonRemoteProperty
    private String alias;
    private int alarmLevel = AlarmLevels.NONE;
    private int scheduleType = TYPE_DAILY;
    @JsonRemoteProperty
    private boolean returnToNormal = true;
    @JsonRemoteProperty
    private boolean disabled = false;
    @JsonRemoteProperty
    private int activeYear;
    @JsonRemoteProperty
    private int activeMonth;
    @JsonRemoteProperty
    private int activeDay;
    @JsonRemoteProperty
    private int activeHour;
    @JsonRemoteProperty
    private int activeMinute;
    @JsonRemoteProperty
    private int activeSecond;
    @JsonRemoteProperty
    private String activeCron;
    @JsonRemoteProperty
    private int inactiveYear;
    @JsonRemoteProperty
    private int inactiveMonth;
    @JsonRemoteProperty
    private int inactiveDay;
    @JsonRemoteProperty
    private int inactiveHour;
    @JsonRemoteProperty
    private int inactiveMinute;
    @JsonRemoteProperty
    private int inactiveSecond;
    @JsonRemoteProperty
    private String inactiveCron;

    public EventTypeVO getEventType() {
        return new EventTypeVO(EventType.EventSources.SCHEDULED, id, 0, getDescription(), alarmLevel,
                getEventDetectorKey());
    }

    public ScheduledEventRT createRuntime() {
        return new ScheduledEventRT(this);
    }

    @Override
    public String getEventDetectorKey() {
        return getEventDetectorKey(id);
    }

    public LocalizableMessage getDescription() {
        LocalizableMessage message;

        if (!StringUtils.isEmpty(alias))
            message = new LocalizableMessage("common.default", alias);
        else if (scheduleType == TYPE_ONCE) {
            if (returnToNormal)
                message = new LocalizableMessage("event.schedule.onceUntil", DateFunctions.getTime(new DateTime(
                        activeYear, activeMonth, activeDay, activeHour, activeMinute, activeSecond, 0).getMillis()),
                        DateFunctions.getTime(new DateTime(inactiveYear, inactiveMonth, inactiveDay, inactiveHour,
                                inactiveMinute, inactiveSecond, 0).getMillis()));
            else
                message = new LocalizableMessage("event.schedule.onceAt", DateFunctions.getTime(new DateTime(
                        activeYear, activeMonth, activeDay, activeHour, activeMinute, activeSecond, 0).getMillis()));
        }
        else if (scheduleType == TYPE_HOURLY) {
            String activeTime = StringUtils.pad(Integer.toString(activeMinute), '0', 2) + ":"
                    + StringUtils.pad(Integer.toString(activeSecond), '0', 2);
            if (returnToNormal)
                message = new LocalizableMessage("event.schedule.hoursUntil", activeTime, StringUtils.pad(
                        Integer.toString(inactiveMinute), '0', 2)
                        + ":" + StringUtils.pad(Integer.toString(inactiveSecond), '0', 2));
            else
                message = new LocalizableMessage("event.schedule.hoursAt", activeTime);
        }
        else if (scheduleType == TYPE_DAILY) {
            if (returnToNormal)
                message = new LocalizableMessage("event.schedule.dailyUntil", activeTime(), inactiveTime());
            else
                message = new LocalizableMessage("event.schedule.dailyAt", activeTime());
        }
        else if (scheduleType == TYPE_WEEKLY) {
            if (returnToNormal)
                message = new LocalizableMessage("event.schedule.weeklyUntil", weekday(true), activeTime(),
                        weekday(false), inactiveTime());
            else
                message = new LocalizableMessage("event.schedule.weeklyAt", weekday(true), activeTime());
        }
        else if (scheduleType == TYPE_MONTHLY) {
            if (returnToNormal)
                message = new LocalizableMessage("event.schedule.monthlyUntil", monthday(true), activeTime(),
                        monthday(false), inactiveTime());
            else
                message = new LocalizableMessage("event.schedule.monthlyAt", monthday(true), activeTime());
        }
        else if (scheduleType == TYPE_YEARLY) {
            if (returnToNormal)
                message = new LocalizableMessage("event.schedule.yearlyUntil", monthday(true), month(true),
                        activeTime(), monthday(false), month(false), inactiveTime());
            else
                message = new LocalizableMessage("event.schedule.yearlyAt", monthday(true), month(true), activeTime());
        }
        else if (scheduleType == TYPE_CRON) {
            if (returnToNormal)
                message = new LocalizableMessage("event.schedule.cronUntil", activeCron, inactiveCron);
            else
                message = new LocalizableMessage("event.schedule.cronAt", activeCron);
        }
        else
            throw new ShouldNeverHappenException("Unknown schedule type: " + scheduleType);

        return message;
    }

    private LocalizableMessage getTypeMessage() {
        switch (scheduleType) {
        case TYPE_HOURLY:
            return new LocalizableMessage("scheduledEvents.type.hour");
        case TYPE_DAILY:
            return new LocalizableMessage("scheduledEvents.type.day");
        case TYPE_WEEKLY:
            return new LocalizableMessage("scheduledEvents.type.week");
        case TYPE_MONTHLY:
            return new LocalizableMessage("scheduledEvents.type.month");
        case TYPE_YEARLY:
            return new LocalizableMessage("scheduledEvents.type.year");
        case TYPE_ONCE:
            return new LocalizableMessage("scheduledEvents.type.once");
        case TYPE_CRON:
            return new LocalizableMessage("scheduledEvents.type.cron");
        }
        return null;
    }

    private String activeTime() {
        return StringUtils.pad(Integer.toString(activeHour), '0', 2) + ":"
                + StringUtils.pad(Integer.toString(activeMinute), '0', 2) + ":"
                + StringUtils.pad(Integer.toString(activeSecond), '0', 2);
    }

    private String inactiveTime() {
        return StringUtils.pad(Integer.toString(inactiveHour), '0', 2) + ":"
                + StringUtils.pad(Integer.toString(inactiveMinute), '0', 2) + ":"
                + StringUtils.pad(Integer.toString(inactiveSecond), '0', 2);
    }

    private static final String[] weekdays = { "", "common.day.mon", "common.day.tue", "common.day.wed",
            "common.day.thu", "common.day.fri", "common.day.sat", "common.day.sun" };

    private LocalizableMessage weekday(boolean active) {
        int day = activeDay;
        if (!active)
            day = inactiveDay;
        return new LocalizableMessage(weekdays[day]);
    }

    private LocalizableMessage monthday(boolean active) {
        int day = activeDay;

        if (!active)
            day = inactiveDay;

        if (day == -3)
            return new LocalizableMessage("common.day.thirdLast");
        if (day == -2)
            return new LocalizableMessage("common.day.secondLastLast");
        if (day == -1)
            return new LocalizableMessage("common.day.last");
        if (day != 11 && day % 10 == 1)
            return new LocalizableMessage("common.counting.st", Integer.toString(day));
        if (day != 12 && day % 10 == 2)
            return new LocalizableMessage("common.counting.nd", Integer.toString(day));
        if (day != 13 && day % 10 == 3)
            return new LocalizableMessage("common.counting.rd", Integer.toString(day));
        return new LocalizableMessage("common.counting.th", Integer.toString(day));
    }

    private static final String[] months = { "", "common.month.jan", "common.month.feb", "common.month.mar",
            "common.month.apr", "common.month.may", "common.month.jun", "common.month.jul", "common.month.aug",
            "common.month.sep", "common.month.oct", "common.month.nov", "common.month.dec" };

    private LocalizableMessage month(boolean active) {
        int day = activeDay;
        if (!active)
            day = inactiveDay;
        return new LocalizableMessage(months[day]);
    }

    @Override
    public String getTypeKey() {
        return "event.audit.scheduledEvent";
    }

    public void validate(DwrResponseI18n response) {
        if (StringUtils.isLengthGreaterThan(alias, 50))
            response.addContextualMessage("alias", "scheduledEvents.validate.aliasTooLong");

        // Check that cron patterns are ok.
        if (scheduleType == TYPE_CRON) {
            try {
                new CronTimerTrigger(activeCron);
            }
            catch (Exception e) {
                response.addContextualMessage("activeCron", "scheduledEvents.validate.activeCron", e.getMessage());
            }

            if (returnToNormal) {
                try {
                    new CronTimerTrigger(inactiveCron);
                }
                catch (Exception e) {
                    response.addContextualMessage("inactiveCron", "scheduledEvents.validate.inactiveCron",
                            e.getMessage());
                }
            }
        }

        // Test that the triggers can be created.
        ScheduledEventRT rt = createRuntime();
        try {
            rt.createTrigger(true);
        }
        catch (RuntimeException e) {
            response.addContextualMessage("activeCron", "scheduledEvents.validate.activeTrigger", e.getMessage());
        }

        if (returnToNormal) {
            try {
                rt.createTrigger(false);
            }
            catch (RuntimeException e) {
                response.addContextualMessage("inactiveCron", "scheduledEvents.validate.inactiveTrigger",
                        e.getMessage());
            }
        }

        // If the event is once, make sure the active time is earlier than the inactive time.
        if (scheduleType == TYPE_ONCE && returnToNormal) {
            DateTime adt = new DateTime(activeYear, activeMonth, activeDay, activeHour, activeMinute, activeSecond, 0);
            DateTime idt = new DateTime(inactiveYear, inactiveMonth, inactiveDay, inactiveHour, inactiveMinute,
                    inactiveSecond, 0);
            if (idt.getMillis() <= adt.getMillis())
                response.addContextualMessage("scheduleType", "scheduledEvents.validate.invalidRtn");
        }
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "common.xid", xid);
        AuditEventType.addPropertyMessage(list, "scheduledEvents.alias", alias);
        AuditEventType.addPropertyMessage(list, "common.alarmLevel", AlarmLevels.getAlarmLevelMessage(alarmLevel));
        AuditEventType.addPropertyMessage(list, "scheduledEvents.type", getTypeMessage());
        AuditEventType.addPropertyMessage(list, "common.rtn", returnToNormal);
        AuditEventType.addPropertyMessage(list, "common.disabled", disabled);
        AuditEventType.addPropertyMessage(list, "common.configuration", getDescription());
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, ScheduledEventVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.xid", from.xid, xid);
        AuditEventType.maybeAddPropertyChangeMessage(list, "scheduledEvents.alias", from.alias, alias);
        AuditEventType.maybeAddAlarmLevelChangeMessage(list, "common.alarmLevel", from.alarmLevel, alarmLevel);
        if (from.scheduleType != scheduleType)
            AuditEventType.addPropertyChangeMessage(list, "scheduledEvents.type", from.getTypeMessage(),
                    getTypeMessage());
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.rtn", from.returnToNormal, returnToNormal);
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.disabled", from.disabled, disabled);
        if (from.activeYear != activeYear || from.activeMonth != activeMonth || from.activeDay != activeDay
                || from.activeHour != activeHour || from.activeMinute != activeMinute
                || from.activeSecond != activeSecond || from.activeCron != activeCron
                || from.inactiveYear != inactiveYear || from.inactiveMonth != inactiveMonth
                || from.inactiveDay != inactiveDay || from.inactiveHour != inactiveHour
                || from.inactiveMinute != inactiveMinute || from.inactiveSecond != inactiveSecond
                || from.inactiveCron != inactiveCron)
            AuditEventType.maybeAddPropertyChangeMessage(list, "common.configuration", from.getDescription(),
                    getDescription());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(int activeDay) {
        this.activeDay = activeDay;
    }

    public int getActiveHour() {
        return activeHour;
    }

    public void setActiveHour(int activeHour) {
        this.activeHour = activeHour;
    }

    public int getActiveMinute() {
        return activeMinute;
    }

    public void setActiveMinute(int activeMinute) {
        this.activeMinute = activeMinute;
    }

    public int getActiveMonth() {
        return activeMonth;
    }

    public void setActiveMonth(int activeMonth) {
        this.activeMonth = activeMonth;
    }

    public int getActiveSecond() {
        return activeSecond;
    }

    public void setActiveSecond(int activeSecond) {
        this.activeSecond = activeSecond;
    }

    public int getActiveYear() {
        return activeYear;
    }

    public void setActiveYear(int activeYear) {
        this.activeYear = activeYear;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public int getInactiveDay() {
        return inactiveDay;
    }

    public void setInactiveDay(int inactiveDay) {
        this.inactiveDay = inactiveDay;
    }

    public int getInactiveHour() {
        return inactiveHour;
    }

    public void setInactiveHour(int inactiveHour) {
        this.inactiveHour = inactiveHour;
    }

    public int getInactiveMinute() {
        return inactiveMinute;
    }

    public void setInactiveMinute(int inactiveMinute) {
        this.inactiveMinute = inactiveMinute;
    }

    public int getInactiveMonth() {
        return inactiveMonth;
    }

    public void setInactiveMonth(int inactiveMonth) {
        this.inactiveMonth = inactiveMonth;
    }

    public int getInactiveSecond() {
        return inactiveSecond;
    }

    public void setInactiveSecond(int inactiveSecond) {
        this.inactiveSecond = inactiveSecond;
    }

    public int getInactiveYear() {
        return inactiveYear;
    }

    public void setInactiveYear(int inactiveYear) {
        this.inactiveYear = inactiveYear;
    }

    public boolean isReturnToNormal() {
        return returnToNormal;
    }

    public void setReturnToNormal(boolean returnToNormal) {
        this.returnToNormal = returnToNormal;
    }

    public int getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(int scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getActiveCron() {
        return activeCron;
    }

    public void setActiveCron(String activeCron) {
        this.activeCron = activeCron;
    }

    public String getInactiveCron() {
        return inactiveCron;
    }

    public void setInactiveCron(String inactiveCron) {
        this.inactiveCron = inactiveCron;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    //
    // /
    // / Serialization
    // /
    //
    public void jsonSerialize(Map<String, Object> map) {
        map.put("xid", xid);
        map.put("alarmLevel", AlarmLevels.CODES.getCode(alarmLevel));
        map.put("scheduleType", TYPE_CODES.getCode(scheduleType));
    }

    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String text = json.getString("alarmLevel");
        if (text != null) {
            alarmLevel = AlarmLevels.CODES.getId(text);
            if (!AlarmLevels.CODES.isValidId(alarmLevel))
                throw new LocalizableJsonException("emport.error.scheduledEvent.invalid", "alarmLevel", text,
                        AlarmLevels.CODES.getCodeList());
        }

        text = json.getString("scheduleType");
        if (text != null) {
            scheduleType = TYPE_CODES.getId(text);
            if (!TYPE_CODES.isValidId(scheduleType))
                throw new LocalizableJsonException("emport.error.scheduledEvent.invalid", "scheduleType", text,
                        TYPE_CODES.getCodeList());
        }
    }
}
