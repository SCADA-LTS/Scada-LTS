package org.scada_lts.dao.model;

public class ScadaObjectIdentifierPermission extends ScadaObjectIdentifier {

    private int permission;

    public ScadaObjectIdentifierPermission() {}

    public ScadaObjectIdentifierPermission(int id, String xid, String name, int permission) {
        super(id, xid, name);
        this.permission = permission;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}
