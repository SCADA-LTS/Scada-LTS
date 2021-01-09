package org.scada_lts.service;

import com.serotonin.mango.rt.event.*;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.MailingListService;

import java.util.List;

public interface ScheduledExecuteInactiveEventService {
    void scheduleEvent(EventHandlerVO eventHandler, EventInstance event);
    void unscheduleEvent(ScheduledEvent event, CommunicationChannel channel);

    static ScheduledExecuteInactiveEventService getInstance() {
        return ScheduledExecuteInactiveEventServiceImpl.getInstance();
    }

    static ScheduledExecuteInactiveEventService newInstance(ScheduledExecuteInactiveEventDAO scheduledEventDAO,
                                                            MailingListService mailingListService) {
        return new ScheduledExecuteInactiveEventServiceImpl(scheduledEventDAO, mailingListService);
    }
}
