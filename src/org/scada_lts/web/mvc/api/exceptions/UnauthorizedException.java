package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ScadaApiException {

    public UnauthorizedException(String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.UNAUTHORIZED)
                .type("/api/exceptions/" + UnauthorizedException.class.getSimpleName())
                .title(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .detail("Access only admin")
                .instance(instance)
                .build());
    }
}
