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
        List<ThreadInfo> response = threadInfoApiService.getThreads(request);
        return new ResponseEntity<>(new ThreadInfoList<>(response), HttpStatus.OK);
    }

    @GetMapping(value = "/stack")
    public ResponseEntity<ThreadInfoList<ThreadInfo.StackInfo[]>> getStackTraceElements(HttpServletRequest request) {
        List<ThreadInfo.StackInfo[]> response = threadInfoApiService.getStackTraceElements(request);
        return new ResponseEntity<>(new ThreadInfoList<>(response), HttpStatus.OK);
    }

    @GetMapping(value = "/names")
    public ResponseEntity<ThreadInfoList<Value>> getThreadNames(HttpServletRequest request) {
        List<Value> response = threadInfoApiService.getThreadNames(request);
        return new ResponseEntity<>(new ThreadInfoList<>(response), HttpStatus.OK);
    }

    @GetMapping(value = "/classes")
    public ResponseEntity<ThreadInfoList<Value>> getThreadClasses(HttpServletRequest request) {
        List<Value> response = threadInfoApiService.getThreadClasses(request);
        return new ResponseEntity<>(new ThreadInfoList<>(response), HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/classes/count")
    public ResponseEntity<Map<Value, Long>> getThreadsGroupByClassCount(HttpServletRequest request) {
        Map<Value, Long> response = threadInfoApiService.getThreadsGroupByClassCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack")
    public ResponseEntity<Map<List<Value>, List<ThreadInfo>>> getThreadsGroupByStackThread(HttpServletRequest request) {
        Map<List<Value>, List<ThreadInfo>> response = threadInfoApiService.getThreadsGroupByStackThread(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack/classes")
    public ResponseEntity<Map<List<Value>, List<Value>>> getThreadsGroupByStackThreadClasses(HttpServletRequest request) {
        Map<List<Value>, List<Value>> response = threadInfoApiService.getThreadsGroupByStackThreadClasses(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack/names")
    public ResponseEntity<Map<List<Value>, List<Value>>> getThreadsGroupByStackThreadNames(HttpServletRequest request) {
        Map<List<Value>, List<Value>> response = threadInfoApiService.getThreadsGroupByStackThreadNames(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack/count")
    public ResponseEntity<Map<List<Value>, Long>> getThreadsGroupByStackThreadCount(HttpServletRequest request) {
        Map<List<Value>, Long> response = threadInfoApiService.getThreadsGroupByStackThreadCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/states")
    public ResponseEntity<ThreadInfoList<Value>> getStates(HttpServletRequest request) {
        List<Value> response = stateThreadInfoApiService.getStates(request);
        return new ResponseEntity<>(new ThreadInfoList<>(response), HttpStatus.OK);
    }

    @GetMapping(value = "/states/{state}")
    public ResponseEntity<ThreadInfoList<ThreadInfo>> getThreadsForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                       HttpServletRequest request) {
        List<ThreadInfo> response = stateThreadInfoApiService.getThreadsForState(state, request);
        return new ResponseEntity<>(new ThreadInfoList<>(response), HttpStatus.OK);
    }

    @GetMapping(value = "/states/{state}/classes")
    public ResponseEntity<ThreadInfoList<Value>> getThreadClassesForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                                HttpServletRequest request) {
        List<Value> response = stateThreadInfoApiService.getThreadClassesForState(state, request);
        return new ResponseEntity<>(new ThreadInfoList<>(response), HttpStatus.OK);
    }

    @GetMapping(value = "/states/{state}/names")
    public ResponseEntity<ThreadInfoList<Value>> getThreadNamesForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                              HttpServletRequest request) {
        List<Value> response = stateThreadInfoApiService.getThreadNamesForState(state, request);
        return new ResponseEntity<>(new ThreadInfoList<>(response), HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/states")
    public ResponseEntity<Map<Value, List<ThreadInfo>>> getThreadsGroupByState(HttpServletRequest request) {
        Map<Value, List<ThreadInfo>> response = stateThreadInfoApiService.getThreadsGroupByState(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/states/classes")
    public ResponseEntity<Map<Value, List<Value>>> getThreadClassesGroupByState(HttpServletRequest request) {
        Map<Value, List<Value>> response = stateThreadInfoApiService.getThreadClassesGroupByState(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/states/names")
    public ResponseEntity<Map<Value, List<Value>>> getThreadNamesGroupByState(HttpServletRequest request) {
        Map<Value, List<Value>> response = stateThreadInfoApiService.getThreadNamesGroupByState(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/states/count")
    public ResponseEntity<Map<Value, Long>> getThreadsCountGroupByState(HttpServletRequest request) {
        Map<Value, Long> response = stateThreadInfoApiService.getThreadsCountGroupByState(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
