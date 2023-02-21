package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.timer.TimerTaskState;
import org.scada_lts.web.mvc.api.json.ScheduledWorkItem;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;

@Service
public class ScheduledWorkItemInfoApiService {

    public List<ScheduledWorkItem> getScheduledWorkItems(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return Common.timer.getTasks().stream()
                .map(ScheduledWorkItem::new)
                .collect(Collectors.toList());
    }

    public List<ScheduledWorkItem> getScheduledWorkItems(HttpServletRequest request, TimerTaskState state) {
        checkIfNonAdminThenUnauthorized(request);
        return Common.timer.getTasks().stream()
                .filter(a -> TimerTaskState.stateOf(a) == state)
                .map(ScheduledWorkItem::new)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return Common.timer.getTasks().stream()
                .collect(Collectors.groupingBy(a -> a.getClass().getName(), Collectors.counting()));
    }

    public Map<String, List<ScheduledWorkItem>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        return Common.timer.getTasks().stream()
                .map(ScheduledWorkItem::new)
                .collect(Collectors.groupingBy(a -> a.getClass().getName(), Collectors.toList()));
    }

    public Map<String, Long> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request, TimerTaskState state) {
        checkIfNonAdminThenUnauthorized(request);
        return Common.timer.getTasks().stream()
                .filter(a -> TimerTaskState.stateOf(a) == state)
                .collect(Collectors.groupingBy(a -> a.getClass().getName(), Collectors.counting()));
    }

    public Map<String, List<ScheduledWorkItem>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request, TimerTaskState state) {
        checkIfNonAdminThenUnauthorized(request);
        return Common.timer.getTasks().stream()
                .filter(a -> TimerTaskState.stateOf(a) == state)
                .map(ScheduledWorkItem::new)
                .collect(Collectors.groupingBy(a -> a.getClass().getName(), Collectors.toList()));
    }
}
