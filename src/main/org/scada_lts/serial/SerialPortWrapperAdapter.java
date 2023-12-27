package org.scada_lts.serial;

import com.serotonin.modbus4j.serial.SerialPortWrapper;

import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortWrapperAdapter implements SerialPortWrapper {

    private final SerialPortService serialPortService;
    private final SerialPortParameters serialPortParameters;

    public SerialPortWrapperAdapter(SerialPortService serialPortService) {
        this.serialPortService = serialPortService;
        this.serialPortParameters = serialPortService.getParameters();
    }

    @Override
    public int getBaudRate() {
        return serialPortParameters.getBaudRate();
    }

    public String getCommPortId() {
        return serialPortParameters.getCommPortId();
    }

    @Override
    public int getDataBits() {
        return serialPortParameters.getDataBits();
    }

    @Override
    public int getFlowControlIn() {
        return serialPortParameters.getFlowControlIn();
    }

    @Override
    public int getFlowControlOut() {
        return serialPortParameters.getFlowControlOut();
    }

    @Override
    public int getParity() {
        return serialPortParameters.getParity();
    }

    @Override
    public int getStopBits() {
        return serialPortParameters.getStopBits();
    }

    public String getPortOwnerName() {
        return serialPortParameters.getPortOwnerName();
    }

    public int getTimeout() {
        return serialPortParameters.getTimeout();
    }

    @Override
    public void open() throws SerialPortException {
        serialPortService.open();
    }

    @Override
    public void close() {
        serialPortService.close();
    }

    @Override
    public InputStream getInputStream() {
        return serialPortService.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return serialPortService.getOutputStream();
    }
}
