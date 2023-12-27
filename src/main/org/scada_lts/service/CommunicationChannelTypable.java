package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.maint.work.AfterWork;

import java.util.Set;

public interface CommunicationChannelTypable {

    boolean validateAddress(String address);
    String removeWhitespace(String address);
    Set<String> formatAddresses(Set<String> addresses, String domain, String replaceRegex);
    Set<String> formatAddresses(Set<String> addresses, String domain);
    String getReplaceRegex();
    int getEventHandlerType();
    void sendMsg(EventInstance event, Set<String> addresses, String alias, AfterWork afterWork);
    void sendLimit(EventInstance event, Set<String> addresses, String alias, AfterWork afterWork);
}
