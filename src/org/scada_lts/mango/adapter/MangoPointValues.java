/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.scada_lts.mango.adapter;

import java.util.List;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.vo.bean.LongPair;
import org.scada_lts.dao.model.point.PointValueAdnnotation;

/** 
 * Adapter for PointValuesService
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public interface MangoPointValues {

	long deletePointValuesBeforeWithOutLast(int dataPointId, long time);

	long deletePointValuesBeforeWithOutLastTwo(int dataPointId, long time);
	
	long deletePointValues(int dataPointId);
	
	long deleteAllPointValue();
	
	long savePointValueImpl(final int pointId, final PointValueTime pointValue, final SetPointSource source, boolean async);

	long deletePointValuesWithMismatchedType(int dataPointId, int dataType);

	long deletePointValuesWithValueLimit(int dataPointId, int limit);
	
	//PointValueTime getPointValue(long id);
	
	void clearUnsavedPointValues();
	
	void savePointValue(int pointId, PointValueTime pointValue);
	 
	List<PointValueTime> getPointValues(int dataPointId, long since);
	
	List<PointValueTime> getPointValuesBetween(int dataPointId,long from, long to);
	
	List<PointValueTime> getLatestPointValues(int dataPointId, int limit);
	
	List<PointValueTime> getLatestPointValues(int dataPointId, int limit, long before);

	PointValueTime getLatestPointValue(int dataPointId);

	PointValueTime getPointValueBefore(int dataPointId, long time);

	PointValueTime getPointValueAt(int dataPointId, long time);
	
	long getInceptionDate(int dataPointId); 
	
	long dateRangeCount(int dataPointId, long from, long to);
	
	LongPair getStartAndEndTime(List<Integer> dataPointIds);
	
	long getStartTime(List<Integer> dataPointIds);
	
	long getEndTime(List<Integer> dataPointIds);
	
	List<Long> getFiledataIds();
	
}
