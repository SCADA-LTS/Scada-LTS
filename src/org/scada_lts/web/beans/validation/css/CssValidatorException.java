package org.scada_lts.web.beans.validation.css;

import org.scada_lts.web.beans.validation.ScadaValidatorException;

public class CssValidatorException extends ScadaValidatorException {

    public CssValidatorException() {
    }

    public CssValidatorException(String message) {
        super(message);
    }

    public CssValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CssValidatorException(Throwable cause) {
        super(cause);
    }

    public CssValidatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
