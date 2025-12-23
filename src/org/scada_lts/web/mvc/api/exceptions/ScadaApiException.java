package org.scada_lts.web.mvc.api.exceptions;


import org.scada_lts.serorepl.utils.StringUtils;
import org.springframework.http.HttpStatus;

public class ScadaApiException extends RuntimeException {

    protected static final String API_EXCEPTIONS = "/api/exceptions/";

    private final ScadaErrorMessage errorMessage;

    public ScadaApiException(Exception ex, String instance, HttpStatus httpStatus) {
        this(ScadaErrorMessage.builder(httpStatus)
                .type("/api/exceptions/" + ScadaApiException.class.getSimpleName())
                .title(httpStatus.getReasonPhrase())
                .detail("exception", ex.getClass().getName() + " : " + truncateMessage(ex.getMessage()))
                .instance(instance)
                .build());
    }

    public ScadaApiException(ScadaErrorMessage errorMessage) {
        super(errorMessage.getDetail().toString());
        this.errorMessage = errorMessage;
    }

    public ScadaErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public static String truncateMessage(String message) {
        try {
            return StringUtils.truncate(message, "...", 255);
        } catch (Exception ex) {
            return message;
        }
    }
}
