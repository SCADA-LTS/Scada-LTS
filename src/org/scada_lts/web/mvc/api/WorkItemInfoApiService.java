package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.WorkItems;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.json.WorkItemInfo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;

@Service
public class WorkItemInfoApiService {

    public List<WorkItemInfo> getWorkItems(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().get();
            return getWorkItemInfos(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<WorkItemInfo> getExecutedWorkItems(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecuted();
            return getWorkItemInfos(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<WorkItemInfo> getExecutedSuccessWorkItems(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getBySuccess();
            return getWorkItemInfos(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<WorkItemInfo> getExecutedFailWorkItems(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByFail();
            return getWorkItemInfos(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<WorkItemInfo> getNotExecutedWorkItems(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByNotExecuted();
            return getWorkItemInfos(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<WorkItemInfo> getExecutedLongerWorkItems(HttpServletRequest request, int executedMs) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecutedLongerThan(executedMs);
            return getWorkItemInfos(workItems, byExecuteMsComparator());
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<WorkItemInfo> getExecutedLessWorkItems(HttpServletRequest request, int executedMs) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecutedLessThan(executedMs);
            return getWorkItemInfos(workItems, byExecuteMsComparator());
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<String, Long> getExecutedWorkItemsGroupBy(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecuted();
            return groupByClassName(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<String, Long> getExecutedSuccessWorkItemsGroupBy(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getBySuccess();
            return groupByClassName(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<String, Long> getExecutedFailWorkItemsGroupBy(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByFail();
            return groupByClassName(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<String, Long> getWorkItemsGroupBy(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().get();
            return groupByClassName(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<String, Long> getNotExecutedWorkItemsGroupBy(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByNotExecuted();
            return groupByClassName(workItems);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<String, Long> getExecutedLongerWorkItemsGroupBy(HttpServletRequest request, int executedMs) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecutedLongerThan(executedMs);
            return groupByClassName(workItems, byExecuteMsComparator());
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<String, Long> getExecutedLessWorkItemsGroupBy(HttpServletRequest request, int executedMs) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecutedLessThan(executedMs);
            return groupByClassName(workItems, byExecuteMsComparator());
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

    private static List<WorkItemInfo> getWorkItemInfos(List<WorkItems.Execute> workItems, Comparator<WorkItems.Execute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.toList());
    }

    private static List<WorkItemInfo> getWorkItemInfos(List<WorkItems.Execute> workItems) {
        return getWorkItemInfos(workItems, Comparator.reverseOrder());
    }

    private static Comparator<WorkItems.Execute> byExecuteMsComparator() {
        return (a, b) -> b.getWorkItem().getExecutedMs() - a.getWorkItem().getExecutedMs();
    }
}
