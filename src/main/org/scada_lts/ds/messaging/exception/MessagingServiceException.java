package org.scada_lts.ds.messaging.exception;

public class MessagingServiceException extends RuntimeException {

    public MessagingServiceException() {
    }

    public MessagingServiceException(String message) {
        super(message);
    }

    public MessagingServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessagingServiceException(Throwable cause) {
        super(cause);
    }

    public MessagingServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
