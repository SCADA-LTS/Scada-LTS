package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonWatchList {

    private int id;
    private String xid;
    private String name;
    private int userId;
    private List<ScadaObjectIdentifier> pointList;
    private List<ShareUser> watchListUsers;

    public JsonWatchList() {}

    public JsonWatchList(int id, String xid, String name, int userId, List<ScadaObjectIdentifier> pointList, List<ShareUser> watchListUsers) {
        this.id = id;
        this.xid = xid;
        this.name = name;
        this.userId = userId;
        this.pointList = pointList;
        this.watchListUsers = watchListUsers;
    }

    public JsonWatchList(WatchList watchList) {
        this.id = watchList.getId();
        this.xid = watchList.getXid();
        this.name = watchList.getName();
        this.userId = watchList.getUserId();
        pointList = watchList.getPointList().stream()
                .map(DataPointVO::toIdentifier)
                .collect(Collectors.toCollection(ArrayList::new));
        watchListUsers = watchList.getWatchListUsers();
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
        this.name = name;
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

