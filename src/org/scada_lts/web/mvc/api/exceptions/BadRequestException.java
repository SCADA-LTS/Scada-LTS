package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class BadRequestException extends ScadaApiException {

    public BadRequestException(Map<String, String> detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type("/api/exceptions/" + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build());
    }

    public BadRequestException(String detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type("/api/exceptions/" + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build());
    }

    public BadRequestException(Exception ex, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type("/exception/" + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + ex.getMessage())
                .instance(instance)
                .build());
    }
}
