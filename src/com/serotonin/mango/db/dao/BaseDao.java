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

import com.serotonin.mango.Common;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ArgumentTypePreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BaseDao{

	private static final String DEFAULT_GENERATED_KEY_COLUMN_NAME = "id";

	protected DataSource dataSource;
	protected DataSourceTransactionManager tm;

	/**
	 * Public constructor for code that needs to get stuff from the database.
	 */
	public BaseDao() {
	}

	protected BaseDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	//
	// XID convenience methods
	//
	protected String generateUniqueXid(String prefix, String tableName) {
		String xid = Common.generateXid(prefix);
		while (!isXidUnique(xid, -1, tableName)) {
			xid = Common.generateXid(prefix);
		}
		return xid;
	}

	protected boolean isXidUnique(String xid, int excludeId, String tableName) {
		return DAO.getInstance().getJdbcTemp().queryForInt("select count(*) from " + tableName
				+ " where xid=? and id<>?", xid, excludeId) == 0;
	}

	/**
	 * Return the column name of the auto-generated key. Wheiter the column name
	 * is different of the default generated key column name, the DAO should
	 * override this method.
	 * 
	 * @return the column name of the generated key
	 */
	protected String getGeneratedKeyName() {
		return DEFAULT_GENERATED_KEY_COLUMN_NAME;
	}

	/**
	 * Implements the prepared statement creator, to get the prepared statement
	 * to insert operation that returning auto-generated key
	 * 
	 * @param sql
	 *            an SQL statement that may contain one or more '?' IN parameter
	 *            placeholders
	 * @param setter
	 *            parameter setter that will set the statement parameters
	 * @return
	 */
	protected PreparedStatementCreator getPreparedStatementCreator(String sql,
			PreparedStatementSetter setter) {

		final String sqlS = sql;
		final PreparedStatementSetter setterS = setter;
		final String generatedKey = getGeneratedKeyName();

		PreparedStatementCreator creator = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement preStmt;
				preStmt = Common.ctx.getDatabaseAccess().prepareStatement(
						connection, sqlS, generatedKey);
				setterS.setValues(preStmt);

				return preStmt;
			}
		};

		return creator;
	}

	private int executeInsert(String sql, PreparedStatementSetter pss) {

		DAO.getInstance().getJdbcTemp().update(sql,pss);
		return DAO.getInstance().getId();

	}

	private long executeInsertLong(String sql, PreparedStatementSetter pss) {

		DAO.getInstance().getJdbcTemp().update(sql,pss);
		return DAO.getInstance().getId();
	}

	protected int doInsert(String sql, Object params[]) {
		return executeInsert(sql, new ArgumentPreparedStatementSetter(params));
	}

	protected int doInsert(String sql, Object params[], int types[]) {
		return executeInsert(sql, new ArgumentTypePreparedStatementSetter(params,
				types));
	}

	protected int doInsert(String sql, PreparedStatementSetter pss) {
		return executeInsert(sql, pss);
	}

	protected long doInsertLong(String sql, Object params[]) {
		return executeInsertLong(sql, new ArgumentPreparedStatementSetter(params));
	}

	protected long doInsertLong(String sql, Object params[], int types[]) {
		return executeInsertLong(sql, new ArgumentTypePreparedStatementSetter(
				params, types));
	}

	protected long doInsertLong(String sql, PreparedStatementSetter pss) {
		return executeInsertLong(sql, pss);
	}



	protected DataSourceTransactionManager getTransactionManager() {
		if (this.tm == null) {
			this.tm = new DataSourceTransactionManager(this.dataSource);
		}

		return this.tm;
	}

	protected TransactionTemplate getTransactionTemplate() {
		return new TransactionTemplate(this.getTransactionManager());
	}
}
