package com.serotonin.mango.rt.event.handlers;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.work.EmailWorkItem;
import com.serotonin.mango.util.EmailContentUtils;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.web.email.MangoEmailContent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailToSmsHandlerRT extends EmailHandlerRT {

    private static final Log LOG = LogFactory.getLog(EmailToSmsHandlerRT.class);

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
    protected Set<String> getActiveRecipients(EventInstance evt, CommunicationChannel channel) {
        if(channel.getType() == CommunicationChannelType.SMS) {
            Set<String> addresses = mailingListService.getRecipientAddresses(getVo().getActiveRecipients(), channel);
            return addedAtDomain(addresses);
        }
        return Collections.emptySet();
    }

    @Override
    protected void sendEmail(EventInstance evt, Set<String> addresses) {
        sendEmail(evt, NotificationType.ACTIVE_SMS, addresses, getVo().getAlias());
    }

    private static void sendEmail(EventInstance evt, NotificationType notificationType, Set<String> addresses,
                                  String alias) {
        try {

            validateEmail(evt, notificationType, addresses, alias);

            if (evt.getEventType().isSystemMessage()) {
                if (((SystemEventType) evt.getEventType()).getSystemEventTypeId() == SystemEventType.TYPE_EMAIL_SEND_FAILURE) {
                    // Don't send email notifications about email send failures.
                    LOG.info("Not sending email for event raised due to email failure");
                    return;
                }
            }
            MangoEmailContent content = EmailContentUtils.createSmsContent(evt, notificationType, alias);
            String[] toAddrs = addresses.toArray(new String[0]);

            EmailWorkItem.queueEmail(toAddrs, content);

        } catch (Exception e) {
            LOG.error(MessageFormat.format("Info about email: {0}, StackTrace: {1}",
                            getInfoEmail(evt,notificationType,alias),
                            ExceptionUtils.getStackTrace(e)));
        }
    }

    private Set<String> addedAtDomain(Set<String> addresses) {
        String domain = systemSettingsService.getSMSDomain();
        return addresses.stream()
                .map(a -> a + "@" + domain)
                .collect(Collectors.toSet());
    }
}
