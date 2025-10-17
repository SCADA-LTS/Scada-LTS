package org.scada_lts.web.beans.validation.css;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CssConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CssValid {
    String message() default "Wrong Css format detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
