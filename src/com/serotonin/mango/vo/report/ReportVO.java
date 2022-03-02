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
package com.serotonin.mango.vo.report;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.serotonin.json.*;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.User;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import com.serotonin.mango.Common;
import com.serotonin.mango.util.DateUtils;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;
import com.serotonin.util.SerializationHelper;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.mvc.api.dto.ReportDTO;

/**
 * @author Matthew Lohbihler
 */

@JsonRemoteEntity
public class ReportVO implements Serializable, JsonSerializable {
    public static final int DATE_RANGE_TYPE_RELATIVE = 1;
    public static final int DATE_RANGE_TYPE_SPECIFIC = 2;

    public static final int EVENTS_NONE = 1;
    public static final int EVENTS_ALARMS = 2;
    public static final int EVENTS_ALL = 3;

    public static final int RELATIVE_DATE_TYPE_PREVIOUS = 1;
    public static final int RELATIVE_DATE_TYPE_PAST = 2;

    public static final int SCHEDULE_CRON = 0;

    @JsonRemoteProperty
    private int id = Common.NEW_ID;
    @JsonRemoteProperty
    private String xid;

    private int userId;
    private String username;
    @JsonRemoteProperty
    private String name;

    private List<ReportPointVO> points = new ArrayList<ReportPointVO>();
    @JsonRemoteProperty
    private int includeEvents = EVENTS_ALARMS;
    @JsonRemoteProperty
    private boolean includeUserComments = true;
    @JsonRemoteProperty
    private int dateRangeType = DATE_RANGE_TYPE_RELATIVE;
    @JsonRemoteProperty
    private int relativeDateType = RELATIVE_DATE_TYPE_PREVIOUS;

    @JsonRemoteProperty
    private int previousPeriodCount = 1;
    @JsonRemoteProperty
    private int previousPeriodType = Common.TimePeriods.DAYS;
    @JsonRemoteProperty
    private int pastPeriodCount = 1;
    @JsonRemoteProperty
    private int pastPeriodType = Common.TimePeriods.DAYS;

    @JsonRemoteProperty
    private boolean fromNone;
    @JsonRemoteProperty
    private int fromYear;
    @JsonRemoteProperty
    private int fromMonth;
    @JsonRemoteProperty
    private int fromDay;
    @JsonRemoteProperty
    private int fromHour;
    @JsonRemoteProperty
    private int fromMinute;

    @JsonRemoteProperty
    private boolean toNone;
    @JsonRemoteProperty
    private int toYear;
    @JsonRemoteProperty
    private int toMonth;
    @JsonRemoteProperty
    private int toDay;
    @JsonRemoteProperty
    private int toHour;
    @JsonRemoteProperty
    private int toMinute;

    @JsonRemoteProperty
    private boolean schedule;
    @JsonRemoteProperty
    private int schedulePeriod = Common.TimePeriods.DAYS;
    @JsonRemoteProperty
    private int runDelayMinutes;
    @JsonRemoteProperty
    private String scheduleCron;

    @JsonRemoteProperty
    private boolean email;

    private List<RecipientListEntryBean> recipients = new ArrayList<RecipientListEntryBean>();
    @JsonRemoteProperty
    private boolean includeData = true;
    @JsonRemoteProperty
    private boolean zipData = false;

    private static final Log LOG = LogFactory.getLog(ReportVO.class);

    public ReportVO() {
        // Default the specific date fields.
        DateTime dt = DateUtils.truncateDateTime(new DateTime(), Common.TimePeriods.DAYS);
        toYear = dt.getYear();
        toMonth = dt.getMonthOfYear();
        toDay = dt.getDayOfMonth();
        toHour = dt.getHourOfDay();
        toMinute = dt.getMinuteOfHour();

        dt = DateUtils.minus(dt, Common.TimePeriods.DAYS, 1);
        fromYear = dt.getYear();
        fromMonth = dt.getMonthOfYear();
        fromDay = dt.getDayOfMonth();
        fromHour = dt.getHourOfDay();
        fromMinute = dt.getMinuteOfHour();
    }

    public static ReportVO createReport(ReportDTO query, User user, List<ReportPointVO> reportPoints) {
        ReportVO report = new ReportVO();
        report.setUserId(user.getId());
        report.setId(query.getId());
        report.setName(query.getName());
        report.setPoints(reportPoints);
        report.setIncludeEvents(query.getIncludeEvents());
        report.setIncludeUserComments(query.isIncludeUserComments());
        report.setDateRangeType(query.getDateRangeType());
        report.setRelativeDateType(query.getRelativeDateType());
        report.setPreviousPeriodCount(query.getPreviousPeriodCount());
        report.setPreviousPeriodType(query.getPreviousPeriodType());
        report.setPastPeriodCount(query.getPastPeriodCount());
        report.setPastPeriodType(query.getPastPeriodType());
        report.setFromNone(query.isFromNone());
        report.setFromYear(query.getFromYear());
        report.setFromMonth(query.getFromMonth());
        report.setFromDay(query.getFromDay());
        report.setFromHour(query.getFromHour());
        report.setFromMinute(query.getFromMinute());
        report.setToNone(query.isToNone());
        report.setToYear(query.getToYear());
        report.setToMonth(query.getToMonth());
        report.setToDay(query.getToDay());
        report.setToHour(query.getToHour());
        report.setToMinute(query.getToMinute());
        report.setSchedule(query.isSchedule());
        report.setSchedulePeriod(query.getSchedulePeriod());
        report.setRunDelayMinutes(query.getRunDelayMinutes());
        report.setScheduleCron(query.getScheduleCron());
        report.setEmail(query.isEmail());
        report.setIncludeData(query.isIncludeData());
        report.setZipData(query.isZipData());
        report.setRecipients(query.getRecipients());
        return report;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ReportPointVO> getPoints() {
        return points;
    }

    public void setPoints(List<ReportPointVO> points) {
        this.points = points;
    }

    public int getIncludeEvents() {
        return includeEvents;
    }

    public void setIncludeEvents(int includeEvents) {
        this.includeEvents = includeEvents;
    }

    public boolean isIncludeUserComments() {
        return includeUserComments;
    }

    public void setIncludeUserComments(boolean includeUserComments) {
        this.includeUserComments = includeUserComments;
    }

    public int getDateRangeType() {
        return dateRangeType;
    }

    public void setDateRangeType(int dateRangeType) {
        this.dateRangeType = dateRangeType;
    }

    public int getRelativeDateType() {
        return relativeDateType;
    }

    public void setRelativeDateType(int relativeDateType) {
        this.relativeDateType = relativeDateType;
    }

    public int getPreviousPeriodCount() {
        return previousPeriodCount;
    }

    public void setPreviousPeriodCount(int previousPeriodCount) {
        this.previousPeriodCount = previousPeriodCount;
    }

    public int getPreviousPeriodType() {
        return previousPeriodType;
    }

    public void setPreviousPeriodType(int previousPeriodType) {
        this.previousPeriodType = previousPeriodType;
    }

    public int getPastPeriodCount() {
        return pastPeriodCount;
    }

    public void setPastPeriodCount(int pastPeriodCount) {
        this.pastPeriodCount = pastPeriodCount;
    }

    public int getPastPeriodType() {
        return pastPeriodType;
    }

    public void setPastPeriodType(int pastPeriodType) {
        this.pastPeriodType = pastPeriodType;
    }

    public boolean isFromNone() {
        return fromNone;
    }

    public void setFromNone(boolean fromNone) {
        this.fromNone = fromNone;
    }

    public int getFromYear() {
        return fromYear;
    }

    public void setFromYear(int fromYear) {
        this.fromYear = fromYear;
    }

    public int getFromMonth() {
        return fromMonth;
    }

    public void setFromMonth(int fromMonth) {
        this.fromMonth = fromMonth;
    }

    public int getFromDay() {
        return fromDay;
    }

    public void setFromDay(int fromDay) {
        this.fromDay = fromDay;
    }

    public int getFromHour() {
        return fromHour;
    }

    public void setFromHour(int fromHour) {
        this.fromHour = fromHour;
    }

    public int getFromMinute() {
        return fromMinute;
    }

    public void setFromMinute(int fromMinute) {
        this.fromMinute = fromMinute;
    }

    public boolean isToNone() {
        return toNone;
    }

    public void setToNone(boolean toNone) {
        this.toNone = toNone;
    }

    public int getToYear() {
        return toYear;
    }

    public void setToYear(int toYear) {
        this.toYear = toYear;
    }

    public int getToMonth() {
        return toMonth;
    }

    public void setToMonth(int toMonth) {
        this.toMonth = toMonth;
    }

    public int getToDay() {
        return toDay;
    }

    public void setToDay(int toDay) {
        this.toDay = toDay;
    }

    public int getToHour() {
        return toHour;
    }

    public void setToHour(int toHour) {
        this.toHour = toHour;
    }

    public int getToMinute() {
        return toMinute;
    }

    public void setToMinute(int toMinute) {
        this.toMinute = toMinute;
    }

    public boolean isSchedule() {
        return schedule;
    }

    public void setSchedule(boolean schedule) {
        this.schedule = schedule;
    }

    public int getSchedulePeriod() {
        return schedulePeriod;
    }

    public void setSchedulePeriod(int schedulePeriod) {
        this.schedulePeriod = schedulePeriod;
    }

    public String getScheduleCron() {
        return scheduleCron;
    }

    public void setScheduleCron(String scheduleCron) {
        this.scheduleCron = scheduleCron;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public List<RecipientListEntryBean> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<RecipientListEntryBean> recipients) {
        this.recipients = recipients;
    }

    public boolean isIncludeData() {
        return includeData;
    }

    public void setIncludeData(boolean includeData) {
        this.includeData = includeData;
    }

    public boolean isZipData() {
        return zipData;
    }

    public void setZipData(boolean zipData) {
        this.zipData = zipData;
    }

    public int getRunDelayMinutes() {
        return runDelayMinutes;
    }

    public void setRunDelayMinutes(int runDelayMinutes) {
        this.runDelayMinutes = runDelayMinutes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    //
    //
    // Serialization
    //
    private static final long serialVersionUID = -1;
    private static final int version = 7;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);

        out.writeObject(points);
        out.writeInt(includeEvents);
        out.writeBoolean(includeUserComments);
        out.writeInt(dateRangeType);
        out.writeInt(relativeDateType);

        out.writeInt(previousPeriodCount);
        out.writeInt(previousPeriodType);
        out.writeInt(pastPeriodCount);
        out.writeInt(pastPeriodType);

        out.writeBoolean(fromNone);
        out.writeInt(fromYear);
        out.writeInt(fromMonth);
        out.writeInt(fromDay);
        out.writeInt(fromHour);
        out.writeInt(fromMinute);
        out.writeBoolean(toNone);
        out.writeInt(toYear);
        out.writeInt(toMonth);
        out.writeInt(toDay);
        out.writeInt(toHour);
        out.writeInt(toMinute);

        out.writeBoolean(schedule);
        out.writeInt(schedulePeriod);
        out.writeInt(runDelayMinutes);
        SerializationHelper.writeSafeUTF(out, scheduleCron);
        out.writeBoolean(email);
        out.writeObject(recipients);
        out.writeBoolean(includeData);
        out.writeBoolean(zipData);
        SerializationHelper.writeSafeUTF(out, username);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            points = convertToReportPointVOs((List<Integer>) in.readObject());
            includeEvents = EVENTS_ALARMS;
            includeUserComments = true;
            dateRangeType = in.readInt();
            relativeDateType = in.readInt();

            previousPeriodCount = in.readInt();
            previousPeriodType = in.readInt();
            pastPeriodCount = in.readInt();
            pastPeriodType = in.readInt();

            fromNone = in.readBoolean();
            fromYear = in.readInt();
            fromMonth = in.readInt();
            fromDay = in.readInt();
            fromHour = in.readInt();
            fromMinute = in.readInt();
            toNone = in.readBoolean();
            toYear = in.readInt();
            toMonth = in.readInt();
            toDay = in.readInt();
            toHour = in.readInt();
            toMinute = in.readInt();

            schedule = in.readBoolean();
            schedulePeriod = in.readInt();
            runDelayMinutes = 0;
            scheduleCron = SerializationHelper.readSafeUTF(in);
            email = in.readBoolean();
            recipients = (List<RecipientListEntryBean>) in.readObject();
            includeData = in.readBoolean();
            zipData = false;
        }
        else if (ver == 2) {
            points = convertToReportPointVOs((List<Integer>) in.readObject());
            includeEvents = EVENTS_ALARMS;
            includeUserComments = true;
            dateRangeType = in.readInt();
            relativeDateType = in.readInt();

            previousPeriodCount = in.readInt();
            previousPeriodType = in.readInt();
            pastPeriodCount = in.readInt();
            pastPeriodType = in.readInt();

            fromNone = in.readBoolean();
            fromYear = in.readInt();
            fromMonth = in.readInt();
            fromDay = in.readInt();
            fromHour = in.readInt();
            fromMinute = in.readInt();
            toNone = in.readBoolean();
            toYear = in.readInt();
            toMonth = in.readInt();
            toDay = in.readInt();
            toHour = in.readInt();
            toMinute = in.readInt();

            schedule = in.readBoolean();
            schedulePeriod = in.readInt();
            runDelayMinutes = in.readInt();
            scheduleCron = SerializationHelper.readSafeUTF(in);
            email = in.readBoolean();
            recipients = (List<RecipientListEntryBean>) in.readObject();
            includeData = in.readBoolean();
            zipData = false;
        }
        else if (ver == 3) {
            points = convertToReportPointVOs((List<Integer>) in.readObject());
            includeEvents = in.readInt();
            includeUserComments = true;
            dateRangeType = in.readInt();
            relativeDateType = in.readInt();

            previousPeriodCount = in.readInt();
            previousPeriodType = in.readInt();
            pastPeriodCount = in.readInt();
            pastPeriodType = in.readInt();

            fromNone = in.readBoolean();
            fromYear = in.readInt();
            fromMonth = in.readInt();
            fromDay = in.readInt();
            fromHour = in.readInt();
            fromMinute = in.readInt();
            toNone = in.readBoolean();
            toYear = in.readInt();
            toMonth = in.readInt();
            toDay = in.readInt();
            toHour = in.readInt();
            toMinute = in.readInt();

            schedule = in.readBoolean();
            schedulePeriod = in.readInt();
            runDelayMinutes = in.readInt();
            scheduleCron = SerializationHelper.readSafeUTF(in);
            email = in.readBoolean();
            recipients = (List<RecipientListEntryBean>) in.readObject();
            includeData = in.readBoolean();
            zipData = false;
        }
        else if (ver == 4) {
            points = convertToReportPointVOs((List<Integer>) in.readObject());
            includeEvents = in.readInt();
            includeUserComments = in.readBoolean();
            dateRangeType = in.readInt();
            relativeDateType = in.readInt();

            previousPeriodCount = in.readInt();
            previousPeriodType = in.readInt();
            pastPeriodCount = in.readInt();
            pastPeriodType = in.readInt();

            fromNone = in.readBoolean();
            fromYear = in.readInt();
            fromMonth = in.readInt();
            fromDay = in.readInt();
            fromHour = in.readInt();
            fromMinute = in.readInt();
            toNone = in.readBoolean();
            toYear = in.readInt();
            toMonth = in.readInt();
            toDay = in.readInt();
            toHour = in.readInt();
            toMinute = in.readInt();

            schedule = in.readBoolean();
            schedulePeriod = in.readInt();
            runDelayMinutes = in.readInt();
            scheduleCron = SerializationHelper.readSafeUTF(in);
            email = in.readBoolean();
            recipients = (List<RecipientListEntryBean>) in.readObject();
            includeData = in.readBoolean();
            zipData = false;
        }
        else if (ver == 5) {
            points = (List<ReportPointVO>) in.readObject();
            includeEvents = in.readInt();
            includeUserComments = in.readBoolean();
            dateRangeType = in.readInt();
            relativeDateType = in.readInt();

            previousPeriodCount = in.readInt();
            previousPeriodType = in.readInt();
            pastPeriodCount = in.readInt();
            pastPeriodType = in.readInt();

            fromNone = in.readBoolean();
            fromYear = in.readInt();
            fromMonth = in.readInt();
            fromDay = in.readInt();
            fromHour = in.readInt();
            fromMinute = in.readInt();
            toNone = in.readBoolean();
            toYear = in.readInt();
            toMonth = in.readInt();
            toDay = in.readInt();
            toHour = in.readInt();
            toMinute = in.readInt();

            schedule = in.readBoolean();
            schedulePeriod = in.readInt();
            runDelayMinutes = in.readInt();
            scheduleCron = SerializationHelper.readSafeUTF(in);
            email = in.readBoolean();
            recipients = (List<RecipientListEntryBean>) in.readObject();
            includeData = in.readBoolean();
            zipData = false;
        }
        else if (ver == 6) {
            points = (List<ReportPointVO>) in.readObject();
            includeEvents = in.readInt();
            includeUserComments = in.readBoolean();
            dateRangeType = in.readInt();
            relativeDateType = in.readInt();

            previousPeriodCount = in.readInt();
            previousPeriodType = in.readInt();
            pastPeriodCount = in.readInt();
            pastPeriodType = in.readInt();

            fromNone = in.readBoolean();
            fromYear = in.readInt();
            fromMonth = in.readInt();
            fromDay = in.readInt();
            fromHour = in.readInt();
            fromMinute = in.readInt();
            toNone = in.readBoolean();
            toYear = in.readInt();
            toMonth = in.readInt();
            toDay = in.readInt();
            toHour = in.readInt();
            toMinute = in.readInt();

            schedule = in.readBoolean();
            schedulePeriod = in.readInt();
            runDelayMinutes = in.readInt();
            scheduleCron = SerializationHelper.readSafeUTF(in);
            email = in.readBoolean();
            recipients = (List<RecipientListEntryBean>) in.readObject();
            includeData = in.readBoolean();
            zipData = in.readBoolean();
        }

        if(ver == 7) {
            points = (List<ReportPointVO>) in.readObject();
            includeEvents = in.readInt();
            includeUserComments = in.readBoolean();
            dateRangeType = in.readInt();
            relativeDateType = in.readInt();

            previousPeriodCount = in.readInt();
            previousPeriodType = in.readInt();
            pastPeriodCount = in.readInt();
            pastPeriodType = in.readInt();

            fromNone = in.readBoolean();
            fromYear = in.readInt();
            fromMonth = in.readInt();
            fromDay = in.readInt();
            fromHour = in.readInt();
            fromMinute = in.readInt();
            toNone = in.readBoolean();
            toYear = in.readInt();
            toMonth = in.readInt();
            toDay = in.readInt();
            toHour = in.readInt();
            toMinute = in.readInt();

            schedule = in.readBoolean();
            schedulePeriod = in.readInt();
            runDelayMinutes = in.readInt();
            scheduleCron = SerializationHelper.readSafeUTF(in);
            email = in.readBoolean();
            recipients = (List<RecipientListEntryBean>) in.readObject();
            includeData = in.readBoolean();
            zipData = in.readBoolean();
            username = SerializationHelper.readSafeUTF(in);
        }
    }

    private static List<ReportPointVO> convertToReportPointVOs(List<Integer> ids) {
        List<ReportPointVO> result = new ArrayList<ReportPointVO>();
        for (Integer id : ids) {
            ReportPointVO vo = new ReportPointVO();
            vo.setPointId(id);
            result.add(vo);
        }
        return result;
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("points", points);
        map.put("recipients", recipients);
        map.put("userId", userId);
        map.put("username", username);
    }

    @Override
    public void jsonDeserialize(JsonReader jsonReader, JsonObject jsonObject) throws JsonException {
        JsonArray jsonPoints = jsonObject.getJsonArray("points");
        if (jsonPoints != null) {
            points.clear();
            for (JsonValue jv : jsonPoints.getElements()) {
                JsonObject jsonPoint = jv.toJsonObject();
                ReportPointVO reportPoint = new ReportPointVO();
                jsonReader.populateObject(reportPoint, jsonPoint);
                points.add(reportPoint);
            }
        }

        JsonArray jsonRecipients = jsonObject.getJsonArray("recipients");
        if (jsonRecipients != null) {
            recipients.clear();
            for (JsonValue jv : jsonRecipients.getElements()) {
                JsonObject jsonRecipient = jv.toJsonObject();
                RecipientListEntryBean recipient = new RecipientListEntryBean();
                jsonReader.populateObject(recipient, jsonRecipient);
                recipients.add(recipient);
            }
        }

        MangoUser userService = new UserService();
        String owner = jsonObject.getString("username");
        if (!StringUtils.isEmpty(owner)) {
            try {
                User user;
                if((user = userService.getUser(owner)) == null) {
                    throw new LocalizableJsonException("emport.error.missingUser", owner);
                }
                this.userId = user.getId();
                this.username = user.getUsername();
            } catch (LocalizableJsonException ex) {
                LOG.warn(ex.getMessage(), ex);
                throw ex;
            } catch (Exception ex) {
                LOG.warn(ex.getMessage(), ex);
                throw new LocalizableJsonException("emport.error.invalid", "owner", owner, "");
            }
        } else {
            Integer ownerId = jsonObject.getInt("userId");
            try {
                User user;
                if(ownerId == null || (user = userService.getUser(ownerId)) == null) {
                    throw new LocalizableJsonException("emport.error.invalid", "ownerId", ownerId, "");
                }
                this.userId = user.getId();
                this.username = user.getUsername();
            } catch (LocalizableJsonException ex) {
                LOG.warn(ex.getMessage(), ex);
                throw ex;
            } catch (Exception ex) {
                LOG.warn(ex.getMessage(), ex);
                throw new LocalizableJsonException("emport.error.invalid", "ownerId", ownerId, "");
            }
        }
    }

    public static String generateXid() {
        return Common.generateXid("REP_");
    }
}
