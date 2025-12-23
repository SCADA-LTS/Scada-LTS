package org.scada_lts.web.mvc.api.exceptions;

import com.serotonin.web.dwr.DwrResponseI18n;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.scada_lts.utils.ApiUtils.toMapMessages;

public class InternalServerErrorException extends ScadaApiException {

    /**
     * Create an InternalServerErrorException representing an HTTP 500 error that includes the provided exception's type and truncated message in the error detail.
     *
     * The constructed error message sets the type to the API exceptions namespace combined with this class name, the title to the HTTP 500 reason phrase, and the detail to a single "exception" entry containing the exception's class name and a truncated form of its message.
     *
     * @param ex the exception whose class and message will be included in the error detail (message will be truncated)
     * @param instance an identifier for this error instance (typically a URI)
     */
    public InternalServerErrorException(Exception ex, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.INTERNAL_SERVER_ERROR)
                .type(API_EXCEPTIONS + InternalServerErrorException.class.getSimpleName())
                .title(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + truncateMessage(ex.getMessage()))
                .instance(instance)
                .build());
    }

    public InternalServerErrorException(DwrResponseI18n response, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.INTERNAL_SERVER_ERROR)
                .type(API_EXCEPTIONS + InternalServerErrorException.class.getSimpleName())
                .title(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .detail(toMapMessages(response))
                .instance(instance)
                .build());
    }

    public InternalServerErrorException(List<String> response, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.INTERNAL_SERVER_ERROR)
                .type(API_EXCEPTIONS + InternalServerErrorException.class.getSimpleName())
                .title(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .detail(toMapMessages(response))
                .instance(instance)
                .build());
    }
}