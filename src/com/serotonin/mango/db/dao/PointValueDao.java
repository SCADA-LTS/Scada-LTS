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
package com.serotonin.mango.db.dao;

import java.util.List;

import javax.sql.DataSource;

import org.scada_lts.mango.adapter.MangoPointValues;
import org.scada_lts.mango.service.PointValueService;
import org.springframework.dao.ConcurrencyFailureException;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.vo.AnonymousUser;
import com.serotonin.mango.vo.bean.LongPair;

public class PointValueDao {
	
	private MangoPointValues pointValueService;

	public PointValueDao() {

		initializePrivateVariables();

	}

	public PointValueDao(DataSource dataSource) {

		initializePrivateVariables();

	}


	/**
	 * Only the PointValueCache should call this method during runtime. Do not
	 * use.
	 */
	public PointValueTime savePointValueSync(int pointId,
			PointValueTime pointValue, SetPointSource source) {
		long id = savePointValueImpl(pointId, pointValue, source, false);

		/*PointValueTime savedPointValue;
		int retries = 5;
		while (true) {
			try {
				savedPointValue = pointValueService.getPointValue(id);
				break;
			} catch (ConcurrencyFailureException e) {
				if (retries <= 0)
					throw e;
				retries--;
			}
		}*/

		return pointValue;
	}

	/**
	 * Only the PointValueCache should call this method during runtime. Do not
	 * use.
	 */
	public void savePointValueAsync(int pointId, PointValueTime pointValue,
			SetPointSource source) {
		long id = savePointValueImpl(pointId, pointValue, source, true);
		if (id != -1)
			pointValueService.clearUnsavedPointValues();
	}

	long savePointValueImpl(final int pointId, final PointValueTime pointValue,
			final SetPointSource source, boolean async) {
		return pointValueService.savePointValueImpl(pointId, pointValue, source, async);
	}

	public void savePointValue(int pointId, PointValueTime pointValue) {
		savePointValueImpl(pointId, pointValue, new AnonymousUser(), true);
	}
        	
	public List<PointValueTime> getPointValues(int dataPointId, long since) {
		return pointValueService.getPointValues(dataPointId, since);
	}

	public List<PointValueTime> getPointValuesBetween(int dataPointId,
			long from, long to) {
		return pointValueService.getPointValuesBetween(dataPointId, from, to);
	}

	public List<PointValueTime> getLatestPointValues(int dataPointId, int limit) {
		return pointValueService.getLatestPointValues(dataPointId, limit);
	}

	public List<PointValueTime> getLatestPointValues(int dataPointId,
			int limit, long before) {
		return pointValueService.getLatestPointValues(dataPointId, limit, before);
	}

	public PointValueTime getLatestPointValue(int dataPointId) {
		return pointValueService.getLatestPointValue(dataPointId);
	}

	public PointValueTime getPointValueBefore(int dataPointId, long time) {
		return pointValueService.getPointValueBefore(dataPointId, time);
	}

	public PointValueTime getPointValueAt(int dataPointId, long time) {
		return pointValueService.getPointValueAt(dataPointId, time);
	}

	public long deletePointValuesBefore(int dataPointId, long time) {
		return pointValueService.deletePointValuesBeforeWithOutLastTwo(dataPointId, time);
	}

	public long deletePointValues(int dataPointId) {
		return pointValueService.deletePointValues(dataPointId);
	}

	public long deleteAllPointData() {
		return pointValueService.deleteAllPointValue();
	}

	public long deletePointValuesWithMismatchedType(int dataPointId,
			int dataType) {
		return pointValueService.deletePointValuesWithMismatchedType(dataPointId, dataType);
	}

	public void compressTables() {
		//TODO rewrite because not have ejt
		//Common.ctx.getDatabaseAccess().executeCompress(ejt);
	}

	public long dateRangeCount(int dataPointId, long from, long to) {
		return pointValueService.dateRangeCount(dataPointId, from, to);
	}

	public long getInceptionDate(int dataPointId) {		
		return pointValueService.getInceptionDate(dataPointId);
	}

	public long getStartTime(List<Integer> dataPointIds) {
		return pointValueService.getStartTime(dataPointIds);
	}

	public long getEndTime(List<Integer> dataPointIds) {
		return pointValueService.getEndTime(dataPointIds);
	}

	public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
		return pointValueService.getStartAndEndTime(dataPointIds);
	}

	public List<Long> getFiledataIds() {
		return pointValueService.getFiledataIds();
	}
	private void initializePrivateVariables(){

		pointValueService = new PointValueService();

	}
}