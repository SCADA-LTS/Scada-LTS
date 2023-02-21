package com.serotonin.mango.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.handlers.NotificationType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.work.*;
import com.serotonin.mango.web.email.IMsgContent;
import com.serotonin.mango.web.email.IMsgSubjectContent;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiConsumer;

public final class SendUtils {

    private static final Log LOG = LogFactory.getLog(SendUtils.class);

    private SendUtils() {}

    public static void sendMsg(EventInstance evt, NotificationType notificationType, Set<String> addresses,
                               String alias, AfterWork afterWork) {
        sendMsg(evt, notificationType, addresses, alias, afterWork, () -> "sendMsg from: " + SendUtils.class.getName());
    }

    public static void sendMsg(EventInstance evt, NotificationType notificationType, Set<String> addresses,
                               String alias, AfterWork afterWork, WorkItemDetails workItemDetails) {
        sendMsg(evt, notificationType, addresses, alias, afterWork, new BeforeWork.NotExecuted() {
            @Override
            public void workNotExecuted(String msg) {
                LOG.warn(msg);
            }

            @Override
            public void workNotExecuted(Exception exception) {
                try {
                    afterWork.workFail(exception);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }, workItemDetails);
    }

    public static void sendMsg(String[] toAddresses, String subject, IMsgContent msgContent, AfterWork afterWork,
                               AfterWork.WorkFinally workFinally, WorkItemDetails workItemDetails) {
        sendMsg(toAddresses, subject, msgContent, afterWork, workFinally, new BeforeWork.NotExecuted() {
            @Override
            public void workNotExecuted(Exception exception) {
                Map<String, Exception> exceptions = new HashMap<>();
                exceptions.put("sendMsgException", exception);
                try {
                    afterWork.workFail(exception);
                } catch (Exception ex) {
                    exceptions.put("workFailException", ex);
                    LOG.error(ex.getMessage(), ex);
                } finally {
                    try {
                        workFinally.workFinally(exceptions);
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }, workItemDetails);
    }

    public static void sendMsgTestSync(Set<String> addresses, AfterWork afterWork, WorkItemDetails workItemDetails) {
        sendMsgTestSync(addresses, afterWork, new BeforeWork.NotExecuted() {
            @Override
            public void workNotExecuted(Exception exception) {
                try {
                    afterWork.workFail(exception);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }, workItemDetails);
    }

    public static void sendMsgTestSync(String toAddresses, IMsgSubjectContent content, Map<String, Object> result,
                                       WorkItemDetails workItemDetails) {
        sendMsgTestSync(new String[]{toAddresses}, content, result, workItemDetails);
    }

    public static void sendMsgTestSync(String[] toAddresses, IMsgSubjectContent content, Map<String, Object> result,
                                       WorkItemDetails workItemDetails) {
        sendMsgTestSync(toAddresses, content, new AfterWork() {
            @Override
            public void workFail(Exception e) {
                LOG.error(MessageFormat.format("Info about email: {0}, error: {1}",
                        "test", e.getMessage()));
                result.put("exception", "Send fail: " + getShortMsg(e));
            }

            @Override
            public void workSuccess() {
                result.put("message", new LocalizableMessage("common.testEmailSent",  Arrays.toString(toAddresses)));
            }
        }, (res, e) -> res.put("exception", "Send fail: " + getShortMsg(e)), result, workItemDetails);
    }

    public static void sendMsgTestSync(String[] toAddresses, IMsgSubjectContent content, DwrResponseI18n response,
                                       WorkItemDetails workItemDetails) {
        sendMsgTestSync(toAddresses, content, new AfterWork() {
            @Override
            public void workFail(Exception e) {
                LOG.error(MessageFormat.format("Info about email: {0}, error: {1}",
                        "test", e.getMessage()));
                response.addGenericMessage("common.default", "Send fail: " + getShortMsg(e));
            }
        }, (res, ex) -> res.addGenericMessage("common.default", "Send fail: " + getShortMsg(ex)), response, workItemDetails);
    }

    private static void sendMsgTestSync(Set<String> addresses, AfterWork afterWork, BeforeWork.NotExecuted notExecuted,
                                        WorkItemDetails workItemDetails) {
        try {
            IMsgSubjectContent content = MsgContentUtils.createEmailTest();
            sendMsgTestSync(addresses.toArray(new String[0]), content, afterWork, (a,b) -> {}, new HashMap<>(), workItemDetails);
        } catch (Exception e) {
            try {
                notExecuted.workNotExecuted(e);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private static void sendMsg(EventInstance evt, NotificationType notificationType, Set<String> addresses,
                                String alias, AfterWork afterWork, BeforeWork.NotExecuted notExecuted,
                                WorkItemDetails workItemDetails) {
        try {

            validateEmail(evt, notificationType, addresses, alias);

            if (evt.getEventType().isSystemMessage()
                    && ((SystemEventType) evt.getEventType()).getSystemEventTypeId() == SystemEventType.TYPE_EMAIL_SEND_FAILURE) {
                // Don't send email notifications about email send failures.
                notExecuted.workNotExecuted("Not sending email for event raised due to email failure");
                return;
            }

            SendEmailConfig.validateSystemSettings();
            SendEmailConfig sendEmailConfig = SendEmailConfig.newConfigFromSystemSettings();

            SendEmailData sendEmailData = toSendEmailData(evt, notificationType, addresses, alias, sendEmailConfig);
            WorkItem workItem = EmailAfterWorkItem.newInstance(sendEmailData, sendEmailConfig, afterWork, workItemDetails);

            // Send the email.
            queueMsg(workItem);

        } catch (Exception e) {
            try {
                notExecuted.workNotExecuted(e);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private static void sendMsg(String[] toAddresses, String subject,
                                IMsgContent msgContent, AfterWork afterWork,
                                AfterWork.WorkFinally workFinally, BeforeWork.NotExecuted notExecuted,
                                WorkItemDetails workItemDetails) {
        try {

            SendEmailConfig.validateSystemSettings();
            SendEmailConfig sendEmailConfig = SendEmailConfig.newConfigFromSystemSettings();

            SendEmailData sendEmailData = toSendEmailData(toAddresses, subject, msgContent, sendEmailConfig);
            WorkItem workItem = EmailFinallyWorkItem.newInstance(sendEmailData, sendEmailConfig, afterWork, workFinally, workItemDetails);

            // Send the email.
            queueMsg(workItem);

        } catch (Exception e) {
            try {
                notExecuted.workNotExecuted(e);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private static <T> void sendMsgTestSync(String[] toAddresses, IMsgSubjectContent content, AfterWork afterWork,
                                            BiConsumer<T, Exception> handleException, T object,
                                            WorkItemDetails workItemDetails) {
        sendMsgTestSync(toAddresses, content, afterWork, handleException, object, new BeforeWork.NotExecuted() {
            @Override
            public void workNotExecuted(Exception exception) {
                try {
                    afterWork.workFail(exception);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }, workItemDetails);
    }

    private static <T> void sendMsgTestSync(String[] toAddresses, IMsgSubjectContent content, AfterWork afterWork,
                                            BiConsumer<T, Exception> handleException, T object,
                                            BeforeWork.NotExecuted notExecuted, WorkItemDetails workItemDetails) {
        try {

            SendEmailConfig.validateSystemSettings();
            SendEmailConfig sendEmailConfig = SendEmailConfig.newConfigFromSystemSettings();

            SendEmailData sendEmailData = toSendEmailData(toAddresses, content, sendEmailConfig);
            WorkItem workItem = EmailAfterWorkItem.newInstance(sendEmailData, sendEmailConfig, afterWork, workItemDetails);

            // Send the email.
            workItem.execute();
        } catch (Exception e) {
            try {
                notExecuted.workNotExecuted(e);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
            try {
                handleException.accept(object, e);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private static String getShortMsg(Exception ex) {
        if(ex.getMessage() == null) {
            return ex.getClass().getSimpleName();
        }
        int size = 30;
        return (ex.getMessage().length() > size ? ex.getMessage().substring(0, size) : ex.getMessage()) + "...";
    }

    private static InternetAddress getFromAddress(SendEmailConfig sendEmailConfig) throws UnsupportedEncodingException {
        return new InternetAddress(sendEmailConfig.getFromAddr(), sendEmailConfig.getPretty());
    }

    private static String getInfoEmail(EventInstance evt, NotificationType notificationType, String alias) {

        String messageInfoAlias = MessageFormat.format("Alias: {0} \n", alias);
        String messageInfoEmail = MessageFormat.format("Event: {0} \n", evt.getId());
        String messageInfoNotification = MessageFormat.format("Notification: {0} \n", notificationType.getKey());
        String subject = "";
        String messageExceptionWhenGetSubjectEmail = "";
        try {
            LocalizableMessage subjectMsg;
            LocalizableMessage notifTypeMsg = new LocalizableMessage(notificationType.getKey());
            if (StringUtils.isEmpty(alias)) {
                if (evt.getId() == Common.NEW_ID)
                    subjectMsg = new LocalizableMessage("ftl.subject.default.log", notifTypeMsg);
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
        return messageInfoEmail +
                messageInfoNotification +
                messageInfoAlias +
                subject +
                messageExceptionWhenGetSubjectEmail;
    }

    private static void validateEmail(EventInstance evt, NotificationType notificationType,
                                      Set<String> addresses, String alias) throws IllegalArgumentException {

        String messageErrorEventInstance = "Event Instance null \n";
        String messageErrorNotyficationType = "Notification type is null \n";
        String messageErrorEmails = " Don't have e-mail \n";
        String messageErrorAlias = " Don't have alias\n";
        String messages = "";
        if (evt == null || evt.getEventType() == null) messages += messageErrorEventInstance;
        if (notificationType == null) messages += messageErrorNotyficationType;
        if (addresses == null || addresses.size() == 0) messages += messageErrorEmails;
        if (alias == null) messages += messageErrorAlias;

        if (messages.length() > 0) {
            throw new IllegalArgumentException(getInfoEmail(evt, notificationType, alias) + messages );
        }

    }

    private static void validateAddresses(EventInstance evt, NotificationType notificationType,
                                          InternetAddress[] internetAddresses, String alias) throws IllegalArgumentException {

        String messageErrorEmails = " Don't have e-mail \n";
        String messages = "";
        if (internetAddresses == null || internetAddresses.length == 0) messages += messageErrorEmails;

        if (!messages.isEmpty()) {
            throw new IllegalArgumentException(getInfoEmail(evt, notificationType, alias) + messages );
        }
    }

    private static InternetAddress[] convertToInternetAddresses(String[] toAddresses) {
        Set<InternetAddress> addresses = new HashSet<>();
        for (int i = 0; i < toAddresses.length; i++) {
            try {
                InternetAddress internetAddress = new InternetAddress(toAddresses[i]);
                addresses.add(internetAddress);
            } catch (AddressException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return addresses.toArray(new InternetAddress[]{});
    }

    private static void validateAddresses(InternetAddress[] internetAddresses) throws IllegalArgumentException {

        String messageErrorEmails = " Don't have e-mail \n";
        String messages = "";
        if (internetAddresses == null || internetAddresses.length == 0) messages += messageErrorEmails;

        if (!messages.isEmpty()) {
            throw new IllegalArgumentException(messages);
        }
    }

    private static SendEmailData toSendEmailData(EventInstance evt, NotificationType notificationType,
                                                 Set<String> addresses, String alias,
                                                 SendEmailConfig sendEmailConfig) throws TemplateException, IOException {
        String[] toAddresses = addresses.toArray(new String[0]);
        IMsgSubjectContent content = notificationType.createContent(evt, alias);
        return toSendEmailData(toAddresses, content, sendEmailConfig);
    }

    private static SendEmailData toSendEmailData(String[] toAddresses, String subject, IMsgContent msgContent,
                                                 SendEmailConfig sendEmailConfig) throws TemplateException, IOException {
        IMsgSubjectContent content = MsgContentUtils.createContent(msgContent, subject);
        return toSendEmailData(toAddresses, content, sendEmailConfig);
    }

    private static SendEmailData toSendEmailData(String[] toAddresses, IMsgSubjectContent content, SendEmailConfig sendEmailConfig) throws UnsupportedEncodingException {
        InternetAddress[] toInternetAddresses = SendUtils.convertToInternetAddresses(toAddresses);
        validateAddresses(toInternetAddresses);
        InternetAddress fromAddress = getFromAddress(sendEmailConfig);
        return new SendEmailData(fromAddress, toInternetAddresses, content);
    }

    private static void queueMsg(WorkItem workItem) {
        Common.ctx.getBackgroundProcessing().addWorkItem(workItem);
    }
}
