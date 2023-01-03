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

import com.mysql.jdbc.Statement;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.DAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for Publisher
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class PublisherDAO {

	private static final Log LOG = LogFactory.getLog(PublisherDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_DATA = "data";

	private static final String COLUMN_NAME_EH_EVENT_TYPE_ID = "eventTypeId";
	private static final String COLUMN_NAME_EH_EVENT_TYPE_REF = "eventTypeRef1";

	// @formatter:off
	private static final String PUBLISHER_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_DATA + " "
			+ "from publishers ";

	private static final String PUBLISHER_INSERT = ""
			+ "insert into publishers ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_DATA + ") "
			+ "values (?,?) ";

	private static final String PUBLISHER_UPDATE = ""
			+ "update publishers set "
				+ COLUMN_NAME_XID + "=?, "
				+ COLUMN_NAME_DATA + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String PUBLISHER_DELETE = ""
			+ "delete from publishers where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String EVENT_HANDLER_DELETE = ""
			+ "delete from eventHandlers where "
				+ COLUMN_NAME_EH_EVENT_TYPE_ID + "="
				+ EventType.EventSources.PUBLISHER
			+ " and "
				+ COLUMN_NAME_EH_EVENT_TYPE_REF + "=? ";
	// @formatter:on

	private class PublisherRowMapper implements RowMapper<PublisherVO<? extends PublishedPointVO>> {

		@Override
		public PublisherVO<? extends PublishedPointVO> mapRow(ResultSet rs, int rowNum) throws SQLException {
			PublisherVO<? extends PublishedPointVO> publisher = (PublisherVO<? extends PublishedPointVO>) new SerializationData().readObject(rs.getBlob(COLUMN_NAME_DATA).getBinaryStream());
			publisher.setId(rs.getInt(COLUMN_NAME_ID));
			publisher.setXid(rs.getString(COLUMN_NAME_XID));
			return publisher;
		}
	}

	public PublisherVO<? extends PublishedPointVO> getPublisher(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPublisher(int id) id:" + id);
		}

		String templateSelectWhere = PUBLISHER_SELECT + "where " + COLUMN_NAME_ID + "=? ";

		PublisherVO<?> publisher;
		try {
			publisher = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhere, new Object[] {id}, new PublisherRowMapper());
		} catch (EmptyResultDataAccessException e) {
			publisher = null;
		}
		return publisher;
	}

	public PublisherVO<? extends PublishedPointVO> getPublisher(String xid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPublisher(String xid) xid:" + xid);
		}

		String templateSelectWhere = PUBLISHER_SELECT + "where " + COLUMN_NAME_XID + "=? ";

		PublisherVO<?> publisher;
		try {
			publisher = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhere, new Object[] {xid}, new PublisherRowMapper());;
		} catch (EmptyResultDataAccessException e) {
			publisher = null;
		}
		return publisher;
	}

	public List<PublisherVO<? extends PublishedPointVO>> getPublishers() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPublishers()");
		}

		return DAO.getInstance().getJdbcTemp().query(PUBLISHER_SELECT, new PublisherRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final PublisherVO<? extends PublishedPointVO> publisher) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(final PublisherVO<? extends PublishedPointVO> publisher) publisher:" + publisher.toString());
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(PUBLISHER_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[] {publisher.getXid(), new SerializationData().writeObject(publisher)}).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(PublisherVO<? extends PublishedPointVO> publisher) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(PublisherVO<? extends PublishedPointVO> publisher) publisher:" + publisher.toString());
		}

		DAO.getInstance().getJdbcTemp().update(PUBLISHER_UPDATE, new Object[] {publisher.getXid(), new SerializationData().writeObject(publisher), publisher.getId()});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" + id);
		}

		DAO.getInstance().getJdbcTemp().update(EVENT_HANDLER_DELETE, new Object[] {id});
		DAO.getInstance().getJdbcTemp().update(PUBLISHER_DELETE, new Object[] {id});
	}
}
