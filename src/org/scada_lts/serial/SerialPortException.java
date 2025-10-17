package org.scada_lts.serial;

public class SerialPortException extends Exception {

    public SerialPortException() {
    }

    public SerialPortException(String message) {
        super(message);
    }

    public SerialPortException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerialPortException(Throwable cause) {
        super(cause);
    }

    public SerialPortException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
