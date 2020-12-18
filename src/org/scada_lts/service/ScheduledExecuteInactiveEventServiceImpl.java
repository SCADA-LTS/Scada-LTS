package org.scada_lts.service;

import com.serotonin.mango.rt.event.*;
import com.serotonin.mango.rt.event.type.ScheduledInactiveEventType;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.joda.time.DateTime;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEvent;
import org.scada_lts.mango.service.MailingListService;

import java.util.*;
import java.util.stream.Collectors;

class ScheduledExecuteInactiveEventServiceImpl implements ScheduledExecuteInactiveEventService {

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

        return scheduledEvents.stream()
                .filter(a -> mailingLists.containsKey(a.getMailingListId())
                        && events.containsKey(a.getSourceEventId()))
                .map(a -> new ScheduledExecuteInactiveEventInstance(
                        CommunicationChannel.newChannel(mailingLists.get(a.getMailingListId()),
                                CommunicationChannelType.getType(a.getEventHandlerType())),
                        events.get(a.getSourceEventId())))
                .collect(Collectors.toSet());
    }

    @Override
    public List<EventInstance> getScheduledEvents(CommunicationChannel channel) {
        synchronized (mutex) {
            return relations.stream()
                    .filter(a -> a.getCommunicationChannel().equals(channel))
                    .map(ScheduledExecuteInactiveEventInstance::getEvent)
                    .sorted(Comparator.comparingInt(EventInstance::getId).reversed())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void unscheduleEvent(CommunicationChannel channel, EventInstance event) {
        ScheduledExecuteInactiveEventInstance inactiveEventInstance =
                new ScheduledExecuteInactiveEventInstance(channel, event);
        scheduledEventDAO.delete(new ScheduledExecuteInactiveEvent(inactiveEventInstance));
        synchronized (mutex) {
            relations.remove(inactiveEventInstance);
        }
    }

    @Override
    public boolean isScheduledInactiveEventType(EventInstance event) {
        return event.getEventType() instanceof ScheduledInactiveEventType;
    }

    @Override
    public void scheduleEvent(EventHandlerVO eventHandlerVO, EventInstance event) {
        if(event.getAlarmLevel() != AlarmLevels.NONE
                && !isScheduledInactiveEventType(event)) {
            DateTime fireTime = new DateTime(event.getActiveTimestamp());
            List<MailingList> mailingLists = mailingListService.convertToMailingLists(eventHandlerVO.getActiveRecipients());

            for (MailingList mailingList : mailingLists) {
                if (mailingList.isCollectInactiveEmails()) {
                    CommunicationChannelType type = CommunicationChannelType.getType(eventHandlerVO.getHandlerType());
                    CommunicationChannel channel = CommunicationChannel.newChannel(mailingList, type);
                    if (!channel.isActiveFor(fireTime)) {
                        ScheduledExecuteInactiveEventInstance inactiveEventInstance =
                                new ScheduledExecuteInactiveEventInstance(channel, event);
                        scheduledEventDAO.insert(
                                new ScheduledExecuteInactiveEvent(inactiveEventInstance));
                        synchronized (mutex) {
                            relations.add(inactiveEventInstance);
                        }
                    }
                }
            }
        }
    }
}
