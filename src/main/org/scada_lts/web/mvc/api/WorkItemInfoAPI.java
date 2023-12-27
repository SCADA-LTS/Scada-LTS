package org.scada_lts.web.mvc.api;

import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import com.serotonin.timer.TimerTaskState;
import org.scada_lts.web.mvc.api.json.ScheduledWorkItem;
import org.scada_lts.web.mvc.api.json.ScheduledWorkItemInfoList;
import org.scada_lts.web.mvc.api.json.WorkItemInfoList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/work-items")
public class WorkItemInfoAPI {

    private final WorkItemInfoApiService workItemInfoApiService;

    public WorkItemInfoAPI(WorkItemInfoApiService workItemInfoApiService) {
        this.workItemInfoApiService = workItemInfoApiService;
    }

    @GetMapping(value = "")
    public ResponseEntity<WorkItemInfoList> getCurrentWorkItems(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentWorkItems(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentWorkItemsMetrics(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentWorkItems(request);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}")
    public ResponseEntity<WorkItemInfoList> getCurrentWorkItemsByPriority(HttpServletRequest request,
                                                                          @PathVariable("priority") WorkItemPriority priority) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentWorkItemsByPriority(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentWorkItemsByPriorityMetrics(HttpServletRequest request,
                                                                                 @PathVariable("priority") WorkItemPriority priority) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentWorkItemsByPriority(request, priority);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsByPriorityGroupByClassName(HttpServletRequest request,
                                                                                                       @PathVariable("priority") WorkItemPriority priority) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentWorkItemsByPriorityGroupByClassName(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                              @PathVariable("priority") WorkItemPriority priority) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentWorkItemsByPriorityGroupByClassNameMetrics(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request,
                                                                                                @PathVariable("priority") WorkItemPriority priority) {
        Map<String, Long>response = workItemInfoApiService.getCurrentWorkItemsByPriorityGroupByClassNameCount(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentWorkItemsGroupByClassNameMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getCurrentWorkItemsGroupByClassNameCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsGroupByPriority(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentWorkItemsGroupByPriority(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentWorkItemsGroupByPriorityMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getCurrentWorkItemsGroupByPriorityCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/not-executed")
    public ResponseEntity<WorkItemInfoList> getCurrentNotExecutedWorkItems(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentNotExecutedWorkItems(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/not-executed/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentNotExecutedWorkItemsMetrics(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentNotExecutedWorkItems(request);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/not-executed/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentNotExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentNotExecutedWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/not-executed/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentNotExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentNotExecutedWorkItemsGroupByClassNameMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/not-executed/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentNotExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getCurrentNotExecutedWorkItemsGroupByClassNameCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/not-executed/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentNotExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentNotExecutedWorkItemsGroupByPriority(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/not-executed/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentNotExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentNotExecutedWorkItemsGroupByPriorityMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/not-executed/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentNotExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getCurrentNotExecutedWorkItemsGroupByPriorityCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedWorkItems(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedWorkItems(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedWorkItemsMetrics(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedWorkItems(request);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/executed/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedWorkItemsGroupByClassNameMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getCurrentExecutedWorkItemsGroupByClassNameCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedWorkItemsGroupByPriority(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedWorkItemsGroupByPriorityMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getCurrentExecutedWorkItemsGroupByPriorityCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/success")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedSuccessWorkItems(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedSuccessWorkItems(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/success/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedSuccessWorkItemsMetrics(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedSuccessWorkItems(request);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/executed/success/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedSuccessWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedSuccessWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/success/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedSuccessWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedSuccessWorkItemsGroupByClassNameMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/success/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedSuccessWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getCurrentExecutedSuccessWorkItemsGroupByClassNameCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/success/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedSuccessWorkItemsGroupByPriority(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedSuccessWorkItemsGroupByPriority(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/success/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedSuccessWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedSuccessWorkItemsGroupByPriorityMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/success/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedSuccessWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getCurrentExecutedSuccessWorkItemsGroupByPriorityCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/longer/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedLongerWorkItems(HttpServletRequest request,
                                                                              @PathVariable("executedMs") int executedMs) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedLongerWorkItems(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedLongerWorkItemsMetrics(HttpServletRequest request,
                                                                                     @PathVariable("executedMs") int executedMs) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedLongerWorkItems(request, executedMs);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLongerWorkItemsGroupByClassName(HttpServletRequest request,
                                                                                                           @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedLongerWorkItemsGroupByClassName(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLongerWorkItemsGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                  @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedLongerWorkItemsGroupByClassNameMetrics(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedLongerWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                                    @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getCurrentExecutedLongerWorkItemsGroupByClassNameCount(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLongerWorkItemsGroupByPriority(HttpServletRequest request,
                                                                                                          @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedLongerWorkItemsGroupByPriority(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/longer/{executedMs}/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLongerWorkItemsGroupByPriorityMetrics(HttpServletRequest request,
                                                                                                                 @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedLongerWorkItemsGroupByPriorityMetrics(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/executed/longer/{executedMs}/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedLongerWorkItemsGroupByPriorityCount(HttpServletRequest request,
                                                                                                   @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getCurrentExecutedLongerWorkItemsGroupByPriorityCount(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/less/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedLessWorkItems(HttpServletRequest request,
                                                                            @PathVariable("executedMs") int executedMs) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedLessWorkItems(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/less/{executedMs}/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedLessWorkItemsMetrics(HttpServletRequest request,
                                                                                   @PathVariable("executedMs") int executedMs) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedLessWorkItems(request, executedMs);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/executed/less/{executedMs}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLessWorkItemsGroupByClassName(HttpServletRequest request,
                                                                                                         @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedLessWorkItemsGroupByClassName(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/less/{executedMs}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLessWorkItemsGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedLessWorkItemsGroupByClassNameMetrics(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/executed/less/{executedMs}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedLessWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                                  @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getCurrentExecutedLessWorkItemsGroupByClassNameCount(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/less/{executedMs}/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLessWorkItemsGroupByPriority(HttpServletRequest request,
                                                                                                        @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedLessWorkItemsGroupByPriority(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/less/{executedMs}/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedLessWorkItemsGroupByPriorityMetrics(HttpServletRequest request,
                                                                                                               @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedLessWorkItemsGroupByPriorityMetrics(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/executed/less/{executedMs}/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedLessWorkItemsGroupByPriorityCount(HttpServletRequest request,
                                                                                                 @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getCurrentExecutedLessWorkItemsGroupByPriorityCount(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/priority/{priority}")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedWorkItemsByPriority(HttpServletRequest request,
                                                                                  @PathVariable("priority") WorkItemPriority priority) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedWorkItemsByPriority(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/priority/{priority}/metrics")
    public ResponseEntity<WorkItemInfoList> getCurrentExecutedWorkItemsByPriorityMetrics(HttpServletRequest request,
                                                                                         @PathVariable("priority") WorkItemPriority priority) {
        WorkItemInfoList response = workItemInfoApiService.getCurrentExecutedWorkItemsByPriority(request, priority);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/executed/priority/{priority}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsByPriorityGroupByClassName(HttpServletRequest request,
                                                                                                               @PathVariable("priority") WorkItemPriority priority) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedWorkItemsByPriorityGroupByClassName(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/priority/{priority}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getCurrentExecutedWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                      @PathVariable("priority") WorkItemPriority priority) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getCurrentExecutedWorkItemsByPriorityGroupByClassNameMetrics(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/priority/{priority}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getCurrentExecutedWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request,
                                                                                                        @PathVariable("priority") WorkItemPriority priority) {
        Map<String, Long> response = workItemInfoApiService.getCurrentExecutedWorkItemsByPriorityGroupByClassNameCount(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/running")
    public ResponseEntity<WorkItemInfoList> getRunningWorkItems(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getRunningWorkItems(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/running/metrics")
    public ResponseEntity<WorkItemInfoList> getRunningWorkItemsMetrics(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getRunningWorkItems(request);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/running/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getRunningWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getRunningWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/running/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getRunningWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getRunningWorkItemsGroupByClassNameMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/running/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getRunningWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getRunningWorkItemsGroupByClassNameCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/running/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getRunningWorkItemsGroupByPriority(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getRunningWorkItemsGroupByPriority(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/running/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getRunningWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getRunningWorkItemsGroupByPriorityMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/running/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getRunningWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getRunningWorkItemsGroupByPriorityCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedWorkItems(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryExecutedWorkItems(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedWorkItemsMetrics(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryExecutedWorkItems(request);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/history/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedWorkItemsGroupByClassNameMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getHistoryExecutedWorkItemsGroupByClassNameCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedWorkItemsGroupByPriority(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedWorkItemsGroupByPriority(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedWorkItemsGroupByPriorityMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getHistoryExecutedWorkItemsGroupByPriorityCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/failed")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedFailedWorkItems(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryExecutedFailedWorkItems(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/failed/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedFailedWorkItemsMetrics(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryExecutedFailedWorkItems(request);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/history/failed/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedFailedWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedFailedWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/failed/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedFailedWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedFailedWorkItemsGroupByClassNameMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/failed/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedFailedWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getHistoryExecutedFailedWorkItemsGroupByClassNameCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/failed/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedFailedWorkItemsGroupByPriority(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedFailedWorkItemsGroupByPriority(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/failed/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedFailedWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedFailedWorkItemsGroupByPriorityMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/failed/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedFailedWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getHistoryExecutedFailedWorkItemsGroupByPriorityCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedLongerWorkItems(HttpServletRequest request,
                                                                              @PathVariable("executedMs") int executedMs) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryExecutedLongerWorkItems(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryExecutedLongerWorkItemsMetrics(HttpServletRequest request,
                                                                                     @PathVariable("executedMs") int executedMs) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryExecutedLongerWorkItems(request, executedMs);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedLongerWorkItemsGroupByClassName(HttpServletRequest request,
                                                                                                           @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedLongerWorkItemsGroupByClassName(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedLongerWorkItemsGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                  @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedLongerWorkItemsGroupByClassNameMetrics(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedLongerWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                                    @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getHistoryExecutedLongerWorkItemsGroupByClassNameCount(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedLongerWorkItemsGroupByPriority(HttpServletRequest request,
                                                                                                          @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedLongerWorkItemsGroupByPriority(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryExecutedLongerWorkItemsGroupByPriorityMetrics(HttpServletRequest request,
                                                                                                                 @PathVariable("executedMs") int executedMs) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryExecutedLongerWorkItemsGroupByPriorityMetrics(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getHistoryExecutedLongerWorkItemsGroupByPriorityCount(HttpServletRequest request,
                                                                                                   @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getHistoryExecutedLongerWorkItemsGroupByPriorityCount(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/priority/{priority}")
    public ResponseEntity<WorkItemInfoList> getHistoryWorkItemsByPriority(HttpServletRequest request,
                                                                          @PathVariable("priority") WorkItemPriority priority) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryWorkItemsByPriority(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/priority/{priority}/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryWorkItemsByPriorityMetrics(HttpServletRequest request,
                                                                                 @PathVariable("priority") WorkItemPriority priority) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryWorkItemsByPriority(request, priority);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/history/priority/{priority}/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryWorkItemsByPriorityGroupByClassName(HttpServletRequest request,
                                                                                                       @PathVariable("priority") WorkItemPriority priority) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryWorkItemsByPriorityGroupByClassName(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/priority/{priority}/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryWorkItemsByPriorityGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                              @PathVariable("priority") WorkItemPriority priority) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryWorkItemsByPriorityGroupByClassNameMetrics(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/priority/{priority}/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryWorkItemsByPriorityGroupByClassNameCount(HttpServletRequest request,
                                                                                                @PathVariable("priority") WorkItemPriority priority) {
        Map<String, Long>response = workItemInfoApiService.getHistoryWorkItemsByPriorityGroupByClassNameCount(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/process")
    public ResponseEntity<WorkItemInfoList> getHistoryProcessWorkItems(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryProcessWorkItems(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/process/metrics")
    public ResponseEntity<WorkItemInfoList> getHistoryProcessWorkItemsMetrics(HttpServletRequest request) {
        WorkItemInfoList response = workItemInfoApiService.getHistoryProcessWorkItems(request);
        return new ResponseEntity<>(response.onlyMetrics(), HttpStatus.OK);
    }

    @GetMapping(value = "/history/process/group-by/classes")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryProcessWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryProcessWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/process/group-by/classes/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryProcessWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryProcessWorkItemsGroupByClassNameMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/process/group-by/classes/count")
    public ResponseEntity<Map<String, Long>> getHistoryProcessWorkItemsGroupByClassNameCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getHistoryProcessWorkItemsGroupByClassNameCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/process/group-by/priority")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryProcessWorkItemsGroupByPriority(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryProcessWorkItemsGroupByPriority(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/process/group-by/priority/metrics")
    public ResponseEntity<Map<String, WorkItemInfoList>> getHistoryProcessWorkItemsGroupByPriorityMetrics(HttpServletRequest request) {
        Map<String, WorkItemInfoList> response = workItemInfoApiService.getHistoryProcessWorkItemsGroupByPriorityMetrics(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/process/group-by/priority/count")
    public ResponseEntity<Map<String, Long>> getHistoryProcessWorkItemsGroupByPriorityCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getHistoryProcessWorkItemsGroupByPriorityCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RestController
    @RequestMapping(path = "/api/work-items/scheduled")
    public static class ScheduledWorkItemInfoAPI {

        private final ScheduledWorkItemInfoApiService scheduledWorkItemInfoApiService;

        public ScheduledWorkItemInfoAPI(ScheduledWorkItemInfoApiService scheduledWorkItemInfoApiService) {
            this.scheduledWorkItemInfoApiService = scheduledWorkItemInfoApiService;
        }

        @GetMapping(value = "")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItems(HttpServletRequest request) {
            ScheduledWorkItemInfoList<ScheduledWorkItem> response = scheduledWorkItemInfoApiService.getScheduledWorkItems(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/metrics")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsMetrics(HttpServletRequest request) {
            ScheduledWorkItemInfoList<ScheduledWorkItem> response = scheduledWorkItemInfoApiService.getScheduledWorkItems(request);
            return new ResponseEntity<>(new ScheduledWorkItemInfoList<>(response.getSize()), HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/classes")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request) {
            Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassName(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/classes/metrics")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassNameMetrics(HttpServletRequest request) {
            Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassNameMetrics(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/classes/count")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request) {
            Map<String, Long> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassNameCount(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/states")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByStates(HttpServletRequest request) {
            Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByStates(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/states/metrics")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByStatesMetrics(HttpServletRequest request) {
            Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByStatesMetrics(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/states/count")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByStatesCount(HttpServletRequest request) {
            Map<String, Long> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByStatesCount(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/states")
        public ResponseEntity<ScheduledWorkItemInfoList<TimerTaskState>> getScheduledWorkItemsStates(HttpServletRequest request) {
            ScheduledWorkItemInfoList<TimerTaskState> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsStates(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/states/{state}")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItems(HttpServletRequest request,
                                                                                                  @PathVariable("state") TimerTaskState state) {
            ScheduledWorkItemInfoList<ScheduledWorkItem> response = scheduledWorkItemInfoApiService.getScheduledWorkItems(request, state);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/states/{state}/metrics")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItemsMetrics(HttpServletRequest request,
                                                                                                         @PathVariable("state") TimerTaskState state) {
            ScheduledWorkItemInfoList<ScheduledWorkItem> response = scheduledWorkItemInfoApiService.getScheduledWorkItems(request, state);
            return new ResponseEntity<>(new ScheduledWorkItemInfoList<>(response.getSize()), HttpStatus.OK);
        }

        @GetMapping(value = "/states/{state}/group-by/classes")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request,
                                                                                                                               @PathVariable("state")TimerTaskState state) {
            Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassName(request, state);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/states/{state}/group-by/classes/metrics")
        public ResponseEntity<Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassNameMetrics(HttpServletRequest request,
                                                                                                                                      @PathVariable("state")TimerTaskState state) {
            Map<String, ScheduledWorkItemInfoList<ScheduledWorkItem>> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassNameMetrics(request, state);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/states/{state}/group-by/classes/count")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                            @PathVariable("state")TimerTaskState state) {
            Map<String, Long> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassNameCount(request, state);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
