package org.scada_lts.service;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.IHighestAlarmLevelDAO;
import org.scada_lts.dao.cache.HighestAlarmLevelCachable;
import org.scada_lts.dao.model.UserAlarmLevel;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.quartz.CronTriggerScheduler;
import org.scada_lts.web.ws.model.AlarmLevelMessage;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

public class HighestAlarmLevelServiceWithCache implements IHighestAlarmLevelService {

    private static final Log LOG = LogFactory.getLog(HighestAlarmLevelServiceWithCache.class);

    private static final String RESET_CRON_KEY = "alarmlevel.highest.cache.reset.cron";
    private static final String RESET_ENABLED_KEY = "alarmlevel.highest.cache.reset.enabled";

    private final HighestAlarmLevelCachable highestAlarmLevelCache;
    private final IHighestAlarmLevelDAO highestAlarmLevelDAO;
    private final ReentrantReadWriteLock lock;
    private final CronTriggerScheduler resetHighestAlarmLevelScheduler;
    private final MangoUser userService;

    public HighestAlarmLevelServiceWithCache(HighestAlarmLevelCachable highestAlarmLevelCache,
                                             IHighestAlarmLevelDAO highestAlarmLevelDAO,
                                             CronTriggerScheduler resetHighestAlarmLevelScheduler) {
        this.highestAlarmLevelCache = highestAlarmLevelCache;
        this.highestAlarmLevelDAO = highestAlarmLevelDAO;
        this.lock = new ReentrantReadWriteLock();
        this.resetHighestAlarmLevelScheduler = resetHighestAlarmLevelScheduler;
        this.userService = new UserService();
    }

    public void init() {
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
        LOG.info(HighestAlarmLevelServiceWithCache.class.getSimpleName() + " initialized");
    }

    @Override
    public int getAlarmLevel(User user) {
        this.lock.readLock().lock();
        try {
            UserAlarmLevel alarmLevel = highestAlarmLevelCache.getAlarmLevel(user);
            return Math.max(alarmLevel.getAlarmLevel(), 0);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean doUpdateAlarmLevel(User user, EventInstance event, BiConsumer<User, AlarmLevelMessage> send) {
        this.lock.writeLock().lock();
        try {
            UserAlarmLevel userAlarmLevel = highestAlarmLevelCache.getAlarmLevel(user);
            if(event.getAlarmLevel() > userAlarmLevel.getAlarmLevel()) {
                highestAlarmLevelCache.putAlarmLevel(user, UserAlarmLevel.fromEvent(user, event));
                send.accept(user, AlarmLevelMessage.alarmLevelFromEvent(event));
                return true;
            }
            return false;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public boolean doSendAlarmLevel(User user, BiConsumer<User, AlarmLevelMessage> send) {
        return doSend(user, send);
    }

    @Override
    public boolean doRemoveAlarmLevel(User user, EventInstance event, BiConsumer<User, AlarmLevelMessage> send) {
        this.lock.writeLock().lock();
        try {
            UserAlarmLevel userAlarmLevel = highestAlarmLevelCache.getAlarmLevel(user);
            if (event.getAlarmLevel() == userAlarmLevel.getAlarmLevel()) {
                highestAlarmLevelCache.removeAlarmLevel(user);
                send.accept(user, highestAlarmLevelCache.getAlarmLevel(user).toAlarmLevelMessage());
                return true;
            }
            return false;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void doResetAlarmLevels(BiConsumer<User, AlarmLevelMessage> send) {
        this.lock.writeLock().lock();
        try {
            highestAlarmLevelCache.resetAlarmLevels();
        } finally {
            this.lock.writeLock().unlock();
        }
        for(User user: userService.getActiveUsers())
            doSend(user, send);
    }

    private boolean doSend(User user, BiConsumer<User, AlarmLevelMessage> send) {
        this.lock.readLock().lock();
        try {
            UserAlarmLevel alarmLevel = highestAlarmLevelCache.getAlarmLevel(user);
            if(alarmLevel.getAlarmLevel() >= AlarmLevels.NONE) {
                send.accept(user, alarmLevel.toAlarmLevelMessage());
                return true;
            }
            return false;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void reload() {
        List<UserAlarmLevel> userAlarmLevels = highestAlarmLevelDAO.selectAlarmLevels();
        userAlarmLevels.forEach(userAlarmLevel -> {
            User user = userService.getUser(userAlarmLevel.getUserId());
            highestAlarmLevelCache.putAlarmLevel(user, userAlarmLevel);
        });
        LOG.info(HighestAlarmLevelServiceWithCache.class.getSimpleName() + " reloaded");
    }
}
