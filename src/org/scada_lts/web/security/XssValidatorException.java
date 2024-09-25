package org.scada_lts.web.security;

import org.scada_lts.web.beans.validation.ScadaValidatorException;

public class XssValidatorException extends ScadaValidatorException {

    public XssValidatorException() {
    }

    public XssValidatorException(String message) {
        super(message);
    }

    public XssValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public XssValidatorException(Throwable cause) {
        super(cause);
    }

    public XssValidatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
