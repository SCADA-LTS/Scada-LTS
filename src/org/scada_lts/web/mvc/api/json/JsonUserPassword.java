package org.scada_lts.web.mvc.api.json;
import com.serotonin.mango.vo.User;

import com.serotonin.mango.Common;
/**
 * @deprecated (new type UserInfo, UserInfoSimple, UserInfoPassword)
 */
@Deprecated
public class JsonUserPassword extends JsonUser {

    private String password;

    public JsonUserPassword() {}

    public JsonUserPassword(int id, String username, String firstName, String lastName, String email, String phone, boolean admin, boolean disabled, String homeUrl, long lastLogin, String password) {
        super(id, username, firstName, lastName, email, phone, admin, disabled, homeUrl, lastLogin);
        this.password = password;
    }

    public JsonUserPassword(int id, String username, String firstName, String lastName, String email, String phone, boolean admin, boolean disabled, String homeUrl, long lastLogin, int receiveAlarmEmails, boolean receiveOwnAuditEvents, String theme, boolean hideMenu, int userProfile, String password) {
        super(id, username, firstName, lastName, email, phone, admin, disabled, homeUrl, lastLogin, receiveAlarmEmails, receiveOwnAuditEvents, theme, hideMenu, userProfile);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Common.encrypt(password);
    }

    public void setPassword(String password, boolean skipEncryption) {
        if(skipEncryption) {
            this.password = password;
        } else {
            setPassword(password);
        }
    }

    @Override
    public User mapToUser() {
        User u = super.mapToUser();
        u.setPassword(this.getPassword());
        return u;
    }
}
