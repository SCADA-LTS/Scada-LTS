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
import org.scada_lts.utils.EventDetectorApiUtils;
import org.scada_lts.web.mvc.api.dto.EventDetectorDTO;
import org.scada_lts.web.mvc.api.json.JsonPointEventDetector;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.scada_lts.utils.EventDetectorApiUtils.*;
import static org.scada_lts.utils.ValidationUtils.formatErrorsJson;


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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/set/{datapointId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonPointEventDetector> createEventDetector(@PathVariable Integer datapointId, HttpServletRequest request, @RequestBody EventDetectorDTO body) {
        return createEventDetectorType(datapointId, body, request);
    }

    @PutMapping(value = "/update/{datapointId}/{id}", consumes = "application/json")
    public ResponseEntity<String> updateEventDetector(@PathVariable Integer datapointId, @PathVariable Integer id, HttpServletRequest request, @RequestBody EventDetectorDTO body) {
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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<JsonPointEventDetector> createEventDetectorType(Integer datapointId, EventDetectorDTO body, HttpServletRequest request){
        LOG.info("/api/eventDetector/set/.../" + datapointId);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String error = validEventDetectorBodyCreate(datapointId, body);
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                return EventDetectorApiUtils.getDataPointById(datapointId, dataPointService).map(dataPoint -> {
                    boolean unique = dataPointService.isEventDetectorXidUnique(dataPoint.getId(), body.getXid(), Common.NEW_ID);
                    if (!unique) {
                        return new ResponseEntity<JsonPointEventDetector>(HttpStatus.CONFLICT);
                    }
                    PointEventDetectorVO pointEventDetectorVO = body.createPointEventDetectorVO(dataPoint);
                    JsonPointEventDetector jsonPointEventDetector = createEventDetector(dataPoint, pointEventDetectorVO);
                    return new ResponseEntity<>(jsonPointEventDetector, HttpStatus.OK);
                }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private JsonPointEventDetector createEventDetector(DataPointVO dataPointVO, PointEventDetectorVO ped) {

        List<PointEventDetectorVO> peds = dataPointVO.getEventDetectors();
        if (!peds.isEmpty())  {
            for (PointEventDetectorVO _ped : peds) {
                if (_ped.getXid().equals(ped.getXid())) {
                    return new JsonPointEventDetector(_ped.getId(), _ped.getXid(), _ped.getAlias(), _ped.getDetectorType(),
                            _ped.getAlarmLevel(), _ped.getLimit(), _ped.getDuration(), _ped.getDurationType(), _ped.isBinaryState(),
                            _ped.getMultistateState(), _ped.getChangeCount(), _ped.getAlphanumericState(), _ped.getWeight());
                }
            }
        }
        dataPointVO.getEventDetectors().add(ped);
        dataPointService.saveEventDetectors(dataPointVO);
        Common.ctx.getRuntimeManager().saveDataPoint(dataPointVO);
        int pedID = dataPointService.getDetectorId(ped.getXid(), dataPointVO.getId());
        return new JsonPointEventDetector(pedID, ped.getXid(), ped.getAlias(), ped.getDetectorType(), ped.getAlarmLevel(),
                ped.getLimit(), ped.getDuration(), ped.getDurationType(), ped.isBinaryState(), ped.getMultistateState(),
                ped.getChangeCount(), ped.getAlphanumericState(), ped.getWeight());
    }


    private ResponseEntity<String> updateEventDetectorType(Integer dataPointId, Integer eventDetectorId, EventDetectorDTO body, HttpServletRequest request) {
        LOG.info("/api/eventDetector/update/.../" + dataPointId + "/" + eventDetectorId);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String error = validEventDetectorBodyUpdate(dataPointId, eventDetectorId, body);
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().body(formatErrorsJson(error));
                }
                return getDataPointById(dataPointId, dataPointService)
                        .map(dataPoint -> findAndUpdateEventDetector(eventDetectorId, body, dataPoint))
                        .orElse(new ResponseEntity<>(formatErrorsJson("dataPoint not found"), HttpStatus.NOT_FOUND));

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> findAndUpdateEventDetector(int eventDetectorId, EventDetectorDTO eventDetectorBody,
                                                              DataPointVO dataPoint) {
        return getEventDetector(dataPoint, eventDetectorId)
                .map(toUpdate -> {
                    String error = EventDetectorApiUtils
                            .validEventDetector(toUpdate, dataPoint, eventDetectorBody);
                    if(!error.isEmpty())
                        return ResponseEntity.badRequest().body(formatErrorsJson(error));
                    return updateEventDetector(eventDetectorBody, dataPoint, toUpdate);
                }).orElse(new ResponseEntity<>(formatErrorsJson("eventDetector not found"), HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<String> updateEventDetector(EventDetectorDTO eventDetectorBody, DataPointVO dataPoint,
                                                       PointEventDetectorVO toUpdate) {
        updateValueEventDetector(toUpdate, eventDetectorBody);
        dataPointService.updateEventDetectorWithType(toUpdate);
        Common.ctx.getRuntimeManager().saveDataPoint(dataPoint);
        return new ResponseEntity<>("update", HttpStatus.OK);
    }
}
