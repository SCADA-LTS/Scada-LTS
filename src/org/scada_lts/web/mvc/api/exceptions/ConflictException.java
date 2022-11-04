package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends ScadaApiException {

    public ConflictException(Exception ex, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.CONFLICT)
                .type("/api/exceptions/" + ConflictException.class.getSimpleName())
                .title(HttpStatus.CONFLICT.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + ex.getMessage())
                .instance(instance)
                .build());
    }
}
