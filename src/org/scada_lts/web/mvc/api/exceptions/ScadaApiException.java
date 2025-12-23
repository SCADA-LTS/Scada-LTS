package org.scada_lts.web.mvc.api.exceptions;


import org.scada_lts.serorepl.utils.StringUtils;
import org.springframework.http.HttpStatus;

public class ScadaApiException extends RuntimeException {

    protected static final String API_EXCEPTIONS = "/api/exceptions/";

    private final ScadaErrorMessage errorMessage;

    /**
     * Constructs a ScadaApiException with a structured ScadaErrorMessage describing the provided exception.
     *
     * The error message includes the exception class name and a truncated form of the exception message, an HTTP status title, and the provided instance identifier.
     *
     * @param ex the original exception to describe
     * @param instance an identifier for the error instance (typically a URI or unique string)
     * @param httpStatus the HTTP status used to derive the error title and status code
     */
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

    /**
     * Access the structured error message for this API exception.
     *
     * @return the ScadaErrorMessage containing the error details for this exception
     */
    public ScadaErrorMessage getErrorMessage() {
        return errorMessage;
    }

    /**
     * Truncates a message to at most 255 characters and appends "..." if truncation occurs.
     *
     * @param message the message to truncate; may be null
     * @return the truncated message with "..." suffix when truncated, or the original message if truncation fails
     */
    public static String truncateMessage(String message) {
        try {
            return StringUtils.truncate(message, "...", 255);
        } catch (Exception ex) {
            return message;
        }
    }
}