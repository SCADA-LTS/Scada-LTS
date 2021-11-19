package org.scada_lts.service;

import com.serotonin.mango.vo.User;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.web.ws.model.AlarmLevelMessage;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.services.UserEventServiceWebsocket;

import java.util.function.BiConsumer;

public interface HighestAlarmLevelService {
    int getAlarmLevel(User user);
    boolean doUpdateAlarmLevel(User user, int alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send);
    boolean doSendAlarmLevel(ScadaPrincipal user, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send);
    boolean doRemoveAlarmLevel(User user, int alarmLevel, BiConsumer<ScadaPrincipal, AlarmLevelMessage> send);
    void doResetAlarmLevel(MangoUser userService, UserEventServiceWebsocket userEventServiceWebsocket);
}
