package org.scada_lts.service;

import br.org.scadabr.api.constants.AlarmLevel;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class ScheduledExecuteInactiveEventServiceImpl implements ScheduledExecuteInactiveEventService {

    private static final Log LOG = LogFactory.getLog(ScheduledExecuteInactiveEventServiceImpl.class);


    private final ScheduledExecuteInactiveEventDAO scheduledEventDAO;
    private final MailingListService mailingListService;
    private final Set<ScheduledExecuteInactiveEventInstance> relations = Collections.newSetFromMap(new ConcurrentHashMap<>());
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
        this.relations.addAll(init(eventDAO, scheduledEventDAO, mailingListService));

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
                .collect(Collectors.toSet());
    }

    @Override
    public List<ScheduledEvent> getScheduledEvents(CommunicationChannel channel) {
        return relations.stream()
                    .filter(a -> CommunicationChannelType.getType(a.getEventHandler().getHandlerType()) == channel.getType())
                    .filter(a -> a.getMailingList().getId() == channel.getChannelId())
                    .map(ScheduledExecuteInactiveEventInstance::toScheduledEvent)
                    .sorted(Comparator.comparingInt(a -> a.getEvent().getId()))
                    .collect(Collectors.toList());

    }

    @Override
    public boolean isScheduledInactiveEventType(EventInstance event) {
        return event.getEventType() instanceof ScheduledInactiveEventType;
    }

    @Override
    public void scheduleEvent(EventHandlerVO eventHandler, EventInstance event) {
        if(event.getAlarmLevel() == AlarmLevels.NONE) {
            LOG.warn("Event with alarm level NONE: eventId:" + event.getId());
            return;
        }
        if(isScheduledInactiveEventType(event)) {
            LOG.warn("Event scheduled type: " + ScheduledInactiveEventType.class.getSimpleName() + ", eventId:" + event.getId());
            return;
        }
        List<MailingList> mailingLists = mailingListService.convertToMailingLists(eventHandler.getActiveRecipients());

        for (MailingList mailingList : mailingLists) {
            if (mailingList.isCollectInactiveEmails()) {
                ScheduledExecuteInactiveEventInstance inactiveEventInstance =
                        new ScheduledExecuteInactiveEventInstance(eventHandler, event, mailingList);
                if (!inactiveEventInstance.isActive()) {
                    if (!contains(inactiveEventInstance)) {
                        scheduledEventDAO.insert(inactiveEventInstance.getKey());
                        add(inactiveEventInstance);
                    } else {
                        LOG.warn("Inactive event instance is duplicated!: " + inactiveEventInstance);
                    }
                }
            }
        }
    }

    @Override
    public void unscheduleEvent(ScheduledEvent event, CommunicationChannel channel) {
        unscheduleEvent(event.getEventHandler(), event.getEvent(), channel);
    }

    private void unscheduleEvent(EventHandlerVO eventHandler,
                                 EventInstance event,
                                 CommunicationChannel communicationChannel) {
        if(communicationChannel.getType().getEventHandlerType() == eventHandler.getHandlerType()) {
            ScheduledExecuteInactiveEventInstance inactiveEventInstance =
                    new ScheduledExecuteInactiveEventInstance(eventHandler, event, communicationChannel.getData());
            if (contains(inactiveEventInstance)) {
                scheduledEventDAO.delete(inactiveEventInstance.getKey());
                remove(inactiveEventInstance);
            } else {
                LOG.warn("Event is not scheduled!: " + inactiveEventInstance);
            }
        }
    }

    private void add(ScheduledExecuteInactiveEventInstance inactiveEventInstance) {
        relations.add(inactiveEventInstance);
    }

    private void remove(ScheduledExecuteInactiveEventInstance inactiveEventInstance) {
        relations.remove(inactiveEventInstance);
    }

    private boolean contains(ScheduledExecuteInactiveEventInstance inactiveEventInstance) {
        return relations.contains(inactiveEventInstance);
    }
}
