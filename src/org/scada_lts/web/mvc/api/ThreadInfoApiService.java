package org.scada_lts.web.mvc.api;

import org.scada_lts.utils.ThreadInfoApiUtils;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.scada_lts.web.mvc.api.json.ThreadInfoList;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.scada_lts.utils.ApiUtils.convertList;
import static org.scada_lts.utils.ApiUtils.convertMap;
import static org.scada_lts.utils.ThreadInfoApiUtils.*;
import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;

@Service
public class ThreadInfoApiService {

    public ThreadInfoList<ThreadInfo> getThreadsInfo(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertList(getThreadStack().keySet().stream()
                    .sorted(Comparator.comparing(Thread::getName))
                    .map(ThreadInfo::new)
                    .collect(Collectors.toList()), ThreadInfoList::new);
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

    public ThreadInfoList<Value> getThreadNames(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertList(getThreadStack().keySet().stream()
                    .map(Thread::getName)
                    .map(Value::new)
                    .sorted(Comparator.comparing(Value::getValue))
                    .collect(Collectors.toList()), ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public ThreadInfoList<Value> getThreadClasses(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertList(getThreadStack().keySet().stream()
                    .map(a -> a.getClass().getName())
                    .distinct()
                    .map(Value::new)
                    .sorted(Comparator.comparing(Value::getValue))
                    .collect(Collectors.toList()), ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, Long> getThreadsGroupByClassNameCount(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(),
                    Collectors.groupingBy(thread -> new Value(thread.getKey().getClass().getName()), Collectors.counting()),
                    Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, ThreadInfoList<ThreadInfo>> getThreadsGroupByClassName(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertMap(groupByAndSort(ThreadInfoApiUtils.getThreads(),
                    Collectors.groupingBy(thread -> new Value(thread.getClass().getName()), Collectors.toList()),
                            (a, b) -> Integer.compare(b.getValue().size(), a.getValue().size())),
                    threads -> new ThreadInfoList<>(threads.stream()
                        .map(ThreadInfo::new)
                        .sorted(Comparator.comparing(ThreadInfo::getName))
                        .collect(Collectors.toList())
                    )
            );
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<List<Value>, ThreadInfoList<ThreadInfo>> getThreadsGroupByStack(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertMap(ThreadInfoApiUtils.groupByAndSort(getThreadStack(), groupByThreadInfo(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<List<Value>, ThreadInfoList<Value>> getThreadsGroupByStackClasses(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertMap(groupByAndSort(getThreadStack(), groupByClassName(), Comparator
                    .comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<List<Value>, ThreadInfoList<Value>> getThreadsGroupByStackNames(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertMap(ThreadInfoApiUtils.groupByAndSort(getThreadStack(), groupByName(), Comparator
                    .comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<List<Value>, Long> getThreadsGroupByStackCount(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return ThreadInfoApiUtils.groupByAndSort(getThreadStack(), groupByCounting(), Map.Entry
                    .comparingByValue(Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }
}
