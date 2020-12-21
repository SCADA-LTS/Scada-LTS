package org.scada_lts.service;

import com.serotonin.mango.rt.event.*;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.MailingListService;

import java.util.List;

public interface ScheduledExecuteInactiveEventService {
    List<ScheduledEvent> getScheduledEvents(CommunicationChannel communicationChannel);
    void scheduleEvent(EventHandlerVO eventHandlerVO, EventInstance event);
    void scheduleEvent(ScheduledEvent event);
    void unscheduleEvent(EventHandlerVO eventHandler, EventInstance event, CommunicationChannel communicationChannel);
    void unscheduleEvent(ScheduledEvent event, CommunicationChannel communicationChannel);

    boolean isScheduledInactiveEventType(EventInstance eventInstance);

    static ScheduledExecuteInactiveEventService getInstance() {
        return ScheduledExecuteInactiveEventServiceImpl.getInstance();
    }

    static ScheduledExecuteInactiveEventService newInstance(EventDAO eventDAO,
                                                            ScheduledExecuteInactiveEventDAO scheduledEventDAO,
                                                            MailingListService mailingListService) {
        return new ScheduledExecuteInactiveEventServiceImpl(eventDAO, scheduledEventDAO, mailingListService);
    }
}
