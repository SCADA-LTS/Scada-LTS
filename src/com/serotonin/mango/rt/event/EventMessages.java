package com.serotonin.mango.rt.event;

import com.serotonin.web.i18n.LocalizableMessage;

public class EventMessages {
    private LocalizableMessage message;
    private LocalizableMessage messageSms;

    public EventMessages(LocalizableMessage message, LocalizableMessage messageSms) {
        this.message = message;
        this.messageSms = messageSms;
    }

    public EventMessages() {
    }

    public LocalizableMessage getMessage() {
        return message;
    }

    public void setMessage(LocalizableMessage message) {
        this.message = message;
    }

    public LocalizableMessage getMessageSms() {
        return messageSms;
    }

    public void setMessageSms(LocalizableMessage messageSms) {
        this.messageSms = messageSms;
    }
}
