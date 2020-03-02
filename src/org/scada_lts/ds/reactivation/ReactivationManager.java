package org.scada_lts.ds.reactivation;

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.http.HttpRetrieverDataSourceVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.Key;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.ds.model.ReactivationDs;
import org.scada_lts.mango.service.DataSourceService;

import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author grzegorz.bylica@abilit.eu on 24.10.18
 */
public class ReactivationManager {

    private static final Log LOG = LogFactory.getLog(ReactivationManager.class);

    private static final long MINUTE_IN_MILISECONDS = 60_000;
    private static final long HOUR_IN_MILISECONDS = 3_600_000;
    private static final long DAY_IN_MILISECONDS = 24 * HOUR_IN_MILISECONDS;

    private static final String EVENT_MESSAGE_OF_REACTIVATION = "";

    private ConcurrentHashMap<String, Map.Entry<String, Integer>> sleepDsIndexJobName = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Key> sleepDsIndexIdDataSource = new ConcurrentHashMap<>();

    private static final ReactivationManager instance = new ReactivationManager();
    private Scheduler scheduler;

    private ReactivationManager() {
        try {
            this.scheduler = new StdSchedulerFactory().getScheduler();
            this.scheduler.start();
        } catch (SchedulerException e) {
            LOG.error(e);
        }
    }

    protected void removeInfoAboutJob(String keyNameJob) {

        Map.Entry<String,Integer> dsInfo = sleepDsIndexJobName.get(keyNameJob);
        sleepDsIndexIdDataSource.remove(dsInfo.getValue());
        sleepDsIndexJobName.remove(keyNameJob);
    }

    public static ReactivationManager getInstance() {
        return instance;
    }

    public void addProcess(StatefulJob sj, ReactivationDs rd, DataSourceVO<?> vo)  {
        LOG.info("addProcess");

        boolean isAllowEnableReactivation = false;
        try {
            isAllowEnableReactivation = ScadaConfig.getInstance().getBoolean(ScadaConfig.HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION, false);
        } catch (Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(e);
            }
        }
        if (isAllowEnableReactivation) {

            JobDetail job = new JobDetail();
            job.setName(vo.getXid());
            job.setJobClass(sj.getClass());

            AbstractMap.SimpleImmutableEntry dsInfo = new AbstractMap.SimpleImmutableEntry<>(vo.getName(), vo.getId());
            sleepDsIndexJobName.put(job.getKey().getName(), dsInfo);
            sleepDsIndexIdDataSource.put(vo.getId(), job.getKey());

            SimpleTrigger trigger = new SimpleTrigger();

            Long interval = getAdditionalMilliseconds(rd);
            Date startTime = new Date(System.currentTimeMillis() + interval);
            if (LOG.isTraceEnabled()) {
                LOG.trace(("process will be start:" + startTime));
            }
            trigger.setStartTime(startTime);
            trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);

            if (LOG.isTraceEnabled()) {
                LOG.trace("Quartz - " + sj.getClass() + " interval: " + interval);
            }

            trigger.setRepeatInterval(interval);
            trigger.setName("Quartz - trigger-" + sj.getClass() + "");

            try {
                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException e) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(e);
                }
            }
        }

    }

    public Map.Entry<String, Integer> getId(String keyNameJob) {
        return sleepDsIndexJobName.get(keyNameJob);
    }

    public long getTimeToNextFire(int idDs) {

        int ERROR = -1;
        try {
            Key key = sleepDsIndexIdDataSource.get(idDs);

            if (scheduler.isStarted() && key != null) {
                Trigger[] trigers = scheduler.getTriggersOfJob(key.getName(), key.getGroup());
                if ((trigers != null) && (trigers.length > 0)) {
                    return scheduler.getTriggersOfJob(key.getName(), key.getGroup())[0].getNextFireTime().getTime() - new Date().getTime();
                } else {
                    return ERROR;
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return ERROR;

    }

    public boolean checkDsIsSleep(int idDs) {
        boolean result = false;

        try {
            Key key = sleepDsIndexIdDataSource.get(idDs);
            if (key != null) {
                Trigger[] triggers = scheduler.getTriggersOfJob(key.getName(), key.getGroup());
                if (triggers.length > 0) {
                    result = true;
                } else {
                    sleepDsIndexIdDataSource.remove(idDs);
                }
            }
        } catch (Exception e) {
            LOG.error(e);
            result = false;
        }
        return result;

    }

    public void stopReactivation(int idDs) {
        Key key = sleepDsIndexIdDataSource.get(idDs);
        try {
            if (key != null) {
                scheduler.deleteJob(key.getName(), key.getGroup());
            }
        } catch (SchedulerException e) {
            LOG.error(e);
        } finally {
            if (key != null) {
                removeInfoAboutJob(key.getName());
            }
        }
    }

    public void startReactivation(int idDs) {
        Key key = sleepDsIndexIdDataSource.get(idDs);
        try {
            ReactivationConnectHttpRetriever rhr = new ReactivationConnectHttpRetriever();
            DataSourceService dsService = new DataSourceService();
            HttpRetrieverDataSourceVO vo = (HttpRetrieverDataSourceVO) dsService.getDataSource(idDs);
            ReactivationManager.getInstance().addProcess(rhr, vo.getReactivation(), vo);
        } catch (Exception e) {
            LOG.error(e);
        }
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
