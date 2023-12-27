package org.scada_lts.web.mvc.api;

import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import com.serotonin.timer.TimerTaskState;
import org.scada_lts.web.mvc.api.json.ScheduledWorkItem;
import org.scada_lts.web.mvc.api.json.ScheduledWorkItemInfoList;
import org.scada_lts.web.mvc.api.json.WorkItemInfoList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/secure/work-items")
public class WorkItemInfoSecureAPI {

    private final WorkItemInfoAPI workItemInfoAPI;

    public WorkItemInfoSecureAPI(WorkItemInfoAPI workItemInfoAPI) {
        this.workItemInfoAPI = workItemInfoAPI;
    }

    @GetMapping(value = "")
    public ResponseEntity<WorkItemInfoList> getCurrentWorkItems(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentWorkItems(request);
    }

    @GetMapping(value = "/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentWorkItemsMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentWorkItemsMetrics(request);
    }

    @GetMapping(value = "/priority/{priority}")
    public ResponseEntity<WorkItemInfoList> getCurrentWorkItemsByPriority(HttpServletRequest request,
                                                                          @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentWorkItemsByPriority(request, priority);
    }

    @GetMapping(value = "/priority/{priority}/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentWorkItemsByPriorityMetrics(HttpServletRequest request,
                                                                                 @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentWorkItemsByPriorityMetrics(request, priority);
    }

    @GetMapping(value = "/priority/{priority}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsByPriorityGroupByClassName(HttpServletRequest request,
                                                                                                       @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentWorkItemsByPriorityGroupByClassName(request, priority);
    }

    @GetMapping(value = "/priority/{priority}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                              @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentWorkItemsByPriorityGroupByClassNameMetrics(request, priority);
    }

    @GetMapping(value = "/priority/{priority}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request,
                                                                                                @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentWorkItemsByPriorityGroupByClassNameCount(request, priority);
    }

    @GetMapping(value = "/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsGroupByClassName(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentWorkItemsGroupByClassName(request);
    }

    @GetMapping(value = "/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentWorkItemsGroupByClassNameMetrics(request);
    }

    @GetMapping(value = "/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentWorkItemsGroupByClassNameCount(request);
    }

    @GetMapping(value = "/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsGroupByPriority(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentWorkItemsGroupByPriority(request);
    }

    @GetMapping(value = "/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentWorkItemsGroupByPriorityMetrics(request);
    }

    @GetMapping(value = "/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentWorkItemsGroupByPriorityCount(request);
    }

    @GetMapping(value = "/not-executed")
    public ResponseEntity<WorkItemInfoList> getCurrentNotExecutedWorkItems(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentNotExecutedWorkItems(request);
    }

    @GetMapping(value = "/not-executed/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentNotExecutedWorkItemsMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentNotExecutedWorkItemsMetrics(request);
    }

    @GetMapping(value = "/not-executed/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentNotExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentNotExecutedWorkItemsGroupByClassName(request);
    }

    @GetMapping(value = "/not-executed/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentNotExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentNotExecutedWorkItemsGroupByClassNameMetrics(request);
    }

    @GetMapping(value = "/not-executed/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentNotExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentNotExecutedWorkItemsGroupByClassNameCount(request);
    }

    @GetMapping(value = "/not-executed/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentNotExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentNotExecutedWorkItemsGroupByPriority(request);
    }

    @GetMapping(value = "/not-executed/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentNotExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentNotExecutedWorkItemsGroupByPriorityMetrics(request);
    }

    @GetMapping(value = "/not-executed/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentNotExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentNotExecutedWorkItemsGroupByPriorityCount(request);
    }

    @GetMapping(value = "/executed")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedWorkItems(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedWorkItems(request);
    }

    @GetMapping(value = "/executed/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedWorkItemsMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsMetrics(request);
    }

    @GetMapping(value = "/executed/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsGroupByClassName(request);
    }

    @GetMapping(value = "/executed/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsGroupByClassNameMetrics(request);
    }

    @GetMapping(value = "/executed/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsGroupByClassNameCount(request);
    }

    @GetMapping(value = "/executed/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsGroupByPriority(request);
    }

    @GetMapping(value = "/executed/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsGroupByPriorityMetrics(request);
    }

    @GetMapping(value = "/executed/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsGroupByPriorityCount(request);
    }

    @GetMapping(value = "/executed/success")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedSuccessWorkItems(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedSuccessWorkItems(request);
    }

    @GetMapping(value = "/executed/success/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedSuccessWorkItemsMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedSuccessWorkItemsMetrics(request);
    }

    @GetMapping(value = "/executed/success/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedSuccessWorkItemsGroupByClassName(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedSuccessWorkItemsGroupByClassName(request);
    }

    @GetMapping(value = "/executed/success/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedSuccessWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedSuccessWorkItemsGroupByClassNameMetrics(request);
    }

    @GetMapping(value = "/executed/success/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedSuccessWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedSuccessWorkItemsGroupByClassNameCount(request);
    }

    @GetMapping(value = "/executed/success/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedSuccessWorkItemsGroupByPriority(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedSuccessWorkItemsGroupByPriority(request);
    }

    @GetMapping(value = "/executed/success/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedSuccessWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedSuccessWorkItemsGroupByPriorityMetrics(request);
    }

    @GetMapping(value = "/executed/success/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedSuccessWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return workItemInfoAPI.getCurrentExecutedSuccessWorkItemsGroupByPriorityCount(request);
    }

    @GetMapping(value = "/executed/longer/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedLongerWorkItems(HttpServletRequest request,
                                                                              @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLongerWorkItems(request, executedMs);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedLongerWorkItemsMetrics(HttpServletRequest request,
                                                                                     @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLongerWorkItemsMetrics(request, executedMs);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLongerWorkItemsGroupByClassName(HttpServletRequest request,
                                                                                                           @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLongerWorkItemsGroupByClassName(request, executedMs);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLongerWorkItemsGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                  @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLongerWorkItemsGroupByClassNameMetrics(request, executedMs);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedLongerWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                                    @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLongerWorkItemsGroupByClassNameCount(request, executedMs);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLongerWorkItemsGroupByPriority(HttpServletRequest request,
                                                                                                          @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLongerWorkItemsGroupByPriority(request, executedMs);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLongerWorkItemsGroupByPriorityMetrics(HttpServletRequest request,
                                                                                                                 @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLongerWorkItemsGroupByPriorityMetrics(request, executedMs);
    }


    @GetMapping(value = "/executed/longer/{executedMs}/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedLongerWorkItemsGroupByPriorityCount(HttpServletRequest request,
                                                                                                   @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLongerWorkItemsGroupByPriorityCount(request, executedMs);
    }

    @GetMapping(value = "/executed/less/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedLessWorkItems(HttpServletRequest request,
                                                                            @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLessWorkItems(request, executedMs);
    }

    @GetMapping(value = "/executed/less/{executedMs}/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedLessWorkItemsMetrics(HttpServletRequest request,
                                                                                   @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLessWorkItemsMetrics(request, executedMs);
    }

    @GetMapping(value = "/executed/less/{executedMs}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLessWorkItemsGroupByClassName(HttpServletRequest request,
                                                                                                         @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLessWorkItemsGroupByClassName(request, executedMs);
    }

    @GetMapping(value = "/executed/less/{executedMs}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLessWorkItemsGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLessWorkItemsGroupByClassNameMetrics(request, executedMs);
    }


    @GetMapping(value = "/executed/less/{executedMs}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedLessWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                                  @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLessWorkItemsGroupByClassNameCount(request, executedMs);
    }

    @GetMapping(value = "/executed/less/{executedMs}/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLessWorkItemsGroupByPriority(HttpServletRequest request,
                                                                                                        @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLessWorkItemsGroupByPriority(request, executedMs);
    }

    @GetMapping(value = "/executed/less/{executedMs}/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLessWorkItemsGroupByPriorityMetrics(HttpServletRequest request,
                                                                                                               @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLessWorkItemsGroupByPriorityMetrics(request, executedMs);
    }


    @GetMapping(value = "/executed/less/{executedMs}/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedLessWorkItemsGroupByPriorityCount(HttpServletRequest request,
                                                                                                 @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getCurrentExecutedLessWorkItemsGroupByPriorityCount(request, executedMs);
    }

    @GetMapping(value = "/executed/priority/{priority}")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedWorkItemsByPriority(HttpServletRequest request,
                                                                                  @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsByPriority(request, priority);
    }

    @GetMapping(value = "/executed/priority/{priority}/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedWorkItemsByPriorityMetrics(HttpServletRequest request,
                                                                                         @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsByPriorityMetrics(request, priority);
    }

    @GetMapping(value = "/executed/priority/{priority}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsByPriorityGroupByClassName(HttpServletRequest request,
                                                                                                               @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsByPriorityGroupByClassName(request, priority);
    }

    @GetMapping(value = "/executed/priority/{priority}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                      @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsByPriorityGroupByClassNameMetrics(request, priority);
    }

    @GetMapping(value = "/executed/priority/{priority}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request,
                                                                                                        @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getCurrentExecutedWorkItemsByPriorityGroupByClassNameCount(request, priority);
    }

    @GetMapping(value = "/running")
    public ResponseEntity<WorkItemInfoList> getRunningWorkItems(HttpServletRequest request) {
        return workItemInfoAPI.getRunningWorkItems(request);
    }

    @GetMapping(value = "/running/metrics")
    public ResponseEntity<WorkItemInfoList> getRunningWorkItemsMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getRunningWorkItemsMetrics(request);
    }

    @GetMapping(value = "/running/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getRunningWorkItemsGroupByClassName(HttpServletRequest request) {
        return workItemInfoAPI.getRunningWorkItemsGroupByClassName(request);
    }

    @GetMapping(value = "/running/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getRunningWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getRunningWorkItemsGroupByClassNameMetrics(request);
    }

    @GetMapping(value = "/running/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getRunningWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return workItemInfoAPI.getRunningWorkItemsGroupByClassNameCount(request);
    }

    @GetMapping(value = "/running/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getRunningWorkItemsGroupByPriority(HttpServletRequest request) {
        return workItemInfoAPI.getRunningWorkItemsGroupByPriority(request);
    }

    @GetMapping(value = "/running/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getRunningWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getRunningWorkItemsGroupByPriorityMetrics(request);
    }

    @GetMapping(value = "/running/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getRunningWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return workItemInfoAPI.getRunningWorkItemsGroupByPriorityCount(request);
    }

    @GetMapping(value = "/history")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedWorkItems(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedWorkItems(request);
    }

    @GetMapping(value = "/history/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedWorkItemsMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedWorkItemsMetrics(request);
    }

    @GetMapping(value = "/history/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedWorkItemsGroupByClassName(request);
    }

    @GetMapping(value = "/history/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedWorkItemsGroupByClassNameMetrics(request);
    }

    @GetMapping(value = "/history/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedWorkItemsGroupByClassNameCount(request);
    }

    @GetMapping(value = "/history/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedWorkItemsGroupByPriority(request);
    }

    @GetMapping(value = "/history/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedWorkItemsGroupByPriorityMetrics(request);
    }

    @GetMapping(value = "/history/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedWorkItemsGroupByPriorityCount(request);
    }

    @GetMapping(value = "/history/failed")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedFailedWorkItems(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedFailedWorkItems(request);
    }

    @GetMapping(value = "/history/failed/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedFailedWorkItemsMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedFailedWorkItemsMetrics(request);
    }

    @GetMapping(value = "/history/failed/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedFailedWorkItemsGroupByClassName(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedFailedWorkItemsGroupByClassName(request);
    }

    @GetMapping(value = "/history/failed/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedFailedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedFailedWorkItemsGroupByClassNameMetrics(request);
    }

    @GetMapping(value = "/history/failed/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedFailedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedFailedWorkItemsGroupByClassNameCount(request);
    }

    @GetMapping(value = "/history/failed/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedFailedWorkItemsGroupByPriority(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedFailedWorkItemsGroupByPriority(request);
    }

    @GetMapping(value = "/history/failed/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedFailedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedFailedWorkItemsGroupByPriorityMetrics(request);
    }

    @GetMapping(value = "/history/failed/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedFailedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryExecutedFailedWorkItemsGroupByPriorityCount(request);
    }

    @GetMapping(value = "/history/longer/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedLongerWorkItems(HttpServletRequest request,
                                                                              @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getHistoryExecutedLongerWorkItems(request, executedMs);
    }

    @GetMapping(value = "/history/longer/{executedMs}/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedLongerWorkItemsMetrics(HttpServletRequest request,
                                                                                     @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getHistoryExecutedLongerWorkItemsMetrics(request, executedMs);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedLongerWorkItemsGroupByClassName(HttpServletRequest request,
                                                                                                           @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getHistoryExecutedLongerWorkItemsGroupByClassName(request, executedMs);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedLongerWorkItemsGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                  @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getHistoryExecutedLongerWorkItemsGroupByClassNameMetrics(request, executedMs);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedLongerWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                                    @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getHistoryExecutedLongerWorkItemsGroupByClassNameCount(request, executedMs);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedLongerWorkItemsGroupByPriority(HttpServletRequest request,
                                                                                                          @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getHistoryExecutedLongerWorkItemsGroupByPriority(request, executedMs);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedLongerWorkItemsGroupByPriorityMetrics(HttpServletRequest request,
                                                                                                                 @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getHistoryExecutedLongerWorkItemsGroupByPriorityMetrics(request, executedMs);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedLongerWorkItemsGroupByPriorityCount(HttpServletRequest request,
                                                                                                   @PathVariable("executedMs") int executedMs) {
        return workItemInfoAPI.getHistoryExecutedLongerWorkItemsGroupByPriorityCount(request, executedMs);
    }

    @GetMapping(value = "/history/priority/{priority}")
    public ResponseEntity<WorkItemInfoList> getHistoryWorkItemsByPriority(HttpServletRequest request,
                                                                          @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getHistoryWorkItemsByPriority(request, priority);
    }

    @GetMapping(value = "/history/priority/{priority}/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryWorkItemsByPriorityMetrics(HttpServletRequest request,
                                                                                 @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getHistoryWorkItemsByPriorityMetrics(request, priority);
    }

    @GetMapping(value = "/history/priority/{priority}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryWorkItemsByPriorityGroupByClassName(HttpServletRequest request,
                                                                                                       @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getHistoryWorkItemsByPriorityGroupByClassName(request, priority);
    }

    @GetMapping(value = "/history/priority/{priority}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                              @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getHistoryWorkItemsByPriorityGroupByClassNameMetrics(request, priority);
    }

    @GetMapping(value = "/history/priority/{priority}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request,
                                                                                                @PathVariable("priority") WorkItemPriority priority) {
        return workItemInfoAPI.getHistoryWorkItemsByPriorityGroupByClassNameCount(request, priority);
    }

    @GetMapping(value = "/history/process")
    public ResponseEntity<WorkItemInfoList> getHistoryProcessWorkItems(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryProcessWorkItems(request);
    }

    @GetMapping(value = "/history/process/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryProcessWorkItemsMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryProcessWorkItemsMetrics(request);
    }

    @GetMapping(value = "/history/process/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryProcessWorkItemsGroupByClassName(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryProcessWorkItemsGroupByClassName(request);
    }

    @GetMapping(value = "/history/process/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryProcessWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryProcessWorkItemsGroupByClassNameMetrics(request);
    }

    @GetMapping(value = "/history/process/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryProcessWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryProcessWorkItemsGroupByClassNameCount(request);
    }

    @GetMapping(value = "/history/process/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryProcessWorkItemsGroupByPriority(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryProcessWorkItemsGroupByPriority(request);
    }

    @GetMapping(value = "/history/process/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryProcessWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryProcessWorkItemsGroupByPriorityMetrics(request);
    }

    @GetMapping(value = "/history/process/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getHistoryProcessWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        return workItemInfoAPI.getHistoryProcessWorkItemsGroupByPriorityCount(request);
    }

    @RestController
    @RequestMapping(path = "/api/secure/work-items/scheduled")
    public static class ScheduledWorkItemInfoSecureAPI {

        private final WorkItemInfoAPI.ScheduledWorkItemInfoAPI scheduledWorkItemInfoAPI;

        public ScheduledWorkItemInfoSecureAPI(WorkItemInfoAPI.ScheduledWorkItemInfoAPI scheduledWorkItemInfoAPI) {
            this.scheduledWorkItemInfoAPI = scheduledWorkItemInfoAPI;
        }

        @GetMapping(value = "")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItems(HttpServletRequest request) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItems(request);
        }

        @GetMapping(value = "/metrics")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsMetrics(HttpServletRequest request) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsMetrics(request);
        }

        @GetMapping(value = "/group-by/classes")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsGroupByClassName(request);
        }

        @GetMapping(value = "/group-by/classes/metrics")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsGroupByClassNameMetrics(request);
        }

        @GetMapping(value = "/group-by/classes/count")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsGroupByClassNameCount(request);
        }

        @GetMapping(value = "/group-by/states")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByStates(HttpServletRequest request) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsGroupByStates(request);
        }

        @GetMapping(value = "/group-by/states/metrics")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByStatesMetrics(HttpServletRequest request) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsGroupByStatesMetrics(request);
        }

        @GetMapping(value = "/group-by/states/count")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByStatesCount(HttpServletRequest request) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsGroupByStatesCount(request);
        }

        @GetMapping(value = "/states")
        public ResponseEntity<ScheduledWorkItemInfoList<TimerTaskState>> getScheduledWorkItemsStates(HttpServletRequest request) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsStates(request);
        }

        @GetMapping(value = "/states/{state}")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItems(HttpServletRequest request,
                                                                                                  @PathVariable("state") TimerTaskState state) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItems(request, state);
        }

        @GetMapping(value = "/states/{state}/metrics")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsMetrics(HttpServletRequest request,
                                                                                                         @PathVariable("state") TimerTaskState state) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsMetrics(request, state);
        }

        @GetMapping(value = "/states/{state}/group-by/classes")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request,
                                                                                                                               @PathVariable("state")TimerTaskState state) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsGroupByClassName(request, state);
        }

        @GetMapping(value = "/states/{state}/group-by/classes/metrics")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                                      @PathVariable("state")TimerTaskState state) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsGroupByClassNameMetrics(request, state);
        }

        @GetMapping(value = "/states/{state}/group-by/classes/count")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                            @PathVariable("state")TimerTaskState state) {
            return scheduledWorkItemInfoAPI.getScheduledWorkItemsGroupByClassNameCount(request, state);
        }
    }
}
