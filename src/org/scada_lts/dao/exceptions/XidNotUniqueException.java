package org.scada_lts.dao.exceptions;

public class XidNotUniqueException extends Exception {
    public XidNotUniqueException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
