package org.scada_lts.serorepl.io;

import jssc.SerialPortException;

import javax.comm.*;

import static jssc.SerialPortException.TYPE_INCORRECT_SERIAL_PORT;

public class SerialUtils {

    public static javax.comm.SerialPort openSerialPort(StreamProperties serialParameters) throws SerialPortException {
        SerialPort serialPort = null;

        try {
            CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(serialParameters.getCommPortId());

            if(commPortIdentifier.getPortType() != 1){
                throw new SerialPortException( serialParameters.getCommPortId(),"openSerialPort", TYPE_INCORRECT_SERIAL_PORT );
            }else {
                serialPort = (SerialPort) commPortIdentifier.open(serialParameters.getPortOwnerName(), 1000);
                serialPort.setSerialPortParams(serialParameters.getBaudRate(), serialParameters.getDataBits(), serialParameters.getStopBits(), serialParameters.getParity());
                serialPort.setFlowControlMode(serialParameters.getFlowControlIn() | serialParameters.getFlowControlOut());
                return serialPort;
            }

        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException e) {
            close(serialPort);
            e.printStackTrace();
        }
        return serialPort;
    }

    public static void close(SerialPort serialPort) {
        if (serialPort != null){

            serialPort.close();
        }
    }

}
