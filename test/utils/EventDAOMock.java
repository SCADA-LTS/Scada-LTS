package utils;


import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventDAOMock {

    private List<EventInstance> eventInstances;
    private List<EventHandlerVO> eventHandlers;

    public EventDAOMock(List<EventInstance> eventInstances, List<EventHandlerVO> eventHandlers) {
        this.eventInstances = eventInstances;
        this.eventHandlers = eventHandlers;
    }

    public List<EventInstance> getAllStatusEvents(Set<Integer> ids) {
        return eventInstances.stream()
                .filter(a -> ids.contains(a.getId()))
                .collect(Collectors.toList());
    }

    public List<EventHandlerVO> getEventHandlers(Set<Integer> ids) {
        return eventHandlers.stream()
                .filter(a -> ids.contains(a.getId()))
                .collect(Collectors.toList());
    }
}
