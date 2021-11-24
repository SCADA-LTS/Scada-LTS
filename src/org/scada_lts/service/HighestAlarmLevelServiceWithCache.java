package org.scada_lts.service;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.IHighestAlarmLevelDAO;
import org.scada_lts.dao.cache.HighestAlarmLevelCachable;
import org.scada_lts.dao.model.UserAlarmLevelEvent;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.quartz.CronTriggerScheduler;
import org.scada_lts.web.ws.model.AlarmLevelMessage;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

@Service
public class HighestAlarmLevelServiceWithCache implements IHighestAlarmLevelService {

    private static final Log LOG = LogFactory.getLog(HighestAlarmLevelServiceWithCache.class);

    private static final String RESET_CRON_KEY = "alarmlevel.highest.cache.reset.cron";
    private static final String RESET_ENABLED_KEY = "alarmlevel.highest.cache.reset.enabled";

    private final HighestAlarmLevelCachable highestAlarmLevelCache;
    private final IHighestAlarmLevelDAO highestAlarmLevelDAO;
    private final ReentrantReadWriteLock lock;
    private final CronTriggerScheduler resetHighestAlarmLevelScheduler;

    public HighestAlarmLevelServiceWithCache(HighestAlarmLevelCachable highestAlarmLevelCache,
                                             IHighestAlarmLevelDAO highestAlarmLevelDAO,
                                             @Qualifier("resetHighestAlarmLevelScheduler")CronTriggerScheduler resetHighestAlarmLevelScheduler) {
        this.highestAlarmLevelCache = highestAlarmLevelCache;
        this.highestAlarmLevelDAO = highestAlarmLevelDAO;
        this.lock = new ReentrantReadWriteLock();
        this.resetHighestAlarmLevelScheduler = resetHighestAlarmLevelScheduler;
    }

    @PostConstruct
    private void init() {
        try {
            boolean resetEnabled = ScadaConfig.getInstance().getBoolean(RESET_ENABLED_KEY, false);
            if(resetEnabled) {
                String cronExpression = ScadaConfig.getInstance().getProperty(RESET_CRON_KEY);
                resetHighestAlarmLevelScheduler.schedule(cronExpression);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        reload();
        LOG.info("HighestAlarmLevelServiceWithCache initialized");
    }

    @Override
    public int getAlarmLevel(User user) {
        this.lock.readLock().lock();
        try {
            UserAlarmLevelEvent alarmLevel = highestAlarmLevelCache.getAlarmLevel(user);
            return Math.max(alarmLevel.getAlarmLevel(), 0);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean doUpdateAlarmLevel(User user, UserAlarmLevelEvent alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {

        if(alarmLevel.getAlarmLevel() > highestAlarmLevelCache.getAlarmLevel(user).getAlarmLevel()) {
            this.lock.writeLock().lock();
            try {
                if(alarmLevel.getAlarmLevel() > highestAlarmLevelCache.getAlarmLevel(user).getAlarmLevel()) {
                    highestAlarmLevelCache.putAlarmLevel(user, alarmLevel);
                    send.accept(new ScadaPrincipal(user), new AlarmLevelMessage(alarmLevel.getAlarmLevel()));
                    return true;
                }
            } finally {
                this.lock.writeLock().unlock();
            }
        }
        return false;
    }

    @Override
    public boolean doSendAlarmLevel(ScadaPrincipal user, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        return doSend(user, send);
    }

    @Override
    public boolean doRemoveAlarmLevel(User user, UserAlarmLevelEvent alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        if(alarmLevel.getEventId() == highestAlarmLevelCache.getAlarmLevel(user).getEventId()
                || highestAlarmLevelCache.getAlarmLevel(user).getEventId() == Common.NEW_ID) {
            this.lock.writeLock().lock();
            try {
                if(alarmLevel.getEventId() == highestAlarmLevelCache.getAlarmLevel(user).getEventId()
                        || highestAlarmLevelCache.getAlarmLevel(user).getEventId() == Common.NEW_ID) {
                    highestAlarmLevelCache.removeAlarmLevel(user);
                    doSend(new ScadaPrincipal(user), send);
                    return true;
                }
            } finally {
                this.lock.writeLock().unlock();
            }
        }
        return false;
    }

    @Override
    public void doResetAlarmLevels(MangoUser userService, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        this.lock.writeLock().lock();
        try {
            highestAlarmLevelCache.resetAlarmLevels();
            List<UserAlarmLevelEvent> userAlarmLevels = highestAlarmLevelDAO.selectAlarmLevels();
            userAlarmLevels.forEach(a -> highestAlarmLevelCache.putAlarmLevel(User.onlyId(a.getUserId()), a));
        } finally {
            this.lock.writeLock().unlock();
        }
        for(User user: userService.getActiveUsers())
            doSend(new ScadaPrincipal(user), send);
    }

    private boolean doSend(ScadaPrincipal principal, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        this.lock.readLock().lock();
        try {
            UserAlarmLevelEvent alarmLevel = highestAlarmLevelCache.getAlarmLevel(User.onlyIdUsername(principal));
            if(alarmLevel.getAlarmLevel() > 0) {
                send.accept(principal, new AlarmLevelMessage(alarmLevel.getAlarmLevel()));
                return true;
            }
            return false;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void reload() {
        this.lock.writeLock().lock();
        try {
            List<UserAlarmLevelEvent> userAlarmLevels = highestAlarmLevelDAO.selectAlarmLevels();
            userAlarmLevels.forEach(a -> highestAlarmLevelCache.putAlarmLevel(User.onlyId(a.getUserId()), a));
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
