package org.scada_lts.web.mvc.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.EmailRecipientDeserializer;
import org.scada_lts.web.beans.validation.xss.XssProtect;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateMailingList {

    @XssProtect
    private String xid;
    @XssProtect
    private String name;
    @JsonDeserialize(using = EmailRecipientDeserializer.class)
    private List<EmailRecipientJson> entries;

    @XssProtect
    private String cronPattern;
    private Boolean collectInactiveEmails;

    private Set<Integer> inactiveIntervals;

    public CreateMailingList() {
    }

    public CreateMailingList(String xid, String name, List<EmailRecipientJson> entries, String cronPattern, Boolean collectInactiveEmails, Set<Integer> inactiveIntervals) {
        this.xid = xid;
        this.name = name;
        this.entries = entries;
        this.cronPattern = cronPattern;
        this.collectInactiveEmails = collectInactiveEmails;
        this.inactiveIntervals = inactiveIntervals;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EmailRecipientJson> getEntriesJson() {
        return entries;
    }

    public List<EmailRecipient> getEntries() {
        return entries.stream().map(EmailRecipientJson::to).collect(Collectors.toList());
    }

    public void setEntries(List<EmailRecipientJson> entries) {
        this.entries = entries;
    }

    public String getCronPattern() {
        return cronPattern;
    }

    public void setCronPattern(String cronPattern) {
        this.cronPattern = cronPattern;
    }

    public Boolean getCollectInactiveEmails() {
        return collectInactiveEmails;
    }

    public void setCollectInactiveEmails(Boolean collectInactiveEmails) {
        this.collectInactiveEmails = collectInactiveEmails;
    }

    public Set<Integer> getInactiveIntervals() {
        return inactiveIntervals;
    }

    public void setInactiveIntervals(Set<Integer> inactiveIntervals) {
        this.inactiveIntervals = inactiveIntervals;
    }
}
