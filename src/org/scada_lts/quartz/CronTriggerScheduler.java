package org.scada_lts.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

public class CronTriggerScheduler {

    private static final Log LOG = LogFactory.getLog(CronTriggerScheduler.class);

    private final StdSchedulerFactory factory;
    private final CronTrigger trigger;
    private final JobDetail job;

    public CronTriggerScheduler(StdSchedulerFactory factory,
                                CronTrigger trigger,
                                JobDetail job) {
        this.factory = factory;
        this.trigger = trigger;
        this.job = job;
    }

    public void schedule(String cronExpression) {
        try {
            trigger.setCronExpression(cronExpression);
            Scheduler scheduler = factory.getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            LOG.info(job.getName() + " scheduled, cron: " + trigger.getCronExpression());
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }
}
