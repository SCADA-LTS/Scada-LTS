package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends ScadaApiException {

    public InternalServerErrorException(Exception ex, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.INTERNAL_SERVER_ERROR)
                .type("/api/exceptions/" + InternalServerErrorException.class.getSimpleName())
                .title(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + ex.getMessage())
                .instance(instance)
                .build());
    }
}
