package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UserLockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
@Controller
@RequestMapping("/api/userlock/datapoint")
public class PointLockAPI {

    private static final Log LOG = LogFactory.getLog(PointLockAPI.class);

    UserLockService userLockService = new UserLockService();

    @RequestMapping(value = "/lock/{pointId}", method = RequestMethod.POST)
    public ResponseEntity<String> lockPoint(@PathVariable int pointId, HttpServletRequest request) {
        LOG.info("/api/userlock/datapoint/lock/" + pointId);
        try {
            User user = Common.getUser(request);
            userLockService.lockDataPoint(user, pointId);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/unlock/{pointId}", method = RequestMethod.POST)
    public ResponseEntity<String> unlockPoint(@PathVariable int pointId, HttpServletRequest request) {
        LOG.info("/api/userlock/datapoint/unlock/" + pointId);
        try {
            User user = Common.getUser(request);
            userLockService.unlockDataPoint(user, pointId);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/checkIfPointIsLocked/{pointId}", method = RequestMethod.GET)
    public ResponseEntity<String> checkIfPointIsLocked(@PathVariable int pointId, HttpServletRequest request) {
        LOG.info("/api/userlock/datapoint/checkIfPointIsLocked/" + pointId);
        try {
            userLockService.checkIfDataPointIsLocked(pointId);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
