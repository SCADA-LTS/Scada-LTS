package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ScadaApiException {

    public NotFoundException(String detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.NOT_FOUND)
                .type("/api/exceptions/" + NotFoundException.class.getSimpleName())
                .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build());
    }
}
