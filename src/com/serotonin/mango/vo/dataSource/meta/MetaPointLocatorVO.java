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
package com.serotonin.mango.vo.dataSource.meta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.serotonin.db.IntValuePair;
import com.serotonin.json.JsonArray;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.json.JsonValue;
import com.serotonin.mango.Common;
import com.serotonin.mango.Common.TimePeriods;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.meta.MetaPointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class MetaPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public static final int UPDATE_EVENT_CONTEXT_UPDATE = 0;
    public static final int UPDATE_EVENT_CRON = 100;

    public static ExportCodes UPDATE_EVENT_CODES = new ExportCodes();
    static {
        UPDATE_EVENT_CODES.addElement(UPDATE_EVENT_CONTEXT_UPDATE, "CONTEXT_UPDATE", "dsEdit.meta.event.context");
        UPDATE_EVENT_CODES.addElement(TimePeriods.MINUTES, "MINUTES", "dsEdit.meta.event.minute");
        UPDATE_EVENT_CODES.addElement(TimePeriods.HOURS, "HOURS", "dsEdit.meta.event.hour");
        UPDATE_EVENT_CODES.addElement(TimePeriods.DAYS, "DAYS", "dsEdit.meta.event.day");
        UPDATE_EVENT_CODES.addElement(TimePeriods.WEEKS, "WEEKS", "dsEdit.meta.event.week");
        UPDATE_EVENT_CODES.addElement(TimePeriods.MONTHS, "MONTHS", "dsEdit.meta.event.month");
        UPDATE_EVENT_CODES.addElement(TimePeriods.YEARS, "YEARS", "dsEdit.meta.event.year");
        UPDATE_EVENT_CODES.addElement(UPDATE_EVENT_CRON, "CRON", "dsEdit.meta.event.cron");
    }

    private List<IntValuePair> context = new ArrayList<IntValuePair>();
    @JsonRemoteProperty
    private String script;
    private int dataTypeId;
    @JsonRemoteProperty
    private boolean settable;
    private int updateEvent = UPDATE_EVENT_CONTEXT_UPDATE;
    @JsonRemoteProperty
    private String updateCronPattern;
    @JsonRemoteProperty
    private int executionDelaySeconds;

    public PointLocatorRT createRuntime() {
        return new MetaPointLocatorRT(this);
    }

    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", "'" + StringUtils.truncate(script, 40) + "'");
    }

    public List<IntValuePair> getContext() {
        return context;
    }

    public void setContext(List<IntValuePair> context) {
        this.context = context;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public int getExecutionDelaySeconds() {
        return executionDelaySeconds;
    }

    public void setExecutionDelaySeconds(int executionDelaySeconds) {
        this.executionDelaySeconds = executionDelaySeconds;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public boolean isSettable() {
        return settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public int getUpdateEvent() {
        return updateEvent;
    }

    public void setUpdateEvent(int updateEvent) {
        this.updateEvent = updateEvent;
    }

    public String getUpdateCronPattern() {
        return updateCronPattern;
    }

    public void setUpdateCronPattern(String updateCronPattern) {
        this.updateCronPattern = updateCronPattern;
    }

    public void validate(DwrResponseI18n response) {
        if (StringUtils.isEmpty(script))
            response.addContextualMessage("script", "validate.required");

        List<String> varNameSpace = new ArrayList<String>();
        for (IntValuePair point : context) {
            String varName = point.getValue();
            if (StringUtils.isEmpty(varName)) {
                response.addContextualMessage("context", "validate.allVarNames");
                break;
            }

            if (!validateVarName(varName)) {
                response.addContextualMessage("context", "validate.invalidVarName", varName);
                break;
            }

            if (varNameSpace.contains(varName)) {
                response.addContextualMessage("context", "validate.duplicateVarName", varName);
                break;
            }

            varNameSpace.add(varName);
        }

        if (!DataTypes.CODES.isValidId(dataTypeId))
            response.addContextualMessage("dataTypeId", "validate.invalidValue");

        if (updateEvent == UPDATE_EVENT_CRON) {
            try {
                new CronTimerTrigger(updateCronPattern);
            }
            catch (Exception e) {
                response.addContextualMessage("updateCronPattern", "validate.invalidCron", updateCronPattern);
            }
        }
        else if (updateEvent != UPDATE_EVENT_CONTEXT_UPDATE && !Common.TIME_PERIOD_CODES.isValidId(updateEvent))
            response.addContextualMessage("updateEvent", "validate.invalidValue");

        if (executionDelaySeconds < 0)
            response.addContextualMessage("executionDelaySeconds", "validate.cannotBeNegative");
    }

    private boolean validateVarName(String varName) {
        char ch = varName.charAt(0);
        if (!Character.isLetter(ch) && ch != '_')
            return false;
        for (int i = 1; i < varName.length(); i++) {
            ch = varName.charAt(i);
            if (!Character.isLetterOrDigit(ch) && ch != '_')
                return false;
        }
        return true;
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", dataTypeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
        AuditEventType.addPropertyMessage(list, "dsEdit.meta.scriptContext", contextToString());
        AuditEventType.addPropertyMessage(list, "dsEdit.meta.script", script);
        AuditEventType.addExportCodeMessage(list, "dsEdit.meta.event", UPDATE_EVENT_CODES, updateEvent);
        if (updateEvent == UPDATE_EVENT_CRON)
            AuditEventType.addPropertyMessage(list, "dsEdit.meta.event.cron", updateCronPattern);
        AuditEventType.addPropertyMessage(list, "dsEdit.meta.delay", executionDelaySeconds);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        MetaPointLocatorVO from = (MetaPointLocatorVO) o;
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, dataTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, settable);
        if (!context.equals(context))
            AuditEventType.addPropertyChangeMessage(list, "dsEdit.meta.scriptContext", from.contextToString(),
                    contextToString());
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.meta.script", from.script, script);
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.meta.event", UPDATE_EVENT_CODES, from.updateEvent,
                updateEvent);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.meta.event.cron", from.updateCronPattern,
                updateCronPattern);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.meta.delay", from.executionDelaySeconds,
                executionDelaySeconds);
    }

    private String contextToString() {
        DataPointDao dataPointDao = new DataPointDao();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (IntValuePair ivp : context) {
            DataPointVO dp = dataPointDao.getDataPoint(ivp.getKey());
            if (first)
                first = false;
            else
                sb.append(", ");

            if (dp == null)
                sb.append("?=");
            else
                sb.append(dp.getName()).append("=");
            sb.append(ivp.getValue());
        }
        return sb.toString();
    }

    //
    //
    // Serialization
    //
    private static final long serialVersionUID = -1;
    private static final int version = 4;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeObject(context);
        SerializationHelper.writeSafeUTF(out, script);
        out.writeInt(dataTypeId);
        out.writeBoolean(settable);
        out.writeInt(updateEvent);
        SerializationHelper.writeSafeUTF(out, updateCronPattern);
        out.writeInt(executionDelaySeconds);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            context = new ArrayList<IntValuePair>();
            Map<Integer, String> ctxMap = (Map<Integer, String>) in.readObject();
            for (Map.Entry<Integer, String> point : ctxMap.entrySet())
                context.add(new IntValuePair(point.getKey(), point.getValue()));

            script = SerializationHelper.readSafeUTF(in);
            dataTypeId = in.readInt();
            settable = false;
            updateEvent = in.readInt();
            updateCronPattern = "";
            executionDelaySeconds = in.readInt();
        }
        else if (ver == 2) {
            context = (List<IntValuePair>) in.readObject();
            script = SerializationHelper.readSafeUTF(in);
            dataTypeId = in.readInt();
            settable = false;
            updateEvent = in.readInt();
            updateCronPattern = "";
            executionDelaySeconds = in.readInt();
        }
        else if (ver == 3) {
            context = (List<IntValuePair>) in.readObject();
            script = SerializationHelper.readSafeUTF(in);
            dataTypeId = in.readInt();
            settable = false;
            updateEvent = in.readInt();
            updateCronPattern = SerializationHelper.readSafeUTF(in);
            executionDelaySeconds = in.readInt();
        }
        else if (ver == 4) {
            context = (List<IntValuePair>) in.readObject();
            script = SerializationHelper.readSafeUTF(in);
            dataTypeId = in.readInt();
            settable = in.readBoolean();
            updateEvent = in.readInt();
            updateCronPattern = SerializationHelper.readSafeUTF(in);
            executionDelaySeconds = in.readInt();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        Integer value = deserializeDataType(json, DataTypes.IMAGE);
        if (value != null)
            dataTypeId = value;

        String text = json.getString("updateEvent");
        if (text != null) {
            updateEvent = UPDATE_EVENT_CODES.getId(text);
            if (updateEvent == -1)
                throw new LocalizableJsonException("emport.error.invalid", "updateEvent", text,
                        UPDATE_EVENT_CODES.getCodeList());
        }

        JsonArray jsonContext = json.getJsonArray("context");
        if (jsonContext != null) {
            context.clear();
            DataPointDao dataPointDao = new DataPointDao();

            for (JsonValue jv : jsonContext.getElements()) {
                JsonObject jo = jv.toJsonObject();
                String xid = jo.getString("dataPointXid");
                if (xid == null)
                    throw new LocalizableJsonException("emport.error.meta.missing", "dataPointXid");

                DataPointVO dp = dataPointDao.getDataPoint(xid);
                if (dp == null)
                    throw new LocalizableJsonException("emport.error.missingPoint", xid);

                String var = jo.getString("varName");
                if (var == null)
                    throw new LocalizableJsonException("emport.error.meta.missing", "varName");

                context.add(new IntValuePair(dp.getId(), var));
            }
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        serializeDataType(map);

        map.put("updateEvent", UPDATE_EVENT_CODES.getCode(updateEvent));

        DataPointDao dataPointDao = new DataPointDao();
        List<Map<String, Object>> pointList = new ArrayList<Map<String, Object>>();
        for (IntValuePair p : context) {
            DataPointVO dp = dataPointDao.getDataPoint(p.getKey());
            if (dp != null) {
                Map<String, Object> point = new HashMap<String, Object>();
                pointList.add(point);
                point.put("varName", p.getValue());
                point.put("dataPointXid", dp.getXid());
            }
        }
        map.put("context", pointList);
    }
}
