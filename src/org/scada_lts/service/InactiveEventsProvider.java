package org.scada_lts.service;

import com.serotonin.mango.rt.event.ScheduledEvent;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;

import java.util.List;

public interface InactiveEventsProvider {
    List<ScheduledEvent> getScheduledEvents(int limit);
    void repeat(ScheduledEvent event);
    void confirm(ScheduledEvent event);
    void clear();
    CommunicationChannel getCommunicationChannel();

    static InactiveEventsProvider newInstance(EventDAO eventDAO, ScheduledExecuteInactiveEventDAO scheduledInactiveEventDAO,
                                                            CommunicationChannel channel, int dataFromBaseLimit) {
        return new InactiveEventsProviderImpl(eventDAO, scheduledInactiveEventDAO, channel, dataFromBaseLimit);
    }
}
