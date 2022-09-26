package org.scada_lts.serial;

class SerialPortParametersImpl implements SerialPortParameters {

	private String commPortId;
    private String portOwnerName;
    private int baudRate;
    private int flowControlIn;
    private int flowControlOut;
    private int dataBits;
    private int stopBits;
    private int parity;
    private int timeout;

    SerialPortParametersImpl() {
        this.baudRate = -1;
        this.flowControlIn = 0;
        this.flowControlOut = 0;
        this.dataBits = 8;
        this.stopBits = 1;
        this.parity = 0;
        this.timeout = 0;
    }

	@Override
	public int getBaudRate() {
		return this.baudRate;
	}
    
    public void setBaudRate(final int baudRate) {
        this.baudRate = baudRate;
    }
    
    public String getCommPortId() {
        return this.commPortId;
    }
    
    public void setCommPortId(final String commPortId) {
        this.commPortId = commPortId;
    }
    
    @Override
	public int getDataBits() {
    	return this.dataBits;
	}
    
    public void setDataBits(final int dataBits) {
        this.dataBits = dataBits;
    }
    
    
    @Override
	public int getFlowControlIn() {
    	return this.flowControlIn;
	}
    
    public void setFlowControlIn(final int flowControlIn) {
        this.flowControlIn = flowControlIn;
    }
    
    @Override
    public int getFlowControlOut() {
    	return this.flowControlOut;
    }
    
    public void setFlowControlOut(final int flowControlOut) {
        this.flowControlOut = flowControlOut;
    }
    
    @Override
    public int getParity() {
        return this.parity;
    }
    
    public void setParity(final int parity) {
        this.parity = parity;
    }
    
    @Override
    public int getStopBits() {
        return this.stopBits;
    }
    
    public void setStopBits(final int stopBits) {
        this.stopBits = stopBits;
    }
    
    public String getPortOwnerName() {
        return this.portOwnerName;
    }
    
    public void setPortOwnerName(final String portOwnerName) {
        this.portOwnerName = portOwnerName;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "SerialPortParametersImpl{" +
                "commPortId='" + commPortId + '\'' +
                ", portOwnerName='" + portOwnerName + '\'' +
                ", baudRate=" + baudRate +
                ", flowControlIn=" + flowControlIn +
                ", flowControlOut=" + flowControlOut +
                ", dataBits=" + dataBits +
                ", stopBits=" + stopBits +
                ", parity=" + parity +
                ", timeout=" + timeout +
                '}';
    }
}
