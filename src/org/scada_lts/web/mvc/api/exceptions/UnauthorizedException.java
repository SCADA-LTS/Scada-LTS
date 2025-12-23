package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ScadaApiException {

    /**
     * Creates an UnauthorizedException representing an HTTP 401 Unauthorized error with the detail "Access denied".
     *
     * @param instance a URI or identifier that references the specific error occurrence
     */
    public UnauthorizedException(String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.UNAUTHORIZED)
                .type(API_EXCEPTIONS + UnauthorizedException.class.getSimpleName())
                .title(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .detail("Access denied")
                .instance(instance)
                .build());
    }
}