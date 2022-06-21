package org.scada_lts.serial;

import com.serotonin.modbus4j.serial.SerialPortWrapper;

import java.io.InputStream;
import java.io.OutputStream;

public interface SerialPortService extends SerialPortParameters, SerialPortWrapper {

    @Override
    void open() throws SerialPortException;
    @Override
    void close();
    @Override
    InputStream getInputStream();
    @Override
    OutputStream getOutputStream();

    boolean isOpen();

    static SerialPortService newService(SerialPortParameters serialParameters) {
        return new SerialPortServiceImpl(serialParameters);
    }
}
