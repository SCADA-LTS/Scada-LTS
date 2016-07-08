/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.vo.dataSource.modbus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.modbus.ModbusSerialDataSource;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.modbus4j.serial.SerialMaster;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class ModbusSerialDataSourceVO extends ModbusDataSourceVO<ModbusSerialDataSourceVO> implements JsonSerializable {
    public static final Type TYPE = Type.MODBUS_SERIAL;

    public enum EncodingType {
        RTU("dsEdit.modbusSerial.encoding.rtu"), //
        @Deprecated
        RTU_REVERSE_CRC("dsEdit.modbusSerial.encoding.rtuReverseCrc"), //
        ASCII("dsEdit.modbusSerial.encoding.ascii");

        private final String nameKey;

        EncodingType(String nameKey) {
            this.nameKey = nameKey;
        }

        public String getNameKey() {
            return nameKey;
        }
    }

    private static ExportCodes CONCURRENCY_CODES = new ExportCodes();
    static {
        CONCURRENCY_CODES.addElement(SerialMaster.SYNC_TRANSPORT, "SYNC_TRANSPORT",
                "dsEdit.modbusSerial.concurrency.transport");
        CONCURRENCY_CODES.addElement(SerialMaster.SYNC_SLAVE, "SYNC_SLAVE", "dsEdit.modbusSerial.concurrency.slave");
        CONCURRENCY_CODES.addElement(SerialMaster.SYNC_FUNCTION, "SYNC_FUNCTION",
                "dsEdit.modbusSerial.concurrency.function");
    };

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("common.default", commPortId);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new ModbusSerialDataSource(this);
    }

    @JsonRemoteProperty
    private String commPortId;
    @JsonRemoteProperty
    private int baudRate = 9600;
    @JsonRemoteProperty
    private int flowControlIn = 0;
    @JsonRemoteProperty
    private int flowControlOut = 0;
    @JsonRemoteProperty
    private int dataBits = 8;
    @JsonRemoteProperty
    private int stopBits = 1;
    @JsonRemoteProperty
    private int parity = 0;
    @JsonRemoteProperty
    private EncodingType encoding;
    @JsonRemoteProperty
    private boolean echo = false;
    private int concurrency = SerialMaster.SYNC_FUNCTION;

    public String getEncodingStr() {
        if (encoding == null)
            return null;
        return encoding.toString();
    }

    public void setEncodingStr(String encoding) {
        if (encoding != null)
            this.encoding = EncodingType.valueOf(encoding);
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public String getCommPortId() {
        return commPortId;
    }

    public void setCommPortId(String commPortId) {
        this.commPortId = commPortId;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public boolean isEcho() {
        return echo;
    }

    public void setEcho(boolean echo) {
        this.echo = echo;
    }

    public int getFlowControlIn() {
        return flowControlIn;
    }

    public void setFlowControlIn(int flowControlIn) {
        this.flowControlIn = flowControlIn;
    }

    public int getFlowControlOut() {
        return flowControlOut;
    }

    public void setFlowControlOut(int flowControlOut) {
        this.flowControlOut = flowControlOut;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public EncodingType getEncoding() {
        return encoding;
    }

    public void setEncoding(EncodingType encoding) {
        this.encoding = encoding;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);
        if (StringUtils.isEmpty(commPortId))
            response.addContextualMessage("commPortId", "validate.required");
        if (baudRate <= 0)
            response.addContextualMessage("baudRate", "validate.invalidValue");
        if (!(flowControlIn == 0 || flowControlIn == 1 || flowControlIn == 4))
            response.addContextualMessage("flowControlIn", "validate.invalidValue");
        if (!(flowControlOut == 0 || flowControlOut == 2 || flowControlOut == 8))
            response.addContextualMessage("flowControlOut", "validate.invalidValue");
        if (dataBits < 5 || dataBits > 8)
            response.addContextualMessage("dataBits", "validate.invalidValue");
        if (stopBits < 1 || stopBits > 3)
            response.addContextualMessage("stopBits", "validate.invalidValue");
        if (parity < 0 || parity > 4)
            response.addContextualMessage("parityBits", "validate.invalidValue");
        if (encoding == null)
            response.addContextualMessage("encodingBits", "validate.required");

        if (!CONCURRENCY_CODES.isValidId(concurrency))
            response.addContextualMessage("concurrency", "validate.invalidValue");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        super.addPropertiesImpl(list);
        AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.port", commPortId);
        AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.baud", baudRate);
        AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.flowControlIn", flowControlIn);
        AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.flowControlOut", flowControlOut);
        AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.dataBits", dataBits);
        AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.stopBits", stopBits);
        AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.parity", parity);
        AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.encoding", encoding);
        AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.echo", echo);
        AuditEventType.addExportCodeMessage(list, "dsEdit.modbusSerial.concurrency", CONCURRENCY_CODES, concurrency);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, ModbusSerialDataSourceVO from) {
        super.addPropertyChangesImpl(list, from);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.port", from.commPortId, commPortId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.baud", from.baudRate, baudRate);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.flowControlIn", from.flowControlIn,
                flowControlIn);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.flowControlOut", from.flowControlOut,
                flowControlOut);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.dataBits", from.dataBits, dataBits);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.stopBits", from.stopBits, stopBits);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.parity", from.parity, parity);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.encoding", from.encoding, encoding);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.echo", from.echo, echo);
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.modbusSerial.concurrency", CONCURRENCY_CODES,
                from.concurrency, concurrency);
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 2;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        SerializationHelper.writeSafeUTF(out, commPortId);
        out.writeInt(baudRate);
        out.writeInt(flowControlIn);
        out.writeInt(flowControlOut);
        out.writeInt(dataBits);
        out.writeInt(stopBits);
        out.writeInt(parity);
        out.writeObject(encoding);
        out.writeBoolean(echo);
        out.writeInt(concurrency);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            commPortId = SerializationHelper.readSafeUTF(in);
            baudRate = in.readInt();
            flowControlIn = in.readInt();
            flowControlOut = in.readInt();
            dataBits = in.readInt();
            stopBits = in.readInt();
            parity = in.readInt();
            encoding = (EncodingType) in.readObject();
            echo = in.readBoolean();
            concurrency = SerialMaster.SYNC_FUNCTION;
        }
        else if (ver == 2) {
            commPortId = SerializationHelper.readSafeUTF(in);
            baudRate = in.readInt();
            flowControlIn = in.readInt();
            flowControlOut = in.readInt();
            dataBits = in.readInt();
            stopBits = in.readInt();
            parity = in.readInt();
            encoding = (EncodingType) in.readObject();
            echo = in.readBoolean();
            concurrency = in.readInt();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);

        // Concurrency
        String text = json.getString("concurrency");
        if (text == null)
            throw new LocalizableJsonException("emport.error.missing", "concurrency", CONCURRENCY_CODES.getCodeList());
        concurrency = CONCURRENCY_CODES.getId(text);
        if (!CONCURRENCY_CODES.isValidId(concurrency))
            throw new LocalizableJsonException("emport.error.invalid", "concurrency", text,
                    CONCURRENCY_CODES.getCodeList());
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        map.put("concurrency", CONCURRENCY_CODES.getCode(concurrency));
    }
}
