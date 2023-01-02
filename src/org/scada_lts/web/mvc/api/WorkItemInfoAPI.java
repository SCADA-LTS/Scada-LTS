package org.scada_lts.web.mvc.api;

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

    @GetMapping(value = "/")
    public ResponseEntity<WorkItemInfoList> getNotExecutedWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getNotExecutedWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/executed")
    public ResponseEntity<WorkItemInfoList> getExecutedWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/success")
    public ResponseEntity<WorkItemInfoList> getExecutedSuccessWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedSuccessWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/fail")
    public ResponseEntity<WorkItemInfoList> getExecutedFailWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedFailWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<WorkItemInfoList> getWorkItems(HttpServletRequest request) {
        List<WorkItemInfo> response = workItemInfoApiService.getWorkItems(request);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/longer/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getExecutedLongerWorkItems(HttpServletRequest request,
                                                                         @PathVariable("executedMs") int executedMs) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedLongerWorkItems(request, executedMs);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/less/{executedMs}")
    public ResponseEntity<WorkItemInfoList> getExecutedLessWorkItems(HttpServletRequest request,
                                                                     @PathVariable("executedMs") int executedMs) {
        List<WorkItemInfo> response = workItemInfoApiService.getExecutedLessWorkItems(request, executedMs);
        return new ResponseEntity<>(new WorkItemInfoList(response), HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/")
    public ResponseEntity<Map<String, Long>> getNotExecutedWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getNotExecutedWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/executed")
    public ResponseEntity<Map<String, Long>> getExecutedWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getExecutedWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/success")
    public ResponseEntity<Map<String, Long>> getExecutedSuccessWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getExecutedSuccessWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/fail")
    public ResponseEntity<Map<String, Long>> getExecutedFailWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getExecutedFailWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/all")
    public ResponseEntity<Map<String, Long>> getWorkItemsGroupBy(HttpServletRequest request) {
        Map<String, Long> response = workItemInfoApiService.getWorkItemsGroupBy(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/longer/{executedMs}")
    public ResponseEntity<Map<String, Long>> getExecutedLongerWorkItemsGroupBy(HttpServletRequest request,
                                                                                  @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getExecutedLongerWorkItemsGroupBy(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/less/{executedMs}")
    public ResponseEntity<Map<String, Long>> getExecutedLessWorkItemsGroupBy(HttpServletRequest request,
                                                                                @PathVariable("executedMs") int executedMs) {
        Map<String, Long> response = workItemInfoApiService.getExecutedLessWorkItemsGroupBy(request, executedMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
