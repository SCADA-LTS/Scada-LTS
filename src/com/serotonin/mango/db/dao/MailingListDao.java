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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.scada_lts.mango.adapter.MangoMailingList;
import org.scada_lts.mango.service.MailingListService;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;

/**
 * @author Matthew Lohbihler
 */
//public class MailingListDao extends BaseDao {
public class MailingListDao {

	MangoMailingList mailingListService = new MailingListService();

	public String generateUniqueXid() {
//		return generateUniqueXid(MailingList.XID_PREFIX, "mailingLists");
		return mailingListService.generateUniqueXid();
	}

	public boolean isXidUnique(String xid, int excludeId) {
//		return isXidUnique(xid, excludeId, "mailingLists");
		return mailingListService.isXidUnique(xid, excludeId);
	}

//	private static final String MAILING_LIST_SELECT = "select id, xid, name from mailingLists ";

	public List<MailingList> getMailingLists() {
//		List<MailingList> result = query(MAILING_LIST_SELECT + "order by name",
//				new MailingListRowMapper());
//		setRelationalData(result);
//		return result;
		return mailingListService.getMailingLists();
	}

	public MailingList getMailingList(int id) {
//		MailingList ml = queryForObject(MAILING_LIST_SELECT + "where id=?",
//				new Object[] { id }, new MailingListRowMapper());
//		setRelationalData(ml);
//		return ml;
		return mailingListService.getMailingList(id);
	}

	public MailingList getMailingList(String xid) {
//		MailingList ml = queryForObject(MAILING_LIST_SELECT + "where xid=?",
//				new Object[] { xid }, new MailingListRowMapper(), null);
//		if (ml != null)
//			setRelationalData(ml);
//		return ml;
		return mailingListService.getMailingList(xid);
	}

//	class MailingListRowMapper implements GenericRowMapper<MailingList> {
//		public MailingList mapRow(ResultSet rs, int rowNum) throws SQLException {
//			MailingList ml = new MailingList();
//			ml.setId(rs.getInt(1));
//			ml.setXid(rs.getString(2));
//			ml.setName(rs.getString(3));
//			return ml;
//		}
//	}

//	private void setRelationalData(List<MailingList> mls) {
//		for (MailingList ml : mls)
//			setRelationalData(ml);
//	}

//	private static final String MAILING_LIST_INACTIVE_SELECT = "select inactiveInterval from mailingListInactive where mailingListId=?";
//	private static final String MAILING_LIST_ENTRIES_SELECT = "select typeId, userId, address, '' from mailingListMembers where mailingListId=?";

//	private void setRelationalData(MailingList ml) {
//		ml.getInactiveIntervals().addAll(
//				query(MAILING_LIST_INACTIVE_SELECT,
//						new Object[] { ml.getId() },
//						new MailingListScheduleInactiveMapper()));
//
//		ml.setEntries(query(MAILING_LIST_ENTRIES_SELECT,
//				new Object[] { ml.getId() }, new EmailRecipientRowMapper()));
//
//		// Update the user type entries with their respective user objects.
//		populateEntrySubclasses(ml.getEntries());
//	}

//	class MailingListScheduleInactiveMapper implements
//			GenericRowMapper<Integer> {
//		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
//			return rs.getInt(1);
//		}
//	}

//	class EmailRecipientRowMapper implements GenericRowMapper<EmailRecipient> {
//		public EmailRecipient mapRow(ResultSet rs, int rowNum)
//				throws SQLException {
//			int type = rs.getInt(1);
//			switch (type) {
//			case EmailRecipient.TYPE_MAILING_LIST:
//				MailingList ml = new MailingList();
//				ml.setId(rs.getInt(2));
//				ml.setName(rs.getString(4));
//				return ml;
//			case EmailRecipient.TYPE_USER:
//				UserEntry ue = new UserEntry();
//				ue.setUserId(rs.getInt(2));
//				return ue;
//			case EmailRecipient.TYPE_ADDRESS:
//				AddressEntry ae = new AddressEntry();
//				ae.setAddress(rs.getString(3));
//				return ae;
//			}
//			throw new ShouldNeverHappenException(
//					"Unknown mailing list entry type: " + type);
//		}
//	}

	public Set<String> getRecipientAddresses(
			List<RecipientListEntryBean> beans, DateTime sendTime) {
//		List<EmailRecipient> entries = new ArrayList<EmailRecipient>(
//				beans.size());
//		for (RecipientListEntryBean bean : beans)
//			entries.add(bean.createEmailRecipient());
//		populateEntrySubclasses(entries);
//		Set<String> addresses = new HashSet<String>();
//		for (EmailRecipient entry : entries)
//			entry.appendAddresses(addresses, sendTime);
//		return addresses;
		return mailingListService.getRecipientAddresses(beans, sendTime);
	}

	public void populateEntrySubclasses(List<EmailRecipient> entries) {
//		// Update the user type entries with their respective user objects.
//		UserDao userDao = new UserDao();
//		for (EmailRecipient e : entries) {
//			if (e instanceof MailingList)
//				// NOTE: this does not set the mailing list name.
//				setRelationalData((MailingList) e);
//			else if (e instanceof UserEntry) {
//				UserEntry ue = (UserEntry) e;
//				ue.setUser(userDao.getUser(ue.getUserId()));
//			}
//		}
		mailingListService.populateEntrySubclasses(entries);
	}

//	private static final String MAILING_LIST_INSERT = "insert into mailingLists (xid, name) values (?,?)";
//	private static final String MAILING_LIST_UPDATE = "update mailingLists set xid=?, name=? where id=?";

	public void saveMailingList(final MailingList ml) {
//		final ExtendedJdbcTemplate ejt2 = ejt;
//		getTransactionTemplate().execute(
//				new TransactionCallbackWithoutResult() {
//					@SuppressWarnings("synthetic-access")
//					@Override
//					protected void doInTransactionWithoutResult(
//							TransactionStatus status) {
//						if (ml.getId() == Common.NEW_ID) {
//							if (Common.getEnvironmentProfile()
//									.getString("db.type").equals("postgres")) {
//								try {
//									// id = doInsert(EVENT_INSERT, args,
//									// EVENT_INSERT_TYPES);
//									Connection conn = DriverManager
//											.getConnection(
//													Common.getEnvironmentProfile()
//															.getString("db.url"),
//													Common.getEnvironmentProfile()
//															.getString(
//																	"db.username"),
//													Common.getEnvironmentProfile()
//															.getString(
//																	"db.password"));
//									PreparedStatement preStmt = conn
//											.prepareStatement(MAILING_LIST_INSERT);
//									preStmt.setString(1, ml.getXid());
//									preStmt.setString(2, ml.getName());
//									preStmt.executeUpdate();
//
//									ResultSet resSEQ = conn
//											.createStatement()
//											.executeQuery(
//													"SELECT currval('mailinglists_id_seq')");
//									resSEQ.next();
//									int id = resSEQ.getInt(1);
//
//									conn.close();
//
//									ml.setId(id);
//
//								} catch (SQLException ex) {
//									ex.printStackTrace();
//									ml.setId(0);
//								}
//							} else {
//								ml.setId(doInsert(
//										MAILING_LIST_INSERT,
//										new Object[] { ml.getXid(),
//												ml.getName() }));
//								saveRelationalData(ml);
//							}
//						} else {
//							ejt2.update(MAILING_LIST_UPDATE,
//									new Object[] { ml.getXid(), ml.getName(),
//											ml.getId() });
//							saveRelationalData(ml);
//						}
//					}
//				});
		mailingListService.saveMailingList(ml);
	}

//	private static final String MAILING_LIST_INACTIVE_INSERT = "insert into mailingListInactive (mailingListId, inactiveInterval) values (?,?)";
//	private static final String MAILING_LIST_ENTRY_INSERT = "insert into mailingListMembers (mailingListId, typeId, userId, address) values (?,?,?,?)";

	void saveRelationalData(final MailingList ml) {
//		// Save the inactive intervals.
//		ejt.update("delete from mailingListInactive where mailingListId=?",
//				new Object[] { ml.getId() });
//
//		// Save what is in the mailing list object.
//		final List<Integer> intervalIds = new ArrayList<Integer>(
//				ml.getInactiveIntervals());
//		ejt.batchUpdate(MAILING_LIST_INACTIVE_INSERT,
//				new BatchPreparedStatementSetter() {
//					public int getBatchSize() {
//						return intervalIds.size();
//					}
//
//					public void setValues(PreparedStatement ps, int i)
//							throws SQLException {
//						ps.setInt(1, ml.getId());
//						ps.setInt(2, intervalIds.get(i));
//					}
//				});
//
//		// Delete existing entries
//		ejt.update("delete from mailingListMembers where mailingListId=?",
//				new Object[] { ml.getId() });
//
//		// Save what is in the mailing list object.
//		final List<EmailRecipient> entries = ml.getEntries();
//		ejt.batchUpdate(MAILING_LIST_ENTRY_INSERT,
//				new BatchPreparedStatementSetter() {
//					public int getBatchSize() {
//						return entries.size();
//					}
//
//					public void setValues(PreparedStatement ps, int i)
//							throws SQLException {
//						EmailRecipient e = entries.get(i);
//						ps.setInt(1, ml.getId());
//						ps.setInt(2, e.getRecipientType());
//						ps.setInt(3, e.getReferenceId());
//						ps.setString(4, e.getReferenceAddress());
//					}
//				});
		mailingListService.saveRelationalData(ml);
	}

	public void deleteMailingList(final int mailingListId) {
//		ejt.update("delete from mailingLists where id=?",
//				new Object[] { mailingListId });
		mailingListService.deleteMailingList(mailingListId);
	}
}
