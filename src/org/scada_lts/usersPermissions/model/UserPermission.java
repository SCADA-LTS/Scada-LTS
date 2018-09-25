package org.scada_lts.usersPermissions.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
public class UserPermission implements Serializable {

    int id;
    String entityXid;
    int permission;
    int userId;

    public UserPermission() {
        //
    }

    public UserPermission(String entityXid, int permission, int userId) {
        this.entityXid = entityXid;
        this.permission = permission;
        this.userId = userId;
    }

    public UserPermission(int id, String entityXid, int permission, int userId) {
        this.id = id;
        this.entityXid = entityXid;
        this.permission = permission;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEntityXid() {
        return entityXid;
    }

    public void setEntityXid(String entityXid) {
        this.entityXid = entityXid;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserPermission{" +
                "id=" + id +
                ", entityXid='" + entityXid + '\'' +
                ", permission=" + permission +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPermission that = (UserPermission) o;
        return id == that.id &&
                permission == that.permission &&
                userId == that.userId &&
                Objects.equals(entityXid, that.entityXid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, entityXid, permission, userId);
    }
}
