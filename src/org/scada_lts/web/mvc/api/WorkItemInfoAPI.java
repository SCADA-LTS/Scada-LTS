package org.scada_lts.web.mvc.api;

import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import com.serotonin.timer.TimerTaskState;
import org.scada_lts.web.mvc.api.json.ScheduledWorkItem;
import org.scada_lts.web.mvc.api.json.ScheduledWorkItemInfoList;
import org.scada_lts.web.mvc.api.json.WorkItemInfo;
import org.scada_lts.web.mvc.api.json.WorkItemInfoList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/work-items")
public class WorkItemInfoAPI {

    private final WorkItemInfoApiService workItemInfoApiService;

    public WorkItemInfoAPI(WorkItemInfoApiService workItemInfoApiService) {
        this.workItemInfoApiService = workItemInfoApiService;
    }

    @GetMapping(value = "")
    public ResponseEntity<WorkItemInfoList> getNotExecutedWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getNotExecutedWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getNotExecutedWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getNotExecutedWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getNotExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getNotExecutedWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed")
    public ResponseEntity<WorkItemInfoList> getExecutedWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/executed/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getExecutedWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getExecutedWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getExecutedWorkItemsGroupByClassName(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getExecutedWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/success")
    public ResponseEntity<WorkItemInfoList> getExecutedSuccessWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedSuccessWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/success/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getExecutedSuccessWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getExecutedSuccessWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/success/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getExecutedSuccessWorkItemsGroupByCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getExecutedSuccessWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/failed")
    public ResponseEntity<WorkItemInfoList> getExecutedFailWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedFailedWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/failed/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getExecutedFailWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getExecutedFailedWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/failed/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getExecutedFailWorkItemsGroupByCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getExecutedFailedWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/running")
    public ResponseEntity<WorkItemInfoList> getRunningWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getRunningWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/running/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getRunningFailWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getRunningWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/running/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getRunningFailWorkItemsGroupByCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getRunningWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<WorkItemInfoList> getWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/all/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/all/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getWorkItemsGroupByCount(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getWorkItemsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/longer/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getExecutedLongerWorkItems(HttpServletRequest request,
                                                                       @PathVariable("executedMs") int executedMs) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedLongerWorkItems(request, executedMs, false);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/longer/{executedMs}/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getExecutedLongerWorkItemsGroupBy(HttpServletRequest request,
                                                                               @PathVariable("executedMs") int executedMs) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getExecutedLongerWorkItemsGroupBy(request, executedMs, false);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/longer/{executedMs}/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getExecutedLongerWorkItemsGroupByCount(HttpServletRequest request,
                                                                               @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getExecutedLongerWorkItemsGroupByClassName(request, executedMs, false);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getExecutedLongerWorkItemsHistory(HttpServletRequest request,
                                                                              @PathVariable("executedMs") int executedMs) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedLongerWorkItems(request, executedMs, true);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getExecutedLongerWorkItemsGroupByHistory(HttpServletRequest request,
                                                                                                    @PathVariable("executedMs") int executedMs) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getExecutedLongerWorkItemsGroupBy(request, executedMs, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history/longer/{executedMs}/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getExecutedLongerWorkItemsGroupByCountHistory(HttpServletRequest request,
                                                                                    @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getExecutedLongerWorkItemsGroupByClassName(request, executedMs, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/less/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getExecutedLessWorkItems(HttpServletRequest request,
                                                                     @PathVariable("executedMs") int executedMs) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedLessWorkItems(request, executedMs);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/less/{executedMs}/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getExecutedLessWorkItemsGroupBy(HttpServletRequest request,
                                                                             @PathVariable("executedMs") int executedMs) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getExecutedLessWorkItemsGroupBy(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/less/{executedMs}/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getExecutedLessWorkItemsGroupByCount(HttpServletRequest request,
                                                                             @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getExecutedLessWorkItemsGroupByClassName(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}")
    public ResponseEntity<WorkItemInfoList> getExecutedWorkItemsByPriority(HttpServletRequest request,
                                                                           @PathVariable("priority") WorkItemPriority priority) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedWorkItemsByPriority(request, priority);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}/group-by-classes")
    public ResponseEntity<Map<String, List<WorkItemInfo>>> getExecutedLessWorkItemsGroupByPriority(HttpServletRequest request,
                                                                                          @PathVariable("priority") WorkItemPriority priority) {
        Map<String, List<WorkItemInfo>> response = workItemInfoApiService.getExecutedLessWorkItemsGroupByPriority(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}/group-by-classes/count")
    public ResponseEntity<Map<String, Long>> getExecutedLessWorkItemsGroupByPriorityCount(HttpServletRequest request,
                                                                                     @PathVariable("priority") WorkItemPriority priority) {
        Map<String, Long> response = workItemInfoApiService.getExecutedLessWorkItemsGroupByPriorityClassName(request, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RestController
    @RequestMapping(path = "/api/work-items/scheduled")
    public static class ScheduledWorkItemInfoAPI {

        private final ScheduledWorkItemInfoApiService scheduledWorkItemInfoApiService;

        public ScheduledWorkItemInfoAPI(ScheduledWorkItemInfoApiService scheduledWorkItemInfoApiService) {
            this.scheduledWorkItemInfoApiService = scheduledWorkItemInfoApiService;
        }

        @GetMapping(value = "/")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItems(HttpServletRequest request) {
            List<ScheduledWorkItem> response = scheduledWorkItemInfoApiService.getScheduledWorkItems(request);
            return new ResponseEntity<>(new ScheduledWorkItemInfoList<>(response), HttpStatus.OK);
        }

        @GetMapping(value = "/group-by-classes")
        public ResponseEntity<Map<String, List<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request) {
            Map<String, List<ScheduledWorkItem>> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassName(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by-classes/count")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request) {
            Map<String, Long> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassNameCount(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/states")
        public ResponseEntity<ScheduledWorkItemInfoList<TimerTaskState>> getScheduledWorkItemsStates(HttpServletRequest request) {
            List<TimerTaskState> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsStates(request);
            return new ResponseEntity<>(new ScheduledWorkItemInfoList<>(response), HttpStatus.OK);
        }

        @GetMapping(value = "/states/{state}")
        public ResponseEntity<ScheduledWorkItemInfoList<ScheduledWorkItem>> getScheduledWorkItems(HttpServletRequest request,
                                                                               @PathVariable("state") TimerTaskState state) {
            List<ScheduledWorkItem> response = scheduledWorkItemInfoApiService.getScheduledWorkItems(request, state);
            return new ResponseEntity<>(new ScheduledWorkItemInfoList<>(response), HttpStatus.OK);
        }

        @GetMapping(value = "/states/{state}/group-by-classes")
        public ResponseEntity<Map<String, List<ScheduledWorkItem>>> getScheduledWorkItemsGroupByClassName(HttpServletRequest request,
                                                                                            @PathVariable("state")TimerTaskState state) {
            Map<String, List<ScheduledWorkItem>> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassName(request, state);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/states/{state}/group-by-classes/count")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                            @PathVariable("state")TimerTaskState state) {
            Map<String, Long> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassNameCount(request, state);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
