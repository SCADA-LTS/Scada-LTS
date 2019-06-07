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
package com.serotonin.mango.vo.dataSource.snmp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.snmp4j.smi.OID;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.snmp.SnmpPointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 * 
 */
@JsonRemoteEntity
public class SnmpPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public interface SetTypes {
        int NONE = 0;
        int INTEGER_32 = 1;
        int OCTET_STRING = 2;
        int OID = 3;
        int IP_ADDRESS = 4;
        int COUNTER_32 = 5;
        int GAUGE_32 = 6;
        int TIME_TICKS = 7;
        int OPAQUE = 8;
        int COUNTER_64 = 9;
    }

    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", oid);
    }

    public boolean isSettable() {
        return setType != SetTypes.NONE;
    }

    public PointLocatorRT createRuntime() {
        return new SnmpPointLocatorRT(this);
    }

    @JsonRemoteProperty
    private String oid;
    private int dataTypeId;
    @JsonRemoteProperty
    private String binary0Value = "0";
    @JsonRemoteProperty
    private int setType;
    @JsonRemoteProperty
    private boolean trapOnly;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public String getBinary0Value() {
        return binary0Value;
    }

    public void setBinary0Value(String binary0Value) {
        this.binary0Value = binary0Value;
    }

    public int getSetType() {
        return setType;
    }

    public void setSetType(int setType) {
        this.setType = setType;
    }

    public boolean isTrapOnly() {
        return trapOnly;
    }

    public void setTrapOnly(boolean trapOnly) {
        this.trapOnly = trapOnly;
    }

    public void validate(DwrResponseI18n response) {
        if (StringUtils.isEmpty(oid))
            response.addContextualMessage("oid", "validate.required");
        else {
            oid = oid.trim();
            try {
                new OID(oid);
            }
            catch (RuntimeException e) {
                response.addContextualMessage("oid", "validate.parseError", e.getMessage());
            }
        }

        if (!DataTypes.CODES.isValidId(dataTypeId))
            response.addContextualMessage("dataTypeId", "validate.invalidValue");
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.oid", oid);
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", dataTypeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.binary0Value", binary0Value);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.setType", setType);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.polling", trapOnly);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        SnmpPointLocatorVO from = (SnmpPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.oid", from.oid, oid);
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, dataTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.binary0Value", from.binary0Value, binary0Value);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.setType", from.setType, setType);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.polling", from.trapOnly, trapOnly);
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 3;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        SerializationHelper.writeSafeUTF(out, oid);
        out.writeInt(dataTypeId);
        SerializationHelper.writeSafeUTF(out, binary0Value);
        out.writeInt(setType);
        out.writeBoolean(trapOnly);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            oid = SerializationHelper.readSafeUTF(in);
            dataTypeId = in.readInt();
            binary0Value = "0";
            setType = in.readInt();
            trapOnly = false;
        }
        else if (ver == 2) {
            oid = SerializationHelper.readSafeUTF(in);
            dataTypeId = in.readInt();
            binary0Value = "0";
            setType = in.readInt();
            trapOnly = in.readBoolean();
        }
        else if (ver == 3) {
            oid = SerializationHelper.readSafeUTF(in);
            dataTypeId = in.readInt();
            binary0Value = SerializationHelper.readSafeUTF(in);
            setType = in.readInt();
            trapOnly = in.readBoolean();
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
