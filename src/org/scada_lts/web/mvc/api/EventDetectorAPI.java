package org.scada_lts.web.mvc.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.mvc.api.dto.eventDetector.EventDetectorBinaryStateDTO;
import org.scada_lts.web.mvc.api.dto.eventDetector.EventDetectorDTO;
import org.scada_lts.web.mvc.api.json.JsonPointEventDetector;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for EventDetector
 *
 * @author Artur Wolak
 */
@Controller
@RequestMapping(path = "/api/eventDetector")
public class EventDetectorAPI {

    private static final Log LOG = LogFactory.getLog(EventDetectorAPI.class);

    @Resource
    private DataPointService dataPointService;

    @GetMapping(value = "/getAll/id/{datapointId}", produces = "application/json")
    public ResponseEntity<List<PointEventDetectorVO>> getEventDetectorsById(@PathVariable int datapointId, HttpServletRequest request) {
        LOG.info("/api/eventDetector/getAll/" + datapointId);
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(dataPointService.getEventDetectors(dataPointService.getDataPoint(datapointId)), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getAll/{datapointXid}", produces = "application/json")
    public ResponseEntity<List<PointEventDetectorVO>> getEventDetectorsByXid(@PathVariable String datapointXid, HttpServletRequest request) {
        LOG.info("/api/eventDetector/getAll/" + datapointXid);
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(dataPointService.getEventDetectors(dataPointService.getDataPointByXid(datapointXid)), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/set/binary/state/{datapointId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonPointEventDetector> setBinaryStateEventDetector(@PathVariable int datapointId, HttpServletRequest request, @RequestBody EventDetectorBinaryStateDTO eventDetectorBinaryStateDTO) {
        LOG.info("/api/eventDetector/set/binary/state/" + datapointId);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dataPointVO = dataPointService.getDataPoint(datapointId);
                JsonPointEventDetector jsonPointEventDetector = createEventDetector(dataPointVO, eventDetectorBinaryStateDTO);
                return new ResponseEntity<>(jsonPointEventDetector, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(value = "/set/binary/state/xid/{datapointXid}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonPointEventDetector> setBinaryStateEventDetectorXid(@PathVariable String datapointXid, HttpServletRequest request, @RequestBody EventDetectorBinaryStateDTO eventDetectorBinaryStateDTO) {
        LOG.info("/api/eventDetector/set/binary/state/xid/" + datapointXid);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dataPointVO = dataPointService.getDataPoint(datapointXid);
                JsonPointEventDetector jsonPointEventDetector = createEventDetector(dataPointVO, eventDetectorBinaryStateDTO);
                return new ResponseEntity<>(jsonPointEventDetector, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private JsonPointEventDetector createEventDetector(DataPointVO dataPointVO, EventDetectorBinaryStateDTO body){
        PointEventDetectorVO pointEventDetectorVO = body.createPointEventDetectorVO(dataPointVO);
        dataPointVO.getEventDetectors().add(pointEventDetectorVO);
        dataPointService.saveEventDetectors(dataPointVO);
        int pedID = dataPointService.getDetectorId(pointEventDetectorVO.getXid(), dataPointVO.getId());
        return new JsonPointEventDetector(pedID, pointEventDetectorVO.getXid(), pointEventDetectorVO.getAlias());
    }

    @PutMapping(value = "/update/binary/state/{datapointId}/{id}", consumes = "application/json")
    public ResponseEntity<String> updateBinaryStateEventDetector(@PathVariable int datapointId, @PathVariable int id, HttpServletRequest request, @RequestBody EventDetectorBinaryStateDTO eventDetectorBinaryStateDTO) {
        LOG.info("/api/eventDetector/update/binary/state/" + datapointId + "/" + id);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dataPointVO = dataPointService.getDataPoint(datapointId);
                PointEventDetectorVO pointEventDetectorVO = eventDetectorBinaryStateDTO.createPointEventDetectorVO(dataPointVO);
                pointEventDetectorVO.setId(id);
                dataPointVO.getEventDetectors().add(pointEventDetectorVO);
                dataPointService.saveEventDetectors(dataPointVO);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/delete/{datapointId}/{id}", produces = "application/json")
    public ResponseEntity<String> deleteEventHandlerById(@PathVariable int datapointId, @PathVariable int id, HttpServletRequest request) {
        LOG.info("/api/eventDetector/delete/" + datapointId + "/" + id);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dataPointVO = dataPointService.getDataPoint(datapointId);
                dataPointService.deleteEventDetector(dataPointVO, id);
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
