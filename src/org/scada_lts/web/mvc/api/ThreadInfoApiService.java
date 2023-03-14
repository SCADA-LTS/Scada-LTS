package org.scada_lts.web.mvc.api;

import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.scada_lts.utils.ThreadInfoApiUtils.*;
import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;

@Service
public class ThreadInfoApiService {

    public List<ThreadInfo> getThreads(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return getThreadStack().keySet().stream()
                    .sorted(Comparator.comparing(Thread::getName))
                    .map(ThreadInfo::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<ThreadInfo.StackInfo[]> getStackTraceElements(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return getThreadStack().values().stream()
                    .map(stackTrace -> Stream.of(stackTrace)
                            .map(ThreadInfo.StackInfo::new)
                            .sorted(Comparator.comparing(ThreadInfo.StackInfo::getClassName))
                            .toArray(ThreadInfo.StackInfo[]::new))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<Value> getThreadNames(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return getThreadStack().keySet().stream()
                    .map(Thread::getName)
                    .map(Value::new)
                    .sorted(Comparator.comparing(Value::getValue))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, Long> getThreadsGroupByClassCount(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(),
                    Collectors.groupingBy(thread -> new Value(thread.getKey().getClass().getName()), Collectors.counting()),
                    Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<ThreadInfo, ThreadInfo.StackInfo[]> getThreadsGroupByThreadStack(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return sorted(getThreadStack().entrySet().stream()
                    .collect(Collectors
                            .toMap(entry -> new ThreadInfo(entry.getKey()), entry ->
                                    Stream.of(entry.getValue())
                                            .map(ThreadInfo.StackInfo::new)
                                            .toArray(ThreadInfo.StackInfo[]::new))
                    ), Comparator.comparing(entry -> entry.getValue().length, Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<ThreadInfo, String[]> getThreadsGroupByThreadStackClasses(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return sorted(getThreadStack().entrySet().stream()
                    .collect(Collectors
                            .toMap(entry -> new ThreadInfo(entry.getKey()), entry ->
                                    Stream.of(entry.getValue())
                                            .map(ThreadInfo.StackInfo::new)
                                            .map(ThreadInfo.StackInfo::getClassName)
                                            .toArray(String[]::new))
                    ), Comparator.comparing(entry -> entry.getValue().length, Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<ThreadInfo, Integer> getThreadsGroupByThreadStackCount(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return sorted(getThreadStack().entrySet().stream().collect(Collectors
                            .toMap(entry -> new ThreadInfo(entry.getKey()), entry -> entry.getValue().length)),
                    Comparator.comparing(entry -> entry.getValue(), Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<List<Value>, List<ThreadInfo>> getThreadsGroupByStackThread(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(), groupByThreadInfo(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<List<Value>, List<Value>> getThreadsGroupByStackThreadClasses(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(), groupBy(), Comparator
                    .comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<List<Value>, List<Value>> getThreadsGroupByStackThreadNames(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(), groupByName(), Comparator
                    .comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<List<Value>, Long> getThreadsGroupByStackThreadCount(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(), groupByCounting(), Map.Entry
                    .comparingByValue(Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }
}
