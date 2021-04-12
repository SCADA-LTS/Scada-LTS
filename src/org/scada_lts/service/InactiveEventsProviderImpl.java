package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

class InactiveEventsProviderImpl implements InactiveEventsProvider {

    private static final Log log = LogFactory.getLog(InactiveEventsProviderImpl.class);


    private final int dataFromBaseLimit;
    private final AtomicInteger nonBlockingLock;
    private final EventDAO eventDAO;
    private final ScheduledExecuteInactiveEventDAO scheduledEventDAO;
    private final CommunicationChannel communicationChannel;
    private final Queue<ScheduledExecuteInactiveEventInstance> relations;
    private final Set<ScheduledExecuteInactiveEventInstance> blocking;

    InactiveEventsProviderImpl(EventDAO eventDAO, ScheduledExecuteInactiveEventDAO scheduledEventDAO,
                           CommunicationChannel communicationChannel, int dataFromBaseLimit) {
        this.eventDAO = eventDAO;
        this.scheduledEventDAO = scheduledEventDAO;
        this.communicationChannel = communicationChannel;
        this.relations = new ConcurrentLinkedQueue<>();
        this.blocking = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.nonBlockingLock = new AtomicInteger(0);
        this.dataFromBaseLimit = dataFromBaseLimit;
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
        if(((relations.peek() == null && blocking.size() < dataFromBaseLimit) || blocking.size() < dataFromBaseLimit) && nonBlockingLock.getAndDecrement() == 0) {
            try {
                List<ScheduledExecuteInactiveEventInstance> events = init(eventDAO, scheduledEventDAO, communicationChannel, blocking, dataFromBaseLimit - blocking.size()).stream()
                        .sorted(Comparator.comparingInt(a -> a.getEvent().getId()))
                        .collect(Collectors.toList());
                blocking.addAll(events);
                relations.addAll(events);
            } finally {
                nonBlockingLock.set(0);
            }
        }
        log.info("relations: " + relations.size());
        log.info("blocking: " + blocking.size());
        List<ScheduledEvent> scheduledEvents = new ArrayList<>();

        AtomicInteger oneExecuteLimit = new AtomicInteger(limit);
        ScheduledExecuteInactiveEventInstance poll;
        while((poll = relations.poll()) != null) {
            ScheduledEvent scheduledEvent = poll.toScheduledEvent();
            scheduledEvents.add(scheduledEvent);
            if(oneExecuteLimit.decrementAndGet() < 1)
                break;
        }
        return scheduledEvents;
    }

    @Override
    public void repeat(ScheduledEvent event) {
        ScheduledExecuteInactiveEventInstance instance = event.toScheduledExecuteInactiveEventInstance(communicationChannel);
        relations.add(instance);
    }

    @Override
    public void confirm(ScheduledEvent event) {
        ScheduledExecuteInactiveEventInstance instance = event.toScheduledExecuteInactiveEventInstance(communicationChannel);
        blocking.remove(instance);
    }

    @Override
    public void clear() {
        relations.clear();
        blocking.clear();
    }

    @Override
    public CommunicationChannel getCommunicationChannel() {
        return communicationChannel;
    }
}
