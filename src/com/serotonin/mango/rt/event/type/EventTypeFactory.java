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

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonValue;
import com.serotonin.json.TypeFactory;
import com.serotonin.mango.rt.event.type.EventType.EventSources;
import com.serotonin.mango.util.LocalizableJsonException;

public class EventTypeFactory implements TypeFactory {
    @Override
    public Class<?> getType(JsonValue jsonValue) throws JsonException {
        if (jsonValue.isNull())
            throw new LocalizableJsonException("emport.error.eventType.null");

        JsonObject json = jsonValue.toJsonObject();

        String text = json.getString("sourceType");
        if (text == null)
            throw new LocalizableJsonException("emport.error.eventType.missing", "sourceType",
                    EventType.SOURCE_CODES.getCodeList());

        int source = EventType.SOURCE_CODES.getId(text);
        if (!EventType.SOURCE_CODES.isValidId(source))
            throw new LocalizableJsonException("emport.error.eventType.invalid", "sourceType", text,
                    EventType.SOURCE_CODES.getCodeList());

        if (source == EventSources.DATA_POINT)
            return DataPointEventType.class;
        if (source == EventSources.DATA_SOURCE)
            return DataSourceEventType.class;
        if (source == EventSources.SYSTEM)
            return SystemEventType.class;
        if (source == EventSources.COMPOUND)
            return CompoundDetectorEventType.class;
        if (source == EventSources.SCHEDULED)
            return ScheduledEventType.class;
        if (source == EventSources.PUBLISHER)
            return PublisherEventType.class;
        if (source == EventSources.AUDIT)
            return AuditEventType.class;
        if (source == EventSources.MAINTENANCE)
            return MaintenanceEventType.class;

        return null;
    }
}
