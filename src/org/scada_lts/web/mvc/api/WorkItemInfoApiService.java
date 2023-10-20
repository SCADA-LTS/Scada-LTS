package org.scada_lts.web.mvc.api;

import com.serotonin.mango.rt.maint.work.*;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.json.WorkItemInfo;
import org.scada_lts.web.mvc.api.json.WorkItemInfoList;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ApiUtils.convertList;
import static org.scada_lts.utils.ApiUtils.convertMap;
import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;

@Service
public class WorkItemInfoApiService {

    public WorkItemInfoList getCurrentWorkItems(HttpServletRequest request) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getCurrentAll), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, Long> getCurrentWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriority, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, Long> getCurrentWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, WorkItemsUtils::getCurrentAll);
    }

    public WorkItemInfoList getCurrentExecutedWorkItems(HttpServletRequest request) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getCurrentExecuted), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getCurrentExecuted);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getCurrentExecuted);
    }

    public Map<String, Long> getCurrentExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getCurrentExecuted);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriority, WorkItemsUtils::getCurrentExecuted);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, WorkItemsUtils::getCurrentExecuted);
    }

    public Map<String, Long> getCurrentExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, WorkItemsUtils::getCurrentExecuted);
    }

    public WorkItemInfoList getCurrentExecutedSuccessWorkItems(HttpServletRequest request) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getCurrentExecutedSuccess), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedSuccessWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getCurrentExecutedSuccess);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedSuccessWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getCurrentExecutedSuccess);
    }

    public Map<String, Long> getCurrentExecutedSuccessWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getCurrentExecutedSuccess);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedSuccessWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriority, WorkItemsUtils::getCurrentExecutedSuccess);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedSuccessWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, WorkItemsUtils::getCurrentExecutedSuccess);
    }

    public Map<String, Long> getCurrentExecutedSuccessWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, WorkItemsUtils::getCurrentExecutedSuccess);
    }

    public WorkItemInfoList getCurrentExecutedLessWorkItems(HttpServletRequest request, int executedMs) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLessThan, executedMs), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLessWorkItemsGroupByClassName(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassName, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLessThan, executedMs);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLessWorkItemsGroupByClassNameMetrics(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLessThan, executedMs);
    }


    public Map<String, Long> getCurrentExecutedLessWorkItemsGroupByClassNameCount(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLessThan, executedMs);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLessWorkItemsGroupByPriority(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByPriority, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLessThan, executedMs);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLessWorkItemsGroupByPriorityMetrics(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLessThan, executedMs);
    }

    public Map<String, Long> getCurrentExecutedLessWorkItemsGroupByPriorityCount(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLessThan, executedMs);
    }

    public WorkItemInfoList getCurrentExecutedLongerWorkItems(HttpServletRequest request, int executedMs) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLongerThan, executedMs), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLongerWorkItemsGroupByClassName(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassName, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLongerThan, executedMs);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLongerWorkItemsGroupByClassNameMetrics(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLongerThan, executedMs);
    }

    public Map<String, Long> getCurrentExecutedLongerWorkItemsGroupByClassNameCount(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLongerThan, executedMs);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLongerWorkItemsGroupByPriority(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByPriority, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLongerThan, executedMs);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLongerWorkItemsGroupByPriorityMetrics(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLongerThan, executedMs);
    }

    public Map<String, Long> getCurrentExecutedLongerWorkItemsGroupByPriorityCount(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, byExecuteMsComparator(),
                WorkItemsUtils::getCurrentExecutedLongerThan, executedMs);
    }

    public WorkItemInfoList getCurrentExecutedWorkItemsByPriority(HttpServletRequest request, WorkItemPriority priority) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, bySerialComparator(),
                WorkItemsUtils::getCurrentExecutedByPriority, priority), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsByPriorityGroupByClassName(HttpServletRequest request,
                                                                                                 WorkItemPriority priority) {
        return get(request, WorkItemInfoApiService::groupByClassName, bySerialComparator(),
                WorkItemsUtils::getCurrentExecutedByPriority, priority);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request,
                                                                                               WorkItemPriority priority) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, bySerialComparator(),
                WorkItemsUtils::getCurrentExecutedByPriority, priority);
    }

    public Map<String, Long> getCurrentExecutedWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request,
                                                                                        WorkItemPriority priority) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, bySerialComparator(),
                WorkItemsUtils::getCurrentExecutedByPriority, priority);
    }

    public WorkItemInfoList getCurrentNotExecutedWorkItems(HttpServletRequest request) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getCurrentNotExecuted), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentNotExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getCurrentNotExecuted);
    }

    public Map<String, WorkItemInfoList> getCurrentNotExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getCurrentNotExecuted);
    }

    public Map<String, Long> getCurrentNotExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getCurrentNotExecuted);
    }

    public Map<String, WorkItemInfoList> getCurrentNotExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriority, WorkItemsUtils::getCurrentNotExecuted);
    }

    public Map<String, WorkItemInfoList> getCurrentNotExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, WorkItemsUtils::getCurrentNotExecuted);
    }


    public Map<String, Long> getCurrentNotExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, WorkItemsUtils::getCurrentNotExecuted);
    }

    public WorkItemInfoList getHistoryProcessWorkItems(HttpServletRequest request) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getHistoryProcess), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getHistoryProcessWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, WorkItemInfoList> getHistoryProcessWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, Long> getHistoryProcessWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, WorkItemInfoList> getHistoryProcessWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriority, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, WorkItemInfoList> getHistoryProcessWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, Long> getHistoryProcessWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, WorkItemsUtils::getHistoryProcess);
    }

    public WorkItemInfoList getHistoryWorkItemsByPriority(HttpServletRequest request, WorkItemPriority priority) {
        return convertList(getWorkItems(request, priority), WorkItemInfoList::new);
    }

    public WorkItemInfoList getHistoryExecutedWorkItems(HttpServletRequest request) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getHistoryExecuted), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, Long> getHistoryExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriority, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, Long> getHistoryExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, WorkItemInfoList> getHistoryWorkItemsByPriorityGroupByClassName(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getHistoryHighPriority);
            case MEDIUM:
                return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getHistoryMediumPriority);
            case LOW:
                return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getHistoryLowPriority);
            case IDLE:
                return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getHistoryIdle);
            default:
                return Collections.emptyMap();
        }
    }

    public Map<String, WorkItemInfoList> getHistoryWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getHistoryHighPriority);
            case MEDIUM:
                return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getHistoryMediumPriority);
            case LOW:
                return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getHistoryLowPriority);
            case IDLE:
                return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getHistoryIdle);
            default:
                return Collections.emptyMap();
        }
    }

    public Map<String, Long> getHistoryWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getHistoryHighPriority);
            case MEDIUM:
                return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getHistoryMediumPriority);
            case LOW:
                return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getHistoryLowPriority);
            case IDLE:
                return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getHistoryIdle);
            default:
                return Collections.emptyMap();
        }
    }

    public WorkItemInfoList getHistoryExecutedFailedWorkItems(HttpServletRequest request) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getHistoryFailed), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedFailedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedFailedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, Long> getHistoryExecutedFailedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedFailedWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriority, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedFailedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, Long> getHistoryExecutedFailedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, WorkItemsUtils::getHistoryFailed);
    }

    public WorkItemInfoList getHistoryExecutedLongerWorkItems(HttpServletRequest request, int executedMs) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, byExecuteMsComparator(),
                WorkItemsUtils::getHistoryByExecutedLongerThan, executedMs), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedLongerWorkItemsGroupByClassName(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassName, byExecuteMsComparator(),
                WorkItemsUtils::getHistoryByExecutedLongerThan, executedMs);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedLongerWorkItemsGroupByClassNameMetrics(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, byExecuteMsComparator(),
                WorkItemsUtils::getHistoryByExecutedLongerThan, executedMs);
    }

    public Map<String, Long> getHistoryExecutedLongerWorkItemsGroupByClassNameCount(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, byExecuteMsComparator(),
                WorkItemsUtils::getHistoryByExecutedLongerThan, executedMs);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedLongerWorkItemsGroupByPriority(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByPriority, byExecuteMsComparator(),
                WorkItemsUtils::getHistoryByExecutedLongerThan, executedMs);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedLongerWorkItemsGroupByPriorityMetrics(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, byExecuteMsComparator(),
                WorkItemsUtils::getHistoryByExecutedLongerThan, executedMs);
    }

    public Map<String, Long> getHistoryExecutedLongerWorkItemsGroupByPriorityCount(HttpServletRequest request, int executedMs) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, byExecuteMsComparator(),
                WorkItemsUtils::getHistoryByExecutedLongerThan, executedMs);
    }

    public WorkItemInfoList getRunningWorkItems(HttpServletRequest request) {
        return convertList(get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getRunning), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getRunningWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassName, WorkItemsUtils::getRunning);
    }

    public Map<String, WorkItemInfoList> getRunningWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameMetrics, WorkItemsUtils::getRunning);
    }

    public Map<String, Long> getRunningWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByClassNameCounting, WorkItemsUtils::getRunning);
    }

    public Map<String, WorkItemInfoList> getRunningWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriority, WorkItemsUtils::getRunning);
    }

    public Map<String, WorkItemInfoList> getRunningWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityMetrics, WorkItemsUtils::getRunning);
    }

    public Map<String, Long> getRunningWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, WorkItemInfoApiService::groupByPriorityCounting, WorkItemsUtils::getRunning);
    }

    private static <T> T get(HttpServletRequest request,
                     Function<List<WorkItemExecute>, T> preparing,
                     Supplier<List<WorkItemExecute>> data) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItemExecute> workItems = data.get();
            return preparing.apply(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    private static <R, T, S> T get(HttpServletRequest request,
                                   BiFunction<List<WorkItemExecute>, R, T> preparing, R preparingArg,
                                   Function<S, List<WorkItemExecute>> data, S dataArg) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItemExecute> workItems = data.apply(dataArg);
            return preparing.apply(workItems, preparingArg);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    private static Map<String, Long> groupByClassNameCounting(List<WorkItemExecute> workItems, Comparator<WorkItemExecute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(WorkItemInfo::getClassName, Collectors.counting()));
    }

    private static Map<String, Long> groupByPriorityCounting(List<WorkItemExecute> workItems, Comparator<WorkItemExecute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(a -> a.getPriority().name(), Collectors.counting()));
    }

    private static Map<String, WorkItemInfoList> groupByClassName(List<WorkItemExecute> workItems, Comparator<WorkItemExecute> comparator) {
        return convertMap(workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(WorkItemInfo::getClassName, Collectors.toList())), WorkItemInfoList::new);
    }

    private static Map<String, WorkItemInfoList> groupByClassNameMetrics(List<WorkItemExecute> workItems, Comparator<WorkItemExecute> comparator) {
        return convertMap(workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(WorkItemInfo::getClassName, Collectors.toList())), a -> new WorkItemInfoList(a).onlyMetrics());
    }

    private static Map<String, WorkItemInfoList> groupByPriority(List<WorkItemExecute> workItems, Comparator<WorkItemExecute> comparator) {
        return convertMap(workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(a -> a.getPriority().name(), Collectors.toList())), WorkItemInfoList::new);
    }

    private static Map<String, WorkItemInfoList> groupByPriorityMetrics(List<WorkItemExecute> workItems, Comparator<WorkItemExecute> comparator) {
        return convertMap(workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(a -> a.getPriority().name(), Collectors.toList())), a -> new WorkItemInfoList(a).onlyMetrics());
    }

    private static Map<String, Long> groupByClassNameCounting(List<WorkItemExecute> workItems) {
        return groupByClassNameCounting(workItems, Comparator.reverseOrder());
    }

    private static Map<String, WorkItemInfoList> groupByClassName(List<WorkItemExecute> workItems) {
        return groupByClassName(workItems, Comparator.reverseOrder());
    }

    private static Map<String, WorkItemInfoList> groupByClassNameMetrics(List<WorkItemExecute> workItems) {
        return groupByClassNameMetrics(workItems, Comparator.reverseOrder());
    }


    private static Map<String, Long> groupByPriorityCounting(List<WorkItemExecute> workItems) {
        return groupByPriorityCounting(workItems, Comparator.reverseOrder());
    }

    private static Map<String, WorkItemInfoList> groupByPriority(List<WorkItemExecute> workItems) {
        return groupByPriority(workItems, Comparator.reverseOrder());
    }

    private static Map<String, WorkItemInfoList> groupByPriorityMetrics(List<WorkItemExecute> workItems) {
        return groupByPriorityMetrics(workItems, Comparator.reverseOrder());
    }

    private static List<WorkItemInfo> getWorkItems(List<WorkItemExecute> workItems, Comparator<WorkItemExecute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.toList());
    }

    private static List<WorkItemInfo> getWorkItems(List<WorkItemExecute> workItems) {
        return getWorkItems(workItems, Comparator.reverseOrder());
    }

    private static Comparator<WorkItemExecute> byExecuteMsComparator() {
        return (a, b) -> Long.compare(b.getWorkItem().getExecutedMs(), a.getWorkItem().getExecutedMs());
    }

    private static Comparator<WorkItemExecute> bySerialComparator() {
        return (a, b) -> Long.compare(b.getSerial(), a.getSerial());
    }

    private static List<WorkItemInfo> getWorkItems(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getHistoryHighPriority);
            case MEDIUM:
                return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getHistoryMediumPriority);
            case LOW:
                return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getHistoryLowPriority);
            case IDLE:
                return get(request, WorkItemInfoApiService::getWorkItems, WorkItemsUtils::getHistoryIdle);
            default:
                return Collections.emptyList();
        }
    }
}
