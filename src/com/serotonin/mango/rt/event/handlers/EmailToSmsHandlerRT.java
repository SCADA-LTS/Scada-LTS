package com.serotonin.mango.rt.event.handlers;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.maint.work.AfterWork;
import com.serotonin.mango.util.MsgContentUtils;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.web.email.IMsgSubjectContent;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;

import java.io.IOException;
import java.util.Set;

import static com.serotonin.mango.util.LoggingUtils.eventHandlerInfo;
import static com.serotonin.mango.util.LoggingUtils.eventInfo;
import static com.serotonin.mango.util.SendUtils.sendMsg;

public class EmailToSmsHandlerRT extends EmailHandlerRT {

    private static final Log LOG = LogFactory.getLog(EmailToSmsHandlerRT.class);

    public enum SmsNotificationType implements NotificationType {
        ACTIVE("activeSms", "ftl.subject.active"),
        MSG_FROM_EVENT("msgFromEventSms", "ftl.subject.active"),
        LIMIT("limitSms", "ftl.subject.active");

        private final String file;
        private final String key;

        SmsNotificationType(String file, String key) {
            this.file = file;
            this.key = key;
        }

        @Override
        public String getFile() {
            return file;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public IMsgSubjectContent createContent(EventInstance evt, String alias) throws TemplateException, IOException {
            return MsgContentUtils.createSms(evt, this, alias);
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
        return formatAddresses(addresses);
    }

    @Override
    protected Set<String> getInactiveRecipients(EventInstance evt) {
        Set<String> addresses = mailingListService.getRecipientAddresses(getVo().getInactiveRecipients(),
                new DateTime(evt.getActiveTimestamp()), CommunicationChannelType.SMS);
        return formatAddresses(addresses);
    }

    @Override
    protected void sendEmail(EventInstance evt, Set<String> addresses) {
        sendMsg(evt, SmsNotificationType.MSG_FROM_EVENT, addresses, vo.getAlias(), new AfterWork() {
            @Override
            public void workFail(Exception exception) {
                LOG.error("Failed sending sms for " + eventHandlerInfo(getVo()) + ", " + eventInfo(evt)
                        + ", error: " + exception.getMessage());
            }
        }, () -> eventHandlerInfo(getVo()) + ", " + eventInfo(evt));
    }

    private Set<String> formatAddresses(Set<String> addresses) {
        String domain = systemSettingsService.getSMSDomain();
        CommunicationChannelType type = CommunicationChannelType.SMS;
        return type.formatAddresses(addresses, domain, type.getReplaceRegex());
    }
}
