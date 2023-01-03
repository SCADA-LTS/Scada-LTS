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
package org.scada_lts.dao.mailingList;

import com.serotonin.mango.vo.mailingList.MailingList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.DAO;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for MailingListInactive
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class MailingListInactiveDAO {

	private static final Log LOG = LogFactory.getLog(MailingListInactiveDAO.class);

	private static final String COLUMN_NAME_ID = "mailingListId";
	private static final String COLUMN_NAME_INTERVAL = "inactiveInterval";

	// @formatter:off
	private static final String MAILING_INACTIVE_SELECT = ""
			+ "select "
				+ COLUMN_NAME_INTERVAL + " "
			+ "from mailingListInactive where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String MAILING_INACTIVE_INSERT = ""
			+ "insert into mailingListInactive ("
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_INTERVAL + ") "
			+ "values (?,?) ";

	private static final String MAILING_INACTIVE_DELETE = ""
			+ "delete from mailingListInactive where "
				+ COLUMN_NAME_ID + "=? ";
	// @formatter:on

	private class MailingListInactiveRowMapper implements RowMapper<Integer> {
		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getInt(COLUMN_NAME_INTERVAL);
		}
	}

	public List<Integer> getInactiveInterval(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace(" getInactiveInterval(int id) id:" + id);
		}

		return DAO.getInstance().getJdbcTemp().query(MAILING_INACTIVE_SELECT, new Object[]{id}, new MailingListInactiveRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void insert(final MailingList mailingList) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(final MailingList mailingList) mailingList:" + mailingList.toString());
		}

		final List<Integer> intervalIds = new ArrayList<Integer>(mailingList.getInactiveIntervals());
		DAO.getInstance().getJdbcTemp().batchUpdate(MAILING_INACTIVE_INSERT,
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setInt(1, mailingList.getId());
						ps.setInt(2, intervalIds.get(i));
					}

					@Override
					public int getBatchSize() {
						return intervalIds.size();
					}
				});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" + id);
		}

		DAO.getInstance().getJdbcTemp().update(MAILING_INACTIVE_DELETE, new Object[]{id});
	}
}
