package org.scada_lts.web.mvc.api.exceptions;

import com.serotonin.web.dwr.DwrResponseI18n;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.scada_lts.utils.ApiUtils.toMapMessages;

public class InternalServerErrorException extends ScadaApiException {

    public InternalServerErrorException(Exception ex, String instance) {
        super(ScadaErrorMessage.builder(HttpStatus.INTERNAL_SERVER_ERROR)
                .type(API_EXCEPTIONS + InternalServerErrorException.class.getSimpleName())
                .title(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + ex.getMessage())
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
