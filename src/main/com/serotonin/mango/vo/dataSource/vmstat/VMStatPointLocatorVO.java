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
package com.serotonin.mango.vo.dataSource.vmstat;

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
import com.serotonin.mango.rt.dataSource.vmstat.VMStatPointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class VMStatPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public interface Attributes {
        int PROCS_R = 1;
        int PROCS_B = 2;
        int MEMORY_SWPD = 3;
        int MEMORY_FREE = 4;
        int MEMORY_BUFF = 5;
        int MEMORY_CACHE = 6;
        int SWAP_SI = 7;
        int SWAP_SO = 8;
        int IO_BI = 9;
        int IO_BO = 10;
        int SYSTEM_IN = 11;
        int SYSTEM_CS = 12;
        int CPU_US = 13;
        int CPU_SY = 14;
        int CPU_ID = 15;
        int CPU_WA = 16;
        int CPU_ST = 17;
    }

    public static ExportCodes ATTRIBUTE_CODES = new ExportCodes();
    static {
        ATTRIBUTE_CODES.addElement(Attributes.PROCS_R, "PROCS_R", "dsEdit.vmstat.attr.procsR");
        ATTRIBUTE_CODES.addElement(Attributes.PROCS_B, "PROCS_B", "dsEdit.vmstat.attr.procsB");
        ATTRIBUTE_CODES.addElement(Attributes.MEMORY_SWPD, "MEMORY_SWPD", "dsEdit.vmstat.attr.memorySwpd");
        ATTRIBUTE_CODES.addElement(Attributes.MEMORY_FREE, "MEMORY_FREE", "dsEdit.vmstat.attr.memoryFree");
        ATTRIBUTE_CODES.addElement(Attributes.MEMORY_BUFF, "MEMORY_BUFF", "dsEdit.vmstat.attr.memoryBuff");
        ATTRIBUTE_CODES.addElement(Attributes.MEMORY_CACHE, "MEMORY_CACHE", "dsEdit.vmstat.attr.memoryCache");
        ATTRIBUTE_CODES.addElement(Attributes.SWAP_SI, "SWAP_SI", "dsEdit.vmstat.attr.swapSi");
        ATTRIBUTE_CODES.addElement(Attributes.SWAP_SO, "SWAP_SO", "dsEdit.vmstat.attr.swapSo");
        ATTRIBUTE_CODES.addElement(Attributes.IO_BI, "IO_BI", "dsEdit.vmstat.attr.ioBi");
        ATTRIBUTE_CODES.addElement(Attributes.IO_BO, "IO_BO", "dsEdit.vmstat.attr.ioBo");
        ATTRIBUTE_CODES.addElement(Attributes.SYSTEM_IN, "SYSTEM_IN", "dsEdit.vmstat.attr.systemIn");
        ATTRIBUTE_CODES.addElement(Attributes.SYSTEM_CS, "SYSTEM_CS", "dsEdit.vmstat.attr.systemCs");
        ATTRIBUTE_CODES.addElement(Attributes.CPU_US, "CPU_US", "dsEdit.vmstat.attr.cpuUs");
        ATTRIBUTE_CODES.addElement(Attributes.CPU_SY, "CPU_SY", "dsEdit.vmstat.attr.cpuSy");
        ATTRIBUTE_CODES.addElement(Attributes.CPU_ID, "CPU_ID", "dsEdit.vmstat.attr.cpuId");
        ATTRIBUTE_CODES.addElement(Attributes.CPU_WA, "CPU_WA", "dsEdit.vmstat.attr.cpuWa");
        ATTRIBUTE_CODES.addElement(Attributes.CPU_ST, "CPU_ST", "dsEdit.vmstat.attr.cpuSt");
    };

    @JsonRemoteProperty
    private int attributeId = Attributes.CPU_ID;

    public boolean isSettable() {
        return false;
    }

    public PointLocatorRT createRuntime() {
        return new VMStatPointLocatorRT(this);
    }

    public LocalizableMessage getConfigurationDescription() {
        if (ATTRIBUTE_CODES.isValidId(attributeId))
            return new LocalizableMessage(ATTRIBUTE_CODES.getKey(attributeId));
        return new LocalizableMessage("common.unknown");
    }

    public int getDataTypeId() {
        return DataTypes.NUMERIC;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public void validate(DwrResponseI18n response) {
        if (!ATTRIBUTE_CODES.isValidId(attributeId))
            response.addContextualMessage("attributeId", "validate.invalidValue");
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addExportCodeMessage(list, "dsEdit.vmstat.attribute", ATTRIBUTE_CODES, attributeId);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        VMStatPointLocatorVO from = (VMStatPointLocatorVO) o;
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.vmstat.attribute", ATTRIBUTE_CODES,
                from.attributeId, attributeId);
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
        out.writeInt(attributeId);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1)
            attributeId = in.readInt();
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String text = json.getString("attributeId");
        if (text == null)
            throw new LocalizableJsonException("emport.error.missing", "attributeId", ATTRIBUTE_CODES.getCodeList());
        attributeId = ATTRIBUTE_CODES.getId(text);
        if (!ATTRIBUTE_CODES.isValidId(attributeId))
            throw new LocalizableJsonException("emport.error.invalid", "attributeId", text, ATTRIBUTE_CODES
                    .getCodeList());
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("attributeId", ATTRIBUTE_CODES.getCode(attributeId));
    }
}
