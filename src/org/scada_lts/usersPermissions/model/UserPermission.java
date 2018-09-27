package org.scada_lts.usersPermissions.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
public class UserPermission implements Serializable {

    long id;
    String entityXid;
    int permission;
    int userId;
    int entityType;

    public enum UserPermissionEntityType {
        DATASOURCE(1), DATAPOINT(2), VIEW(3), WATCHLIST(4);
        private final int entityType;

        UserPermissionEntityType(int entityType) {
            this.entityType = entityType;
        }

        public int toInt() {
            return entityType;
        }

        @Override
        public String toString() {
            return String.valueOf(entityType);
        }
    }

    public UserPermission() {
        //
    }

    public UserPermission(String entityXid, int permission, int userId) {
        this.entityXid = entityXid;
        this.permission = permission;
        this.userId = userId;
    }

    public UserPermission(String entityXid, int userId, int permission, int entityType) {
        this.entityXid = entityXid;
        this.permission = permission;
        this.userId = userId;
        this.entityType = entityType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
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

    @Override
    public String toString() {
        return "UserPermission{" +
                "id=" + id +
                ", entityXid='" + entityXid + '\'' +
                ", permission=" + permission +
                ", userId=" + userId +
                '}';
    }
}
