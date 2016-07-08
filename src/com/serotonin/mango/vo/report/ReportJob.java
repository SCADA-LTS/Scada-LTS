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

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.ReportWorkItem;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTrigger;

/**
 * @author Matthew Lohbihler
 */
public class ReportJob extends TimerTask {
    private static final Map<Integer, ReportJob> JOB_REGISTRY = new HashMap<Integer, ReportJob>();

    public static void scheduleReportJob(ReportVO report) {
        synchronized (JOB_REGISTRY) {
            // Ensure that there is no existing job.
            unscheduleReportJob(report);

            if (report.isSchedule()) {
                CronTimerTrigger trigger;
                if (report.getSchedulePeriod() == ReportVO.SCHEDULE_CRON) {
                    try {
                        trigger = new CronTimerTrigger(report.getScheduleCron());
                    }
                    catch (ParseException e) {
                        throw new ShouldNeverHappenException(e);
                    }
                }
                else
                    trigger = Common.getCronTrigger(report.getSchedulePeriod(), report.getRunDelayMinutes() * 60);

                ReportJob reportJob = new ReportJob(trigger, report);
                JOB_REGISTRY.put(report.getId(), reportJob);
                Common.timer.schedule(reportJob);
            }
        }
    }

    public static void unscheduleReportJob(ReportVO report) {
        synchronized (JOB_REGISTRY) {
            ReportJob reportJob = JOB_REGISTRY.remove(report.getId());
            if (reportJob != null)
                reportJob.cancel();
        }
    }

    private final ReportVO report;

    private ReportJob(TimerTrigger trigger, ReportVO report) {
        super(trigger);
        this.report = report;
    }

    @Override
    public void run(long runtime) {
        ReportWorkItem.queueReport(report);
    }
}
