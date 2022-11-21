package org.scada_lts.web.mvc.api.datasources;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import org.scada_lts.web.mvc.api.datasources.meta.MetaPointLocatorJson;
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
        @JsonSubTypes.Type(value = SnmpPointLocatorJson.class, name = "5"),
        @JsonSubTypes.Type(value = MetaPointLocatorJson.class, name = "9"),
})
public class DataPointLocatorJson {

    private int dataTypeId;
    private boolean settable;

    public DataPointLocatorJson() {}

    public DataPointLocatorJson(PointLocatorVO pointLocatorVO) {
        this.dataTypeId = pointLocatorVO.getDataTypeId();
        this.settable = pointLocatorVO.isSettable();
    }

    public PointLocatorVO parsePointLocatorData() {
        throw new UnsupportedOperationException("Method not overwritten");
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

}
