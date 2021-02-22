package com.serotonin.mango.rt.event;

import com.serotonin.web.i18n.LocalizableMessage;

public class EventMessages {
    private LocalizableMessage message;
    private LocalizableMessage shortMessage;

    public EventMessages(LocalizableMessage message, LocalizableMessage shortMessage) {
        this.message = message;
        this.shortMessage = shortMessage;
    }

    public EventMessages() {
    }

    public LocalizableMessage getMessage() {
        return message;
    }

    public void setMessage(LocalizableMessage message) {
        this.message = message;
    }

    public LocalizableMessage getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(LocalizableMessage shortMessage) {
        this.shortMessage = shortMessage;
    }
}
