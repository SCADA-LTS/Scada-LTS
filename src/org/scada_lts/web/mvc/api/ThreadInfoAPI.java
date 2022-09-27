package org.scada_lts.web.mvc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.scada_lts.utils.ThreadInfoApiUtils.*;


@RestController
@RequestMapping(path = "/api/threads")
public class ThreadInfoAPI {

    private static final Log LOG = LogFactory.getLog(ThreadInfoAPI.class);

    @GetMapping(value = "/")
    public ResponseEntity<List<ThreadInfo>> getThreads() {
        try {
            return new ResponseEntity<>(getThreadStack().keySet().stream()
                    .sorted(Comparator.comparing(Thread::getName))
                    .map(ThreadInfo::new)
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/stack/")
    public ResponseEntity<Collection<ThreadInfo.StackInfo[]>> getStackTraceElements() {
        try {
            return new ResponseEntity<>(getThreadStack().values().stream()
                    .map(stackTrace -> Stream.of(stackTrace)
                            .map(ThreadInfo.StackInfo::new)
                            .sorted(Comparator.comparing(ThreadInfo.StackInfo::getClassName))
                            .toArray(ThreadInfo.StackInfo[]::new))
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/names/")
    public ResponseEntity<List<Value>> getThreadNames() {
        try {
            return new ResponseEntity<>(getThreadStack().keySet().stream()
                    .map(Thread::getName)
                    .map(Value::new)
                    .sorted(Comparator.comparing(Value::getValue))
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
/*
    @GetMapping(value = "/group-by/name/")
    public ResponseEntity<Map<Value, List<Value>>> getThreadsGroupByName() {
        try {
            Map<Value, List<Value>> threadTypeCount = groupByAndSort(getThreadStack(),
                    Collectors.groupingBy(thread -> new Value(thread.getKey().getClass().getName()), Collectors.toList()),
                    Comparator.comparingInt(a -> a.getValue().size()));
            return new ResponseEntity<>(threadTypeCount, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    @GetMapping(value = "/group-by/count/")
    public ResponseEntity<Map<Value, Long>> getThreadsGroupByCount() {
        try {
            Map<Value, Long> threadTypeCount = groupByAndSort(getThreadStack(),
                    Collectors.groupingBy(thread -> new Value(thread.getKey().getClass().getName()), Collectors.counting()),
                    Map.Entry.comparingByValue(Comparator.reverseOrder()));
            return new ResponseEntity<>(threadTypeCount, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/thread-stack/")
    public ResponseEntity<Map<ThreadInfo, ThreadInfo.StackInfo[]>> getThreadsGroupByThreadStack() {
        try {
            return new ResponseEntity<>(sorted(getThreadStack().entrySet().stream()
                            .collect(Collectors
                                    .toMap(entry -> new ThreadInfo(entry.getKey()), entry ->
                                            Stream.of(entry.getValue())
                                                    .map(ThreadInfo.StackInfo::new)
                                                    .toArray(ThreadInfo.StackInfo[]::new))
                            ), Comparator.comparing(entry -> entry.getValue().length, Comparator.reverseOrder())), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/thread-stack/name/")
    public ResponseEntity<Map<ThreadInfo, String[]>> getThreadsGroupByThreadStackName() {
        try {
            return new ResponseEntity<>(sorted(getThreadStack().entrySet().stream()
                    .collect(Collectors
                            .toMap(entry -> new ThreadInfo(entry.getKey()), entry ->
                                    Stream.of(entry.getValue())
                                            .map(ThreadInfo.StackInfo::new)
                                            .map(ThreadInfo.StackInfo::getClassName)
                                            .toArray(String[]::new))
                    ), Comparator.comparing(entry -> entry.getValue().length, Comparator.reverseOrder())), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/thread-stack/class/")
    public ResponseEntity<Map<ThreadInfo, List<Value>>> getThreadsGroupByThreadStackClass() {
        try {
            return new ResponseEntity<>(sorted(getThreadStack().entrySet().stream()
                    .collect(Collectors
                            .toMap(entry -> new ThreadInfo(entry.getKey()), entry ->
                                    Stream.of(entry.getValue())
                                            .map(StackTraceElement::getClassName)
                                            .map(Value::new)
                                            .collect(Collectors.toList()))
                    ), Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/thread-stack/count/")
    public ResponseEntity<Map<ThreadInfo, Integer>> getThreadsGroupByThreadStackCounting() {
        try {
            return new ResponseEntity<>(sorted(getThreadStack().entrySet().stream().collect(Collectors
                            .toMap(entry -> new ThreadInfo(entry.getKey()), entry -> entry.getValue().length)),
                    Comparator.comparing(entry -> entry.getValue(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/stack-thread/")
    public ResponseEntity<Map<List<Value>, List<ThreadInfo>>> getThreadsGroupByStackThread() {
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByThreadInfo(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/stack-thread/class/")
    public ResponseEntity<Map<List<Value>, List<Value>>> getThreadsGroupByStackThreadClass() {
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupBy(), Comparator
                    .comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/stack-thread/name/")
    public ResponseEntity<Map<List<Value>, List<Value>>> getThreadsGroupByStackThreadName() {
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByName(), Comparator
                    .comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/stack-thread/count/")
    public ResponseEntity<Map<List<Value>, Long>> getThreadsGroupByStackThreadCounting() {
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByWithCounting(), Map.Entry
                    .comparingByValue(Comparator.reverseOrder())), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
