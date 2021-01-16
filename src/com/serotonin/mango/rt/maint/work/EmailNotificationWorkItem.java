package com.serotonin.mango.rt.maint.work;

import com.serotonin.mango.Common;
import com.serotonin.mango.util.SendEmailConfig;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.web.email.EmailSender;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Set;

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

    private static EmailNotificationWorkItem newInstance(String[] toAddrs, MangoEmailContent content,
                                                         AfterWork afterWork,
                                                         SendEmailConfig sendEmailConfig) throws UnsupportedEncodingException, AddressException {
        InternetAddress fromAddress = new InternetAddress(sendEmailConfig.getFromAddr(),
                sendEmailConfig.getPretty());

        InternetAddress[] toAddresses = new InternetAddress[toAddrs.length];
        for (int i = 0; i < toAddrs.length; i++)
            toAddresses[i] = new InternetAddress(toAddrs[i]);

        return new EmailNotificationWorkItem(afterWork, fromAddress, toAddresses,
                content, sendEmailConfig);
    }

    public static void queueMsg(String[] toAddrs, MangoEmailContent content,
                                AfterWork afterWork,
                                SendEmailConfig sendEmailConfig) throws UnsupportedEncodingException, AddressException {
        EmailNotificationWorkItem item = EmailNotificationWorkItem.newInstance(toAddrs, content, afterWork, sendEmailConfig);
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
    public void workError(Throwable throwable) {
        afterWork.workError(throwable);
    }

    @Override
    public void workSuccess() {
        afterWork.workSuccess();
    }

    @Override
    public void workFinally(Set<Throwable> throwables) {
        afterWork.workFinally(throwables);
    }

    @Override
    public int getPriority() {
        return WorkItem.PRIORITY_LOW;
    }
}
