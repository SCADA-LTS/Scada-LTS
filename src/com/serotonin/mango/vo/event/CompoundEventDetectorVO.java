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
package com.serotonin.mango.vo.event;

import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.compound.CompoundEventDetectorRT;
import com.serotonin.mango.rt.event.compound.ConditionParseException;
import com.serotonin.mango.rt.event.compound.LogicalOperator;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class CompoundEventDetectorVO implements ChangeComparable<CompoundEventDetectorVO>, JsonSerializable {
    public static final String XID_PREFIX = "CED_";

    private int id = Common.NEW_ID;
    private String xid;
    @JsonRemoteProperty
    private String name;
    private int alarmLevel = AlarmLevels.NONE;
    @JsonRemoteProperty
    private boolean returnToNormal = true;
    @JsonRemoteProperty
    private boolean disabled = false;
    @JsonRemoteProperty
    private String condition;

    public boolean isNew() {
        return id == Common.NEW_ID;
    }

    public EventTypeVO getEventType() {
        return new EventTypeVO(EventType.EventSources.COMPOUND, id, 0, new LocalizableMessage("common.default", name),
                alarmLevel);
    }

    public String getTypeKey() {
        return "event.audit.compoundEventDetector";
    }

    public void validate(DwrResponseI18n response) {
        if (StringUtils.isEmpty(name))
            response.addContextualMessage("name", "compoundDetectors.validation.nameRequired");

        validate(condition, response);
    }

    public static void validate(String condition, DwrResponseI18n response) {
        try {
            User user = Common.getUser();
            Permissions.ensureDataSourcePermission(user);

            LogicalOperator l = CompoundEventDetectorRT.parseConditionStatement(condition);
            List<String> keys = l.getDetectorKeys();

            // Get all of the point event detectors.
            List<DataPointVO> dataPoints = new DataPointDao().getDataPoints(null, true);

            for (String key : keys) {
                if (!key.startsWith(SimpleEventDetectorVO.POINT_EVENT_DETECTOR_PREFIX))
                    continue;

                boolean found = false;
                for (DataPointVO dp : dataPoints) {
                    if (!Permissions.hasDataSourcePermission(user, dp.getDataSourceId()))
                        continue;

                    for (PointEventDetectorVO ped : dp.getEventDetectors()) {
                        if (ped.getEventDetectorKey().equals(key) && ped.isRtnApplicable()) {
                            found = true;
                            break;
                        }
                    }

                    if (found)
                        break;
                }

                if (!found)
                    throw new ConditionParseException(new LocalizableMessage("compoundDetectors.validation.invalidKey"));
            }
        }
        catch (ConditionParseException e) {
            response.addMessage("condition", e.getLocalizableMessage());
            if (e.isRange()) {
                response.addData("range", true);
                response.addData("from", e.getFrom());
                response.addData("to", e.getTo());
            }
        }
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "common.xid", xid);
        AuditEventType.addPropertyMessage(list, "compoundDetectors.name", name);
        AuditEventType.addPropertyMessage(list, "common.alarmLevel", AlarmLevels.getAlarmLevelMessage(alarmLevel));
        AuditEventType.addPropertyMessage(list, "common.rtn", returnToNormal);
        AuditEventType.addPropertyMessage(list, "common.disabled", disabled);
        AuditEventType.addPropertyMessage(list, "compoundDetectors.condition", condition);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, CompoundEventDetectorVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.xid", from.xid, xid);
        AuditEventType.maybeAddPropertyChangeMessage(list, "compoundDetectors.name", from.name, name);
        AuditEventType.maybeAddAlarmLevelChangeMessage(list, "common.alarmLevel", from.alarmLevel, alarmLevel);
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.rtn", from.returnToNormal, returnToNormal);
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.disabled", from.disabled, disabled);
        AuditEventType.maybeAddPropertyChangeMessage(list, "compoundDetectors.condition", from.condition, condition);
    }

    public CompoundEventDetectorRT createRuntime() {
        return new CompoundEventDetectorRT(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public boolean isReturnToNormal() {
        return returnToNormal;
    }

    public void setReturnToNormal(boolean returnToNormal) {
        this.returnToNormal = returnToNormal;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    //
    // /
    // / Serialization
    // /
    //
    public void jsonSerialize(Map<String, Object> map) {
        map.put("xid", xid);
        map.put("alarmLevel", AlarmLevels.CODES.getCode(alarmLevel));
    }

    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String text = json.getString("alarmLevel");
        if (text != null) {
            alarmLevel = AlarmLevels.CODES.getId(text);
            if (!AlarmLevels.CODES.isValidId(alarmLevel))
                throw new LocalizableJsonException("emport.error.scheduledEvent.invalid", "alarmLevel", text,
                        AlarmLevels.CODES.getCodeList());
        }
    }
}
