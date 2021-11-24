package org.scada_lts.service;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IHighestAlarmLevelDAO;
import org.scada_lts.dao.model.UserAlarmLevel;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.model.AlarmLevelMessage;
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
        int alarmLevel = highestAlarmLevelDAO.selectAlarmLevel(user).orElse(new UserAlarmLevel()).getAlarmLevel();
        return Math.max(alarmLevel, 0);
    }

    @Override
    public boolean doUpdateAlarmLevel(User user, UserAlarmLevel alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        try {
            send.accept(new ScadaPrincipal(user), new AlarmLevelMessage(alarmLevel.getAlarmLevel()));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean doSendAlarmLevel(ScadaPrincipal user, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        try {
            return doSend(user, send);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean doRemoveAlarmLevel(User user, UserAlarmLevel alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        try {
            doSend(new ScadaPrincipal(user), send);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void doResetAlarmLevels(MangoUser userService, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send) {
        for(User user: userService.getActiveUsers())
            doSend(new ScadaPrincipal(user), send);
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
