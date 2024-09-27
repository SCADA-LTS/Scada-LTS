package org.scada_lts.web.beans.validation.xss;

import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.beans.validation.AbstractConstraintValidator;
import org.scada_lts.web.beans.validation.ScadaValidator;

public class XssConstraintValidator extends AbstractConstraintValidator<XssProtect, String> {

    @Override
    public void beforeValidate(String value) throws Exception {
        if (StringUtils.isEmpty(value)) {
            throw new XssValidatorException("Input is empty");
        }
    }

    @Override
    public void validate(String value) throws Exception {
        ScadaValidator<String> validator = new RegexXssValidator();
        validator.validate(value);
    }
}
