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
package com.serotonin.mango.view;

import java.util.Map;
import java.util.Objects;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.User;
import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class ShareUser implements JsonSerializable {
    public static final int ACCESS_NONE = 0;
    public static final int ACCESS_READ = 1;
    public static final int ACCESS_SET = 2;
    public static final int ACCESS_OWNER = 3;

    public static final ExportCodes ACCESS_CODES = new ExportCodes();
    static {
        ACCESS_CODES.addElement(ACCESS_NONE, "NONE", "common.access.none");
        ACCESS_CODES.addElement(ACCESS_READ, "READ", "common.access.read");
        ACCESS_CODES.addElement(ACCESS_SET, "SET", "common.access.set");
    }

    private int userId;
    private int accessType;

    public ShareUser() {
    }

    public ShareUser(int userId, int accessType) {
        this.userId = userId;
        this.accessType = accessType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAccessType() {
        return accessType;
    }

    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String text = json.getString("user");
        if (StringUtils.isEmpty(text))
            throw new LocalizableJsonException("emport.error.viewShare.missing", "user");
        User user = new UserDao().getUser(text);
        if (user == null)
            throw new LocalizableJsonException("emport.error.missingUser", text);
        userId = user.getId();

        text = json.getString("accessType");
        if (StringUtils.isEmpty(text))
            throw new LocalizableJsonException("emport.error.missing", "accessType", ACCESS_CODES
                    .getCodeList(ACCESS_OWNER));
        accessType = ACCESS_CODES.getId(text, ACCESS_OWNER);
        if (accessType == -1)
            throw new LocalizableJsonException("emport.error.invalid", "permission", text, ACCESS_CODES
                    .getCodeList(ACCESS_OWNER));
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        User user = new UserDao().getUser(userId);
        if(user != null) {
            map.put("user", user.getUsername());
        } else {
            map.put("user", "");
            map.put("userId", userId);
        }
        map.put("accessType", ACCESS_CODES.getCode(accessType));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShareUser)) return false;
        ShareUser shareUser = (ShareUser) o;
        return getUserId() == shareUser.getUserId() &&
                getAccessType() == shareUser.getAccessType();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getUserId(), getAccessType());
    }

    @Override
    public String toString() {
        return "ShareUser{" +
                "userId=" + userId +
                ", accessType=" + accessType +
                '}';
    }
}
