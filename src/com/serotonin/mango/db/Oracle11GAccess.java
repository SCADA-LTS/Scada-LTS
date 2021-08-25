/*
 * Mango - Open Source M2M - http://mango.serotoninsoftware.com Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
 * 
 * @author Matthew Lohbihler
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.mango.Common;

public class Oracle11GAccess extends BasePooledAccess {
	private enum ErrorCode {
		TableOrViewDoesNotExist(942);

		private final int code;

		ErrorCode(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}

	private static final String CREATE_SCHEMA_SCRIPT_PATH = "/WEB-INF/db/createTables-oracle11g.sql";
	private static final String ORACLE_DRIVER_CLASS = "oracle.jdbc.OracleDriver";
	private final Log log = LogFactory.getLog(Oracle11GAccess.class);

	public Oracle11GAccess(ServletContext ctx) {
		super(ctx);
	}

	public Oracle11GAccess(ServletContext ctx, String dbPrefix) {
		super(ctx, dbPrefix);
	}

	@Override
	protected void initializeImpl(String propertyPrefix) {
		super.initializeImpl(propertyPrefix);
		((BasicDataSource) dataSource).setInitialSize(3);
		((BasicDataSource) dataSource).setMaxWait(-1);
		((BasicDataSource) dataSource).setTestWhileIdle(true);
		((BasicDataSource) dataSource).setTimeBetweenEvictionRunsMillis(10000);
		((BasicDataSource) dataSource).setMinEvictableIdleTimeMillis(60000);
	}

	@Override
	protected void initializeImpl(String propertyPrefix, String dataSourceName) {
		super.initializeImpl(propertyPrefix, dataSourceName);
	}

	@Override
	public DatabaseType getType() {
		return DatabaseType.ORACLE11G;
	}

	@Override
	protected String getDriverClassName() {
		return ORACLE_DRIVER_CLASS;
	}

	@Override
	protected void createSchema(String scriptFile) {

		// Create MySql Connection
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Connection con = DriverManager.getConnection(Common
					.getEnvironmentProfile().getString(getDbPrefix() + "url"), Common
					.getEnvironmentProfile().getString(getDbPrefix() + "username"), Common
					.getEnvironmentProfile().getString(getDbPrefix() + "password"));

			// Initialize object for ScripRunner
			ScriptRunner sr = new ScriptRunner(con, false, false);

			// Give the input file to Reader
			Reader reader = new BufferedReader(new InputStreamReader(
					ctx.getResourceAsStream(scriptFile)));

			// Exctute script
			sr.runScript(reader);

		} catch (Exception e) {
			throw new ShouldNeverHappenException(e);
		}
	}

	@Override
	protected boolean newDatabaseCheck(ExtendedJdbcTemplate ejt) {
		try {
			ejt.execute("select count(*) from users");
		} catch (DataAccessException e) {
			if (e.getCause() instanceof SQLException) {
				SQLException se = (SQLException) e.getCause();
				if ("42000".equals(se.getSQLState())
						|| se.getErrorCode() == ErrorCode.TableOrViewDoesNotExist
								.getCode()) {
					// This state means a missing table. Assume that the schema
					// needs to be created.
					createSchema(CREATE_SCHEMA_SCRIPT_PATH);
					return true;
				}
			}
			throw e;
		}
		return false;
	}

	@Override
	public double applyBounds(double value) {
		// TODO VANIA: VERIFY
		if (Double.isNaN(value))
			return 0;
		if (value == Double.POSITIVE_INFINITY)
			return Double.MAX_VALUE;
		if (value == Double.NEGATIVE_INFINITY)
			return -Double.MAX_VALUE;

		return value;
	}

	@Override
	public void executeCompress(ExtendedJdbcTemplate ejt) {
		// no op
	}

	/**
	 * Prepares the statement to insert operation that returning the
	 * auto-generated key from the inserted row
	 * 
	 * @connection the connection (session) with the database
	 * @param sql
	 *            an SQL statement that may contain one or more '?' IN parameter
	 *            placeholders
	 * @param generatedKey
	 *            name of the generated column that should be returned from the
	 *            inserted row or rows
	 * @return a new <code>PreparedStatement</code> object, containing the
	 *         pre-compiled statement, that is capable of returning the
	 *         auto-generated key designated by the generated column name.
	 * 
	 */
	public PreparedStatement prepareStatement(Connection connection,
			String sql, String generatedKey) throws SQLException {
		return connection.prepareStatement(sql, new String[] { generatedKey });
	}

}
