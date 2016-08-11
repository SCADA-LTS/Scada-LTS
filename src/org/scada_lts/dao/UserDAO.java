package org.scada_lts.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

/** 
 * DAO for Unsilenced Alarm.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class UserDAO {
	
	private static final Log LOG = LogFactory.getLog(UserDAO.class);
	private final static String  COLUMN_NAME_ID = "id";
	
	// @formatter:off
	private static final String SQL = ""
			+ "select "
				+ "id "
			+ "from "
				+ "users ";
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
}
