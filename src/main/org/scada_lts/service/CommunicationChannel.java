package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.joda.time.DateTime;
import org.scada_lts.mango.service.SystemSettingsService;

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

    static CommunicationChannel newChannel(MailingList mailingList, CommunicationChannelTypable type,
                                           SystemSettingsService systemSettingsService) {
        return new CommunicationChannelImpl(mailingList, type, systemSettingsService);
    }

    static CommunicationChannel newSmsChannel(MailingList mailingList, SystemSettingsService systemSettingsService) {
        return new CommunicationChannelImpl(mailingList, CommunicationChannelType.SMS, systemSettingsService);
    }

    static CommunicationChannel newEmailChannel(MailingList mailingList, SystemSettingsService systemSettingsService) {
        return new CommunicationChannelImpl(mailingList, CommunicationChannelType.EMAIL, systemSettingsService);
    }

    static CommunicationChannel newChannel(MailingList mailingList, EventHandlerVO eventHandler, SystemSettingsService systemSettingsService) {
        return new CommunicationChannelImpl(mailingList, CommunicationChannelType.getType(eventHandler.getHandlerType()), systemSettingsService);
    }

}
