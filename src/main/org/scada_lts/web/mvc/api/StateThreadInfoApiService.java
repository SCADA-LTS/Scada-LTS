package org.scada_lts.web.mvc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.utils.ApiUtils;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.scada_lts.web.mvc.api.json.ThreadInfoList;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ApiUtils.convertList;
import static org.scada_lts.utils.ApiUtils.convertMap;
import static org.scada_lts.utils.ThreadInfoApiUtils.*;
import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;


@Service
public class StateThreadInfoApiService {

    private static final Log LOG = LogFactory.getLog(StateThreadInfoApiService.class);

    public ThreadInfoList<Value> getStates(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertList(new ArrayList<>(getThreads().stream()
                            .map(Thread::getState)
                            .map(Thread.State::toString)
                            .sorted()
                            .map(Value::new)
                            .collect(Collectors.toSet())),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public ThreadInfoList<ThreadInfo> getThreadsForState(HttpServletRequest request, Thread.State state) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertList(getThreads().stream()
                            .filter(a -> a.getState() == state)
                            .map(ThreadInfo::new)
                            .collect(Collectors.toList()),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public ThreadInfoList<Value> getThreadsForStateClasses(HttpServletRequest request, Thread.State state) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertList(getThreads().stream()
                    .filter(a -> a.getState() == state)
                    .map(Thread::getClass)
                    .map(Class::getName)
                    .sorted()
                    .map(Value::new)
                    .collect(Collectors.toList()),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public ThreadInfoList<Value> getThreadsForStateNames(HttpServletRequest request, Thread.State state) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertList(getThreads().stream()
                    .filter(a -> a.getState() == state)
                    .map(Thread::getName)
                    .sorted()
                    .map(Value::new)
                    .collect(Collectors.toList()),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, ThreadInfoList<ThreadInfo>> getThreadsGroupByStates(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertMap(groupByAndSort(getThreadStack(), groupByStates(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, ThreadInfoList<Value>> getThreadsGroupByStatesClasses(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertMap(groupByAndSort(getThreadStack(), groupByStatesClass(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, ThreadInfoList<Value>> getThreadsGroupByStatesNames(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return convertMap(groupByAndSort(getThreadStack(), groupByStatesName(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())),
                    ThreadInfoList::new);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, Long> getThreadsGroupByStatesCount(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(), groupByStatesCounting(),
                    Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }
}
