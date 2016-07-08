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
import com.serotonin.mango.db.dao.SystemSettingsDao;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class SystemEventType extends EventType {
	//
	// /
	// / Static stuff
	// /
	//
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
	}

	private static List<EventTypeVO> systemEventTypes;

	public static List<EventTypeVO> getSystemEventTypes() {
		if (systemEventTypes == null) {
			systemEventTypes = new ArrayList<EventTypeVO>();

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
		}
		return systemEventTypes;
	}

	private static void addEventTypeVO(int type, String key,
			int defaultAlarmLevel) {
		systemEventTypes.add(new EventTypeVO(EventType.EventSources.SYSTEM,
				type, 0, new LocalizableMessage(key), SystemSettingsDao
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
		et.setAlarmLevel(alarmLevel);

		SystemSettingsDao dao = new SystemSettingsDao();
		dao.setIntValue(SYSTEM_SETTINGS_PREFIX + type, alarmLevel);
	}

	public static void raiseEvent(SystemEventType type, long time, boolean rtn,
			LocalizableMessage message) {
		EventTypeVO vo = getEventType(type.getSystemEventTypeId());
		int alarmLevel = vo.getAlarmLevel();
		Common.ctx.getEventManager().raiseEvent(type, time, rtn, alarmLevel,
				message, null);
	}

	public static void returnToNormal(SystemEventType type, long time) {
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

	@Override
	public int getEventSourceId() {
		return EventType.EventSources.SYSTEM;
	}

	public int getSystemEventTypeId() {
		return systemEventTypeId;
	}

	@Override
	public boolean isSystemMessage() {
		return true;
	}

	@Override
	public String toString() {
		return "SystemEventType(eventTypeId=" + systemEventTypeId + ")";
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
