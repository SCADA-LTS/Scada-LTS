package org.scada_lts.web.beans.validation.css;

import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.beans.validation.AbstractConstraintValidator;
import org.scada_lts.web.beans.validation.ScadaValidator;


public class CssConstraintValidator extends AbstractConstraintValidator<CssValid, String> {

    @Override
    public void initialize(CssValid constraintAnnotation) {
    }

    @Override
    public void beforeValidate(String value) throws Exception {
        if(StringUtils.isEmpty(value)) {
            throw new CssValidatorException("Empty");
        }
    }

    @Override
    public void validate(String value) throws Exception {
        ScadaValidator<String> validator = new SacCssValidator();
        validator.validate(value);
    }
}
