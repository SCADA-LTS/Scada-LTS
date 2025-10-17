package org.scada_lts.dao.model;

public class BaseObjectIdentifier {

    private int id;
    private String xid;

    public BaseObjectIdentifier() {}

    public BaseObjectIdentifier(int id, String xid) {
        this.id = id;
        this.xid = xid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }
}
