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
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;

import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.mango.adapter.MangoDataPoint;

import java.util.*;

public final class OnlyMigrationDataPointService implements MangoDataPoint {

	private DataPointDAO dataPointDAO;

	public OnlyMigrationDataPointService(DataPointDAO dataPointDAO) {
		this.dataPointDAO = dataPointDAO;
	}

	@Override
	public DataPointVO getDataPoint(String xId) {
		throw new UnsupportedOperationException();
	}
	@Override
	public DataPointVO getDataPointByXid(String xid) {
		throw new UnsupportedOperationException();
	}
	@Override
	public DataPointVO getDataPoint(int id) {
		throw new UnsupportedOperationException();
	}

	private void setRelationalData(List<DataPointVO> dpList) {
		throw new UnsupportedOperationException();
	}

	public List<PointEventDetectorVO> getEventDetectors(DataPointVO dataPoint) {
		throw new UnsupportedOperationException();
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
		List<DataPointVO> dpList = dataPointDAO.getDataPoints();
		if (includeRelationalData) {
			setRelationalData(dpList);
		}
		if (comparator != null) {
			Collections.sort(dpList, comparator);
		}
		return dpList;
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

	@Override
	public List<DataPointVO> searchDataPointsBy(String searchText) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DataPointVO> getDataPointsWithAccess(User user) {
		throw new UnsupportedOperationException();
	}
}
