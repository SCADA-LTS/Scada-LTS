package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.reactivation.MenagerReactivation;
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

    @RequestMapping(value = "/api/check-reactivation/{idDs}", method = RequestMethod.GET)
    public ResponseEntity<String> checkReactivation(@PathVariable(name="idDs") int idDs, HttpServletRequest request) {

        LOG.info("/api/check-reactivation/{idDs} idDs:" + idDs);
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                long time = MenagerReactivation.getInstance().getTimeToNextFire(idDs);
                return new ResponseEntity<String>(""+time, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
