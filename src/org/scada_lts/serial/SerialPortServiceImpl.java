package org.scada_lts.serial;



import com.fazecast.jSerialComm.SerialPort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SerialPortServiceImpl implements SerialPortService {

    private static final Log LOG = LogFactory.getLog(SerialPortServiceImpl.class);
    public static final String SERIAL_PORT_NO_INITIALIZED = "SerialPort no initialized!";
    private final SerialPortParameters serialPortParameters;
    private SerialPort serialPort;
    private final ReentrantReadWriteLock lock;

    SerialPortServiceImpl(SerialPortParameters serialParameters) {
        this.serialPortParameters = serialParameters;
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void open() throws SerialPortException {
        this.lock.writeLock().lock();
        try {
            if(getSerialPort() == null || !getSerialPort().isOpen()) {
                SerialPort serialPortOpened = SerialPortUtils.openSerialPort(serialPortParameters);
                setSerialPort(serialPortOpened);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SerialPortException(ex);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void close() {
        this.lock.writeLock().lock();
        try {
            if(getSerialPort() == null) {
                LOG.warn(SERIAL_PORT_NO_INITIALIZED);
                return;
            }
            SerialPortUtils.close(getSerialPort());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public InputStream getInputStream() {
        this.lock.readLock().lock();
        try {
            if(getSerialPort() == null) {
                LOG.warn(SERIAL_PORT_NO_INITIALIZED);
                return null;
            }
            return getSerialPort().getInputStream();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public OutputStream getOutputStream() {
        this.lock.readLock().lock();
        try {
            if(getSerialPort() == null) {
                LOG.warn(SERIAL_PORT_NO_INITIALIZED);
                return null;
            }
            OutputStream outputStream = getSerialPort().getOutputStream();
            outputStream.flush();
            return outputStream;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean isOpen() {
        this.lock.readLock().lock();
        try {
            if(getSerialPort() == null) {
                LOG.warn(SERIAL_PORT_NO_INITIALIZED);
                return false;
            }
            return getSerialPort().isOpen();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public SerialPortParameters getParameters() {
        return serialPortParameters;
    }

    private void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    private SerialPort getSerialPort() {
        return this.serialPort;
    }
}
