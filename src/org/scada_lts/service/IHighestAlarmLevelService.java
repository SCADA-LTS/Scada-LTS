package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import org.scada_lts.web.ws.model.WsAlarmLevelMessage;

import java.util.function.BiConsumer;

public interface IHighestAlarmLevelService {
    int getAlarmLevel(User user);
    boolean doUpdateAlarmLevel(User user, EventInstance event, BiConsumer<User, WsAlarmLevelMessage> send);
    boolean doSendAlarmLevel(User user, BiConsumer<User, WsAlarmLevelMessage> send);
    boolean doRemoveAlarmLevel(User user, EventInstance event, BiConsumer<User, WsAlarmLevelMessage> send);
    void doResetAlarmLevels(BiConsumer<User, WsAlarmLevelMessage> send);
}
