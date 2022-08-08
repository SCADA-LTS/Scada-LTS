package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.handlers.EmailHandlerRT;
import com.serotonin.mango.rt.event.handlers.EmailToSmsHandlerRT;
import com.serotonin.mango.rt.maint.work.AfterWork;
import com.serotonin.mango.util.SendUtils;
import com.serotonin.mango.vo.event.EventHandlerVO;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CommunicationChannelType implements CommunicationChannelTypable {

    EMAIL(EventHandlerVO.TYPE_EMAIL, "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])") {

        private String replaceRegex = "\\s";

        @Override
        public boolean sendMsg(EventInstance event, Set<String> addresses, String alias, AfterWork afterWork) {
            SendUtils.sendMsg(event, EmailHandlerRT.EmailNotificationType.ACTIVE, addresses, alias, afterWork);
            return true;
        }

        @Override
        public boolean sendLimit(EventInstance event, Set<String> addresses, String alias, AfterWork afterWork) {
            sendMsg(event, addresses, alias, afterWork);
            return true;
        }

        @Override
        public Set<String> formatAddresses(Set<String> addresses, String domain, String replaceRegex) {
            return CommunicationChannelType.replaceByRegex(addresses, replaceRegex, "");
        }

        @Override
        public Set<String> formatAddresses(Set<String> addresses, String domain) {
            return formatAddresses(addresses, domain, getReplaceRegex());
        }

        @Override
        public String getReplaceRegex() {
            return replaceRegex;
        }
    },
    SMS(EventHandlerVO.TYPE_SMS, "") {

        private String replaceRegex = "[^0-9+]";
        private String firstPlus = "^[+]{1}";

        @Override
        public boolean validateAddress(String address) {
            return !EMAIL.validateAddress(address);
        }

        @Override
        public boolean sendMsg(EventInstance event, Set<String> addresses, String alias, AfterWork afterWork) {
            SendUtils.sendMsg(event, EmailToSmsHandlerRT.SmsNotificationType.MSG_FROM_EVENT, addresses, alias, afterWork);
            return true;
        }

        @Override
        public boolean sendLimit(EventInstance event, Set<String> addresses, String alias, AfterWork afterWork) {
            SendUtils.sendMsg(event, EmailToSmsHandlerRT.SmsNotificationType.LIMIT, addresses, alias, afterWork);
            return true;
        }

        @Override
        public Set<String> formatAddresses(Set<String> addresses, String domain, String replaceRegex) {
            Set<String> formatted = CommunicationChannelType.replaceByRegex(addresses, replaceRegex, "");
            formatted = CommunicationChannelType.replaceByRegex(formatted, firstPlus, "00");
            return addedAtDomain(formatted,domain);
        }

        @Override
        public Set<String> formatAddresses(Set<String> addresses, String domain) {
            return formatAddresses(addresses, domain, getReplaceRegex());
        }

        @Override
        public String getReplaceRegex() {
            return replaceRegex;
        }

        private Set<String> addedAtDomain(Set<String> addresses, String domain) {
            return addresses.stream()
                    .map(a -> a.contains("@") ? a : a + "@" + domain)
                    .collect(Collectors.toSet());
        }
    };

    private final int eventHandlerType;
    private final String[] regexExps;

    CommunicationChannelType(int eventHandlerType, String... regexExps) {
        this.eventHandlerType = eventHandlerType;
        this.regexExps = regexExps;
    }

    @Override
    public int getEventHandlerType() {
        return eventHandlerType;
    }

    @Override
    public boolean validateAddress(String address) {
        if(address == null || address.isEmpty())
            return false;
        for(String pattern: regexExps) {
            if(address.toLowerCase().matches(pattern))
                return true;
        }
        return false;
    }

    @Override
    public String removeWhitespace(String address) {
        return address.replaceAll("\\s","");
    }

    public static CommunicationChannelTypable getType(int eventHandlerType) {
        return Stream.of(CommunicationChannelType.values())
                .filter(a -> a.getEventHandlerType() == eventHandlerType)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("There is no communication channel for this type of event handler. Type id: " + eventHandlerType));
    }

    private static Set<String> replaceByRegex(Set<String> addresses, String regex, String replacement) {
        return addresses.stream()
                .map(a -> a.replaceAll(regex,replacement))
                .collect(Collectors.toSet());
    }
}
