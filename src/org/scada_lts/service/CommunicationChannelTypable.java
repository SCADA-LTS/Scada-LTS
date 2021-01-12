package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;

import java.util.Set;

public interface CommunicationChannelTypable {

    boolean validateAddress(String address);
    String removeWhitespace(String address);
    Set<String> formatAddresses(Set<String> addresses, String domain, String replaceRegex);
    Set<String> formatAddresses(Set<String> addresses, String domain);
    String getReplaceRegex();
    int getEventHandlerType();
    boolean sendMsg(EventInstance event, Set<String> addresses, String alias);
    boolean sendLimit(EventInstance event, Set<String> addresses, String alias);
}
