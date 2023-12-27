package org.scada_lts.serial;


import java.io.InputStream;
import java.io.OutputStream;

public interface SerialPortService extends AutoCloseable {

    @Override
    void close();
    void open() throws SerialPortException;
    InputStream getInputStream();
    OutputStream getOutputStream();

    boolean isOpen();
    SerialPortParameters getParameters();

    static SerialPortService newService(SerialPortParameters serialParameters) {
        return new SerialPortServiceImpl(serialParameters);
    }
}
