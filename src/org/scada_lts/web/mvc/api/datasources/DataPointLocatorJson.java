package org.scada_lts.web.mvc.api.datasources;

import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;

public class DataPointLocatorJson {

    @JsonRemoteProperty
    private int dataTypeId;
    @JsonRemoteProperty
    private Boolean settable;

    public DataPointLocatorJson() {}

    public DataPointLocatorJson(PointLocatorVO pointLocatorVO) {
        this.dataTypeId = pointLocatorVO.getDataTypeId();
        this.settable = pointLocatorVO.isSettable();
    }

    public PointLocatorVO parsePointLocatorData() {
        throw new UnsupportedOperationException("Unsupported type");
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public Boolean isSettable() {
        return settable;
    }

    public void setSettable(Boolean settable) {
        this.settable = settable;
    }

}
