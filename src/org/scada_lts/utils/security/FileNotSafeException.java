package org.scada_lts.utils.security;

public class FileNotSafeException extends Exception {

    public FileNotSafeException(String message) {
        super("The file is not safe: " + message);
    }

}
