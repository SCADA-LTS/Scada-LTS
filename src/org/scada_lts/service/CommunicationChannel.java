package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.joda.time.DateTime;

import java.util.Set;

public interface CommunicationChannel {

    int getChannelId();
    CommunicationChannelTypable getType();

    int getDailyLimitSentNumber();
    boolean isDailyLimitSent();
    String getSendingActivationCron();
    boolean isActiveFor(DateTime fireTime);
    boolean isActiveFor(EventInstance event);
    Set<String> getActiveAdresses(DateTime fireTime);
    Set<String> getActiveAdresses(EventInstance event);
    Set<String> getAllAdresses();
    boolean isCollectInactiveEvents();
    MailingList getData();

    static CommunicationChannel newChannel(MailingList mailingList, CommunicationChannelTypable type) {
        return new CommunicationChannelImpl(mailingList, type);
    }

    static CommunicationChannel newSmsChannel(MailingList mailingList) {
        return new CommunicationChannelImpl(mailingList, CommunicationChannelType.SMS);
    }

    static CommunicationChannel newEmailChannel(MailingList mailingList) {
        return new CommunicationChannelImpl(mailingList, CommunicationChannelType.EMAIL);
    }

    static CommunicationChannel newChannel(MailingList mailingList, EventHandlerVO eventHandler) {
        return new CommunicationChannelImpl(mailingList, CommunicationChannelType.getType(eventHandler.getHandlerType()));
    }

}
