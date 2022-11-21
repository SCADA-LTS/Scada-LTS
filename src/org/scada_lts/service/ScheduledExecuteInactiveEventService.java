package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.MailingListService;

public interface ScheduledExecuteInactiveEventService {
    boolean scheduleEvent(EventHandlerVO eventHandler, EventInstance event);
    boolean scheduleEventFail(EventHandlerVO eventHandler, EventInstance event);
    boolean unscheduleEvent(ScheduledEvent event, CommunicationChannel channel);

    static ScheduledExecuteInactiveEventService getInstance() {
        return ScheduledExecuteInactiveEventServiceImpl.getInstance();
    }

    static ScheduledExecuteInactiveEventService newInstance(ScheduledExecuteInactiveEventDAO scheduledEventDAO,
                                                            MailingListService mailingListService) {
        return new ScheduledExecuteInactiveEventServiceImpl(scheduledEventDAO, mailingListService);
    }
}
