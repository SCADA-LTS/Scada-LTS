package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.utils.SQLPageWithTotal;
import org.scada_lts.web.mvc.api.dto.EventCommentDTO;
import org.scada_lts.web.mvc.api.dto.EventDTO;
import org.scada_lts.web.mvc.api.json.JsonEventSearch;
import org.scada_lts.web.mvc.api.json.JsonIdSelection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Simple controller for Events in Scada-LTS
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
@Controller
@RequestMapping(value = "/api/events")
public class EventsAPI {

    private static final Log LOG = LogFactory.getLog(EventsAPI.class);

    @Resource
    private EventService eventService;

    /**
     * Get Events related to specific Data Point
     *
     * @param datapointId Data Point Id number
     * @param query       Additional query like limit or offset
     * @param request     HTTP request with user data
     * @return EventDTO List
     */
    @GetMapping(value = "/datapoint/{id}")
    public ResponseEntity<List<EventDTO>> getDataPointEvents(@PathVariable("id") int datapointId, @RequestParam Map<String, String> query, HttpServletRequest request) {
        LOG.info("GET::/api/events/datapoint/" + datapointId);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                int limit = 1000;
                int offset = 0;
                if (query.containsKey("limit")) {
                    limit = Integer.parseInt(query.get("limit"));
                }
                if (query.containsKey("offset")) {
                    offset = Integer.parseInt(query.get("offset"));
                }
                return new ResponseEntity<>(
                        eventService.getDataPointEventsWithLimit(datapointId, limit, offset),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Acknowledge specific Event Alarm from REST API
     *
     * @param eventId Event ID number
     * @param request Request containing user data
     * @return Response
     */
    @PutMapping(value = "/ack/{id}")
    public ResponseEntity<String> acknowledgeEvent(@PathVariable("id") int eventId, HttpServletRequest request) {
        LOG.info("PUT::/api/events/ack/" + eventId);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Date time = new Date();
                eventService.ackEvent(eventId, time.getTime(), user.getId(), 0);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Silence specific Event Alarm from REST API
     *
     * @param eventId Event ID number
     * @param request Request containing user data
     * @return Response
     */
    @PutMapping(value = "/silence/{id}")
    public ResponseEntity<String> silenceEvent(@PathVariable("id") int eventId, HttpServletRequest request) {
        LOG.info("PUT::/api/events/silence/" + eventId);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Date time = new Date();
                eventService.silenceEvent(eventId, user.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Unsilence specific Event Alarm from REST API
     *
     * @param eventId Event ID number
     * @param request Request containing user data
     * @return Response
     */
    @PutMapping(value = "/unsilence/{id}")
    public ResponseEntity<String> unsilenceEvent(@PathVariable("id") int eventId, HttpServletRequest request) {
        LOG.info("PUT::/api/events/unsilence/" + eventId);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Date time = new Date();
                eventService.unsilenceEvent(eventId, user.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search Events related to specific filters
     *
     * @param query       Additional query like limit or offset
     * @param request     HTTP request with user data
     * @return EventDTO List
     */
    @PostMapping(value = "/search")
    public ResponseEntity<SQLPageWithTotal<EventDTO>> getEvents(@RequestBody JsonEventSearch query, HttpServletRequest request) {
            LOG.info("POST::/api/events/search/");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                SQLPageWithTotal<EventDTO> result = eventService.getEventsWithLimit(query, user);
                return new ResponseEntity<SQLPageWithTotal<EventDTO>>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get unsilenced highest level alarm
     *
     * @param request     HTTP request with user data
     * @return Integer
     */
    @GetMapping(value = "/highestUnsilencedLevelAlarm")
    public ResponseEntity<Integer> getHighestUnsilencedLevelAlarm(HttpServletRequest request) {
        LOG.info("GET::/api/events/highestUnsilencedLevelAlarm/");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                int result = eventService.getHighestUnsilencedAlarmLevel(user.getId());
                return new ResponseEntity<Integer>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Acknowledge all Events Alarm from REST API
     *
     * @param request Request containing user data
     * @return Response
     */
    @PostMapping(value = "/ackAll")
    public ResponseEntity<String> acknowledgeAllEvents(HttpServletRequest request) {
        LOG.info("GET::/api/events/ackAll");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Date time = new Date();
                eventService.ackAllPending(time.getTime(), user.getId(), 0);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Silence all Events Alarm from REST API
     *
     * @param request Request containing user data
     * @return Response
     */
    @PostMapping(value = "/silenceAll")
    public ResponseEntity<String> silenceAllEvents(HttpServletRequest request) {
        LOG.info("GET::/api/events/silenceAll");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Date time = new Date();
                eventService.silenceAll(user.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Acknowledge selected Events Alarm from REST API
     *
     * @param request Request containing user data
     * @return Response
     */
    @PostMapping(value = "/ackSelected")
    public ResponseEntity<String> acknowledgeSelectedEvents(@RequestBody JsonIdSelection query, HttpServletRequest request) {
        LOG.info("GET::/api/events/ackSelected");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Date time = new Date();
                List <Integer> ids = new ArrayList<Integer>();
                for (String id: query.getIds().split(",")) {
                    ids.add(Integer.parseInt(id.trim()));
                }
                eventService.ackSelected(time.getTime(), user.getId(), 0, ids);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Silence selected Events Alarm from REST API
     *
     * @param request Request containing user data
     * @return Response
     */
    @PostMapping(value = "/silenceSelected")
    public ResponseEntity<String> silenceSelectedEvents(@RequestBody JsonIdSelection query, HttpServletRequest request) {
        LOG.info("GET::/api/events/silenceSelectedEvents");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Date time = new Date();
                List <Integer> ids = new ArrayList<Integer>();
                for (String id: query.getIds().split(",")) {
                    ids.add(Integer.parseInt(id.trim()));
                }
//                eventService.silenceEvents(ids, user.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Silence selected Events Alarm from REST API
     *
     * @param request Request containing user data
     * @return Response
     */
    @PostMapping(value = "/unsilenceSelected")
    public ResponseEntity<String> unsilenceSelectedEvents(@RequestBody JsonIdSelection query, HttpServletRequest request) {
        LOG.info("GET::/api/events/unsilenceSelectedEvents");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Date time = new Date();
                List <Integer> ids = new ArrayList<Integer>();
                for (String id: query.getIds().split(",")) {
                    ids.add(Integer.parseInt(id.trim()));
                }
                eventService.unsilenceEvents(ids, user.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * get comments by eventId
     *
     * @param eventId Event ID number
     * @param request     HTTP request with user data
     * @return EventDTO List
     */
    @GetMapping(value = "/{eventId}/comments")
    public ResponseEntity<List<EventCommentDTO>> getCommentsByEventId(@PathVariable("eventId") int eventId, HttpServletRequest request) {
        LOG.info("GET::/api/comments/"+eventId);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                List<EventCommentDTO> result = eventService.findCommentsByEventId(eventId);
                return new ResponseEntity<List<EventCommentDTO>>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
