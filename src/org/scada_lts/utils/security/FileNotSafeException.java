package org.scada_lts.utils.security;

public class FileNotSafeException extends Exception {

    public FileNotSafeException(String message) {
        super("The file is not safe: " + message);
    }

    public FileNotSafeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotSafeException(Throwable cause) {
        super(cause);
    }

    public FileNotSafeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
