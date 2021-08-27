package org.scada_lts.web.mvc.api.components.cmp;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.service.MultiChangesHistoryService;
import org.scada_lts.web.mvc.api.components.cmp.model.SetValueErrorsDTO;
import org.scada_lts.web.mvc.api.components.cmp.model.SetValuePointDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static org.scada_lts.utils.ChangeDataApiUtils.checkIfValuesCanBeSet;

/**
 * @autor grzegorz.bylica@gmail.com on 24.09.2019
 */
@Controller
public class ChangeDataAPI {
    private static final Log LOG = LogFactory.getLog(ChangeDataAPI.class);

    @Autowired
    private DataPointService dataPointService;

    @Resource
    private PointValueService pointValueService;

    @Resource
    private MultiChangesHistoryService multiChangesHistoryService;


    @RequestMapping(value = "/api/cmp/set/{xIdViewAndIdCmp}/{interpretedState}", method = RequestMethod.POST)
    public ResponseEntity<SetValuePointDTO[]> set(
            @RequestBody SetValuePointDTO[] xIDsValues,
            @PathVariable String xIdViewAndIdCmp,
            @PathVariable String interpretedState,
            HttpServletRequest request) {

        LOG.info("/api/cmp/set pxIdViewAndIdCmp:"+xIdViewAndIdCmp+" interpretedState:"+interpretedState+" xIDSsValues:" + xIDsValues.toString());

        try {
            User user = Common.getUser(request);

            if (user != null) {
                for (SetValuePointDTO sv : xIDsValues) {
                    try {
                        dataPointService.saveAPI(user, sv.getValue(), sv.getXid());
                    } catch (Exception e) {
                        sv.setError(e.getMessage());
                    }
                }

                multiChangesHistoryService.addToCmpHistory(
                        user.getId(),
                        xIdViewAndIdCmp,
                        interpretedState,
                        xIDsValues
                );


                return new ResponseEntity<>(xIDsValues, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(e);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/cmp/set/noadit/{xIdViewAndIdCmp}/{interpretedState}", method = RequestMethod.POST)
    public ResponseEntity<SetValuePointDTO[]> setNoAdit(
            @RequestBody SetValuePointDTO[] xIDsValues,
            @PathVariable String xIdViewAndIdCmp,
            @PathVariable String interpretedState,
            HttpServletRequest request) {

        LOG.info("/api/cmp/set pxIdViewAndIdCmp:"+xIdViewAndIdCmp+" interpretedState:"+interpretedState+" xIDSsValues:" + xIDsValues.toString());

        try {
            User user = Common.getUser(request);

            if (user != null) {
                for (SetValuePointDTO sv : xIDsValues) {
                    try {
                        dataPointService.saveAPI(user, sv.getValue(), sv.getXid());
                    } catch (Exception e) {
                        sv.setError(e.getMessage());
                    }
                }

                return new ResponseEntity<>(xIDsValues, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(e);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/api/cmp/setsAllOrNone", produces = "application/json")
    public ResponseEntity<SetValueErrorsDTO> setValuesAllOrNone(@RequestBody SetValuePointDTO[] xIDsValues, HttpServletRequest request) {
        LOG.info("POST:/api/cmp/setsAllOrNone");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                SetValueErrorsDTO errors = checkIfValuesCanBeSet(user, xIDsValues);
                if (!errors.getErrors().isEmpty()) {
                    return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                for (SetValuePointDTO sv : xIDsValues) {
                    dataPointService.savePointValue(user, sv.getValue(), sv.getXid());
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/api/cmp/sets", produces = "application/json")
    public ResponseEntity<SetValueErrorsDTO> setValues(@RequestBody SetValuePointDTO[] xIDsValues, HttpServletRequest request) {
        LOG.info("POST:/api/cmp/sets");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                SetValueErrorsDTO errors = checkIfValuesCanBeSet(user, xIDsValues);
                for (SetValuePointDTO sv : xIDsValues) {
                    if (errors.getErrors().stream().noneMatch(o -> o.getXid().equals(sv.getXid())))
                        dataPointService.savePointValue(user, sv.getValue(), sv.getXid());
                }
                return new ResponseEntity<>(errors, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
