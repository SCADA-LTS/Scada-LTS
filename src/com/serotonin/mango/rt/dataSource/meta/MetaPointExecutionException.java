package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.web.i18n.LocalizableMessage;

public class MetaPointExecutionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final LocalizableMessage message;

    public MetaPointExecutionException(LocalizableMessage message) {
        this.message = message;
    }

    public LocalizableMessage getErrorMessage() {
        return message;
    }
}
