package org.scada_lts.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;

public class CronTriggerScheduler {

    private static final Log LOG = LogFactory.getLog(CronTriggerScheduler.class);
    private final CronTrigger trigger;
    private final JobDetail job;
    private final ScadaScheduler scheduler;

    public CronTriggerScheduler(ScadaScheduler scadaScheduler,
                                CronTrigger trigger,
                                JobDetail job) {
        this.trigger = trigger;
        this.job = job;
        this.scheduler = scadaScheduler;
    }

    public void schedule(String cronExpression) {
        try {
            trigger.setCronExpression(cronExpression);
            scheduler.schedule(job, trigger);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void start() {
        scheduler.start(job, trigger);
    }

    public void stop() {
        scheduler.stop(job.getKey());
    }
}
