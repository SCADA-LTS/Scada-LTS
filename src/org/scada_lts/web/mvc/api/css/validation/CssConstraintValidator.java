package org.scada_lts.web.mvc.api.css.validation;


import com.serotonin.mango.util.LoggingUtils;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.css.validation.utils.CssValidator;
import org.scada_lts.web.mvc.api.css.validation.utils.W3cCssValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class CssConstraintValidator implements ConstraintValidator<CssValid, String> {

    @Override
    public void initialize(CssValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            addErrors(context, "Empty");
            return false;
        }
        try {
            CssValidator validator = new W3cCssValidator();
            validator.validate(value);
            return true;
        }catch (Exception e) {
            addErrors(context, LoggingUtils.causeInfo(e));
            return false;
        }
    }

    private static void addErrors(ConstraintValidatorContext context, String msg) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Unexpected error: " + msg).addConstraintViolation();
    }
}
