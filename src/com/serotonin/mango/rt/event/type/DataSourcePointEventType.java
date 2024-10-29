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
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;

import java.util.Map;
import java.util.Objects;

@JsonRemoteEntity
public class DataSourcePointEventType extends DataSourceEventType {

    private DataSourceEventType dataSourceEventType;
    private int dataPointId;
    private final int duplicateHandling = DuplicateHandling.IGNORE_SAME_MESSAGE;

    private final Log LOG = LogFactory.getLog(DataSourcePointEventType.class);

    public DataSourcePointEventType() {
        // Required for reflection.
    }

    public DataSourcePointEventType(DataSourceEventType dataSourceEventType, int dataPointId) {
        this.dataSourceEventType = dataSourceEventType;
        this.dataPointId = dataPointId;
    }

    @Override
    public int getEventSourceId() {
        return EventSources.DATA_SOURCE_POINT;
    }

    @Override
    public int getDataSourceEventTypeId() {
        return dataSourceEventType.getDataSourceEventTypeId();
    }

    @Override
    public int getAlarmLevel() {
        return dataSourceEventType.getAlarmLevel();
    }

    @Override
    public int getDataSourceId() {
        return dataSourceEventType.getDataSourceId();
    }

    @Override
    public int getDataPointId() {
        return dataPointId;
    }

    @Override
    public String toString() {
        return "DataSoureEventType(dataSourceId=" + dataSourceEventType.getDataSourceId() + ", dataPointId=" + dataPointId + ", eventTypeId=" + dataSourceEventType.getDataSourceEventTypeId() + ")";
    }

    @Override
    public int getDuplicateHandling() {
        return duplicateHandling;
    }

    @Override
    public int getReferenceId1() {
        return dataSourceEventType.getDataSourceId();
    }

    @Override
    public int getReferenceId2() {
        return dataSourceEventType.getDataSourceEventTypeId();
    }

    @Override
    public int getReferenceId3() {
        return dataPointId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSourcePointEventType that = (DataSourcePointEventType) o;
        return dataPointId == that.dataPointId && Objects.equals(dataSourceEventType, that.dataSourceEventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSourceEventType, dataPointId);
    }

    //
    // /
    // / Serialization
    // /
    //
    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        dataSourceEventType.jsonSerialize(map);
        DataPointService dataPointService = new DataPointService();
        DataPointVO dataPoint = dataPointService.getDataPoint(dataPointId);
        if(dataPoint != null) {
            map.put("DATA_POINT_XID", dataPoint.getXid());
        } else {
            LOG.error("DataPoint for id: " + dataPointId + " does not exist.");
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);
        DataSourceEventType dataSourceEventType = new DataSourceEventType();
        dataSourceEventType.jsonDeserialize(reader, json);
        this.dataSourceEventType = dataSourceEventType;

        DataPointVO dataPoint = getDataPoint(json, "DATA_POINT_XID");
        if(dataPoint != null) {
            dataPointId = dataPoint.getId();
        }
    }
}
