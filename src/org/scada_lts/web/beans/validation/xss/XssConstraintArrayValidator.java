package org.scada_lts.web.beans.validation.xss;

import org.scada_lts.web.beans.validation.AbstractConstraintValidator;
import org.scada_lts.web.beans.validation.ScadaValidator;

public class XssConstraintArrayValidator extends AbstractConstraintValidator<XssProtect, String[]> {

    @Override
    public void validate(String[] array) throws Exception {
        ScadaValidator<String> validator = new XssValidator();
        for(String value: array) {
            validator.validate(value);
        }
    }
}
