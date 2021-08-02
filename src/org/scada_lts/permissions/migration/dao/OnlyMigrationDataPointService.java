/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.permissions.migration.dao;


import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;

import org.quartz.SchedulerException;
import org.scada_lts.cache.EventDetectorsCache;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.*;
import org.scada_lts.mango.adapter.MangoDataPoint;


import java.io.IOException;
import java.util.*;

public class OnlyMigrationDataPointService implements MangoDataPoint {

	private DataPointDAO dataPointDAO;

	private UserCommentDAO userCommentDAO;

	private PointEventDetectorDAO pointEventDetectorDAO;

	public OnlyMigrationDataPointService(DataPointDAO dataPointDAO,
										 UserCommentDAO userCommentDAO,
										 PointEventDetectorDAO pointEventDetectorDAO) {
		this.dataPointDAO = dataPointDAO;
		this.userCommentDAO = userCommentDAO;
		this.pointEventDetectorDAO = pointEventDetectorDAO;
	}

	@Override
	public DataPointVO getDataPoint(String xId) {
		DataPointVO dp = dataPointDAO.getDataPoint(xId);
		setRelationalData(dp);
		return dp;
	}
	@Override
	public DataPointVO getDataPointByXid(String xid) {
		DataPointVO dp = dataPointDAO.getDataPoint(xid);
		return dp;
	}
	@Override
	public DataPointVO getDataPoint(int id) {
		DataPointVO dp = dataPointDAO.getDataPoint(id);
		setRelationalData(dp);
		return dp;
	}

	private void setRelationalData(DataPointVO dp) {
		if (dp != null) {
			setEventDetectors(dp);
			setPointComments(dp);
		}
	}

	private void setEventDetectors(DataPointVO dataPoint) {
		dataPoint.setEventDetectors(getEventDetectors(dataPoint));
	}

	public List<PointEventDetectorVO> getEventDetectors(DataPointVO dataPoint) {

		EventDetectorsCache.LOG.trace("getEventDetectors() dpId:" + dataPoint.getId());
		long startTime = 0;
		if (EventDetectorsCache.LOG.isTraceEnabled()) {
			startTime = System.currentTimeMillis();
		}

		List<PointEventDetectorVO> result = null;
		try {
			boolean cacheEnable = ScadaConfig.getInstance().getBoolean(ScadaConfig.ENABLE_CACHE, false);
			if (cacheEnable) {
				result = EventDetectorsCache.getInstance().getEventDetectors(dataPoint);
			} else {
				result = pointEventDetectorDAO.getPointEventDetectors(dataPoint);
			}
		} catch (SchedulerException | IOException e) {
			EventDetectorsCache.LOG.error(e);
		}

		long endTime = 0;
		if (EventDetectorsCache.LOG.isTraceEnabled()) {
			endTime = System.currentTimeMillis();
		}
		EventDetectorsCache.LOG.trace("TimeExecute:"+(endTime-startTime)+ " getEventDetectors() dpId:"+dataPoint.getId());

		return result;
	}

	private void setPointComments(DataPointVO dp) {
		dp.setComments(userCommentDAO.getPointComments(dp));
	}

	@Override
	public String generateUniqueXid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isXidUnique(String xid, int excludeId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getExtendedPointName(int dataPointId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DataPointVO> getDataPoints(Comparator<DataPointVO> comparator, boolean includeRelationalData) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DataPointVO> getDataPoints(int dataSourceId, Comparator<DataPointVO> comparator) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveDataPoint(DataPointVO dp) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insertDataPoint(DataPointVO dp) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateDataPoint(DataPointVO dp) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateDataPointShallow(DataPointVO dp) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDataPoints(int dataSourceId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDataPoint(int dataPointId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deletePointHistory(int dataPointId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deletePointHistory(int dataPointId, long min, long max) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDataPointImpl(String dataPointIdList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getDataPointIdFromDetectorId(int pedId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDetectorXid(int pedId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getDetectorId(String pedXid, int dataPointId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String generateEventDetectorUniqueXid(int dataPointId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEventDetectorXidUnique(int dataPointId, String xid, int excludeId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void copyPermissions(int fromDataPointId, int toDataPointId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PointHierarchy getPointHierarchy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void savePointHierarchy(PointFolder root) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void savePointFolder(PointFolder folder, int parentId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void savePointsInFolder(PointFolder folder) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<PointHistoryCount> getTopPointHistoryCounts() {
		throw new UnsupportedOperationException();
	}
}
