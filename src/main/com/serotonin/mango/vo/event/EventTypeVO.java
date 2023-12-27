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

import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.CompoundDetectorEventType;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.MaintenanceEventType;
import com.serotonin.mango.rt.event.type.PublisherEventType;
import com.serotonin.mango.rt.event.type.ScheduledEventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.web.i18n.LocalizableMessage;

public class EventTypeVO {
    /**
     * The type of event. @see EventType.EventSources
     */
    private int typeId;
    /**
     * For data point event, the data point id For data source event, the data source id For system event, the type id
     */
    private int typeRef1;
    /**
     * For data point event, the point event detector id For data source event, the data source event type For system
     * event, undefined
     */
    private int typeRef2;
    private LocalizableMessage description;
    private List<EventHandlerVO> handlers;
    private int alarmLevel;
    private String eventDetectorKey;
    private int duplicateHandling;

    public EventTypeVO(int typeId, int typeRef1, int typeRef2) {
        this.typeId = typeId;
        this.typeRef1 = typeRef1;
        this.typeRef2 = typeRef2;
    }

    public EventTypeVO(int typeId, int typeRef1, int typeRef2, LocalizableMessage description, int alarmLevel) {
        this(typeId, typeRef1, typeRef2);
        this.description = description;
        this.alarmLevel = alarmLevel;
    }

    public EventTypeVO(int typeId, int typeRef1, int typeRef2, LocalizableMessage description, int alarmLevel,
            int duplicateHandling) {
        this(typeId, typeRef1, typeRef2);
        this.description = description;
        this.alarmLevel = alarmLevel;
        this.duplicateHandling = duplicateHandling;
    }

    public EventTypeVO(int typeId, int typeRef1, int typeRef2, LocalizableMessage description, int alarmLevel,
            String eventDetectorKey) {
        this(typeId, typeRef1, typeRef2, description, alarmLevel);
        this.eventDetectorKey = eventDetectorKey;
    }

    public EventType createEventType() {
        if (typeId == EventType.EventSources.DATA_POINT)
            return new DataPointEventType(typeRef1, typeRef2);
        if (typeId == EventType.EventSources.DATA_SOURCE)
            return new DataSourceEventType(typeRef1, typeRef2, alarmLevel, duplicateHandling);
        if (typeId == EventType.EventSources.SYSTEM)
            return new SystemEventType(typeRef1, typeRef2);
        if (typeId == EventType.EventSources.COMPOUND)
            return new CompoundDetectorEventType(typeRef1);
        if (typeId == EventType.EventSources.SCHEDULED)
            return new ScheduledEventType(typeRef1);
        if (typeId == EventType.EventSources.PUBLISHER)
            return new PublisherEventType(typeRef1, typeRef2);
        if (typeId == EventType.EventSources.AUDIT)
            return new AuditEventType(typeRef1, typeRef2);
        if (typeId == EventType.EventSources.MAINTENANCE)
            return new MaintenanceEventType(typeRef1);
        return null;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeRef1() {
        return typeRef1;
    }

    public void setTypeRef1(int typeRef1) {
        this.typeRef1 = typeRef1;
    }

    public int getTypeRef2() {
        return typeRef2;
    }

    public void setTypeRef2(int typeRef2) {
        this.typeRef2 = typeRef2;
    }

    public LocalizableMessage getDescription() {
        return description;
    }

    public void setDescription(LocalizableMessage description) {
        this.description = description;
    }

    public List<EventHandlerVO> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<EventHandlerVO> handlers) {
        this.handlers = handlers;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getEventDetectorKey() {
        return eventDetectorKey;
    }

    public void setEventDetectorKey(String eventDetectorKey) {
        this.eventDetectorKey = eventDetectorKey;
    }
}
