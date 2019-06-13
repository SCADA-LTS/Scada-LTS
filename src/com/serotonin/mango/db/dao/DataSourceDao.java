/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db.dao;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.mango.adapter.MangoDataSource;
import org.scada_lts.mango.adapter.MangoPointHierarchy;
import org.scada_lts.mango.adapter.MangoPointHierarchyCacheable;
import org.scada_lts.mango.service.DataSourceService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.db.spring.GenericResultSetExtractor;
import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;

//public class DataSourceDao extends BaseDao {
public class DataSourceDao {

	private MangoDataSource dataSourceService = new DataSourceService();

//	private static final String DATA_SOURCE_SELECT = "select id, xid, name, data from dataSources ";
//	private final Log LOG = LogFactory.getLog(DataSourceDao.class);

	public List<DataSourceVO<?>> getDataSources() {
//		List<DataSourceVO<?>> dss = query(DATA_SOURCE_SELECT,
//				new DataSourceRowMapper());
//		Collections.sort(dss, new DataSourceNameComparator());
//		return dss;
		return dataSourceService.getDataSources();
	}

//	static class DataSourceNameComparator implements
//			Comparator<DataSourceVO<?>> {
//		public int compare(DataSourceVO<?> ds1, DataSourceVO<?> ds2) {
//			if (StringUtils.isEmpty(ds1.getName()))
//				return -1;
//			return ds1.getName().compareToIgnoreCase(ds2.getName());
//		}
//	}

	public DataSourceVO<?> getDataSource(int id) {
//		return queryForObject(DATA_SOURCE_SELECT + " where id=?",
//				new Object[] { id }, new DataSourceRowMapper(), null);
		return dataSourceService.getDataSource(id);
	}

	public DataSourceVO<?> getDataSource(String xid) {
//		return queryForObject(DATA_SOURCE_SELECT + " where xid=?",
//				new Object[] { xid }, new DataSourceRowMapper(), null);
		return dataSourceService.getDataSource(xid);
	}

//	class DataSourceRowMapper implements GenericRowMapper<DataSourceVO<?>> {
//		public DataSourceVO<?> mapRow(ResultSet rs, int rowNum)
//				throws SQLException {
//			DataSourceVO<?> ds;
//			if (Common.getEnvironmentProfile().getString("db.type")
//					.equals("postgres")) {
//				ds = (DataSourceVO<?>) SerializationHelper.readObject(rs
//						.getBinaryStream(4));
//			} else {
//				ds = (DataSourceVO<?>) SerializationHelper.readObject(rs
//						.getBlob(4).getBinaryStream());
//			}
//			ds.setId(rs.getInt(1));
//			ds.setXid(rs.getString(2));
//			ds.setName(rs.getString(3));
//			return ds;
//		}
//	}

	public String generateUniqueXid() {
//		return generateUniqueXid(DataSourceVO.XID_PREFIX, "dataSources");
		return dataSourceService.generateUniqueXid();
	}

	public boolean isXidUnique(String xid, int excludeId) {
//		return isXidUnique(xid, excludeId, "dataSources");
		return dataSourceService.isXidUnique(xid, excludeId);
	}

	public void saveDataSource(final DataSourceVO<?> vo) {
//		// Decide whether to insert or update.
//		if (vo.getId() == Common.NEW_ID) {
//			insertDataSource(vo);
//		} else {
//		  updateDataSource(vo);
//		  MangoPointHierarchy.getInst().changeDataSource(vo);
//		}
		dataSourceService.saveDataSource(vo);
	}

//	private void insertDataSource(final DataSourceVO<?> vo) {
//		if (Common.getEnvironmentProfile().getString("db.type")
//				.equals("postgres")) {
//			try {
//				// id = doInsert(EVENT_INSERT, args, EVENT_INSERT_TYPES);
//				Connection conn = DriverManager
//						.getConnection(
//								Common.getEnvironmentProfile().getString(
//										"db.url"),
//								Common.getEnvironmentProfile().getString(
//										"db.username"),
//								Common.getEnvironmentProfile().getString(
//										"db.password"));
//				PreparedStatement preStmt = conn
//						.prepareStatement("insert into dataSources (xid, name, dataSourceType, data) values (?,?,?,?)");
//				preStmt.setString(1, vo.getXid());
//				preStmt.setString(2, vo.getName());
//				preStmt.setInt(3, vo.getType().getId());
//				preStmt.setBytes(4, SerializationHelper.writeObjectToArray(vo));
//				preStmt.executeUpdate();
//
//				ResultSet resSEQ = conn.createStatement().executeQuery(
//						"SELECT currval('datasources_id_seq')");
//				resSEQ.next();
//				int id = resSEQ.getInt(1);
//
//				conn.close();
//
//				vo.setId(id);
//
//			} catch (SQLException ex) {
//				ex.printStackTrace();
//				vo.setId(0);
//			}
//		} else {
//			vo.setId(doInsert(
//					"insert into dataSources (xid, name, dataSourceType, data) values (?,?,?,?)",
//					new Object[] { vo.getXid(), vo.getName(),
//							vo.getType().getId(),
//							SerializationHelper.writeObject(vo) }, new int[] {
//							Types.VARCHAR,
//							Types.VARCHAR,
//							Types.INTEGER,
//							Common.getEnvironmentProfile().getString("db.type")
//									.equals("postgres") ? Types.BINARY
//									: Types.BLOB }));
//		}
//
//		AuditEventType.raiseAddedEvent(AuditEventType.TYPE_DATA_SOURCE, vo);
//	}

//	@SuppressWarnings("unchecked")
//	private void updateDataSource(final DataSourceVO<?> vo) {
//		DataSourceVO<?> old = getDataSource(vo.getId());
//		ejt.update(
//				"update dataSources set xid=?, name=?, data=? where id=?",
//				new Object[] { vo.getXid(), vo.getName(),
//						SerializationHelper.writeObject(vo), vo.getId() },
//				new int[] {
//						Types.VARCHAR,
//						Types.VARCHAR,
//						Common.getEnvironmentProfile().getString("db.type")
//								.equals("postgres") ? Types.BINARY : Types.BLOB,
//						Types.INTEGER });
//		LOG.debug("Start updating DS!");
//		// if datasource's name has changed, update datapoints
//		if (!vo.getName().equals(old.getName())) {
//			List<DataPointVO> dpList = new DataPointDao().getDataPoints(
//					vo.getId(), null);
//			for (DataPointVO dp : dpList) {
//				LOG.debug("Updating DP: " + dp.getName());
//				dp.setDataSourceName(vo.getName());
//				dp.setDeviceName(vo.getName());
//				new DataPointDao().updateDataPoint(dp);
//			}
//
//		}
//
//		AuditEventType.raiseChangedEvent(AuditEventType.TYPE_DATA_SOURCE, old,
//				(ChangeComparable<DataSourceVO<?>>) vo);
//	}

	public void deleteDataSource(final int dataSourceId) {
//		DataSourceVO<?> vo = getDataSource(dataSourceId);
//		final ExtendedJdbcTemplate ejt2 = ejt;
//
//		new DataPointDao().deleteDataPoints(dataSourceId);
//
//		if (vo != null) {
//			getTransactionTemplate().execute(
//					new TransactionCallbackWithoutResult() {
//						@Override
//						protected void doInTransactionWithoutResult(
//								TransactionStatus status) {
//							new MaintenanceEventDao()
//									.deleteMaintenanceEventsForDataSource(dataSourceId);
//							ejt2.update(
//									"delete from eventHandlers where eventTypeId="
//											+ EventType.EventSources.DATA_SOURCE
//											+ " and eventTypeRef1=?",
//									new Object[] { dataSourceId });
//							ejt2.update(
//									"delete from dataSourceUsers where dataSourceId=?",
//									new Object[] { dataSourceId });
//							ejt2.update("delete from dataSources where id=?",
//									new Object[] { dataSourceId });
//						}
//					});
//
//			AuditEventType.raiseDeletedEvent(AuditEventType.TYPE_DATA_SOURCE,
//					vo);
//		}
		dataSourceService.deleteDataSource(dataSourceId);
	}

//	public void copyPermissions(final int fromDataSourceId,
//			final int toDataSourceId) {
//		final List<Integer> userIds = queryForList(
//				"select userId from dataSourceUsers where dataSourceId=?",
//				new Object[] { fromDataSourceId }, Integer.class);
//
//		ejt.batchUpdate("insert into dataSourceUsers values (?,?)",
//				new BatchPreparedStatementSetter() {
//					@Override
//					public int getBatchSize() {
//						return userIds.size();
//					}
//
//					@Override
//					public void setValues(PreparedStatement ps, int i)
//							throws SQLException {
//						ps.setInt(1, toDataSourceId);
//						ps.setInt(2, userIds.get(i));
//					}
//				});
//	}

	public int copyDataSource(final int dataSourceId, final ResourceBundle bundle) {
//		return getTransactionTemplate().execute(
//				new TransactionCallback<Integer>() {
//					@Override
//					public Integer doInTransaction(TransactionStatus status) {
//						DataPointDao dataPointDao = new DataPointDao();
//
//						DataSourceVO<?> dataSource = getDataSource(dataSourceId);
//
//						// Copy the data source.
//						DataSourceVO<?> dataSourceCopy = dataSource.copy();
//						dataSourceCopy.setId(Common.NEW_ID);
//						dataSourceCopy.setXid(generateUniqueXid());
//						dataSourceCopy.setEnabled(false);
//						dataSourceCopy.setName(StringUtils.truncate(
//								LocalizableMessage.getMessage(bundle,
//										"common.copyPrefix",
//										dataSource.getName()), 40));
//						saveDataSource(dataSourceCopy);
//
//						// Copy permissions.
//						copyPermissions(dataSource.getId(),
//								dataSourceCopy.getId());
//
//						// Copy the points.
//						for (DataPointVO dataPoint : dataPointDao
//								.getDataPoints(dataSourceId, null)) {
//							DataPointVO dataPointCopy = dataPoint.copy();
//							dataPointCopy.setId(Common.NEW_ID);
//							dataPointCopy.setXid(dataPointDao
//									.generateUniqueXid());
//							dataPointCopy.setName(dataPoint.getName());
//							dataPointCopy.setDataSourceId(dataSourceCopy
//									.getId());
//							dataPointCopy.setDataSourceName(dataSourceCopy
//									.getName());
//							dataPointCopy.setDeviceName(dataSourceCopy
//									.getName());
//							dataPointCopy.setEnabled(dataPoint.isEnabled());
//							dataPointCopy.getComments().clear();
//
//							// Copy the event detectors
//							for (PointEventDetectorVO ped : dataPointCopy
//									.getEventDetectors()) {
//								ped.setId(Common.NEW_ID);
//								ped.njbSetDataPoint(dataPointCopy);
//							}
//
//							dataPointDao.saveDataPoint(dataPointCopy);
//
//							// Copy permissions
//							dataPointDao.copyPermissions(dataPoint.getId(),
//									dataPointCopy.getId());
//						}
//
//						return dataSourceCopy.getId();
//					}
//				});
		return dataSourceService.copyDataSource(dataSourceId, bundle);
	}

	public Object getPersistentData(int id) {
		return DAO.getInstance().getJdbcTemp().query("select rtdata from dataSources where id=?",
				new Object[] { id },
				new GenericResultSetExtractor<Serializable>() {
					@Override
					public Serializable extractData(ResultSet rs)
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
				"update dataSources set rtdata=? where id=?",
				new Object[] { SerializationHelper.writeObject(data), id },
				new int[] {
						Common.getEnvironmentProfile().getString("db.type")
								.equals("postgres") ? Types.BINARY : Types.BLOB,
						Types.INTEGER });
	}
}
