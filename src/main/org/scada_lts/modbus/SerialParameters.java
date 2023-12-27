package org.scada_lts.modbus;

import java.io.InputStream;
import java.io.OutputStream;

import com.serotonin.modbus4j.serial.SerialPortWrapper;

@Deprecated
public class SerialParameters implements SerialPortWrapper {

	private String commPortId;
    private String portOwnerName;
    private int baudRate;
    private int flowControlIn;
    private int flowControlOut;
    private int dataBits;
    private int stopBits;
    private int parity;
    
    public SerialParameters() {
        this.baudRate = -1;
        this.flowControlIn = 0;
        this.flowControlOut = 0;
        this.dataBits = 8;
        this.stopBits = 1;
        this.parity = 0;
    }
    
    @Override
	public void close() throws Exception {
    	// not have in old version
		// no op ?		
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
	public InputStream getInputStream() {
		// TODO Auto-generated method stub (not have in old version)
		return null;
	}

	@Override
	public OutputStream getOutputStream() {
		// TODO Auto-generated method stub (not have in old version)
		return null;
	}

	@Override
	public void open() throws Exception {
		// TODO Auto-generated method stub (not have in old version)	
	}
	
}
