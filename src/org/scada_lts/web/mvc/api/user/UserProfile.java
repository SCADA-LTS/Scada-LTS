package org.scada_lts.web.mvc.api.user;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.web.beans.validation.xss.XssProtect;

import java.util.List;

public class UserProfile {

    @XssProtect
    private String name;

    private List<Integer> dataSourcePermissions;

    private List<DataPointAccess> dataPointPermissions;

    private List<WatchListAccess> watchlistPermissions;

    private List<ViewAccess> viewPermissions;

    @XssProtect
    private String xid;

    public UserProfile(String name, List<Integer> dataSourcePermissions, List<DataPointAccess> dataPointPermissions, List<WatchListAccess> watchlistPermissions, List<ViewAccess> viewPermissions, String xid) {
        this.name = name;
        this.dataSourcePermissions = dataSourcePermissions;
        this.dataPointPermissions = dataPointPermissions;
        this.watchlistPermissions = watchlistPermissions;
        this.viewPermissions = viewPermissions;
        this.xid = xid;
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

    public UsersProfileVO toVo() {
        UsersProfileVO usersProfileVO = new UsersProfileVO();
        usersProfileVO.setId(Common.NEW_ID);
        usersProfileVO.setName(name);
        usersProfileVO.setXid(xid);
        usersProfileVO.setViewPermissions(viewPermissions);
        usersProfileVO.setWatchlistPermissions(watchlistPermissions);
        usersProfileVO.setDataPointPermissions(dataPointPermissions);
        usersProfileVO.setDataSourcePermissions(dataSourcePermissions);
        return new UsersProfileVO();
    }
}
