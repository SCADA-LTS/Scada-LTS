package org.scada_lts.service;

import com.serotonin.mango.vo.event.EventHandlerVO;

import java.util.stream.Stream;

public enum CommunicationChannelType {

    EMAIL(EventHandlerVO.TYPE_EMAIL, "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"),
    SMS(EventHandlerVO.TYPE_SMS, "^(\\+\\d{1,3}[- ]?)?\\d{9}$", "^(\\+\\d{1,3}[- ]?)?\\d{10}$");

    private final int eventHandlerType;
    private final String[] regexExps;

    CommunicationChannelType(int eventHandlerType, String... regexExps) {
        this.eventHandlerType = eventHandlerType;
        this.regexExps = regexExps;
    }

    public int getEventHandlerType() {
        return eventHandlerType;
    }

    public boolean validateAddress(String address) {
        if(address == null || address.isEmpty())
            return false;
        for(String pattern: regexExps) {
            if(address.matches(pattern))
                return true;
        }
        return false;
    }

    public static CommunicationChannelType getType(int eventHandlerType) {
        return Stream.of(CommunicationChannelType.values())
                .filter(a -> a.getEventHandlerType() == eventHandlerType)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("There is no communication channel for this type of event handler."));
    }
}
