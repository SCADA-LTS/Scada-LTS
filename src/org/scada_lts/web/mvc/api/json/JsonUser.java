package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.Common;

public class JsonUser {

    private int id = Common.NEW_ID;
    private String username;
    private String email;
    private String phone;
    private boolean admin;
    private boolean disabled;
    private String homeUrl;
    private long lastLogin;

    public JsonUser() {
    }

    public JsonUser(int id, String username, String email, String phone, boolean admin, boolean disabled, String homeUrl, long lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.admin = admin;
        this.disabled = disabled;
        this.homeUrl = homeUrl;
        this.lastLogin = lastLogin;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }
}
