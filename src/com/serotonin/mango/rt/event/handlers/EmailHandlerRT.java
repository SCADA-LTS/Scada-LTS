/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.event.handlers;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Collections;

import com.serotonin.mango.rt.event.type.ScheduledInactiveEventType;
import com.serotonin.mango.util.EmailContentUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.MailingListDao;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.work.EmailWorkItem;
import com.serotonin.mango.util.timeout.ModelTimeoutClient;
import com.serotonin.mango.util.timeout.ModelTimeoutTask;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.timer.TimerTask;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;

public class EmailHandlerRT extends EventHandlerRT implements ModelTimeoutClient<EventInstance> {
    private static final Log LOG = LogFactory.getLog(EmailHandlerRT.class);

    private TimerTask escalationTask;

    private Set<String> activeRecipients;
    private ScheduledExecuteInactiveEventService service;
    private MailingListService mailingListService;

    public enum NotificationType {
        ACTIVE("active", "ftl.subject.active"), //
        ESCALATION("escalation", "ftl.subject.escalation"), //
        INACTIVE("inactive", "ftl.subject.inactive"),
        ACTIVE_SMS("activeSms", "ftl.subject.active");

        String file;
        String key;

        private NotificationType(String file, String key) {
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

    /**
     * The list of all of the recipients - active and escalation - for sending upon inactive if configured to do so.
     */
    private Set<String> inactiveRecipients;

    public EmailHandlerRT(EventHandlerVO vo) {
        this.vo = vo;
        this.service = ScheduledExecuteInactiveEventService.getInstance();
        this.mailingListService = new MailingListService();
    }

    public EmailHandlerRT(EventHandlerVO vo, MailingListService mailingListService) {
        this.vo = vo;
        this.service = ScheduledExecuteInactiveEventService.getInstance();
        this.mailingListService = mailingListService;
    }

    public EmailHandlerRT(EventHandlerVO vo,
                          ScheduledExecuteInactiveEventService service,
                          MailingListService mailingListService) {
        this.vo = vo;
        this.service = service;
        this.mailingListService = mailingListService;
    }

    public Set<String> getActiveRecipients() {
        return activeRecipients;
    }

    @Override
    public void eventRaised(EventInstance evt) {
        if(service.isScheduledInactiveEventType(evt)) {
            eventScheduledRaised(evt, (ScheduledInactiveEventType)evt.getEventType());
        } else {
            raised(evt);
        }
    }

    protected Set<String> getActiveRecipients(EventInstance evt) {
        return mailingListService.getRecipientAddresses(vo.getActiveRecipients(),
                new DateTime(evt.getActiveTimestamp()), CommunicationChannelType.EMAIL);
    }

    protected Set<String> getInactiveRecipients(EventInstance evt) {
        return mailingListService.getRecipientAddresses(vo.getInactiveRecipients(),
                new DateTime(evt.getActiveTimestamp()), CommunicationChannelType.EMAIL);
    }

    protected Set<String> getActiveRecipients(EventInstance evt, CommunicationChannel channel) {
        if(channel.getType() == CommunicationChannelType.EMAIL) {
            return mailingListService.getRecipientAddresses(vo.getActiveRecipients(), channel);
        }
        return Collections.emptySet();
    }

    protected EventHandlerVO getVo() {
        return this.vo;
    }

    private void raised(EventInstance evt) {

        // Get the email addresses to send to
        activeRecipients = getActiveRecipients(evt);

        service.scheduleEvent(vo, evt);

        // Send an email to the active recipients.
        sendEmail(evt, activeRecipients);

        // If an inactive notification is to be sent, save the active recipients.
        if (vo.isSendInactive()) {
            if (vo.isInactiveOverride())
                inactiveRecipients = getInactiveRecipients(evt);
            else
                inactiveRecipients = activeRecipients;
        }

        // If an escalation is to be sent, set up timeout to trigger it.
        if (vo.isSendEscalation()) {
            long delayMS = Common.getMillis(vo.getEscalationDelayType(), vo.getEscalationDelay());
            escalationTask = new ModelTimeoutTask<EventInstance>(delayMS, this, evt);
        }
    }

    private void eventScheduledRaised(EventInstance evt, ScheduledInactiveEventType eventType) {
        activeRecipients = getActiveRecipients(evt, eventType.getCommunicationChannel());

        // Send an email to the active recipients.
        sendEmail(evt, activeRecipients);
    }

    //
    // TimeoutClient
    //
    synchronized public void scheduleTimeout(EventInstance evt, long fireTime) {
        // Get the email addresses to send to
        Set<String> addresses = new MailingListDao().getRecipientAddresses(vo.getEscalationRecipients(), new DateTime(
                fireTime));

        // Send the escalation.
        sendEmail(evt, NotificationType.ESCALATION, addresses);

        // If an inactive notification is to be sent, save the escalation recipients, but only if inactive recipients
        // have not been overridden.
        if (vo.isSendInactive() && !vo.isInactiveOverride())
            inactiveRecipients.addAll(addresses);
    }

    @Override
    synchronized public void eventInactive(EventInstance evt) {
        // Cancel the escalation job in case it's there
        if (escalationTask != null)
            escalationTask.cancel();

        if (inactiveRecipients != null && inactiveRecipients.size() > 0)
            // Send an email to the inactive recipients.
            sendEmail(evt, NotificationType.INACTIVE, inactiveRecipients);
    }

    public static void sendActiveEmail(EventInstance evt, Set<String> addresses) {
        sendEmail(evt, NotificationType.ACTIVE, addresses, null);
    }

    private void sendEmail(EventInstance evt, NotificationType notificationType, Set<String> addresses) {
        sendEmail(evt, notificationType, addresses, vo.getAlias());
    }

    protected void sendEmail(EventInstance evt, Set<String> addresses) {
        sendEmail(evt, NotificationType.ACTIVE, addresses, vo.getAlias());
    }

    protected static String getInfoEmail(EventInstance evt, NotificationType notificationType, String alias) {

        String messageInfoAlias = MessageFormat.format("Alias: {0} \n", alias);
        String messageInfoEmail = MessageFormat.format("Event: {0} \n", evt.getId());
        String messageInfoNotyfication = MessageFormat.format("Notyfication: {0} \n", notificationType.getKey());
        String subject = "";
        String messageExceptionWhenGetSubjectEmail = "";
        try {
            LocalizableMessage subjectMsg;
            LocalizableMessage notifTypeMsg = new LocalizableMessage(notificationType.getKey());
            if (StringUtils.isEmpty(alias)) {
                if (evt.getId() == Common.NEW_ID)
                    subjectMsg = new LocalizableMessage("ftl.subject.default", notifTypeMsg);
                else
                    subjectMsg = new LocalizableMessage("ftl.subject.default.id", notifTypeMsg, evt.getId());
            } else {
                if (evt.getId() == Common.NEW_ID)
                    subjectMsg = new LocalizableMessage("ftl.subject.alias", alias, notifTypeMsg);
                else
                    subjectMsg = new LocalizableMessage("ftl.subject.alias.id", alias, notifTypeMsg, evt.getId());
            }

            ResourceBundle bundle = Common.getBundle();
            subject = subjectMsg.getLocalizedMessage(bundle);
        } catch (Exception e) {
            messageExceptionWhenGetSubjectEmail =  MessageFormat.format("StackTrace for subjectMsg {0}",ExceptionUtils.getStackTrace(e));
        }

        String messages = new StringBuilder()
                .append(messageInfoEmail)
                .append(messageInfoNotyfication)
                .append(messageInfoAlias)
                .append(subject)
                .append(messageExceptionWhenGetSubjectEmail).toString();

        return messages;
    }

    protected static void validateEmail(EventInstance evt, NotificationType notificationType, Set<String> addresses, String alias) throws Exception {

        String messageErrorEventInstance = "Event Instance null \n";
        String messageErrorNotyficationType = "Notification type is null \n";
        String messageErrorEmails = "Don't have e-mail \n";
        String messageErrorAlias = "Don't have alias\n";
        String messages = "";
        if (evt == null || evt.getEventType() == null) messages += messageErrorEventInstance;
        if (notificationType == null) messages += messageErrorNotyficationType;
        if (addresses == null || addresses.size() == 0) messages += messageErrorEmails;
        if (alias == null) messages += messageErrorAlias;

        if (messages.length() > 0) {
            throw new Exception(getInfoEmail(evt, notificationType, alias) + messages );
        }

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
            String[] toAddrs = addresses.toArray(new String[0]);
            MangoEmailContent content = EmailContentUtils.createContent(evt, notificationType, alias);

            // Send the email.
            EmailWorkItem.queueEmail(toAddrs, content);

        } catch (Exception e) {
            LOG.error(MessageFormat.format("Info about email: {0}, StackTrace: {1}",
                            getInfoEmail(evt,notificationType,alias),
                            ExceptionUtils.getStackTrace(e)));
        }
    }
}
