package org.scada_lts.web.mvc.api.datasources;

import com.serotonin.mango.vo.DataPointVO;

import java.util.ArrayList;

public class DataPointJson {

    private Integer id;
    private String xid;
    private String name;
    private String description;
    private Boolean enabled;
    private int dataSourceTypeId;
    private int dataSourceId;
    private String deviceName;
    private DataPointLocatorJson pointLocator;
    private String datasourceName;
    private String dataSourceXid;
    private int typeId;
    private Boolean settable;

    public DataPointJson() {}

    public DataPointJson(DataPointVO dpVO) {
        this.id = dpVO.getId();
        this.xid = dpVO.getXid();
        this.name = dpVO.getName();
        this.description = dpVO.getDescription();
        this.enabled = dpVO.isEnabled();
        this.dataSourceTypeId = dpVO.getDataSourceTypeId();
        this.dataSourceId = dpVO.getDataSourceId();
        this.deviceName = dpVO.getDeviceName();
        this.pointLocator = DataSourcePointJsonFactory.getDataPointLocatorJson(dpVO.getPointLocator());
        this.datasourceName = dpVO.getDataSourceName();
        this.dataSourceXid = dpVO.getDataSourceXid();
        this.typeId = this.pointLocator.getDataTypeId();
        this.settable = this.pointLocator.isSettable();
    }

    public DataPointVO createDataPointVO() {
        DataPointVO dpVO = new DataPointVO();
        dpVO.setId(this.getId() != null ? this.id : 0);
        dpVO.setXid(this.xid);
        dpVO.setName(this.name);
        dpVO.setDescription(this.description);
        dpVO.setEnabled(this.enabled);
        dpVO.setDataSourceTypeId(this.dataSourceTypeId);
        dpVO.setDataSourceId(this.dataSourceId);
        dpVO.setDeviceName(this.deviceName);
        dpVO.setPointLocator(this.pointLocator.parsePointLocatorData());
        dpVO.setEventDetectors(new ArrayList<>());
        dpVO.setDataSourceName(this.datasourceName);
        dpVO.setDataSourceXid(this.dataSourceXid);
        dpVO.defaultTextRenderer();
        return dpVO;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public int getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(int dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public int getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(int dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DataPointLocatorJson getPointLocator() {
        return pointLocator;
    }

    public void setPointLocator(DataPointLocatorJson pointLocator) {
        this.pointLocator = pointLocator;
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

    public Boolean isSettable() {
        return settable;
    }

    public void setSettable(Boolean settable) {
        this.settable = settable;
    }

    public String getDataSourceXid() {
        return dataSourceXid;
    }

    public void setDataSourceXid(String dataSourceXid) {
        this.dataSourceXid = dataSourceXid;
    }
}
