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
package com.serotonin.mango.rt.event.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.SystemSettingsDao;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class AuditEventType extends EventType {
    //
    // /
    // / Static stuff
    // /
    //
    private static final String AUDIT_SETTINGS_PREFIX = "auditEventAlarmLevel";

    public static final int TYPE_DATA_SOURCE = 1;
    public static final int TYPE_DATA_POINT = 2;
    public static final int TYPE_POINT_EVENT_DETECTOR = 3;
    public static final int TYPE_COMPOUND_EVENT_DETECTOR = 4;
    public static final int TYPE_SCHEDULED_EVENT = 5;
    public static final int TYPE_EVENT_HANDLER = 6;
    public static final int TYPE_POINT_LINK = 7;
    public static final int TYPE_MAINTENANCE_EVENT = 8;

    private static final ExportCodes TYPE_CODES = new ExportCodes();
    static {
        TYPE_CODES.addElement(TYPE_DATA_SOURCE, "DATA_SOURCE");
        TYPE_CODES.addElement(TYPE_DATA_POINT, "DATA_POINT");
        TYPE_CODES.addElement(TYPE_POINT_EVENT_DETECTOR, "POINT_EVENT_DETECTOR");
        TYPE_CODES.addElement(TYPE_COMPOUND_EVENT_DETECTOR, "COMPOUND_EVENT_DETECTOR");
        TYPE_CODES.addElement(TYPE_SCHEDULED_EVENT, "SCHEDULED_EVENT");
        TYPE_CODES.addElement(TYPE_EVENT_HANDLER, "EVENT_HANDLER");
        TYPE_CODES.addElement(TYPE_POINT_LINK, "POINT_LINK");
        TYPE_CODES.addElement(TYPE_MAINTENANCE_EVENT, "MAINTENANCE_EVENT");
    }

    private static List<EventTypeVO> auditEventTypes;

    public static List<EventTypeVO> getAuditEventTypes() {
        if (auditEventTypes == null) {
            auditEventTypes = new ArrayList<EventTypeVO>();

            addEventTypeVO(TYPE_DATA_SOURCE, "event.audit.dataSource");
            addEventTypeVO(TYPE_DATA_POINT, "event.audit.dataPoint");
            addEventTypeVO(TYPE_POINT_EVENT_DETECTOR, "event.audit.pointEventDetector");
            addEventTypeVO(TYPE_COMPOUND_EVENT_DETECTOR, "event.audit.compoundEventDetector");
            addEventTypeVO(TYPE_SCHEDULED_EVENT, "event.audit.scheduledEvent");
            addEventTypeVO(TYPE_EVENT_HANDLER, "event.audit.eventHandler");
            addEventTypeVO(TYPE_POINT_LINK, "event.audit.pointLink");
            addEventTypeVO(TYPE_MAINTENANCE_EVENT, "event.audit.maintenanceEvent");
        }
        return auditEventTypes;
    }

    private static void addEventTypeVO(int type, String key) {
        auditEventTypes.add(new EventTypeVO(EventType.EventSources.AUDIT, type, 0, new LocalizableMessage(key),
                SystemSettingsDao.getIntValue(AUDIT_SETTINGS_PREFIX + type, AlarmLevels.INFORMATION)));
    }

    public static EventTypeVO getEventType(int type) {
        for (EventTypeVO et : getAuditEventTypes()) {
            if (et.getTypeRef1() == type)
                return et;
        }
        return null;
    }

    public static void setEventTypeAlarmLevel(int type, int alarmLevel) {
        EventTypeVO et = getEventType(type);
        et.setAlarmLevel(alarmLevel);

        SystemSettingsDao dao = new SystemSettingsDao();
        dao.setIntValue(AUDIT_SETTINGS_PREFIX + type, alarmLevel);
    }

    public static void raiseAddedEvent(int auditEventTypeId, ChangeComparable<?> o) {
        List<LocalizableMessage> list = new ArrayList<LocalizableMessage>();
        o.addProperties(list);
        raiseEvent(auditEventTypeId, o, "event.audit.added", list.toArray());
    }

    public static <T> void raiseChangedEvent(int auditEventTypeId, T from, ChangeComparable<T> to) {
        List<LocalizableMessage> changes = new ArrayList<LocalizableMessage>();
        to.addPropertyChanges(changes, from);
        if (changes.size() == 0)
            // If the object wasn't in fact changed, don't raise an event.
            return;
        raiseEvent(auditEventTypeId, to, "event.audit.changed", changes.toArray());
    }

    public static void raiseDeletedEvent(int auditEventTypeId, ChangeComparable<?> o) {
        List<LocalizableMessage> list = new ArrayList<LocalizableMessage>();
        o.addProperties(list);
        raiseEvent(auditEventTypeId, o, "event.audit.deleted", list.toArray());
    }

    private static void raiseEvent(int auditEventTypeId, ChangeComparable<?> o, String key, Object[] props) {
        User user = Common.getUser();
        Object username;
        if (user != null)
            username = user.getUsername() + " (" + user.getId() + ")";
        else {
            String descKey = Common.getBackgroundProcessDescription();
            if (descKey == null)
                username = new LocalizableMessage("common.unknown");
            else
                username = new LocalizableMessage(descKey);
        }

        LocalizableMessage message = new LocalizableMessage(key, username, new LocalizableMessage(o.getTypeKey()),
                o.getId(), new LocalizableMessage("event.audit.propertyList." + props.length, props));

        AuditEventType type = new AuditEventType(auditEventTypeId, o.getId());
        type.setRaisingUser(user);

        Common.ctx.getEventManager().raiseEvent(type, System.currentTimeMillis(), false,
                getEventType(type.getAuditEventTypeId()).getAlarmLevel(), message, null);
    }

    //
    // /
    // / Utility methods for other classes
    // /
    //
    public static void addPropertyMessage(List<LocalizableMessage> list, String propertyNameKey, Object propertyValue) {
        list.add(new LocalizableMessage("event.audit.property", new LocalizableMessage(propertyNameKey), propertyValue));
    }

    public static void addPropertyMessage(List<LocalizableMessage> list, String propertyNameKey, boolean propertyValue) {
        list.add(new LocalizableMessage("event.audit.property", new LocalizableMessage(propertyNameKey),
                getBooleanMessage(propertyValue)));
    }

    public static void addPeriodMessage(List<LocalizableMessage> list, String propertyNameKey, int periodType,
            int period) {
        list.add(new LocalizableMessage("event.audit.property", new LocalizableMessage(propertyNameKey), Common
                .getPeriodDescription(periodType, period)));
    }

    public static void addExportCodeMessage(List<LocalizableMessage> list, String propertyNameKey, ExportCodes codes,
            int id) {
        list.add(new LocalizableMessage("event.audit.property", new LocalizableMessage(propertyNameKey),
                getExportCodeMessage(codes, id)));
    }

    public static void addDataTypeMessage(List<LocalizableMessage> list, String propertyNameKey, int dataTypeId) {
        list.add(new LocalizableMessage("event.audit.property", new LocalizableMessage(propertyNameKey), DataTypes
                .getDataTypeMessage(dataTypeId)));
    }

    public static void maybeAddPropertyChangeMessage(List<LocalizableMessage> list, String propertyNameKey,
            int fromValue, int toValue) {
        if (fromValue != toValue)
            addPropertyChangeMessage(list, propertyNameKey, fromValue, toValue);
    }

    public static void maybeAddPropertyChangeMessage(List<LocalizableMessage> list, String propertyNameKey,
            Object fromValue, Object toValue) {
        if (!StringUtils.isEqual(fromValue, toValue))
            addPropertyChangeMessage(list, propertyNameKey, fromValue, toValue);
    }

    public static void maybeAddPropertyChangeMessage(List<LocalizableMessage> list, String propertyNameKey,
            boolean fromValue, boolean toValue) {
        if (fromValue != toValue)
            addPropertyChangeMessage(list, propertyNameKey, getBooleanMessage(fromValue), getBooleanMessage(toValue));
    }

    public static void maybeAddAlarmLevelChangeMessage(List<LocalizableMessage> list, String propertyNameKey,
            int fromAlarmLevel, int toAlarmLevel) {
        if (fromAlarmLevel != toAlarmLevel)
            addPropertyChangeMessage(list, propertyNameKey, AlarmLevels.getAlarmLevelMessage(fromAlarmLevel),
                    AlarmLevels.getAlarmLevelMessage(toAlarmLevel));
    }

    public static void maybeAddPeriodChangeMessage(List<LocalizableMessage> list, String propertyNameKey,
            int fromPeriodType, int fromPeriod, int toPeriodType, int toPeriod) {
        if (fromPeriodType != toPeriodType || fromPeriod != toPeriod)
            addPropertyChangeMessage(list, propertyNameKey, Common.getPeriodDescription(fromPeriodType, fromPeriod),
                    Common.getPeriodDescription(toPeriodType, toPeriod));
    }

    public static void maybeAddExportCodeChangeMessage(List<LocalizableMessage> list, String propertyNameKey,
            ExportCodes exportCodes, int fromId, int toId) {
        if (fromId != toId)
            addPropertyChangeMessage(list, propertyNameKey, getExportCodeMessage(exportCodes, fromId),
                    getExportCodeMessage(exportCodes, toId));
    }

    public static void maybeAddDataTypeChangeMessage(List<LocalizableMessage> list, String propertyNameKey,
            int fromDataTypeId, int toDataTypeId) {
        if (fromDataTypeId != toDataTypeId)
            addPropertyChangeMessage(list, propertyNameKey, DataTypes.getDataTypeMessage(fromDataTypeId),
                    DataTypes.getDataTypeMessage(toDataTypeId));
    }

    private static LocalizableMessage getBooleanMessage(boolean value) {
        if (value)
            return new LocalizableMessage("common.true");
        return new LocalizableMessage("common.false");
    }

    private static LocalizableMessage getExportCodeMessage(ExportCodes exportCodes, int id) {
        String key = exportCodes.getKey(id);
        if (key == null)
            return new LocalizableMessage("common.unknown");
        return new LocalizableMessage(key);
    }

    public static void addPropertyChangeMessage(List<LocalizableMessage> list, String propertyNameKey,
            Object fromValue, Object toValue) {
        list.add(new LocalizableMessage("event.audit.changedProperty", new LocalizableMessage(propertyNameKey),
                fromValue, toValue));
    }

    //
    // /
    // / Instance stuff
    // /
    //
    private int auditEventTypeId;
    private int referenceId;
    private User raisingUser;

    public AuditEventType() {
        // Required for reflection.
    }

    public AuditEventType(int auditEventTypeId, int referenceId) {
        this.auditEventTypeId = auditEventTypeId;
        this.referenceId = referenceId;
    }

    @Override
    public int getEventSourceId() {
        return EventType.EventSources.AUDIT;
    }

    public int getAuditEventTypeId() {
        return auditEventTypeId;
    }

    @Override
    public String toString() {
        return "AuditEventType(auditTypeId=" + auditEventTypeId + ", referenceId=" + referenceId + ")";
    }

    @Override
    public int getDuplicateHandling() {
        return DuplicateHandling.ALLOW;
    }

    @Override
    public int getReferenceId1() {
        return auditEventTypeId;
    }

    @Override
    public int getReferenceId2() {
        return referenceId;
    }

    public void setRaisingUser(User raisingUser) {
        this.raisingUser = raisingUser;
    }

    @Override
    public boolean excludeUser(User user) {
        if (raisingUser != null && !raisingUser.isReceiveOwnAuditEvents())
            return user.equals(raisingUser);
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + auditEventTypeId;
        result = prime * result + referenceId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AuditEventType other = (AuditEventType) obj;
        if (auditEventTypeId != other.auditEventTypeId)
            return false;
        if (referenceId != other.referenceId)
            return false;
        return true;
    }

    //
    // /
    // / Serialization
    // /
    //
    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        map.put("auditType", TYPE_CODES.getCode(auditEventTypeId));
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);
        auditEventTypeId = getInt(json, "auditType", TYPE_CODES);
    }
}
