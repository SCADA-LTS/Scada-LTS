package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends ScadaApiException {

    /**
     * Create a ConflictException representing an HTTP 409 Conflict error.
     *
     * Constructs a ScadaErrorMessage with HTTP status CONFLICT; the error detail
     * embeds the causing exception's class name and a truncated exception message.
     *
     * @param ex the underlying exception that caused this conflict
     * @param instance a string identifying the error instance (typically a URI or unique id)
     */
    public ConflictException(Exception ex, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.CONFLICT)
                .type(API_EXCEPTIONS +  ConflictException.class.getSimpleName())
                .title(HttpStatus.CONFLICT.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + truncateMessage(ex.getMessage()))
                .instance(instance)
                .build());
    }
}