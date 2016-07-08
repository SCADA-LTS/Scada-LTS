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
package com.serotonin.mango.web.dwr;

import java.util.List;
import java.util.ResourceBundle;

import com.serotonin.InvalidArgumentException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.MailingListDao;
import com.serotonin.mango.db.dao.ReportDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.rt.maint.work.ReportWorkItem;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportJob;
import com.serotonin.mango.vo.report.ReportPointVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.util.ColorUtils;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class ReportsDwr extends BaseDwr {
    public DwrResponseI18n init() {
        DwrResponseI18n response = new DwrResponseI18n();
        ReportDao reportDao = new ReportDao();
        User user = Common.getUser();

        response.addData("points", getReadablePoints());
        response.addData("mailingLists", new MailingListDao().getMailingLists());
        response.addData("users", new UserDao().getUsers());
        response.addData("reports", reportDao.getReports(user.getId()));
        response.addData("instances", getReportInstances(user));

        return response;
    }

    public ReportVO getReport(int id, boolean copy) {
        ReportVO report;
        if (id == Common.NEW_ID) {
            report = new ReportVO();
            report.setName(getMessage("common.newName"));
        }
        else {
            report = new ReportDao().getReport(id);

            if (copy) {
                report.setId(Common.NEW_ID);
                report.setName(LocalizableMessage.getMessage(getResourceBundle(), "common.copyPrefix", report.getName()));
            }

            Permissions.ensureReportPermission(Common.getUser(), report);
        }
        return report;
    }

    public DwrResponseI18n saveReport(int id, String name, List<ReportPointVO> points, int includeEvents,
            boolean includeUserComments, int dateRangeType, int relativeDateType, int previousPeriodCount,
            int previousPeriodType, int pastPeriodCount, int pastPeriodType, boolean fromNone, int fromYear,
            int fromMonth, int fromDay, int fromHour, int fromMinute, boolean toNone, int toYear, int toMonth,
            int toDay, int toHour, int toMinute, boolean schedule, int schedulePeriod, int runDelayMinutes,
            String scheduleCron, boolean email, boolean includeData, boolean zipData,
            List<RecipientListEntryBean> recipients) {

        DwrResponseI18n response = new DwrResponseI18n();

        // Basic validation
        validateData(response, name, points, dateRangeType, relativeDateType, previousPeriodCount, pastPeriodCount);

        if (schedule) {
            if (schedulePeriod == ReportVO.SCHEDULE_CRON) {
                // Check the cron pattern.
                try {
                    new CronTimerTrigger(scheduleCron);
                }
                catch (Exception e) {
                    response.addContextualMessage("scheduleCron", "reports.validate.cron", e.getMessage());
                }
            }
            else {
                if (runDelayMinutes < 0)
                    response.addContextualMessage("runDelayMinutes", "reports.validate.lessThan0");
                else if (runDelayMinutes > 59)
                    response.addContextualMessage("runDelayMinutes", "reports.validate.greaterThan59");
            }
        }

        if (schedule && email && recipients.isEmpty())
            response.addContextualMessage("recipients", "reports.validate.needRecip");

        if (response.getHasMessages())
            return response;

        User user = Common.getUser();
        ReportDao reportDao = new ReportDao();
        ReportVO report;
        if (id == Common.NEW_ID) {
            report = new ReportVO();
            report.setUserId(user.getId());
        }
        else
            report = reportDao.getReport(id);

        Permissions.ensureReportPermission(user, report);

        // Update the new values.
        report.setName(name);
        report.setPoints(points);
        report.setIncludeEvents(includeEvents);
        report.setIncludeUserComments(includeUserComments);
        report.setDateRangeType(dateRangeType);
        report.setRelativeDateType(relativeDateType);
        report.setPreviousPeriodCount(previousPeriodCount);
        report.setPreviousPeriodType(previousPeriodType);
        report.setPastPeriodCount(pastPeriodCount);
        report.setPastPeriodType(pastPeriodType);
        report.setFromNone(fromNone);
        report.setFromYear(fromYear);
        report.setFromMonth(fromMonth);
        report.setFromDay(fromDay);
        report.setFromHour(fromHour);
        report.setFromMinute(fromMinute);
        report.setToNone(toNone);
        report.setToYear(toYear);
        report.setToMonth(toMonth);
        report.setToDay(toDay);
        report.setToHour(toHour);
        report.setToMinute(toMinute);
        report.setSchedule(schedule);
        report.setSchedulePeriod(schedulePeriod);
        report.setRunDelayMinutes(runDelayMinutes);
        report.setScheduleCron(scheduleCron);
        report.setEmail(email);
        report.setIncludeData(includeData);
        report.setZipData(zipData);
        report.setRecipients(recipients);

        // Save the report
        reportDao.saveReport(report);

        // Conditionally schedule the report.
        ReportJob.scheduleReportJob(report);

        // Send back the report id in case this was new.
        response.addData("reportId", report.getId());
        return response;
    }

    public DwrResponseI18n runReport(String name, List<ReportPointVO> points, int includeEvents,
            boolean includeUserComments, int dateRangeType, int relativeDateType, int previousPeriodCount,
            int previousPeriodType, int pastPeriodCount, int pastPeriodType, boolean fromNone, int fromYear,
            int fromMonth, int fromDay, int fromHour, int fromMinute, boolean toNone, int toYear, int toMonth,
            int toDay, int toHour, int toMinute, boolean email, boolean includeData, boolean zipData,
            List<RecipientListEntryBean> recipients) {
        DwrResponseI18n response = new DwrResponseI18n();

        // Basic validation
        validateData(response, name, points, dateRangeType, relativeDateType, previousPeriodCount, pastPeriodCount);

        if (!response.getHasMessages()) {
            ReportVO report = new ReportVO();
            report.setName(name);
            report.setUserId(Common.getUser().getId());
            report.setPoints(points);
            report.setIncludeEvents(includeEvents);
            report.setIncludeUserComments(includeUserComments);
            report.setDateRangeType(dateRangeType);
            report.setRelativeDateType(relativeDateType);
            report.setPreviousPeriodCount(previousPeriodCount);
            report.setPreviousPeriodType(previousPeriodType);
            report.setPastPeriodCount(pastPeriodCount);
            report.setPastPeriodType(pastPeriodType);
            report.setFromNone(fromNone);
            report.setFromYear(fromYear);
            report.setFromMonth(fromMonth);
            report.setFromDay(fromDay);
            report.setFromHour(fromHour);
            report.setFromMinute(fromMinute);
            report.setToNone(toNone);
            report.setToYear(toYear);
            report.setToMonth(toMonth);
            report.setToDay(toDay);
            report.setToHour(toHour);
            report.setToMinute(toMinute);
            report.setEmail(email);
            report.setIncludeData(includeData);
            report.setZipData(zipData);
            report.setRecipients(recipients);

            ReportWorkItem.queueReport(report);
        }

        return response;
    }

    public void deleteReport(int id) {
        ReportDao reportDao = new ReportDao();

        ReportVO report = reportDao.getReport(id);
        if (report != null) {
            Permissions.ensureReportPermission(Common.getUser(), report);
            ReportJob.unscheduleReportJob(report);
            reportDao.deleteReport(id);
        }
    }

    private void validateData(DwrResponseI18n response, String name, List<ReportPointVO> points, int dateRangeType,
            int relativeDateType, int previousPeriodCount, int pastPeriodCount) {
        if (StringUtils.isEmpty(name))
            response.addContextualMessage("name", "reports.validate.required");
        if (StringUtils.isLengthGreaterThan(name, 100))
            response.addContextualMessage("name", "reports.validate.longerThan100");
        if (points.isEmpty())
            response.addContextualMessage("points", "reports.validate.needPoint");
        if (dateRangeType != ReportVO.DATE_RANGE_TYPE_RELATIVE && dateRangeType != ReportVO.DATE_RANGE_TYPE_SPECIFIC)
            response.addGenericMessage("reports.validate.invalidDateRangeType");
        if (relativeDateType != ReportVO.RELATIVE_DATE_TYPE_PAST
                && relativeDateType != ReportVO.RELATIVE_DATE_TYPE_PREVIOUS)
            response.addGenericMessage("reports.validate.invalidRelativeDateType");
        if (previousPeriodCount < 1)
            response.addContextualMessage("previousPeriodCount", "reports.validate.periodCountLessThan1");
        if (pastPeriodCount < 1)
            response.addContextualMessage("pastPeriodCount", "reports.validate.periodCountLessThan1");

        User user = Common.getUser();
        DataPointDao dataPointDao = new DataPointDao();
        for (ReportPointVO point : points) {
            Permissions.ensureDataPointReadPermission(user, dataPointDao.getDataPoint(point.getPointId()));

            try {
                if (!StringUtils.isEmpty(point.getColour()))
                    ColorUtils.toColor(point.getColour());
            }
            catch (InvalidArgumentException e) {
                response.addContextualMessage("points", "reports.validate.colour", point.getColour());
            }
        }
    }

    public List<ReportInstance> deleteReportInstance(int instanceId) {
        User user = Common.getUser();
        ReportDao reportDao = new ReportDao();
        reportDao.deleteReportInstance(instanceId, user.getId());
        return getReportInstances(user);
    }

    public List<ReportInstance> getReportInstances() {
        return getReportInstances(Common.getUser());
    }

    private List<ReportInstance> getReportInstances(User user) {
        List<ReportInstance> result = new ReportDao().getReportInstances(user.getId());
        ResourceBundle bundle = getResourceBundle();
        for (ReportInstance i : result)
            i.setBundle(bundle);
        return result;
    }

    public void setPreventPurge(int instanceId, boolean value) {
        new ReportDao().setReportInstancePreventPurge(instanceId, value, Common.getUser().getId());
    }

    public ReportVO createReportFromWatchlist(int watchListId) {
        WatchList watchList = new WatchListDao().getWatchList(watchListId);
        if (watchList == null)
            return null;

        ReportVO report = new ReportVO();
        report.setName(LocalizableMessage.getMessage(getResourceBundle(), "common.copyPrefix", watchList.getName()));
        for (DataPointVO dp : watchList.getPointList()) {
            ReportPointVO rp = new ReportPointVO();
            rp.setPointId(dp.getId());
            rp.setColour(dp.getChartColour());
            rp.setConsolidatedChart(true);
            report.getPoints().add(rp);
        }

        return report;
    }
}
