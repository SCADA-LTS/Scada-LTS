package org.scada_lts.web.mvc.api;

import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.scada_lts.utils.ThreadInfoApiUtils.*;


@RestController
@RequestMapping(path = "/api/threads")
public class ThreadInfoAPI {

    private final ThreadInfoApiService threadInfoApiService;

    public ThreadInfoAPI(ThreadInfoApiService threadInfoApiService) {
        this.threadInfoApiService = threadInfoApiService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ThreadInfo>> getThreads(HttpServletRequest request) {
        List<ThreadInfo> response = threadInfoApiService.getThreads(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/stack/")
    public ResponseEntity<List<ThreadInfo.StackInfo[]>> getStackTraceElements(HttpServletRequest request) {
        List<ThreadInfo.StackInfo[]> response = threadInfoApiService.getStackTraceElements(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/names/")
    public ResponseEntity<List<Value>> getThreadNames(HttpServletRequest request) {
        List<Value> response = threadInfoApiService.getThreadNames(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/count/")
    public ResponseEntity<Map<Value, Long>> getThreadsGroupByClassCount(HttpServletRequest request) {
        Map<Value, Long> response = threadInfoApiService.getThreadsGroupByClassCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/thread-stack/")
    public ResponseEntity<Map<ThreadInfo, ThreadInfo.StackInfo[]>> getThreadsGroupByThreadStack(HttpServletRequest request) {
        Map<ThreadInfo, ThreadInfo.StackInfo[]> response = threadInfoApiService.getThreadsGroupByThreadStack(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/thread-stack/classes/")
    public ResponseEntity<Map<ThreadInfo, String[]>> getThreadsGroupByThreadStackClasses(HttpServletRequest request) {
        Map<ThreadInfo, String[]> response = threadInfoApiService.getThreadsGroupByThreadStackClasses(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/thread-stack/count/")
    public ResponseEntity<Map<ThreadInfo, Integer>> getThreadsGroupByThreadStackCount(HttpServletRequest request) {
        Map<ThreadInfo, Integer> response = threadInfoApiService.getThreadsGroupByThreadStackCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack-thread/")
    public ResponseEntity<Map<List<Value>, List<ThreadInfo>>> getThreadsGroupByStackThread(HttpServletRequest request) {
        Map<List<Value>, List<ThreadInfo>> response = threadInfoApiService.getThreadsGroupByStackThread(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack-thread/classes/")
    public ResponseEntity<Map<List<Value>, List<Value>>> getThreadsGroupByStackThreadClasses(HttpServletRequest request) {
        Map<List<Value>, List<Value>> response = threadInfoApiService.getThreadsGroupByStackThreadClasses(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack-thread/names/")
    public ResponseEntity<Map<List<Value>, List<Value>>> getThreadsGroupByStackThreadNames(HttpServletRequest request) {
        Map<List<Value>, List<Value>> response = threadInfoApiService.getThreadsGroupByStackThreadNames(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/group-by/stack-thread/count/")
    public ResponseEntity<Map<List<Value>, Long>> getThreadsGroupByStackThreadCount(HttpServletRequest request) {
        Map<List<Value>, Long> response = threadInfoApiService.getThreadsGroupByStackThreadCount(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
