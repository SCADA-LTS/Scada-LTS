package org.scada_lts.web.mvc.api;

import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import com.serotonin.mango.rt.maint.work.WorkItems;
import org.scada_lts.utils.WorkItemsUtils;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.json.WorkItemInfo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;

@Service
public class WorkItemInfoApiService {

    public List<WorkItemInfo> getWorkItems(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getAll);
    }

    public List<WorkItemInfo> getExecutedWorkItems(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getByExecuted);
    }

    public List<WorkItemInfo> getExecutedSuccessWorkItems(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getBySuccess);
    }

    public List<WorkItemInfo> getExecutedFailedWorkItems(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getByFailed);
    }

    public List<WorkItemInfo> getNotExecutedWorkItems(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getByNotExecuted);
    }

    public List<WorkItemInfo> getExecutedLongerWorkItems(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::getWorkItems, byExecuteMsComparator(),
                WorkItemsUtils::getByExecutedLongerThan, executedMs);
    }

    public List<WorkItemInfo> getExecutedWorkItemsByPriority(HttpServletRequest request, WorkItemPriority priority) {
        return get(request, WorkItemInfoApiService::getWorkItems, byExecuteMsComparator(),
                WorkItemsUtils::getByPriority, priority);
    }

    public List<WorkItemInfo> getExecutedLessWorkItems(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::getWorkItems, byExecuteMsComparator(),
                WorkItemsUtils::getByExecutedLessThan, executedMs);
    }

    public Map<String, Long> getExecutedWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getByExecuted);
    }

    public Map<String, Long> getExecutedSuccessWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getBySuccess);
    }

    public Map<String, Long> getExecutedFailedWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getByFailed);
    }

    public Map<String, Long> getWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getAll);
    }

    public Map<String, Long> getNotExecutedWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getByNotExecuted);
    }

    public Map<String, Long> getExecutedLongerWorkItemsGroupBy(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassName, byExecuteMsComparator(),
                WorkItemsUtils::getByExecutedLongerThan, executedMs);
    }

    public Map<String, Long> getExecutedLessWorkItemsGroupBy(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassName, byExecuteMsComparator(),
                WorkItemsUtils::getByExecutedLessThan, executedMs);
    }

    public Map<String, Long> getExecutedLessWorkItemsGroupByPriority(HttpServletRequest request,
                                                                     WorkItemPriority priority) {
        return get(request, WorkItemInfoApiService::groupByClassName,
                byExecuteMsComparator(), WorkItemsUtils::getByPriority, priority);
    }

    public List<WorkItemInfo> getRunningWorkItems(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getRunning);
    }

    public Map<String, Long> getRunningWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getRunning);
    }

    private static <T> T get(HttpServletRequest request,
                     Function<List<WorkItems.Execute>, T> preparing,
                     Supplier<List<WorkItems.Execute>> data) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = data.get();
            return preparing.apply(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    private static <R, T, S> T get(HttpServletRequest request,
                        BiFunction<List<WorkItems.Execute>, R, T> preparing, R preparingArg,
                        Function<S, List<WorkItems.Execute>> data, S dataArg) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = data.apply(dataArg);
            return preparing.apply(workItems, preparingArg);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    private static Map<String, Long> groupByClassName(List<WorkItems.Execute> workItems, Comparator<WorkItems.Execute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(a -> a.getWorkItemExecute().getClassName(), Collectors.counting()));
    }

    private static Map<String, Long> groupByClassName(List<WorkItems.Execute> workItems) {
        return groupByClassName(workItems, Comparator.reverseOrder());
    }

    private static List<WorkItemInfo> getWorkItems(List<WorkItems.Execute> workItems, Comparator<WorkItems.Execute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.toList());
    }

    private static List<WorkItemInfo> getWorkItems(List<WorkItems.Execute> workItems) {
        return getWorkItems(workItems, Comparator.reverseOrder());
    }

    private static Comparator<WorkItems.Execute> byExecuteMsComparator() {
        return (a, b) -> b.getWorkItem().getExecutedMs() - a.getWorkItem().getExecutedMs();
    }
}
