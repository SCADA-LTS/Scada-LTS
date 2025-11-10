package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class NotFoundException extends ScadaApiException {

    public NotFoundException(String detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.NOT_FOUND)
                .type(API_EXCEPTIONS +  NotFoundException.class.getSimpleName())
                .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build());
    }

    public NotFoundException(Map<String, Object> detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type(API_EXCEPTIONS + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detailObj(detail)
                .instance(instance)
                .build());
    }
}
