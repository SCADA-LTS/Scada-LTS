package org.scada_lts.web.mvc.api.css;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CssValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CssValid {
    String message() default "Wrong Css format detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
