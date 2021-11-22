package org.scada_lts.service;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IHighestAlarmLevelDAO;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.model.AlarmLevelMessage;
import org.scada_lts.web.ws.services.UserEventServiceWebsocket;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Service
public class HighestAlarmLevelService implements IHighestAlarmLevelService {

    private final IHighestAlarmLevelDAO highestAlarmLevelDAO;

    public HighestAlarmLevelService(IHighestAlarmLevelDAO highestAlarmLevelDAO) {
        this.highestAlarmLevelDAO = highestAlarmLevelDAO;
    }

    @Override
    public int getAlarmLevel(User user) {
        int alarmLevel = highestAlarmLevelDAO.selectAlarmLevel(user).getAlarmLevel();
        return Math.max(alarmLevel, 0);
    }

    @Override
    public boolean doUpdateAlarmLevel(User user, int alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        send.accept(new ScadaPrincipal(user), new AlarmLevelMessage(alarmLevel));
        return true;
    }

    @Override
    public boolean doSendAlarmLevel(ScadaPrincipal user, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        return doSend(user, send);
    }

    @Override
    public boolean doRemoveAlarmLevel(User user, int alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        doSend(new ScadaPrincipal(user), send);
        return true;
    }

    @Override
    public void doResetAlarmLevel(MangoUser userService, UserEventServiceWebsocket userEventServiceWebsocket) {
        for(User user: userService.getActiveUsers()) {
            doSend(new ScadaPrincipal(user), userEventServiceWebsocket::sendAlarmLevel);
            userEventServiceWebsocket.sendEventUpdate(new ScadaPrincipal(user));
        }
    }

    private boolean doSend(ScadaPrincipal principal, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        if(principal != null) {
            User user = User.onlyIdUsername(principal);
            send.accept(principal, new AlarmLevelMessage(getAlarmLevel(user)));
            return true;
        }
        return false;
    }
}
