/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2009 Serotonin Software Technologies Inc.
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
package com.serotonin.mango.util;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * A Quartz job that, when executed, will call the setEventActive method on the TimeDelayedEventProducer target.
 * 
 * @author Matthew Lohbihler
 */
public class TimeoutJob implements Job {
    private static final String TARGET_KEY = "TimeoutClient";
    private static final String TARGET_MODEL = "TargetModel";

    public static JobDetail createJobDetail(TimeoutClient timeoutClient, String jobName, String jobGroup, Object model) {
        JobDetail jobDetail = new JobDetail(jobName, jobGroup, TimeoutJob.class);
        jobDetail.getJobDataMap().put(TARGET_KEY, timeoutClient);
        if (model != null)
            jobDetail.getJobDataMap().put(TARGET_MODEL, model);
        return jobDetail;
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Object model = jobDataMap.get(TARGET_MODEL);
        ((TimeoutClient) jobDataMap.get(TARGET_KEY)).scheduleTimeout(model, context.getFireTime().getTime());
    }
}
