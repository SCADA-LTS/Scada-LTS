package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class NotFoundException extends ScadaApiException {

    /**
     * Creates a 404 Not Found SCADA API exception with a human-readable detail message and an instance identifier.
     *
     * @param detail   human-readable explanation of the error
     * @param instance URI or identifier that references this specific occurrence of the error
     */
    public NotFoundException(String detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.NOT_FOUND)
                .type(API_EXCEPTIONS +  NotFoundException.class.getSimpleName())
                .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build());
    }

    /**
     * Constructs a NotFoundException representing a 404 Not Found error with a structured detail object.
     *
     * @param detail   a map containing structured detail data to include in the error message
     * @param instance an instance identifier (typically a URI) that identifies this specific error occurrence
     */
    public NotFoundException(Map<String, Object> detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.NOT_FOUND)
                .type(API_EXCEPTIONS + NotFoundException.class.getSimpleName())
                .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                .detailObj(detail)
                .instance(instance)
                .build());
    }

    /**
     * Creates a NotFoundException representing a 404 Not Found error with the provided instance and detail map.
     *
     * @param instance a URI or identifier for the specific occurrence of the error
     * @param detail   a map of string key/value pairs to include as the error detail
     */
    public NotFoundException(String instance, Map<String, String> detail) {
        super(ScadaErrorMessage.builder(HttpStatus.NOT_FOUND)
                .type(API_EXCEPTIONS + NotFoundException.class.getSimpleName())
                .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build());
    }
}