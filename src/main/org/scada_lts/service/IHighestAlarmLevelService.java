package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import org.scada_lts.web.ws.model.AlarmLevelMessage;

import java.util.function.BiConsumer;

public interface IHighestAlarmLevelService {
    int getAlarmLevel(User user);
    boolean doUpdateAlarmLevel(User user, EventInstance event, BiConsumer<User, AlarmLevelMessage> send);
    boolean doSendAlarmLevel(User user, BiConsumer<User, AlarmLevelMessage> send);
    boolean doRemoveAlarmLevel(User user, EventInstance event, BiConsumer<User, AlarmLevelMessage> send);
    void doResetAlarmLevels(BiConsumer<User, AlarmLevelMessage> send);
}
