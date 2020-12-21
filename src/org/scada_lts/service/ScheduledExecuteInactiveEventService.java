package org.scada_lts.service;

import com.serotonin.mango.rt.event.*;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.MailingListService;

import java.util.List;
import java.util.Set;

public interface ScheduledExecuteInactiveEventService {
    List<ScheduledEvent> getScheduledEvents(MailingList mailingLis);
    void scheduleEvent(EventHandlerVO eventHandlerVO, EventInstance event);
    void scheduleEvent(ScheduledEvent event);
    void unscheduleEvent(EventHandlerVO eventHandler, EventInstance event, MailingList mailingList);
    void unscheduleEvent(ScheduledEvent event, MailingList mailingList);

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
