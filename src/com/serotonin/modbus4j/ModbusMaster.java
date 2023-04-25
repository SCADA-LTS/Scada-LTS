package com.serotonin.modbus4j;

import com.serotonin.modbus4j.base.KeyedModbusLocator;
import com.serotonin.modbus4j.base.ReadFunctionGroup;
import com.serotonin.modbus4j.base.SlaveProfile;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.InvalidDataConversionException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.locator.BinaryLocator;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ModbusResponse;
import com.serotonin.modbus4j.msg.ReadCoilsRequest;
import com.serotonin.modbus4j.msg.ReadDiscreteInputsRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadInputRegistersRequest;
import com.serotonin.modbus4j.msg.ReadResponse;
import com.serotonin.modbus4j.msg.WriteCoilRequest;
import com.serotonin.modbus4j.msg.WriteCoilsRequest;
import com.serotonin.modbus4j.msg.WriteMaskRegisterRequest;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.sero.epoll.InputStreamEPollWrapper;
import com.serotonin.modbus4j.sero.log.BaseIOLog;
import com.serotonin.modbus4j.sero.messaging.MessageControl;
import com.serotonin.modbus4j.sero.util.ArrayUtils;
import com.serotonin.modbus4j.sero.util.ProgressiveTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ModbusMaster extends Modbus {
    private int timeout = 500;
    private int retries = 2;
    protected boolean connected = false;
    private boolean multipleWritesOnly;
    private int discardDataDelay = 0;
    private BaseIOLog ioLog;
    private InputStreamEPollWrapper ePoll;
    private final Map<Integer, SlaveProfile> slaveProfiles = new HashMap();
    protected boolean initialized;

    public ModbusMaster() {
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public abstract void init() throws ModbusInitException;

    public boolean isInitialized() {
        return this.initialized;
    }

    public abstract void destroy();

    public final ModbusResponse send(ModbusRequest request) throws ModbusTransportException {
        request.validate(this);
        return this.sendImpl(request);
    }

    public abstract ModbusResponse sendImpl(ModbusRequest var1) throws ModbusTransportException;

    public <T> T getValue(BaseLocator<T> locator) throws ModbusTransportException, ErrorResponseException {
        BatchRead<String> batch = new BatchRead();
        batch.addLocator("", locator);
        BatchResults<String> result = this.send(batch);
        return (T) result.getValue("");
    }

    public <T> void setValue(BaseLocator<T> locator, Object value) throws ModbusTransportException, ErrorResponseException {
        int slaveId = locator.getSlaveId();
        int registerRange = locator.getRange();
        int writeOffset = locator.getOffset();
        if (registerRange != 2 && registerRange != 4) {
            if (registerRange == 1) {
                if (!(value instanceof Boolean)) {
                    throw new InvalidDataConversionException("Only boolean values can be written to coils");
                }

                if (this.multipleWritesOnly) {
                    this.setValue(new WriteCoilsRequest(slaveId, writeOffset, new boolean[]{(Boolean)value}));
                } else {
                    this.setValue(new WriteCoilRequest(slaveId, writeOffset, (Boolean)value));
                }
            } else if (locator.getDataType() == 1) {
                if (!(value instanceof Boolean)) {
                    throw new InvalidDataConversionException("Only boolean values can be written to coils");
                }

                this.setHoldingRegisterBit(slaveId, writeOffset, ((BinaryLocator)locator).getBit(), (Boolean)value);
            } else {
                short[] data = locator.valueToShorts((T) value);
                if (data.length == 1 && !this.multipleWritesOnly) {
                    this.setValue(new WriteRegisterRequest(slaveId, writeOffset, data[0]));
                } else {
                    this.setValue(new WriteRegistersRequest(slaveId, writeOffset, data));
                }
            }

        } else {
            throw new RuntimeException("Cannot write to input status or input register ranges");
        }
    }

    public List<Integer> scanForSlaveNodes() {
        List<Integer> result = new ArrayList();

        for(int i = 1; i <= 255; ++i) {
            if (this.testSlaveNode(i)) {
                result.add(i);
            }
        }

        return result;
    }

    public ProgressiveTask scanForSlaveNodes(final NodeScanListener l) {
        l.progressUpdate(0.0F);
        ProgressiveTask task = new ProgressiveTask(l) {
            private int node = 1;

            protected void runImpl() {
                if (ModbusMaster.this.testSlaveNode(this.node)) {
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

    public boolean testSlaveNode(int node) {
        try {
            this.send((ModbusRequest)(new ReadHoldingRegistersRequest(node, 0, 1)));
            return true;
        } catch (ModbusTransportException var3) {
            return false;
        }
    }

    public int getRetries() {
        return this.retries;
    }

    public void setRetries(int retries) {
        if (retries < 0) {
            this.retries = 0;
        } else {
            this.retries = retries;
        }

    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        if (timeout < 1) {
            this.timeout = 1;
        } else {
            this.timeout = timeout;
        }

    }

    public boolean isMultipleWritesOnly() {
        return this.multipleWritesOnly;
    }

    public void setMultipleWritesOnly(boolean multipleWritesOnly) {
        this.multipleWritesOnly = multipleWritesOnly;
    }

    public int getDiscardDataDelay() {
        return this.discardDataDelay;
    }

    public void setDiscardDataDelay(int discardDataDelay) {
        if (discardDataDelay < 0) {
            this.discardDataDelay = 0;
        } else {
            this.discardDataDelay = discardDataDelay;
        }

    }

    public BaseIOLog getIoLog() {
        return this.ioLog;
    }

    public void setIoLog(BaseIOLog ioLog) {
        this.ioLog = ioLog;
    }

    public InputStreamEPollWrapper getePoll() {
        return this.ePoll;
    }

    public void setePoll(InputStreamEPollWrapper ePoll) {
        this.ePoll = ePoll;
    }

    public <K> BatchResults<K> send(BatchRead<K> batch) throws ModbusTransportException, ErrorResponseException {
        if (!this.initialized) {
            throw new ModbusTransportException("not initialized");
        } else {
            BatchResults<K> results = new BatchResults();
            List<ReadFunctionGroup<K>> functionGroups = batch.getReadFunctionGroups(this);
            Iterator var4 = functionGroups.iterator();

            while(var4.hasNext()) {
                ReadFunctionGroup<K> functionGroup = (ReadFunctionGroup)var4.next();
                this.sendFunctionGroup(functionGroup, results, batch.isErrorsInResults(), batch.isExceptionsInResults());
                if (batch.isCancel()) {
                    break;
                }
            }

            return results;
        }
    }

    protected MessageControl getMessageControl() {
        MessageControl conn = new MessageControl();
        conn.setRetries(this.getRetries());
        conn.setTimeout(this.getTimeout());
        conn.setDiscardDataDelay(this.getDiscardDataDelay());
        conn.setExceptionHandler(this.getExceptionHandler());
        conn.setIoLog(this.ioLog);
        return conn;
    }

    protected void closeMessageControl(MessageControl conn) {
        if (conn != null) {
            conn.close();
        }

    }

    private <K> void sendFunctionGroup(ReadFunctionGroup<K> functionGroup, BatchResults<K> results, boolean errorsInResults, boolean exceptionsInResults) throws ModbusTransportException, ErrorResponseException {
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
                throw new RuntimeException("Unsupported function");
            }

            request = new ReadInputRegistersRequest(slaveId, startOffset, length);
        }

        ReadResponse response;
        Iterator var11;
        KeyedModbusLocator locator;
        try {
            response = (ReadResponse)this.send((ModbusRequest)request);
        } catch (ModbusTransportException var15) {
            ModbusTransportException e = var15;
            if (!exceptionsInResults) {
                throw var15;
            }

            var11 = functionGroup.getLocators().iterator();

            while(var11.hasNext()) {
                locator = (KeyedModbusLocator)var11.next();
                results.addResult((K) locator.getKey(), e);
            }

            return;
        }

        byte[] data = null;
        if (!errorsInResults && response.isException()) {
            throw new ErrorResponseException((ModbusRequest)request, response);
        } else {
            if (!response.isException()) {
                data = response.getData();
            }

            var11 = functionGroup.getLocators().iterator();

            while(true) {
                while(var11.hasNext()) {
                    locator = (KeyedModbusLocator)var11.next();
                    if (errorsInResults && response.isException()) {
                        results.addResult((K) locator.getKey(), new ExceptionResult(response.getExceptionCode()));
                    } else {
                        try {
                            results.addResult((K) locator.getKey(), locator.bytesToValue(data, startOffset));
                        } catch (RuntimeException var14) {
                            throw new RuntimeException("Result conversion exception. data=" + ArrayUtils.toHexString(data) + ", startOffset=" + startOffset + ", locator=" + locator + ", functionGroup.functionCode=" + functionGroup.getFunctionCode() + ", functionGroup.startOffset=" + startOffset + ", functionGroup.length=" + length, var14);
                        }
                    }
                }

                return;
            }
        }
    }

    private void setValue(ModbusRequest request) throws ModbusTransportException, ErrorResponseException {
        ModbusResponse response = this.send(request);
        if (response != null) {
            if (response.isException()) {
                throw new ErrorResponseException(request, response);
            }
        }
    }

    private void setHoldingRegisterBit(int slaveId, int writeOffset, int bit, boolean value) throws ModbusTransportException, ErrorResponseException {
        SlaveProfile sp = this.getSlaveProfile(slaveId);
        if (sp.getWriteMaskRegister()) {
            WriteMaskRegisterRequest request = new WriteMaskRegisterRequest(slaveId, writeOffset);
            request.setBit(bit, value);
            ModbusResponse response = this.send((ModbusRequest)request);
            if (response == null) {
                return;
            }

            if (!response.isException()) {
                return;
            }

            if (response.getExceptionCode() != 1) {
                throw new ErrorResponseException(request, response);
            }

            sp.setWriteMaskRegister(false);
        }

        int regValue = (Integer)this.getValue(new NumericLocator(slaveId, 3, writeOffset, 2));
        if (value) {
            regValue |= 1 << bit;
        } else {
            regValue &= ~(1 << bit);
        }

        this.setValue(new WriteRegisterRequest(slaveId, writeOffset, regValue));
    }

    private SlaveProfile getSlaveProfile(int slaveId) {
        SlaveProfile sp = (SlaveProfile)this.slaveProfiles.get(slaveId);
        if (sp == null) {
            sp = new SlaveProfile();
            this.slaveProfiles.put(slaveId, sp);
        }

        return sp;
    }
}
