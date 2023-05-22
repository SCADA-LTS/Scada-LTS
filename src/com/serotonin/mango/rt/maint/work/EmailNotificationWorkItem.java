package com.serotonin.mango.rt.maint.work;

import com.serotonin.mango.Common;
import com.serotonin.mango.util.SendEmailConfig;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.web.email.EmailSender;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

@Deprecated
public class EmailNotificationWorkItem extends AbstractBeforeAfterWorkItem {

    private final AfterWork afterWork;
    private final InternetAddress fromAddress;
    private final InternetAddress[] toAddresses;
    private final MangoEmailContent content;
    private final SendEmailConfig sendEmailConfig;

    private EmailNotificationWorkItem(AfterWork afterWork, InternetAddress fromAddress,
                                      InternetAddress[] toAddresses, MangoEmailContent content,
                                      SendEmailConfig sendEmailConfig) {
        this.afterWork = afterWork;
        this.fromAddress = fromAddress;
        this.toAddresses = toAddresses;
        this.content = content;
        this.sendEmailConfig = sendEmailConfig;
    }

    public static EmailNotificationWorkItem newInstance(InternetAddress[] internetAddresses, MangoEmailContent content,
                                                         AfterWork afterWork,
                                                         SendEmailConfig sendEmailConfig) throws UnsupportedEncodingException {
        InternetAddress fromAddress = new InternetAddress(sendEmailConfig.getFromAddr(),
                sendEmailConfig.getPretty());
        return new EmailNotificationWorkItem(afterWork, fromAddress, internetAddresses,
                content, sendEmailConfig);
    }

    public static void queueMsg(InternetAddress[] internetAddresses, MangoEmailContent content,
                                AfterWork afterWork,
                                SendEmailConfig sendEmailConfig) throws UnsupportedEncodingException {
        EmailNotificationWorkItem item = EmailNotificationWorkItem.newInstance(internetAddresses, content, afterWork, sendEmailConfig);
        Common.ctx.getBackgroundProcessing().addWorkItem(item);
    }

    @Override
    public void work() {
        EmailSender emailSender = new EmailSender(sendEmailConfig.getHost(), sendEmailConfig.getPort(),
                sendEmailConfig.isAuthorization(), sendEmailConfig.getUsername(), sendEmailConfig.getPassword(),
                sendEmailConfig.isTls());
        emailSender.send(fromAddress, toAddresses, content.getSubject(), content);
    }

    @Override
    public void workSuccess() {
        afterWork.workSuccess();
    }

    @Override
    public void workFail(Exception exception) {
        afterWork.workFail(exception);
    }

    @Override
    public int getPriority() {
        return WorkItem.PRIORITY_LOW;
    }
}
