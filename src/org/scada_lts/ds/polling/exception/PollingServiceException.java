package org.scada_lts.ds.polling.exception;

public class PollingServiceException extends RuntimeException {

    public PollingServiceException() {
    }

    public PollingServiceException(String message) {
        super(message);
    }

    public PollingServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PollingServiceException(Throwable cause) {
        super(cause);
    }

    public PollingServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
