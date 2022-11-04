package org.scada_lts.web.mvc.api.exceptions;


import org.springframework.http.HttpStatus;

public class ScadaApiException extends RuntimeException {

    private final ScadaErrorMessage errorMessage;

    public ScadaApiException(Exception ex, String instance, HttpStatus httpStatus) {
        this(ScadaErrorMessage.builder(httpStatus)
                .type("/api/exceptions/" + ScadaApiException.class.getSimpleName())
                .title(httpStatus.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + ex.getMessage())
                .instance(instance)
                .build());
    }

    public ScadaApiException(ScadaErrorMessage errorMessage) {
        super(errorMessage.getDetail().toString());
        this.errorMessage = errorMessage;
    }

    public ScadaErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
