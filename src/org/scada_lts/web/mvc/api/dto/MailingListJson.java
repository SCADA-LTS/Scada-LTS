/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.scada_lts.web.mvc.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.EmailRecipientDeserializer;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.scada_lts.web.beans.validation.xss.XssProtect;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MailingListJson extends EmailRecipientJson {
    public static final String XID_PREFIX = "ML_";

    private int id = Common.NEW_ID;

    @XssProtect
    private String xid;
    @XssProtect
    private String name;
    @JsonDeserialize(using = EmailRecipientDeserializer.class)
    private List<EmailRecipientJson> entries;
    @XssProtect
    private String cronPattern;
    private boolean collectInactiveEmails;
    private int dailyLimitSentEmailsNumber;
    private boolean dailyLimitSentEmails;
    private Set<Integer> inactiveIntervals = new TreeSet<Integer>();

    public MailingListJson() {
    }

    public MailingListJson(MailingList mailingList) {
        this.id = mailingList.getId();
        this.xid = mailingList.getXid();
        this.name = mailingList.getName();
        this.entries = mailingList.getEntries().stream().map(EmailRecipient::to).collect(Collectors.toList());
        this.cronPattern = mailingList.getCronPattern();
        this.collectInactiveEmails = mailingList.isCollectInactiveEmails();
        this.dailyLimitSentEmailsNumber = mailingList.getDailyLimitSentEmailsNumber();
        this.dailyLimitSentEmails = mailingList.isDailyLimitSentEmails();
        this.inactiveIntervals = mailingList.getInactiveIntervals();
    }

    @Override
    public int getRecipientType() {
        return EmailRecipient.TYPE_MAILING_LIST;
    }

    @Override
    public String getReferenceAddress() {
        return null;
    }

    @Override
    public int getReferenceId() {
        return id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public List<EmailRecipientJson> getEntries() {
        return entries;
    }

    public void setEntries(List<EmailRecipientJson> entries) {
        this.entries = entries;
    }

    public Set<Integer> getInactiveIntervals() {
        return inactiveIntervals;
    }

    public void setInactiveIntervals(Set<Integer> inactiveIntervals) {
        this.inactiveIntervals = inactiveIntervals;
    }

    @Override
    public String toString() {
        return "MailingList(" + entries + ")";
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        // Don't call the super method, because a mailing list can't be a member of a mailing list.
        map.put("xid", xid);
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) {
        // no op
    }

    public String getCronPattern() {
        return cronPattern;
    }

    public void setCronPattern(String cronPattern) {
        this.cronPattern = cronPattern;
    }

    public boolean isCollectInactiveEmails() {
        return collectInactiveEmails;
    }

    public void setCollectInactiveEmails(boolean collectInactiveEmails) {
        this.collectInactiveEmails = collectInactiveEmails;
    }

    public int getDailyLimitSentEmailsNumber() {
        return dailyLimitSentEmailsNumber;
    }

    public void setDailyLimitSentEmailsNumber(int dailyLimitSentEmailsNumber) {
        this.dailyLimitSentEmailsNumber = dailyLimitSentEmailsNumber;
    }

    public boolean isDailyLimitSentEmails() {
        return dailyLimitSentEmails;
    }

    public void setDailyLimitSentEmails(boolean dailyLimitSentEmails) {
        this.dailyLimitSentEmails = dailyLimitSentEmails;
    }

    @Override
    public EmailRecipient to() {
        MailingList mailingList = new MailingList();
        mailingList.setId(id);
        mailingList.setXid(xid);
        mailingList.setName(name);
        mailingList.setCronPattern(cronPattern);
        mailingList.setEntries(entries.stream().map(EmailRecipientJson::to).collect(Collectors.toList()));
        mailingList.setDailyLimitSentEmails(dailyLimitSentEmails);
        mailingList.setCollectInactiveEmails(collectInactiveEmails);
        mailingList.setDailyLimitSentEmailsNumber(dailyLimitSentEmailsNumber);
        mailingList.setInactiveIntervals(inactiveIntervals);
        return mailingList;
    }
}
