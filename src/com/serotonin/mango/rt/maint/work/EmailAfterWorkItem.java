package com.serotonin.mango.rt.maint.work;

import com.serotonin.mango.util.SendEmailConfig;
import com.serotonin.mango.web.email.EmailTimeoutSender;
import com.serotonin.mango.web.email.IMsgSubjectContent;

import javax.mail.internet.InternetAddress;

public class EmailAfterWorkItem extends AbstractBeforeAfterWorkItem {

    private final InternetAddress fromAddress;
    private final InternetAddress[] toAddresses;
    private final IMsgSubjectContent content;
    private final SendEmailConfig sendEmailConfig;
    private final AfterWork afterWork;

    protected EmailAfterWorkItem(InternetAddress fromAddress, InternetAddress[] toAddresses, IMsgSubjectContent content,
                                 SendEmailConfig sendEmailConfig, AfterWork afterWork) {
        this.afterWork = afterWork;
        this.fromAddress = fromAddress;
        this.toAddresses = toAddresses;
        this.content = content;
        this.sendEmailConfig = sendEmailConfig;
    }

    public static WorkItem newInstance(InternetAddress fromAddress, InternetAddress[] toAddresses, IMsgSubjectContent content,
                                       SendEmailConfig sendEmailConfig, AfterWork afterWork) {
        return new EmailAfterWorkItem(fromAddress, toAddresses, content, sendEmailConfig, afterWork);
    }

    @Override
    public void work() {
        EmailTimeoutSender emailSender = new EmailTimeoutSender(sendEmailConfig);
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
        return WorkItem.PRIORITY_MEDIUM;
    }
}
