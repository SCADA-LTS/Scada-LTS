package org.scada_lts.web.mvc.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.EmailRecipientDeserializer;

import java.util.List;

public class MailingListDTO {

    private Integer id;
    private String xid;
    private String name;
    @JsonDeserialize(using = EmailRecipientDeserializer.class)
    private List<EmailRecipient> entries;

    private String cronPattern;
    private Boolean collectInactiveEmails;

    public MailingListDTO() {
    }

    public MailingListDTO(Integer id, String xid, String name, List<EmailRecipient> entries, String cronPattern, Boolean collectInactiveEmails) {
        this.id = id;
        this.xid = xid;
        this.name = name;
        this.entries = entries;
        this.cronPattern = cronPattern;
        this.collectInactiveEmails = collectInactiveEmails;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<EmailRecipient> getEntries() {
        return entries;
    }

    public void setEntries(List<EmailRecipient> entries) {
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
}
