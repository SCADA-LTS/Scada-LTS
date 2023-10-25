package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.timer.TimerTaskState;
import org.scada_lts.web.mvc.api.json.ScheduledWorkItem;
import org.scada_lts.web.mvc.api.json.ScheduledWorkItemInfoList;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ApiUtils.convertList;
import static org.scada_lts.utils.ApiUtils.convertMap;
import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;

@Service
public class ScheduledWorkItemInfoApiService {

    public ScheduledWorkItemInfoList<ScheduledWorkItem> getScheduledWorkItems(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return convertList(Common.timer.getTasks().stream()
                .map(ScheduledWorkItem::new)
                .collect(Collectors.toList()), ScheduledWorkItemInfoList::new);
    }

    public ScheduledWorkItemInfoList<ScheduledWorkItem> getScheduledWorkItems(HttpServletRequest request, TimerTaskState state) {
        checkIfNonAdminThenUnauthorized(request);
        return convertList(Common.timer.getTasks().stream()
                .filter(a -> TimerTaskState.stateOf(a) == state)
                .map(ScheduledWorkItem::new)
                .collect(Collectors.toList()), ScheduledWorkItemInfoList::new);
    }

    public ScheduledWorkItemInfoList<TimerTaskState> getScheduledWorkItemsStates(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return convertList(Common.timer.getTasks().stream()
                .map(TimerTaskState::stateOf)
                .collect(Collectors.toList()), ScheduledWorkItemInfoList::new);
    }

    public Map<String, Long> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return Common.timer.getTasks().stream()
                .collect(Collectors.groupingBy(a -> a.getClass().getName(), Collectors.counting()));
    }

    public Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return convertMap(Common.timer.getTasks().stream()
                .map(ScheduledWorkItem::new)
                .collect(Collectors.groupingBy(ScheduledWorkItem::getClassName, Collectors.toList())),
                ScheduledWorkItemInfoList::new);
    }

    public Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return convertMap(Common.timer.getTasks().stream()
                        .map(ScheduledWorkItem::new)
                        .collect(Collectors.groupingBy(ScheduledWorkItem::getClassName, Collectors.toList())),
                a -> new ScheduledWorkItemInfoList<>(a.size()));
    }

    public Map<String, Long> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request, TimerTaskState state) {
        checkIfNonAdminThenUnauthorized(request);
        return Common.timer.getTasks().stream()
                .filter(a -> TimerTaskState.stateOf(a) == state)
                .collect(Collectors.groupingBy(a -> a.getClass().getName(), Collectors.counting()));
    }

    public Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request, TimerTaskState state) {
        checkIfNonAdminThenUnauthorized(request);
        return convertMap(Common.timer.getTasks().stream()
                .filter(a -> TimerTaskState.stateOf(a) == state)
                .map(ScheduledWorkItem::new)
                .collect(Collectors.groupingBy(ScheduledWorkItem::getClassName, Collectors.toList())),
                ScheduledWorkItemInfoList::new);
    }

    public Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsGroupByClassNameMetrics(HttpServletRequest request, TimerTaskState state) {
        checkIfNonAdminThenUnauthorized(request);
        return convertMap(Common.timer.getTasks().stream()
                        .filter(a -> TimerTaskState.stateOf(a) == state)
                        .map(ScheduledWorkItem::new)
                        .collect(Collectors.groupingBy(ScheduledWorkItem::getClassName, Collectors.toList())),
                a -> new ScheduledWorkItemInfoList<>(a.size()));
    }

    public Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsGroupByStates(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return convertMap(Common.timer.getTasks().stream()
                        .map(ScheduledWorkItem::new)
                        .collect(Collectors.groupingBy(a -> a.getState().name(), Collectors.toList())),
                ScheduledWorkItemInfoList::new);
    }

    public Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsGroupByStatesMetrics(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return convertMap(Common.timer.getTasks().stream()
                        .map(ScheduledWorkItem::new)
                        .collect(Collectors.groupingBy(a -> a.getState().name(), Collectors.toList())),
                a -> new ScheduledWorkItemInfoList<>(a.size()));
    }

    public Map<String, Long> getScheduledWorkItemsGroupByStatesCount(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return Common.timer.getTasks().stream()
                        .map(ScheduledWorkItem::new)
                        .collect(Collectors.groupingBy(a -> a.getState().name(), Collectors.counting()));
    }
}
