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
package com.serotonin.mango.vo;

import com.serotonin.web.taglib.DateFunctions;

import java.util.Objects;

public class UserComment {
    public static final int TYPE_EVENT = 1;
    public static final int TYPE_POINT = 2;

    // Configuration fields
    private int userId;
    private long ts;
    private String comment;

    // Relational fields
    private String username;

    private int typeKey;

    public UserComment() {
    }

    public UserComment(int userId, long ts, String comment, String username, int typeKey) {
        this.userId = userId;
        this.ts = ts;
        this.comment = comment;
        this.username = username;
        this.typeKey = typeKey;
    }

    public String getPrettyTime() {
        return DateFunctions.getTime(ts);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static boolean validUserCommentType(Integer typeId) {
        return typeId == UserComment.TYPE_EVENT || typeId == UserComment.TYPE_POINT;
    }

    public int getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(int typeKey) {
        this.typeKey = typeKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserComment)) return false;
        UserComment that = (UserComment) o;
        return getUserId() == that.getUserId() && getTs() == that.getTs() && getTypeKey() == that.getTypeKey() && Objects.equals(getComment(), that.getComment()) && Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getTs(), getComment(), getUsername(), getTypeKey());
    }
}
