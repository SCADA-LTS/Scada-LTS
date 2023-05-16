package org.scada_lts.web.mvc.api.user;
import com.serotonin.mango.vo.User;

public class UserInfo extends UserInfoSimple {

    private String homeUrl;
    private long lastLogin;
    private int receiveAlarmEmails;
    private boolean receiveOwnAuditEvents;
    private String theme;
    private boolean hideMenu;
    private int userProfile;
    private boolean enableFullScreen;
    private boolean hideShortcutDisableFullScreen;
    private boolean forceFullScreenMode;
    private boolean forceHideShortcutDisableFulLScreen;

    public UserInfo() {
    }

    @Deprecated
    public UserInfo(int id, String username, String firstName, String lastName, String email, String phone,
                    boolean admin, boolean disabled, String homeUrl, long lastLogin) {
        super(id, username, firstName, lastName, phone, email, admin, disabled);
        this.homeUrl = homeUrl;
        this.lastLogin = lastLogin;
    }

    public UserInfo(int id, String username, String firstName, String lastName, String email, String phone,
                    boolean admin, boolean disabled, String homeUrl, long lastLogin, int receiveAlarmEmails,
                    boolean receiveOwnAuditEvents, String theme, boolean hideMenu, int userProfile,
                    boolean enableFullScreen, boolean hideShortcutDisableFullScreen) {
        super(id, username, firstName, lastName, phone, email, admin, disabled);
        this.homeUrl = homeUrl;
        this.lastLogin = lastLogin;
        this.receiveAlarmEmails = receiveAlarmEmails;
        this.receiveOwnAuditEvents = receiveOwnAuditEvents;
        this.theme = theme;
        this.hideMenu = hideMenu;
        this.userProfile = userProfile;
        this.enableFullScreen = enableFullScreen;
        this.hideShortcutDisableFullScreen = hideShortcutDisableFullScreen;
    }

    public UserInfo(User user, boolean forceFullScreenMode, boolean forceHideShortcutDisableFulLScreen) {
        super(user);
        this.homeUrl = user.getHomeUrl();
        this.lastLogin = user.getLastLogin();
        this.receiveAlarmEmails = user.getReceiveAlarmEmails();
        this.receiveOwnAuditEvents = user.isReceiveOwnAuditEvents();
        this.theme = user.getTheme();
        this.hideMenu = user.isHideMenu();
        this.userProfile = user.getUserProfile();
        this.enableFullScreen = user.isEnableFullScreen();
        this.hideShortcutDisableFullScreen = user.isHideShortcutDisableFullScreen();
        this.forceFullScreenMode = forceFullScreenMode;
        this.forceHideShortcutDisableFulLScreen = forceHideShortcutDisableFulLScreen;
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

    public boolean isEnableFullScreen() {
        return enableFullScreen;
    }

    public void setEnableFullScreen(boolean enableFullScreen) {
        this.enableFullScreen = enableFullScreen;
    }

    public boolean isHideShortcutDisableFullScreen() {
        return hideShortcutDisableFullScreen;
    }

    public void setHideShortcutDisableFullScreen(boolean hideShortcutDisableFullScreen) {
        this.hideShortcutDisableFullScreen = hideShortcutDisableFullScreen;
    }

    public boolean isForceFullScreenMode() {
        return forceFullScreenMode;
    }

    public void setForceFullScreenMode(boolean forceFullScreenMode) {
        this.forceFullScreenMode = forceFullScreenMode;
    }

    public boolean isForceHideShortcutDisableFulLScreen() {
        return forceHideShortcutDisableFulLScreen;
    }

    public void setForceHideShortcutDisableFulLScreen(boolean forceHideShortcutDisableFulLScreen) {
        this.forceHideShortcutDisableFulLScreen = forceHideShortcutDisableFulLScreen;
    }

    public User toUser() {
        User u = new User(this.getId(), this.getUsername(), this.getFirstName(), this.getLastName(),
        this.getEmail(), this.getPhone(), this.isAdmin(), this.isDisabled(), this.getHomeUrl(), this.getLastLogin());
        u.setTheme(this.getTheme());
        u.setHideMenu(this.isHideMenu());
        u.setReceiveOwnAuditEvents(this.isReceiveOwnAuditEvents());
        u.setReceiveAlarmEmails(this.getReceiveAlarmEmails());
        u.setUserProfileId(this.getUserProfile());
        u.setEnableFullScreen(this.isEnableFullScreen());
        u.setHideShortcutDisableFullScreen(this.isHideShortcutDisableFullScreen());
        return u;
    }

    public User toUserNonAdmin(User userFromSession) {
        User u = new User(userFromSession.getId(), userFromSession.getUsername(), this.getFirstName(), this.getLastName(),
                this.getEmail(), this.getPhone(), false, userFromSession.isDisabled(), userFromSession.getHomeUrl(),
                userFromSession.getLastLogin());
        u.setHideMenu(userFromSession.isHideMenu());
        u.setUserProfileId(userFromSession.getUserProfile());

        u.setReceiveOwnAuditEvents(this.isReceiveOwnAuditEvents());
        u.setReceiveAlarmEmails(this.getReceiveAlarmEmails());
        u.setTheme(this.getTheme());
        u.setEnableFullScreen(this.isEnableFullScreen());
        u.setHideShortcutDisableFullScreen(this.isHideShortcutDisableFullScreen());
        return u;
    }
}
