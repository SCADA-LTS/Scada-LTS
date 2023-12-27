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
package com.serotonin.mango.vo.dataSource.galil;

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
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.galil.InputPointTypeRT;
import com.serotonin.mango.rt.dataSource.galil.PointTypeRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class InputPointTypeVO extends PointTypeVO {
    private static final int[] EXCLUDE_DATA_TYPES = { DataTypes.ALPHANUMERIC, DataTypes.IMAGE, DataTypes.MULTISTATE };

    private int dataTypeId = DataTypes.BINARY;
    @JsonRemoteProperty
    private int inputId = 1;
    @JsonRemoteProperty
    private double scaleRawLow = 0;
    @JsonRemoteProperty
    private double scaleRawHigh = 1;
    @JsonRemoteProperty
    private double scaleEngLow = 0;
    @JsonRemoteProperty
    private double scaleEngHigh = 1;

    @Override
    public int typeId() {
        return Types.INPUT;
    }

    @Override
    public PointTypeRT createRuntime() {
        return new InputPointTypeRT(this);
    }

    @Override
    public int getDataTypeId() {
        return dataTypeId;
    }

    @Override
    public LocalizableMessage getDescription() {
        return new LocalizableMessage("dsEdit.galil.pointType.input");
    }

    @Override
    public boolean isSettable() {
        return false;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        if (!DataTypes.CODES.isValidId(dataTypeId, EXCLUDE_DATA_TYPES))
            response.addContextualMessage("dataTypeId", "validate.invalidValue");

        if (dataTypeId == DataTypes.BINARY) {
            if (inputId < 1 || inputId > 96)
                response.addContextualMessage("inputPointType.inputId", "validate.1to96");
        }
        else {
            if (inputId < 1 || inputId > 8)
                response.addContextualMessage("inputPointType.inputId", "validate.1to8");
            if (scaleRawHigh <= scaleRawLow)
                response.addContextualMessage("inputPointType.scaleRawHighId", "validate.greaterThanRawLow");
            if (scaleEngHigh <= scaleEngLow)
                response.addContextualMessage("inputPointType.scaleEngHighId", "validate.greaterThanEngLow");
        }
    }

    public int getInputId() {
        return inputId;
    }

    public void setInputId(int inputId) {
        this.inputId = inputId;
    }

    public double getScaleRawLow() {
        return scaleRawLow;
    }

    public void setScaleRawLow(double scaleRawLow) {
        this.scaleRawLow = scaleRawLow;
    }

    public double getScaleRawHigh() {
        return scaleRawHigh;
    }

    public void setScaleRawHigh(double scaleRawHigh) {
        this.scaleRawHigh = scaleRawHigh;
    }

    public double getScaleEngLow() {
        return scaleEngLow;
    }

    public void setScaleEngLow(double scaleEngLow) {
        this.scaleEngLow = scaleEngLow;
    }

    public double getScaleEngHigh() {
        return scaleEngHigh;
    }

    public void setScaleEngHigh(double scaleEngHigh) {
        this.scaleEngHigh = scaleEngHigh;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", dataTypeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.galil.inputNumber", inputId);
        AuditEventType.addPropertyMessage(list, "dsEdit.galil.scaleLow", scaleRawLow);
        AuditEventType.addPropertyMessage(list, "dsEdit.galil.scaleHigh", scaleRawHigh);
        AuditEventType.addPropertyMessage(list, "dsEdit.galil.engLow", scaleEngLow);
        AuditEventType.addPropertyMessage(list, "dsEdit.galil.engHigh", scaleEngHigh);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        InputPointTypeVO from = (InputPointTypeVO) o;
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, dataTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.inputNumber", from.inputId, inputId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.scaleLow", from.scaleRawLow, scaleRawLow);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.scaleHigh", from.scaleRawHigh, scaleRawHigh);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.engLow", from.scaleEngLow, scaleEngLow);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.engHigh", from.scaleEngHigh, scaleEngHigh);
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
        out.writeInt(dataTypeId);
        out.writeInt(inputId);
        out.writeDouble(scaleRawLow);
        out.writeDouble(scaleRawHigh);
        out.writeDouble(scaleEngLow);
        out.writeDouble(scaleEngHigh);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            dataTypeId = in.readInt();
            inputId = in.readInt();
            scaleRawLow = in.readDouble();
            scaleRawHigh = in.readDouble();
            scaleEngLow = in.readDouble();
            scaleEngHigh = in.readDouble();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);

        String text = json.getString("dataType");
        if (text != null) {
            dataTypeId = DataTypes.CODES.getId(text);
            if (!DataTypes.CODES.isValidId(dataTypeId, EXCLUDE_DATA_TYPES))
                throw new LocalizableJsonException("emport.error.invalid", "dataType", text, DataTypes.CODES
                        .getCodeList(EXCLUDE_DATA_TYPES));
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        map.put("dataType", DataTypes.CODES.getCode(dataTypeId));
    }
}
