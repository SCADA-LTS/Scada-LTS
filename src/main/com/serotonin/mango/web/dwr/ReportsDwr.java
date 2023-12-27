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

import com.serotonin.mango.Common;
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
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.adapter.MangoReport;
import org.scada_lts.mango.service.*;
import org.scada_lts.permissions.service.GetReportInstancesWithAccess;
import org.scada_lts.permissions.service.GetReportsWithAccess;

import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Matthew Lohbihler
 */
public class ReportsDwr extends BaseDwr {
    public DwrResponseI18n init() {
        DwrResponseI18n response = new DwrResponseI18n();
        MangoReport reportDao = new ReportService();
        User user = Common.getUser();

        response.addData("points", getReadablePoints());
        response.addData("mailingLists", new MailingListService().getMailingLists());
        response.addData("users", new UserService().getUsers());
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
            report = new ReportService().getReport(id);

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

        User user = Common.getUser();
        MangoReport reportDao = new ReportService();
        ReportVO report;
        if (id == Common.NEW_ID) {
            report = new ReportVO();
            report.setUserId(user.getId());
            report.setUsername(user.getUsername());
        }
        else
            report = reportDao.getReport(id);

        Permissions.ensureReportPermission(user, report);

        // Update the new values.
        report.setUserId(user.getId());
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


        DwrResponseI18n response = new DwrResponseI18n();

        // Basic validation
        report.validate(response, user);

        if (response.getHasMessages())
            return response;

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

        User user = Common.getUser();

        ReportVO report = new ReportVO();
        report.setName(name);
        report.setUserId(user.getId());
        report.setUsername(user.getUsername());
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

        DwrResponseI18n response = new DwrResponseI18n();

        // Basic validation
        report.validateRun(response, user);

        if(response.getHasMessages())
            return response;
        ReportWorkItem.queueReport(report);

        return response;
    }

    public void deleteReport(int id) {
        MangoReport reportService = new ReportService();

        ReportVO report = reportService.getReport(id);
        if (report != null) {
            GetReportsWithAccess.ensureReportOwnerPermission(Common.getUser(), report);
            ReportJob.unscheduleReportJob(report);
            reportService.deleteReport(id);
        }
    }

    public List<ReportInstance> deleteReportInstance(int instanceId) {
        User user = Common.getUser();
        MangoReport reportService = new ReportService();
        ReportInstance reportInstance = reportService.getReportInstance(instanceId);
        if(reportInstance != null) {
            GetReportInstancesWithAccess.ensureReportInstanceOwnerPermission(user, reportInstance);
            reportService.deleteReportInstance(instanceId, user.getId());
        }
        return getReportInstances(user);
    }

    public List<ReportInstance> getReportInstances() {
        return getReportInstances(Common.getUser());
    }

    private List<ReportInstance> getReportInstances(User user) {
        List<ReportInstance> result = new ReportService().getReportInstances(user.getId());
        ResourceBundle bundle = getResourceBundle();
        for (ReportInstance i : result)
            i.setBundle(bundle);
        return result;
    }

    public void setPreventPurge(int instanceId, boolean value) {
        new ReportService().setReportInstancePreventPurge(instanceId, value, Common.getUser().getId());
    }

    public ReportVO createReportFromWatchlist(int watchListId) {
        WatchListService watchListService = new WatchListService();
        WatchList watchList = watchListService.getWatchList(watchListId);
        if (watchList == null)
            return null;

        watchListService.populateWatchlistData(watchList);
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
