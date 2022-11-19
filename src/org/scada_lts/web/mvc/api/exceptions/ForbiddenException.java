package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ScadaApiException {

    public ForbiddenException(String detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.FORBIDDEN)
                .type(API_EXCEPTIONS + ForbiddenException.class.getSimpleName())
                .title(HttpStatus.FORBIDDEN.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build());
    }
}
