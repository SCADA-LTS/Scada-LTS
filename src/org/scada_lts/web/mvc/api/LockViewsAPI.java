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
 * @author Mateusz Hyski hyski.mateusz@gmail.com
 */
@Controller
public class LockViewsAPI {

    private static final Log LOG = LogFactory.getLog(LockViewsAPI.class);

    @Autowired
    private Facade facade;

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
    @RequestMapping(value = "/api/lockviews/lockedby/{xidName}", method = RequestMethod.GET)
    public ResponseEntity<String> lockedby(@PathVariable("xidName") String xidName, HttpServletRequest request) {
        LOG.info("/api/lockviews/lockedby/"+xidName);


        try {
            return new ResponseEntity<>(facade.getOwnerOfEditView(xidName).toString(),HttpStatus.OK);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @RequestMapping(value = "/api/lockviews/breakeditactionforuser/{xidName}", method = RequestMethod.GET)
    public void  breakEditActionForUser(@PathVariable("xidName") String xidName, HttpServletRequest request) {
        LOG.info("/api/lockviews/breakeditactionforuser/"+xidName);
        LOG.info("View with xid="+xidName+" will be removed from map and will available to edit");
        facade.breakEditActionForUser(xidName);
        LOG.info("View with xid="+xidName+" is available to edit");
    }

}
