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
import org.scada_lts.web.mvc.api.dto.eventDetector.EventDetectorChangeDTO;
import org.scada_lts.web.mvc.api.dto.eventDetector.EventDetectorDTO;
import org.scada_lts.web.mvc.api.json.JsonPointEventDetector;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller for EventDetector
 *
 * @author Artur Wolak
 */
@Controller
@RequestMapping(path = "/api/eventDetector")
public class EventDetectorAPI {

    private static final Log LOG = LogFactory.getLog(EventDetectorAPI.class);
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    @Resource
    private DataPointService dataPointService;

    public EventDetectorAPI() {
    }

    public EventDetectorAPI(DataPointService dataPointService) {
        this.dataPointService = dataPointService;
    }

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
    public ResponseEntity<JsonPointEventDetector> createBinaryStateEventDetector(@PathVariable int datapointId, HttpServletRequest request, @RequestBody EventDetectorBinaryStateDTO body) {
        return createEventDetectorType(datapointId, body, request);
    }

    @PostMapping(value = "/set/change/{datapointId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonPointEventDetector> createChangeEventDetector(@PathVariable int datapointId, HttpServletRequest request, @RequestBody EventDetectorChangeDTO body) {
        return createEventDetectorType(datapointId, body, request);
    }

    @PutMapping(value = "/update/binary/state/{datapointId}/{id}", consumes = "application/json")
    public ResponseEntity<String> updateBinaryStateEventDetector(@PathVariable int datapointId, @PathVariable int id, HttpServletRequest request, @RequestBody EventDetectorBinaryStateDTO body) {
        return updateEventDetectorType(datapointId, id, body, request);
    }

    @PutMapping(value = "/update/change/{datapointId}/{id}", consumes = "application/json")
    public ResponseEntity<String> updateChangeEventDetector(@PathVariable int datapointId, @PathVariable int id, HttpServletRequest request, @RequestBody EventDetectorChangeDTO body) {
        return updateEventDetectorType(datapointId, id, body, request);
    }

    @DeleteMapping(value = "/delete/{datapointId}/{id}", produces = "application/json")
    public ResponseEntity<String> deleteEventDetectorById(@PathVariable int datapointId, @PathVariable int id, HttpServletRequest request) {
        LOG.info("/api/eventDetector/delete/" + datapointId + "/" + id);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dataPointVO = dataPointService.getDataPoint(datapointId);
                List<PointEventDetectorVO> peds = dataPointVO.getEventDetectors();
                if (!peds.isEmpty())  {
                    peds.removeIf(ped -> ped.getId() == id);
                }
                dataPointService.deleteEventDetector(dataPointVO, id);
                Common.ctx.getRuntimeManager().saveDataPoint(dataPointVO);
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

    private ResponseEntity<JsonPointEventDetector> createEventDetectorType(int datapointId, EventDetectorDTO body, HttpServletRequest request){
        LOG.info("/api/eventDetector/set/.../" + datapointId);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dataPointVO = dataPointService.getDataPoint(datapointId);
                PointEventDetectorVO pointEventDetectorVO = body.createPointEventDetectorVO(dataPointVO);
                JsonPointEventDetector jsonPointEventDetector = createEventDetector(dataPointVO, pointEventDetectorVO);
                return new ResponseEntity<>(jsonPointEventDetector, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private JsonPointEventDetector createEventDetector(DataPointVO dataPointVO, PointEventDetectorVO pointEventDetectorVO) {

        List<PointEventDetectorVO> peds = dataPointVO.getEventDetectors();
        if (!peds.isEmpty())  {
            for (PointEventDetectorVO ped : peds) {
                if (ped.getXid().equals(pointEventDetectorVO.getXid())) {
                    return new JsonPointEventDetector(ped.getId(), ped.getXid(), ped.getAlias());
                }
            }
        }
        dataPointVO.getEventDetectors().add(pointEventDetectorVO);
        dataPointService.saveEventDetectors(dataPointVO);
        if(atomicInteger.getAndDecrement() == 0) {
            try {
                Common.ctx.getRuntimeManager().saveDataPoint(dataPointVO);
            } finally {
                atomicInteger.set(0);
            }
        }


        int pedID = dataPointService.getDetectorId(pointEventDetectorVO.getXid(), dataPointVO.getId());
        return new JsonPointEventDetector(pedID, pointEventDetectorVO.getXid(), pointEventDetectorVO.getAlias());
    }


    private ResponseEntity<String> updateEventDetectorType(int datapointId, int id, EventDetectorDTO body, HttpServletRequest request) {
        LOG.info("/api/eventDetector/update/.../" + datapointId + "/" + id);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dataPointVO = dataPointService.getDataPoint(datapointId);
                PointEventDetectorVO pointEventDetectorVO = body.createPointEventDetectorVO(dataPointVO);
                pointEventDetectorVO.setId(id);
                List<PointEventDetectorVO> peds = dataPointVO.getEventDetectors();
                if (!peds.isEmpty())  {
                    peds.removeIf(ped -> ped.getId() == id);
                }
                dataPointVO.getEventDetectors().add(pointEventDetectorVO);
                dataPointService.saveEventDetectors(dataPointVO);
                Common.ctx.getRuntimeManager().saveDataPoint(dataPointVO);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
