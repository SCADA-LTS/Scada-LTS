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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.Statement;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

/**
 * DAO for PointEventDetector
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
@Repository
public class PointEventDetectorDAO {

	private static final Log LOG = LogFactory.getLog(PointEventDetectorDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_ALIAS = "alias";
	private static final String COLUMN_NAME_DATA_POINT_ID = "dataPointId";
	private static final String COLUMN_NAME_DETECTOR_TYPE = "detectorType";
	private static final String COLUMN_NAME_ALARM_LEVEL = "alarmLevel";
	private static final String COLUMN_NAME_STATE_LIMIT = "stateLimit";
	private static final String COLUMN_NAME_DURATION = "duration";
	private static final String COLUMN_NAME_DURATION_TYPE = "durationType";
	private static final String COLUMN_NAME_BINARY_STATE = "binaryState";
	private static final String COLUMN_NAME_MULTISTATE_STATE = "multistateState";
	private static final String COLUMN_NAME_CHANGE_COUNT = "changeCount";
	private static final String COLUMN_NAME_ALPHANUMERIC_STATE = "alphanumericState";
	private static final String COLUMN_NAME_WEIGHT = "weight";

	private static final String COLUMN_NAME_EH_EVENT_TYPE_ID = "eventTypeId";
	private static final String COLUMN_NAME_EH_EVENT_TYPE_REF1 = "eventTypeRef1";
	private static final String COLUMN_NAME_EH_EVENT_TYPE_REF2 = "eventTypeRef2";

	// @formatter:off
	private static final String POINT_EVENT_DETECTOR_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_ALIAS + ", "
				+ COLUMN_NAME_DETECTOR_TYPE + ", "
				+ COLUMN_NAME_ALARM_LEVEL + ", "
				+ COLUMN_NAME_STATE_LIMIT + ", "
				+ COLUMN_NAME_DURATION + ", "
				+ COLUMN_NAME_DURATION_TYPE + ", "
				+ COLUMN_NAME_BINARY_STATE + ", "
				+ COLUMN_NAME_MULTISTATE_STATE + ", "
				+ COLUMN_NAME_CHANGE_COUNT + ", "
				+ COLUMN_NAME_ALPHANUMERIC_STATE + ", "
				+ COLUMN_NAME_WEIGHT + " "
			+ "from pointEventDetectors ";

	private static final String POINT_EVENT_DETECTOR_INSERT = ""
			+ "insert into pointEventDetectors ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_ALIAS + ", "
				+ COLUMN_NAME_DATA_POINT_ID + ", "
				+ COLUMN_NAME_DETECTOR_TYPE + ", "
				+ COLUMN_NAME_ALARM_LEVEL + ", "
				+ COLUMN_NAME_STATE_LIMIT + ", "
				+ COLUMN_NAME_DURATION + ", "
				+ COLUMN_NAME_DURATION_TYPE + ", "
				+ COLUMN_NAME_BINARY_STATE + ", "
				+ COLUMN_NAME_MULTISTATE_STATE + ", "
				+ COLUMN_NAME_CHANGE_COUNT + ", "
				+ COLUMN_NAME_ALPHANUMERIC_STATE + ", "
				+ COLUMN_NAME_WEIGHT
			+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?) ";

	private static final String POINT_EVENT_DETECTOR_UPDATE = ""
			+ "update pointEventDetectors set "
				+ COLUMN_NAME_XID + "=?, "
				+ COLUMN_NAME_ALIAS + "=?, "
				+ COLUMN_NAME_ALARM_LEVEL + "=?, "
				+ COLUMN_NAME_STATE_LIMIT + "=?, "
				+ COLUMN_NAME_DURATION + "=?, "
				+ COLUMN_NAME_DURATION_TYPE + "=?, "
				+ COLUMN_NAME_BINARY_STATE + "=?, "
				+ COLUMN_NAME_MULTISTATE_STATE + "=?, "
				+ COLUMN_NAME_CHANGE_COUNT + "=?, "
				+ COLUMN_NAME_ALPHANUMERIC_STATE + "=?, "
				+ COLUMN_NAME_WEIGHT + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String POINT_EVENT_DETECTOR_DELETE = ""
			+ "delete from pointEventDetectors where ";

	private static final String POINT_EVENT_DETECTOR_SELECT_DP_ID = ""
			+ "select "
				+ COLUMN_NAME_DATA_POINT_ID + " "
			+ "from pointEventDetectors where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String POINT_EVENT_DETECTOR_SELECT_XID = ""
			+ "select "
				+ COLUMN_NAME_XID + " "
			+ "from pointEventDetectors where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String POINT_EVENT_DETECTOR_SELECT_ID = ""
			+ "select "
				+ COLUMN_NAME_ID + " "
			+ "from pointEventDetectors where "
				+ COLUMN_NAME_XID + "=? "
			+ "and "
				+ COLUMN_NAME_DATA_POINT_ID + "=? ";

	private static final String POINT_EVENT_DETECTOR_SELECT_COUNT = ""
			+ "select count(*) from pointEventDetectors where "
				+ COLUMN_NAME_DATA_POINT_ID + "=? "
			+ "and "
				+ COLUMN_NAME_XID + "=? "
			+ "and "
				+ COLUMN_NAME_ID + "<>? ";

	private static final String EVENT_HANDLER_DELETE = ""
			+ "delete from eventHandlers where "
				+ COLUMN_NAME_EH_EVENT_TYPE_ID + "="
			+ EventType.EventSources.DATA_POINT
			+ " and "
				+ COLUMN_NAME_EH_EVENT_TYPE_REF1 + "=? "
			+ "and "
				+ COLUMN_NAME_EH_EVENT_TYPE_REF2 + "=? ";
	// @formatter:on

	private class PointEventDetectorRowMapper implements RowMapper<PointEventDetectorVO> {

		private final DataPointVO dataPoint;

		public PointEventDetectorRowMapper(DataPointVO dataPoint) {
			this.dataPoint = dataPoint;
		}

		@Override
		public PointEventDetectorVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			PointEventDetectorVO pointEventDetector = new PointEventDetectorVO();
			pointEventDetector.setId(rs.getInt(COLUMN_NAME_ID));
			pointEventDetector.setXid(rs.getString(COLUMN_NAME_XID));
			pointEventDetector.setAlias(rs.getString(COLUMN_NAME_ALIAS));
			pointEventDetector.setDetectorType(rs.getInt(COLUMN_NAME_DETECTOR_TYPE));
			pointEventDetector.setAlarmLevel(rs.getInt(COLUMN_NAME_ALARM_LEVEL));
			pointEventDetector.setLimit(rs.getDouble(COLUMN_NAME_STATE_LIMIT));
			pointEventDetector.setDuration(rs.getInt(COLUMN_NAME_DURATION));
			pointEventDetector.setDurationType(rs.getInt(COLUMN_NAME_DURATION_TYPE));
			pointEventDetector.setBinaryState(DAO.charToBool(rs.getString(COLUMN_NAME_BINARY_STATE)));
			pointEventDetector.setMultistateState(rs.getInt(COLUMN_NAME_MULTISTATE_STATE));
			pointEventDetector.setChangeCount(rs.getInt(COLUMN_NAME_CHANGE_COUNT));
			pointEventDetector.setAlphanumericState(rs.getString(COLUMN_NAME_ALPHANUMERIC_STATE));
			pointEventDetector.setWeight(rs.getDouble(COLUMN_NAME_WEIGHT));
			pointEventDetector.njbSetDataPoint(dataPoint);
			return pointEventDetector;
		}
	}

	public int getDataPointId(int pointEventDetectorId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPointId(int pointEventDetectorId) pointEventDetectorId:" + pointEventDetectorId);
		}

		return DAO.getInstance().getJdbcTemp().queryForObject(POINT_EVENT_DETECTOR_SELECT_DP_ID,
					new Object[] {pointEventDetectorId}, Integer.class);
		
	}

	public List<PointEventDetectorVO> getPointEventDetectors(DataPointVO dataPoint) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPointEventDetectors(DataPointVO dataPoint) dataPoint:" + dataPoint.toString());
		}

		String templateSelectWhereIdOrderBy = POINT_EVENT_DETECTOR_SELECT + "where " + COLUMN_NAME_DATA_POINT_ID + "=? "
				+ "order by " + COLUMN_NAME_ID;

		return DAO.getInstance().getJdbcTemp().query(templateSelectWhereIdOrderBy,
				new Object[] {dataPoint.getId()}, new PointEventDetectorRowMapper(dataPoint));

	}

	public int getId(String pointEventDetectorXid, int dataPointId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getId(String pointEventDetectorXid, int dataPointId) pointEventDetectorXid:" + pointEventDetectorXid
					+ ", dataPointId:" + dataPointId);
		}

		int id = DAO.getInstance().getJdbcTemp().queryForObject(POINT_EVENT_DETECTOR_SELECT_ID,
				new Object[] {pointEventDetectorXid, dataPointId}, Integer.class);
		
		if (id == 0) {
			return -1;
		} else {
			return id;
		}
		
	}

	public String getXid(int pointEventDetectorId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getXid(int pointEventDetectorId) pointEventDetectorId:" + pointEventDetectorId);
		}

		return DAO.getInstance().getJdbcTemp().queryForObject(POINT_EVENT_DETECTOR_SELECT_XID, new Object[] {pointEventDetectorId},
					String.class);

	}

	public boolean isEventDetectorXidUnique(int dataPointId, String xid, int excludeId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("isEventDetectorXidUnique(int dataPointId, String xid, int excludeId) dataPointId:" + dataPointId + ", xid:" + xid + ", excludeId:" + excludeId);
		}

		int size = DAO.getInstance().getJdbcTemp().queryForObject(POINT_EVENT_DETECTOR_SELECT_COUNT,
					new Object[]{dataPointId, xid, excludeId}, Integer.class);
		
		return size == 0;
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final PointEventDetectorVO pointEventDetector) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(PointEventDetectorVO pointEventDetector) pointEventDetector:" + pointEventDetector.toString());
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(POINT_EVENT_DETECTOR_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[] {
						pointEventDetector.getXid(),
						pointEventDetector.getAlias(),
						pointEventDetector.njbGetDataPoint().getId(),
						pointEventDetector.getDetectorType(),
						pointEventDetector.getAlarmLevel(),
						pointEventDetector.getLimit(),
						pointEventDetector.getDuration(),
						pointEventDetector.getDurationType(),
						DAO.boolToChar(pointEventDetector.isBinaryState()),
						pointEventDetector.getMultistateState(),
						pointEventDetector.getChangeCount(),
						pointEventDetector.getAlphanumericState(),
						pointEventDetector.getWeight()
				}).setValues(ps);
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(PointEventDetectorVO pointEventDetector) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(PointEventDetectorVO pointEventDetector) pointEventDetector:" + pointEventDetector.toString());
		}

		DAO.getInstance().getJdbcTemp().update(POINT_EVENT_DETECTOR_UPDATE, new Object[] {
				pointEventDetector.getXid(),
				pointEventDetector.getAlias(),
				pointEventDetector.getAlarmLevel(),
				pointEventDetector.getLimit(),
				pointEventDetector.getDuration(),
				pointEventDetector.getDurationType(),
				DAO.boolToChar(pointEventDetector.isBinaryState()),
				pointEventDetector.getMultistateState(),
				pointEventDetector.getChangeCount(),
				pointEventDetector.getAlphanumericState(),
				pointEventDetector.getWeight(),
				pointEventDetector.getId()
			}
		);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int dataPointId, int pointEventDetectorId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int dataPointId) dataPointId:" + pointEventDetectorId);
		}

		String templateDelete = POINT_EVENT_DETECTOR_DELETE + " id=?";

		DAO.getInstance().getJdbcTemp().update(EVENT_HANDLER_DELETE, new Object[]{dataPointId, pointEventDetectorId});
		DAO.getInstance().getJdbcTemp().update(templateDelete, new Object[] {pointEventDetectorId});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void deleteWithId(String dataPointIds) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteWithId(String dataPointIds) dataPointIds:" + dataPointIds);
		}

		String[] parameters = dataPointIds.split(",");

		StringBuilder queryBuilder = new StringBuilder(POINT_EVENT_DETECTOR_DELETE + COLUMN_NAME_DATA_POINT_ID + " in (?");
		for (int i = 1; i<parameters.length; i++) {
			queryBuilder.append(",?");
		}
		queryBuilder.append(")");

		DAO.getInstance().getJdbcTemp().update(queryBuilder.toString(), (Object[]) parameters);
	}
}
