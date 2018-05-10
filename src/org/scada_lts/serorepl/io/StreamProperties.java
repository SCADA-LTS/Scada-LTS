package org.scada_lts.serorepl.io;

public class StreamProperties {

    private static final long serialVersionUID = 1L;
    private int baudRate = -1;
    private int flowControlIn = 0;
    private int flowControlOut = 0;
    private int dataBits = 8;
    private int stopBits = 1;
    private int parity = 0;
    private String commPortId;
    private String portOwnerName;


    public StreamProperties() {
    }

    public String getCommPortId() {
        return commPortId;
    }

    public void setCommPortId(String commPortId) {
        this.commPortId = commPortId;
    }

    public String getPortOwnerName() {
        return portOwnerName;
    }

    public void setPortOwnerName(String portOwnerName) {
        this.portOwnerName = portOwnerName;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getFlowControlIn() {
        return flowControlIn;
    }

    public void setFlowControlIn(int flowControlIn) {
        this.flowControlIn = flowControlIn;
    }

    public int getFlowControlOut() {
        return flowControlOut;
    }

    public void setFlowControlOut(int flowControlOut) {
        this.flowControlOut = flowControlOut;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }
}
