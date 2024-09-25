package org.scada_lts.web.beans.validation;


import com.serotonin.mango.util.LoggingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;


public abstract class AbstractConstraintValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    private static final Logger LOG = LogManager.getLogger(AbstractConstraintValidator.class);

    @Override
    public void initialize(A constraintAnnotation) {
    }

    @Override
    public final boolean isValid(T value, ConstraintValidatorContext context) {
        try {
            beforeValidate(value);
        } catch (Exception e) {
            LOG.warn("ConstraintValidator: {}", LoggingUtils.causeInfo(e));
            addErrors(context, LoggingUtils.causeInfo(e));
            return false;
        }
        try {
            validate(value);
            return true;
        } catch (Exception e) {
            LOG.warn("ConstraintValidator: {}", LoggingUtils.causeInfo(e));
            addErrors(context, LoggingUtils.causeInfo(e));
            return false;
        }
    }

    public void beforeValidate(T value) throws Exception {}
    public abstract void validate(T value) throws Exception;

    private static void addErrors(ConstraintValidatorContext context, String msg) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Unexpected error: " + msg).addConstraintViolation();
    }
}
