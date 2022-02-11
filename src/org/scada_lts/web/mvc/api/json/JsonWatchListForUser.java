package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.Permissions;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.permissions.service.ShareUserType;

import java.util.List;
import java.util.stream.Collectors;

public class JsonWatchListForUser {

    private int userId;
    private ScadaObjectIdentifier identifier;
    private List<DataPointOnWatchListForUser> pointList;
    private ShareUserType shareUserType;


    public JsonWatchListForUser(WatchList wl, User user) {
        this.identifier = wl.toIdentifier();
        this.userId = wl.getUserId();
        this.pointList = wl.getPointList().stream()
                .map(point -> new DataPointOnWatchListForUser(point, getType(user, point)))
                .collect(Collectors.toList());
        this.shareUserType = ShareUserType.getType(wl.getUserAccess(user));
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

    public ShareUserType getShareUserType() {
        return shareUserType;
    }

    public void setShareUserType(ShareUserType shareUserType) {
        this.shareUserType = shareUserType;
    }

    public static class DataPointOnWatchListForUser {

        private ScadaObjectIdentifier identifier;
        private String description;
        private ShareUserType shareUserType;

        DataPointOnWatchListForUser(DataPointVO dp, ShareUserType shareUserType) {
            this.identifier = dp.toIdentifier();
            this.description = dp.getDescription();
            this.shareUserType = shareUserType;
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

        public ShareUserType getShareUserType() {
            return shareUserType;
        }

        public void setShareUserType(ShareUserType shareUserType) {
            this.shareUserType = shareUserType;
        }
    }

    private static ShareUserType getType(User user, DataPointVO dataPoint) {
        if(dataPoint.isSettable() && Permissions.hasDataPointSetPermission(user, dataPoint))
            return ShareUserType.ACCESS_SET;
        if(Permissions.hasDataPointReadPermission(user, dataPoint))
            return ShareUserType.ACCESS_READ;
        return ShareUserType.ACCESS_NONE;
    }
}
