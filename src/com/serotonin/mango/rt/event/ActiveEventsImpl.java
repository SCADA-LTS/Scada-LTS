package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ActiveEventsImpl implements ActiveEvents {

    private static final Log LOG = LogFactory.getLog(ActiveEventsImpl.class);

    private final Map<EventType, List<EventInstance>> activeEvents = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock activeEventsLock = new ReentrantReadWriteLock(true);

    @Override
    public void initActiveEvents(List<EventInstance> events) {
        activeEventsLock.writeLock().lock();
        try {
            for(EventInstance event: events) {
                activeEvents.compute(event.getEventType(), (a, b) -> {
                    if (b == null) {
                        return new CopyOnWriteArrayList<>(Set.of(event));
                    } else {
                        b.add(event);
                        return b;
                    }
                });
            }
        } finally {
            activeEventsLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isIgnoreIfNotThenAddActiveEvent(EventInstance evt) {
        activeEventsLock.writeLock().lock();
        try {
            EventType type = evt.getEventType();
            LocalizableMessage message = evt.getMessage();
            List<EventInstance> dup = activeEvents.get(type);
            boolean ignore = isIgnore(type, message, dup);
            if (!ignore && evt.isRtnApplicable()) {
                if (dup == null) {
                    dup = new CopyOnWriteArrayList<>();
                    activeEvents.put(type, dup);
                }
                dup.add(evt);
            }
            return ignore;
        } finally {
            activeEventsLock.writeLock().unlock();
        }
    }

    @Override
    public List<EventInstance> removeActiveEvents(EventType type, LocalizableMessage onlyWithThisMessage) {
        activeEventsLock.writeLock().lock();
        try {
            if(onlyWithThisMessage == null) {
                return activeEvents.remove(type);
            } else {
                List<EventInstance> toRemove = new ArrayList<>();
                List<EventInstance> events = activeEvents.get(type);
                if(events == null)
                    return null;
                for (EventInstance event : events) {
                    EventType eventType = event.getEventType();
                    LocalizableMessage eventMessage = event.getMessage();
                    if (eventType.getDuplicateHandling() == EventType.DuplicateHandling.IGNORE_SAME_MESSAGE
                            && eventMessage != null && containMessage(eventMessage, onlyWithThisMessage)) {
                        toRemove.add(event);
                    }
                }
                if(toRemove.isEmpty())
                    return null;
                events.removeAll(toRemove);
                return toRemove;
            }
        } finally {
            activeEventsLock.writeLock().unlock();
        }
    }

    @Override
    public List<EventInstance> removeActiveEvents(Predicate<EventType> removeIf) {
        activeEventsLock.writeLock().lock();
        try {
            List<EventInstance> toRemove = new ArrayList<>();
            for(EventInstance event: getActiveEvents()) {
                if(removeIf.test(event.getEventType())) {
                    List<EventInstance> events = activeEvents.get(event.getEventType());
                    if(events != null) {
                        events.remove(event);
                    }
                    toRemove.add(event);
                }
            }
            return toRemove;
        } finally {
            activeEventsLock.writeLock().unlock();
        }
    }

    @Override
    public int calculateGlobalHighestAlarmLevel() {
        activeEventsLock.readLock().lock();
        try {
            int max = 0;
            for (EventInstance event : getActiveEvents()) {
                if (event.getAlarmLevel() > max)
                    max = event.getAlarmLevel();
            }
            return max;
        } finally {
            activeEventsLock.readLock().unlock();
        }
    }

    private List<EventInstance> getActiveEvents() {
        return activeEvents.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static boolean isIgnore(EventType type, LocalizableMessage message, List<EventInstance> dup) {
        if (dup != null) {
            // Check the duplicate handling.
            int dh = type.getDuplicateHandling();
            if (dh == EventType.DuplicateHandling.DO_NOT_ALLOW) {
                // Create a log error...
                LOG.error("An event was raised for a type that is already active: type="
                        + type + ", message=" + message.getKey());
                // ... but ultimately just ignore the thing.
                return true;
            }

            if (dh == EventType.DuplicateHandling.IGNORE) {
                // Safely return.
                return true;
            }

            if (dh == EventType.DuplicateHandling.IGNORE_SAME_MESSAGE) {
                // Ignore only if the message is the same. There may be events
                // of this type with different messages,
                // so look through them all for a match.
                for (EventInstance e : dup) {
                    if (e.getMessage().equals(message)) {
                        return true;
                    }
                }
            }

            // Otherwise we just continue...
        }

        return false;
    }

    private static boolean containMessage(LocalizableMessage parent,
                                          LocalizableMessage contain) {
        if(parent.equals(contain)) {
            return true;
        }
        Object[] args = parent.getArgs();
        if(args != null && args.length > 0) {
            for (Object arg : parent.getArgs()) {
                if(arg instanceof LocalizableMessage) {
                    if(arg.equals(contain)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
