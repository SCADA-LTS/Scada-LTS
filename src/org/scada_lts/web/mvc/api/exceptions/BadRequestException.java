package org.scada_lts.web.mvc.api.exceptions;

import com.serotonin.web.dwr.DwrResponseI18n;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.scada_lts.utils.ApiUtils.toMapMessages;

public class BadRequestException extends ScadaApiException {

    public BadRequestException(Map<String, String> detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type(API_EXCEPTIONS + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build());
    }

    public BadRequestException(String detail, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type(API_EXCEPTIONS + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build());
    }

    /**
     * Constructs a BadRequestException with an HTTP 400 error payload whose detail describes the provided exception.
     *
     * The error detail contains an "exception" entry formatted as "<ExceptionClassName> : <truncatedMessage>".
     *
     * @param ex the exception whose class and message are included in the error detail
     * @param instance an identifier (typically the request instance URI) associated with this error
     */
    public BadRequestException(Exception ex, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type(API_EXCEPTIONS + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + truncateMessage(ex.getMessage()))
                .instance(instance)
                .build());
    }

    /**
     * Creates a BadRequestException whose error detail is populated from a DWR i18n response.
     *
     * The constructed error has HTTP 400 status, a standardized API exception type and title,
     * and a detail map extracted from the provided DWR response.
     *
     * @param response the DWR localized response containing field or validation messages to include in the error detail
     * @param instance a unique instance identifier for this error occurrence
     */
    public BadRequestException(DwrResponseI18n response, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type(API_EXCEPTIONS + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail(toMapMessages(response))
                .instance(instance)
                .build());
    }

    /**
     * Creates a BadRequestException that represents an HTTP 400 error whose detail is an object.
     *
     * @param instance a string identifying the error instance (typically an instance URI or unique id)
     * @param detail   a map representing the error detail object to include in the response payload
     */
    public BadRequestException(String instance, Map<String, Object> detail) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type(API_EXCEPTIONS + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detailObj(detail)
                .instance(instance)
                .build());
    }
}