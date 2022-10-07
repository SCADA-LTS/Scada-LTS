package org.scada_lts.web.mvc.api.datasources;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.scada_lts.web.mvc.api.datasources.meta.MetaDataSourceJson;
import org.scada_lts.web.mvc.api.datasources.modbusip.ModbusIpDataSourceJson;
import org.scada_lts.web.mvc.api.datasources.snmp.SnmpDataSourceJson;
import org.scada_lts.web.mvc.api.datasources.virtual.VirtualDataSourceJson;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VirtualDataSourceJson.class, name = "1"),
        @JsonSubTypes.Type(value = ModbusIpDataSourceJson.class, name = "3"),
        @JsonSubTypes.Type(value = SnmpDataSourceJson.class, name = "5"),
        @JsonSubTypes.Type(value = MetaDataSourceJson.class, name = "9")
})
public class DataSourceJson {

    private int id;
    private String xid;
    private String name;
    private int type;

    private boolean enabled;
    private String connectionDescription;

    public DataSourceJson() {
    }

    public DataSourceJson(DataSourceVO<?> dataSourceVO) {
        this.id = dataSourceVO.getId();
        this.xid = dataSourceVO.getXid();
        this.name = dataSourceVO.getName();
        this.type = dataSourceVO.getType().getId();
        this.enabled = dataSourceVO.isEnabled();
        this.connectionDescription = dataSourceVO.getConnectionDescription().getKey();
    }

    public DataSourceVO<?> createDataSourceVO() {
        throw new UnsupportedOperationException("Method not overwritten");
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getConnectionDescription() {
        return connectionDescription;
    }

    public void setConnectionDescription(String connectionDescription) {
        this.connectionDescription = connectionDescription;
    }
}
