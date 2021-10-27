package utils;

import br.org.scadabr.vo.exporter.ZIPProjectManager;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.mango.web.dwr.beans.DataExportDefinition;
import com.serotonin.mango.web.dwr.beans.EventExportDefinition;
import com.serotonin.mango.web.dwr.beans.ImportTask;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserNonAttributesEquals {

    private User user;

    public UserNonAttributesEquals(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getPhone() {
        return user.getPhone();
    }

    public int getId() {
        return user.getId();
    }

    public String getPassword() {
        return user.getPassword();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public View getView() {
        return user.getView();
    }

    public WatchList getWatchList() {
        return user.getWatchList();
    }

    public DataPointVO getEditPoint() {
        return user.getEditPoint();
    }

    public boolean isDisabled() {
        return user.isDisabled();
    }

    public List<Integer> getDataSourcePermissions() {
        return user.getDataSourcePermissions();
    }

    public List<DataPointAccess> getDataPointPermissions() {
        return user.getDataPointPermissions();
    }

    public DataSourceVO<?> getEditDataSource() {
        return user.getEditDataSource();
    }

    public int getSelectedWatchList() {
        return user.getSelectedWatchList();
    }

    public String getHomeUrl() {
        return user.getHomeUrl();
    }

    public long getLastLogin() {
        return user.getLastLogin();
    }

    public Map<String, byte[]> getReportImageData() {
        return user.getReportImageData();
    }

    public PublisherVO<? extends PublishedPointVO> getEditPublisher() {
        return user.getEditPublisher();
    }

    public ImportTask getImportTask() {
        return user.getImportTask();
    }

    public boolean isMuted() {
        return user.isMuted();
    }

    public int getReceiveAlarmEmails() {
        return user.getReceiveAlarmEmails();
    }

    public boolean isReceiveOwnAuditEvents() {
        return user.isReceiveOwnAuditEvents();
    }

    public DataExportDefinition getDataExportDefinition() {
        return user.getDataExportDefinition();
    }

    public EventExportDefinition getEventExportDefinition() {
        return user.getEventExportDefinition();
    }

    public String getTheme() {
        return user.getTheme();
    }

    public boolean isHideMenu() {
        return user.isHideMenu();
    }

    public ZIPProjectManager getUploadedProject() {
        return user.getUploadedProject();
    }

    public int getUserProfile() {
        return user.getUserProfile();
    }

    public List<Integer> getDataSourceProfilePermissions() {
        return user.getDataSourceProfilePermissions();
    }

    public List<DataPointAccess> getDataPointProfilePermissions() {
        return user.getDataPointProfilePermissions();
    }

    public List<WatchListAccess> getWatchListProfilePermissions() {
        return user.getWatchListProfilePermissions();
    }

    public List<ViewAccess> getViewProfilePermissions() {
        return user.getViewProfilePermissions();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserNonAttributesEquals)) return false;
        UserNonAttributesEquals user = (UserNonAttributesEquals) o;
        return this.user.getId() == user.getId() &&
                this.user.isAdmin() == user.isAdmin() &&
                this.user.isDisabled() == user.isDisabled() &&
                this.user.getSelectedWatchList() == user.getSelectedWatchList() &&
                this.user.getLastLogin() == user.getLastLogin() &&
                this.user.getReceiveAlarmEmails() == user.getReceiveAlarmEmails() &&
                this.user.isReceiveOwnAuditEvents() == user.isReceiveOwnAuditEvents() &&
                this.user.isHideMenu() == user.isHideMenu() &&
                this.user.getUserProfile() == user.getUserProfile() &&
                this.user.isMuted() == user.isMuted() &&
                Objects.equals(this.user.getUsername(), user.getUsername()) &&
                Objects.equals(this.user.getPassword(), user.getPassword()) &&
                Objects.equals(this.user.getEmail(), user.getEmail()) &&
                Objects.equals(this.user.getPhone(), user.getPhone()) &&
                Objects.equals(this.user.getDataSourcePermissions(), user.getDataSourcePermissions()) &&
                Objects.equals(this.user.getDataPointPermissions(), user.getDataPointPermissions()) &&
                Objects.equals(this.user.getDataSourceProfilePermissions(), user.getDataSourceProfilePermissions()) &&
                Objects.equals(this.user.getDataPointProfilePermissions(), user.getDataPointProfilePermissions()) &&
                Objects.equals(this.user.getWatchListProfilePermissions(), user.getWatchListProfilePermissions()) &&
                Objects.equals(this.user.getViewProfilePermissions(), user.getViewProfilePermissions()) &&
                Objects.equals(this.user.getHomeUrl(), user.getHomeUrl()) &&
                Objects.equals(this.user.getTheme(), user.getTheme()) &&
                Objects.equals(this.user.getView(), user.getView()) &&
                Objects.equals(this.user.getWatchList(), user.getWatchList()) &&
                Objects.equals(this.user.getEditPoint(), user.getEditPoint()) &&
                Objects.equals(this.user.getEditDataSource(), user.getEditDataSource()) &&
                Objects.equals(this.user.getReportImageData(), user.getReportImageData()) &&
                Objects.equals(this.user.getEditPublisher(), user.getEditPublisher()) &&
                Objects.equals(this.user.getImportTask(), user.getImportTask()) &&
                Objects.equals(this.user.getDataExportDefinition(), user.getDataExportDefinition()) &&
                Objects.equals(this.user.getEventExportDefinition(), user.getEventExportDefinition()) &&
                Objects.equals(this.user.getUploadedProject(), user.getUploadedProject()) &&
                Objects.equals(this.user.getFirstName(), user.getFirstName()) &&
                Objects.equals(this.user.getLastName(), user.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.user.getId(), this.user.getUsername(), this.user.getPassword(), this.user.getEmail(),
                this.user.getPhone(), this.user.isAdmin(), this.user.isDisabled(), this.user.getDataSourcePermissions(),
                this.user.getDataPointPermissions(), this.user.getDataSourceProfilePermissions(),
                this.user.getDataPointProfilePermissions(), this.user.getWatchListProfilePermissions(),
                this.user.getViewProfilePermissions(), this.user.getSelectedWatchList(), this.user.getHomeUrl(),
                this.user.getLastLogin(), this.user.getReceiveAlarmEmails(), this.user.isReceiveOwnAuditEvents(),
                this.user.getTheme(), this.user.isHideMenu(), this.user.getUserProfile(), this.user.getView(),
                this.user.getWatchList(), this.user.getEditPoint(), this.user.getEditDataSource(),
                this.user.getReportImageData(), this.user.getEditPublisher(), this.user.getImportTask(), this.user.isMuted(),
                this.user.getDataExportDefinition(), this.user.getEventExportDefinition(), this.user.getUploadedProject(),
                this.user.getFirstName(), this.user.getLastName());
    }

    @Override
    public String toString() {
        return "UserNonAttributesEquals{" +
                "user=" + user +
                '}';
    }
}
