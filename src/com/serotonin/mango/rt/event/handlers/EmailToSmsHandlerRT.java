package com.serotonin.mango.rt.event.handlers;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailToSmsHandlerRT extends EmailHandlerRT {

    private static final Log LOG = LogFactory.getLog(EmailHandlerRT.class);

    private final SystemSettingsService systemSettingsService;
    private final MailingListService mailingListService;

    @Deprecated
    public EmailToSmsHandlerRT(EventHandlerVO vo) {
        super(vo);
        this.systemSettingsService = new SystemSettingsService();
        this.mailingListService = new MailingListService();
    }

    public EmailToSmsHandlerRT(EventHandlerVO vo, ScheduledExecuteInactiveEventService service,
                               MailingListService mailingListService, SystemSettingsService systemSettingsService) {
        super(vo, service, mailingListService);
        this.systemSettingsService = systemSettingsService;
        this.mailingListService = mailingListService;
    }

    @Override
    protected Set<String> getActiveRecipients(EventInstance evt) {
        Set<String> addresses = mailingListService.getRecipientAddresses(getVo().getActiveRecipients(),
                new DateTime(evt.getActiveTimestamp()), CommunicationChannelType.SMS);
        String domain = systemSettingsService.getSMSDomain();
        return addresses.stream()
                .map(a -> a + "@" + domain)
                .collect(Collectors.toSet());
    }

    @Override
    protected Set<String> getInactiveRecipients(EventInstance evt) {
        Set<String> addresses = mailingListService.getRecipientAddresses(getVo().getInactiveRecipients(),
                new DateTime(evt.getActiveTimestamp()), CommunicationChannelType.SMS);
        String domain = systemSettingsService.getSMSDomain();
        return addresses.stream()
                .map(a -> a + "@" + domain)
                .collect(Collectors.toSet());
    }

    @Override
    protected Set<String> getActiveRecipients(EventInstance evt, CommunicationChannel channel) {
        if(channel.getType() == CommunicationChannelType.SMS) {
            Set<String> addresses = mailingListService.getRecipientAddresses(getVo().getActiveRecipients(),
                    new DateTime(evt.getActiveTimestamp()), channel);
            String domain = systemSettingsService.getSMSDomain();
            return addedAtDomain(addresses, domain);
        }
        LOG.warn("Event id: " + evt.getId() + " and emailList id: " + channel.getChannelId()+ " it is not related to SMS communication!");
        return Collections.emptySet();
    }

    private Set<String> addedAtDomain(Set<String> addresses, String domain) {
        return addresses.stream()
                .map(a -> a + "@" + domain)
                .collect(Collectors.toSet());
    }
}
