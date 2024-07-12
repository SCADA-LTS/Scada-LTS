package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IHighestAlarmLevelDAO;
import org.scada_lts.dao.model.UserAlarmLevel;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.ws.model.WsAlarmLevelMessage;

import java.util.function.BiConsumer;

public class HighestAlarmLevelService implements IHighestAlarmLevelService {

    private final IHighestAlarmLevelDAO highestAlarmLevelDAO;
    private final MangoUser userService;

    public HighestAlarmLevelService(IHighestAlarmLevelDAO highestAlarmLevelDAO) {
        this.highestAlarmLevelDAO = highestAlarmLevelDAO;
        this.userService = new UserService();
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
        for(User user: userService.getActiveUsers())
            doSend(user, send);
    }

    private boolean doSend(User user, BiConsumer<User, WsAlarmLevelMessage> send) {
        if(user != null) {
            send.accept(user, new WsAlarmLevelMessage(getAlarmLevel(user)));
            return true;
        }
        return false;
    }
}
