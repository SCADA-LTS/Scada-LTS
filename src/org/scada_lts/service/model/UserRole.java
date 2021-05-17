package org.scada_lts.service.model;

public class UserRole {

    private int userId;
    private String role;

    public UserRole() {
    }

    public UserRole(int userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
