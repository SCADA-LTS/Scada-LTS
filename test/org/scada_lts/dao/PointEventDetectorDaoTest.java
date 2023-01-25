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
package org.scada_lts.dao;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test PointEventDetectorDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class PointEventDetectorDaoTest extends TestDAO {

	private static final String XID = "ped xid";
	private static final String ALIAS = "ped alias";
	private static final int DETECTOR_TYPE = 2;
	private static final int ALARM_LEVEL = 3;
	private static final double STATE_LIMIT = 1.5;
	private static final int DURATION = 5;
	private static final int DURATION_TYPE = 1;
	private static final boolean BINARY_STATE = true;
	private static final int MULTISTATE_STATE = 3;
	private static final int CHANGE_COUNT = 2;
	private static final String ALPHANUMERIC_STATE = "ped alphanumericState";
	private static final double WEIGHT = 2.7;

	private static final String SECOND_XID = "sec ped xid";
	private static final String SECOND_ALIAS = "sec ped alias";
	private static final int SECOND_DETECTOR_TYPE = 1;
	private static final int SECOND_ALARM_LEVEL = 1;
	private static final double SECOND_STATE_LIMIT = 1.1;
	private static final int SECOND_DURATION = 7;
	private static final int SECOND_DURATION_TYPE = 2;
	private static final boolean SECOND_BINARY_STATE = true;
	private static final int SECOND_MULTISTATE_STATE = 2;
	private static final int SECOND_CHANGE_COUNT = 1;
	private static final String SECOND_ALPHANUMERIC_STATE = "sec alphanumericState";
	private static final double SECOND_WEIGHT = 5.2;

	private static final String UPDATE_XID = "up ped xid";
	private static final String UPDATE_ALIAS = "up ped alias";
	private static final int UPDATE_ALARM_LEVEL = 2;
	private static final double UPDATE_STATE_LIMIT = 1.7;
	private static final int UPDATE_DURATION = 3;
	private static final int UPDATE_DURATION_TYPE = 4;
	private static final boolean UPDATE_BINARY_STATE = false;
	private static final int UPDATE_MULTISTATE_STATE = 5;
	private static final int UPDATE_CHANGE_COUNT = 6;
	private static final String UPDATE_ALPHANUMERIC_STATE = "up alphanumericState";
	private static final double UPDATE_WEIGHT = 1.2;

	private static final int LIST_SIZE = 2;

	private static final String DP_XID = "1xid";
	private static final int DP_DATA_SOURCE_ID = 1;
	private static final String DP_DATA_SOURCE_NAME = "1name";
	private static final String DP_DATA_SOURCE_XID = "1DsXid";
	private static final int DP_DATA_SOURCE_TYPE_ID = 1;
	@Test
	public void test() {

		//TODO It is necessary to insert DataSource object before insert DataPoint object
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (xid, name, dataSourceType, data) values ('x1', 'dataName', 1, 0);");

		DataPointVO dataPoint = new DataPointVO(LoggingTypes.ON_CHANGE);
		dataPoint.setXid(DP_XID);
		dataPoint.setDataSourceId(DP_DATA_SOURCE_ID);
		dataPoint.setDataSourceName(DP_DATA_SOURCE_NAME);
		dataPoint.setDataSourceXid(DP_DATA_SOURCE_XID);
		dataPoint.setDataSourceTypeId(DP_DATA_SOURCE_TYPE_ID);

		DataPointDAO dataPointDAO = new DataPointDAO();

		//Insert DataPoint
		int firstDpId = dataPointDAO.insert(dataPoint);
		dataPoint.setId(firstDpId);

		PointEventDetectorVO pointEventDetector = new PointEventDetectorVO();
		pointEventDetector.setXid(XID);
		pointEventDetector.setAlias(ALIAS);
		pointEventDetector.njbSetDataPoint(dataPoint);
		pointEventDetector.setDetectorType(DETECTOR_TYPE);
		pointEventDetector.setAlarmLevel(ALARM_LEVEL);
		pointEventDetector.setLimit(STATE_LIMIT);
		pointEventDetector.setDuration(DURATION);
		pointEventDetector.setDurationType(DURATION_TYPE);
		pointEventDetector.setBinaryState(BINARY_STATE);
		pointEventDetector.setMultistateState(MULTISTATE_STATE);
		pointEventDetector.setChangeCount(CHANGE_COUNT);
		pointEventDetector.setAlphanumericState(ALPHANUMERIC_STATE);
		pointEventDetector.setWeight(WEIGHT);

		PointEventDetectorVO secondPointEventDecorator = new PointEventDetectorVO();
		secondPointEventDecorator.setXid(SECOND_XID);
		secondPointEventDecorator.setAlias(SECOND_ALIAS);
		secondPointEventDecorator.njbSetDataPoint(dataPoint);
		secondPointEventDecorator.setDetectorType(SECOND_DETECTOR_TYPE);
		secondPointEventDecorator.setAlarmLevel(SECOND_ALARM_LEVEL);
		secondPointEventDecorator.setLimit(SECOND_STATE_LIMIT);
		secondPointEventDecorator.setDuration(SECOND_DURATION);
		secondPointEventDecorator.setDurationType(SECOND_DURATION_TYPE);
		secondPointEventDecorator.setBinaryState(SECOND_BINARY_STATE);
		secondPointEventDecorator.setMultistateState(SECOND_MULTISTATE_STATE);
		secondPointEventDecorator.setChangeCount(SECOND_CHANGE_COUNT);
		secondPointEventDecorator.setAlphanumericState(SECOND_ALPHANUMERIC_STATE);
		secondPointEventDecorator.setWeight(SECOND_WEIGHT);

		PointEventDetectorDAO pointEventDetectorDAO = new PointEventDetectorDAO();

		//Insert PointEventDetector
		int firstId = pointEventDetectorDAO.insert(dataPoint.getId(), pointEventDetector);
		int secondId = pointEventDetectorDAO.insert(dataPoint.getId(), secondPointEventDecorator);
		pointEventDetector.setId(firstId);
		secondPointEventDecorator.setId(secondId);

		//Update PointEventDetector
		PointEventDetectorVO updatePointEventDecorator = new PointEventDetectorVO();
		updatePointEventDecorator.setId(firstId);
		updatePointEventDecorator.setXid(UPDATE_XID);
		updatePointEventDecorator.setAlias(UPDATE_ALIAS);
		updatePointEventDecorator.njbSetDataPoint(dataPoint);
		updatePointEventDecorator.setAlarmLevel(UPDATE_ALARM_LEVEL);
		updatePointEventDecorator.setLimit(UPDATE_STATE_LIMIT);
		updatePointEventDecorator.setDuration(UPDATE_DURATION);
		updatePointEventDecorator.setDurationType(UPDATE_DURATION_TYPE);
		updatePointEventDecorator.setBinaryState(UPDATE_BINARY_STATE);
		updatePointEventDecorator.setMultistateState(UPDATE_MULTISTATE_STATE);
		updatePointEventDecorator.setChangeCount(UPDATE_CHANGE_COUNT);
		updatePointEventDecorator.setAlphanumericState(UPDATE_ALPHANUMERIC_STATE);
		updatePointEventDecorator.setWeight(UPDATE_WEIGHT);

		pointEventDetectorDAO.update(dataPoint.getId(), updatePointEventDecorator);

		//Select all PointEventDetectors with specific DataPoint
		List<PointEventDetectorVO> pointEventDetectorList = pointEventDetectorDAO.getPointEventDetectors(dataPoint);
		//Check list size
		assertTrue(pointEventDetectorList.size() == LIST_SIZE);
		//Check IDs
		assertTrue(pointEventDetectorList.get(0).getId() == firstId);
		assertTrue(pointEventDetectorList.get(1).getId() == secondId);
		//Check update
		assertTrue(pointEventDetectorList.get(0).getXid().equals(UPDATE_XID));
		assertTrue(pointEventDetectorList.get(0).getAlias().equals(UPDATE_ALIAS));
		assertTrue(pointEventDetectorList.get(0).getAlarmLevel() == UPDATE_ALARM_LEVEL);
		assertTrue(pointEventDetectorList.get(0).getLimit() == UPDATE_STATE_LIMIT);
		assertTrue(pointEventDetectorList.get(0).getDuration() == UPDATE_DURATION);
		assertTrue(pointEventDetectorList.get(0).getDurationType() == UPDATE_DURATION_TYPE);
		assertTrue(pointEventDetectorList.get(0).isBinaryState() == UPDATE_BINARY_STATE);
		assertTrue(pointEventDetectorList.get(0).getMultistateState() == UPDATE_MULTISTATE_STATE);
		assertTrue(pointEventDetectorList.get(0).getChangeCount() == UPDATE_CHANGE_COUNT);
		assertTrue(pointEventDetectorList.get(0).getAlphanumericState().equals(UPDATE_ALPHANUMERIC_STATE));
		assertTrue(pointEventDetectorList.get(0).getWeight() == UPDATE_WEIGHT);

		//Delete all pointEventDetector with specific DataPointId
		pointEventDetectorDAO.delete(dataPoint.getId(), pointEventDetector);
		assertTrue(pointEventDetectorDAO.getPointEventDetectors(dataPoint).size() == 1);
	}
}
