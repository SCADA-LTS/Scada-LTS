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
package com.serotonin.mango.vo.dataSource.pop3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.pop3.Pop3PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import com.serotonin.web.taglib.Functions;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class Pop3PointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public boolean isSettable() {
        return false;
    }

    public PointLocatorRT createRuntime() {
        return new Pop3PointLocatorRT(this);
    }

    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("dsEdit.pop3.dpconn", Functions.escapeLessThan(valueRegex));
    }

    @JsonRemoteProperty
    private boolean findInSubject;
    @JsonRemoteProperty
    private String valueRegex;
    @JsonRemoteProperty
    private boolean ignoreIfMissing;
    @JsonRemoteProperty
    private String valueFormat;
    private int dataTypeId;
    @JsonRemoteProperty
    private boolean useReceivedTime;
    @JsonRemoteProperty
    private String timeRegex;
    @JsonRemoteProperty
    private String timeFormat;

    public boolean isFindInSubject() {
        return findInSubject;
    }

    public void setFindInSubject(boolean findInSubject) {
        this.findInSubject = findInSubject;
    }

    public String getValueRegex() {
        return valueRegex;
    }

    public void setValueRegex(String valueRegex) {
        this.valueRegex = valueRegex;
    }

    public boolean isIgnoreIfMissing() {
        return ignoreIfMissing;
    }

    public void setIgnoreIfMissing(boolean ignoreIfMissing) {
        this.ignoreIfMissing = ignoreIfMissing;
    }

    public String getValueFormat() {
        return valueFormat;
    }

    public void setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public boolean isUseReceivedTime() {
        return useReceivedTime;
    }

    public void setUseReceivedTime(boolean useReceivedTime) {
        this.useReceivedTime = useReceivedTime;
    }

    public String getTimeRegex() {
        return timeRegex;
    }

    public void setTimeRegex(String timeRegex) {
        this.timeRegex = timeRegex;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public void validate(DwrResponseI18n response) {
        if (StringUtils.isEmpty(valueRegex))
            response.addContextualMessage("valueRegex", "validate.required");
        else {
            try {
                Pattern pattern = Pattern.compile(valueRegex);
                if (pattern.matcher("").groupCount() < 1)
                    response.addContextualMessage("valueRegex", "validate.captureGroup");
            }
            catch (PatternSyntaxException e) {
                response.addContextualMessage("valueRegex", "common.default", e.getMessage());
            }
        }

        if (dataTypeId == DataTypes.NUMERIC && !StringUtils.isEmpty(valueFormat)) {
            try {
                new DecimalFormat(valueFormat);
            }
            catch (IllegalArgumentException e) {
                response.addContextualMessage("valueFormat", "common.default", e.getMessage());
            }
        }

        if (!DataTypes.CODES.isValidId(dataTypeId))
            response.addContextualMessage("dataTypeId", "validate.invalidValue");

        if (!StringUtils.isEmpty(timeRegex)) {
            try {
                Pattern pattern = Pattern.compile(timeRegex);
                if (pattern.matcher("").groupCount() < 1)
                    response.addContextualMessage("timeRegex", "validate.captureGroup");
            }
            catch (PatternSyntaxException e) {
                response.addContextualMessage("timeRegex", "common.default", e.getMessage());
            }

            if (StringUtils.isEmpty(timeFormat))
                response.addContextualMessage("timeFormat", "validate.required");
            else {
                try {
                    new SimpleDateFormat(timeFormat);
                }
                catch (IllegalArgumentException e) {
                    response.addContextualMessage("timeFormat", "common.default", e.getMessage());
                }
            }
        }
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", dataTypeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.findInSubject", findInSubject);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.valueRegex", valueRegex);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.ignoreIfMissing", ignoreIfMissing);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.numberFormat", valueFormat);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.useMessageTime", useReceivedTime);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.timeRegex", timeRegex);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.timeFormat", timeFormat);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        Pop3PointLocatorVO from = (Pop3PointLocatorVO) o;
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, dataTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.findInSubject", from.findInSubject,
                findInSubject);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.valueRegex", from.valueRegex, valueRegex);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.ignoreIfMissing", from.ignoreIfMissing,
                ignoreIfMissing);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.numberFormat", from.valueFormat, valueFormat);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.useMessageTime", from.useReceivedTime,
                useReceivedTime);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.timeRegex", from.timeRegex, timeRegex);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.timeFormat", from.timeFormat, timeFormat);
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
        out.writeBoolean(findInSubject);
        SerializationHelper.writeSafeUTF(out, valueRegex);
        out.writeBoolean(ignoreIfMissing);
        out.writeInt(dataTypeId);
        SerializationHelper.writeSafeUTF(out, valueFormat);
        out.writeBoolean(useReceivedTime);
        SerializationHelper.writeSafeUTF(out, timeRegex);
        SerializationHelper.writeSafeUTF(out, timeFormat);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            findInSubject = false;
            valueRegex = SerializationHelper.readSafeUTF(in);
            ignoreIfMissing = in.readBoolean();
            dataTypeId = in.readInt();
            valueFormat = SerializationHelper.readSafeUTF(in);
            useReceivedTime = in.readBoolean();
            timeRegex = SerializationHelper.readSafeUTF(in);
            timeFormat = SerializationHelper.readSafeUTF(in);
        }
        else if (ver == 2) {
            findInSubject = in.readBoolean();
            valueRegex = SerializationHelper.readSafeUTF(in);
            ignoreIfMissing = in.readBoolean();
            dataTypeId = in.readInt();
            valueFormat = SerializationHelper.readSafeUTF(in);
            useReceivedTime = in.readBoolean();
            timeRegex = SerializationHelper.readSafeUTF(in);
            timeFormat = SerializationHelper.readSafeUTF(in);
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
