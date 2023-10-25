package org.scada_lts.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class CronTriggerScheduler {

    private static final Log LOG = LogFactory.getLog(CronTriggerScheduler.class);
    private final CronTrigger trigger;
    private final JobDetail job;

    private final Scheduler scheduler;

    public CronTriggerScheduler(StdSchedulerFactory factory,
                                CronTrigger trigger,
                                JobDetail job) {
        this.trigger = trigger;
        this.job = job;
        try {
            this.scheduler = factory.getScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void schedule(String cronExpression) {
        try {
            trigger.setCronExpression(cronExpression);
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            LOG.info(job.getName() + " scheduled, cron: " + trigger.getCronExpression());
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public void start() {
        try {
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            LOG.info(job.getName() + " scheduled, cron: " + trigger.getCronExpression());
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public void stop() {
        try {
            if(isScheduled(scheduler, job.getName()))
                scheduler.deleteJob(job.getName(), job.getGroup());
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static boolean isScheduled(Scheduler scheduler, String jobName) throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            Trigger[] triggers = scheduler.getTriggersOfJob(jobName, groupName);
            if(triggers.length > 0)
                return true;
        }
        return false;
    }
}
