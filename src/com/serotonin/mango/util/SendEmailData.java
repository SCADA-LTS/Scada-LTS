package com.serotonin.mango.util;

import com.serotonin.mango.web.email.IMsgSubjectContent;

import javax.mail.internet.InternetAddress;

public class SendEmailData {

    private final InternetAddress fromAddress;
    private final InternetAddress[] toAddresses;
    private final IMsgSubjectContent content;

    public SendEmailData(InternetAddress fromAddress, InternetAddress[] toAddresses, IMsgSubjectContent content) {
        this.fromAddress = fromAddress;
        this.toAddresses = toAddresses;
        this.content = content;
    }

    public InternetAddress getFromAddress() {
        return fromAddress;
    }

    public InternetAddress[] getToAddresses() {
        return toAddresses;
    }

    public IMsgSubjectContent getContent() {
        return content;
    }
}
