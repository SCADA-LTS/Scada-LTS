package org.scada_lts.web.mvc.api.storungsandalarms;
/*
 * (c) 2020 hyski.mateusz@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class Validation {

    protected static final Log LOG = LogFactory.getLog(Validation.class);

    /*
    * do this "param" contain a number 0-9999
    *
    * if not as a result an information is given what is not correct
    *
     */
    static String validateDoParamIsIntegerAndBetween0And9999(String paramName,String param) {
        if( !param.matches(RegexSyntax.VALUE_BETWEEN_0_AND_9999)){
            return "Value "+paramName+" is not correct.It should be a number beetwen 0 and 9999.";
        }
        return null;
    }
    /*
    * do this parameteter contain correct value in specific date format
    *
     * if not as a result an information is given what is not correct
     */
    static String doGivenParameterHaveCorrectDateFormat(String parameter){
        if( !parameter.matches(RegexSyntax.DATE_FORMAT)){
            StringBuilder messagePart= new StringBuilder(parameter+" should contain value in format yyyy-mm-dd");
            LOG.info(parameter+" do not contain correct value."+messagePart.toString());
            return messagePart.toString();
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
