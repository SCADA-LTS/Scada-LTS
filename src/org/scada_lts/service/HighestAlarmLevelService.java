package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IHighestAlarmLevelDAO;
import org.scada_lts.dao.model.UserAlarmLevel;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.ws.model.WsAlarmLevelMessage;

import java.util.function.BiConsumer;

public class HighestAlarmLevelService implements IHighestAlarmLevelService {

    private final IHighestAlarmLevelDAO highestAlarmLevelDAO;

    public HighestAlarmLevelService(IHighestAlarmLevelDAO highestAlarmLevelDAO) {
        this.highestAlarmLevelDAO = highestAlarmLevelDAO;
    }

    @Override
    public int getAlarmLevel(User user) {
        int alarmLevel = highestAlarmLevelDAO.selectAlarmLevel(user).orElse(UserAlarmLevel.onlyUser(user)).getAlarmLevel();
        return Math.max(alarmLevel, 0);
    }

    @Override
    public boolean doUpdateAlarmLevel(User user, EventInstance event, BiConsumer<User, WsAlarmLevelMessage> send) {
        try {
            send.accept(user, new WsAlarmLevelMessage(event.getAlarmLevel()));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean doSendAlarmLevel(User user, BiConsumer<User, WsAlarmLevelMessage> send) {
        try {
            return doSend(user, send);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean doRemoveAlarmLevel(User user, EventInstance event, BiConsumer<User, WsAlarmLevelMessage> send) {
        try {
            doSend(user, send);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void doResetAlarmLevels(BiConsumer<User, WsAlarmLevelMessage> send) {
        ApplicationBeans.Lazy.getLoggedUsersBean().ifPresent(loggedUsers -> {
            for(User user: loggedUsers.getUsers())
                doSend(user, send);
        });
    }

    private boolean doSend(User user, BiConsumer<User, WsAlarmLevelMessage> send) {
        if(user != null) {
            send.accept(user, new WsAlarmLevelMessage(getAlarmLevel(user)));
            return true;
        }
        return false;
    }
}
