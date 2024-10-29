package org.scada_lts.web.beans.validation;

public class ScadaValidatorException extends Exception {

    public ScadaValidatorException() {
    }

    public ScadaValidatorException(String message) {
        super(message);
    }

    public ScadaValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScadaValidatorException(Throwable cause) {
        super(cause);
    }

    public ScadaValidatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
