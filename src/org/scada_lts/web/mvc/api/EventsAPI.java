package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.utils.SQLPageWithTotal;
import org.scada_lts.web.mvc.api.dto.EventDTO;
import org.scada_lts.web.mvc.api.json.JsonDataPoint;
import org.scada_lts.web.mvc.api.json.JsonEvent;
import org.scada_lts.web.mvc.api.json.JsonEventSearch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
        LOG.info("GET::/api/events/ack/" + eventId);
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
     * Search Events related to specific filters
     *
     * @param query       Additional query like limit or offset
     * @param request     HTTP request with user data
     * @return EventDTO List
     */
    @PostMapping(value = "/search")
    public ResponseEntity<SQLPageWithTotal<EventDTO>> getEvents(@RequestBody JsonEventSearch query, HttpServletRequest request) {
        LOG.info("GET::/api/events/search/");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                SQLPageWithTotal<EventDTO> result = eventService.getEventsWithLimit(
                        query.getAlarmLevel(),
                        query.getEventSourceType(),
                        query.getStatus(),
                        query.getKeywords(),
                        0,
                        query.getSortBy(),
                        query.getSortDesc(),
                        query.getLimit(),
                        query.getOffset());

                return new ResponseEntity<SQLPageWithTotal<EventDTO>>(result, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
