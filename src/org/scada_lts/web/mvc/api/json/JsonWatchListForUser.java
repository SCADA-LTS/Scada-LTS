package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.Permissions;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;
import java.util.stream.Collectors;

public class JsonWatchListForUser {

    private int userId;
    private ScadaObjectIdentifier identifier;
    private List<DataPointOnWatchListForUser> pointList;
    private int accessType;

    public JsonWatchListForUser(WatchList watchList, User user) {
        this.identifier = watchList.toIdentifier();
        this.userId = watchList.getUserId();
        this.pointList = watchList.getPointList().stream()
                .map(point -> new DataPointOnWatchListForUser(point, getType(user, point)))
                .collect(Collectors.toList());
        this.accessType = watchList.getUserAccess(user);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ScadaObjectIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(ScadaObjectIdentifier identifier) {
        this.identifier = identifier;
    }

    public List<DataPointOnWatchListForUser> getPointList() {
        return pointList;
    }

    public void setPointList(List<DataPointOnWatchListForUser> pointList) {
        this.pointList = pointList;
    }

    public int getAccessType() {
        return accessType;
    }

    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }

    public static class DataPointOnWatchListForUser {

        private ScadaObjectIdentifier identifier;
        private String description;
        private int accessType;

        DataPointOnWatchListForUser(DataPointVO dp, int accessType) {
            this.identifier = dp.toIdentifier();
            this.description = dp.getDescription();
            this.accessType = accessType;
        }

        public ScadaObjectIdentifier getIdentifier() {
            return identifier;
        }

        public void setIdentifier(ScadaObjectIdentifier identifier) {
            this.identifier = identifier;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getAccessType() {
            return accessType;
        }

        public void setAccessType(int accessType) {
            this.accessType = accessType;
        }
    }

    private static int getType(User user, DataPointVO dataPoint) {
        if(dataPoint.getPointLocator() != null && dataPoint.getPointLocator().isSettable() && Permissions.hasDataPointSetPermission(user, dataPoint))
            return ShareUser.ACCESS_SET;
        if(Permissions.hasDataPointReadPermission(user, dataPoint))
            return ShareUser.ACCESS_READ;
        return ShareUser.ACCESS_NONE;
    }
}
