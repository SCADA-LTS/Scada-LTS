package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.joda.time.DateTime;

import java.util.Set;

public interface CommunicationChannel {

    int getChannelId();
    CommunicationChannelType getType();

    int getDailyLimitSentNumber();
    boolean isDailyLimitSent();
    String getSendingActivationCron();
    boolean isActiveFor(DateTime fireTime);
    boolean isActiveFor(EventInstance event);
    Set<String> getActiveAdresses(DateTime fireTime);
    Set<String> getActiveAdresses(EventInstance event);
    boolean isCollectInactiveEvents();

    static CommunicationChannel newChannel(MailingList mailingList, CommunicationChannelType type) {
        return new CommunicationChannelImpl(mailingList, type);
    }

    static CommunicationChannel newEmailChannel(MailingList mailingList) {
        return new CommunicationChannelImpl(mailingList, CommunicationChannelType.EMAIL);
    }

    static CommunicationChannel newSmsChannel(MailingList mailingList) {
        return new CommunicationChannelImpl(mailingList, CommunicationChannelType.SMS);
    }
}
