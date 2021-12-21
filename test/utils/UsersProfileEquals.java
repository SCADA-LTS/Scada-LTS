package utils;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;

import java.util.List;
import java.util.Objects;

public class UsersProfileEquals {

    private UsersProfileVO profile;

    public UsersProfileEquals(UsersProfileVO profile) {
        this.profile = profile;
    }

    public UsersProfileVO getProfile() {
        return profile;
    }

    public String getXid() {
        return profile.getXid();
    }

    public void setXid(String xid) {
        profile.setXid(xid);
    }

    public String getName() {
        return profile.getName();
    }

    public void setName(String username) {
        profile.setName(username);
    }

    public int getId() {
        return profile.getId();
    }

    public void setId(int id) {
        profile.setId(id);
    }

    public List<Integer> getDataSourcePermissions() {
        return profile.getDataSourcePermissions();
    }

    public List<DataPointAccess> getDataPointPermissions() {
        return profile.getDataPointPermissions();
    }
    public List<WatchListAccess> getWatchlistPermissions() {
        return profile.getWatchlistPermissions();
    }

    public List<ViewAccess> getViewPermissions() {
        return profile.getViewPermissions();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersProfileEquals)) return false;
        UsersProfileEquals that = (UsersProfileEquals) o;
        return getId() == that.getId() &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDataSourcePermissions(), that.getDataSourcePermissions()) &&
                Objects.equals(getDataPointPermissions(), that.getDataPointPermissions()) &&
                Objects.equals(getWatchlistPermissions(), that.getWatchlistPermissions()) &&
                Objects.equals(getViewPermissions(), that.getViewPermissions()) &&
                Objects.equals(getXid(), that.getXid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId(), getDataSourcePermissions(), getDataPointPermissions(), getWatchlistPermissions(), getViewPermissions(), getXid());
    }

    @Override
    public String toString() {
        return "UsersProfileEquals{" +
                "profile=" + profile +
                '}';
    }
}
