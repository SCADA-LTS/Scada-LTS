package org.scada_lts.web.mvc.api.json;
import com.serotonin.mango.vo.User;
/**
 * @deprecated (new type UserInfo, UserInfoSimple, UserInfoPassword)
 */
@Deprecated
public class JsonUserInfo {

    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private boolean admin;
    private boolean disabled;

    public JsonUserInfo() {
    }

    public JsonUserInfo(int id, String username, String firstName, String lastName, String phone, String email, boolean admin, boolean disabled) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.admin = admin;
        this.disabled = disabled;
    }

    public JsonUserInfo(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.admin = user.isAdmin();
        this.disabled = user.isDisabled();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
