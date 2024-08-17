package org.scada_lts.web.mvc.api.components.cmp;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.RestApiSource;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.service.MultiChangesHistoryService;
import org.scada_lts.web.mvc.api.components.cmp.model.SetValuePointDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @autor grzegorz.bylica@gmail.com on 24.09.2019
 */
@Controller
public class ChangeDataAPI {
    private static final Log LOG = LogFactory.getLog(ChangeDataAPI.class);

    private final DataPointService dataPointService;
    private final MultiChangesHistoryService multiChangesHistoryService;

    public ChangeDataAPI(DataPointService dataPointService, MultiChangesHistoryService multiChangesHistoryService) {
        this.dataPointService = dataPointService;
        this.multiChangesHistoryService = multiChangesHistoryService;
    }

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
                        dataPointService.save(user, sv.getValue(), sv.getXid(), new RestApiSource());
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
                        dataPointService.save(user, sv.getValue(), sv.getXid(), new RestApiSource());
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
}
