package org.scada_lts.web.mvc.api.css.validation.utils;

public interface CssValidator {

    void validate(String style) throws CustomCssException;
}
