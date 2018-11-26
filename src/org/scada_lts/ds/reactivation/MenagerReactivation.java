package org.scada_lts.ds.reactivation;

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.Key;
import org.scada_lts.ds.model.ReactivationDs;

import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @autor grzegorz.bylica@gmail.com on 24.10.18
 */
public class MenagerReactivation {

    private static final Log LOG = LogFactory.getLog(MenagerReactivation.class);

    private static final long MINUTE_IN_MILISECONDS = 60_000;
    private static final long HOUR_IN_MILISECONDS = 3_600_000;
    private static final long DAY_IN_MILISECONDS = 24 * HOUR_IN_MILISECONDS;

    private static final String EVENT_MESSAGE_OF_REACTIVATION = "";

    private ConcurrentHashMap<String, Map.Entry<String, Integer>> sleepDsIndexJobName = new ConcurrentHashMap<String, Map.Entry<String, Integer>>();
    private ConcurrentHashMap<Integer, Key> sleepDsIndexIdDs = new ConcurrentHashMap<>();

    private static final MenagerReactivation instance = new MenagerReactivation();
    private Scheduler scheduler;

    public MenagerReactivation() {
        try {
            this.scheduler = new StdSchedulerFactory().getScheduler();
            this.scheduler.start();
        } catch (SchedulerException e) {
            LOG.error(e);
        }
    }

    public static MenagerReactivation getInstance() {
        return instance;
    }

    public void addProcess(StatefulJob sj, ReactivationDs rd, DataSourceVO<?> vo)  {
        LOG.info("addProcess");

        JobDetail job = new JobDetail();
        job.setName(sj.toString());
        job.setJobClass(sj.getClass());

        AbstractMap.SimpleImmutableEntry dsInfo = new AbstractMap.SimpleImmutableEntry<>(vo.getName(), vo.getId());
        sleepDsIndexJobName.put(job.getKey().getName(), dsInfo);
        sleepDsIndexIdDs.put(vo.getId(), job.getKey());

        SimpleTrigger trigger = new SimpleTrigger();

        Long interval = getAdditionalMilliseconds(rd);
        Date startTime = new Date(System.currentTimeMillis() + interval );
        LOG.info("process will be start:" + startTime);
        trigger.setStartTime(startTime);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);

        LOG.trace("Quartz - "+sj.getClass()+ " interval: " + interval);
        trigger.setRepeatInterval(interval);

        trigger.setName("Quartz - trigger-"+sj.getClass()+"");

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOG.error(e);
        }

    }

    public Map.Entry<String, Integer> getId(String keyNameJob) {
        return sleepDsIndexJobName.get(keyNameJob);
    }

    public void removeInfoAboutJob(String keyNameJob) {

        Map.Entry<String,Integer> dsInfo = sleepDsIndexJobName.get(keyNameJob);
        sleepDsIndexIdDs.remove(dsInfo.getValue());
        sleepDsIndexJobName.remove(keyNameJob);
    }

    public long getTimeToNextFire(int idDs) {

        int ERROR = -1;
        try {
            Key key = sleepDsIndexIdDs.get(idDs);
            return scheduler.getTriggersOfJob(key.getName(), key.getGroup())[0].getNextFireTime().getTime() - new Date().getTime();
        } catch (SchedulerException e) {
            LOG.error(e);
        }
        return ERROR;

    }

    private long getAdditionalMilliseconds(ReactivationDs r) {
        if (r.getType() == ReactivationDs.TYPE_OF_SLEEP_MINUTE) {
            return r.getValue() * MINUTE_IN_MILISECONDS;
        } else if (r.getType() == ReactivationDs.TYPE_OF_SLEEP_HOUR) {
            return r.getValue() * HOUR_IN_MILISECONDS;
        } else if (r.getType() == ReactivationDs.TYPE_OF_SLEEP_DAY){
            return r.getValue() * DAY_IN_MILISECONDS;
        }
        return 0;
    }
}
