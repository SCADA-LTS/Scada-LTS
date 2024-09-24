package org.scada_lts.web.mvc.api.css.validation.utils;

public class CustomCssException extends Exception {

    public CustomCssException() {
    }

    public CustomCssException(String message) {
        super(message);
    }

    public CustomCssException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomCssException(Throwable cause) {
        super(cause);
    }

    public CustomCssException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
