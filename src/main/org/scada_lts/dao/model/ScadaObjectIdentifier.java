package org.scada_lts.dao.model;

/**
 * Scada Object Identifier
 *
 * This class contain a simple Scada Object
 * description that can be used in multiple
 * places among the project where user want
 * to list business object without details.
 */
public class ScadaObjectIdentifier {

    private int id;
    private String xid;
    private String name;

    public ScadaObjectIdentifier() {}

    public ScadaObjectIdentifier(int id, String xid, String name) {
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
