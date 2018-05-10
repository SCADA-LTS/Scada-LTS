package org.scada_lts.serorepl.io;

//import jssc.SerialPort;
//import jssc.SerialPortException;
//import javax.comm.SerialPort;
//import javax.comm.CommPortIdentifier;
//import javax.comm.NoSuchPortException;
//import javax.comm.PortInUseException;

public class SerialUtils {

   /* public static SerialPort openSerialPort(StreamProperties serialParameters) throws SerialPortException {
        SerialPort serialPort = null;

        try {
            CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(serialParameters.getCommPortId());

            if(commPortIdentifier.getPortType() != 1){
               // throw new SerialPortException("Port with ID: " + serialParameters.getCommPortId() + " is not a serial port");
            }else {
                serialPort = (SerialPort) commPortIdentifier.open(serialParameters.getPortOwnerName(), 1000);
            }

        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        }
    }

    public static void close(SerialPort serialPort) {

    }*/

}
