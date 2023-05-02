package com.serotonin.modbus4j;

import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ModbusResponse;
import com.serotonin.modbus4j.sero.epoll.InputStreamEPollWrapper;
import com.serotonin.modbus4j.sero.log.BaseIOLog;
import com.serotonin.modbus4j.sero.messaging.MessageControl;
import com.serotonin.modbus4j.sero.messaging.MessagingExceptionHandler;
import com.serotonin.modbus4j.sero.util.ProgressiveTask;

import java.util.List;

public interface IModbusMaster {
    boolean isConnected();

    void setConnected(boolean connected);

    void init() throws ModbusInitException;

    boolean isInitialized();

    void destroy();

    ModbusResponse sendImpl(ModbusRequest var1) throws ModbusTransportException;

    <T> T getValue(BaseLocator<T> locator) throws ModbusTransportException, ErrorResponseException;

    <T> void setValue(BaseLocator<T> locator, Object value) throws ModbusTransportException, ErrorResponseException;

    List<Integer> scanForSlaveNodes();

    ProgressiveTask scanForSlaveNodes(NodeScanListener l);

    boolean testSlaveNode(int node);

    int getRetries();

    void setRetries(int retries);

    int getTimeout();

    void setTimeout(int timeout);

    boolean isMultipleWritesOnly();

    void setMultipleWritesOnly(boolean multipleWritesOnly);

    int getDiscardDataDelay();

    void setDiscardDataDelay(int discardDataDelay);

    BaseIOLog getIoLog();

    void setIoLog(BaseIOLog ioLog);

    InputStreamEPollWrapper getePoll();

    void setePoll(InputStreamEPollWrapper ePoll);

    <K> BatchResults<K> send(BatchRead<K> batch) throws ModbusTransportException, ErrorResponseException;

    MessageControl getMessageControl();

    void closeMessageControl(MessageControl conn);

    int getMaxReadCount(int registerRange);

    void validateNumberOfBits(int bits) throws ModbusTransportException;

    void validateNumberOfRegisters(int registers) throws ModbusTransportException;

    void setExceptionHandler(MessagingExceptionHandler exceptionHandler);

    MessagingExceptionHandler getExceptionHandler();

    int getMaxReadBitCount();

    void setMaxReadBitCount(int maxReadBitCount);

    int getMaxReadRegisterCount();

    void setMaxReadRegisterCount(int maxReadRegisterCount);

    int getMaxWriteRegisterCount();

    void setMaxWriteRegisterCount(int maxWriteRegisterCount);
}
