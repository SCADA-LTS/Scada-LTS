package org.scada_lts.ds.polling.exception;

public class MasterException extends RuntimeException {

    public MasterException() {
    }

    public MasterException(String message) {
        super(message);
    }

    public MasterException(String message, Throwable cause) {
        super(message, cause);
    }

    public MasterException(Throwable cause) {
        super(cause);
    }

    public MasterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
