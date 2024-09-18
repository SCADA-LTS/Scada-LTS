package org.scada_lts.web.mvc.api.css.validation.utils;

public class CssException extends Exception {

    public CssException() {
    }

    public CssException(String message) {
        super(message);
    }

    public CssException(String message, Throwable cause) {
        super(message, cause);
    }

    public CssException(Throwable cause) {
        super(cause);
    }

    public CssException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
