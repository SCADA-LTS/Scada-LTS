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
package org.scada_lts.mango.adapter;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;
import java.util.ResourceBundle;

/**
 * DataSourceService adapter
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public interface MangoDataSource {

	List<DataSourceVO<?>> getDataSources();

	DataSourceVO<?> getDataSource(int id);

	DataSourceVO<?> getDataSource(String xid);

	String generateUniqueXid();

	boolean isXidUnique(String xid, int excludeId);

	void saveDataSource(final DataSourceVO<?> vo);

	void deleteDataSource(final int dataSourceId);

	int copyDataSource(final int dataSourceId, final ResourceBundle bundle);

    List<DataSourceVO<?>> getDataSources(DataSourceVO.Type type);

	List<DataSourceVO<?>> getDataSourcesWithAccess(User user);

	boolean hasDataSourceReadPermission(User user, DataSourceVO<?> dataSource);

	List<DataSourceVO<?>> getDataSourcesPlc(User user);

	boolean toggleDataSource(int id);

	boolean toggleDataSource(String xid);

	List<ScadaObjectIdentifier> getAllDataSources();
}
