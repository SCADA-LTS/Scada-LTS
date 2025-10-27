package org.scada_lts.web.mvc.api.user;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.scada_lts.web.beans.validation.xss.XssProtect;

public class UserInfoSimple {

    private int id = Common.NEW_ID;
    @XssProtect
    private String username;
    @XssProtect
    private String firstName;
    @XssProtect
    private String lastName;
    @XssProtect
    private String phone;
    @XssProtect
    private String email;
    private boolean admin;
    private boolean disabled;

    public UserInfoSimple() {
    }

    public UserInfoSimple(int id, String username, String firstName, String lastName, String phone, String email,
                          boolean admin, boolean disabled) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.admin = admin;
        this.disabled = disabled;
    }

    public UserInfoSimple(User user) {
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
