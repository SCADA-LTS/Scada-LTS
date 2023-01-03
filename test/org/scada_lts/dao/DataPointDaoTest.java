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
import org.junit.Test;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.impl.DataPointDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test DataPointDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class DataPointDaoTest extends TestDAO {

	private static final String XID = "dp xid";
	private static final int DATA_SOURCE_ID = 1;
	private static final String DATA_SOURCE_NAME = "1name";
	private static final String DATA_SOURCE_XID = "1DsXid";
	private static final int DATA_SOURCE_TYPE_ID = 1;

	private static final String SECOND_XID = "secXid";
	private static final int SECOND_DATA_SOURCE_ID = 1;
	private static final String SECOND_DATA_SOURCE_NAME = "sec 1n";
	private static final String SECOND_DATA_SOURCE_XID = "sec1DsXid";
	private static final int SECOND_DATA_SOURCE_TYPE_ID = 2;

	private static final String UPDATE_XID = "UPxid";

	private static final int LIST_SIZE = 2;

	@Test
	public void test() {

		//TODO It is necessary to insert DataSource object before insert DataPoint object
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (xid, name, dataSourceType, data) values ('x1', 'dataName', 1, 0);");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (xid, name, dataSourceType, data) values ('x24ll', 'dataName', 2, 0);");

		DataPointVO dataPoint = new DataPointVO(LoggingTypes.ON_CHANGE);
		dataPoint.setXid(XID);
		dataPoint.setDataSourceId(DATA_SOURCE_ID);
		dataPoint.setDataSourceName(DATA_SOURCE_NAME);
		dataPoint.setDataSourceXid(DATA_SOURCE_XID);
		dataPoint.setDataSourceTypeId(DATA_SOURCE_TYPE_ID);

		DataPointVO secondDataPoint = new DataPointVO(LoggingTypes.ON_CHANGE);
		secondDataPoint.setXid(SECOND_XID);
		secondDataPoint.setDataSourceId(SECOND_DATA_SOURCE_ID);
		secondDataPoint.setDataSourceName(SECOND_DATA_SOURCE_NAME);
		secondDataPoint.setDataSourceXid(SECOND_DATA_SOURCE_XID);
		secondDataPoint.setDataSourceTypeId(SECOND_DATA_SOURCE_TYPE_ID);

		DataPointDAO dataPointDAO = new DataPointDAO();

		//Insert
		int firstId = dataPointDAO.insert(dataPoint);
		int secondId = dataPointDAO.insert(secondDataPoint);
		dataPoint.setId(firstId);
		secondDataPoint.setId(secondId);

		//Select single object by id
		DataPointVO dataPointSelectId = dataPointDAO.getDataPoint(firstId);
		assertTrue(dataPointSelectId.getId() == firstId);
		assertTrue(dataPointSelectId.getXid().equals(XID));
		assertTrue(dataPointSelectId.getDataSourceId() == DATA_SOURCE_ID);

		//Select single object by xid
		DataPointVO dataPointSelectXid = dataPointDAO.getDataPoint(secondDataPoint.getXid());
		assertTrue(dataPointSelectXid.getId() == secondId);
		assertTrue(dataPointSelectXid.getXid().equals(SECOND_XID));
		assertTrue(dataPointSelectXid.getDataSourceId() == SECOND_DATA_SOURCE_ID);

		//Select all objects
		List<DataPointVO> dataPointList = dataPointDAO.getDataPoints();
		//Check list size
		assertTrue(dataPointList.size() == LIST_SIZE);
		//Check IDs
		assertTrue(dataPointList.get(0).getId() == firstId);
		assertTrue(dataPointList.get(1).getId() == secondId);

		//Update
		DataPointVO dataPointUpdate = new DataPointVO(LoggingTypes.ON_CHANGE);
		dataPointUpdate.setId(firstId);
		dataPointUpdate.setXid(UPDATE_XID);

		dataPointDAO.update(dataPointUpdate);
		dataPointUpdate = dataPointDAO.getDataPoint(firstId);
		assertTrue(dataPointUpdate.getId() == firstId);
		assertTrue(dataPointUpdate.getXid().equals(UPDATE_XID));

		//Delete
		dataPointDAO.delete(firstId);
		try{
			dataPointDAO.getDataPoint(firstId);
		} catch(Exception e){
			assertTrue(e.getClass().equals(EmptyResultDataAccessException.class));
			assertTrue(e.getMessage().equals("Incorrect result size: expected 1, actual 0"));
		}
	}
}
