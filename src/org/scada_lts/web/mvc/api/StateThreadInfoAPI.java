package org.scada_lts.web.mvc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.ThreadInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ThreadInfoApiUtils.*;


@RestController
@RequestMapping(path = "/api/threads/states/")
public class StateThreadInfoAPI {

    private static final Log LOG = LogFactory.getLog(StateThreadInfoAPI.class);

    @GetMapping(value = "/")
    public ResponseEntity<List<Value>> getStates() {
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
    public ResponseEntity<List<ThreadInfo>> getStatesGroupByState(@PathVariable("state") String state) {
        try {
            if(state != null && state.isEmpty())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            try {
                Thread.State.valueOf(state);
            } catch (Exception ex) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Map<Value, List<ThreadInfo>> map = groupByAndSort(getThreadStack(), groupByStates(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
            return new ResponseEntity<>(map.get(new Value(state)), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/state/{state}/count/")
    public ResponseEntity<Long> getStatesGroupByStateCount(@PathVariable("state") String state) {
        try {
            if(state != null && state.isEmpty())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            try {
                Thread.State.valueOf(state);
            } catch (Exception ex) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Map<Value, Long> map = groupByAndSort(getThreadStack(), groupByStatesCounting(),
                    Map.Entry.comparingByValue(Comparator.reverseOrder()));
            Long result = map.get(new Value(state));
            return new ResponseEntity<>((result == null ? 0 : result), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/state/{state}/class/")
    public ResponseEntity<List<Value>> getStatesGroupByStateClass(@PathVariable("state") String state) {
        try {
            if(state != null && state.isEmpty())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            try {
                Thread.State.valueOf(state);
            } catch (Exception ex) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Map<Value, List<Value>> map = groupByAndSort(getThreadStack(), groupByStatesClass(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
            return new ResponseEntity<>(map.get(new Value(state)), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/state/{state}/name/")
    public ResponseEntity<List<Value>> getStatesGroupByStateName(@PathVariable("state") String state) {
        try {
            if(state != null && state.isEmpty())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            try {
                Thread.State.valueOf(state);
            } catch (Exception ex) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Map<Value, List<Value>> map = groupByAndSort(getThreadStack(), groupByStatesName(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()));
            return new ResponseEntity<>(map.get(new Value(state)), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/")
    public ResponseEntity<Map<Value, List<ThreadInfo>>> getStatesGroupBy() {
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByStates(),
                            Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/class/")
    public ResponseEntity<Map<Value, List<Value>>> getStatesGroupByClass() {
        try {
            return new ResponseEntity<>(groupByAndSort(getThreadStack(), groupByStatesClass(),
                    Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder())
            ), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/name/")
    public ResponseEntity<Map<Value, List<Value>>> getStatesGroupByName() {
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
    public ResponseEntity<Map<Value, Long>> getStatesGroupByCount() {
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
