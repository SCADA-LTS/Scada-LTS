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
package com.serotonin.mango.rt.dataSource.sql;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.io.StreamUtils;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.vo.dataSource.sql.SqlDataSourceVO;
import com.serotonin.mango.vo.dataSource.sql.SqlPointLocatorVO;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class SqlDataSourceRT extends PollingDataSource {
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
	public static final int STATEMENT_EXCEPTION_EVENT = 2;

	private final Log log = LogFactory.getLog(SqlDataSourceRT.class);

	private final SqlDataSourceVO vo;
	private Connection conn;
	private int timeoutCount = 0;
	private int timeoutsToReconnect = 3;

	public SqlDataSourceRT(SqlDataSourceVO vo) {
		super(vo);
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
		this.vo = vo;
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {
		if (conn == null)
			return;

		SqlPointLocatorVO locatorVO = ((SqlPointLocatorRT) dataPoint
				.getPointLocator()).getVO();

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(locatorVO.getUpdateStatement());

			if (locatorVO.getDataTypeId() == DataTypes.ALPHANUMERIC)
				stmt.setString(1, valueTime.getStringValue());
			else if (locatorVO.getDataTypeId() == DataTypes.BINARY)
				stmt.setBoolean(1, valueTime.getBooleanValue());
			else if (locatorVO.getDataTypeId() == DataTypes.MULTISTATE)
				stmt.setInt(1, valueTime.getIntegerValue());
			else if (locatorVO.getDataTypeId() == DataTypes.NUMERIC)
				stmt.setDouble(1, valueTime.getDoubleValue());
			else if (locatorVO.getDataTypeId() == DataTypes.IMAGE) {
				byte[] data = ((ImageValue) valueTime.getValue())
						.getImageData();
                                if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){
                                    stmt.setBinaryStream(1, new ByteArrayInputStream(data), data.length);
                                }
                                else{
                                    stmt.setBlob(1, new ByteArrayInputStream(data), data.length);
                                }
			} else
				throw new ShouldNeverHappenException("What's this?: "
						+ locatorVO.getDataTypeId());

			int rows = stmt.executeUpdate();
			if (rows == 0) {
				raiseEvent(STATEMENT_EXCEPTION_EVENT, valueTime.getTime(),
						false, new LocalizableMessage(
								"event.sql.noRowsUpdated", dataPoint.getVO()
										.getName()));
			} else
				dataPoint.setPointValue(valueTime, source);
		} catch (IOException e) {
			raiseEvent(STATEMENT_EXCEPTION_EVENT, valueTime.getTime(), false,
					new LocalizableMessage("event.sql.setError", dataPoint
							.getVO().getName(), getExceptionMessage(e)));
		} catch (SQLException e) {
			raiseEvent(STATEMENT_EXCEPTION_EVENT, valueTime.getTime(), false,
					new LocalizableMessage("event.sql.setError", dataPoint
							.getVO().getName(), getExceptionMessage(e)));
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				throw new ShouldNeverHappenException(e);
			}
		}
	}

	@Override
	protected void doPoll(long time) {
		if (conn == null)
			return;

		// If there is no select statement, don't bother. It's true that we
		// wouldn't need to bother polling at all,
		// but for now this will do.
		if (StringUtils.isEmpty(vo.getSelectStatement()))
			return;

		PreparedStatement stmt = null;

		try {

			if (timeoutCount >= timeoutsToReconnect) {
				System.out.println("[SQL] Trying to reconnect !");
				timeoutCount = 0;
				initialize();
			} else {
				stmt = conn.prepareStatement(vo.getSelectStatement());
				if (vo.isRowBasedQuery())
					doRowPollImpl(time, stmt);
				else
					doColumnPollImpl(time, stmt);
			}

		} catch (Exception e) {
			raiseEvent(STATEMENT_EXCEPTION_EVENT, time, true,
					getExceptionMessage(e));
			timeoutCount++;
			System.out.println("[SQL] Poll Failed !");
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				// no op
			}
		}
	}

	private void doColumnPollImpl(long time, PreparedStatement stmt)
			throws SQLException {
		ResultSet rs = stmt.executeQuery();
		ResultSetMetaData meta = rs.getMetaData();

		if (rs.next()) {

			for (DataPointRT dp : dataPoints) {
				SqlPointLocatorRT locatorRT = dp.getPointLocator();
				SqlPointLocatorVO locatorVO = locatorRT.getVO();

				String fieldName = locatorVO.getFieldName();
				if (!StringUtils.isEmpty(fieldName)) {
					// Point value.
					MangoValue value;
					try {
						value = getValue(locatorVO, rs, fieldName, time);
					} catch (IOException e) {
						continue;
					} catch (SQLException e) {
						continue;
					}

					// Point time override.
					long pointTime = time;
					String timeOverride = locatorVO.getTimeOverrideName();
					if (!StringUtils.isEmpty(timeOverride)) {
						// Find the meta data for the column.
						int column = -1;
						for (int i = 1; i <= meta.getColumnCount(); i++) {
							if (timeOverride.equalsIgnoreCase(meta
									.getColumnLabel(i))) {
								column = i;
								break;
							}
						}

						if (column == -1) {
							// Couldn't find the column.
							raiseEvent(STATEMENT_EXCEPTION_EVENT, time, true,
									new LocalizableMessage(
											"event.sql.timeNotFound",
											timeOverride));
							continue;
						}

						pointTime = getTimeOverride(meta, column, rs, time);
						if (pointTime == -1)
							continue;
					}

					dp.updatePointValue(new PointValueTime(value, pointTime));
				}
			}
		} else
			raiseEvent(STATEMENT_EXCEPTION_EVENT, time, true,
					new LocalizableMessage("event.sql.noData"));

		rs.close();
	}

	private void doRowPollImpl(long time, PreparedStatement stmt)
			throws SQLException {
		ResultSet rs = stmt.executeQuery();
		ResultSetMetaData meta = rs.getMetaData();

		while (rs.next()) {
			// In each row:
			// - the first column is the row identifier which is matched with
			// the field name in the vo
			// - the second column is the value
			// - the third column (if it exists) is the time override

			String rowId = rs.getString(1);

			// Find the vo in question.
			boolean found = false;
			for (DataPointRT dp : dataPoints) {
				SqlPointLocatorRT locatorRT = dp.getPointLocator();
				SqlPointLocatorVO locatorVO = locatorRT.getVO();
				String fieldName = locatorVO.getFieldName();

				if (!StringUtils.isEmpty(fieldName)
						&& fieldName.equalsIgnoreCase(rowId)) {
					found = true;

					// Point value.
					MangoValue value;
					try {
						value = getValue(locatorVO, rs, meta.getColumnLabel(2),
								time);
					} catch (IOException e) {
						continue;
					} catch (SQLException e) {
						continue;
					}

					// Point time override.
					long pointTime = time;
					if (meta.getColumnCount() > 2) {
						pointTime = getTimeOverride(meta, 3, rs, time);
						if (pointTime == -1)
							continue;
					}

					dp.updatePointValue(new PointValueTime(value, pointTime));
				}
			}

			if (!found)
				raiseEvent(STATEMENT_EXCEPTION_EVENT, time, true,
						new LocalizableMessage("event.sql.noDataPoint", rowId));
		}

		rs.close();
	}

	private MangoValue getValue(SqlPointLocatorVO locatorVO, ResultSet rs,
			String fieldName, long time) throws SQLException, IOException {
		try {
			int dataType = locatorVO.getDataTypeId();
			if (dataType == DataTypes.ALPHANUMERIC)
				return new AlphanumericValue(rs.getString(fieldName));
			else if (dataType == DataTypes.BINARY)
				return new BinaryValue(rs.getBoolean(fieldName));
			else if (dataType == DataTypes.MULTISTATE)
				return new MultistateValue(rs.getInt(fieldName));
			else if (dataType == DataTypes.NUMERIC)
				return new NumericValue(rs.getDouble(fieldName));
			else if (dataType == DataTypes.IMAGE) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
                                if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){
                                    StreamUtils.transfer(rs.getBinaryStream(fieldName),out);
                                }
                                else{
                                    StreamUtils.transfer(rs.getBlob(fieldName).getBinaryStream(),out);
                                }
				return new ImageValue(out.toByteArray(), ImageValue.TYPE_JPG);
			} else
				throw new ShouldNeverHappenException("What's this?: "
						+ locatorVO.getDataTypeId());
		} catch (SQLException e) {
			// Field level error. Assume a bad field name.
			raiseEvent(STATEMENT_EXCEPTION_EVENT, time, true,
					getExceptionMessage(e));
			throw e;
		}
	}

	private long getTimeOverride(ResultSetMetaData meta, int column,
			ResultSet rs, long time) throws SQLException {
		switch (meta.getColumnType(column)) {
		case Types.BIGINT:
		case Types.LONGVARCHAR:
			return rs.getLong(column);
		case Types.DATE:
			return rs.getDate(column).getTime();
		case Types.TIME:
			return rs.getTime(column).getTime();
		case Types.TIMESTAMP:
			return rs.getTimestamp(column).getTime();
		}

		raiseEvent(
				STATEMENT_EXCEPTION_EVENT,
				time,
				true,
				new LocalizableMessage("event.sql.dataTypeNotSupported", meta
						.getColumnTypeName(column), meta.getColumnType(column)));
		return -1;
	}

	//
	// /
	// / Lifecycle
	// /
	//
	@Override
	public void initialize() {
		// Get a connection to the database. No need to pool, because we don't
		// intend to close it until we shut down.
		try {
			DriverManager.registerDriver((Driver) Class.forName(
					vo.getDriverClassname()).newInstance());
			conn = DriverManager.getConnection(vo.getConnectionUrl(),
					vo.getUsername(), vo.getPassword());

			// Test the connection.
			conn.getMetaData();

			// Deactivate any existing event.
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis());
		} catch (Exception e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
					true, DataSourceRT.getExceptionMessage(e));
			log.info("Error while initializing data source", e);
			return;
		}

		super.initialize();
	}

	@Override
	public void terminate() {
		super.terminate();

		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			throw new ShouldNeverHappenException(e);
		}
	}
}
