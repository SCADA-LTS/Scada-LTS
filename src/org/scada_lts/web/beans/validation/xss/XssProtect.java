package org.scada_lts.web.beans.validation.xss;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = XssConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface XssProtect {
    String message() default "Potential XSS detected in the request body.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
