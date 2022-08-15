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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
                            .toArray(ThreadInfo.StackInfo[]::new))
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/names/")
    public ResponseEntity<List<Value>> getNames() {
        try {
            return new ResponseEntity<>(getThreadStack().keySet().stream()
                    .map(Thread::getName)
                    .map(Value::new)
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/")
    public ResponseEntity<Map<String, Long>> getGroupByThreadTypeCount() {
        try {
            Map<String, Long> threadTypeCount = groupByAndSort(getThreadStack(), Collectors
                    .groupingBy(thread -> thread.getClass().getName(), Collectors.counting()), Map.Entry
                    .comparingByValue(Comparator.reverseOrder()));
            return new ResponseEntity<>(threadTypeCount, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/thread-stack/")
    public ResponseEntity<Map<ThreadInfo, ThreadInfo.StackInfo[]>> getGroupByThreadStack() {
        try {
            return new ResponseEntity<>(getThreadStack().entrySet().stream()
                            .collect(Collectors
                                    .toMap(entry -> new ThreadInfo(entry.getKey()), entry ->
                                            Stream.of(entry.getValue())
                                                    .map(ThreadInfo.StackInfo::new)
                                                    .toArray(ThreadInfo.StackInfo[]::new))
                            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/thread-stack-class/")
    public ResponseEntity<Map<ThreadInfo, List<Value>>> getGroupByThreadStackClass() {
        try {
            return new ResponseEntity<>(getThreadStack().entrySet().stream()
                    .collect(Collectors
                            .toMap(entry -> new ThreadInfo(entry.getKey()), entry ->
                                    Stream.of(entry.getValue())
                                            .map(StackTraceElement::getClassName)
                                            .map(Value::new)
                                            .collect(Collectors.toList()))
                    ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/thread-stack-count/")
    public ResponseEntity<Map<ThreadInfo, Integer>> getGroupByThreadStackCount() {
        try {
            return new ResponseEntity<>(getThreadStack().entrySet().stream()
                    .collect(Collectors
                            .toMap(entry -> new ThreadInfo(entry.getKey()), entry -> entry.getValue().length)
                    ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/stack-thread/")
    public ResponseEntity<Map<List<Value>, List<ThreadInfo>>> getGroupByStackThread() {
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByThreadInfo(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/stack-thread-class/")
    public ResponseEntity<Map<List<Value>, List<Value>>> getGroupByStackThreadClass() {
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupBy(), Comparator
                    .comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/stack-thread-count/")
    public ResponseEntity<Map<List<Value>, Long>> getGroupByStackThreadCount() {
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByWithCounting(), Map.Entry
                    .comparingByValue(Comparator.reverseOrder())), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private <K,V> Map<K, V> groupByAndSort(Map<Thread, StackTraceElement[]> threads,
                                           Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<K, V>> groupBy,
                                           Comparator<Map.Entry<K, V>> comparator) {
        return sorted(threads.entrySet().stream()
                .collect(groupBy), comparator);
    }

    private <K, V> Map<K, V> sorted(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
        return map.entrySet().stream()
                .sorted(comparator)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> {
                    throw new IllegalStateException();
                }, LinkedHashMap::new));
    }

    private Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<List<Value>, List<Value>>> groupBy() {
        return Collectors.groupingBy(
                entry -> Stream.of(entry.getValue())
                        .map(StackTraceElement::getClassName)
                        .map(Value::new)
                        .collect(Collectors.toList()),
                Collectors.mapping(entry -> new Value(entry.getKey().getName()), Collectors.toList()));
    }

    private Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<List<Value>, Long>> groupByWithCounting() {
       return Collectors.groupingBy(
                entry -> Stream.of(entry.getValue())
                        .map(StackTraceElement::getClassName)
                        .map(Value::new)
                        .collect(Collectors.toList()),
                Collectors.counting());
    }

    private Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<List<Value>, List<ThreadInfo>>> groupByThreadInfo() {
        return Collectors.groupingBy(
                entry -> Stream.of(entry.getValue())
                        .map(StackTraceElement::getClassName)
                        .map(Value::new)
                        .collect(Collectors.toList()),
                Collectors.mapping(entry -> new ThreadInfo(entry.getKey()), Collectors.toList()));
    }

    private Map<Thread, StackTraceElement[]> getThreadStack() {
        return Thread.getAllStackTraces();
    }

    static class Value {
        private String value;

        public Value(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Value)) return false;
            Value value = (Value) o;
            return Objects.equals(getValue(), value.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getValue());
        }

        @Override
        public String toString() {
            return "Name{" +
                    "name='" + value + '\'' +
                    '}';
        }
    }

}
