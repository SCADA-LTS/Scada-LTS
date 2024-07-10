package org.scada_lts.web.mvc.api;

import com.serotonin.mango.rt.maint.work.*;
import org.scada_lts.quartz.ReadItemsPerSecond;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.json.WorkItemInfo;
import org.scada_lts.web.mvc.api.json.WorkItemInfoList;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ApiUtils.convertMap;
import static org.scada_lts.utils.ApiUtils.sortMap;
import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;
import static org.scada_lts.web.mvc.api.WorkItemInfoApiService.Comparators.byExecuteMsComparator;
import static org.scada_lts.web.mvc.api.WorkItemInfoApiService.Comparators.bySerialComparator;
import static org.scada_lts.web.mvc.api.WorkItemInfoApiService.Conditions.*;

@Service
public class WorkItemInfoApiService {

    public WorkItemInfoList getCurrentWorkItems(HttpServletRequest request) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList,
                WorkItemsUtils::getCurrentAll, WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, GroupBy::className, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, Long> getCurrentWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, GroupBy::priority, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getCurrentAll);
    }

    public Map<String, Long> getCurrentWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getCurrentAll);
    }

    public WorkItemInfoList getCurrentExecutedWorkItems(HttpServletRequest request) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getCurrentAll,
                isExecuted(), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, GroupBy::className, WorkItemsUtils::getCurrentAll, isExecuted());
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentAll, isExecuted());
    }

    public Map<String, Long> getCurrentExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentAll, isExecuted());
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, GroupBy::priority, WorkItemsUtils::getCurrentAll, isExecuted());
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getCurrentAll, isExecuted());
    }

    public Map<String, Long> getCurrentExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getCurrentAll, isExecuted());
    }

    public WorkItemInfoList getCurrentExecutedSuccessWorkItems(HttpServletRequest request) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getCurrentAll,
                isExecuted().and(isSuccess()), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedSuccessWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, GroupBy::className, WorkItemsUtils::getCurrentAll, isExecuted().and(isSuccess()));
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedSuccessWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentAll, isExecuted().and(isSuccess()));
    }

    public Map<String, Long> getCurrentExecutedSuccessWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentAll, isExecuted().and(isSuccess()));
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedSuccessWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, GroupBy::priority, WorkItemsUtils::getCurrentAll, isExecuted().and(isSuccess()));
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedSuccessWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getCurrentAll, isExecuted().and(isSuccess()));
    }

    public Map<String, Long> getCurrentExecutedSuccessWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getCurrentAll, isExecuted().and(isSuccess()));
    }

    public WorkItemInfoList getCurrentExecutedLessWorkItems(HttpServletRequest request, int executedMs) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getCurrentAll,
                isExecuted().and(isLessThan(executedMs)), byExecuteMsComparator(), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLessWorkItemsGroupByClassName(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::className, WorkItemsUtils::getCurrentAll, isExecuted().and(isLessThan(executedMs)), byExecuteMsComparator());
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLessWorkItemsGroupByClassNameMetrics(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentAll, isExecuted().and(isLessThan(executedMs)), byExecuteMsComparator());
    }

    public Map<String, Long> getCurrentExecutedLessWorkItemsGroupByClassNameCount(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentAll, isExecuted().and(isLessThan(executedMs)), byExecuteMsComparator());

    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLessWorkItemsGroupByPriority(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priority, WorkItemsUtils::getCurrentAll, isExecuted().and(isLessThan(executedMs)), byExecuteMsComparator());
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLessWorkItemsGroupByPriorityMetrics(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getCurrentAll, isExecuted().and(isLessThan(executedMs)), byExecuteMsComparator());
    }

    public Map<String, Long> getCurrentExecutedLessWorkItemsGroupByPriorityCount(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getCurrentAll, isExecuted().and(isLessThan(executedMs)), byExecuteMsComparator());
    }

    public WorkItemInfoList getCurrentExecutedLongerWorkItems(HttpServletRequest request, int executedMs) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getCurrentAll,
                isExecuted().and(isLongerThan(executedMs)), byExecuteMsComparator(), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLongerWorkItemsGroupByClassName(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getCurrentAll, isExecuted().and(isLongerThan(executedMs)), byExecuteMsComparator());
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLongerWorkItemsGroupByClassNameMetrics(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentAll, isExecuted().and(isLongerThan(executedMs)), byExecuteMsComparator());
    }

    public Map<String, Long> getCurrentExecutedLongerWorkItemsGroupByClassNameCount(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentAll, isExecuted().and(isLongerThan(executedMs)), byExecuteMsComparator());
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLongerWorkItemsGroupByPriority(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priority, WorkItemsUtils::getCurrentAll, isExecuted().and(isLongerThan(executedMs)), byExecuteMsComparator());
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedLongerWorkItemsGroupByPriorityMetrics(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getCurrentAll, isExecuted().and(isLongerThan(executedMs)), byExecuteMsComparator());
    }

    public Map<String, Long> getCurrentExecutedLongerWorkItemsGroupByPriorityCount(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getCurrentAll, isExecuted().and(isLongerThan(executedMs)), byExecuteMsComparator());
    }

    public WorkItemInfoList getCurrentExecutedWorkItemsByPriority(HttpServletRequest request, WorkItemPriority priority) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getCurrentAll,
                isExecuted().and(isPriority(priority)), WorkItemInfoList::new);
    }

    public WorkItemInfoList getCurrentWorkItemsByPriority(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getCurrentHighPriority, WorkItemInfoList::new);
            case MEDIUM:
                return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getCurrentMediumPriority, WorkItemInfoList::new);
            case LOW:
                return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getCurrentLowPriority, WorkItemInfoList::new);
            default:
                return new WorkItemInfoList(Collections.emptyList(), null);
        }
    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsByPriorityGroupByClassName(HttpServletRequest request,
                                                                                                 WorkItemPriority priority) {
        return get(request, GroupBy::className, WorkItemsUtils::getCurrentAll, isExecuted().and(isPriority(priority)));

    }

    public Map<String, WorkItemInfoList> getCurrentExecutedWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request,
                                                                                               WorkItemPriority priority) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentAll, isExecuted().and(isPriority(priority)));
    }

    public Map<String, Long> getCurrentExecutedWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request,
                                                                                        WorkItemPriority priority) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentAll, isExecuted().and(isPriority(priority)));
    }

    public WorkItemInfoList getCurrentNotExecutedWorkItems(HttpServletRequest request) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getCurrentAll,
                isNotExecuted(), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getCurrentNotExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, GroupBy::className, WorkItemsUtils::getCurrentAll, isNotExecuted());
    }

    public Map<String, WorkItemInfoList> getCurrentNotExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentAll, isNotExecuted());
    }

    public Map<String, Long> getCurrentNotExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentAll, isNotExecuted());
    }

    public Map<String, WorkItemInfoList> getCurrentNotExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, GroupBy::priority, WorkItemsUtils::getCurrentAll, isNotExecuted());
    }

    public Map<String, WorkItemInfoList> getCurrentNotExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getCurrentAll, isNotExecuted());
    }

    public Map<String, Long> getCurrentNotExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getCurrentAll, isNotExecuted());
    }

    public WorkItemInfoList getHistoryProcessWorkItems(HttpServletRequest request) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList,
                WorkItemsUtils::getHistoryProcess, WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getHistoryProcessWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, GroupBy::className, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, WorkItemInfoList> getHistoryProcessWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, Long> getHistoryProcessWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, WorkItemInfoList> getHistoryProcessWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, GroupBy::priority, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, WorkItemInfoList> getHistoryProcessWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getHistoryProcess);
    }

    public Map<String, Long> getHistoryProcessWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getHistoryProcess);
    }

    public WorkItemInfoList getHistoryWorkItemsByPriority(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getHistoryHighPriority, WorkItemInfoList::new);
            case MEDIUM:
                return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getHistoryMediumPriority, WorkItemInfoList::new);
            case LOW:
                return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getHistoryLowPriority, WorkItemInfoList::new);
            default:
                return new WorkItemInfoList(new ArrayList<>(), null);
        }
    }

    public WorkItemInfoList getHistoryExecutedWorkItems(HttpServletRequest request) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getHistoryExecuted, WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, GroupBy::className, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, Long> getHistoryExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, GroupBy::priority, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, Long> getHistoryExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getHistoryExecuted);
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsByPriorityGroupByClassName(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, GroupBy::className, WorkItemsUtils::getCurrentHighPriority);
            case MEDIUM:
                return get(request, GroupBy::className, WorkItemsUtils::getCurrentMediumPriority);
            case LOW:
                return get(request, GroupBy::className, WorkItemsUtils::getCurrentLowPriority);
            default:
                return Collections.emptyMap();
        }
    }

    public Map<String, WorkItemInfoList> getCurrentWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentHighPriority);
            case MEDIUM:
                return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentMediumPriority);
            case LOW:
                return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getCurrentLowPriority);
            default:
                return Collections.emptyMap();
        }
    }

    public Map<String, Long> getCurrentWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentHighPriority);
            case MEDIUM:
                return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentMediumPriority);
            case LOW:
                return get(request, GroupBy::classNameCounting, WorkItemsUtils::getCurrentLowPriority);
            default:
                return Collections.emptyMap();
        }
    }

    public Map<String, WorkItemInfoList> getHistoryWorkItemsByPriorityGroupByClassName(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, GroupBy::className, WorkItemsUtils::getHistoryHighPriority);
            case MEDIUM:
                return get(request, GroupBy::className, WorkItemsUtils::getHistoryMediumPriority);
            case LOW:
                return get(request, GroupBy::className, WorkItemsUtils::getHistoryLowPriority);
            default:
                return Collections.emptyMap();
        }
    }

    public Map<String, WorkItemInfoList> getHistoryWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getHistoryHighPriority);
            case MEDIUM:
                return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getHistoryMediumPriority);
            case LOW:
                return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getHistoryLowPriority);
            default:
                return Collections.emptyMap();
        }
    }

    public Map<String, Long> getHistoryWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request, WorkItemPriority priority) {
        switch (priority) {
            case HIGH:
                return get(request, GroupBy::classNameCounting, WorkItemsUtils::getHistoryHighPriority);
            case MEDIUM:
                return get(request, GroupBy::classNameCounting, WorkItemsUtils::getHistoryMediumPriority);
            case LOW:
                return get(request, GroupBy::classNameCounting, WorkItemsUtils::getHistoryLowPriority);
            default:
                return Collections.emptyMap();
        }
    }

    public WorkItemInfoList getHistoryExecutedFailedWorkItems(HttpServletRequest request) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getHistoryFailed, WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedFailedWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, GroupBy::className, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedFailedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, Long> getHistoryExecutedFailedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedFailedWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, GroupBy::priority, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedFailedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getHistoryFailed);
    }

    public Map<String, Long> getHistoryExecutedFailedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getHistoryFailed);
    }

    public WorkItemInfoList getHistoryExecutedLongerWorkItems(HttpServletRequest request, int executedMs) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getHistoryExecutedLonger,
                isLongerThan(executedMs), byExecuteMsComparator(), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedLongerWorkItemsGroupByClassName(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::className, WorkItemsUtils::getHistoryExecutedLonger, isLongerThan(executedMs), byExecuteMsComparator());
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedLongerWorkItemsGroupByClassNameMetrics(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getHistoryExecutedLonger, isLongerThan(executedMs), byExecuteMsComparator());
    }

    public Map<String, Long> getHistoryExecutedLongerWorkItemsGroupByClassNameCount(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getHistoryExecutedLonger, isLongerThan(executedMs), byExecuteMsComparator());
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedLongerWorkItemsGroupByPriority(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priority, WorkItemsUtils::getHistoryExecutedLonger, isLongerThan(executedMs), byExecuteMsComparator());
    }

    public Map<String, WorkItemInfoList> getHistoryExecutedLongerWorkItemsGroupByPriorityMetrics(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getHistoryExecutedLonger, isLongerThan(executedMs), byExecuteMsComparator());
    }

    public Map<String, Long> getHistoryExecutedLongerWorkItemsGroupByPriorityCount(HttpServletRequest request, int executedMs) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getHistoryExecutedLonger, isLongerThan(executedMs), byExecuteMsComparator());
    }

    public WorkItemInfoList getRunningWorkItems(HttpServletRequest request) {
        return getTo(request, WorkItemInfoApiService::convertToWorkItemInfoList, WorkItemsUtils::getRunning,
                isRunning(), WorkItemInfoList::new);
    }

    public Map<String, WorkItemInfoList> getRunningWorkItemsGroupByClassName(HttpServletRequest request) {
        return get(request, GroupBy::className, WorkItemsUtils::getRunning, isRunning());
    }

    public Map<String, WorkItemInfoList> getRunningWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return get(request, GroupBy::classNameMetrics, WorkItemsUtils::getRunning, isRunning());
    }

    public Map<String, Long> getRunningWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return get(request, GroupBy::classNameCounting, WorkItemsUtils::getRunning, isRunning());
    }

    public Map<String, WorkItemInfoList> getRunningWorkItemsGroupByPriority(HttpServletRequest request) {
        return get(request, GroupBy::priority, WorkItemsUtils::getRunning, isRunning());
    }

    public Map<String, WorkItemInfoList> getRunningWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return get(request, GroupBy::priorityMetrics, WorkItemsUtils::getRunning, isRunning());
    }

    public Map<String, Long> getRunningWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return get(request, GroupBy::priorityCounting, WorkItemsUtils::getRunning, isRunning());
    }

    private static <T, R> R getTo(HttpServletRequest request,
                                  BiFunction<List<WorkItemExecute>, Comparator<WorkItemExecute>, T> preparing,
                                  Supplier<ReadWorkItems> data,
                                  BiFunction<T, ReadItemsPerSecond, R> converter) {
        return getTo(request, preparing, data, a -> true, bySerialComparator(), converter);
    }

    private static <T, R> R getTo(HttpServletRequest request,
                                  BiFunction<List<WorkItemExecute>, Comparator<WorkItemExecute>, T> preparing,
                                  Supplier<ReadWorkItems> data,
                                  Predicate<WorkItemExecute> filtering,
                                  BiFunction<T, ReadItemsPerSecond, R> converter) {
        return getTo(request, preparing, data, filtering, bySerialComparator(), converter);
    }

    private static <T, R> R getTo(HttpServletRequest request,
                                  BiFunction<List<WorkItemExecute>, Comparator<WorkItemExecute>, T> preparing,
                                  Supplier<ReadWorkItems> data,
                                  Predicate<WorkItemExecute> filtering,
                                  Comparator<WorkItemExecute> comparator,
                                  BiFunction<T, ReadItemsPerSecond, R> converter) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            ReadWorkItems workItems = data.get();
            return converter.apply(preparing.apply(filtering(workItems.get(), filtering), comparator.thenComparing(Comparator.reverseOrder())), workItems.getItemsPerSecond());
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    private static <T> T get(HttpServletRequest request,
                             BiFunction<List<WorkItemInfo>, ReadItemsPerSecond, T> preparing,
                             Supplier<ReadWorkItems> data) {
        return get(request, preparing, data, a -> true, bySerialComparator());
    }

    private static <T> T get(HttpServletRequest request,
                             BiFunction<List<WorkItemInfo>, ReadItemsPerSecond, T> preparing,
                             Supplier<ReadWorkItems> data,
                             Predicate<WorkItemExecute> filtering) {
        return get(request, preparing, data, filtering, bySerialComparator());
    }

    private static <T> T get(HttpServletRequest request,
                             Function<List<WorkItemInfo>, T> preparing,
                             Supplier<ReadWorkItems> data) {
        return get(request, preparing, data, a -> true, bySerialComparator());
    }

    private static <T> T get(HttpServletRequest request,
                             Function<List<WorkItemInfo>, T> preparing,
                             Supplier<ReadWorkItems> data,
                             Predicate<WorkItemExecute> filtering) {
        return get(request, preparing, data, filtering, bySerialComparator());
    }

    private static <T> T get(HttpServletRequest request,
                             Function<List<WorkItemInfo>, T> preparing,
                             Supplier<ReadWorkItems> data,
                             Predicate<WorkItemExecute> filtering,
                             Comparator<WorkItemExecute> comparator) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            ReadWorkItems workItems = data.get();
            return preparing.apply(convertToWorkItemInfoList(filtering(workItems.get(), filtering), comparator.thenComparing(Comparator.reverseOrder())));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    private static <T> T get(HttpServletRequest request,
                             BiFunction<List<WorkItemInfo>, ReadItemsPerSecond, T> preparing,
                             Supplier<ReadWorkItems> data,
                             Predicate<WorkItemExecute> filtering,
                             Comparator<WorkItemExecute> comparator) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            ReadWorkItems workItems = data.get();
            return preparing.apply(convertToWorkItemInfoList(filtering(workItems.get(), filtering), comparator.thenComparing(Comparator.reverseOrder())), workItems.getItemsPerSecond());
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    private static List<WorkItemExecute> filtering(List<WorkItemExecute> workItems,
                                                   Predicate<WorkItemExecute> filtering) {
        return workItems.stream()
                .filter(filtering)
                .collect(Collectors.toList());
    }

    private static List<WorkItemInfo> convertToWorkItemInfoList(List<WorkItemExecute> workItems, Comparator<WorkItemExecute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.toList());
    }

    static class GroupBy {
        private GroupBy() {}

        private static Map<String, Long> classNameCounting(List<WorkItemInfo> workItems) {
            return sortMap(workItems.stream()
                    .collect(Collectors.groupingBy(WorkItemInfo::getClassName, Collectors.counting())), Map.Entry.comparingByValue());
        }

        private static Map<String, Long> priorityCounting(List<WorkItemInfo> workItems) {
            return sortMap(workItems.stream()
                    .collect(Collectors.groupingBy(a -> a.getPriority().name(), Collectors.counting())), Map.Entry.comparingByValue());
        }

        private static Map<String, WorkItemInfoList> className(List<WorkItemInfo> workItems,
                                                               ReadItemsPerSecond itemsPerSecond) {
            return convertMap(workItems.stream().collect(Collectors.groupingBy(WorkItemInfo::getClassName, Collectors.toList())),
                    workItemExecutes -> new WorkItemInfoList(workItemExecutes, itemsPerSecond), Comparator.comparing(a -> a.getValue().size()));
        }

        private static Map<String, WorkItemInfoList> classNameMetrics(List<WorkItemInfo> workItems,
                                                                      ReadItemsPerSecond itemsPerSecond) {
            return convertMap(workItems.stream()
                            .collect(Collectors.groupingBy(WorkItemInfo::getClassName, Collectors.toList())),
                    workItemExecutes -> new WorkItemInfoList(workItemExecutes, itemsPerSecond).onlyMetrics(), Comparator.comparing(a -> a.getValue().size()));
        }

        private static Map<String, WorkItemInfoList> priority(List<WorkItemInfo> workItems,
                                                              ReadItemsPerSecond itemsPerSecond) {
            return convertMap(workItems.stream()
                            .collect(Collectors.groupingBy(a -> a.getPriority().name(), Collectors.toList())),
                    workItemExecutes -> new WorkItemInfoList(workItemExecutes, itemsPerSecond), Comparator.comparing(a -> a.getValue().size()));
        }

        private static Map<String, WorkItemInfoList> priorityMetrics(List<WorkItemInfo> workItems,
                                                                     ReadItemsPerSecond itemsPerSecond) {
            return convertMap(workItems.stream()
                            .collect(Collectors.groupingBy(a -> a.getPriority().name(), Collectors.toList())),
                    workItemExecutes -> new WorkItemInfoList(workItemExecutes, itemsPerSecond).onlyMetrics(), Comparator.comparing(a -> a.getValue().size()));
        }
    }

    static class Comparators {

        private Comparators() {}

        static Comparator<WorkItemExecute> byExecuteMsComparator() {
            return (a, b) -> Long.compare(b.getWorkItem().getExecutedMs(), a.getWorkItem().getExecutedMs());
        }

        static Comparator<WorkItemExecute> bySerialComparator() {
            return (a, b) -> Long.compare(b.getSerial(), a.getSerial());
        }
    }

    static class Conditions {

        private Conditions() {}

        static Predicate<WorkItemExecute> isExecuted() {
            return execute -> execute.getWorkItem().isExecuted();
        }

        static Predicate<WorkItemExecute> isNotExecuted() {
            return execute -> !execute.getWorkItem().isExecuted();
        }

        static Predicate<WorkItemExecute> isSuccess() {
            return execute -> execute.getWorkItem().isSuccess();
        }

        static Predicate<WorkItemExecute> isLessThan(long executedMs) {
            return execute -> execute.getWorkItem().getExecutedMs() < executedMs;
        }

        static Predicate<WorkItemExecute> isLongerThan(long executedMs) {
            return execute -> execute.getWorkItem().getExecutedMs() > executedMs;
        }

        static Predicate<WorkItemExecute> isRunning() {
            return execute -> execute.getWorkItem().isRunning();
        }

        static Predicate<WorkItemExecute> isPriority(WorkItemPriority priority) {
            return execute -> execute.getPriority() == priority;
        }
    }
}
