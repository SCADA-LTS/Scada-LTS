package org.scada_lts.service;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.model.UserAlarmLevelEvent;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.web.ws.model.AlarmLevelMessage;
import org.scada_lts.web.ws.beans.ScadaPrincipal;

import java.util.function.BiConsumer;

public interface IHighestAlarmLevelService {
    int getAlarmLevel(User user);
    boolean doUpdateAlarmLevel(User user, UserAlarmLevelEvent alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send);
    boolean doSendAlarmLevel(ScadaPrincipal user, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send);
    boolean doRemoveAlarmLevel(User user, UserAlarmLevelEvent alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send);
    void doResetAlarmLevels(MangoUser userService, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send);
}
