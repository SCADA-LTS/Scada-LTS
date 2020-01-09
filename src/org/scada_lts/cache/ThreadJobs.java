package org.scada_lts.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.quartz.UpdateEventDetectors;

import java.io.IOException;
import java.util.Date;

class ThreadJobs {

    private static final Log LOG = LogFactory.getLog(PointEventDetectorsCache.class);

    ThreadJobs() {
        JobDetail job = new JobDetail();
        job.setName("UpdateEventDetectors");
        job.setJobClass(UpdateEventDetectors.class);

        SimpleTrigger trigger = new SimpleTrigger();

        Date startTime = null;
        try {
            startTime = new Date(System.currentTimeMillis()
                    + ScadaConfig.getInstance().getLong(ScadaConfig.START_UPDATE_EVENT_DETECTORS, 10_000_000));
        }
        catch (IOException ioException) {
            LOG.info(ioException.getMessage());
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Quartz - startTime:" + startTime);
        }

        trigger.setStartTime(startTime);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);

        Long interval = null;
        try {
            interval = ScadaConfig.getInstance().getLong(ScadaConfig.MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS, 5_000_000);
        }
        catch (IOException ioException) {
            LOG.info(ioException.getMessage());
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Quartz - interval:" + interval);
        }
        trigger.setRepeatInterval(interval);
        trigger.setName("Quartz - trigger-UpdateEventDetectors");


        Scheduler scheduler = null;
        try {

            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

        }
        catch (SchedulerException schedulerException) {
            LOG.info(schedulerException.getMessage());
        }
    }
}
