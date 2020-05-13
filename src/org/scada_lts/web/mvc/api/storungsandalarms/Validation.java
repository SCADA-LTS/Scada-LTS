package org.scada_lts.web.mvc.api.storungsandalarms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class Validation {

    protected static final Log LOG = LogFactory.getLog(Validation.class);

    protected boolean doGivenParameterIsValid(String paramName, String param){
        if( !param.equals("0") || param.equals("1")){
            LOG.info(paramName+" do not contain correct value."+paramName+" should contain value 0 or 1");
            return false;
        }
        return true;
    }

    protected ResponseEntity<String> backResponse(String parameterName){
        return new ResponseEntity<String>("Value "+parameterName+" is not valid", HttpStatus.OK);
    }

}
