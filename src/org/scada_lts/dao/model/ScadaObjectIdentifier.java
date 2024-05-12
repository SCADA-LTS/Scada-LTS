package org.scada_lts.dao.model;

/**
 * Scada Object Identifier
 *
 * This class contain a simple Scada Object
 * description that can be used in multiple
 * places among the project where user want
 * to list business object without details.
 */
public class ScadaObjectIdentifier extends BaseObjectIdentifier {

    private BaseObjectIdentifier baseObjectIdentifier;

    private String name;

    public ScadaObjectIdentifier() {}

    public ScadaObjectIdentifier(int id, String xid, String name) {
        super(id, xid);
        this.baseObjectIdentifier = new BaseObjectIdentifier(id, xid);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return baseObjectIdentifier.getId();
    }

    @Override
    public void setId(int id) {
        baseObjectIdentifier.setId(id);
    }

    @Override
    public String getXid() {
        return baseObjectIdentifier.getXid();
    }

    @Override
    public void setXid(String xid) {
        baseObjectIdentifier.setXid(xid);
    }
}
