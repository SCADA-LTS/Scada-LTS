package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.List;
import java.util.function.Predicate;

public interface ActiveEvents {

    void initActiveEvents(List<EventInstance> events);
    boolean isIgnoreIfNotThenAddActiveEvent(EventInstance evt);
    int calculateGlobalHighestAlarmLevel();
    List<EventInstance> removeActiveEvents(EventType type, LocalizableMessage onlyWithThisMessage);
    List<EventInstance> removeActiveEvents(Predicate<EventType> removeIf);

    static ActiveEvents newSync() {
        return new ActiveEventsSync();
    }
}
