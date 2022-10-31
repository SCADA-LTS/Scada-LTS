package org.scada_lts.ds.messaging.exception;

public class MessagingChannelException extends RuntimeException {

    public MessagingChannelException() {
    }

    public MessagingChannelException(String message) {
        super(message);
    }

    public MessagingChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessagingChannelException(Throwable cause) {
        super(cause);
    }

    public MessagingChannelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
