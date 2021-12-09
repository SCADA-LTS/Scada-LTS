package org.scada_lts.web.mvc.api.datasources.modbusip;

import com.serotonin.mango.vo.dataSource.modbus.ModbusPointLocatorVO;
import org.scada_lts.web.mvc.api.datasources.DataPointLocatorJson;

public class ModbusIpPointLocatorJson extends DataPointLocatorJson {

    private int range;
    private int modbusDataType;
    private int slaveId;
    private boolean slaveMonitor;
    private boolean socketMonitor;
    private int offset;
    private byte bit;
    private int registerCount;
    private String charset;
    private boolean settableOverride;
    private double multiplier;
    private double additive;
    boolean relinquishable;

    public ModbusIpPointLocatorJson() {}

    public ModbusIpPointLocatorJson(ModbusPointLocatorVO pointLocatorVO) {
        super(pointLocatorVO);
        this.range = pointLocatorVO.getRange();
        this.modbusDataType = pointLocatorVO.getModbusDataType();
        this.slaveId = pointLocatorVO.getSlaveId();
        this.slaveMonitor = pointLocatorVO.isSlaveMonitor();
        this.socketMonitor = pointLocatorVO.isSocketMonitor();
        this.offset = pointLocatorVO.getOffset();
        this.bit = pointLocatorVO.getBit();
        this.registerCount = pointLocatorVO.getRegisterCount();
        this.charset = pointLocatorVO.getCharset();
        this.settableOverride = pointLocatorVO.isSettableOverride();
        this.multiplier = pointLocatorVO.getMultiplier();
        this.additive = pointLocatorVO.getAdditive();
        this.relinquishable = pointLocatorVO.isRelinquishable();
    }

    @Override
    public ModbusPointLocatorVO parsePointLocatorData() {
        ModbusPointLocatorVO plVO = new ModbusPointLocatorVO();
        plVO.setRange(this.getRange());
        plVO.setModbusDataType(this.getModbusDataType());
        plVO.setSlaveId(this.getSlaveId());
        plVO.setSlaveMonitor(this.isSlaveMonitor());
        plVO.setSocketMonitor(this.isSocketMonitor());
        plVO.setOffset(this.getOffset());
        plVO.setBit(this.getBit());
        plVO.setRegisterCount(this.getRegisterCount());
        plVO.setCharset(this.getCharset());
        plVO.setSettableOverride(this.isSettableOverride());
        plVO.setMultiplier(this.getMultiplier());
        plVO.setAdditive(this.getAdditive());
        return plVO;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getModbusDataType() {
        return modbusDataType;
    }

    public void setModbusDataType(int modbusDataType) {
        this.modbusDataType = modbusDataType;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public boolean isSlaveMonitor() {
        return slaveMonitor;
    }

    public void setSlaveMonitor(boolean slaveMonitor) {
        this.slaveMonitor = slaveMonitor;
    }

    public boolean isSocketMonitor() {
        return socketMonitor;
    }

    public void setSocketMonitor(boolean socketMonitor) {
        this.socketMonitor = socketMonitor;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte getBit() {
        return bit;
    }

    public void setBit(byte bit) {
        this.bit = bit;
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public void setRegisterCount(int registerCount) {
        this.registerCount = registerCount;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isSettableOverride() {
        return settableOverride;
    }

    public void setSettableOverride(boolean settableOverride) {
        this.settableOverride = settableOverride;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getAdditive() {
        return additive;
    }

    public void setAdditive(double additive) {
        this.additive = additive;
    }

    public boolean isRelinquishable() {
        return relinquishable;
    }

    public void setRelinquishable(boolean relinquishable) {
        this.relinquishable = relinquishable;
    }
}
