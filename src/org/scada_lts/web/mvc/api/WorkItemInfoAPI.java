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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/work-items")
public class WorkItemInfoAPI {

    private final WorkItemInfoApiService workItemInfoApiService;

    public WorkItemInfoAPI(WorkItemInfoApiService workItemInfoApiService) {
        this.workItemInfoApiService = workItemInfoApiService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<WorkItemInfoList> getNotExecutedWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getNotExecutedWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/")
    public ResponseEntity<Map<String, Long>> getNotExecutedWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getNotExecutedWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/executed/")
    public ResponseEntity<WorkItemInfoList> getExecutedWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/executed/group-by/")
    public ResponseEntity<Map<String, Long>> getExecutedWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getExecutedWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/success/")
    public ResponseEntity<WorkItemInfoList> getExecutedSuccessWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedSuccessWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/success/group-by/")
    public ResponseEntity<Map<String, Long>> getExecutedSuccessWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getExecutedSuccessWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/fail/")
    public ResponseEntity<WorkItemInfoList> getExecutedFailWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedFailWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/fail/group-by/")
    public ResponseEntity<Map<String, Long>> getExecutedFailWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getExecutedFailWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/all/")
    public ResponseEntity<WorkItemInfoList> getWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/all/group-by/")
    public ResponseEntity<Map<String, Long>> getWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Deprecated
    @GetMapping(value = "/group-by/all/")
    public ResponseEntity<Map<String, Long>> getWorkItemsGroupBy2(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Deprecated
    @GetMapping(value = "/limit/{limit}/")
    public ResponseEntity<List<WorkItemInfo>> setWorkItemsLimit(@PathVariable(value = "limit", required = true) Integer limit) {
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }

    @GetMapping(value = "/longer/{executedMs}/")
    public ResponseEntity<WorkItemInfoList> getExecutedLongerWorkItems(HttpServletRequest request,
                                                                       @PathVariable("executedMs") int executedMs) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedLongerWorkItems(request, executedMs);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/longer/{executedMs}/group-by/")
    public ResponseEntity<Map<String, Long>> getExecutedLongerWorkItemsGroupBy(HttpServletRequest request,
                                                                               @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getExecutedLongerWorkItemsGroupBy(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/less/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getExecutedLessWorkItems(HttpServletRequest request,
                                                                     @PathVariable("executedMs") int executedMs) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedLessWorkItems(request, executedMs);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/less/{executedMs}/group-by/")
    public ResponseEntity<Map<String, Long>> getExecutedLessWorkItemsGroupBy(HttpServletRequest request,
                                                                             @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getExecutedLessWorkItemsGroupBy(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}")
    public ResponseEntity<WorkItemInfoList> getExecutedWorkItemsByPriority(HttpServletRequest request,
                                                                           @PathVariable("priority") WorkItemPriority priority) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedWorkItemsByPriority(request, priority);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/priority/{priority}/group-by/")
    public ResponseEntity<Map<String, Long>> getExecutedLessWorkItemsGroupByPriority(HttpServletRequest request,
                                                                                     @PathVariable("priority") WorkItemPriority priority) {
        Map<String, Long> response = workItemInfoApiService.getExecutedLessWorkItemsGroupByPriority(request, priority);
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
        public ResponseEntity<ScheduledWorkItemInfoList> getScheduledWorkItems(HttpServletRequest request) {
            List<ScheduledWorkItem> response = scheduledWorkItemInfoApiService.getScheduledWorkItems(request);
            return new ResponseEntity<>(new ScheduledWorkItemInfoList(response), HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request) {
            Map<String, Long> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassName(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/state/{state}/")
        public ResponseEntity<ScheduledWorkItemInfoList> getScheduledWorkItems(HttpServletRequest request,
                                                                               @PathVariable("state") TimerTaskState state) {
            List<ScheduledWorkItem> response = scheduledWorkItemInfoApiService.getScheduledWorkItems(request, state);
            return new ResponseEntity<>(new ScheduledWorkItemInfoList(response), HttpStatus.OK);
        }

        @GetMapping(value = "/state/{state}/group-by/")
        public ResponseEntity<Map<String, Long>> getScheduledWorkItemsGroupByClassNameCount(HttpServletRequest request,
                                                                                            @PathVariable("state")TimerTaskState state) {
            Map<String, Long> response = scheduledWorkItemInfoApiService.getScheduledWorkItemsGroupByClassName(request, state);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
