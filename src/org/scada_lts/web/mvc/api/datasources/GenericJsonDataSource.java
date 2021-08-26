package org.scada_lts.web.mvc.api.datasources;

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;

public class GenericJsonDataSource {

    private boolean enabled;
    private int id;
    private int type;
    private String xid;
    private String name;
    private String connectionDescription;

    public GenericJsonDataSource() {}

    public GenericJsonDataSource(DataSourceVO<?> ds) {
        this.enabled = ds.isEnabled();
        this.id = ds.getId();
        this.type = ds.getType().getId();
        this.xid = ds.getXid();
        this.name = ds.getName();
        this.connectionDescription = ds.getConnectionDescription().getKey();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int typeId) {
        this.type = typeId;
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

    public String getConnectionDescription() {
        return connectionDescription;
    }

    public void setConnectionDescription(String connectionDescription) {
        this.connectionDescription = connectionDescription;
    }
}
