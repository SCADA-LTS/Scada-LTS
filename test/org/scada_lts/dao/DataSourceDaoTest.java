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


import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.scada_lts.dao.impl.DataSourceDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;

/**
 * Test DataSourceDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class DataSourceDaoTest extends TestDAO {

	private static final String XID = "fXid";
	private static final String NAME = "fName";
	private static final boolean ENABLE = false;

	private static final String SECOND_XID = "sXid";
	private static final String SECOND_NAME = "sName";
	private static final boolean SECOND_ENABLE = true;

	private static final String UPDATE_XID = "uXid";
	private static final String UPDATE_NAME = "uName";
	private static final boolean UPDATE_ENABLE = true;

	private static int LIST_SIZE = 2;

	private DataSourceDAO dataSourceDAO = new DataSourceDAO();

	@Test
	public void testDataSource() {
		DataSourceVO dataSource = new VirtualDataSourceVO();
		dataSource.setXid(XID);
		dataSource.setName(NAME);
		dataSource.setEnabled(ENABLE);

		DataSourceVO secondDataSource = new VirtualDataSourceVO();
		secondDataSource.setXid(SECOND_XID);
		secondDataSource.setName(SECOND_NAME);
		secondDataSource.setEnabled(SECOND_ENABLE);

		//Insert objects
		int firstId = dataSourceDAO.insert(dataSource);
		int secondId = dataSourceDAO.insert(secondDataSource);
		dataSource.setId(firstId);
		secondDataSource.setId(secondId);

		//Select single object
		DataSourceVO dataSourceSelectId = dataSourceDAO.getDataSource(firstId);
		assertTrue(dataSourceSelectId.getXid().equals(dataSource.getXid()));
		assertTrue(dataSourceSelectId.getName().equals(dataSource.getName()));
		assertTrue(dataSourceSelectId.isEnabled() == dataSource.isEnabled());

		DataSourceVO dataSourceSelectXid = dataSourceDAO.getDataSource(secondId);
		assertTrue(dataSourceSelectXid.getXid().equals(secondDataSource.getXid()));
		assertTrue(dataSourceSelectXid.getName().equals(secondDataSource.getName()));
		assertTrue(dataSourceSelectXid.isEnabled() == secondDataSource.isEnabled());

		//Select all objects
		List<DataSourceVO<?>> dataSourceList = dataSourceDAO.getDataSources();
		//Check list size
		assertTrue(dataSourceList.size() == LIST_SIZE);
		//Check IDs
		assertTrue(dataSourceList.get(0).getId() == dataSource.getId());
		assertTrue(dataSourceList.get(1).getId() == secondDataSource.getId());

		//Update
		DataSourceVO dataSourceUpdate = new VirtualDataSourceVO();
		dataSourceUpdate.setId(dataSource.getId());
		dataSourceUpdate.setXid(UPDATE_XID);
		dataSourceUpdate.setName(UPDATE_NAME);
		dataSourceUpdate.setEnabled(UPDATE_ENABLE);

		dataSourceDAO.update(dataSourceUpdate);
		DataSourceVO dataSourceSelectUpdate = dataSourceDAO.getDataSource(dataSource.getId());
		assertTrue(dataSourceSelectUpdate.getId() == dataSource.getId());
		assertTrue(dataSourceSelectUpdate.getXid().equals(UPDATE_XID));
		assertTrue(dataSourceSelectUpdate.getName().equals(UPDATE_NAME));
		assertTrue(dataSourceSelectUpdate.isEnabled() == UPDATE_ENABLE);

		//Delete
		dataSourceDAO.delete(firstId);
		try{
			dataSourceDAO.getDataSource(firstId);
		} catch(Exception e){
			assertTrue(e.getClass().equals(EmptyResultDataAccessException.class));
			assertTrue(e.getMessage().equals("Incorrect result size: expected 1, actual 0"));
		}
	}

	/*@Test
	public void testDataSourceUser() {
		DAO.getInstance().getJdbcTemp().update("insert into users (username, password, email, phone, admin, disabled, receiveAlarmEmails, receiveOwnAuditEvents) values ('fN','fP','fMail',9123,false,false,0,true)");
		DAO.getInstance().getJdbcTemp().update("insert into users (username, password, email, phone, admin, disabled, receiveAlarmEmails, receiveOwnAuditEvents) values ('sN','sP','sMail',1293,false,true,0,true)");

		DataSourceVO dataSource = new VirtualDataSourceVO();
		dataSource.setXid(XID);
		dataSource.setName(NAME);
		dataSource.setEnabled(ENABLE);

		int firstId = dataSourceDAO.insert(dataSource);

		List<Integer> userIds = IntStream.range(0, 3).map(i -> i + 1).boxed().collect(Collectors.toList());

		//Insert dataSourceUsers
		dataSourceDAO.batchInsert(userIds, firstId);

		//Select dataSourceUsers
		List<Integer> userIdsFromDb = dataSourceDAO.getDataSourceUsersId(firstId);
		//Check size
		assertTrue(userIds.size() == userIdsFromDb.size());
		//Check elements
		assertTrue(userIdsFromDb.containsAll(userIds));
	}*/
}
