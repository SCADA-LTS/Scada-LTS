package org.scada_lts.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.scada_lts.web.beans.ApplicationBeans;

public class EverySecond implements StatefulJob {
    private static final ScheduleExecutor<Job> UPDATE_ITEMS_PER_SECOND = new ScheduleExecutor<>();

    public static void init() {
        ApplicationBeans.getBean("everySecondScheduler", CronTriggerScheduler.class).start();
    }

    public static void schedule(Job job) {
        UPDATE_ITEMS_PER_SECOND.schedule(job);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        UPDATE_ITEMS_PER_SECOND.execute(jobExecutionContext);
    }
}