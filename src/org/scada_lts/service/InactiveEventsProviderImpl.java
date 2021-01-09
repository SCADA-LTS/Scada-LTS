package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEvent;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InactiveEventsProviderImpl implements InactiveEventsProvider {

    private static final int LIMIT = 100;
    private AtomicInteger lockNonBlocking = new AtomicInteger(0);
    private final EventDAO eventDAO;
    private final ScheduledExecuteInactiveEventDAO scheduledEventDAO;
    private final CommunicationChannel communicationChannel;
    private final Queue<ScheduledExecuteInactiveEventInstance> relations;
    private final Set<ScheduledExecuteInactiveEventInstance> blocking;

    InactiveEventsProviderImpl(EventDAO eventDAO, ScheduledExecuteInactiveEventDAO scheduledEventDAO,
                           CommunicationChannel communicationChannel) {
        this.eventDAO = eventDAO;
        this.scheduledEventDAO = scheduledEventDAO;
        this.communicationChannel = communicationChannel;
        this.relations = new ConcurrentLinkedQueue<>();
        this.blocking = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    private static Set<ScheduledExecuteInactiveEventInstance> init(EventDAO eventDAO,
                                                            ScheduledExecuteInactiveEventDAO scheduledEventDAO,
                                                            CommunicationChannel communicationChannel,
                                                            Set<ScheduledExecuteInactiveEventInstance> exclude,
                                                            int limit) {

        List<ScheduledExecuteInactiveEvent> scheduledEvents = scheduledEventDAO
                .selectByMailingListId(communicationChannel.getChannelId(),
                        exclude.stream()
                                .map(ScheduledExecuteInactiveEventInstance::getKey)
                                .collect(Collectors.toList()), limit);

        Map<Integer, EventInstance> events = eventDAO.getAllStatusEvents(scheduledEvents.stream()
                .map(ScheduledExecuteInactiveEvent::getSourceEventId)
                .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(EventInstance::getId, a -> a));

        Map<Integer, EventHandlerVO> eventHandlers = eventDAO.getEventHandlers(scheduledEvents.stream()
                .map(ScheduledExecuteInactiveEvent::getEventHandlerId)
                .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(EventHandlerVO::getId, a -> a));

        return scheduledEvents.stream()
                .filter(a -> events.containsKey(a.getSourceEventId())
                        && eventHandlers.containsKey(a.getEventHandlerId()))
                .map(a -> new ScheduledExecuteInactiveEventInstance(
                        eventHandlers.get(a.getEventHandlerId()),
                        events.get(a.getSourceEventId()),
                        communicationChannel.getData()))
                .filter(a -> a.getEventHandler().getHandlerType() == communicationChannel.getType().getEventHandlerType())
                .collect(Collectors.toSet());
    }

    @Override
    public List<ScheduledEvent> getScheduledEvents(int limit) {
        if(limit <= 0) {
            return Collections.emptyList();
        }
        if(relations.isEmpty() && lockNonBlocking.getAndDecrement() == 0) {
            try {
                relations.addAll(init(eventDAO, scheduledEventDAO, communicationChannel, blocking, LIMIT).stream()
                        .sorted(Comparator.comparingInt(a -> a.getEvent().getId()))
                        .collect(Collectors.toList()));
            } finally {
                lockNonBlocking.set(0);
            }
        }
        List<ScheduledEvent> scheduledEvents = new ArrayList<>();
        while(relations.peek() != null && scheduledEvents.size() < limit) {
            ScheduledExecuteInactiveEventInstance poll = relations.poll();
            ScheduledEvent scheduledEvent = poll.toScheduledEvent();
            scheduledEvents.add(scheduledEvent);
            blocking.add(poll);
        }
        return scheduledEvents;
    }

    @Override
    public void repeat(ScheduledEvent event) {
        ScheduledExecuteInactiveEventInstance instance =
                new ScheduledExecuteInactiveEventInstance(event.getEventHandler(), event.getEvent(),
                        communicationChannel.getData());
        if(!relations.contains(instance))
            relations.add(instance);
    }

    @Override
    public void confirm(ScheduledEvent event) {
        blocking.remove(new ScheduledExecuteInactiveEventInstance(event.getEventHandler(), event.getEvent(),
                communicationChannel.getData()));
    }

    @Override
    public CommunicationChannel getChannel() {
        return communicationChannel;
    }
}
