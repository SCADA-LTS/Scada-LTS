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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.UserEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.UserIdentifier;
import org.scada_lts.mango.service.UserService;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntryJson extends EmailRecipientJson {
    private int userId;
    private UserIdentifier user;

    private static final Log LOG = LogFactory.getLog(UserEntryJson.class);

    public UserEntryJson() {
    }

    public UserEntryJson(int userId, UserIdentifier user) {
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

    public UserIdentifier getUser() {
        return user;
    }

    public void setUser(UserIdentifier user) {
        this.user = user;
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
        if (user == null) {
            User user1 = new UserService().getUser(userId);
            user = new UserIdentifier(user1);
        }
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

        User user1 = new UserService().getUser(username);
        if (user1 == null)
            throw new LocalizableJsonException("emport.error.recipient.invalid.reference", "username", username);

        user = new UserIdentifier(user1);
        userId = user.getId();
    }

    @Override
    public UserEntry to() {
        User user1 = new UserService().getUser(userId);
        return new UserEntry(userId, user1);
    }
}
