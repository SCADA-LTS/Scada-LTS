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

import java.util.List;

import javax.sql.DataSource;

import org.scada_lts.mango.adapter.MangoPointValues;
import org.scada_lts.mango.service.PointValueService;

import com.serotonin.db.MappedRowCallback;
import com.serotonin.mango.rt.dataImage.IdPointValueTime;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.vo.bean.LongPair;

public class PointValueDao extends BaseDao {
	//private static List<UnsavedPointValue> UNSAVED_POINT_VALUES = new ArrayList<UnsavedPointValue>();

	/*private static final String POINT_VALUE_INSERT_START = "insert into pointValues (dataPointId, dataType, pointValue, ts) values ";
	private static final String POINT_VALUE_INSERT_VALUES = "(?,?,?,?)";
	private static final int POINT_VALUE_INSERT_VALUES_COUNT = 4;
	private static final String POINT_VALUE_INSERT = POINT_VALUE_INSERT_START
			+ POINT_VALUE_INSERT_VALUES;
	private static final String POINT_VALUE_ANNOTATION_INSERT = "insert into pointValueAnnotations "
			+ "(pointValueId, textPointValueShort, textPointValueLong, sourceType, sourceId) values (?,?,?,?,?)";
	*/
	private MangoPointValues pointValuesService;

	public PointValueDao() {
		super();
		pointValuesService = new PointValueService();
	}

	public PointValueDao(DataSource dataSource) {
		super(dataSource);
		pointValuesService = new PointValueService();
	}

	/**
	 * Only the PointValueCache should call this method during runtime. Do not
	 * use.
	 */
	public PointValueTime savePointValueSync(int pointId,
			PointValueTime pointValue, SetPointSource source) {
		
		/*
		long id = savePointValueImpl(pointId, pointValue, source, false);

		PointValueTime savedPointValue;
		int retries = 5;
		while (true) {
			try {
				savedPointValue = getPointValue(id);
				break;
			} catch (ConcurrencyFailureException e) {
				if (retries <= 0)
					throw e;
				retries--;
			}
		}

		return savedPointValue;
		*/
		pointValuesService.savePointValue(pointId, pointValue, source, false);
		
		return pointValue;
	}

	/**
	 * Only the PointValueCache should call this method during runtime. Do not
	 * use.
	 */
	public void savePointValueAsync(int pointId, PointValueTime pointValue,
			SetPointSource source) {
		/*long id = savePointValueImpl(pointId, pointValue, source, true);
		if (id != -1)
			clearUnsavedPointValues();*/
		pointValuesService.savePointValue(pointId, pointValue, source, false);
		
	}

	long savePointValueImpl(final int pointId, final PointValueTime pointValue,
			final SetPointSource source, boolean async) {
		/*MangoValue value = pointValue.getValue();
		final int dataType = DataTypes.getDataType(value);
		double dvalue = 0;
		String svalue = null;

		if (dataType == DataTypes.IMAGE) {
			ImageValue imageValue = (ImageValue) value;
			dvalue = imageValue.getType();
			if (imageValue.isSaved())
				svalue = Long.toString(imageValue.getId());
		} else if (value.hasDoubleRepresentation())
			dvalue = value.getDoubleValue();
		else
			svalue = value.getStringValue();

		// Check if we need to create an annotation.
		long id;
		try {
			if (svalue != null || source != null || dataType == DataTypes.IMAGE) {
				final double dvalueFinal = dvalue;
				final String svalueFinal = svalue;

				// Create a transaction within which to do the insert.
				id = getTransactionTemplate().execute(
						new TransactionCallback<Long>() {
							public Long doInTransaction(TransactionStatus status) {
								return savePointValue(pointId, dataType,
										dvalueFinal, pointValue.getTime(),
										svalueFinal, source, false);
							}
						});
			} else
				// Single sql call, so no transaction required.
				id = savePointValue(pointId, dataType, dvalue,
						pointValue.getTime(), svalue, source, async);
		} catch (ConcurrencyFailureException e) {
			// Still failed to insert after all of the retries. Store the data
			synchronized (UNSAVED_POINT_VALUES) {
				UNSAVED_POINT_VALUES.add(new UnsavedPointValue(pointId,
						pointValue, source));
			}
			return -1;
		}

		// Check if we need to save an image
		if (dataType == DataTypes.IMAGE) {
			ImageValue imageValue = (ImageValue) value;
			if (!imageValue.isSaved()) {
				imageValue.setId(id);

				File file = new File(Common.getFiledataPath(),
						imageValue.getFilename());

				// Write the file.
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(file);
					StreamUtils
							.transfer(
									new ByteArrayInputStream(imageValue
											.getData()), out);
				} catch (IOException e) {
					// Rethrow as an RTE
					throw new ImageSaveException(e);
				} finally {
					try {
						if (out != null)
							out.close();
					} catch (IOException e) {
						// no op
					}
				}

				// Allow the data to be GC'ed
				imageValue.setData(null);
			}
		}
		return id;
		*/
		long id = pointValuesService.savePointValue(pointId, pointValue, source, false);
		return id;
	}

	/*private void clearUnsavedPointValues() {
		if (!UNSAVED_POINT_VALUES.isEmpty()) {
			synchronized (UNSAVED_POINT_VALUES) {
				while (!UNSAVED_POINT_VALUES.isEmpty()) {
					UnsavedPointValue data = UNSAVED_POINT_VALUES.remove(0);
					savePointValueImpl(data.getPointId(), data.getPointValue(),
							data.getSource(), false);
				}
			}
		}
	}*/

	public void savePointValue(int pointId, PointValueTime pointValue) {
		//savePointValueImpl(pointId, pointValue, new AnonymousUser(), true);\
		pointValuesService.savePointValue(pointId, pointValue);
	}

	/*long savePointValue(final int pointId, final int dataType, double dvalue,
			final long time, final String svalue, final SetPointSource source,
			boolean async) {
		// Apply database specific bounds on double values.
		dvalue = DatabaseAccess.getDatabaseAccess().applyBounds(dvalue);

		if (async) {
			BatchWriteBehind.add(new BatchWriteBehindEntry(pointId, dataType,
					dvalue, time), this);
			return -1;
		}

		int retries = 5;
		while (true) {
			try {
				return savePointValueImpl(pointId, dataType, dvalue, time,
						svalue, source);
			} catch (ConcurrencyFailureException e) {
				if (retries <= 0)
					throw e;
				retries--;
			} catch (RuntimeException e) {
				throw new RuntimeException(
						"Error saving point value: dataType=" + dataType
								+ ", dvalue=" + dvalue, e);
			}
		}
		
		long id = pointValuesDAO.savePointValue(pointId, pointValue, source, false);
		return id;
				
	} */
        
	/*private long savePointValueImpl(int pointId, int dataType, double dvalue, long time, String svalue, SetPointSource source) {
                long id;
                if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){                
                    try {
                        Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                                                    Common.getEnvironmentProfile().getString("db.username"),
                                                    Common.getEnvironmentProfile().getString("db.password"));
                        PreparedStatement preStmt = conn.prepareStatement(POINT_VALUE_INSERT);
                        preStmt.setInt(1, pointId);
                        preStmt.setInt(2, dataType);
                        preStmt.setDouble(3, dvalue);
                        preStmt.setLong(4, time);
                        preStmt.executeUpdate();

                        ResultSet resSEQ = conn.createStatement().executeQuery("SELECT currval('pointvalues_id_seq')");
                        resSEQ.next();
                        id = resSEQ.getInt(1);

                        conn.close(); 
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        id = 0;
                    }
                }    
                else{
                    id = doInsertLong(POINT_VALUE_INSERT, new Object[] { pointId, dataType, dvalue, time });
                } // id created

		if (svalue == null && dataType == DataTypes.IMAGE){
			svalue = Long.toString(id);
                }

		// Check if we need to create an annotation.
		if (svalue != null || source != null) {
			Integer sourceType = null, sourceId = null;
			if (source != null) {
				sourceType = source.getSetPointSourceType();
				sourceId = source.getSetPointSourceId();
			}

			String shortString = null;
			String longString = null;
			if (svalue != null) {
				if (svalue.length() > 128)
					longString = svalue;
				else
					shortString = svalue;
			}

                        if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){                
                            try {
                                Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                                                            Common.getEnvironmentProfile().getString("db.username"),
                                                            Common.getEnvironmentProfile().getString("db.password"));
                                PreparedStatement preStmt = conn.prepareStatement(POINT_VALUE_ANNOTATION_INSERT);
                                preStmt.setLong(1, id);
                                preStmt.setString(2, shortString);
                                preStmt.setString(3, longString);
                                preStmt.setInt(4, sourceType == null ? 0 : sourceType.intValue());
                                preStmt.setInt(5, sourceId == null ? 0 : sourceId.intValue());
                                preStmt.executeUpdate();

                                conn.close();                                 
                            } catch (Throwable ex) {
                                ex.printStackTrace();
                            }
                        }    
                        else{
                                ejt.update(POINT_VALUE_ANNOTATION_INSERT, new Object[] { id,
                                                shortString, longString, sourceType, sourceId }, new int[] {
                                                Types.INTEGER, Types.VARCHAR, Types.CLOB, Types.SMALLINT,
                                                Types.INTEGER });
                        }
		}

		return id;
	}*/

	/*private static final String POINT_VALUE_SELECT = "select pv.dataType, pv.pointValue, pva.textPointValueShort, pva.textPointValueLong, pv.ts, pva.sourceType, "
			+ "  pva.sourceId "
			+ "from pointValues pv "
			+ "  left join pointValueAnnotations pva on pv.id = pva.pointValueId";*/

	public List<PointValueTime> getPointValues(int dataPointId, long since) {
		/*return pointValuesQuery(POINT_VALUE_SELECT
				+ " where pv.dataPointId=? and pv.ts >= ? order by ts",
				new Object[] { dataPointId, since }, 0);*/
		
		return pointValuesService.getPointValues(dataPointId, since);
		
		
	}

	public List<PointValueTime> getPointValuesBetween(int dataPointId,
			long from, long to) {
		/*return pointValuesQuery(
				POINT_VALUE_SELECT
						+ " where pv.dataPointId=? and pv.ts >= ? and pv.ts<? order by ts",
				new Object[] { dataPointId, from, to }, 0);*/
		return pointValuesService.getPointValuesBetween(dataPointId, from, to);
	}

	public List<PointValueTime> getLatestPointValues(int dataPointId, int limit) {
		/*return pointValuesQuery(POINT_VALUE_SELECT
				+ " where pv.dataPointId=? order by pv.ts desc",
				new Object[] { dataPointId }, limit);*/
		return pointValuesService.getLatestPointValues(dataPointId, limit);
	}

	public List<PointValueTime> getLatestPointValues(int dataPointId,
			int limit, long before) {
		/*return pointValuesQuery(POINT_VALUE_SELECT
				+ " where pv.dataPointId=? and pv.ts<? order by pv.ts desc",
				new Object[] { dataPointId, before }, limit);*/
		return pointValuesService.getLatestPointValues(dataPointId, limit, before);
	}

	public PointValueTime getLatestPointValue(int dataPointId) {
		/*long maxTs = ejt.queryForLong(
				"select max(ts) from pointValues where dataPointId=?",
				new Object[] { dataPointId }, 0);
		if (maxTs == 0)
			return null;
		return pointValueQuery(POINT_VALUE_SELECT
				+ " where pv.dataPointId=? and pv.ts=?", new Object[] {
				dataPointId, maxTs });*/
		return pointValuesService.getLatestPointValue(dataPointId);
	}

	/*private PointValueTime getPointValue(long id) {
		return pointValueQuery(POINT_VALUE_SELECT + " where pv.id=?",
				new Object[] { id });
		
	}*/

	public PointValueTime getPointValueBefore(int dataPointId, long time) {
		/*Long valueTime = queryForObject(
				"select max(ts) from pointValues where dataPointId=? and ts<?",
				new Object[] { dataPointId, time }, Long.class, null);
		if (valueTime == null)
			return null;
		return getPointValueAt(dataPointId, valueTime);*/
		return pointValuesService.getPointValueBefore(dataPointId, time);
	}

	public PointValueTime getPointValueAt(int dataPointId, long time) {
		/*return pointValueQuery(POINT_VALUE_SELECT
				+ " where pv.dataPointId=? and pv.ts=?", new Object[] {
				dataPointId, time });*/
		return pointValuesService.getPointValueAt(dataPointId, time);
	}

	/*private PointValueTime pointValueQuery(String sql, Object[] params) {
		List<PointValueTime> result = pointValuesQuery(sql, params, 1);
		if (result.size() == 0)
			return null;
		return result.get(0);
	}*/

	/*private List<PointValueTime> pointValuesQuery(String sql, Object[] params,
			int limit) {
		List<PointValueTime> result = query(sql, params,
				new PointValueRowMapper(), limit);
		updateAnnotations(result);
		return result;
	}*/

	public void getPointValuesBetween(int dataPointId, long from, long to,
			MappedRowCallback<PointValueTime> callback) {
		/*query(POINT_VALUE_SELECT
				+ " where pv.dataPointId=? and pv.ts >= ? and pv.ts<? order by ts",
				new Object[] { dataPointId, from, to },
				new PointValueRowMapper(), callback);*/
		//TODO
	}

	/*class PointValueRowMapper implements GenericRowMapper<PointValueTime> {
		public PointValueTime mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MangoValue value = createMangoValue(rs, 1);
			long time = rs.getLong(5);

			int sourceType = rs.getInt(6);
			if (rs.wasNull())
				// No annotations, just return a point value.
				return new PointValueTime(value, time);

			// There was a source for the point value, so return an annotated
			// version.
			return new AnnotatedPointValueTime(value, time, sourceType,
					rs.getInt(7));
		}
	}*/

	/*MangoValue createMangoValue(ResultSet rs, int firstParameter)
			throws SQLException {
		int dataType = rs.getInt(firstParameter);
		MangoValue value;
		switch (dataType) {
		case (DataTypes.NUMERIC):
			value = new NumericValue(rs.getDouble(firstParameter + 1));
			break;
		case (DataTypes.BINARY):
			value = new BinaryValue(rs.getDouble(firstParameter + 1) == 1);
			break;
		case (DataTypes.MULTISTATE):
			value = new MultistateValue(rs.getInt(firstParameter + 1));
			break;
		case (DataTypes.ALPHANUMERIC):
			String s = rs.getString(firstParameter + 2);
			if (s == null)
				s = rs.getString(firstParameter + 3);
			value = new AlphanumericValue(s);
			break;
		case (DataTypes.IMAGE):
			value = new ImageValue(Integer.parseInt(rs
					.getString(firstParameter + 2)),
					rs.getInt(firstParameter + 3));
			break;
		default:
			value = null;
		}
		return value;
	}*/

	/*private void updateAnnotations(List<PointValueTime> values) {
		Map<Integer, List<AnnotatedPointValueTime>> userIds = new HashMap<Integer, List<AnnotatedPointValueTime>>();
		List<AnnotatedPointValueTime> alist;

		// Look for annotated point values.
		AnnotatedPointValueTime apv;
		for (PointValueTime pv : values) {
			if (pv instanceof AnnotatedPointValueTime) {
				apv = (AnnotatedPointValueTime) pv;
				if (apv.getSourceType() == SetPointSource.Types.USER) {
					alist = userIds.get(apv.getSourceId());
					if (alist == null) {
						alist = new ArrayList<AnnotatedPointValueTime>();
						userIds.put(apv.getSourceId(), alist);
					}
					alist.add(apv);
				}
			}
		}

		// Get the usernames from the database.
		if (userIds.size() > 0)
			updateAnnotations("select id, username from users where id in ",
					userIds);
	}*/

	/*private void updateAnnotations(String sql,
			Map<Integer, List<AnnotatedPointValueTime>> idMap) {
		// Get the description information from the database.
		List<IntValuePair> data = query(
				sql + "(" + createDelimitedList(idMap.keySet(), ",", null)
						+ ")", new GenericIntValuePairRowMapper());

		// Collate the data with the id map, and set the values in the
		// annotations
		List<AnnotatedPointValueTime> annos;
		for (IntValuePair ivp : data) {
			annos = idMap.get(ivp.getKey());
			for (AnnotatedPointValueTime avp : annos)
				avp.setSourceDescriptionArgument(ivp.getValue());
		}
	}*/

	//
	//
	// Multiple-point callback for point history replays
	//
	/*private static final String POINT_ID_VALUE_SELECT = "select pv.dataPointId, pv.dataType, pv.pointValue, " //
			+ "pva.textPointValueShort, pva.textPointValueLong, pv.ts "
			+ "from pointValues pv "
			+ "  left join pointValueAnnotations pva on pv.id = pva.pointValueId";*/

	public void getPointValuesBetween(List<Integer> dataPointIds, long from,
			long to, MappedRowCallback<IdPointValueTime> callback) {
		/*String ids = createDelimitedList(dataPointIds, ",", null);
		query(POINT_ID_VALUE_SELECT + " where pv.dataPointId in (" + ids
				+ ") and pv.ts >= ? and pv.ts<? order by ts", new Object[] {
				from, to }, new IdPointValueRowMapper(), callback);*/
		//TODO
	}

	/**
	 * Note: this does not extract source information from the annotation.
	 */
	/*class IdPointValueRowMapper implements GenericRowMapper<IdPointValueTime> {
		public IdPointValueTime mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			int dataPointId = rs.getInt(1);
			MangoValue value = createMangoValue(rs, 2);
			long time = rs.getLong(6);
			return new IdPointValueTime(dataPointId, value, time);
		}
	}*/

	//
	//
	// Point value deletions
	//
	/*public long deletePointValuesBefore(int dataPointId, long time) {
		return deletePointValues(
				"delete from pointValues where dataPointId=? and ts<?",
				new Object[] { dataPointId, time });
	}*/

	/*public long deletePointValues(int dataPointId) {
		return deletePointValues("delete from pointValues where dataPointId=?",
				new Object[] { dataPointId });
	}*/

	/*public long deleteAllPointData() {
		return deletePointValues("delete from pointValues", null);
	}*/

	/*public long deletePointValuesWithMismatchedType(int dataPointId,
			int dataType) {
		return deletePointValues(
				"delete from pointValues where dataPointId=? and dataType<>?",
				new Object[] { dataPointId, dataType });
	}*/

	/*public void compressTables() {
		Common.ctx.getDatabaseAccess().executeCompress(ejt);
	}*/

	/*private long deletePointValues(String sql, Object[] params) {
		int cnt;
		if (params == null)
			cnt = ejt.update(sql);
		else
			cnt = ejt.update(sql, params);
		clearUnsavedPointValues();
		return cnt;
	}*/

	public long dateRangeCount(int dataPointId, long from, long to) {
		/*return ejt
				.queryForLong(
						"select count(*) from pointValues where dataPointId=? and ts>=? and ts<=?",
						new Object[] { dataPointId, from, to });*/
		return pointValuesService.dateRangeCount(dataPointId, from, to);
	}

	public long getInceptionDate(int dataPointId) {
		/*return ejt.queryForLong(
				"select min(ts) from pointValues where dataPointId=?",
				new Object[] { dataPointId }, -1);*/
		return pointValuesService.getInceptionDate(dataPointId);
	}

	public long getStartTime(List<Integer> dataPointIds) {
		/*if (dataPointIds.isEmpty())
			return -1;
		return ejt
				.queryForLong("select min(ts) from pointValues where dataPointId in ("
						+ createDelimitedList(dataPointIds, ",", null) + ")");*/
		return pointValuesService.getStartTime(dataPointIds);
	}

	public long getEndTime(List<Integer> dataPointIds) {
		/*if (dataPointIds.isEmpty())
			return -1;
		return ejt
				.queryForLong("select max(ts) from pointValues where dataPointId in ("
						+ createDelimitedList(dataPointIds, ",", null) + ")");*/
		return pointValuesService.getEndTime(dataPointIds);
	}

	public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
		/*if (dataPointIds.isEmpty())
			return null;
		return queryForObject(
				"select min(ts), max(ts) from pointValues where dataPointId in ("
						+ createDelimitedList(dataPointIds, ",", null) + ")",
				null, new GenericRowMapper<LongPair>() {
					@Override
					public LongPair mapRow(ResultSet rs, int index)
							throws SQLException {
						long l = rs.getLong(1);
						if (rs.wasNull())
							return null;
						return new LongPair(l, rs.getLong(2));
					}
				}, null);*/
		return pointValuesService.getStartAndEndTime(dataPointIds);
	}

	public List<Long> getFiledataIds() {
		/*return queryForList(
				"select distinct id from ( " //
						+ "  select id as id from pointValues where dataType="
						+ DataTypes.IMAGE
						+ "  union"
						+ "  select d.pointValueId as id from reportInstanceData d "
						+ "    join reportInstancePoints p on d.reportInstancePointId=p.id"
						+ "  where p.dataType="
						+ DataTypes.IMAGE
						+ ") a order by 1", new Object[] {}, Long.class);*/
		return pointValuesService.getFiledataIds();
		
	}

	/**
	 * Class that stored point value data when it could not be saved to the
	 * database due to concurrency errors.
	 * 
	 * @author Matthew Lohbihler
	 */
	/*class UnsavedPointValue {
		private final int pointId;
		private final PointValueTime pointValue;
		private final SetPointSource source;

		public UnsavedPointValue(int pointId, PointValueTime pointValue,
				SetPointSource source) {
			this.pointId = pointId;
			this.pointValue = pointValue;
			this.source = source;
		}

		public int getPointId() {
			return pointId;
		}

		public PointValueTime getPointValue() {
			return pointValue;
		}

		public SetPointSource getSource() {
			return source;
		}
	}

	class BatchWriteBehindEntry {
		private final int pointId;
		private final int dataType;
		private final double dvalue;
		private final long time;

		public BatchWriteBehindEntry(int pointId, int dataType, double dvalue,
				long time) {
			this.pointId = pointId;
			this.dataType = dataType;
			this.dvalue = dvalue;
			this.time = time;
		}

		public void writeInto(Object[] params, int index) {
			index *= POINT_VALUE_INSERT_VALUES_COUNT;
			params[index++] = pointId;
			params[index++] = dataType;
			params[index++] = dvalue;
			params[index++] = time;
		}
	}

	static class BatchWriteBehind implements WorkItem {
		private static final ObjectQueue<BatchWriteBehindEntry> ENTRIES = new ObjectQueue<PointValueDao.BatchWriteBehindEntry>();
		private static final CopyOnWriteArrayList<BatchWriteBehind> instances = new CopyOnWriteArrayList<BatchWriteBehind>();
		private static Log LOG = LogFactory.getLog(BatchWriteBehind.class);
		private static final int SPAWN_THRESHOLD = 10000;
		private static final int MAX_INSTANCES = 5;
		private static int MAX_ROWS = 1000;
		private static final IntegerMonitor ENTRIES_MONITOR = new IntegerMonitor(
				"BatchWriteBehind.ENTRIES_MONITOR", null);
		private static final IntegerMonitor INSTANCES_MONITOR = new IntegerMonitor(
				"BatchWriteBehind.INSTANCES_MONITOR", null);

		static {
			if (Common.ctx.getDatabaseAccess().getType() == DatabaseAccess.DatabaseType.DERBY)
				// This has not bee tested to be optimal
				MAX_ROWS = 1000;
			else if (Common.ctx.getDatabaseAccess().getType() == DatabaseAccess.DatabaseType.MSSQL)
				// MSSQL has max rows of 1000, and max parameters of 2100. In
				// this case that works out to...
				MAX_ROWS = 524;
			else if (Common.ctx.getDatabaseAccess().getType() == DatabaseAccess.DatabaseType.MYSQL)
				// This appears to be an optimal value
				MAX_ROWS = 2000;
			else if (Common.ctx.getDatabaseAccess().getType() == DatabaseAccess.DatabaseType.POSTGRES)
				// This has not bee tested to be optimal
				MAX_ROWS = 2000;
			else if (Common.ctx.getDatabaseAccess().getType() == DatabaseAccess.DatabaseType.ORACLE11G)
				// This has not been tested to be optimal
				MAX_ROWS = 2000;
			else
				throw new ShouldNeverHappenException("Unknown database type: "
						+ Common.ctx.getDatabaseAccess().getType());

			Common.MONITORED_VALUES.addIfMissingStatMonitor(ENTRIES_MONITOR);
			Common.MONITORED_VALUES.addIfMissingStatMonitor(INSTANCES_MONITOR);
		}

		static void add(BatchWriteBehindEntry e, PointValueDao dao) {
			synchronized (ENTRIES) {
				ENTRIES.push(e);
				ENTRIES_MONITOR.setValue(ENTRIES.size());
				if (ENTRIES.size() > instances.size() * SPAWN_THRESHOLD) {
					if (instances.size() < MAX_INSTANCES) {
						BatchWriteBehind bwb = new BatchWriteBehind(dao);
						instances.add(bwb);
						INSTANCES_MONITOR.setValue(instances.size());
						try {
							Common.ctx.getBackgroundProcessing().addWorkItem(
									bwb);
						} catch (RejectedExecutionException ree) {
							instances.remove(bwb);
							INSTANCES_MONITOR.setValue(instances.size());
							throw ree;
						}
					}
				}
			}
		}

		private final PointValueDao dao;

		public BatchWriteBehind(PointValueDao dao) {
			this.dao = dao;
		}

		public void execute() {
			try {
				BatchWriteBehindEntry[] inserts;
				while (true) {
					synchronized (ENTRIES) {
						if (ENTRIES.size() == 0)
							break;

						inserts = new BatchWriteBehindEntry[ENTRIES.size() < MAX_ROWS ? ENTRIES
								.size() : MAX_ROWS];
						ENTRIES.pop(inserts);
						ENTRIES_MONITOR.setValue(ENTRIES.size());
					}

					// Create the sql and parameters
					Object[][] params = new Object[inserts.length][POINT_VALUE_INSERT_VALUES_COUNT];

					for (int i = 0; i < inserts.length; i++) {
						inserts[i].writeInto(params[i], 0);
					}

					// Insert the data
					int retries = 10;
					while (true) {
						try {
							dao.executeBatchUpdate(POINT_VALUE_INSERT, params);
							break;
						} catch (ConcurrencyFailureException e) {
							if (retries <= 0) {
								LOG.error("Concurrency failure saving "
										+ inserts.length
										+ " batch inserts after 10 tries. Data lost.");
								break;
							}

							int wait = (10 - retries) * 100;
							try {
								if (wait > 0) {
									synchronized (this) {
										wait(wait);
									}
								}
							} catch (InterruptedException ie) {
								// no op
							}

							retries--;
						} catch (RuntimeException e) {
							LOG.error("Error saving " + inserts.length
									+ " batch inserts. Data lost.", e);
							break;
						}
					}
				}
			} finally {
				instances.remove(this);
				INSTANCES_MONITOR.setValue(instances.size());
			}
		}

		public int getPriority() {
			return WorkItem.PRIORITY_HIGH;
		}
	}*/

	/*public void executeBatchUpdate(String sql, Object[][] params) {
		batchUpdate(sql, params);
	}*/
}
