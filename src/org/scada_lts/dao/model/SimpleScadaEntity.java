package org.scada_lts.dao.model;

public class SimpleScadaEntity {

    private int id;
    private String xid;
    private String name;

    public SimpleScadaEntity() {}

    public SimpleScadaEntity(int id, String xid, String name) {
        this.id = id;
        this.xid = xid;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
