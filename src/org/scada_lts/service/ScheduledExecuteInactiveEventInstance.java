package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;

import java.util.Objects;

public class ScheduledExecuteInactiveEventInstance {

    private CommunicationChannel communicationChannel;
    private EventInstance event;

    public ScheduledExecuteInactiveEventInstance(CommunicationChannel channel, EventInstance event) {
        this.communicationChannel = channel;
        this.event = event;
    }

    public CommunicationChannel getCommunicationChannel() {
        return communicationChannel;
    }

    public EventInstance getEvent() {
        return event;
    }

    public boolean isActive() {
        return communicationChannel.isActiveFor(event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledExecuteInactiveEventInstance)) return false;
        ScheduledExecuteInactiveEventInstance that = (ScheduledExecuteInactiveEventInstance) o;
        return getEvent().getId() == that.getEvent().getId() &&
                Objects.equals(getCommunicationChannel(), that.getCommunicationChannel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommunicationChannel(), getEvent().getId());
    }
}
