package org.scada_lts.dao.exceptions;

public class EntityNotExistsException extends Exception {
    public EntityNotExistsException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
    public EntityNotExistsException(String errorMessage) {
        super(errorMessage);
    }
}
