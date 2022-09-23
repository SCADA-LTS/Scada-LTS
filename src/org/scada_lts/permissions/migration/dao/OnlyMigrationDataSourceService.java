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


import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.mango.adapter.MangoDataSource;

import java.util.List;
import java.util.ResourceBundle;

public final class OnlyMigrationDataSourceService implements MangoDataSource {

	private DataSourceDAO dataSourceDAO;

	public OnlyMigrationDataSourceService(DataSourceDAO dataSourceDAO) {
		this.dataSourceDAO = dataSourceDAO;
	}

	@Override
	public List<DataSourceVO<?>> getDataSources() {
		return dataSourceDAO.getDataSources();
	}

	@Override
	public DataSourceVO<?> getDataSource(int id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataSourceVO<?> getDataSource(String xid) {
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
	public void saveDataSource(final DataSourceVO<?> dataSource) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDataSource(final int dataSourceId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int copyDataSource(final int dataSourceId, final ResourceBundle bundle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DataSourceVO<?>> getDataSources(DataSourceVO.Type type) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	public List<Integer> getDataSourceId(int userId) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	public void deleteDataSourceUser(int userId) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	public void insertPermissions(User user) {
		throw new UnsupportedOperationException();
	}
}
