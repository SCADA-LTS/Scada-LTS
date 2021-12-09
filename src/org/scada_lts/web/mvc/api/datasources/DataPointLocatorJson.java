package org.scada_lts.web.mvc.api.datasources;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import org.scada_lts.web.mvc.api.datasources.modbusip.ModbusIpPointLocatorJson;
import org.scada_lts.web.mvc.api.datasources.snmp.SnmpPointLocatorJson;
import org.scada_lts.web.mvc.api.datasources.virtual.VirtualPointLocatorJson;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "dataSourceTypeId"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VirtualPointLocatorJson.class, name = "1"),
        @JsonSubTypes.Type(value = ModbusIpPointLocatorJson.class, name = "3"),
        @JsonSubTypes.Type(value = SnmpPointLocatorJson.class, name = "5")
})
public abstract class DataPointLocatorJson {

    private int dataTypeId;
    private int dataSourceTypeId;
    private boolean settable;

    public DataPointLocatorJson() {}

    public DataPointLocatorJson(PointLocatorVO pointLocatorVO) {
        this.dataTypeId = pointLocatorVO.getDataTypeId();
        this.settable = pointLocatorVO.isSettable();
    }

    public PointLocatorVO parsePointLocatorData() {
        return null;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public boolean isSettable() {
        return settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public int getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(int dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

}
