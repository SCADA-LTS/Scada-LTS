package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonPointEventDetector implements Serializable {
    private int id;
    private String xid;
    private String alias;

    public JsonPointEventDetector(int id, String xid, String alias) {
        this.setId(id);
        this.setXid(xid);
        this.setAlias(alias);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getXid() { return xid; }
    public void setXid(String xid) {
        this.xid = xid;
    }
}
