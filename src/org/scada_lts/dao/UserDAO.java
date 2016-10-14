package org.scada_lts.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.vo.UserComment;

/** 
 * DAO for Unsilenced Alarm.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class UserDAO {
	
	private static final Log LOG = LogFactory.getLog(UserDAO.class);
	private final static String COLUMN_NAME_ID = "id";
	private final static String COLUMN_NAME_USER_ID = "userId";
	private final static String COLUMN_NAME_COMMENT_TYPE = "commentType";
	private final static String COLUMN_NAME_TYPE_KEY = "typeKey";
	private final static String COLUMN_NAME_TIME_STAMP = "ts";
	private final static String COLUMN_NAME_COMMENT_TEXT = "commentText";
	
	// @formatter:off
	private static final String SQL = ""
			+ "select "
				+ "id "
			+ "from "
				+ "users ";
	
	private static final String USER_COMMENT_INSERT = ""
			+"insert userComments ("
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_COMMENT_TYPE+", "
				+ COLUMN_NAME_TYPE_KEY+", "
				+ COLUMN_NAME_TIME_STAMP+", "
				+ COLUMN_NAME_COMMENT_TEXT+") "
			+ "values (?,?,?,?,?)";
	
	// @formatter:on
	
	protected List<Integer> getAll() {
		LOG.trace("UserDAO");
		try {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<Integer> list = DAO.getInstance().getJdbcTemp().query(SQL, new RowMapper() {
				@Override
				public Integer mapRow(ResultSet rs, int rownumber) throws SQLException {	
					return rs.getInt(COLUMN_NAME_ID);
				}
			});

			return list;
		} catch (Exception e) {
			LOG.error(e);
		}
		return null;
	}
	
	@Deprecated
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public Object[] createComments(int typeId, int eventId, UserComment comment) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("eventId:"+eventId+" comment:"+comment.toString());
		}
		
		DAO.getInstance().getJdbcTemp().update(USER_COMMENT_INSERT, new Object[] { 
			 						comment.getUserId(),
			 						typeId,
			 						eventId,
			 						comment.getTs(),
			 						comment.getComment()
			 				});
		//TODO set pk on table in database
		return new Object[] {comment.getUserId(), eventId, comment.getTs()};
		
	}
}
