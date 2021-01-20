package com.serotonin.mango.rt.event;

import com.serotonin.mango.vo.event.EventHandlerVO;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.ScheduledExecuteInactiveEventInstance;

import java.util.Objects;

public class ScheduledEvent {

    private final EventInstance event;
    private final EventHandlerVO eventHandler;

    public ScheduledEvent(EventInstance event, EventHandlerVO eventHandler) {
        this.event = event;
        this.eventHandler = eventHandler;
    }

    public EventInstance getEvent() {
        return event;
    }

    public EventHandlerVO getEventHandler() {
        return eventHandler;
    }

    public ScheduledExecuteInactiveEventInstance toScheduledExecuteInactiveEventInstance(CommunicationChannel channel) {
        return new ScheduledExecuteInactiveEventInstance(eventHandler, event, channel.getData());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledEvent)) return false;
        ScheduledEvent that = (ScheduledEvent) o;
        return Objects.equals(getEvent().getId(), that.getEvent().getId()) &&
                Objects.equals(getEventHandler().getId(), that.getEventHandler().getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getEvent().getId(), getEventHandler().getId());
    }
}
