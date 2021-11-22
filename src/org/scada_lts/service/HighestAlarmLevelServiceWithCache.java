package org.scada_lts.service;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IHighestAlarmLevelDAO;
import org.scada_lts.dao.cache.HighestAlarmLevelCachable;
import org.scada_lts.dao.model.UserAlarmLevel;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.web.ws.model.AlarmLevelMessage;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.services.UserEventServiceWebsocket;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

@Service
public class HighestAlarmLevelServiceWithCache implements IHighestAlarmLevelService {

    private final HighestAlarmLevelCachable highestAlarmLevelCache;
    private final IHighestAlarmLevelDAO highestAlarmLevelDAO;
    private final ReentrantReadWriteLock lock;
    private volatile boolean initiated;

    public HighestAlarmLevelServiceWithCache(HighestAlarmLevelCachable highestAlarmLevelCache,
                                             IHighestAlarmLevelDAO highestAlarmLevelDAO) {
        this.highestAlarmLevelCache = highestAlarmLevelCache;
        this.highestAlarmLevelDAO = highestAlarmLevelDAO;
        this.lock = new ReentrantReadWriteLock();
        this.initiated = false;
    }

    public void init() {
        if(!initiated) {
            this.lock.writeLock().lock();
            try {
                List<UserAlarmLevel> userAlarmLevels = highestAlarmLevelDAO.selectAlarmLevels();
                userAlarmLevels.forEach(a -> highestAlarmLevelCache.putAlarmLevel(User.onlyId(a.getUserId()), a.getAlarmLevel()));
                this.initiated = true;
            } finally {
                this.lock.writeLock().unlock();
            }
        }
    }

    @Override
    public int getAlarmLevel(User user) {
        this.lock.readLock().lock();
        try {
            int alarmLevel = highestAlarmLevelCache.getAlarmLevel(user);
            return Math.max(alarmLevel, 0);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean doUpdateAlarmLevel(User user, int alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        if(alarmLevel > highestAlarmLevelCache.getAlarmLevel(user)) {
            this.lock.writeLock().lock();
            try {
                if(alarmLevel > highestAlarmLevelCache.getAlarmLevel(user)) {
                    highestAlarmLevelCache.putAlarmLevel(user, alarmLevel);
                    send.accept(new ScadaPrincipal(user), new AlarmLevelMessage(alarmLevel));
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
    public boolean doRemoveAlarmLevel(User user, int alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        if(alarmLevel <= 0) {
            highestAlarmLevelCache.removeAlarmLevel(user);
            doSend(new ScadaPrincipal(user), send);
            return true;
        }
        if(alarmLevel == highestAlarmLevelCache.getAlarmLevel(user)) {
            this.lock.writeLock().lock();
            try {
                if(alarmLevel == highestAlarmLevelCache.getAlarmLevel(user)) {
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
    public void doResetAlarmLevel(MangoUser userService, UserEventServiceWebsocket userEventServiceWebsocket) {
        this.lock.writeLock().lock();
        try {
            highestAlarmLevelCache.resetAlarmLevel();
            List<UserAlarmLevel> userAlarmLevels = highestAlarmLevelDAO.selectAlarmLevels();
            userAlarmLevels.forEach(a -> highestAlarmLevelCache.putAlarmLevel(User.onlyId(a.getUserId()), a.getAlarmLevel()));
        } finally {
            this.lock.writeLock().unlock();
        }
        for(User user: userService.getActiveUsers()) {
            doSend(new ScadaPrincipal(user), userEventServiceWebsocket::sendAlarmLevel);
            userEventServiceWebsocket.sendEventUpdate(new ScadaPrincipal(user));
        }
    }

    private boolean doSend(ScadaPrincipal principal, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        if(principal != null) {
            this.lock.readLock().lock();
            try {
                int alarmLevel = highestAlarmLevelCache.getAlarmLevel(User.onlyIdUsername(principal));
                send.accept(principal, new AlarmLevelMessage(alarmLevel));
                return true;
            } finally {
                this.lock.readLock().unlock();
            }
        }
        return false;
    }
}
