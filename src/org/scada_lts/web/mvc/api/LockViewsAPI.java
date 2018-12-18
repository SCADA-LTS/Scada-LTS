package org.scada_lts.web.mvc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.facade.Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * REST API controller and responsibility :
 * -check view state
 * -get details of user who locked view(etc. who work with view at the time when somebody else tries edit view)
 * -break edit action - in other words take control on view with show information for other user about this action
 *
 * @author Mateusz Hyski {@link "mailto:mateusz.hyski@softq.pl;hyski.mateusz@gmail.com","ScadaLTS"}
 */
@Controller
public class LockViewsAPI {

    private static final Log LOG = LogFactory.getLog(LockViewsAPI.class);


    /**
     * Facade here is used to separate logic level from REST API
     */
    @Autowired
    private Facade facade;

    /**
     * with only xidName we check view availability
     *
     * @param xidName
     * @param request
     * @return ResponseEntity<String> 1-unavailable,0 available
     */
    @RequestMapping(value = "/api/lockviews/availableunavailableview/{xidName}", method = RequestMethod.GET)
    public ResponseEntity<String> availableunavailableview(@PathVariable("xidName") String xidName, HttpServletRequest request) {
        LOG.info("/api/lockviews/availableunavailableview/"+xidName);

        String result="";
        try {
            result = facade.checkAvailabibityView(xidName);
            return new ResponseEntity<String>(result,HttpStatus.OK);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * user information downloaded like: {"username","viewXid","sessionId","timestamp"}
     *
     * @param xidName
     * @param request
     * @return ResponseEntity<String>
     */
    @RequestMapping(value = "/api/lockviews/lockedby/{xidName}", method = RequestMethod.GET)
    public ResponseEntity<String> lockedby(@PathVariable("xidName") String xidName, HttpServletRequest request) {
        LOG.info("/api/lockviews/lockedby/"+xidName);


        try {
            return new ResponseEntity<>(facade.getOwnerOfEditView(xidName),HttpStatus.OK);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * just break edit operation on view means in other meaning remove control on view
     *
     * @param xidName
     * @param request
     */
    @RequestMapping(value = "/api/lockviews/breakeditactionforuser/{xidName}", method = RequestMethod.GET)
    public ResponseEntity<String>  breakEditActionForUser(@PathVariable("xidName") String xidName, HttpServletRequest request) {

        LOG.info("/api/lockviews/breakeditactionforuser/"+xidName);
        LOG.info("View with xid="+xidName+" will be removed from map and will available to edit");

        try {
            facade.breakEditActionForUser(xidName);
            LOG.info("View with xid="+xidName+" is available to edit");
            return new ResponseEntity<String>("ok",HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

}
