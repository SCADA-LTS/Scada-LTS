package org.scada_lts.web.mvc.api.dto;

import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.report.ReportPointVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportDTO {

    private int id;
    private String name;
    private List<HashMap<String, Object>> points;

    private int includeEvents;
    private boolean includeUserComments;
    private int dateRangeType;
    private int relativeDateType;
    private int previousPeriodCount;
    private int previousPeriodType;
    private int pastPeriodCount;
    private int pastPeriodType;
    private boolean fromNone;
    private int fromYear;
    private int fromMonth;
    private int fromDay;
    private int fromHour;
    private int fromMinute;
    private boolean toNone;
    private int toYear;
    private int toMonth;
    private int toDay;
    private int toHour;
    private int toMinute;
    private boolean schedule;
    private int schedulePeriod;
    private int runDelayMinutes;
    private String scheduleCron;
    private boolean email;
    private boolean includeData;
    private boolean zipData;

    public ReportDTO() {
    }

    public ReportDTO(int id, String name, List<HashMap<String, Object>> points, int includeEvents, boolean includeUserComments, int dateRangeType, int relativeDateType, int previousPeriodCount, int previousPeriodType, int pastPeriodCount, int pastPeriodType, boolean fromNone, int fromYear, int fromMonth, int fromDay, int fromHour, int fromMinute, boolean toNone, int toYear, int toMonth, int toDay, int toHour, int toMinute, boolean schedule, int schedulePeriod, int runDelayMinutes, String scheduleCron, boolean email, boolean includeData, boolean zipData) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.includeEvents = includeEvents;
        this.includeUserComments = includeUserComments;
        this.dateRangeType = dateRangeType;
        this.relativeDateType = relativeDateType;
        this.previousPeriodCount = previousPeriodCount;
        this.previousPeriodType = previousPeriodType;
        this.pastPeriodCount = pastPeriodCount;
        this.pastPeriodType = pastPeriodType;
        this.fromNone = fromNone;
        this.fromYear = fromYear;
        this.fromMonth = fromMonth;
        this.fromDay = fromDay;
        this.fromHour = fromHour;
        this.fromMinute = fromMinute;
        this.toNone = toNone;
        this.toYear = toYear;
        this.toMonth = toMonth;
        this.toDay = toDay;
        this.toHour = toHour;
        this.toMinute = toMinute;
        this.schedule = schedule;
        this.schedulePeriod = schedulePeriod;
        this.runDelayMinutes = runDelayMinutes;
        this.scheduleCron = scheduleCron;
        this.email = email;
        this.includeData = includeData;
        this.zipData = zipData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HashMap<String, Object>> getPoints() {
        return points;
    }

    public void setPoints(List<HashMap<String, Object>> points) {
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

    public int getRunDelayMinutes() {
        return runDelayMinutes;
    }

    public void setRunDelayMinutes(int runDelayMinutes) {
        this.runDelayMinutes = runDelayMinutes;
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
}
