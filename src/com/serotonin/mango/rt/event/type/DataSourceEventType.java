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

import java.util.Map;
import java.util.Objects;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@JsonRemoteEntity
public class DataSourceEventType extends EventType {
    private int dataSourceId;
    private int dataSourceEventTypeId;
    private int alarmLevel;
    private int duplicateHandling;

    private final Log LOG = LogFactory.getLog(DataSourceEventType.class);

    public DataSourceEventType() {
        // Required for reflection.
    }

    public DataSourceEventType(int dataSourceId, int dataSourceEventTypeId) {
        this(dataSourceId, dataSourceEventTypeId, AlarmLevels.URGENT, EventType.DuplicateHandling.IGNORE);
    }

    public DataSourceEventType(int dataSourceId, int dataSourceEventTypeId, int alarmLevel, int duplicateHandling) {
        this.dataSourceId = dataSourceId;
        this.dataSourceEventTypeId = dataSourceEventTypeId;
        this.alarmLevel = alarmLevel;
        this.duplicateHandling = duplicateHandling;
    }

    @Override
    public int getEventSourceId() {
        return EventType.EventSources.DATA_SOURCE;
    }

    public int getDataSourceEventTypeId() {
        return dataSourceEventTypeId;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    @Override
    public int getDataSourceId() {
        return dataSourceId;
    }

    @Override
    public String toString() {
        return "DataSoureEventType(dataSourceId=" + dataSourceId + ", eventTypeId=" + dataSourceEventTypeId + ")";
    }

    @Override
    public int getDuplicateHandling() {
        return duplicateHandling;
    }

    @Override
    public int getReferenceId1() {
        return dataSourceId;
    }

    @Override
    public int getReferenceId2() {
        return dataSourceEventTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceEventType)) return false;
        DataSourceEventType that = (DataSourceEventType) o;
        return getDataSourceId() == that.getDataSourceId() && getDataSourceEventTypeId() == that.getDataSourceEventTypeId() && getAlarmLevel() == that.getAlarmLevel() && getDuplicateHandling() == that.getDuplicateHandling();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataSourceId(), getDataSourceEventTypeId(), getAlarmLevel(), getDuplicateHandling());
    }

    //
    // /
    // / Serialization
    // /
    //
    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        DataSourceVO<?> ds = new DataSourceDao().getDataSource(dataSourceId);
        if(ds != null) {
            map.put("XID", ds.getXid());
            map.put("dataSourceEventType", ds.getEventCodes().getCode(dataSourceEventTypeId));
        } else {
            LOG.error("DataSource for id: " + dataSourceId + " does not exist.");
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);
        DataSourceVO<?> ds = getDataSource(json, "XID");
        if(ds != null) {
            dataSourceId = ds.getId();
            dataSourceEventTypeId = getInt(json, "dataSourceEventType", ds.getEventCodes());
        }
    }
}
