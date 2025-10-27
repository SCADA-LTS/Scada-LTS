package org.scada_lts.web.mvc.api.user;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.web.beans.validation.xss.XssProtect;

import java.util.List;

public class UserProfile {

    private int id;
    @XssProtect
    private String xid;
    @XssProtect
    private String name;
    private List<Integer> dataSourcePermissions;
    private List<DataPointAccess> dataPointPermissions;
    private List<WatchListAccess> watchlistPermissions;
    private List<ViewAccess> viewPermissions;

    public UserProfile() {}

    public UserProfile(UsersProfileVO profile) {
        this.id = profile.getId();
        this.xid = profile.getXid();
        this.name = profile.getName();
        this.dataSourcePermissions = profile.getDataSourcePermissions();
        this.dataPointPermissions = profile.getDataPointPermissions();
        this.watchlistPermissions = profile.getWatchlistPermissions();
        this.viewPermissions = profile.getViewPermissions();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getDataSourcePermissions() {
        return dataSourcePermissions;
    }

    public void setDataSourcePermissions(List<Integer> dataSourcePermissions) {
        this.dataSourcePermissions = dataSourcePermissions;
    }

    public List<DataPointAccess> getDataPointPermissions() {
        return dataPointPermissions;
    }

    public void setDataPointPermissions(List<DataPointAccess> dataPointPermissions) {
        this.dataPointPermissions = dataPointPermissions;
    }

    public List<WatchListAccess> getWatchlistPermissions() {
        return watchlistPermissions;
    }

    public void setWatchlistPermissions(List<WatchListAccess> watchlistPermissions) {
        this.watchlistPermissions = watchlistPermissions;
    }

    public List<ViewAccess> getViewPermissions() {
        return viewPermissions;
    }

    public void setViewPermissions(List<ViewAccess> viewPermissions) {
        this.viewPermissions = viewPermissions;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UsersProfileVO toVo() {
        UsersProfileVO usersProfileVO = new UsersProfileVO();
        usersProfileVO.setId(id);
        usersProfileVO.setXid(xid);
        usersProfileVO.setName(name);
        usersProfileVO.setViewPermissions(viewPermissions);
        usersProfileVO.setWatchlistPermissions(watchlistPermissions);
        usersProfileVO.setDataPointPermissions(dataPointPermissions);
        usersProfileVO.setDataSourcePermissions(dataSourcePermissions);
        return usersProfileVO;
    }
}
