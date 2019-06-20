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
package com.serotonin.mango.vo.dataSource.onewire;

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
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.onewire.OneWirePointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class OneWirePointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public interface AttributeTypes {
        int TEMPURATURE = 1;
        int HUMIDITY = 2;
        int AD_VOLTAGE = 3;
        int LATCH_STATE = 4;
        int WIPER_POSITION = 5;
        int COUNTER = 6;
    }

    private static final ExportCodes ATTRIBUTE_CODES = new ExportCodes();
    static {
        ATTRIBUTE_CODES.addElement(AttributeTypes.TEMPURATURE, "TEMPURATURE", "dsEdit.1wire.attr.temperature");
        ATTRIBUTE_CODES.addElement(AttributeTypes.HUMIDITY, "HUMIDITY", "dsEdit.1wire.attr.humidity");
        ATTRIBUTE_CODES.addElement(AttributeTypes.AD_VOLTAGE, "AD_VOLTAGE", "dsEdit.1wire.attr.adVoltage");
        ATTRIBUTE_CODES.addElement(AttributeTypes.LATCH_STATE, "LATCH_STATE", "dsEdit.1wire.attr.latchState");
        ATTRIBUTE_CODES.addElement(AttributeTypes.WIPER_POSITION, "WIPER_POSITION", "dsEdit.1wire.attr.wiperPosition");
        ATTRIBUTE_CODES.addElement(AttributeTypes.COUNTER, "COUNTER", "dsEdit.1wire.attr.counter");
    }

    public static String getAttributeDescription(int attributeId) {
        if (attributeId == AttributeTypes.TEMPURATURE)
            return "Temperature";
        if (attributeId == AttributeTypes.HUMIDITY)
            return "Humidity";
        if (attributeId == AttributeTypes.AD_VOLTAGE)
            return "AD voltage";
        if (attributeId == AttributeTypes.LATCH_STATE)
            return "Latch state";
        if (attributeId == AttributeTypes.WIPER_POSITION)
            return "Wiper position";
        if (attributeId == AttributeTypes.COUNTER)
            return "Counter";
        return "Unknown";
    }

    public static int getAttributeDataType(int attributeId) {
        if (attributeId == AttributeTypes.LATCH_STATE)
            return DataTypes.BINARY;
        if (attributeId == AttributeTypes.WIPER_POSITION)
            return DataTypes.MULTISTATE;
        return DataTypes.NUMERIC;
    }

    /**
     * The address of the container.
     */
    @JsonRemoteProperty
    private String address;
    /**
     * The attribute within the container that we are interested in.
     */
    private int attributeId;
    /**
     * The index of the attribute within the container. This applies to ad voltages and latch states (channel), wiper
     * positions (potentiometer), and pages (counters).
     */
    @JsonRemoteProperty
    private int index;

    public String getAttributeDescription() {
        return getAttributeDescription(attributeId);
    }

    public int getDataTypeId() {
        return getAttributeDataType(attributeId);
    }

    public String getAttributeIndexDescription() {
        String s = getAttributeDescription();
        if (attributeId == AttributeTypes.AD_VOLTAGE || attributeId == AttributeTypes.LATCH_STATE
                || attributeId == AttributeTypes.WIPER_POSITION || attributeId == AttributeTypes.COUNTER)
            s += " " + index;
        return s;
    }

    public boolean isSettable() {
        return attributeId == AttributeTypes.LATCH_STATE || attributeId == AttributeTypes.WIPER_POSITION;
    }

    public PointLocatorRT createRuntime() {
        return new OneWirePointLocatorRT(this);
    }

    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("dsEdit.1wire.dpconn", address, getAttributeIndexDescription());
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void validate(DwrResponseI18n response) {
        if (StringUtils.isEmpty(address))
            response.addContextualMessage("address", "validate.required");
        if (!ATTRIBUTE_CODES.isValidId(attributeId))
            response.addContextualMessage("attributeId", "validate.invalidValue");
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.1wire.address", address);
        AuditEventType.addExportCodeMessage(list, "dsEdit.1wire.attribute", ATTRIBUTE_CODES, attributeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.1wire.index", index);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        OneWirePointLocatorVO from = (OneWirePointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.1wire.address", from.address, address);
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.1wire.attribute", ATTRIBUTE_CODES,
                from.attributeId, attributeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.1wire.index", from.index, index);
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        SerializationHelper.writeSafeUTF(out, address);
        out.writeInt(attributeId);
        out.writeInt(index);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            address = SerializationHelper.readSafeUTF(in);
            attributeId = in.readInt();
            index = in.readInt();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String text = json.getString("attributeType");
        if (text == null)
            throw new LocalizableJsonException("emport.error.missing", "attributeType", ATTRIBUTE_CODES.getCodeList());
        int id = ATTRIBUTE_CODES.getId(text);
        if (id == -1)
            throw new LocalizableJsonException("emport.error.invalid", "attributeType", text, ATTRIBUTE_CODES
                    .getCodeList());
        attributeId = id;
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("attributeType", ATTRIBUTE_CODES.getCode(attributeId));
    }
}
