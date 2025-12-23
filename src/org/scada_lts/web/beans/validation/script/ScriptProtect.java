package org.scada_lts.web.beans.validation.script;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {ScriptConstraintListValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptProtect {
    String message() default "Potential XSS detected in the request body.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
