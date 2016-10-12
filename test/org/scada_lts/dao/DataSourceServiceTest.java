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

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import org.junit.Test;
import org.scada_lts.mango.service.DataSourceService;

import java.util.List;
import java.util.ResourceBundle;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test DataSourceService
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class DataSourceServiceTest extends TestDAO {

	private static final int ID = -1;
	private static final String XID = "fXid";
	private static final String NAME = "fName";
	private static final boolean ENABLE = false;

	private static final String UPDATE_XID = "uXid";
	private static final String UPDATE_NAME = "uName";
	private static final boolean UPDATE_ENABLE = true;

	private DataSourceService dataSourceService = new DataSourceService();

	@Test
	public void testCRUD() {
		//Insert
		DataSourceVO dataSource = new VirtualDataSourceVO();
		dataSource.setId(ID);
		dataSource.setXid(XID);
		dataSource.setName(NAME);
		dataSource.setEnabled(ENABLE);
		dataSourceService.saveDataSource(dataSource);
		dataSource.setId(dataSourceService.getDataSource(XID).getId());
		assertTrue(dataSourceService.getDataSources().size() == 1);

		//Update
		DataSourceVO dataSourceUpdate = new VirtualDataSourceVO();
		dataSourceUpdate.setId(dataSource.getId());
		dataSourceUpdate.setXid(UPDATE_XID);
		dataSourceUpdate.setName(UPDATE_NAME);
		dataSourceUpdate.setEnabled(UPDATE_ENABLE);
		dataSourceService.saveDataSource(dataSourceUpdate);
		assertTrue(dataSourceService.getDataSources().size() == 1);

		dataSourceUpdate = dataSourceService.getDataSource(UPDATE_XID);
		assertTrue(dataSourceUpdate.getId() == dataSource.getId());
		assertTrue(dataSourceUpdate.getName().equals(UPDATE_NAME));
		assertTrue(dataSourceUpdate.isEnabled() == UPDATE_ENABLE);

		//Delete
		dataSourceService.deleteDataSource(dataSourceUpdate.getId());
		assertTrue(dataSourceService.getDataSources().size() == 0);
	}

	@Test
	public void testCopyMethods() {
		DataSourceVO dataSource = new VirtualDataSourceVO();
		dataSource.setXid(XID);
		dataSource.setName(NAME);
		dataSource.setEnabled(ENABLE);
		dataSourceService.saveDataSource(dataSource);

		List<DataSourceVO<?>> dataSourceList = dataSourceService.getDataSources();
		assertTrue(dataSourceList.size() == 1);

		ResourceBundle bundle = mock(ResourceBundle.class);
		when(bundle.getLocale()).thenReturn(null);

		//Copy dataSource
		dataSourceService.copyDataSource(dataSourceList.get(0).getId(), bundle);
		dataSourceList = dataSourceService.getDataSources();
		assertTrue(dataSourceList.size() == 2);
		assertTrue(dataSourceList.get(0).getTypeKey().equals(dataSourceList.get(1).getTypeKey()));
	}
}
