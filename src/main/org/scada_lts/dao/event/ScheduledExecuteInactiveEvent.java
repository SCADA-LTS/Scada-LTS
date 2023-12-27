package org.scada_lts.dao.event;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;

import java.util.Objects;

public class ScheduledExecuteInactiveEvent {

    private final int eventHandlerId;
    private final int sourceEventId;
    private final int mailingListId;

    public ScheduledExecuteInactiveEvent(int eventHandlerId, int sourceEventId, int mailingListId) {
        this.eventHandlerId = eventHandlerId;
        this.sourceEventId = sourceEventId;
        this.mailingListId = mailingListId;
    }

    public ScheduledExecuteInactiveEvent(EventHandlerVO eventHandler, EventInstance sourceEvent, MailingList mailingList) {
        this.eventHandlerId = eventHandler.getId();
        this.sourceEventId = sourceEvent.getId();
        this.mailingListId = mailingList.getId();
    }

    public int getMailingListId() {
        return mailingListId;
    }

    public int getSourceEventId() {
        return sourceEventId;
    }

    public int getEventHandlerId() {
        return eventHandlerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledExecuteInactiveEvent)) return false;
        ScheduledExecuteInactiveEvent that = (ScheduledExecuteInactiveEvent) o;
        return getEventHandlerId() == that.getEventHandlerId() &&
                getSourceEventId() == that.getSourceEventId() &&
                getMailingListId() == that.getMailingListId();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getEventHandlerId(), getSourceEventId(), getMailingListId());
    }
}
