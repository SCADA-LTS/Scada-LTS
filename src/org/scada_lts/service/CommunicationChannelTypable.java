package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;

import java.util.Set;

public interface CommunicationChannelTypable {

    boolean validateAddress(String address);
    int getEventHandlerType();
    boolean sendMsg(EventInstance event, Set<String> addresses, String alias);
}
