package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.view.ShareUser;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public class JsonWatchList extends ScadaObjectIdentifier {

    private int userId;
    private List<ScadaObjectIdentifier> pointList;
    private List<ShareUser> watchListUsers;

    public JsonWatchList(int id, String xid, String name, int userId, List<ScadaObjectIdentifier> pointList, List<ShareUser> watchListUsers) {
        super(id, xid, name);
        this.userId = userId;
        this.pointList = pointList;
        this.watchListUsers = watchListUsers;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<ScadaObjectIdentifier> getPointList() {
        return pointList;
    }

    public void setPointList(List<ScadaObjectIdentifier> pointList) {
        this.pointList = pointList;
    }

    public List<ShareUser> getWatchListUsers() {
        return watchListUsers;
    }

    public void setWatchListUsers(List<ShareUser> watchListUsers) {
        this.watchListUsers = watchListUsers;
    }
}

