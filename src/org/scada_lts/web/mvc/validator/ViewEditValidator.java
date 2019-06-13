/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.web.mvc.validator;

import org.scada_lts.web.mvc.form.ViewEditForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.serotonin.mango.view.View;
import com.serotonin.web.dwr.DwrMessageI18n;
import com.serotonin.web.dwr.DwrResponseI18n;

@Component
public class ViewEditValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
       return ViewEditForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DwrResponseI18n response = new DwrResponseI18n();
        View view = ((ViewEditForm)target).getView();
        view.validate(response);
        reject(errors, "view.", response);
    }
    
    public static void reject(final Errors errors, final String errorCode, final Object... args) {
        errors.reject(errorCode, args, "???" + errorCode + "(10)???");
    }
    
    public static void rejectValue(final Errors errors, final String field, final String errorCode, final Object... args) {
        errors.rejectValue(field, errorCode, args, "???" + errorCode + "(11)???");
    }
    
    public static void reject(final Errors errors, final String fieldPrefix, final DwrResponseI18n response) {
        for (final DwrMessageI18n m : response.getMessages()) {
            if (m.getContextKey() != null) {
                rejectValue(errors, fieldPrefix + m.getContextKey(), m.getContextualMessage().getKey(), m.getContextualMessage().getArgs());
            }
            else {
                reject(errors, m.getGenericMessage().getKey(), m.getGenericMessage().getArgs());
            }
        }
    }
}
