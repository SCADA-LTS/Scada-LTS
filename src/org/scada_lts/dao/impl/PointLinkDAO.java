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
package org.scada_lts.dao.impl;

import com.mysql.jdbc.Statement;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for PointLink
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
@Repository
public class PointLinkDAO {

	private static final Log LOG = LogFactory.getLog(PointLinkDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_SOURCE_POINT_ID = "sourcePointId";
	private static final String COLUMN_NAME_TARGET_POINT_ID = "targetPointId";
	private static final String COLUMN_NAME_SCRIPT = "script";
	private static final String COLUMN_NAME_EVENT_TYPE = "eventType";
	private static final String COLUMN_NAME_DISABLED = "disabled";

	// @formatter:off
	private static final String POINT_LINK_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_SOURCE_POINT_ID + ", "
				+ COLUMN_NAME_TARGET_POINT_ID + ", "
				+ COLUMN_NAME_SCRIPT + ", "
				+ COLUMN_NAME_EVENT_TYPE + ", "
				+ COLUMN_NAME_DISABLED + " "
			+ "from "
				+ "pointLinks ";

	private static final String POINT_LINK_INSERT = ""
			+ "insert into pointLinks ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_SOURCE_POINT_ID + ", "
				+ COLUMN_NAME_TARGET_POINT_ID + ", "
				+ COLUMN_NAME_SCRIPT + ", "
				+ COLUMN_NAME_EVENT_TYPE + ", "
				+ COLUMN_NAME_DISABLED
			+ ") "
			+ "values (?,?,?,?,?,?) ";

	private static final String POINT_LINK_UPDATE = ""
			+ "update pointLinks set "
				+ COLUMN_NAME_XID + "=?, "
				+ COLUMN_NAME_SOURCE_POINT_ID + "=?, "
				+ COLUMN_NAME_TARGET_POINT_ID + "=?, "
				+ COLUMN_NAME_SCRIPT + "=?, "
				+ COLUMN_NAME_EVENT_TYPE + "=?, "
				+ COLUMN_NAME_DISABLED + "=? "
			+ "where "
				+ COLUMN_NAME_ID
			+ "=? ";

	private static final String POINT_LINK_DELETE = ""
			+ "delete from pointLinks where "
				+ COLUMN_NAME_ID
			+ "=? ";

	private static final String POINT_LINK_SELECT_WHERE = ""
				+ POINT_LINK_SELECT
			+ " where "
				+ COLUMN_NAME_SOURCE_POINT_ID + "=? "
			+ "or "
				+ COLUMN_NAME_TARGET_POINT_ID + "=? ";
	// @formatter:on

	private class PointLinkRowMapper implements RowMapper<PointLinkVO> {

		@Override
		public PointLinkVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			PointLinkVO pointLink = new PointLinkVO();
			pointLink.setId(rs.getInt(COLUMN_NAME_ID));
			pointLink.setXid(rs.getString(COLUMN_NAME_XID));
			pointLink.setSourcePointId(rs.getInt(COLUMN_NAME_SOURCE_POINT_ID));
			pointLink.setTargetPointId(rs.getInt(COLUMN_NAME_TARGET_POINT_ID));
			pointLink.setScript(rs.getString(COLUMN_NAME_SCRIPT));
			pointLink.setEvent(rs.getInt(COLUMN_NAME_EVENT_TYPE));
			pointLink.setDisabled(CharTo.charToBool(rs.getString(COLUMN_NAME_DISABLED)));
			return pointLink;
		}
	}

	public PointLinkVO getPointLink(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPointLink(int id) id:" + id);
		}

		String templateSelectWhereId = POINT_LINK_SELECT + "where " + COLUMN_NAME_ID + "=? ";

		PointLinkVO pointLinkVO;
		try {
			pointLinkVO = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {id}, new PointLinkRowMapper());
		} catch (EmptyResultDataAccessException e) {
			pointLinkVO = null;
		}
		return pointLinkVO;
	}

	public PointLinkVO getPointLink(String xid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPointLink(int xid) xid:" + xid);
		}

		String templateSelectWhereId = POINT_LINK_SELECT + "where " + COLUMN_NAME_XID + "=? ";

		PointLinkVO pointLinkVO;
		try {
			pointLinkVO = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[]{xid}, new PointLinkRowMapper());
		} catch (EmptyResultDataAccessException e) {
			pointLinkVO = null;
		}
		return pointLinkVO;
	}

	public List<PointLinkVO> getPointLinks() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPointLinks()");
		}

		return DAO.getInstance().getJdbcTemp().query(POINT_LINK_SELECT, new PointLinkRowMapper());
	}

	public List<PointLinkVO> getPointLinksForPoint(int datapointId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPointLinksForPoint(int datapointId) datapointId:" + datapointId);
		}

		return DAO.getInstance().getJdbcTemp().query(POINT_LINK_SELECT_WHERE, new Object[]{datapointId, datapointId}, new PointLinkRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final PointLinkVO pointLink) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insertPointLink(PointLinkVO pointLink) pointLink:" + pointLink.toString());
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(POINT_LINK_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[] {
					pointLink.getXid(),
					pointLink.getSourcePointId(),
					pointLink.getTargetPointId(),
					pointLink.getScript(),
					pointLink.getEvent(),
					CharTo.boolToChar(pointLink.isDisabled())}
				).setValues(ps);
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(PointLinkVO pointLink) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(PointLinkVO pointLink) pointLink:" + pointLink.toString());
		}

		DAO.getInstance().getJdbcTemp().update(POINT_LINK_UPDATE, new Object[] {
				pointLink.getXid(),
				pointLink.getSourcePointId(),
				pointLink.getTargetPointId(),
				pointLink.getScript(),
				pointLink.getEvent(),
				CharTo.boolToChar(pointLink.isDisabled()),
				pointLink.getId()
		});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" + id);
		}

		DAO.getInstance().getJdbcTemp().update(POINT_LINK_DELETE, new Object[] {id});
	}
}

