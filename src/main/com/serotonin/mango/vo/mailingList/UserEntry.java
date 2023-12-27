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
package com.serotonin.mango.vo.mailingList;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.User;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.service.CommunicationChannelTypable;
import org.scada_lts.service.CommunicationChannelType;

@JsonRemoteEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntry extends EmailRecipient {
    private int userId;
    private User user;

    private static final Log LOG = LogFactory.getLog(UserEntry.class);

    public UserEntry() {
    }

    public UserEntry(int userId, User user) {
        this.userId = userId;
        this.user = user;
    }

    @Override
    public int getRecipientType() {
        return EmailRecipient.TYPE_USER;
    }

    @Override
    public int getReferenceId() {
        return userId;
    }

    @Override
    public String getReferenceAddress() {
        return null;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void appendAddresses(Set<String> addresses, DateTime sendTime) {
        appendAllAddresses(addresses);
    }

    @Override
    public void appendAddresses(Set<String> addresses, DateTime sendTime, CommunicationChannelTypable type) {
        appendAllAddresses(addresses, type);
    }

    @Override
    public void appendAllAddresses(Set<String> addresses) {
        if (user == null)
            return;
        if (!user.isDisabled())
            addresses.add(user.getEmail());
    }

    @Override
    public void appendAllAddresses(Set<String> addresses, CommunicationChannelTypable type) {
        if (user == null)
            return;
        if (!user.isDisabled()) {
            if(type == CommunicationChannelType.EMAIL && !StringUtils.isEmpty(user.getEmail()))
                addresses.add(user.getEmail());
            if(type == CommunicationChannelType.SMS && !StringUtils.isEmpty(user.getPhone()))
                addresses.add(user.getPhone());
        }
    }

    @Override
    public String toString() {
        if (user == null)
            return "userId=" + userId;
        return user.getUsername();
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        if (user == null)
            user = new UserDao().getUser(userId);
        setUsername(map);
    }

    private void setUsername(Map<String, Object> map) {
        try {
            User user = new UserDao().getUser(userId);
            map.put("username", user.getUsername());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);

        String username = json.getString("username");
        if (username == null)
            throw new LocalizableJsonException("emport.error.recipient.missing.reference", "username");

        user = new UserDao().getUser(username);
        if (user == null)
            throw new LocalizableJsonException("emport.error.recipient.invalid.reference", "username", username);

        userId = user.getId();
    }
}
