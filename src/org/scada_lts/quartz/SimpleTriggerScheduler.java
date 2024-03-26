package org.scada_lts.quartz;

import org.quartz.*;

import java.util.Date;

public class SimpleTriggerScheduler {

    private final SimpleTrigger trigger;
    private final JobDetail job;
    private final ScadaScheduler scheduler;

    public SimpleTriggerScheduler(ScadaScheduler scheduler,
                                  SimpleTrigger trigger,
                                  JobDetail job) {
        this.trigger = trigger;
        this.job = job;
        this.scheduler = scheduler;
    }

    public void schedule(Date startTime, long interval) {
        trigger.setStartTime(startTime);
        trigger.setRepeatInterval(interval);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        scheduler.schedule(job, trigger);
    }

    public void start() {
        scheduler.start(job, trigger);
    }

    public void stop() {
        scheduler.stop(job.getKey());
    }
}
