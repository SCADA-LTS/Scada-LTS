package org.scada_lts.service;

import com.serotonin.mango.rt.event.*;
import com.serotonin.mango.rt.event.type.ScheduledInactiveEventType;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEvent;
import org.scada_lts.mango.service.MailingListService;

import java.util.*;
import java.util.stream.Collectors;

class ScheduledExecuteInactiveEventServiceImpl implements ScheduledExecuteInactiveEventService {

    private static final Log LOG = LogFactory.getLog(ScheduledExecuteInactiveEventServiceImpl.class);


    private final ScheduledExecuteInactiveEventDAO scheduledEventDAO;
    private final MailingListService mailingListService;
    private final Set<ScheduledExecuteInactiveEventInstance> relations;
    private final Object mutex = new Object();

    private static class LazyHolder {
        public static final ScheduledExecuteInactiveEventService INSTANCE = new ScheduledExecuteInactiveEventServiceImpl(new EventDAO(),
                ScheduledExecuteInactiveEventDAO.getInstance(), new MailingListService());
    }

    static ScheduledExecuteInactiveEventService getInstance() {
        return LazyHolder.INSTANCE;
    }

    ScheduledExecuteInactiveEventServiceImpl(EventDAO eventDAO,
                                                     ScheduledExecuteInactiveEventDAO scheduledEventDAO,
                                                     MailingListService mailingListService) {
        this.scheduledEventDAO = scheduledEventDAO;
        this.mailingListService = mailingListService;
        this.relations = init(eventDAO, scheduledEventDAO, mailingListService);

    }

    private Set<ScheduledExecuteInactiveEventInstance> init(EventDAO eventDAO,
                                                            ScheduledExecuteInactiveEventDAO scheduledEventDAO,
                                                            MailingListService mailingListService) {

        List<ScheduledExecuteInactiveEvent> scheduledEvents = scheduledEventDAO.select();

        Map<Integer, EventInstance> events = eventDAO.getAllStatusEvents(scheduledEvents.stream()
                        .map(ScheduledExecuteInactiveEvent::getSourceEventId)
                        .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(EventInstance::getId, a -> a));

        Map<Integer, MailingList> mailingLists = mailingListService.getMailingLists(scheduledEvents.stream()
                        .map(ScheduledExecuteInactiveEvent::getMailingListId)
                        .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(MailingList::getId, a -> a));

        Map<Integer, EventHandlerVO> eventHandlers = eventDAO.getEventHandlers(scheduledEvents.stream()
                        .map(ScheduledExecuteInactiveEvent::getEventHandlerId)
                        .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(EventHandlerVO::getId, a -> a));

        return scheduledEvents.stream()
                .filter(a -> mailingLists.containsKey(a.getMailingListId())
                        && events.containsKey(a.getSourceEventId())
                        && eventHandlers.containsKey(a.getEventHandlerId()))
                .map(a -> new ScheduledExecuteInactiveEventInstance(
                        eventHandlers.get(a.getEventHandlerId()),
                        events.get(a.getSourceEventId()),
                        mailingLists.get(a.getMailingListId())))
                /*.collect(() -> new TreeMap<>(Comparator.comparingInt(ScheduledExecuteInactiveEvent::getSourceEventId)
                                    .thenComparing(ScheduledExecuteInactiveEvent::getMailingListId)
                                    .thenComparing(ScheduledExecuteInactiveEvent::getEventHandlerId)),
                        (a,b) -> a.put(b.getKey(), b), TreeMap::putAll);*/
                .collect(Collectors.toSet());
    }

    @Override
    public List<ScheduledEvent> getScheduledEvents(MailingList mailingList) {
        synchronized (mutex) {
            return relations.stream()
                    .filter(a -> a.getMailingList().getId() == mailingList.getId())
                    .map(ScheduledExecuteInactiveEventInstance::toScheduledEvent)
                    .sorted(Comparator.comparingInt(a -> a.getEvent().getId()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void unscheduleEvent(EventHandlerVO eventHandler,
                                EventInstance event,
                                MailingList mailingList) {
        ScheduledExecuteInactiveEventInstance inactiveEventInstance =
                new ScheduledExecuteInactiveEventInstance(eventHandler, event, mailingList);
        synchronized (mutex) {
            if (relations.contains(inactiveEventInstance)) {
                scheduledEventDAO.delete(inactiveEventInstance.getKey());
                relations.remove(inactiveEventInstance);
            } else {
                LOG.warn("Event is not scheduled!: " + inactiveEventInstance);
            }
        }
    }

    @Override
    public boolean isScheduledInactiveEventType(EventInstance event) {
        return event.getEventType() instanceof ScheduledInactiveEventType;
    }

    @Override
    public void scheduleEvent(EventHandlerVO eventHandler, EventInstance event) {
        if(event.getAlarmLevel() != AlarmLevels.NONE
                && !isScheduledInactiveEventType(event)) {
            List<MailingList> mailingLists = mailingListService.convertToMailingLists(eventHandler.getActiveRecipients());

            for (MailingList mailingList : mailingLists) {
                if (mailingList.isCollectInactiveEmails()) {
                    ScheduledExecuteInactiveEventInstance inactiveEventInstance =
                            new ScheduledExecuteInactiveEventInstance(eventHandler, event, mailingList);
                    if (!inactiveEventInstance.isActive()) {
                        synchronized (mutex) {
                            if (!relations.contains(inactiveEventInstance)) {
                                scheduledEventDAO.insert(inactiveEventInstance.getKey());
                                relations.add(inactiveEventInstance);
                            } else {
                                LOG.warn("Duplicated!: " + inactiveEventInstance);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scheduleEvent(ScheduledEvent event) {
        scheduleEvent(event.getEventHandler(), event.getEvent());
    }

    @Override
    public void unscheduleEvent(ScheduledEvent event, MailingList mailingList) {
        unscheduleEvent(event.getEventHandler(), event.getEvent(), mailingList);
    }
}
