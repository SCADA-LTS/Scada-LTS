package org.scada_lts.dao.error;

public class EntityNotUniqueException extends RuntimeException {

    public EntityNotUniqueException(String errorMessage) {
        super(errorMessage);
    }

    public EntityNotUniqueException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
