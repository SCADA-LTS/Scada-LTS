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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.Common;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.web.i18n.LocalizableMessage;

import static com.serotonin.mango.rt.event.type.EventType.DuplicateHandling.IGNORE;

@JsonRemoteEntity
public class SystemEventType extends EventType {
	//
	// /
	// / Static stuff
	// /
	//
	private static final Log LOG = LogFactory.getLog(SystemEventType.class);
	private static final String SYSTEM_SETTINGS_PREFIX = "systemEventAlarmLevel";

	public static final int TYPE_SYSTEM_STARTUP = 1;
	public static final int TYPE_SYSTEM_SHUTDOWN = 2;
	public static final int TYPE_MAX_ALARM_LEVEL_CHANGED = 3;
	public static final int TYPE_USER_LOGIN = 4;
	// public static final int TYPE_VERSION_CHECK = 5;
	public static final int TYPE_COMPOUND_DETECTOR_FAILURE = 6;
	public static final int TYPE_SET_POINT_HANDLER_FAILURE = 7;
	public static final int TYPE_EMAIL_SEND_FAILURE = 8;
	public static final int TYPE_POINT_LINK_FAILURE = 9;
	public static final int TYPE_PROCESS_FAILURE = 10;
	public static final int TYPE_SCRIPT_HANDLER_FAILURE = 11;
	public static final int TYPE_SMS_SEND_FAILURE = 12;
	public static final int TYPE_ASSIGNED_EVENT = 13;
	public static final int TYPE_UNASSIGNED_EVENT = 14;

	public static final ExportCodes TYPE_CODES = new ExportCodes();
	static {
		TYPE_CODES.addElement(TYPE_SYSTEM_STARTUP, "SYSTEM_STARTUP");
		TYPE_CODES.addElement(TYPE_SYSTEM_SHUTDOWN, "SYSTEM_SHUTDOWN");
		TYPE_CODES.addElement(TYPE_MAX_ALARM_LEVEL_CHANGED,
				"MAX_ALARM_LEVEL_CHANGED");
		TYPE_CODES.addElement(TYPE_USER_LOGIN, "USER_LOGIN");
		// TYPE_CODES.addElement(TYPE_VERSION_CHECK, "VERSION_CHECK");
		TYPE_CODES.addElement(TYPE_COMPOUND_DETECTOR_FAILURE,
				"COMPOUND_DETECTOR_FAILURE");
		TYPE_CODES.addElement(TYPE_SET_POINT_HANDLER_FAILURE,
				"SET_POINT_HANDLER_FAILURE");
		TYPE_CODES.addElement(TYPE_EMAIL_SEND_FAILURE, "EMAIL_SEND_FAILURE");
		TYPE_CODES.addElement(TYPE_POINT_LINK_FAILURE, "POINT_LINK_FAILURE");
		TYPE_CODES.addElement(TYPE_PROCESS_FAILURE, "PROCESS_FAILURE");
		TYPE_CODES.addElement(TYPE_SCRIPT_HANDLER_FAILURE, "SCRIPT_HANDLER_FAILURE");
		TYPE_CODES.addElement(TYPE_SMS_SEND_FAILURE, "SMS_SEND_FAILURE");
		TYPE_CODES.addElement(TYPE_ASSIGNED_EVENT, "ASSIGNED_EVENT");
		TYPE_CODES.addElement(TYPE_UNASSIGNED_EVENT, "UNASSIGNED_EVENT");
	}

	private static List<EventTypeVO> systemEventTypes;

	public static List<EventTypeVO> getSystemEventTypes() {
		if (systemEventTypes == null) {
			systemEventTypes = new CopyOnWriteArrayList<>();

			addEventTypeVO(TYPE_SYSTEM_STARTUP, "event.system.startup",
					AlarmLevels.INFORMATION);
			addEventTypeVO(TYPE_SYSTEM_SHUTDOWN, "event.system.shutdown",
					AlarmLevels.INFORMATION);
			addEventTypeVO(TYPE_MAX_ALARM_LEVEL_CHANGED,
					"event.system.maxAlarmChanged", AlarmLevels.NONE);
			addEventTypeVO(TYPE_USER_LOGIN, "event.system.userLogin",
					AlarmLevels.INFORMATION);
			// addEventTypeVO(TYPE_VERSION_CHECK, "event.system.versionCheck",
			// AlarmLevels.INFORMATION);
			addEventTypeVO(TYPE_COMPOUND_DETECTOR_FAILURE,
					"event.system.compound", AlarmLevels.URGENT);
			addEventTypeVO(TYPE_SET_POINT_HANDLER_FAILURE,
					"event.system.setPoint", AlarmLevels.URGENT);
			addEventTypeVO(TYPE_EMAIL_SEND_FAILURE, "event.system.email",
					AlarmLevels.INFORMATION);
			addEventTypeVO(TYPE_POINT_LINK_FAILURE, "event.system.pointLink",
					AlarmLevels.URGENT);
			addEventTypeVO(TYPE_PROCESS_FAILURE, "event.system.process",
					AlarmLevels.URGENT);
			addEventTypeVO(TYPE_SCRIPT_HANDLER_FAILURE, "event.system.script",
					AlarmLevels.URGENT);
			addEventTypeVO(TYPE_SMS_SEND_FAILURE, "event.system.sms",
					AlarmLevels.INFORMATION);
			addEventTypeVO(TYPE_ASSIGNED_EVENT, "event.system.assigned",
					AlarmLevels.INFORMATION);
			addEventTypeVO(TYPE_UNASSIGNED_EVENT, "event.system.unassigned",
					AlarmLevels.INFORMATION);
		}
		return systemEventTypes;
	}

	private static void addEventTypeVO(int type, String key,
			int defaultAlarmLevel) {
		systemEventTypes.add(new EventTypeVO(EventType.EventSources.SYSTEM,
				type, 0, new LocalizableMessage(key), SystemSettingsDAO
						.getIntValue(SYSTEM_SETTINGS_PREFIX + type,
								defaultAlarmLevel)));
	}

	public static EventTypeVO getEventType(int type) {
		for (EventTypeVO et : getSystemEventTypes()) {
			if (et.getTypeRef1() == type)
				return et;
		}
		return null;
	}

	public static void setEventTypeAlarmLevel(int type, int alarmLevel) {
		EventTypeVO et = getEventType(type);
		if(et != null) {
			et.setAlarmLevel(alarmLevel);

			SystemSettingsDAO dao = new SystemSettingsDAO();
			dao.setIntValue(SYSTEM_SETTINGS_PREFIX + type, alarmLevel);
		} else {
			LOG.warn(LoggingUtils.eventTypeInfo(type, alarmLevel));
		}
	}

	public static void raiseEvent(EventType type, long time, boolean rtn,
			LocalizableMessage message) {
		EventTypeVO vo = getEventType(type.getReferenceId1());
		if(vo != null) {
			int alarmLevel = vo.getAlarmLevel();
			Common.ctx.getEventManager().raiseEvent(type, time, rtn, alarmLevel,
					message, null);
		} else {
			LOG.error(LoggingUtils.systemEventTypInfo(type));
		}
	}

	public static void returnToNormal(EventType type, long time) {
		Common.ctx.getEventManager().returnToNormal(type, time);
	}

	//
	// /
	// / Instance stuff
	// /
	//
	private int systemEventTypeId;
	private int refId2;
	private int duplicateHandling = EventType.DuplicateHandling.ALLOW;

	public SystemEventType() {
		// Required for reflection.
	}

	public SystemEventType(int systemEventTypeId) {
		this.systemEventTypeId = systemEventTypeId;
	}

	public SystemEventType(int systemEventTypeId, int refId2) {
		this(systemEventTypeId);
		this.refId2 = refId2;
	}

	public SystemEventType(int systemEventTypeId, int refId2,
			int duplicateHandling) {
		this(systemEventTypeId);
		this.refId2 = refId2;
		this.duplicateHandling = duplicateHandling;
	}

	public static SystemEventType duplicateIgnoreEventType(int systemEventTypeId, int refId2) {
		return new SystemEventType(systemEventTypeId, refId2, IGNORE);
	}

	@Override
	public int getEventSourceId() {
		return EventType.EventSources.SYSTEM;
	}

	public int getSystemEventTypeId() {
		return systemEventTypeId;
	}

	@Override
	public int getEventHandlerId() {
		return this.systemEventTypeId == TYPE_SET_POINT_HANDLER_FAILURE ||
				this.systemEventTypeId == TYPE_SCRIPT_HANDLER_FAILURE ||
				this.systemEventTypeId == TYPE_EMAIL_SEND_FAILURE ||
				this.systemEventTypeId == TYPE_SMS_SEND_FAILURE ||
				this.systemEventTypeId == TYPE_PROCESS_FAILURE ? getReferenceId2() : -1;
	}

	@Override
	public boolean isSystemMessage() {
		return true;
	}

	@Override
	public String toString() {
		return "SystemEventType{" +
				"systemEventTypeId=" + systemEventTypeId +
				", refId2=" + refId2 +
				", duplicateHandling=" + duplicateHandling +
				'}';
	}

	@Override
	public int getDuplicateHandling() {
		return duplicateHandling;
	}

	@Override
	public int getReferenceId1() {
		return systemEventTypeId;
	}

	@Override
	public int getReferenceId2() {
		return refId2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + refId2;
		result = prime * result + systemEventTypeId;
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
		SystemEventType other = (SystemEventType) obj;
		if (refId2 != other.refId2)
			return false;
		if (systemEventTypeId != other.systemEventTypeId)
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
		map.put("systemType", TYPE_CODES.getCode(systemEventTypeId));
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		super.jsonDeserialize(reader, json);
		systemEventTypeId = getInt(json, "systemType", TYPE_CODES);
	}
}
