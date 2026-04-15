package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.adapter.MangoEvent;

import java.util.List;
import java.util.function.Predicate;

public interface ActiveEvents {

    void initActiveEvents(List<EventInstance> events);
    boolean addActiveEvent(EventInstance event);
    boolean isIgnoreIfNotThenAddActiveEvent(EventInstance event, boolean suppressed);
    int calculateGlobalHighestAlarmLevel();
    List<EventInstance> removeActiveEvents(EventType type, LocalizableMessage onlyWithThisMessage);
    List<EventInstance> removeActiveEvents(Predicate<EventType> removeIf);

    static ActiveEvents newSync(MangoEvent eventService) {
        return new ActiveEventsSync(eventService);
    }
}
