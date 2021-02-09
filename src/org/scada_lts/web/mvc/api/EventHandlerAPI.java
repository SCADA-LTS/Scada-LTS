package org.scada_lts.web.mvc.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.web.mvc.api.dto.eventHandler.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/api/eventHandler")
public class EventHandlerAPI {

    private static final Log LOG = LogFactory.getLog(EventHandlerAPI.class);

    @Resource
    private EventService eventHandlerService;

    @GetMapping(value = "/getAll", produces = "application/json")
    public ResponseEntity<List<EventHandlerVO>> getAllEventHandlers(HttpServletRequest request) {
        LOG.info("/api/eventHandler/getAll");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(eventHandlerService.getEventHandlers(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getAllPlc", produces = "application/json")
    public ResponseEntity<List<EventHandlerPlcDTO>> getAllRawEventHandlers(HttpServletRequest request) {
        LOG.info("/api/eventHandler/getAllPlc");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(eventHandlerService.getPlcEventHandlers(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get/{xid}", produces = "application/json")
    public ResponseEntity<EventHandlerVO> getEventHandlerByXid(@PathVariable("xid") String xid, HttpServletRequest request) {
        LOG.info("/api/eventHandler/get/" + xid);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(eventHandlerService.getEventHandler(xid), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get/id/{id}", produces = "application/json")
    public ResponseEntity<EventHandlerVO> getEventHandlerById(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("/api/eventHandler/get/id/" + id);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(eventHandlerService.getEventHandler(id), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get/plc/datapoint/{id}", produces = "application/json")
    public ResponseEntity<List<EventHandlerPlcDTO>> getPlcEventHandlerByDatapointId(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("/api/eventHandler/get/plc/datapoint/" + id);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(eventHandlerService.getEventHandlersByDatapointId(id), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<EventHandlerVO> createEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler, HttpServletRequest request) {
        LOG.info("/api/eventHandler/set/...");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                EventTypeVO typeVO = new EventTypeVO(typeId, typeRef1, typeRef2);
                return new ResponseEntity<>(eventHandlerService.saveEventHandler(typeVO, handler), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/set/{typeId}/{typeRef1}/{typeRef2}/1", produces = "application/json")
    public ResponseEntity<EventHandlerVO> createEventHandlerTypePoint(@PathVariable("typeId") int typeId, @PathVariable("typeRef1") int typeRef1, @PathVariable("typeRef2") int typeRef2, @RequestBody EventHandlerPointDTO handler, HttpServletRequest request) {
        return createEventHandler(typeId, typeRef1, typeRef2, handler.createEventHandlerVO(), request);
    }

    @PostMapping(value = "/set/{typeId}/{typeRef1}/{typeRef2}/2", produces = "application/json")
    public ResponseEntity<EventHandlerVO> createEventHandlerTypeEmail(@PathVariable("typeId") int typeId, @PathVariable("typeRef1") int typeRef1, @PathVariable("typeRef2") int typeRef2, @RequestBody EventHandlerEmailDTO handler, HttpServletRequest request) {
        return createEventHandler(typeId, typeRef1, typeRef2, handler.createEventHandlerVO(), request);
    }

    @PostMapping(value = "/set/{typeId}/{typeRef1}/{typeRef2}/3", produces = "application/json")
    public ResponseEntity<EventHandlerVO> createEventHandlerTypeProcess(@PathVariable("typeId") int typeId, @PathVariable("typeRef1") int typeRef1, @PathVariable("typeRef2") int typeRef2, @RequestBody EventHandlerProcessDTO handler, HttpServletRequest request) {
        return createEventHandler(typeId, typeRef1, typeRef2, handler.createEventHandlerVO(), request);
    }

    @PostMapping(value = "/set/{typeId}/{typeRef1}/{typeRef2}/4", produces = "application/json")
    public ResponseEntity<EventHandlerVO> createEventHandlerTypeScript(@PathVariable("typeId") int typeId, @PathVariable("typeRef1") int typeRef1, @PathVariable("typeRef2") int typeRef2, @RequestBody EventHandlerScriptDTO handler, HttpServletRequest request) {
        return createEventHandler(typeId, typeRef1, typeRef2, handler.createEventHandlerVO(), request);
    }

    @PostMapping(value = "/set/{typeId}/{typeRef1}/{typeRef2}/5", produces = "application/json")
    public ResponseEntity<EventHandlerVO> createEventHandlerTypeSms(@PathVariable("typeId") int typeId, @PathVariable("typeRef1") int typeRef1, @PathVariable("typeRef2") int typeRef2, @RequestBody EventHandlerSmsDTO handler, HttpServletRequest request) {
        return createEventHandler(typeId, typeRef1, typeRef2, handler.createEventHandlerVO(), request);
    }

    @PutMapping(value = "/update/{typeId}/{typeRef1}/{typeRef2}", consumes = "application/json")
    public ResponseEntity<String> updateEventHandler(@PathVariable("typeId") int typeId, @PathVariable("typeRef1") int typeRef1, @PathVariable("typeRef2") int typeRef2, @RequestBody EventHandlerVO handler, HttpServletRequest request) {
        LOG.info("/api/eventHandler/update/...");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                try {
                    EventTypeVO typeVO = new EventTypeVO(typeId, typeRef1, typeRef2);
                    eventHandlerService.saveEventHandler(typeVO, handler);
                    return new ResponseEntity<>("{\"status\":\"updated\"}", HttpStatus.OK);
                } catch (NullPointerException ex) {
                    return new ResponseEntity<>("{\"status\":\"warning\"}", HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/delete/{xid}", produces = "application/json")
    public ResponseEntity<String> deleteEventHandlerByXid(@PathVariable("xid") String xid, HttpServletRequest request) {
        LOG.info("/api/eventHandler/delete/" + xid);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                eventHandlerService.deleteEventHandler(xid);
                Map<String, String> response = new HashMap<>();
                response.put("status", "deleted");
                ObjectMapper m = new ObjectMapper();
                try {
                    String json = m.writeValueAsString(response);
                    return new ResponseEntity<>(json, HttpStatus.OK);
                } catch (JsonProcessingException e) {
                    LOG.error(e);
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/delete/id/{id}", produces = "application/json")
    public ResponseEntity<String> deleteEventHandlerById(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("/api/eventHandler/delete/id/" + id);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                eventHandlerService.deleteEventHandler(id);
                Map<String, String> response = new HashMap<>();
                response.put("status", "deleted");
                ObjectMapper m = new ObjectMapper();
                try {
                    String json = m.writeValueAsString(response);
                    return new ResponseEntity<>(json, HttpStatus.OK);
                } catch (JsonProcessingException e) {
                    LOG.error(e);
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
