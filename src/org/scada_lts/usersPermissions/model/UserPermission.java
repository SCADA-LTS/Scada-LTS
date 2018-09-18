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

    public UserPermission() {
        //
    }

    public UserPermission(int id, String entityXid, int permission) {
        this.id = id;
        this.entityXid = entityXid;
        this.permission = permission;
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

    @Override
    public String toString() {
        return "UserPermission{" +
                "id=" + id +
                ", entityXid='" + entityXid + '\'' +
                ", permission=" + permission +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPermission that = (UserPermission) o;
        return id == that.id &&
                permission == that.permission &&
                Objects.equals(entityXid, that.entityXid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, entityXid, permission);
    }
}
