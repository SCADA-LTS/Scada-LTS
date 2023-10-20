package org.scada_lts.web.mvc.api;

import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.scada_lts.web.mvc.api.json.ThreadInfoList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.scada_lts.utils.ThreadInfoApiUtils.Value;


@RestController
@RequestMapping(path = "/api/threads-secure")
public class ThreadInfoSecureAPI {

    private final ThreadInfoAPI threadInfoAPI;

    public ThreadInfoSecureAPI(ThreadInfoAPI threadInfoAPI) {
        this.threadInfoAPI = threadInfoAPI;
    }

    @GetMapping("")
    public ResponseEntity<ThreadInfoList<ThreadInfo>> getThreads(HttpServletRequest request) {
        return threadInfoAPI.getThreads(request);
    }

    @GetMapping(value = "/metrics")
    public ResponseEntity<ThreadInfoList<Value>> getThreadsMetrics(HttpServletRequest request) {
        return threadInfoAPI.getThreadsMetrics(request);
    }

    @GetMapping(value = "/stack")
    public ResponseEntity<ThreadInfoList<ThreadInfo.StackInfo[]>> getStackTraceElements(HttpServletRequest request) {
        return threadInfoAPI.getStackTraceElements(request);
    }

    @GetMapping(value = "/names")
    public ResponseEntity<ThreadInfoList<Value>> getThreadNames(HttpServletRequest request) {
        return threadInfoAPI.getThreadNames(request);
    }

    @GetMapping(value = "/classes")
    public ResponseEntity<ThreadInfoList<Value>> getThreadClasses(HttpServletRequest request) {
        return threadInfoAPI.getThreadClasses(request);
    }

    @GetMapping(value = "/group-by/classes")
    public ResponseEntity<Map<Value, ThreadInfoList<ThreadInfo>>> getThreadsGroupByClassName(HttpServletRequest request) {
        return threadInfoAPI.getThreadsGroupByClassName(request);
    }

    @GetMapping(value = "/group-by/classes/count")
    public ResponseEntity<Map<Value, Long>> getThreadsGroupByClassNameCount(HttpServletRequest request) {
        return threadInfoAPI.getThreadsGroupByClassNameCount(request);
    }

    @GetMapping(value = "/group-by/stack")
    public ResponseEntity<Map<List<Value>, ThreadInfoList<ThreadInfo>>> getThreadsGroupByStackThread(HttpServletRequest request) {
        return threadInfoAPI.getThreadsGroupByStackThread(request);
    }

    @GetMapping(value = "/group-by/stack/classes")
    public ResponseEntity<Map<List<Value>, ThreadInfoList<Value>>> getThreadsGroupByStackThreadClasses(HttpServletRequest request) {
        return threadInfoAPI.getThreadsGroupByStackThreadClasses(request);
    }

    @GetMapping(value = "/group-by/stack/names")
    public ResponseEntity<Map<List<Value>, ThreadInfoList<Value>>> getThreadsGroupByStackThreadNames(HttpServletRequest request) {
        return threadInfoAPI.getThreadsGroupByStackThreadNames(request);
    }

    @GetMapping(value = "/group-by/stack/count")
    public ResponseEntity<Map<List<Value>, Long>> getThreadsGroupByStackThreadCount(HttpServletRequest request) {
        return threadInfoAPI.getThreadsGroupByStackThreadCount(request);
    }

    @GetMapping(value = "/states")
    public ResponseEntity<ThreadInfoList<Value>> getStates(HttpServletRequest request) {
        return threadInfoAPI.getStates(request);
    }

    @GetMapping(value = "/states/{state}")
    public ResponseEntity<ThreadInfoList<ThreadInfo>> getThreadsForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                       HttpServletRequest request) {
        return threadInfoAPI.getThreadsForState(request, state);
    }

    @GetMapping(value = "/states/{state}/metrics")
    public ResponseEntity<ThreadInfoList<Value>> getThreadsForStateMetrics(@PathVariable(value = "state", required = true) Thread.State state,
                                                                         HttpServletRequest request) {
        return threadInfoAPI.getThreadsForStateMetrics(request, state);
    }

    @GetMapping(value = "/states/{state}/classes")
    public ResponseEntity<ThreadInfoList<Value>> getThreadClassesForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                                HttpServletRequest request) {
        return threadInfoAPI.getThreadClassesForState(request, state);
    }

    @GetMapping(value = "/states/{state}/names")
    public ResponseEntity<ThreadInfoList<Value>> getThreadNamesForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                                        HttpServletRequest request) {
        return threadInfoAPI.getThreadNamesForState(request, state);
    }

    @GetMapping(value = "/group-by/states")
    public ResponseEntity<Map<Value, ThreadInfoList<ThreadInfo>>> getThreadsGroupByState(HttpServletRequest request) {
        return threadInfoAPI.getThreadsGroupByState(request);
    }

    @GetMapping(value = "/group-by/states/classes")
    public ResponseEntity<Map<Value, ThreadInfoList<Value>>> getThreadClassesGroupByState(HttpServletRequest request) {
        return threadInfoAPI.getThreadClassesGroupByState(request);
    }

    @GetMapping(value = "/group-by/states/names")
    public ResponseEntity<Map<Value, ThreadInfoList<Value>>> getThreadNamesGroupByState(HttpServletRequest request) {
        return threadInfoAPI.getThreadNamesGroupByState(request);
    }

    @GetMapping(value = "/group-by/states/count")
    public ResponseEntity<Map<Value, Long>> getThreadsCountGroupByState(HttpServletRequest request) {
        return threadInfoAPI.getThreadsCountGroupByState(request);
    }
}
