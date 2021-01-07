package com.serotonin.mango.rt.event.handlers;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;

import java.util.Set;
import java.util.stream.Collectors;

public class EmailToSmsHandlerRT extends EmailHandlerRT {

    private static final Log LOG = LogFactory.getLog(EmailToSmsHandlerRT.class);

    public enum SmsNotificationType implements NotificationType {
        ACTIVE("activeSms", "ftl.subject.active");

        String file;
        String key;

        SmsNotificationType(String file, String key) {
            this.file = file;
            this.key = key;
        }

        public String getFile() {
            return file;
        }

        public String getKey() {
            return key;
        }
    }

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
        return addedAtDomain(addresses);
    }

    @Override
    protected Set<String> getInactiveRecipients(EventInstance evt) {
        Set<String> addresses = mailingListService.getRecipientAddresses(getVo().getInactiveRecipients(),
                new DateTime(evt.getActiveTimestamp()), CommunicationChannelType.SMS);
        return addedAtDomain(addresses);
    }

    @Override
    protected void sendEmail(EventInstance evt, Set<String> addresses) {
        CommunicationChannelType.SMS.sendMsg(evt, addresses, getVo().getAlias());
    }

    private Set<String> addedAtDomain(Set<String> addresses) {
        String domain = systemSettingsService.getSMSDomain();
        return addresses.stream()
                .map(a -> a + "@" + domain)
                .collect(Collectors.toSet());
    }
}
