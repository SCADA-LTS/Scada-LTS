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
package com.serotonin.mango.vo.dataSource.http;

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
import com.serotonin.mango.rt.dataSource.http.HttpRetrieverPointLocatorRT;
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
public class HttpRetrieverPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public boolean isSettable() {
        return false;
    }

    public PointLocatorRT createRuntime() {
        return new HttpRetrieverPointLocatorRT(this);
    }

    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("dsEdit.httpRetriever.dpconn", Functions.escapeLessThan(valueRegex));
    }

    @JsonRemoteProperty
    private String valueRegex;
    @JsonRemoteProperty
    private boolean ignoreIfMissing;
    @JsonRemoteProperty
    private String valueFormat;
    private int dataTypeId;
    @JsonRemoteProperty
    private String timeRegex;
    @JsonRemoteProperty
    private String timeFormat;

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
        AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.valueRegex", valueRegex);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.ignoreIfMissing", ignoreIfMissing);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.numberFormat", valueFormat);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.timeRegex", timeRegex);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.timeFormat", timeFormat);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        HttpRetrieverPointLocatorVO from = (HttpRetrieverPointLocatorVO) o;
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, dataTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.valueRegex", from.valueRegex,
                valueRegex);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.ignoreIfMissing",
                from.ignoreIfMissing, ignoreIfMissing);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.numberFormat", from.valueFormat,
                valueFormat);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.timeRegex", from.timeRegex, timeRegex);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.timeFormat", from.timeFormat,
                timeFormat);
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
        SerializationHelper.writeSafeUTF(out, valueRegex);
        out.writeBoolean(ignoreIfMissing);
        out.writeInt(dataTypeId);
        SerializationHelper.writeSafeUTF(out, valueFormat);
        SerializationHelper.writeSafeUTF(out, timeRegex);
        SerializationHelper.writeSafeUTF(out, timeFormat);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            valueRegex = SerializationHelper.readSafeUTF(in);
            ignoreIfMissing = in.readBoolean();
            dataTypeId = in.readInt();
            valueFormat = SerializationHelper.readSafeUTF(in);
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
