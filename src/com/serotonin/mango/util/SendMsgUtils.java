package com.serotonin.mango.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.handlers.NotificationType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.work.EmailWorkItem;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Set;

public final class SendMsgUtils {

    private SendMsgUtils() {}

    private static final Log LOG = LogFactory.getLog(SendMsgUtils.class);

    public static boolean sendEmail(EventInstance evt, NotificationType notificationType, Set<String> addresses,
                                 String alias) {
        try {

            validateEmail(evt, notificationType, addresses, alias);

            if (evt.getEventType().isSystemMessage()) {
                if (((SystemEventType) evt.getEventType()).getSystemEventTypeId() == SystemEventType.TYPE_EMAIL_SEND_FAILURE) {
                    // Don't send email notifications about email send failures.
                    LOG.info("Not sending email for event raised due to email failure");
                    return false;
                }
            }
            String[] toAddrs = addresses.toArray(new String[0]);
            MangoEmailContent content = EmailContentUtils.createContent(evt, notificationType, alias);

            // Send the email.
            EmailWorkItem.queueEmail(toAddrs, content);
            return true;

        } catch (Exception e) {
            LOG.error(MessageFormat.format("Info about email: {0}, StackTrace: {1}",
                    getInfoEmail(evt,notificationType,alias),
                    e.getMessage()));
            return false;
        }
    }

    public static boolean sendSms(EventInstance evt, NotificationType notificationType, Set<String> addresses,
                               String alias) {
        try {

            validateEmail(evt, notificationType, addresses, alias);

            if (evt.getEventType().isSystemMessage()) {
                if (((SystemEventType) evt.getEventType()).getSystemEventTypeId() == SystemEventType.TYPE_EMAIL_SEND_FAILURE) {
                    // Don't send email notifications about email send failures.
                    LOG.info("Not sending email for event raised due to email failure");
                    return false;
                }
            }

            MangoEmailContent content = EmailContentUtils.createTextContent(evt, notificationType, alias);
            String[] toAddrs = addresses.toArray(new String[0]);

            EmailWorkItem.queueEmail(toAddrs, content);
            return true;

        } catch (Exception e) {
            LOG.error(MessageFormat.format("Info about email: {0}, StackTrace: {1}",
                    getInfoEmail(evt,notificationType,alias),
                    e.getMessage()));
            return false;
        }
    }

    private static String getInfoEmail(EventInstance evt, NotificationType notificationType, String alias) {

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

    private static void validateEmail(EventInstance evt, NotificationType notificationType, Set<String> addresses, String alias) throws Exception {

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
}
