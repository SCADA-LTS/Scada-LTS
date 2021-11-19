package org.scada_lts.service;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.cache.HighestAlarmLevelCachable;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.utils.ApplicationBeans;
import org.scada_lts.web.ws.model.AlarmLevelMessage;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.services.UserEventServiceWebsocket;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

@Service
public class HighestAlarmLevelServiceWithCache implements HighestAlarmLevelService {

    private final HighestAlarmLevelCachable highestAlarmLevelCache;
    private final ReentrantReadWriteLock lock;

    public HighestAlarmLevelServiceWithCache(HighestAlarmLevelCachable highestAlarmLevelCache) {
        this.highestAlarmLevelCache = highestAlarmLevelCache;
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public int getAlarmLevel(User user) {
        int alarmLevel = highestAlarmLevelCache.getAlarmLevel(user);
        return Math.max(alarmLevel, 0);
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
            this.lock.readLock().lock();
            try {
                if(alarmLevel == highestAlarmLevelCache.getAlarmLevel(user)) {
                    highestAlarmLevelCache.removeAlarmLevel(user);
                    doSend(new ScadaPrincipal(user), send);
                    return true;
                }
            } finally {
                this.lock.readLock().unlock();
            }
        }
        return false;
    }

    @Override
    public void doResetAlarmLevel(MangoUser userService, UserEventServiceWebsocket userEventServiceWebsocket) {
        for(User user: userService.getActiveUsers()) {
            doRemoveAlarmLevel(user, AlarmLevels.NONE, userEventServiceWebsocket::sendAlarmLevel);
            userEventServiceWebsocket.sendEventUpdate(new ScadaPrincipal(user));
        }
    }

    private boolean doSend(ScadaPrincipal principal, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        if(principal != null) {
            int alarmLevel = highestAlarmLevelCache.getAlarmLevel(User.onlyIdUsername(principal));
            if (alarmLevel > -1) {
                send.accept(principal, new AlarmLevelMessage(alarmLevel));
                return true;
            }
        }
        return false;
    }
}
