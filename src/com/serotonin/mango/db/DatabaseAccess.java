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

import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.MissingResourceException;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.spring.ConnectionCallbackVoid;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.db.upgrade.DBUpgrade;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.util.StringUtils;

abstract public class DatabaseAccess {
	private final Log log = LogFactory.getLog(DatabaseAccess.class);

	public enum DatabaseType {
		DERBY {
			@Override
			DatabaseAccess getImpl(ServletContext ctx) {
				return new DerbyAccess(ctx);
			}
		},
		MSSQL {
			@Override
			DatabaseAccess getImpl(ServletContext ctx) {
				return new MSSQLAccess(ctx);
			}
		},
		MYSQL {
			@Override
			DatabaseAccess getImpl(ServletContext ctx) {
				return new MySQLAccess(ctx);
			}
		},
		POSTGRES {
			@Override
			DatabaseAccess getImpl(ServletContext ctx) {
				return new PostgreSQLAccess(ctx);
			}
		},
		ORACLE11G {
			@Override
			DatabaseAccess getImpl(ServletContext ctx) {
				return new Oracle11GAccess(ctx);
			}
		};

		abstract DatabaseAccess getImpl(ServletContext ctx);
	}

	public static DatabaseAccess createDatabaseAccess(ServletContext ctx) {

		String type = Common.getEnvironmentProfile().getString("db.type",
				"derby");
		DatabaseType dt = DatabaseType.valueOf(type.toUpperCase());

		if (dt == null)
			throw new IllegalArgumentException("Unknown database type: " + type);

		return dt.getImpl(ctx);
	}

	public static DatabaseAccess getDatabaseAccess() {
		return Common.ctx.getDatabaseAccess();
	}

	protected final ServletContext ctx;

	protected DatabaseAccess(ServletContext ctx) {
		this.ctx = ctx;
	}

	public void initialize() {
		if (Common.getEnvironmentProfile().getString("db.datasource", "false")
				.equals("true")) {
			initializeImpl(
					"",
					Common.getEnvironmentProfile().getString(
							"db.datasourceName"));
		} else {
			initializeImpl("");
		}

		ExtendedJdbcTemplate ejt = new ExtendedJdbcTemplate();
		ejt.setDataSource(getDataSource());

		try {
			if (newDatabaseCheck(ejt)) {
				// Check if we should convert from another database.
				String convertTypeStr = null;
				try {
					convertTypeStr = Common.getEnvironmentProfile().getString(
							"convert.db.type");
				} catch (MissingResourceException e) {
					// no op
				}

				if (!StringUtils.isEmpty(convertTypeStr)) {
					// Found a database type from which to convert.
					DatabaseType convertType = DatabaseType
							.valueOf(convertTypeStr.toUpperCase());
					if (convertType == null)
						throw new IllegalArgumentException(
								"Unknown convert database type: " + convertType);

					DatabaseAccess sourceAccess = convertType.getImpl(ctx);
					sourceAccess.initializeImpl("convert.");

					DBConvert convert = new DBConvert();
					convert.setSource(sourceAccess);
					convert.setTarget(this);
					try {
						convert.execute();
					} catch (SQLException e) {
						throw new ShouldNeverHappenException(e);
					}

					sourceAccess.terminate();
				} else {
					// New database. Create a default user.
					User user = new User();
					user.setId(Common.NEW_ID);
					user.setUsername("admin");
					user.setPassword(Common.encrypt("admin"));
					user.setEmail("admin@yourMangoDomain.com");
					user.setHomeUrl("");
					user.setPhone("");
					user.setAdmin(true);
					user.setDisabled(false);
					user.setDataSourcePermissions(new LinkedList<Integer>());
					user.setDataPointPermissions(new LinkedList<DataPointAccess>());
					new UserDao().saveUser(user);

					// Record the current version.
					new SystemSettingsDAO().setValue(
							SystemSettingsDAO.DATABASE_SCHEMA_VERSION,
							Common.getVersion());
				}
			} else {
				// // The database exists, so let's make its schema version
				// matches
				// // the application version.
				if (Common.getEnvironmentProfile()
						.getString("db.upgrade.check", "false").equals("true")) {
					DBUpgrade.checkUpgrade();
				}
			}
		} catch (CannotGetJdbcConnectionException e) {
			log.fatal("Unable to connect to database of type "
					+ getType().name(), e);
			throw e;
		}

		postInitialize(ejt);
	}

	abstract public DatabaseType getType();

	abstract public void terminate();

	abstract public DataSource getDataSource();

	abstract public double applyBounds(double value);

	abstract public File getDataDirectory();

	abstract public void executeCompress(ExtendedJdbcTemplate ejt);

	abstract protected void initializeImpl(String propertyPrefix);

	abstract protected void initializeImpl(String propertyPrefix,
			String dataSourceName);

	protected void postInitialize(
			@SuppressWarnings("unused") ExtendedJdbcTemplate ejt) {
		// no op - override as necessary
	}

	abstract protected boolean newDatabaseCheck(ExtendedJdbcTemplate ejt);

	abstract public void runScript(String[] script, final OutputStream out)
			throws Exception;

	public void doInConnection(ConnectionCallbackVoid callback) {
		DataSource dataSource = getDataSource();
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection(dataSource);
			conn.setAutoCommit(false);
			callback.doInConnection(conn);
			conn.commit();
		} catch (Exception e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException e1) {
				log.warn("Exception during rollback", e1);
			}

			// Wrap and rethrow
			throw new ShouldNeverHappenException(e);
		} finally {
			if (conn != null)
				DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	public String getDatabasePassword(String propertyPrefix) {
		String input = Common.getEnvironmentProfile().getString(
				propertyPrefix + "db.password");
		return new DatabaseAccessUtils().decrypt(input);
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
		return connection.prepareStatement(sql, 1);
	}

}
