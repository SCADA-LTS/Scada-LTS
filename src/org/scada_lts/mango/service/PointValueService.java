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
package org.scada_lts.mango.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.pointvalues.PointValueAdnnotationsDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.mango.adapter.MangoPointValues;

import com.serotonin.io.StreamUtils;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.ImageSaveException;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.bean.LongPair;

/** 
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class PointValueService implements MangoPointValues {
	 
	 private PointValueDAO  pointValueDAO; 
	 
	 private PointValueAdnnotationsDAO pointValueAdnnotationDAO;
	 
	 public PointValueService() {
		 pointValueDAO = new PointValueDAO(); 
		 pointValueAdnnotationDAO = new PointValueAdnnotationsDAO();
	 }
     
	 public long savePointValue(final long pointId, final PointValueTime pointValue, final SetPointSource source, boolean async) {
		 
		 //TOOD rewrite
		 MangoValue value = pointValue.getValue();
		 final int dataType = DataTypes.getDataType(value);

		 PointValue pv = new PointValue();
		 pv.setDataPointId(pointId);
		 pv.setPointValue(pointValue);
		 long id = (long) pointValueDAO.create(pv)[0];
			
		 //TODO rewrite to save blob or xml svg
		 if (dataType == DataTypes.IMAGE) {
			// Check if we need to save an image
			if (dataType == DataTypes.IMAGE) {
				ImageValue imageValue = (ImageValue) value;
				if (!imageValue.isSaved()) {
					imageValue.setId(id);

					File file = new File(Common.getFiledataPath(),
							imageValue.getFilename());

					// Write the file.
					FileOutputStream out = null;
					try {
						out = new FileOutputStream(file);
						StreamUtils
								.transfer(
										new ByteArrayInputStream(imageValue
												.getData()), out);
					} catch (IOException e) {
						// Rethrow as an RTE
						throw new ImageSaveException(e);
					} finally {
						try {
							if (out != null)
								out.close();
						} catch (IOException e) {
							// no op
						}
					}

					// Allow the data to be GC'ed
					imageValue.setData(null);
				}
			}
		 }

		 return id;
		 
	 }
	 
	// TODO why don't return id;
	public void savePointValue(int pointId, PointValueTime pointValue) {
		 PointValue pv = new PointValue();
		 pv.setDataPointId(pointId);
		 pv.setPointValue(pointValue);
		 pointValueDAO.create(pv);
	}
	 
	public List<PointValueTime> getPointValues(int dataPointId, long since) {
		List<PointValue> lst = pointValueDAO.filtered(
				PointValueDAO.POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP, 
				new Object[]{dataPointId, since}, GenericDaoCR.NO_LIMIT);
		return getLstPointValueTime(lst);
	}
	
	public List<PointValueTime> getPointValuesBetween(int dataPointId,
			long from, long to) {
		List<PointValue> lst = pointValueDAO.filtered(
				PointValueDAO.POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP_FROM_TO, 
				new Object[]{dataPointId, from, to}, GenericDaoCR.NO_LIMIT);
		return getLstPointValueTime(lst);
	}
	
	public List<PointValueTime> getLatestPointValues(int dataPointId, int limit) {
		List<PointValue> lst = pointValueDAO.filtered(
				PointValueDAO.POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID, 
				new Object[]{dataPointId},limit);
		return getLstPointValueTime(lst);
	}
	
	public List<PointValueTime> getLatestPointValues(int dataPointId,
			int limit, long before) {
		List<PointValue> lst = pointValueDAO.filtered(
				PointValueDAO.POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID, 
				new Object[]{dataPointId, before},limit);
		return getLstPointValueTime(lst);
	}

	public PointValueTime getLatestPointValue(int dataPointId) {
		
		Long maxTs = pointValueDAO.getLatestPointValue(dataPointId);
		if (maxTs == null || maxTs == 0)
			return null;
		
		List<PointValue> lstValues = pointValueDAO.findByIdAndTs(dataPointId, maxTs);
		
		pointValueAdnnotationDAO.updateAnnotations(lstValues);
		if (lstValues.size() == 0)
			return null;
		return lstValues.get(0).getPointValue();
	}	

	public PointValueTime getPointValueBefore(int dataPointId, long time) {
		List<PointValue> lst = pointValueDAO.filtered(
				PointValueDAO.POINT_VALUE_FILTER_BEFORE_TIME_STAMP_BASE_ON_DATA_POINT_ID, 
				new Object[]{dataPointId,time},1);
		if (lst != null && lst.size() > 0) {
			return lst.get(0).getPointValue();
		} else {
			return null;
		}
	}

	public PointValueTime getPointValueAt(int dataPointId, long time) {
		List<PointValue> lst = pointValueDAO.filtered(
				PointValueDAO.POINT_VALUE_FILTER_AT_TIME_STAMP_BASE_ON_DATA_POINT_ID, 
				new Object[]{dataPointId,time},1);
		if (lst != null && lst.size() > 0) {
			return lst.get(0).getPointValue();
		} else {
			return null;
		}
	}
	
	// The same From TO simple get From To and run on every record;
	/*public void getPointValuesBetween(int dataPointId, long from, long to,
			MappedRowCallback<PointValueTime> callback) {
		query(POINT_VALUE_SELECT
				+ " where pv.dataPointId=? and pv.ts >= ? and pv.ts<? order by ts",
				new Object[] { dataPointId, from, to },
				new PointValueRowMapper(), callback);
	}*/

	//TODO rewrite
	private List<PointValueTime> getLstPointValueTime( List<PointValue> lstIn ) {
		List<PointValueTime> lst = new ArrayList<PointValueTime>();
		
		for (PointValue pv: lstIn) {
			lst.add(pv.getPointValue());
		}
		return lst;
	}

	@Override
	public long getInceptionDate(int dataPointId) {
		return pointValueDAO.getInceptionDate(dataPointId);
	}

	@Override
	public long dateRangeCount(int dataPointId, long from, long to) {
		return pointValueDAO.dateRangeCount(dataPointId,from,to);
	}

	@Override
	public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
		return pointValueDAO.getStartAndEndTime(dataPointIds);
	}

	@Override
	public long getStartTime(List<Integer> dataPointIds) {
		return pointValueDAO.getStartTime(dataPointIds);
	}
	
	@Override
	public long getEndTime(List<Integer> dataPointIds) {
		return pointValueDAO.getEndTime(dataPointIds);
	}

	@Override
	public List<Long> getFiledataIds() {
		return pointValueDAO.getFiledataIds();
	}

}
