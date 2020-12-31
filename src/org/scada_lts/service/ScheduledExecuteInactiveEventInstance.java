package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.util.IntervalUtil;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEvent;

import java.util.Objects;

public class ScheduledExecuteInactiveEventInstance {

    private ScheduledExecuteInactiveEvent key;
    private MailingList mailingList;
    private EventHandlerVO eventHandler;
    private EventInstance event;

    public ScheduledExecuteInactiveEventInstance(EventHandlerVO eventHandler,
                                                 EventInstance event,
                                                 MailingList mailingList) {
        this.key = new ScheduledExecuteInactiveEvent(eventHandler, event, mailingList);
        this.mailingList = mailingList;
        this.eventHandler = eventHandler;
        this.event = event;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public EventInstance getEvent() {
        return event;
    }

    public EventHandlerVO getEventHandler() {
        return eventHandler;
    }

    public ScheduledExecuteInactiveEvent getKey() {
        return key;
    }

    public ScheduledEvent toScheduledEvent() {
        return new ScheduledEvent(event, eventHandler);
    }

    public boolean isActive() {
        return IntervalUtil.isActiveByInterval(mailingList, event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledExecuteInactiveEventInstance)) return false;
        ScheduledExecuteInactiveEventInstance that = (ScheduledExecuteInactiveEventInstance) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {

        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "ScheduledExecuteInactiveEventInstance{" +
                "key=" + key +
                ", mailingList=" + mailingList +
                ", eventHandler=" + eventHandler +
                ", event=" + event +
                '}';
    }
}
