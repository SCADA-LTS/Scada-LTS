package com.serotonin.mango.rt.maint.work;

import com.serotonin.mango.util.SendEmailConfig;
import com.serotonin.mango.web.email.IMsgSubjectContent;

import javax.mail.internet.InternetAddress;
import java.util.Map;

public class EmailFinallyWorkItem extends EmailAfterWorkItem {

    private final AfterWork.WorkFinally workFinally;

    protected EmailFinallyWorkItem(InternetAddress fromAddress,
                                   InternetAddress[] toAddresses,
                                   IMsgSubjectContent content,
                                   SendEmailConfig sendEmailConfig,
                                   AfterWork afterWork,
                                   AfterWork.WorkFinally workFinally) {
        super(fromAddress, toAddresses, content, sendEmailConfig, afterWork);
        this.workFinally = workFinally;
    }

    public static WorkItem newInstance(InternetAddress fromAddress, InternetAddress[] toAddresses, IMsgSubjectContent content,
                                       SendEmailConfig sendEmailConfig, AfterWork.WorkFinally workFinally) {
        return new EmailFinallyWorkItem(fromAddress, toAddresses, content, sendEmailConfig, new AfterWork() {}, workFinally);
    }

    public static WorkItem newInstance(InternetAddress fromAddress, InternetAddress[] toAddresses, IMsgSubjectContent content,
                                       SendEmailConfig sendEmailConfig, AfterWork afterWork, AfterWork.WorkFinally workFinally) {
        return new EmailFinallyWorkItem(fromAddress, toAddresses, content, sendEmailConfig, afterWork, workFinally);
    }

    @Override
    public void workFinally(Map<String, Exception> exceptions) {
        this.workFinally.workFinally(exceptions);
    }

    @Override
    public void workFinallyFail(Exception finallyException, Map<String, Exception> exceptions) {
        this.workFinally.workFinallyFail(finallyException, exceptions);
    }
}
