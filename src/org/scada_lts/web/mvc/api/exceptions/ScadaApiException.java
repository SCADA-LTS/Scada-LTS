package org.scada_lts.web.mvc.api.exceptions;


public class ScadaApiException extends RuntimeException {

    private ScadaErrorMessage errorMessage;

    public ScadaApiException(ScadaErrorMessage errorMessage) {
        super(errorMessage.getDetail().toString());
        this.errorMessage = errorMessage;
    }

    public ScadaErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
