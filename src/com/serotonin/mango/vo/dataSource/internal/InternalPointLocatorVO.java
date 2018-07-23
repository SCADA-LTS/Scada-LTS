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
package com.serotonin.mango.vo.dataSource.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.internal.InternalPointLocatorRT;
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
public class InternalPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public interface Attributes {
        int BATCH_ENTRIES = 1;
        int BATCH_INSTANCES = 2;
        int MONITOR_HIGH = 3;
        int MONITOR_MEDIUM = 4;
        int MONITOR_SCHEDULED = 5;
        int MONITOR_STACK_HEIGHT = 6;
        int MONITOR_THREAD_COUNT = 7;
    }

    // Values in this array correspond to the attribute ids above.
    public static String[] MONITOR_NAMES = { "", //
            "BatchWriteBehind.ENTRIES_MONITOR", //
            "BatchWriteBehind.INSTANCES_MONITOR", //
            "WorkItemMonitor.highPriorityServiceQueueSize", //
            "WorkItemMonitor.mediumPriorityServiceQueueSize", //
            "WorkItemMonitor.scheduledTimerTaskCount", //
            "WorkItemMonitor.maxStackHeight", //
            "WorkItemMonitor.threadCount", //
    };

    public static ExportCodes ATTRIBUTE_CODES = new ExportCodes();
    static {
        ATTRIBUTE_CODES.addElement(Attributes.BATCH_ENTRIES, "BATCH_ENTRIES", "dsEdit.internal.attr.BATCH_ENTRIES");
        ATTRIBUTE_CODES.addElement(Attributes.BATCH_INSTANCES, "BATCH_INSTANCES",
                "dsEdit.internal.attr.BATCH_INSTANCES");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_HIGH, "MONITOR_HIGH", "dsEdit.internal.attr.MONITOR_HIGH");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_MEDIUM, "MONITOR_MEDIUM", "dsEdit.internal.attr.MONITOR_MEDIUM");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_SCHEDULED, "MONITOR_SCHEDULED",
                "dsEdit.internal.attr.MONITOR_SCHEDULED");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_STACK_HEIGHT, "MONITOR_STACK_HEIGHT",
                "dsEdit.internal.attr.MONITOR_STACK_HEIGHT");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_THREAD_COUNT, "MONITOR_THREAD_COUNT",
                "dsEdit.internal.attr.MONITOR_THREAD_COUNT");
    };

    private int attributeId = Attributes.BATCH_ENTRIES;

    public boolean isSettable() {
        return false;
    }

    public PointLocatorRT createRuntime() {
        return new InternalPointLocatorRT(this);
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
        AuditEventType.addExportCodeMessage(list, "dsEdit.internal.attribute", ATTRIBUTE_CODES, attributeId);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        InternalPointLocatorVO from = (InternalPointLocatorVO) o;
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.internal.attribute", ATTRIBUTE_CODES,
                from.attributeId, attributeId);
    }

    //
    //
    // Serialization
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
            throw new LocalizableJsonException("emport.error.invalid", "attributeId", text,
                    ATTRIBUTE_CODES.getCodeList());
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("attributeId", ATTRIBUTE_CODES.getCode(attributeId));
    }
}
