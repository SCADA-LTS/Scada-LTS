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
package com.serotonin.mango.vo.permission;

import java.util.Map;
import java.util.Objects;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 * 
 */
@JsonRemoteEntity
public class DataPointAccess implements JsonSerializable {
	public static final int READ = 1;
	public static final int SET = 2;

	private static final ExportCodes ACCESS_CODES = new ExportCodes();
	static {
		ACCESS_CODES.addElement(READ, "READ", "common.access.read");
		ACCESS_CODES.addElement(SET, "SET", "common.access.set");
	}

	private int dataPointId;
	private int permission;

	public DataPointAccess() {
	}

	public DataPointAccess(int dataPointId, int permission) {
		this.dataPointId = dataPointId;
		this.permission = permission;
	}

	public int getDataPointId() {
		return dataPointId;
	}

	public void setDataPointId(int dataPointId) {
		this.dataPointId = dataPointId;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		String text = json.getString("dataPointXid");
		if (StringUtils.isEmpty(text))
			throw new LocalizableJsonException(
					"emport.error.permission.missing", "dataPointXid");

		DataPointVO dp = new DataPointDao().getDataPoint(text);
		if (dp == null)
			throw new LocalizableJsonException("emport.error.missingPoint",
					text);
		dataPointId = dp.getId();

		text = json.getString("permission");
		if (StringUtils.isEmpty(text))
			throw new LocalizableJsonException("emport.error.missing",
					"permission", ACCESS_CODES.getCodeList());
		permission = ACCESS_CODES.getId(text);
		if (permission == -1)
			throw new LocalizableJsonException("emport.error.invalid",
					"permission", text, ACCESS_CODES.getCodeList());
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		map.put("dataPointXid", new DataPointDao().getDataPoint(dataPointId)
				.getXid());
		map.put("permission", ACCESS_CODES.getCode(permission));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DataPointAccess)) return false;
		DataPointAccess that = (DataPointAccess) o;
		return getDataPointId() == that.getDataPointId() &&
				getPermission() == that.getPermission();
	}

	@Override
	public int hashCode() {

		return Objects.hash(getDataPointId(), getPermission());
	}

	@Override
	public String toString() {
		return "DataPointAccess{" +
				"dataPointId=" + dataPointId +
				", permission=" + permission +
				'}';
	}
}
