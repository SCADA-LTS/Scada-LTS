package com.serotonin.mango.rt.publish.persistent;

import com.serotonin.web.i18n.LocalizableMessage;

public class PersistentAbortException extends Exception {
    private static final long serialVersionUID = 5178593744483624380L;

    private final LocalizableMessage localizableMessage;

    public PersistentAbortException(LocalizableMessage message) {
        this.localizableMessage = message;
    }

    public LocalizableMessage getLocalizableMessage() {
        return localizableMessage;
    }
}
