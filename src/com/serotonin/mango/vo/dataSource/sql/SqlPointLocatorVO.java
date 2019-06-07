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
package com.serotonin.mango.vo.dataSource.sql;

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
import com.serotonin.mango.rt.dataSource.sql.SqlPointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class SqlPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", fieldName);
    }

    public boolean isSettable() {
        return !StringUtils.isEmpty(updateStatement);
    }

    public PointLocatorRT createRuntime() {
        return new SqlPointLocatorRT(this);
    }

    @JsonRemoteProperty
    private String fieldName;
    @JsonRemoteProperty
    private String timeOverrideName;
    private int dataTypeId;
    @JsonRemoteProperty
    private String updateStatement;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getTimeOverrideName() {
        return timeOverrideName;
    }

    public void setTimeOverrideName(String timeOverrideName) {
        this.timeOverrideName = timeOverrideName;
    }

    public String getUpdateStatement() {
        return updateStatement;
    }

    public void setUpdateStatement(String updateStatement) {
        this.updateStatement = updateStatement;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public void validate(DwrResponseI18n response) {
        if (!DataTypes.CODES.isValidId(dataTypeId))
            response.addContextualMessage("dataTypeId", "validate.invalidValue");
        if (StringUtils.isEmpty(fieldName) && StringUtils.isEmpty(updateStatement))
            response.addContextualMessage("fieldName", "validate.fieldName");
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", dataTypeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.sql.rowId", fieldName);
        AuditEventType.addPropertyMessage(list, "dsEdit.sql.timeColumn", timeOverrideName);
        AuditEventType.addPropertyMessage(list, "dsEdit.sql.update", updateStatement);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        SqlPointLocatorVO from = (SqlPointLocatorVO) o;
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, dataTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.rowId", from.fieldName, fieldName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.timeColumn", from.timeOverrideName,
                timeOverrideName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.update", from.updateStatement, updateStatement);
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
        SerializationHelper.writeSafeUTF(out, fieldName);
        SerializationHelper.writeSafeUTF(out, timeOverrideName);
        SerializationHelper.writeSafeUTF(out, updateStatement);
        out.writeInt(dataTypeId);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            fieldName = SerializationHelper.readSafeUTF(in);
            timeOverrideName = "";
            updateStatement = SerializationHelper.readSafeUTF(in);
            dataTypeId = in.readInt();
        }
        else if (ver == 2) {
            fieldName = SerializationHelper.readSafeUTF(in);
            timeOverrideName = SerializationHelper.readSafeUTF(in);
            updateStatement = SerializationHelper.readSafeUTF(in);
            dataTypeId = in.readInt();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        Integer value = deserializeDataType(json, DataTypes.IMAGE);
        if (value != null)
            dataTypeId = value;
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        serializeDataType(map);
    }
}
