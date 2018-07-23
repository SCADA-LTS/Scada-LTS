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
package com.serotonin.mango.vo.dataSource.ebro;

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
import com.serotonin.mango.rt.dataSource.ebro.EBI25Constants;
import com.serotonin.mango.rt.dataSource.ebro.EBI25PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.mango.vo.dataSource.DataPointSaveHandler;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class EBI25PointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public static final int TYPE_VALUE = 1;
    public static final int TYPE_BATTERY = 2;
    public static final int TYPE_SIGNAL = 3;

    private static final ExportCodes TYPE_CODES = new ExportCodes();
    static {
        TYPE_CODES.addElement(TYPE_VALUE, "VALUE");
        TYPE_CODES.addElement(TYPE_BATTERY, "BATTERY");
        TYPE_CODES.addElement(TYPE_SIGNAL, "SIGNAL");
    }

    public PointLocatorRT createRuntime() {
        return new EBI25PointLocatorRT(this);
    }

    public int getDataTypeId() {
        return DataTypes.NUMERIC;
    }

    @Override
    public boolean isSettable() {
        return false;
    }

    public LocalizableMessage getConfigurationDescription() {
        switch (type) {
        case TYPE_VALUE:
            switch (unit) {
            case EBI25Constants.UNIT_TYPE_TEMPERATURE:
                return new LocalizableMessage("dsEdit.ebi25.unit.temperature");
            case EBI25Constants.UNIT_TYPE_PRESSURE:
                return new LocalizableMessage("dsEdit.ebi25.unit.pressure");
            case EBI25Constants.UNIT_TYPE_HUMIDITY:
                return new LocalizableMessage("dsEdit.ebi25.unit.humidity");
            case EBI25Constants.UNIT_TYPE_VOLTAGE:
                return new LocalizableMessage("dsEdit.ebi25.unit.voltage");
            case EBI25Constants.UNIT_TYPE_CURRENT:
                return new LocalizableMessage("dsEdit.ebi25.unit.current");
            case EBI25Constants.UNIT_TYPE_PH:
                return new LocalizableMessage("dsEdit.ebi25.unit.ph");
            case EBI25Constants.UNIT_TYPE_CONDUCTIVITY:
                return new LocalizableMessage("dsEdit.ebi25.unit.conductivity");
            }
        }

        return new LocalizableMessage("common.noMessage");
    }

    @JsonRemoteProperty
    private int index;
    private int type;
    private String serialNumber;
    private String productionDate;
    private String calibrationDate;
    private String hardwareVersion;
    private String firmwareVersion;
    private int unit;
    private int sampleRate;
    private double lowLimit;
    private double highLimit;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public String getCalibrationDate() {
        return calibrationDate;
    }

    public void setCalibrationDate(String calibrationDate) {
        this.calibrationDate = calibrationDate;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public double getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(double lowLimit) {
        this.lowLimit = lowLimit;
    }

    public double getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(double highLimit) {
        this.highLimit = highLimit;
    }

    public void validate(DwrResponseI18n response) {
        if (!TYPE_CODES.isValidId(type))
            response.addContextualMessage("type", "validate.invalidValue");
        if (type == TYPE_VALUE && !EBI25Constants.UNIT_CODES.isValidId(unit))
            response.addContextualMessage("unit", "validate.invalidValue");
    }

    @Override
    public DataPointSaveHandler getDataPointSaveHandler() {
        return new EBI25PointSaveHandler();
    }

    public double translateFromRawValue(int rawValue) {
        if (type == TYPE_VALUE) {
            switch (unit) {
            case EBI25Constants.UNIT_TYPE_TEMPERATURE:
                return ((double) rawValue) / 10;
            }
        }

        return rawValue;
    }

    public int translateToRawValue(double value) {
        if (type == TYPE_VALUE) {
            switch (unit) {
            case EBI25Constants.UNIT_TYPE_TEMPERATURE:
                return (int) value * 10;
            }
        }

        return (int) value;
    }

    public String getSuffix() {
        switch (unit) {
        case EBI25Constants.UNIT_TYPE_TEMPERATURE:
            return "&deg;C";
        case EBI25Constants.UNIT_TYPE_PRESSURE:
            return "kp";
        case EBI25Constants.UNIT_TYPE_HUMIDITY:
            return "%";
        case EBI25Constants.UNIT_TYPE_VOLTAGE:
            return "v";
        case EBI25Constants.UNIT_TYPE_CURRENT:
            return "a";
        }

        return "";
    }

    public LocalizableMessage getPrettyType() {
        switch (type) {
        case TYPE_VALUE:
            return new LocalizableMessage("dsEdit.ebi25.type.value");
        case TYPE_BATTERY:
            return new LocalizableMessage("dsEdit.ebi25.type.battery");
        case TYPE_SIGNAL:
            return new LocalizableMessage("dsEdit.ebi25.type.signal");
        }
        return new LocalizableMessage("common.unknown");
    }

    public LocalizableMessage getPrettyUnit() {
        switch (type) {
        case TYPE_VALUE:
            switch (unit) {
            case EBI25Constants.UNIT_TYPE_TEMPERATURE:
                return new LocalizableMessage("dsEdit.ebi25.unit.temperature");
            case EBI25Constants.UNIT_TYPE_PRESSURE:
                return new LocalizableMessage("dsEdit.ebi25.unit.pressure");
            case EBI25Constants.UNIT_TYPE_HUMIDITY:
                return new LocalizableMessage("dsEdit.ebi25.unit.humidity");
            case EBI25Constants.UNIT_TYPE_VOLTAGE:
                return new LocalizableMessage("dsEdit.ebi25.unit.voltage");
            case EBI25Constants.UNIT_TYPE_CURRENT:
                return new LocalizableMessage("dsEdit.ebi25.unit.current");
            case EBI25Constants.UNIT_TYPE_PH:
                return new LocalizableMessage("dsEdit.ebi25.unit.ph");
            case EBI25Constants.UNIT_TYPE_CONDUCTIVITY:
                return new LocalizableMessage("dsEdit.ebi25.unit.conductivity");
            default:
                return new LocalizableMessage("common.unknown");
            }
        }

        return new LocalizableMessage("common.noMessage");
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.ebi25.sampleRate", sampleRate);
        AuditEventType.addPropertyMessage(list, "dsEdit.ebi25.lowLimit", lowLimit);
        AuditEventType.addPropertyMessage(list, "dsEdit.ebi25.highLimit", highLimit);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        EBI25PointLocatorVO from = (EBI25PointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.ebi25.sampleRate", from.sampleRate, sampleRate);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.ebi25.lowLimit", from.lowLimit, lowLimit);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.ebi25.highLimit", from.highLimit, highLimit);
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
        out.writeInt(index);
        out.writeInt(type);
        SerializationHelper.writeSafeUTF(out, serialNumber);
        SerializationHelper.writeSafeUTF(out, productionDate);
        SerializationHelper.writeSafeUTF(out, calibrationDate);
        SerializationHelper.writeSafeUTF(out, hardwareVersion);
        SerializationHelper.writeSafeUTF(out, firmwareVersion);
        out.writeInt(unit);
        out.writeInt(sampleRate);
        out.writeDouble(lowLimit);
        out.writeDouble(highLimit);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            index = in.readInt();
            type = in.readInt();
            serialNumber = SerializationHelper.readSafeUTF(in);
            productionDate = SerializationHelper.readSafeUTF(in);
            calibrationDate = SerializationHelper.readSafeUTF(in);
            hardwareVersion = SerializationHelper.readSafeUTF(in);
            firmwareVersion = SerializationHelper.readSafeUTF(in);
            unit = in.readInt();
            sampleRate = in.readInt();
            lowLimit = in.readDouble();
            highLimit = in.readDouble();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String text = json.getString("type");
        if (text == null)
            throw new LocalizableJsonException("emport.error.missing", "type", TYPE_CODES.getCodeList());
        type = TYPE_CODES.getId(text);
        if (!TYPE_CODES.isValidId(type))
            throw new LocalizableJsonException("emport.error.invalid", "range", text, TYPE_CODES.getCodeList());

        if (type == TYPE_VALUE) {
            text = json.getString("unit");
            if (text == null)
                throw new LocalizableJsonException("emport.error.missing", "unit", EBI25Constants.UNIT_CODES
                        .getCodeList());

            unit = EBI25Constants.UNIT_CODES.getId(text);
            if (!EBI25Constants.UNIT_CODES.isValidId(unit))
                throw new LocalizableJsonException("emport.error.invalid", "unit", text, EBI25Constants.UNIT_CODES
                        .getCodeList());
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("type", TYPE_CODES.getCode(type));
        if (type == TYPE_VALUE)
            map.put("unit", EBI25Constants.UNIT_CODES.getCode(unit));
    }
}
