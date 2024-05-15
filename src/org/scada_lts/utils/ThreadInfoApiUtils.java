package org.scada_lts.utils;

import org.scada_lts.web.mvc.api.json.ThreadInfo;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ThreadInfoApiUtils {

    private ThreadInfoApiUtils() {}

    public static <K,V> Map<K, V> groupByAndSort(Map<Thread, StackTraceElement[]> threads,
                                                 Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<K, V>> groupBy,
                                                 Comparator<Map.Entry<K, V>> comparator) {
        return sorted(threads.entrySet().stream()
                .collect(groupBy), comparator);
    }

    public static <K,V> Map<K, V> groupByAndSort(List<Thread> threads,
                                                 Collector<Thread, ?, Map<K, V>> groupBy,
                                                 Comparator<Map.Entry<K, V>> comparator) {
        return sorted(threads.stream()
                .collect(groupBy), comparator);
    }

    public static <K, V> Map<K, V> sorted(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
        return map.entrySet().stream()
                .sorted(comparator)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> {
                    throw new IllegalStateException();
                }, LinkedHashMap::new));
    }

    public static Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<List<Value>, List<Value>>> groupByClassName() {
        return Collectors.groupingBy(
                entry -> Stream.of(entry.getValue())
                        .map(StackTraceElement::getClassName)
                        .map(Value::new)
                        .collect(Collectors.toList()),
                Collectors.mapping(entry -> new Value(entry.getKey().getClass().getName()), Collectors.toList()));
    }

    public static Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<List<Value>, List<Value>>> groupByName() {
        return Collectors.groupingBy(
                entry -> Stream.of(entry.getValue())
                        .map(StackTraceElement::getClassName)
                        .map(Value::new)
                        .collect(Collectors.toList()),
                Collectors.mapping(entry -> new Value(entry.getKey().getName()), Collectors.toList()));
    }

    public static Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<Value, List<ThreadInfo>>> groupByStates() {
        return Collectors.groupingBy(
                entry -> new Value(entry.getKey().getState().toString()),
                Collectors.mapping(entry -> new ThreadInfo(entry.getKey()), Collectors.toList()));
    }

    public static Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<Value, List<Value>>> groupByStatesName() {
        return Collectors.groupingBy(
                entry -> new Value(entry.getKey().getState().toString()),
                Collectors.mapping(entry -> new Value(entry.getKey().getName()), Collectors.toList()));
    }


    public static Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<Value, List<Value>>> groupByStatesClass() {
        return Collectors.groupingBy(
                entry -> new Value(entry.getKey().getState().toString()),
                Collectors.mapping(entry -> new Value(entry.getKey().getClass().getName()), Collectors.toList()));
    }


    public static Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<Value, Long>> groupByStatesCounting() {
        return Collectors.groupingBy(
                entry -> new Value(entry.getKey().getState().toString()),
                Collectors.mapping(entry -> new Value(entry.getKey().getName()), Collectors.counting()));
    }

    public static Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<List<Value>, Long>> groupByCounting() {
        return Collectors.groupingBy(
                entry -> Stream.of(entry.getValue())
                        .map(StackTraceElement::getClassName)
                        .map(Value::new)
                        .collect(Collectors.toList()),
                Collectors.counting());
    }

    public static Collector<Map.Entry<Thread, StackTraceElement[]>, ?, Map<List<Value>, List<ThreadInfo>>> groupByThreadInfo() {
        return Collectors.groupingBy(
                entry -> Stream.of(entry.getValue())
                        .map(StackTraceElement::getClassName)
                        .map(Value::new)
                        .collect(Collectors.toList()),
                Collectors.mapping(entry -> new ThreadInfo(entry.getKey()), Collectors.toList()));
    }

    public static Map<Thread, StackTraceElement[]> getThreadStack() {
        return new HashMap<>(Thread.getAllStackTraces());
    }

    public static List<Thread> getThreads() {
        return getThreadStack().keySet().stream()
                .sorted(Comparator.comparing(Thread::getId).reversed()
                        .thenComparing(Thread::getName)
                        .thenComparing(Thread::getState))
                .collect(Collectors.toList());
    }

    public static class Value implements Comparable<Value> {
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
            return value;
        }

        @Override
        public int compareTo(Value o) {
            if(this.value == null || o.value == null)
                return -1;
            return this.value.compareTo(o.value);
        }
    }
}
