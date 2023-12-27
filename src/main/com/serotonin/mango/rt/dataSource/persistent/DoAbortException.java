package com.serotonin.mango.rt.dataSource.persistent;

import com.serotonin.web.i18n.LocalizableMessage;

public class DoAbortException extends Exception {
    private static final long serialVersionUID = 5178593744483624380L;

    private final LocalizableMessage localizableMessage;

    public DoAbortException(LocalizableMessage message) {
        this.localizableMessage = message;
    }

    public LocalizableMessage getLocalizableMessage() {
        return localizableMessage;
    }
}
