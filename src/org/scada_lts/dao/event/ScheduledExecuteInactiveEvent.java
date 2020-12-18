package org.scada_lts.dao.event;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.ScheduledExecuteInactiveEventInstance;

import java.util.Objects;

public class ScheduledExecuteInactiveEvent {

    private int id;
    private int mailingListId;
    private int sourceEventId;
    private int eventHandlerType;

    public ScheduledExecuteInactiveEvent() {
        this.id = Common.NEW_ID;
        this.mailingListId = Common.NEW_ID;
        this.sourceEventId = Common.NEW_ID;
        this.eventHandlerType = EventHandlerVO.TYPE_EMAIL;
    }

    public ScheduledExecuteInactiveEvent(ScheduledExecuteInactiveEventInstance scheduledInactiveCommunicationEvent) {
        CommunicationChannel communicationChannel = scheduledInactiveCommunicationEvent
                .getCommunicationChannel();
        this.id = Common.NEW_ID;
        this.mailingListId = communicationChannel.getChannelId();
        this.sourceEventId = scheduledInactiveCommunicationEvent.getEvent().getId();
        this.eventHandlerType = communicationChannel.getType().getEventHandlerType();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMailingListId() {
        return mailingListId;
    }

    public void setMailingListId(int mailingListId) {
        this.mailingListId = mailingListId;
    }

    public int getSourceEventId() {
        return sourceEventId;
    }

    public void setSourceEventId(int sourceEventId) {
        this.sourceEventId = sourceEventId;
    }

    public int getEventHandlerType() {
        return eventHandlerType;
    }

    public void setEventHandlerType(int eventHandlerType) {
        this.eventHandlerType = eventHandlerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledExecuteInactiveEvent)) return false;
        ScheduledExecuteInactiveEvent that = (ScheduledExecuteInactiveEvent) o;
        return getMailingListId() == that.getMailingListId() &&
                getSourceEventId() == that.getSourceEventId() &&
                getEventHandlerType() == that.getEventHandlerType();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getMailingListId(), getSourceEventId(), getEventHandlerType());
    }
}
