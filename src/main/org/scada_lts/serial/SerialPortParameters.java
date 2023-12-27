package org.scada_lts.serial;

import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO;


public interface SerialPortParameters {

    int getBaudRate();

    String getCommPortId();

    int getDataBits();

    int getFlowControlIn();

    int getFlowControlOut();

    int getParity();

    int getStopBits();

    String getPortOwnerName();

    int getTimeout();

    static SerialPortParameters newParameters(String portOwnerName, ModbusSerialDataSourceVO configuration) {
        SerialPortParametersImpl params = new SerialPortParametersImpl();
        params.setCommPortId(configuration.getCommPortId());
        params.setPortOwnerName(portOwnerName);
        params.setBaudRate(configuration.getBaudRate());
        params.setFlowControlIn(configuration.getFlowControlIn());
        params.setFlowControlOut(configuration.getFlowControlOut());
        params.setDataBits(configuration.getDataBits());
        params.setStopBits(configuration.getStopBits());
        params.setParity(configuration.getParity());
        params.setTimeout(configuration.getTimeout());
        return params;
    }

    static SerialPortParameters newParameters(String portOwnerName, String commPortId, int baudRate, int flowControlIn,
                                              int flowControlOut, int dataBits, int stopBits, int parity, int timeout) {
        SerialPortParametersImpl params = new SerialPortParametersImpl();
        params.setCommPortId(commPortId);
        params.setPortOwnerName(portOwnerName);
        params.setBaudRate(baudRate);
        params.setFlowControlIn(flowControlIn);
        params.setFlowControlOut(flowControlOut);
        params.setDataBits(dataBits);
        params.setStopBits(stopBits);
        params.setParity(parity);
        params.setTimeout(timeout);
        return params;
    }
}
