package org.scada_lts.web.mvc.api;

import org.scada_lts.web.mvc.api.json.ThreadInfo;
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

    @RestController
    @RequestMapping(path = "/api/threads/states/")
    public static class StateThreadInfoAPI {

        private final StateThreadInfoApiService stateThreadInfoApiService;

        public StateThreadInfoAPI(StateThreadInfoApiService stateThreadInfoApiService) {
            this.stateThreadInfoApiService = stateThreadInfoApiService;
        }

        @GetMapping(value = "/")
        public ResponseEntity<List<Value>> getStates(HttpServletRequest request) {
            List<Value> response = stateThreadInfoApiService.getStates(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/state/{state}/")
        public ResponseEntity<List<ThreadInfo>> getThreadsForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                                   HttpServletRequest request) {
            List<ThreadInfo> response = stateThreadInfoApiService.getThreadsForState(state, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/state/{state}/count/")
        public ResponseEntity<Long> getThreadsCountForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                            HttpServletRequest request) {
            Long response = stateThreadInfoApiService.getThreadsCountForState(state, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/state/{state}/classes/")
        public ResponseEntity<List<Value>> getThreadClassesForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                                    HttpServletRequest request) {
            List<Value> response = stateThreadInfoApiService.getThreadClassesForState(state, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/state/{state}/names/")
        public ResponseEntity<List<Value>> getThreadNamesForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                                  HttpServletRequest request) {
            List<Value> response = stateThreadInfoApiService.getThreadNamesForState(state, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/")
        public ResponseEntity<Map<Value, List<ThreadInfo>>> getThreadsGroupByState(HttpServletRequest request) {
            Map<Value, List<ThreadInfo>> response = stateThreadInfoApiService.getThreadsGroupByState(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/classes/")
        public ResponseEntity<Map<Value, List<Value>>> getThreadClassesGroupByState(HttpServletRequest request) {
            Map<Value, List<Value>> response = stateThreadInfoApiService.getThreadClassesGroupByState(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/names/")
        public ResponseEntity<Map<Value, List<Value>>> getThreadNamesGroupByState(HttpServletRequest request) {
            Map<Value, List<Value>> response = stateThreadInfoApiService.getThreadNamesGroupByState(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping(value = "/group-by/count/")
        public ResponseEntity<Map<Value, Long>> getThreadsCountGroupByState(HttpServletRequest request) {
            Map<Value, Long> response = stateThreadInfoApiService.getThreadsCountGroupByState(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
