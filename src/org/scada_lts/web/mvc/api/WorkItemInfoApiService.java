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
    public List<WorkItemInfo> getExecutedLongerWorkItems(HttpServletRequest request, int executedMs, boolean history) {
        return get(request, WorkItemInfoApiService::getWorkItems, byExecuteMsComparator(),
                executed -> {
                    if(history)
                        return WorkItemsUtils.getByExecutedLongerThanHistory(executed);
                    return WorkItemsUtils.getByExecutedLongerThan(executed);
                }, executedMs);
    }

    public List<WorkItemInfo> getExecutedWorkItemsByPriority(HttpServletRequest request, WorkItemPriority priority) {
        return get(request, WorkItemInfoApiService::getWorkItems, byExecuteMsComparator(),
                WorkItemsUtils::getByPriority, priority);
    }

    public List<WorkItemInfo> getExecutedLessWorkItems(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::getWorkItems, byExecuteMsComparator(),
                WorkItemsUtils::getByExecutedLessThan, executedMs);
    }

    public Map<String, Long> getExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getByExecuted);
    }

    public Map<String, Long> getExecutedSuccessWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getBySuccess);
    }

    public Map<String, Long> getExecutedFailedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getByFailed);
    }

    public Map<String, Long> getWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getAll);
    }

    public Map<String, Long> getNotExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getByNotExecuted);
    }

    public Map<String, Long> getExecutedLongerWorkItemsGroupByClassName(HttpServletRequest request, int executedMs, boolean history) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, byExecuteMsComparator(),
                executed -> {
                    if(history)
                        return WorkItemsUtils.getByExecutedLongerThanHistory(executed);
                    return WorkItemsUtils.getByExecutedLongerThan(executed);
                }, executedMs);
    }

    public Map<String, Long> getExecutedLessWorkItemsGroupByClassName(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, byExecuteMsComparator(),
                WorkItemsUtils::getByExecutedLessThan, executedMs);
    }

    public Map<String, Long> getExecutedLessWorkItemsGroupByPriorityClassName(HttpServletRequest request,
                                                                              WorkItemPriority priority) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting,
                byExecuteMsComparator(), WorkItemsUtils::getByPriority, priority);
    }

    public Map<String, Long> getRunningWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getRunning);
    }

    public Map<String, List<WorkItemInfo>> getExecutedWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getByExecuted);
    }

    public Map<String, List<WorkItemInfo>> getExecutedSuccessWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getBySuccess);
    }

    public Map<String, List<WorkItemInfo>> getExecutedFailedWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getByFailed);
    }

    public Map<String, List<WorkItemInfo>> getWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getAll);
    }

    public Map<String, List<WorkItemInfo>> getNotExecutedWorkItemsGroupBy(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getByNotExecuted);
    }

    public Map<String, List<WorkItemInfo>> getExecutedLongerWorkItemsGroupBy(HttpServletRequest request, int executedMs, boolean history) {
        return get(request, WorkItemInfoApiService::groupByClassName, byExecuteMsComparator(),
                executed -> {
                    if(history)
                        return WorkItemsUtils.getByExecutedLongerThanHistory(executed);
                    return WorkItemsUtils.getByExecutedLongerThan(executed);
                }, executedMs);
    }

    public Map<String, List<WorkItemInfo>> getExecutedLessWorkItemsGroupBy(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassName, byExecuteMsComparator(),
                WorkItemsUtils::getByExecutedLessThan, executedMs);
    }

    public Map<String, List<WorkItemInfo>> getExecutedLessWorkItemsGroupByPriority(HttpServletRequest request,
                                                                     WorkItemPriority priority) {
        return get(request, WorkItemInfoApiService::groupByClassName,
                byExecuteMsComparator(), WorkItemsUtils::getByPriority, priority);
    }

    public List<WorkItemInfo> getRunningWorkItems(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getRunning);
    }

    public Map<String, List<WorkItemInfo>> getRunningWorkItemsGroupBy(HttpServletRequest request) {
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

    private static Map<String, Long> groupByClassNameCounting(List<WorkItems.Execute> workItems, Comparator<WorkItems.Execute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(a -> a.getWorkItemExecute().getClassName(), Collectors.counting()));
    }

    private static Map<String, List<WorkItemInfo>> groupByClassName(List<WorkItems.Execute> workItems, Comparator<WorkItems.Execute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(a -> a.getWorkItemExecute().getClassName(), Collectors.toList()));
    }

    private static Map<String, Long> groupByClassNameCounting(List<WorkItems.Execute> workItems) {
        return groupByClassNameCounting(workItems, Comparator.reverseOrder());
    }

    private static Map<String, List<WorkItemInfo>> groupByClassName(List<WorkItems.Execute> workItems) {
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
