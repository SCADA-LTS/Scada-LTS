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
    protected String doGivenParameterHaveCorrectDateFormat(String parameter){
        if( parameter.matches("^[0-9]{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")){
            StringBuilder messagePart= new StringBuilder(parameter+" should contain value in format yyyy-mm-dd");
            LOG.info(parameter+" do not contain correct value."+messagePart.toString());
            return messagePart.toString();
        }
        return null;
    }
    protected String doGivenParameterHaveValueFromScopeSince1To23(String parameter){
        String anyAlphanumericChar="[a-z^][^A-Z^]";
        if( parameter.matches(anyAlphanumericChar+"[1-9]{1}") || parameter.matches(anyAlphanumericChar+"[2]{1}[0-3]{1}")){
            StringBuilder messagePart= new StringBuilder(parameter+" should contain value from 1 to 23");
            LOG.info(parameter+" do not contain correct value."+messagePart);
            return messagePart.toString();
        }
        return null;
    }
    protected String doGivenParameterHaveValue0Or1(String paramName, String param){
        if( !param.equals("0") || param.equals("1")){
            LOG.info(paramName+" do not contain correct value."+paramName+" should contain value 0 or 1");
            return paramName+" should contain value 0 or 1";
        }
        return null;
    }
    protected ResponseEntity<String> backResponseWithValidationError(String parameterName,String validationMessage){
        return new ResponseEntity<String>("Value "+parameterName+" is not valid."+validationMessage, HttpStatus.OK);
    }
    protected ResponseEntity<String> backResponse(String parameterName){
        return new ResponseEntity<String>("Value "+parameterName+" is not valid.", HttpStatus.OK);
    }

}
