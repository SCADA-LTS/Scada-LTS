package org.scada_lts.web.mvc.api.css;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CssValidator implements ConstraintValidator<CssValid, CssStyle> {

    @Override
    public void initialize(CssValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(CssStyle value, ConstraintValidatorContext context) {
        return value != null && !value.getContent().trim().isEmpty();
    }
}