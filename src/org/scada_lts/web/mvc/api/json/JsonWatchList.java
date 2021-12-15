package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.permissions.service.WatchListGetShareUsers;
import org.scada_lts.utils.ApplicationBeans;

import java.util.ArrayList;
import java.util.List;

public class JsonWatchList extends ScadaObjectIdentifier {

    private int userId;
    private List<ScadaObjectIdentifier> pointList;
    private List<ShareUser> watchListUsers;

    public JsonWatchList() {
        super();
    }

    public JsonWatchList(int id, String xid, String name, int userId, List<ScadaObjectIdentifier> pointList, List<ShareUser> watchListUsers) {
        super(id, xid, name);
        this.userId = userId;
        this.pointList = pointList;
        this.watchListUsers = watchListUsers;
    }

    public JsonWatchList(WatchList wl) {
        super(wl.getId(), wl.getXid(), wl.getName());
        this.userId = wl.getUserId();
        pointList = new ArrayList<>();
        wl.getPointList().forEach(p -> pointList.add(new ScadaObjectIdentifier(p.getId(), p.getXid(), p.getName())));
        watchListUsers = ApplicationBeans.getWatchListGetShareUsersBean().getShareUsersWithProfile(wl);
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

    public WatchList createWatchList() {
        WatchList wl = new WatchList();
        wl.setId(this.getId());
        wl.setXid(this.getXid());
        wl.setUserId(this.getUserId());
        wl.setName(this.getName());
        List<DataPointVO> list = new ArrayList<>();
        this.getPointList().forEach(p -> {
            DataPointVO dp = new DataPointVO();
            dp.setId(p.getId());
            dp.setXid(p.getXid());
            dp.setName(p.getName());
            list.add(dp);
        });
        wl.setPointList(list);
        return wl;
    }
}

