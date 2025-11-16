package com.serotonin.mango.web.dwr.beans;

public class ModbusDeviceInfoBean {
    private int slaveId;
    private String vendorName;
    private String productCode;
    private String majorMinorRevision;
    private String errorMessage;

    public ModbusDeviceInfoBean(int slaveId) {
        this.slaveId = slaveId;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getMajorMinorRevision() {
        return majorMinorRevision;
    }

    public void setMajorMinorRevision(String majorMinorRevision) {
        this.majorMinorRevision = majorMinorRevision;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
