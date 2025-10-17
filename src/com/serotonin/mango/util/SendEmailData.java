package com.serotonin.mango.util;

import com.serotonin.mango.web.email.IMsgSubjectContent;

import javax.mail.internet.InternetAddress;
import java.util.Arrays;

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

    @Override
    public String toString() {
        return "SendEmailData{" +
                "fromAddress=" + fromAddress +
                ", toAddresses=" + Arrays.toString(toAddresses) +
                ", subject=" + (content == null ? "null" : content.getSubject()) +
                '}';
    }
}
