package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ThreadInfoApiUtils.*;


@RestController
@RequestMapping(path = "/api/threads/states/")
public class StateThreadInfoAPI {

    private static final Log LOG = LogFactory.getLog(StateThreadInfoAPI.class);

    @GetMapping(value = "/")
    public ResponseEntity<List<Value>> getStates(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(getThreadStack().keySet().stream()
                    .map(Thread::getState)
                    .map(Thread.State::toString)
                    .sorted()
                    .map(Value::new)
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/state/{state}/")
    public ResponseEntity<List<ThreadInfo>> getThreadsForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                               HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            Map<Value, List<ThreadInfo>> map = groupByAndSort(getThreadStack(), groupByStates(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
            return new ResponseEntity<>(map.get(new Value(state.name())), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/state/{state}/count/")
    public ResponseEntity<Long> getThreadsCountForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                        HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            Map<Value, Long> map = groupByAndSort(getThreadStack(), groupByStatesCounting(),
                    Map.Entry.comparingByValue(Comparator.reverseOrder()));
            Long result = map.get(new Value(state.name()));
            return new ResponseEntity<>((result == null ? 0 : result), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/state/{state}/classes/")
    public ResponseEntity<List<Value>> getThreadClassesForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                                HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            Map<Value, List<Value>> map = groupByAndSort(getThreadStack(), groupByStatesClass(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
            return new ResponseEntity<>(map.get(new Value(state.name())), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/state/{state}/names/")
    public ResponseEntity<List<Value>> getThreadNamesForState(@PathVariable(value = "state", required = true) Thread.State state,
                                                              HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            Map<Value, List<Value>> map = groupByAndSort(getThreadStack(), groupByStatesName(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
            return new ResponseEntity<>(map.get(new Value(state.name())), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/")
    public ResponseEntity<Map<Value, List<ThreadInfo>>> getThreadsGroupByState(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByStates(),
                            Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/classes/")
    public ResponseEntity<Map<Value, List<Value>>> getThreadClassesGroupByState(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByStatesClass(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/names/")
    public ResponseEntity<Map<Value, List<Value>>> getThreadNamesGroupByState(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByStatesName(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/count/")
    public ResponseEntity<Map<Value, Long>> getThreadsCountGroupByState(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByStatesCounting(),
                    Map.Entry.comparingByValue(Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
