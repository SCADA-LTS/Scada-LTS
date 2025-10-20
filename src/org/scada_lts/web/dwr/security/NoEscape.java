package org.scada_lts.web.dwr.security;

import java.lang.annotation.*;

/**
 * Marks a field or method parameter as safe to carry raw HTML.
 * Use sparingly: only for server-generated HTML fragments you really intend to render via innerHTML.
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoEscape {}
