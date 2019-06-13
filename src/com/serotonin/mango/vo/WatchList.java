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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.serotonin.json.JsonArray;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.json.JsonValue;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class WatchList implements JsonSerializable {
    public static final String XID_PREFIX = "WL_";

    private int id = Common.NEW_ID;
    private String xid;
    private int userId;
    @JsonRemoteProperty
    private String name;
    private final List<DataPointVO> pointList = new CopyOnWriteArrayList<DataPointVO>();
    private List<ShareUser> watchListUsers = new ArrayList<ShareUser>();

    public int getUserAccess(User user) {
        if (user.getId() == userId)
            return ShareUser.ACCESS_OWNER;

        for (ShareUser wlu : watchListUsers) {
            if (wlu.getUserId() == user.getId())
                return wlu.getAccessType();
        }
        return ShareUser.ACCESS_NONE;
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
        if (name == null)
            this.name = "";
        else
            this.name = name;
    }

    public List<DataPointVO> getPointList() {
        return pointList;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<ShareUser> getWatchListUsers() {
        return watchListUsers;
    }

    public void setWatchListUsers(List<ShareUser> watchListUsers) {
        this.watchListUsers = watchListUsers;
    }

    public void validate(DwrResponseI18n response) {
        if (StringUtils.isEmpty(name))
            response.addMessage("name", new LocalizableMessage("validate.required"));
        else if (StringUtils.isLengthGreaterThan(name, 50))
            response.addMessage("name", new LocalizableMessage("validate.notLongerThan", 50));

        if (StringUtils.isEmpty(xid))
            response.addMessage("xid", new LocalizableMessage("validate.required"));
        else if (StringUtils.isLengthGreaterThan(xid, 50))
            response.addMessage("xid", new LocalizableMessage("validate.notLongerThan", 50));
        else if (!new WatchListDao().isXidUnique(xid, id))
            response.addMessage("xid", new LocalizableMessage("validate.xidUsed"));

        for (DataPointVO dpVO : pointList)
            dpVO.validate(response);
    }

    //
    //
    // Serialization
    //
    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("xid", xid);

        map.put("user", new UserDao().getUser(userId).getUsername());

        List<String> dpXids = new ArrayList<String>();
        for (DataPointVO dpVO : pointList)
            dpXids.add(dpVO.getXid());
        map.put("dataPoints", dpXids);

        map.put("sharingUsers", watchListUsers);
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String username = json.getString("user");
        if (StringUtils.isEmpty(username))
            throw new LocalizableJsonException("emport.error.missingValue", "user");
        User user = new UserDao().getUser(username);
        if (user == null)
            throw new LocalizableJsonException("emport.error.missingUser", username);
        userId = user.getId();

        JsonArray jsonDataPoints = json.getJsonArray("dataPoints");
        if (jsonDataPoints != null) {
            pointList.clear();
            DataPointDao dataPointDao = new DataPointDao();
            for (JsonValue jv : jsonDataPoints.getElements()) {
                String xid = jv.toJsonString().getValue();
                DataPointVO dpVO = dataPointDao.getDataPoint(xid);
                if (dpVO == null)
                    throw new LocalizableJsonException("emport.error.missingPoint", xid);
                pointList.add(dpVO);
            }
        }

        JsonArray jsonSharers = json.getJsonArray("sharingUsers");
        if (jsonSharers != null) {
            watchListUsers.clear();
            for (JsonValue jv : jsonSharers.getElements()) {
                ShareUser shareUser = reader.readPropertyValue(jv, ShareUser.class, null);
                if (shareUser.getUserId() != userId)
                    // No need for the owning user to be in this list.
                    watchListUsers.add(shareUser);
            }
        }
    }
}
