package com.serotonin.modbus4j;

import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.modbus4j.base.KeyedModbusLocator;
import com.serotonin.modbus4j.base.ReadFunctionGroup;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.*;
import com.serotonin.modbus4j.sero.epoll.InputStreamEPollWrapper;
import com.serotonin.modbus4j.sero.log.BaseIOLog;
import com.serotonin.modbus4j.sero.messaging.MessageControl;
import com.serotonin.modbus4j.sero.messaging.MessagingExceptionHandler;
import com.serotonin.modbus4j.sero.util.ArrayUtils;
import com.serotonin.modbus4j.sero.util.ProgressiveTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class FixedModbusMaster extends ModbusMaster implements IModbusMaster {

    private final Log LOG = LogFactory.getLog(FixedModbusMaster.class);
    private final ModbusMaster modbusMaster;

    public FixedModbusMaster(ModbusMaster modbusMaster) {
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
        List<Integer> result = new ArrayList<>();

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

            @Override
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
        if (!modbusMaster.isInitialized()) {
            throw new ModbusTransportException("not initialized");
        } else {
            BatchResults<K> results = new BatchResults<>();
            List<ReadFunctionGroup<K>> functionGroups = batch.getReadFunctionGroups(modbusMaster);
            Iterator<ReadFunctionGroup<K>> functionGroupsIterator = functionGroups.iterator();

            while(functionGroupsIterator.hasNext()) {
                ReadFunctionGroup<K> functionGroup = functionGroupsIterator.next();
                try {
                    this.sendFunctionGroupFixed(functionGroup, results, batch.isErrorsInResults(), batch.isExceptionsInResults());
                } catch (Throwable throwable) {
                    LOG.error(LoggingUtils.exceptionInfo(throwable), throwable);
                    if (!batch.isExceptionsInResults()) {
                        throw throwable;
                    } else {
                        Iterator<KeyedModbusLocator<K>> locators = functionGroup.getLocators().iterator();
                        KeyedModbusLocator<K> locator;

                        while(locators.hasNext()) {
                            locator = locators.next();
                            results.addResult(locator.getKey(), throwable);
                        }
                        return results;
                    }
                }
                if (batch.isCancel()) {
                    break;
                }
            }

            return results;
        }
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

    private <K> void sendFunctionGroupFixed(ReadFunctionGroup<K> functionGroup, BatchResults<K> results, boolean errorsInResults, boolean exceptionsInResults) throws ModbusTransportException, ErrorResponseException {
        int slaveId = functionGroup.getSlaveAndRange().getSlaveId();
        int startOffset = functionGroup.getStartOffset();
        int length = functionGroup.getLength();
        Object request;
        if (functionGroup.getFunctionCode() == 1) {
            request = new ReadCoilsRequest(slaveId, startOffset, length);
        } else if (functionGroup.getFunctionCode() == 2) {
            request = new ReadDiscreteInputsRequest(slaveId, startOffset, length);
        } else if (functionGroup.getFunctionCode() == 3) {
            request = new ReadHoldingRegistersRequest(slaveId, startOffset, length);
        } else {
            if (functionGroup.getFunctionCode() != 4) {
                RuntimeException unsupportedFunction = new RuntimeException("Unsupported function");
                if (!exceptionsInResults) {
                    throw unsupportedFunction;
                } else {
                    Iterator<KeyedModbusLocator<K>> locators = functionGroup.getLocators().iterator();
                    KeyedModbusLocator<K> locator;

                    while(locators.hasNext()) {
                        locator = locators.next();
                        results.addResult(locator.getKey(), unsupportedFunction);
                    }

                    return;
                }
            }

            request = new ReadInputRegistersRequest(slaveId, startOffset, length);
        }

        ReadResponse response;
        Iterator<KeyedModbusLocator<K>> locators;
        KeyedModbusLocator<K> locator;
        try {
            response = (ReadResponse)this.send((ModbusRequest)request);
        } catch (ModbusTransportException modbusTransportException) {
            if (!exceptionsInResults) {
                throw modbusTransportException;
            }

            locators = functionGroup.getLocators().iterator();

            while(locators.hasNext()) {
                locator = locators.next();
                results.addResult(locator.getKey(), modbusTransportException);
            }

            return;
        }

        byte[] data = null;
        if (!errorsInResults && response.isException()) {
            throw new ErrorResponseException((ModbusRequest)request, response);
        } else {
            if (response != null && !response.isException()) {
                data = response.getData();
            }

            locators = functionGroup.getLocators().iterator();

            while(true) {
                while(locators.hasNext()) {
                    locator = locators.next();
                    if(response == null) {
                        RuntimeException exception = new RuntimeException("Response is null for: " + locator.getKey());
                        if(!exceptionsInResults) {
                            throw exception;
                        } else {
                            results.addResult(locator.getKey(), exception);
                        }
                    } else if(data == null) {
                        RuntimeException exception = new RuntimeException("Data is null for: " + locator.getKey());
                        if(!exceptionsInResults) {
                            throw exception;
                        } else {
                            results.addResult(locator.getKey(), exception);
                        }
                    } else if (errorsInResults && response.isException()) {
                        results.addResult(locator.getKey(), new ExceptionResult(response.getExceptionCode()));
                    } else {
                        try {
                            results.addResult(locator.getKey(), locator.bytesToValue(data, startOffset));
                        } catch (RuntimeException convertException) {
                            String error = "Result conversion exception. data=" + ArrayUtils.toHexString(data) + ", startOffset=" + startOffset + ", locator=" + locator + ", functionGroup.functionCode=" + functionGroup.getFunctionCode() + ", functionGroup.startOffset=" + startOffset + ", functionGroup.length=" + length;
                            RuntimeException convertExceptionWithMessage = new RuntimeException(error, convertException);
                            if(!exceptionsInResults) {
                                throw convertExceptionWithMessage;
                            } else {
                                results.addResult(locator.getKey(), convertExceptionWithMessage);
                            }
                        }
                    }
                }

                return;
            }
        }
    }
}
