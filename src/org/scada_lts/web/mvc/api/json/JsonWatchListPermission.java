package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.vo.permission.Permissions;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifierPermission;
import org.scada_lts.web.beans.ApplicationBeans;

import java.util.ArrayList;
import java.util.List;

public class JsonWatchListPermission extends ScadaObjectIdentifier {

    private int userId;
    private List<ScadaObjectIdentifierPermission> pointList;
    private List<ShareUser> watchListUsers;

    public JsonWatchListPermission(WatchList wl, User user) {
        super(wl.getId(), wl.getXid(), wl.getName());
        this.userId = wl.getUserId();
        pointList = new ArrayList<>();
        wl.getPointList().forEach(p -> pointList.add(
                new ScadaObjectIdentifierPermission(
                        p.getId(), p.getXid(), p.getName(),
                        Permissions.hasDataPointSetPermission(user, p)
                                ? DataPointAccess.SET
                                : DataPointAccess.READ)));
        watchListUsers = ApplicationBeans.getWatchListGetShareUsersBean().getShareUsersWithProfile(wl);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<ScadaObjectIdentifierPermission> getPointList() {
        return pointList;
    }

    public void setPointList(List<ScadaObjectIdentifierPermission> pointList) {
        this.pointList = pointList;
    }

    public List<ShareUser> getWatchListUsers() {
        return watchListUsers;
    }

    public void setWatchListUsers(List<ShareUser> watchListUsers) {
        this.watchListUsers = watchListUsers;
    }

}
