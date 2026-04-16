package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.event.handlers.EventHandlerRT;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.DataSourcePointEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoEvent;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ActiveEventsSync implements ActiveEvents {

    private static final Log LOG = LogFactory.getLog(ActiveEventsSync.class);

    private final Map<EventType, List<EventInstance>> activeEvents = new HashMap<>();
    private final MangoEvent eventService;
    private final ReentrantReadWriteLock activeEventsLock = new ReentrantReadWriteLock(true);

    public ActiveEventsSync(MangoEvent eventService) {
        this.eventService = eventService;
    }

    @Override
    public void initActiveEvents(List<EventInstance> events) {
        activeEventsLock.writeLock().lock();
        try {
            for(EventInstance event: events) {
                add(event);
            }
        } finally {
            activeEventsLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isIgnoreIfNotThenAddActiveEvent(EventInstance event, boolean suppressed) {
        activeEventsLock.writeLock().lock();
        try {
            EventType type = event.getEventType();
            LocalizableMessage message = event.getMessage();
            List<EventInstance> dup = activeEvents.get(type);
            boolean ignore = isIgnore(type, message, dup);
            if(!ignore) {
                if (!suppressed) {
                    setHandlers(event);
                }

                eventService.saveEvent(event);

                if (event.isRtnApplicable()) {
                    if (dup == null) {
                        dup = new ArrayList<>();
                        activeEvents.put(type, dup);
                    }
                    dup.add(event);
                }
            }
            return ignore;
        } finally {
            activeEventsLock.writeLock().unlock();
        }
    }

    @Override
    public boolean addActiveEvent(EventInstance event) {
        activeEventsLock.writeLock().lock();
        try {
            return add(event);
        } finally {
            activeEventsLock.writeLock().unlock();
        }
    }

    @Override
    public List<EventInstance> removeActiveEvents(EventType type, LocalizableMessage onlyWithThisMessage) {
        activeEventsLock.writeLock().lock();
        try {
            if(onlyWithThisMessage == null) {
                List<EventInstance> toRemove = activeEvents.remove(type);
                return toRemove == null ? null : new ArrayList<>(toRemove);
            } else {
                List<EventInstance> toRemove = new ArrayList<>();
                List<EventInstance> dup = activeEvents.get(type);
                if(dup == null)
                    return null;
                for (EventInstance event : dup) {
                    LocalizableMessage eventMessage = event.getMessage();
                    if (eventMessage != null && containMessage(eventMessage, onlyWithThisMessage)) {
                        toRemove.add(event);
                    }
                }
                if(toRemove.isEmpty())
                    return null;
                dup.removeAll(toRemove);
                if(dup.isEmpty()) {
                    activeEvents.remove(type);
                }
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
            List<EventType> toRemoveKeys = new ArrayList<>();
            for(EventInstance event: getActiveEvents()) {
                EventType eventType = event.getEventType();
                if(removeIf.test(eventType)) {
                    List<EventInstance> events = activeEvents.get(eventType);
                    if(events != null) {
                        events.remove(event);
                        if(events.isEmpty()) {
                            toRemoveKeys.add(eventType);
                        }
                    }
                    toRemove.add(event);
                }
            }
            if(toRemove.isEmpty())
                return null;
            for(EventType type: toRemoveKeys) {
                activeEvents.remove(type);
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

    @Override
    public boolean isActiveEventsForDataPoint(EventType type) {
        activeEventsLock.readLock().lock();
        try {
            for(EventInstance event: getActiveEvents()) {
                if((event.getEventType() instanceof DataSourcePointEventType) && event.getEventType().getDataPointId() == type.getDataPointId()) {
                    return true;
                }
            }
            return false;
        } finally {
            activeEventsLock.readLock().unlock();
        }
    }

    @Override
    public boolean isActiveEventsForDataSource(EventType type) {
        activeEventsLock.readLock().lock();
        try {
            for(EventInstance event: getActiveEvents()) {
                if((event.getEventType() instanceof DataSourceEventType) && event.getEventType().getDataSourceId() == type.getDataSourceId()) {
                    return true;
                }
            }
            return false;
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
        // Check if there is an event for this type already active.
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

    private boolean add(EventInstance event) {
        activeEvents.putIfAbsent(event.getEventType(), new ArrayList<>());
        return activeEvents.get(event.getEventType()).add(event);
    }

    private void setHandlers(EventInstance evt) {
        List<EventHandlerVO> vos = eventService
                .getEventHandlers(evt.getEventType());
        List<EventHandlerRT> rts = null;
        for (EventHandlerVO vo : vos) {
            if (!vo.isDisabled()) {
                if (rts == null)
                    rts = new ArrayList<>();
                rts.add(vo.createRuntime());
            }
        }
        if (rts != null)
            evt.setHandlers(rts);
    }
}
