package org.scada_lts.web.mvc.api.datasources.modbusip;

import com.serotonin.mango.vo.dataSource.modbus.ModbusIpDataSourceVO;
import org.scada_lts.web.mvc.api.datasources.DataSourceJson;

public class ModbusIpDataSourceJson extends DataSourceJson {

    private int updatePeriodType;
    private int updatePeriods;

    private boolean quantize;
    private int timeout;
    private int retries;
    private boolean contiguousBatches;
    private boolean createSlaveMonitorPoints;
    private int maxReadBitCount;
    private int maxReadRegisterCount;
    private int maxWriteRegisterCount;

    private String transportType;
    private String host;
    private int port;
    private boolean encapsulated;
    private boolean createSocketMonitorPort;

    public ModbusIpDataSourceJson() {}

    public ModbusIpDataSourceJson(ModbusIpDataSourceVO dataSourceVO) {
        super(dataSourceVO);
        this.updatePeriodType = dataSourceVO.getUpdatePeriodType();
        this.updatePeriods = dataSourceVO.getUpdatePeriods();
        this.quantize = dataSourceVO.isQuantize();
        this.timeout = dataSourceVO.getTimeout();
        this.retries = dataSourceVO.getRetries();
        this.contiguousBatches = dataSourceVO.isContiguousBatches();
        this.createSlaveMonitorPoints = dataSourceVO.isCreateSlaveMonitorPoints();
        this.maxReadBitCount = dataSourceVO.getMaxReadBitCount();
        this.maxReadRegisterCount = dataSourceVO.getMaxReadRegisterCount();
        this.maxWriteRegisterCount = dataSourceVO.getMaxWriteRegisterCount();

        this.transportType = dataSourceVO.getTransportType().name();
        this.host = dataSourceVO.getHost();
        this.port = dataSourceVO.getPort();
        this.encapsulated = dataSourceVO.isEncapsulated();
        this.createSocketMonitorPort = dataSourceVO.isCreateSocketMonitorPoint();
    }

    @Override
    public ModbusIpDataSourceVO createDataSourceVO() {
        ModbusIpDataSourceVO vo = new ModbusIpDataSourceVO();
        vo.setId(this.getId());
        vo.setName(this.getName());
        vo.setXid(this.getXid());
        vo.setEnabled(this.isEnabled());
        vo.setUpdatePeriodType(this.getUpdatePeriodType());
        vo.setUpdatePeriods(this.getUpdatePeriods());
        vo.setQuantize(this.isQuantize());
        vo.setTimeout(this.getTimeout());
        vo.setRetries(this.getRetries());
        vo.setContiguousBatches(this.isContiguousBatches());
        vo.setCreateSocketMonitorPoint(this.isCreateSocketMonitorPort());
        vo.setMaxReadBitCount(this.getMaxReadBitCount());
        vo.setMaxReadRegisterCount(this.getMaxReadRegisterCount());
        vo.setMaxWriteRegisterCount(this.getMaxWriteRegisterCount());

        vo.setTransportTypeStr(this.getTransportType());
        vo.setHost(this.getHost());
        vo.setPort(this.getPort());
        vo.setEncapsulated(this.isEncapsulated());
        vo.setCreateSocketMonitorPoint(this.isCreateSocketMonitorPort());

        return vo;
    }

    public int getUpdatePeriodType() {
        return updatePeriodType;
    }

    public void setUpdatePeriodType(int updatePeriodType) {
        this.updatePeriodType = updatePeriodType;
    }

    public int getUpdatePeriods() {
        return updatePeriods;
    }

    public void setUpdatePeriods(int updatePeriods) {
        this.updatePeriods = updatePeriods;
    }

    public boolean isQuantize() {
        return quantize;
    }

    public void setQuantize(boolean quantize) {
        this.quantize = quantize;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public boolean isContiguousBatches() {
        return contiguousBatches;
    }

    public void setContiguousBatches(boolean contiguousBatches) {
        this.contiguousBatches = contiguousBatches;
    }

    public boolean isCreateSlaveMonitorPoints() {
        return createSlaveMonitorPoints;
    }

    public void setCreateSlaveMonitorPoints(boolean createSlaveMonitorPoints) {
        this.createSlaveMonitorPoints = createSlaveMonitorPoints;
    }

    public int getMaxReadBitCount() {
        return maxReadBitCount;
    }

    public void setMaxReadBitCount(int maxReadBitCount) {
        this.maxReadBitCount = maxReadBitCount;
    }

    public int getMaxReadRegisterCount() {
        return maxReadRegisterCount;
    }

    public void setMaxReadRegisterCount(int maxReadRegisterCount) {
        this.maxReadRegisterCount = maxReadRegisterCount;
    }

    public int getMaxWriteRegisterCount() {
        return maxWriteRegisterCount;
    }

    public void setMaxWriteRegisterCount(int maxWriteRegisterCount) {
        this.maxWriteRegisterCount = maxWriteRegisterCount;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEncapsulated() {
        return encapsulated;
    }

    public void setEncapsulated(boolean encapsulated) {
        this.encapsulated = encapsulated;
    }

    public boolean isCreateSocketMonitorPort() {
        return createSocketMonitorPort;
    }

    public void setCreateSocketMonitorPort(boolean createSocketMonitorPort) {
        this.createSocketMonitorPort = createSocketMonitorPort;
    }
}
