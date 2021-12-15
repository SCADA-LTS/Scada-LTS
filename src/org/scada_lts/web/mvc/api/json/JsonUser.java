package org.scada_lts.web.mvc.api.json;
import com.serotonin.mango.vo.User;

import com.serotonin.mango.Common;

public class JsonUser {

    private int id = Common.NEW_ID;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean admin;
    private boolean disabled;
    private String homeUrl;
    private long lastLogin;
    private int receiveAlarmEmails;
    private boolean receiveOwnAuditEvents;
    private String theme;
    private boolean hideMenu;
    private int userProfile;

    public JsonUser() {
    }

    public JsonUser(int id, String username, String firstName, String lastName, String email, String phone, boolean admin, boolean disabled, String homeUrl, long lastLogin) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.admin = admin;
        this.disabled = disabled;
        this.homeUrl = homeUrl;
        this.lastLogin = lastLogin;
    }

    public JsonUser(int id, String username, String firstName, String lastName, String email, String phone, boolean admin, boolean disabled, String homeUrl, long lastLogin, int receiveAlarmEmails, boolean receiveOwnAuditEvents, String theme, boolean hideMenu, int userProfile) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.admin = admin;
        this.disabled = disabled;
        this.homeUrl = homeUrl;
        this.lastLogin = lastLogin;
        this.receiveAlarmEmails = receiveAlarmEmails;
        this.receiveOwnAuditEvents = receiveOwnAuditEvents;
        this.theme = theme;
        this.hideMenu = hideMenu;
        this.userProfile = userProfile;
    }

    public JsonUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.admin = user.isAdmin();
        this.disabled = user.isDisabled();
        this.homeUrl = user.getHomeUrl();
        this.lastLogin = user.getLastLogin();
        this.receiveAlarmEmails = user.getReceiveAlarmEmails();
        this.receiveOwnAuditEvents = user.isReceiveOwnAuditEvents();
        this.theme = user.getTheme();
        this.hideMenu = user.isHideMenu();
        this.userProfile = 0;
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

    public int getReceiveAlarmEmails() {
        return receiveAlarmEmails;
    }

    public void setReceiveAlarmEmails(int receiveAlarmEmails) {
        this.receiveAlarmEmails = receiveAlarmEmails;
    }

    public boolean isReceiveOwnAuditEvents() {
        return receiveOwnAuditEvents;
    }

    public void setReceiveOwnAuditEvents(boolean receiveOwnAuditEvents) {
        this.receiveOwnAuditEvents = receiveOwnAuditEvents;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isHideMenu() {
        return hideMenu;
    }

    public void setHideMenu(boolean hideMenu) {
        this.hideMenu = hideMenu;
    }

    public int getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(int userProfile) {
        this.userProfile = userProfile;
    }

    public User mapToUser() {
        User u = new User(this.getId(), this.getUsername(), this.getFirstName(), this.getLastName(),
        this.getEmail(), this.getPhone(), this.isAdmin(), this.isDisabled(), this.getHomeUrl(), this.getLastLogin());
        u.setTheme(this.getTheme());
        u.setHideMenu(this.isHideMenu());
        u.setReceiveOwnAuditEvents(this.isReceiveOwnAuditEvents());
        u.setReceiveAlarmEmails(this.getReceiveAlarmEmails());
        u.setUserProfileId(this.getUserProfile());
        return u;
    }
}
