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
package org.scada_lts.mango.service;

import com.serotonin.db.spring.GenericResultSetExtractor;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.dao.MaintenanceEventDAO;
import org.scada_lts.ds.state.UserCpChangeEnableStateDs;
import org.scada_lts.mango.adapter.MangoDataSource;
import org.scada_lts.mango.adapter.MangoPointHierarchy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.ResourceBundle;

import static org.scada_lts.mango.service.DataSourceService_Sql_Commands.DATASOURCES_SELECT;
import static org.scada_lts.mango.service.DataSourceService_Sql_Commands.DATASOURCES_UPDATE;

/**
 * Service for DataSourceDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class DataSourceService implements MangoDataSource {

	@Override
	public Object getPersistentData(int id) {

		return DAO.getInstance().getJdbcTemp().	query(DATASOURCES_SELECT,
				  new Object[] {
		id
	},
			new GenericResultSetExtractor<Serializable>()

	{
		@Override
		public Serializable extractData (ResultSet rs)
							throws SQLException, DataAccessException {
		if (!rs.next())
			return null;

		InputStream is;

		if (Common.getEnvironmentProfile().getString("db.type")
				.equals("postgres")) {
			Blob blob = rs.getBlob(1);
			is = blob.getBinaryStream();
			if (blob == null)
				return null;
		} else {
			is = rs.getBinaryStream(1);
			if (is == null)
				return null;
		}

		return (Serializable) SerializationHelper
				.readObjectInContext(is);
	}
	});
}
	public void savePersistentData(int id, Object data) {
		DAO.getInstance().getJdbcTemp().update(
				DATASOURCES_UPDATE,
				new Object[] { SerializationHelper.writeObject(data), id },
				new int[] {
						Common.getEnvironmentProfile().getString("db.type")
								.equals("postgres") ? Types.BINARY : Types.BLOB,
						Types.INTEGER });
	}
	//TODO spring
	private static final DataSourceDAO dataSourceDAO = new DataSourceDAO();

	private static final DataPointService dataPointService = new DataPointService();

	@Override
	public List<DataSourceVO<?>> getDataSources() {
		return dataSourceDAO.getDataSources();
	}

	@Override
	public DataSourceVO<?> getDataSource(int id) {
		try {
			return dataSourceDAO.getDataSource(id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public DataSourceVO<?> getDataSource(String xid) {
		return dataSourceDAO.getDataSource(xid);
	}

	@Override
	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(DataSourceVO.XID_PREFIX, "dataSources");
	}

	@Override
	public boolean isXidUnique(String xid, int excludeId) {
		return DAO.getInstance().isXidUnique(xid, excludeId, "dataSources");
	}

	@Override
	public void saveDataSource(final DataSourceVO<?> dataSource) {
		if (dataSource.getId() == Common.NEW_ID) {
			dataSource.setId(dataSourceDAO.insert(dataSource));
		} else {
			updateDataSource(dataSource);
			MangoPointHierarchy.getInst().changeDataSource(dataSource);
		}
	}

	private void updateDataSource(DataSourceVO<?> dataSource) {
		DataSourceVO<?> oldDataSource = dataSourceDAO.getDataSource(dataSource.getId());
		dataSourceDAO.update(dataSource);

		// if datasource's name has changed, update datapoints
		if (!dataSource.getName().equals(oldDataSource.getName())) {
			List<DataPointVO> dataPointList = dataPointService.getDataPoints(dataSource.getId(), null);
			for (DataPointVO dataPoint : dataPointList) {
				dataPoint.setDataSourceName(dataPoint.getName());
				dataPoint.setDeviceName(dataPoint.getName());
				dataPointService.updateDataPoint(dataPoint);
			}
		}
	}

	@Override
	public void deleteDataSource(final int dataSourceId) {
		DataSourceVO<?> dataSource = dataSourceDAO.getDataSource(dataSourceId);
		dataPointService.deleteDataPoints(dataSourceId);

		if (dataSource != null) {
			deleteInTransaction(dataSourceId);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	private void deleteInTransaction(final int dataSourceId) {
		new MaintenanceEventDAO().deleteMaintenanceEventsForDataSource(dataSourceId);
		dataSourceDAO.delete(dataSourceId);
	}

	private void copyPermissions(final int fromDataSourceId, final int toDataSourceId) {
		List<Integer> userIDs = dataSourceDAO.getDataSourceUsersId(fromDataSourceId);
		dataSourceDAO.batchInsert(userIDs, toDataSourceId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public int copyDataSource(final int dataSourceId, final ResourceBundle bundle) {
		DataSourceVO<?> dataSource = dataSourceDAO.getDataSource(dataSourceId);

		//Copy the data source
		DataSourceVO<?> dataSourceCopy = dataSource.copy();
		dataSourceCopy.setId(Common.NEW_ID);
		dataSourceCopy.setXid(generateUniqueXid());
		dataSourceCopy.setEnabled(false);
		dataSourceCopy.setState(new UserCpChangeEnableStateDs());

		//TODO seroUtils
		dataSourceCopy.setName(StringUtils.truncate(LocalizableMessage.getMessage(bundle, "common.copyPrefix", dataSource.getName()), 40));

		saveDataSource(dataSourceCopy);

		//Copy permissions
		copyPermissions(dataSource.getId(), dataSourceCopy.getId());

		//Copy points
		for (DataPointVO dataPoint: dataPointService.getDataPoints(dataSourceId, null)) {
			DataPointVO dataPointCopy = dataPoint.copy();
			dataPointCopy.setId(Common.NEW_ID);
			dataPointCopy.setXid(new DataPointService().generateUniqueXid());
			dataPointCopy.setName(dataPoint.getName());
			dataPointCopy.setDataSourceId(dataSourceCopy.getId());
			dataPointCopy.setDataSourceName(dataSourceCopy.getName());
			dataPointCopy.setDeviceName(dataSourceCopy.getName());
			dataPointCopy.setEnabled(dataSourceCopy.isEnabled());
			dataPointCopy.getComments().clear();

			//Copy event detectors
			for (PointEventDetectorVO pointEventDetector: dataPointCopy.getEventDetectors()) {
				pointEventDetector.setId(Common.NEW_ID);
				pointEventDetector.njbSetDataPoint(dataPointCopy);
			}
			dataPointService.saveDataPoint(dataPointCopy);

			//Copy permissions
			dataPointService.copyPermissions(dataPoint.getId(), dataPointCopy.getId());
		}
		return dataSourceCopy.getId();
	}

	public List<Integer> getDataSourceId(int userId) {
		return dataSourceDAO.getDataSourceIdFromDsUsers(userId);
	}

	public void deleteDataSourceUser(int userId) {
		dataSourceDAO.deleteDataSourceUser(userId);
	}

	public void insertPermissions(User user) {
		dataSourceDAO.insertPermissions(user);
	}
}
