package org.scada_lts.web.mvc.api.exceptions;

import com.serotonin.web.dwr.DwrResponseI18n;
import org.springframework.http.HttpStatus;

import java.util.List;
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

    public BadRequestException(Exception ex, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type(API_EXCEPTIONS + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + ex.getMessage())
                .instance(instance)
                .build());
    }

    public BadRequestException(DwrResponseI18n response, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type(API_EXCEPTIONS + BadRequestException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail(toMapMessages(response))
                .instance(instance)
                .build());
    }
}
