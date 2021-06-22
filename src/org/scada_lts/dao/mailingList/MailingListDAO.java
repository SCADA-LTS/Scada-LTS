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

import com.mysql.jdbc.Statement;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifierRowMapper;
import org.scada_lts.utils.QueryUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * DAO for MailingList
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class MailingListDAO {

	private static final Log LOG = LogFactory.getLog(MailingListDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS = "dailyLimitSentEmails";
    private static final String COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS_NUMBER = "dailyLimitSentEmailsNumber";
	private static final String COLUMN_NAME_COLLECT_INACTIVE_EMAILS = "collectInactiveEmails";
	private static final String COLUMN_NAME_CRON_PATTERN = "cronPattern";


	// @formatter:off

	private static final String MAILING_LIST_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS + ", "
                + COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS_NUMBER + ", "
				+ COLUMN_NAME_COLLECT_INACTIVE_EMAILS + ", "
				+ COLUMN_NAME_CRON_PATTERN + " "
			+ "from mailingLists ";

	private static final String MAILING_LIST_SELECT_WHERE_ID_IN = ""
			+ "select "
			+ COLUMN_NAME_ID + ", "
			+ COLUMN_NAME_XID + ", "
			+ COLUMN_NAME_NAME + ", "
			+ COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS + ", "
            + COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS_NUMBER + ", "
			+ COLUMN_NAME_COLLECT_INACTIVE_EMAILS + ", "
			+ COLUMN_NAME_CRON_PATTERN + " "
			+ "from mailingLists where "
			+ COLUMN_NAME_ID + " "
			+ "in (?) ";

	private static final String MAILING_LIST_INSERT = ""
			+ "insert into mailingLists ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS + ", "
                + COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS_NUMBER + ", "
				+ COLUMN_NAME_COLLECT_INACTIVE_EMAILS + ", "
				+ COLUMN_NAME_CRON_PATTERN + ") "
			+ "values (?,?,?,?,?,?) ";

	private static final String MAILING_LIST_UPDATE = ""
			+ "update mailingLists set "
				+ COLUMN_NAME_XID + "=?, "
				+ COLUMN_NAME_NAME + "=?, "
				+ COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS + "=?, "
                + COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS_NUMBER + "=?, "
				+ COLUMN_NAME_COLLECT_INACTIVE_EMAILS + "=?, "
				+ COLUMN_NAME_CRON_PATTERN + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String MAILING_LIST_DELETE = ""
			+ "delete from mailingLists where "
				+ COLUMN_NAME_ID + "=? ";


	// @formatter:on

	private class MailingListRowMapper implements RowMapper<MailingList> {

		@Override
		public MailingList mapRow(ResultSet rs, int rowNum) throws SQLException {
			MailingList mailingList = new MailingList();
			mailingList.setId(rs.getInt(COLUMN_NAME_ID));
			mailingList.setXid(rs.getString(COLUMN_NAME_XID));
			mailingList.setName(rs.getString((COLUMN_NAME_NAME)));
			mailingList.setCollectInactiveEmails(rs.getBoolean(COLUMN_NAME_COLLECT_INACTIVE_EMAILS));
			mailingList.setCronPattern(rs.getString(COLUMN_NAME_CRON_PATTERN));
			mailingList.setDailyLimitSentEmails(rs.getBoolean(COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS));
            mailingList.setDailyLimitSentEmailsNumber(rs.getInt(COLUMN_NAME_DAILY_LIMIT_SENT_EMAILS_NUMBER));
			return mailingList;
		}
	}

	public MailingList getMailingList(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getMailingList(int id) id:" + id);
		}

		String templateSelectWhereId = MAILING_LIST_SELECT + "where " + COLUMN_NAME_ID + "=?";

		return DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {id}, new MailingListRowMapper());
	}

	public MailingList getMailingList(String xid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getMailingL" +
					"ist(String xid) xid:" + xid);
		}

		String templateSelectWhereXid = MAILING_LIST_SELECT + "where " + COLUMN_NAME_XID + "=?";

		MailingList mailingList;
		try {
			mailingList = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereXid, new Object[] {xid}, new MailingListRowMapper());
		} catch (EmptyResultDataAccessException e) {
			mailingList = null;
		}
		return mailingList;
	}

	public List<MailingList> getMailingLists() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getMailingLists()");
		}

		String templateSelectOrderBy = MAILING_LIST_SELECT + "order by name";

		return DAO.getInstance().getJdbcTemp().query(templateSelectOrderBy, new MailingListRowMapper());
	}

	public List<ScadaObjectIdentifier> getSimpleMailingLists() {
		String templateSelectOrderBy = MAILING_LIST_SELECT + "order by name";
		return DAO.getInstance().getJdbcTemp().query(templateSelectOrderBy, ScadaObjectIdentifierRowMapper.withDefaultNames());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final MailingList mailingList) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(MailingList mailingList) mailingList:" + mailingList.toString());
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(MAILING_LIST_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[] {mailingList.getXid(), mailingList.getName(),
						mailingList.isDailyLimitSentEmails(), mailingList.getDailyLimitSentEmailsNumber(),
                        mailingList.isCollectInactiveEmails(), mailingList.getCronPattern()}).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(MailingList mailingList) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(MailingList mailingList) mailingList:" + mailingList.toString());
		}

		DAO.getInstance().getJdbcTemp().update(MAILING_LIST_UPDATE, new Object[] {mailingList.getXid(), mailingList.getName(),
				mailingList.isDailyLimitSentEmails(), mailingList.getDailyLimitSentEmailsNumber(),
                mailingList.isCollectInactiveEmails(), mailingList.getCronPattern(), mailingList.getId()});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" + id);
		}

		DAO.getInstance().getJdbcTemp().update(MAILING_LIST_DELETE, new Object[] {id});
	}

	@Transactional(readOnly = true)
	public List<MailingList> getMailingLists(Set<Integer> ids) {
		if(ids.isEmpty())
			return Collections.emptyList();
		String args = QueryUtils.getArgsIn(ids.size());
		String query = MAILING_LIST_SELECT_WHERE_ID_IN.replace("?", args);
		return DAO.getInstance().getJdbcTemp().query(query, ids.toArray(), new MailingListRowMapper());
	}

}
