package org.scada_lts.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.utils.Key;

import java.util.Date;

public class ScadaScheduler {

    private static final Log LOG = LogFactory.getLog(ScadaScheduler.class);
    private final Scheduler scheduler;

    public ScadaScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void init() {
        try {
            scheduler.start();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void destroy() {
        try {
            for(String group: scheduler.getJobGroupNames()) {
                for(String job: scheduler.getJobNames(group)) {
                    stop(new Key(job, group));
                }
            }
            scheduler.shutdown(false);
            Thread.sleep(1000);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void schedule(JobDetail job, Trigger trigger) {
        if(!isStarted(scheduler))
            return;
        stop(job.getKey());
        start(job, trigger);
    }

    public void start(JobDetail job, Trigger trigger) {
        if(!isStarted(scheduler))
            return;
        if(!isScheduled(scheduler, job.getName(), job.getGroup()))
            doSchedule(job, trigger);
    }

    public void stop(Key key) {
        if(!isStarted(scheduler))
            return;
        if(isScheduled(scheduler, key.getName(), key.getGroup()))
            doDelete(key);
    }

    public long getTimeToNextFire(Key key) {
        Trigger trigger = getFirstTrigger(scheduler, key.getName(), key.getGroup());
        if (trigger == null) {
            return -1;
        }
        return trigger.getNextFireTime().getTime() - new Date().getTime();
    }

    public boolean checkFire(Key key) {
        return isScheduled(scheduler, key.getName(), key.getGroup());
    }

    public boolean isStarted() {
        return isStarted(scheduler);
    }

    private static boolean isScheduled(Scheduler scheduler, String jobName, String groupName) {
        return getFirstTrigger(scheduler, jobName, groupName) != null;
    }

    private static Trigger getFirstTrigger(Scheduler scheduler, String jobName, String groupName) {
        try {
            Trigger[] triggers = scheduler.getTriggersOfJob(jobName, groupName);
            if (isTrigger(triggers)) {
                return triggers[0];
            }
            return null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    private static boolean isStarted(Scheduler scheduler) {
        try {
            if (scheduler.isStarted()) {
                return true;
            } else {
                LOG.error("scheduler: " + scheduler.getSchedulerName() + ", is not started!");
                return false;
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean isTrigger(Trigger[] triggers) {
        return triggers != null && triggers.length > 0;
    }

    private void doSchedule(JobDetail job, Trigger trigger) {
        try {
            scheduler.scheduleJob(job, trigger);
            LOG.info(job.getName() + " scheduled, " + trigger);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void doDelete(Key key) {
        try {
            scheduler.deleteJob(key.getName(), key.getGroup());
            LOG.info(key.getName() + " stopped, " + key);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
