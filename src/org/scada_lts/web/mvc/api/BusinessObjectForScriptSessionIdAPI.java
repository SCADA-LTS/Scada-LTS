package org.scada_lts.web.mvc.api;

import com.serotonin.mango.ScriptSessionAndUsers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Controller for cooperation with dwrScriptSessionId for Business Objects
 *
 * @author Mateusz Hyski  mateusz.hyski@softq.pl hyski.mateusz@gmail.com
 */
@Controller
public class BusinessObjectForScriptSessionIdAPI {

    private static final Log LOG = LogFactory.getLog(BusinessObjectForScriptSessionIdAPI.class);

    @RequestMapping(value = "/api/business_object_for_script_session_id/unlockObject/{dwrScriptSessionId}", method = RequestMethod.GET)
    public ResponseEntity<String> unlockObject(@PathVariable("dwrScriptSessionId") String dwrScriptSessionId, HttpServletRequest request) {
        LOG.info("/api/business_object_for_script_session_id/unlockObject/{dwrScriptSessionId} dwrScriptSessionId:" + dwrScriptSessionId);

        boolean result = Boolean.FALSE;
        try {
            result = ScriptSessionAndUsers.removeScriptSessionForObjectBySessionIdAndScriptSessionId(
                    request.getSession().getId(),
                    dwrScriptSessionId
            );
            LOG.info("Business object under dwrScriptSessionId: "+dwrScriptSessionId+" has "+(result?" ":" not ")+"been unlocked.");
            return new ResponseEntity<String>(String.valueOf(result),result?HttpStatus.OK:HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
}
