package com.serotonin.modbus4j;

import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.*;
import com.serotonin.modbus4j.sero.epoll.InputStreamEPollWrapper;
import com.serotonin.modbus4j.sero.log.BaseIOLog;
import com.serotonin.modbus4j.sero.messaging.MessageControl;
import com.serotonin.modbus4j.sero.messaging.MessagingExceptionHandler;
import com.serotonin.modbus4j.sero.util.ProgressiveTask;

import java.util.*;

public class SlaveIdLimit255ModbusMaster extends ModbusMaster implements IModbusMaster {
    private final ModbusMaster modbusMaster;

    public SlaveIdLimit255ModbusMaster(ModbusMaster modbusMaster) {
        this.modbusMaster = modbusMaster;
    }

    @Override
    public boolean isConnected() {
        return modbusMaster.isConnected();
    }

    @Override
    public void setConnected(boolean connected) {
        modbusMaster.setConnected(connected);
    }

    @Override
    public void init() throws ModbusInitException {
        modbusMaster.init();
    }

    @Override
    public boolean isInitialized() {
        return modbusMaster.isInitialized();
    }

    @Override
    public void destroy() {
        modbusMaster.destroy();
    }

    @Override
    public ModbusResponse sendImpl(ModbusRequest var1) throws ModbusTransportException {
        return modbusMaster.sendImpl(var1);
    }

    @Override
    public <T> T getValue(BaseLocator<T> locator) throws ModbusTransportException, ErrorResponseException {
        return modbusMaster.getValue(locator);
    }

    @Override
    public <T> void setValue(BaseLocator<T> locator, Object value) throws ModbusTransportException, ErrorResponseException {
        modbusMaster.setValue(locator, value);
    }

    @Override
    public List<Integer> scanForSlaveNodes() {
        List<Integer> result = new ArrayList();

        for(int i = 1; i <= 255; ++i) {
            if (modbusMaster.testSlaveNode(i)) {
                result.add(i);
            }
        }

        return result;
    }

    @Override
    public ProgressiveTask scanForSlaveNodes(NodeScanListener l) {
        l.progressUpdate(0.0F);
        ProgressiveTask task = new ProgressiveTask(l) {
            private int node = 1;

            protected void runImpl() {
                if (modbusMaster.testSlaveNode(this.node)) {
                    l.nodeFound(this.node);
                }

                this.declareProgress((float)this.node / 255.0F);
                ++this.node;
                if (this.node > 255) {
                    this.completed = true;
                }

            }
        };
        (new Thread(task)).start();
        return task;
    }

    @Override
    public boolean testSlaveNode(int node) {
        return modbusMaster.testSlaveNode(node);
    }

    @Override
    public int getRetries() {
        return modbusMaster.getRetries();
    }

    @Override
    public void setRetries(int retries) {
        modbusMaster.setRetries(retries);
    }

    @Override
    public int getTimeout() {
        return modbusMaster.getTimeout();
    }

    @Override
    public void setTimeout(int timeout) {
        modbusMaster.setTimeout(timeout);
    }

    @Override
    public boolean isMultipleWritesOnly() {
        return modbusMaster.isMultipleWritesOnly();
    }

    @Override
    public void setMultipleWritesOnly(boolean multipleWritesOnly) {
        modbusMaster.setMultipleWritesOnly(multipleWritesOnly);
    }

    @Override
    public int getDiscardDataDelay() {
        return modbusMaster.getDiscardDataDelay();
    }

    @Override
    public void setDiscardDataDelay(int discardDataDelay) {
        modbusMaster.setDiscardDataDelay(discardDataDelay);
    }

    @Override
    public BaseIOLog getIoLog() {
        return modbusMaster.getIoLog();
    }

    @Override
    public void setIoLog(BaseIOLog ioLog) {
        modbusMaster.setIoLog(ioLog);
    }

    @Override
    public InputStreamEPollWrapper getePoll() {
        return modbusMaster.getePoll();
    }

    @Override
    public void setePoll(InputStreamEPollWrapper ePoll) {
        modbusMaster.setePoll(ePoll);
    }

    @Override
    public <K> BatchResults<K> send(BatchRead<K> batch) throws ModbusTransportException, ErrorResponseException {
        return modbusMaster.send(batch);
    }

    @Override
    public MessageControl getMessageControl() {
        return modbusMaster.getMessageControl();
    }

    @Override
    public void closeMessageControl(MessageControl conn) {
        modbusMaster.closeMessageControl(conn);
    }

    @Override
    public int getMaxReadCount(int registerRange) {
        return modbusMaster.getMaxReadCount(registerRange);
    }

    @Override
    public void validateNumberOfBits(int bits) throws ModbusTransportException {
        modbusMaster.validateNumberOfBits(bits);
    }

    @Override
    public void validateNumberOfRegisters(int registers) throws ModbusTransportException {
        modbusMaster.validateNumberOfRegisters(registers);
    }

    @Override
    public void setExceptionHandler(MessagingExceptionHandler exceptionHandler) {
        modbusMaster.setExceptionHandler(exceptionHandler);
    }

    @Override
    public MessagingExceptionHandler getExceptionHandler() {
        return modbusMaster.getExceptionHandler();
    }

    @Override
    public int getMaxReadBitCount() {
        return modbusMaster.getMaxReadBitCount();
    }

    @Override
    public void setMaxReadBitCount(int maxReadBitCount) {
        modbusMaster.setMaxReadBitCount(maxReadBitCount);
    }

    @Override
    public int getMaxReadRegisterCount() {
        return modbusMaster.getMaxReadRegisterCount();
    }

    @Override
    public void setMaxReadRegisterCount(int maxReadRegisterCount) {
        modbusMaster.setMaxReadRegisterCount(maxReadRegisterCount);
    }

    @Override
    public int getMaxWriteRegisterCount() {
        return modbusMaster.getMaxWriteRegisterCount();
    }

    @Override
    public void setMaxWriteRegisterCount(int maxWriteRegisterCount) {
        modbusMaster.setMaxWriteRegisterCount(maxWriteRegisterCount);
    }
}
