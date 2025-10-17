package org.scada_lts.web.beans.validation.xss;

import org.scada_lts.web.beans.validation.AbstractConstraintValidator;
import org.scada_lts.web.beans.validation.ScadaValidator;

public class XssConstraintValidator extends AbstractConstraintValidator<XssProtect, String> {

    @Override
    public void validate(String value) throws Exception {
        ScadaValidator<String> validator = new XssValidator();
        validator.validate(value);
    }
}
