package org.scada_lts.web.mvc.api.datasources;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "dataSourceTypeId"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VirtualPointLocatorJson.class, name = "1"),
        @JsonSubTypes.Type(value = SnmpPointLocatorJson.class, name = "5")
})
public abstract class DataPointLocatorAbstract<T> {

    private int dataTypeId;
    private int dataSourceTypeId;
    private boolean settable;

    public T parsePointLocatorData() {
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
