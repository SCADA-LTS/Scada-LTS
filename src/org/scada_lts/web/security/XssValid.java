package org.scada_lts.web.security;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = XssConstraintValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface XssValid {
    String message() default "Potential XSS detected in the request body.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
