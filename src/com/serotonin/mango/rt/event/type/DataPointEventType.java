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

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.db.dao.DataPointDao;

@JsonRemoteEntity
public class DataPointEventType extends EventType {
	private int dataSourceId = -1;
	private int dataPointId;
	private int pointEventDetectorId;
	private int duplicateHandling = EventType.DuplicateHandling.IGNORE;

	private final Log LOG = LogFactory.getLog(DataPointEventType.class);

	public DataPointEventType() {
		// Required for reflection.
	}

	public DataPointEventType(int dataPointId, int pointEventDetectorId) {
		this.dataPointId = dataPointId;
		this.pointEventDetectorId = pointEventDetectorId;
	}

	@Override
	public int getEventSourceId() {
		return EventType.EventSources.DATA_POINT;
	}

	@Override
	public int getDataSourceId() {
		if (dataSourceId == -1)
			dataSourceId = new DataPointDao().getDataPoint(dataPointId)
					.getDataSourceId();
		LOG.debug(toString() + " - getDataSourceId() - "
				+ Integer.toString(dataSourceId));
		return dataSourceId;
	}

	@Override
	public int getDataPointId() {
		return dataPointId;
	}

	public int getPointEventDetectorId() {
		return pointEventDetectorId;
	}

	@Override
	public String toString() {
		return "DataPointEventType(dataPointId=" + dataPointId
				+ ", detectorId=" + pointEventDetectorId + ")";
	}

	@Override
	public int getDuplicateHandling() {
		return duplicateHandling;
	}

	public void setDuplicateHandling(int duplicateHandling) {
		this.duplicateHandling = duplicateHandling;
	}

	@Override
	public int getReferenceId1() {
		return dataPointId;
	}

	@Override
	public int getReferenceId2() {
		return pointEventDetectorId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pointEventDetectorId;
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
		DataPointEventType other = (DataPointEventType) obj;
		if (pointEventDetectorId != other.pointEventDetectorId)
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
		setDataPointXid(map);
		setDetectorXid(map);
	}

	private void setDataPointXid(Map<String, Object> map) {
		DataPointDao dataPointDao = new DataPointDao();
		DataPointVO dataPointVO = dataPointDao.getDataPoint(dataPointId);
		if(dataPointVO != null)
			map.put("dataPointXID", dataPointVO.getXid());
		else {
			LOG.error("DataPoint for id: " + dataPointId + " does not exist.");
		}
	}

	private void setDetectorXid(Map<String, Object> map) {
		try {
			DataPointDao dataPointDao = new DataPointDao();
			String xid = dataPointDao.getDetectorXid(pointEventDetectorId);
			map.put("detectorXID", xid);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		super.jsonDeserialize(reader, json);
		dataPointId = getDataPointId(json, "dataPointXID");
		pointEventDetectorId = getPointEventDetectorId(json, dataPointId,
				"detectorXID");
	}
}
