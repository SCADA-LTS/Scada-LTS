package com.serotonin.mango.rt.maint.work;

import com.serotonin.mango.util.SendEmailConfig;
import com.serotonin.mango.util.SendEmailData;
import com.serotonin.mango.web.email.EmailTimeoutSender;

public class EmailAfterWorkItem extends AbstractBeforeAfterWorkItem {

    private final SendEmailData sendEmailData;
    private final SendEmailConfig sendEmailConfig;
    private final AfterWork afterWork;

    protected EmailAfterWorkItem(SendEmailData sendEmailData, SendEmailConfig sendEmailConfig, AfterWork afterWork) {
        this.afterWork = afterWork;
        this.sendEmailData = sendEmailData;
        this.sendEmailConfig = sendEmailConfig;
    }

    public static WorkItem newInstance(SendEmailData sendEmailData, SendEmailConfig sendEmailConfig,
                                       AfterWork afterWork) {
        return new EmailAfterWorkItem(sendEmailData, sendEmailConfig, afterWork);
    }

    @Override
    public void work() {
        EmailTimeoutSender emailSender = new EmailTimeoutSender(sendEmailConfig);
        emailSender.send(sendEmailData.getFromAddress(), sendEmailData.getToAddresses(),
                sendEmailData.getContent().getSubject(), sendEmailData.getContent());
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

    @Override
    public String toString() {
        return "EmailAfterWorkItem{" +
                "sendEmailData=" + sendEmailData +
                ", sendEmailConfig=" + sendEmailConfig +
                '}';
    }

    @Override
    public String getDetails() {
        return this.toString();
    }
}
