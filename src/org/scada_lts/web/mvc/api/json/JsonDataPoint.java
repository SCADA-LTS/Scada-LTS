package org.scada_lts.web.mvc.api.json;

public class JsonDataPoint {

    private int id;
    private String name;
    private String xid;
    private boolean enabled;
    private String description;
    private String datasourceName;
    private int typeId;

    public JsonDataPoint(int id, String name, String xid, boolean enabled, String description, String datasourceName, int typeId) {
        this.id = id;
        this.name = name;
        this.xid = xid;
        this.enabled = enabled;
        this.description = description;
        this.datasourceName = datasourceName;
        this.typeId = typeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

}
