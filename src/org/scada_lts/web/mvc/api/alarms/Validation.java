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

package org.scada_lts.web.mvc.api.alarms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.MessageFormat;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class Validation {

    protected static final Log LOG = LogFactory.getLog(Validation.class);

    static String validateNumberFormat(String paramName, String value) {
        if(RegexSyntax.VALUE_NOT_NUMERIC.matcher(value).find()){
            String msg = "Param name {0}, value {1} is not correct. It should be a number;";
            return MessageFormat.format(msg, paramName, value);
        }
        return "";
    }

    static String validateBetweenZeroTo9999(String paramName, String value) {
        String number = validateNumberFormat(paramName, value);
        if(!number.isEmpty()) {
            return number;
        }
        if(!value.matches(RegexSyntax.VALUE_BETWEEN_0_TO_9999.pattern())) {
            String msg = "Param name {0}, value {1} is not correct. It should be a number between 0 and 9999;";
            return MessageFormat.format(msg, paramName, value);
        }
        return "";
    }

    static String validateDateFormat(String value) {
        if(!value.matches(RegexSyntax.DATE_FORMAT.pattern())) {
            return value + " should contain value in format yyyy-mm-dd;";
        }
        return "";
    }
}
