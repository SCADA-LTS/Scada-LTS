package org.scada_lts.web.mvc.api;

import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ThreadInfoApiUtils.*;
import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;


@Service
public class StateThreadInfoApiService {

    public List<Value> getStates(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return getThreadStack().keySet().stream()
                    .map(Thread::getState)
                    .map(Thread.State::toString)
                    .sorted()
                    .map(Value::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<ThreadInfo> getThreadsForState(Thread.State state, HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            Map<Value, List<ThreadInfo>> map = groupByAndSort(getThreadStack(), groupByStates(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
            List<ThreadInfo> response = map.get(new Value(state.name()));
            return response == null ? new ArrayList<>() :  response;
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<Value> getThreadClassesForState(Thread.State state, HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            Map<Value, List<Value>> map = groupByAndSort(getThreadStack(), groupByStatesClass(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
            List<Value> response = map.get(new Value(state.name()));
            return response == null ? new ArrayList<>() :  response;
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public List<Value> getThreadNamesForState(Thread.State state, HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            Map<Value, List<Value>> map = groupByAndSort(getThreadStack(), groupByStatesName(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
            List<Value> response = map.get(new Value(state.name()));
            return response == null ? new ArrayList<>() :  response;
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, List<ThreadInfo>> getThreadsGroupByState(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(), groupByStates(),
                            Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, List<Value>> getThreadClassesGroupByState(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(), groupByStatesClass(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, List<Value>> getThreadNamesGroupByState(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(), groupByStatesName(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    public Map<Value, Long> getThreadsCountGroupByState(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return groupByAndSort(getThreadStack(), groupByStatesCounting(),
                    Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }
}
