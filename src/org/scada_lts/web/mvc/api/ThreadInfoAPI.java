package org.scada_lts.web.mvc.api;

import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.scada_lts.web.mvc.api.json.ThreadInfoList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.scada_lts.utils.ThreadInfoApiUtils.*;


@RestController
@RequestMapping(path = "/api/threads")
public class ThreadInfoAPI {

    private final ThreadInfoApiService threadInfoApiService;
    private final StateThreadInfoApiService stateThreadInfoApiService;

    public ThreadInfoAPI(ThreadInfoApiService threadInfoApiService, StateThreadInfoApiService stateThreadInfoApiService) {
        this.threadInfoApiService = threadInfoApiService;
        this.stateThreadInfoApiService = stateThreadInfoApiService;
    }

    @GetMapping(value = "")
    public ResponseEntity<ThreadInfoList<ThreadInfo>> getThreads(HttpServletRequest request) {
        ThreadInfoList<ThreadInfo> response = threadInfoApiService.getThreadsInfo(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/metrics")
    public ResponseEntity<ThreadInfoList<Value>> getThreadsMetrics(HttpServletRequest request) {
        ThreadInfoList<Value> response = threadInfoApiService.getThreadNames(request);
        return new ResponseEntity<>(new ThreadInfoList<>(response.getSize()), HttpStatus.OK);
    }

    @GetMapping(value = "/stack")
    public ResponseEntity<ThreadInfoList<ThreadInfo.StackInfo[]>> getStackTraceElements(HttpServletRequest request) {
        List<ThreadInfo.StackInfo[]> response = threadInfoApiService.getStackTraceElements(request);
        return new ResponseEntity<>(new ThreadInfoList<>(response), HttpStatus.OK);
    }

    @GetMapping(value = "/names")
    public ResponseEntity<ThreadInfoList<Value>> getThreadNames(HttpServletRequest request) {
        ThreadInfoList<Value> response = threadInfoApiService.getThreadNames(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/classes")
    public ResponseEntity<ThreadInfoList<Value>> getThreadClasses(HttpServletRequest request) {
        ThreadInfoList<Value> response = threadInfoApiService.getThreadClasses(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/classes")
    public ResponseEntity<Map<Value, ThreadInfoList<ThreadInfo>>> getThreadsGroupByClassName(HttpServletRequest request) {
        Map<Value, ThreadInfoList<ThreadInfo>> response = threadInfoApiService.getThreadsGroupByClassName(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/classes/count")
    public ResponseEntity<Map<Value, Long>> getThreadsGroupByClassNameCount(HttpServletRequest request) {
        Map<Value, Long> response = threadInfoApiService.getThreadsGroupByClassNameCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack")
    public ResponseEntity<Map<List<Value>, ThreadInfoList<ThreadInfo>>> getThreadsGroupByStackThread(HttpServletRequest request) {
        Map<List<Value>, ThreadInfoList<ThreadInfo>> response = threadInfoApiService.getThreadsGroupByStack(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack/classes")
    public ResponseEntity<Map<List<Value>, ThreadInfoList<Value>>> getThreadsGroupByStackThreadClasses(HttpServletRequest request) {
        Map<List<Value>, ThreadInfoList<Value>> response = threadInfoApiService.getThreadsGroupByStackClasses(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack/names")
    public ResponseEntity<Map<List<Value>, ThreadInfoList<Value>>> getThreadsGroupByStackThreadNames(HttpServletRequest request) {
        Map<List<Value>, ThreadInfoList<Value>> response = threadInfoApiService.getThreadsGroupByStackNames(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack/count")
    public ResponseEntity<Map<List<Value>, Long>> getThreadsGroupByStackThreadCount(HttpServletRequest request) {
        Map<List<Value>, Long> response = threadInfoApiService.getThreadsGroupByStackCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/states")
    public ResponseEntity<ThreadInfoList<Value>> getStates(HttpServletRequest request) {
        ThreadInfoList<Value> response = stateThreadInfoApiService.getStates(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/states/{state}")
    public ResponseEntity<ThreadInfoList<ThreadInfo>> getThreadsForState(HttpServletRequest request,
                                                                         @PathVariable(value = "state", required = true) Thread.State state) {
        ThreadInfoList<ThreadInfo> response = stateThreadInfoApiService.getThreadsForState(request, state);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/states/{state}/metrics")
    public ResponseEntity<ThreadInfoList<Value>> getThreadsForStateMetrics(HttpServletRequest request,
                                                                           @PathVariable(value = "state", required = true) Thread.State state) {
        ThreadInfoList<Value> response = stateThreadInfoApiService.getThreadsForStateNames(request, state);
        return new ResponseEntity<>(new ThreadInfoList<>(response.getSize()), HttpStatus.OK);
    }

    @GetMapping(value = "/states/{state}/classes")
    public ResponseEntity<ThreadInfoList<Value>> getThreadClassesForState(HttpServletRequest request,
                                                                          @PathVariable(value = "state", required = true) Thread.State state) {
        ThreadInfoList<Value> response = stateThreadInfoApiService.getThreadsForStateClasses(request, state);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/states/{state}/names")
    public ResponseEntity<ThreadInfoList<Value>> getThreadNamesForState(HttpServletRequest request,
                                                                        @PathVariable(value = "state", required = true) Thread.State state) {
        ThreadInfoList<Value> response = stateThreadInfoApiService.getThreadsForStateNames(request, state);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/states")
    public ResponseEntity<Map<Value, ThreadInfoList<ThreadInfo>>> getThreadsGroupByState(HttpServletRequest request) {
        Map<Value, ThreadInfoList<ThreadInfo>> response = stateThreadInfoApiService.getThreadsGroupByStates(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/states/classes")
    public ResponseEntity<Map<Value, ThreadInfoList<Value>>> getThreadClassesGroupByState(HttpServletRequest request) {
        Map<Value, ThreadInfoList<Value>> response = stateThreadInfoApiService.getThreadsGroupByStatesClasses(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/states/names")
    public ResponseEntity<Map<Value, ThreadInfoList<Value>>> getThreadNamesGroupByState(HttpServletRequest request) {
        Map<Value, ThreadInfoList<Value>> response = stateThreadInfoApiService.getThreadsGroupByStatesNames(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/states/count")
    public ResponseEntity<Map<Value, Long>> getThreadsCountGroupByState(HttpServletRequest request) {
        Map<Value, Long> response = stateThreadInfoApiService.getThreadsGroupByStatesCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
