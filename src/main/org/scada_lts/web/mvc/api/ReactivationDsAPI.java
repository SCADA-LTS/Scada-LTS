package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.reactivation.ReactivationManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @autor grzegorz.bylica@gmail.com on 08.11.18
 */
@Controller
public class ReactivationDsAPI {

    private static final Log LOG = LogFactory.getLog(ReactivationDsAPI.class);

    private static final String REACTIVATION_RESPONSE_STARTED = "started";
    private static final String REACTIVATION_RESPONSE_STOPED = "stoped";
    private static final String REACTIVATION_RESPONSE_NO_CHANGE = "nothing_changed";


    @RequestMapping(value = "/api/check-time-reactivation/{idDs}", method = RequestMethod.GET)
    public ResponseEntity<String> checkTimeReactivation(@PathVariable(name="idDs") int idDs, HttpServletRequest request) {

        LOG.info("/api/check-reactivation/{idDs} idDs:" + idDs);
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                long time = ReactivationManager.getInstance().getTimeToNextFire(idDs);
                return new ResponseEntity<String>(""+time, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/check-enable-reactivation/{idDs}", method = RequestMethod.GET)
    public ResponseEntity<String> checkEnableReactivation(@PathVariable(name="idDs") int idDs, HttpServletRequest request) {

        LOG.info("/api/check-enable-reactivation/{idDs} idDs:" + idDs);

        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                boolean sleep = ReactivationManager.getInstance().checkDsIsSleep(idDs);
                return new ResponseEntity<String>(""+sleep, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/disable-reactivation/{idDs}", method = RequestMethod.POST)
    public ResponseEntity<String> disableReactivation(@PathVariable(name="idDs") int idDs, HttpServletRequest request) {

        LOG.info("/api/disable-reactivation/{idDs} idDs:" + idDs);

        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                boolean sleep = ReactivationManager.getInstance().checkDsIsSleep(idDs);
                String result="";
                if (sleep) {
                    ReactivationManager.getInstance().stopReactivation(idDs);
                    result = REACTIVATION_RESPONSE_STOPED;
                } else {
                    result = REACTIVATION_RESPONSE_NO_CHANGE;
                }
                return new ResponseEntity<String>(""+result, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @RequestMapping(value = "/api/enable-reactivation/{idDs}", method = RequestMethod.POST)
    public ResponseEntity<String> enableReactivation(@PathVariable(name="idDs") int idDs, HttpServletRequest request) {

        LOG.info("/api/enable-reactivation/{idDs} idDs:" + idDs);

        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                boolean sleep = ReactivationManager.getInstance().checkDsIsSleep(idDs);
                String result="";
                if (!sleep) {
                    ReactivationManager.getInstance().startReactivation(idDs);
                    result = REACTIVATION_RESPONSE_STARTED;
                } else {
                    result = REACTIVATION_RESPONSE_NO_CHANGE;
                }
                return new ResponseEntity<String>(""+result, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
