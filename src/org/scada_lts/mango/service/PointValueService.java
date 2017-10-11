/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.mango.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.Configurations;
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.model.point.PointValueAdnnotation;
import org.scada_lts.dao.pointvalues.PointValueAdnnotationsDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.mango.adapter.MangoPointValues;
import org.slf4j.profiler.Profiler;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.io.StreamUtils;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.ImageSaveException;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.vo.AnonymousUser;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.monitor.IntegerMonitor;
import com.serotonin.util.queue.ObjectQueue;

/** 
 * Base on the PointValueDao
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
@Service
public class PointValueService implements MangoPointValues {
	 
	private static List<UnsavedPointValue> UNSAVED_POINT_VALUES = new ArrayList<UnsavedPointValue>();
	private static final int POINT_VALUE_INSERT_VALUES_COUNT = 4;

	private static PointValueAdnnotationsDAO pointValueAnnotationsDAO = new PointValueAdnnotationsDAO();

	public PointValueService() {
		
	}

	/**
	 * Only the PointValueCache should call this method during runtime. Do not
	 * use.
	 */
	public PointValueTime savePointValueSync(int pointId,
			PointValueTime pointValue, SetPointSource source) {
		long id = savePointValueImpl(pointId, pointValue, source, false);

		PointValueTime savedPointValue;
		int retries = 5;
		while (true) {
			try {
				savedPointValue = PointValueDAO.getInstance().findById(new Object[]{id}).getPointValue();
				break;
			} catch (ConcurrencyFailureException e) {
				if (retries <= 0)
					throw e;
				retries--;
			}
		}

		return savedPointValue;
	}

	/**
	 * Only the PointValueCache should call this method during runtime. Do not
	 * use.
	 */
	public void savePointValueAsync(int pointId, PointValueTime pointValue,
			SetPointSource source) {
		
		Profiler profiler = null;
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler = new Profiler("savePointValueAsync");
			profiler.start("start");
		}
		
		long id = savePointValueImpl(pointId, pointValue, source, true);
		if (id != -1)
			clearUnsavedPointValues();
		
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler.stop().print();
		}
	}

	
	public long savePointValueImpl(final int pointId, final PointValueTime pointValue,
			final SetPointSource source, boolean async) {
		
		Profiler profiler = null;
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler = new Profiler("savePointValueImpl");
			profiler.start("start");
		}
			
		MangoValue value = pointValue.getValue();
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
				id = savePointValueInTrasaction(pointId, dataType, dvalueFinal, pointValue.getTime(), svalueFinal, source, false);
				
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

		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler.stop().print();
		}
		return id;
	}

	public void clearUnsavedPointValues() {
		if (!UNSAVED_POINT_VALUES.isEmpty()) {
			synchronized (UNSAVED_POINT_VALUES) {
				while (!UNSAVED_POINT_VALUES.isEmpty()) {
					UnsavedPointValue data = UNSAVED_POINT_VALUES.remove(0);
					savePointValueImpl(data.getPointId(), data.getPointValue(),
							data.getSource(), false);
				}
			}
		}
	}

	public void savePointValue(int pointId, PointValueTime pointValue) {
		savePointValueImpl(pointId, pointValue, new AnonymousUser(), true);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	long savePointValueInTrasaction(final int pointId, final int dataType, double dvalue, final long time, final String svalue, final SetPointSource source,	boolean async) {
		// Apply database specific bounds on double values.
		
		Profiler profiler = null;
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler = new Profiler("savePointValueInTrasaction");
			profiler.start("start");
		}
			
		dvalue = PointValueDAO.getInstance().applyBounds(dvalue);

		if (async) {
			BatchWriteBehind.add(new BatchWriteBehindEntry(pointId, dataType,
					dvalue, time), this);
			return -1;
		}

		int retries = 5;
		while (true) {
			try {
				
				long id = savePointValueImpl(pointId, dataType, dvalue, time,
						svalue, source);
				if (Configurations.getInstance().getCheckPerformance().getConfig()) {
					profiler.stop().print();
				}
				return id;
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
	}	
	
	
	public long savePointValue(final int pointId, final int dataType, double dvalue,
			final long time, final String svalue, final SetPointSource source,
			boolean async) {
		
		Profiler profiler = null;
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler = new Profiler("savePointValue");
			profiler.start("start");
		}
		// Apply database specific bounds on double values.
		dvalue = PointValueDAO.getInstance().applyBounds(dvalue);

		if (async) {
			BatchWriteBehind.add(new BatchWriteBehindEntry(pointId, dataType,
					dvalue, time), this);
			return -1;
		}

		int retries = 5;
		while (true) {
			try {
				long id = savePointValueImpl(pointId, dataType, dvalue, time,
						svalue, source);
				if (Configurations.getInstance().getCheckPerformance().getConfig()) {
					profiler.stop().print();
				}
				return id;
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
	}

	private long savePointValueImpl(int pointId, int dataType, double dvalue, long time, String svalue, SetPointSource source) {
        
		Profiler profiler = null;
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler = new Profiler("savePointValueImpl");
			profiler.start("start");
		}
			
		long id = (Long) PointValueDAO.getInstance().create(pointId, dataType, dvalue, time)[0];
                 
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
			
			PointValueAdnnotation pointValueAdnnotation = new PointValueAdnnotation(id,shortString, longString, sourceType, sourceId);
			
			PointValueAdnnotationsDAO.getInstance().create(pointValueAdnnotation);
           
		}
		
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler.stop().print();
		}

		return id;
	}
	
	//TODO rewrite
	private List<PointValueTime> getLstPointValueTime( List<PointValue> lstIn ) {
			List<PointValueTime> lst = new ArrayList<PointValueTime>();
			
			for (PointValue pv: lstIn) {
				lst.add(pv.getPointValue());
			}
			return lst;
	}

	
	public List<PointValueTime> getPointValues(int dataPointId, long since) {
		List<PointValue> lst = PointValueDAO.getInstance().filtered(
				PointValueDAO.POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP, 
				new Object[]{dataPointId, since}, GenericDaoCR.NO_LIMIT);
		return getLstPointValueTime(lst);
	}

	public List<PointValueTime> getPointValuesBetween(int dataPointId,
			long from, long to) {
		List<PointValue> lst = PointValueDAO.getInstance().filtered(
				PointValueDAO.POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP_FROM_TO, 
				new Object[]{dataPointId, from, to}, GenericDaoCR.NO_LIMIT);
		return getLstPointValueTime(lst);
	}

	public List<PointValueTime> getLatestPointValues(int dataPointId, int limit) {
		List<PointValue> lst =  PointValueDAO.getInstance().filtered(
				PointValueDAO.POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID, 
				new Object[]{dataPointId},limit);
		return getLstPointValueTime(lst);
	}

	public List<PointValueTime> getLatestPointValues(int dataPointId,
			int limit, long before) {
		List<PointValue> lst =  PointValueDAO.getInstance().filtered(
				PointValueDAO.POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID, 
				new Object[]{dataPointId, before},limit);
		return getLstPointValueTime(lst);
	}

	public PointValueTime getLatestPointValue(int dataPointId) {
		Long maxTs = PointValueDAO.getInstance().getLatestPointValue(dataPointId);
		if (maxTs == null || maxTs == 0)
			return null;
		
		List<PointValue> lstValues = PointValueDAO.getInstance().findByIdAndTs(dataPointId, maxTs);
		
		PointValueAdnnotationsDAO.getInstance().updateAnnotations(lstValues);
		if (lstValues.size() == 0)
			return null;
		return lstValues.get(0).getPointValue();
	}

	public PointValueTime getPointValueBefore(int dataPointId, long time) {
		List<PointValue> lst = PointValueDAO.getInstance().filtered(
				PointValueDAO.POINT_VALUE_FILTER_BEFORE_TIME_STAMP_BASE_ON_DATA_POINT_ID, 
				new Object[]{dataPointId,time},1);
		if (lst != null && lst.size() > 0) {
			return lst.get(0).getPointValue();
		} else {
			return null;
		}
	}

	public PointValueTime getPointValueAt(int dataPointId, long time) {
		List<PointValue> lst =PointValueDAO.getInstance().filtered(
				PointValueDAO.POINT_VALUE_FILTER_AT_TIME_STAMP_BASE_ON_DATA_POINT_ID, 
				new Object[]{dataPointId,time},1);
		if (lst != null && lst.size() > 0) {
			return lst.get(0).getPointValue();
		} else {
			return null;
		}
	}
	
	public long deletePointValuesBefore(int dataPointId, long time) {
		return PointValueDAO.getInstance().deletePointValuesBefore(dataPointId,time);
	}
	
	@Override
	public long dateRangeCount(int dataPointId, long from, long to) {
		return PointValueDAO.getInstance().dateRangeCount(dataPointId,from,to);
	}

	@Override
	public long getInceptionDate(int dataPointId) {
		return  PointValueDAO.getInstance().getInceptionDate(dataPointId);
	}

	@Override
	public long getStartTime(List<Integer> dataPointIds) {
		return PointValueDAO.getInstance().getStartTime(dataPointIds);
	}

	@Override
	public long getEndTime(List<Integer> dataPointIds) {
		return PointValueDAO.getInstance().getEndTime(dataPointIds);
	}

	@Override
	public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
		return PointValueDAO.getInstance().getStartAndEndTime(dataPointIds);
	}

	@Override
	public List<Long> getFiledataIds() {
		return PointValueDAO.getInstance().getFiledataIds();
	}

	/**
	 * Class that stored point value data when it could not be saved to the
	 * database due to concurrency errors.
	 * 
	 * @author Matthew Lohbihler
	 */
	class UnsavedPointValue {
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

	//TODO (gb) In my opinion it must rewrite
	static class BatchWriteBehind implements WorkItem {
		private static final ObjectQueue<BatchWriteBehindEntry> ENTRIES = new ObjectQueue<PointValueService.BatchWriteBehindEntry>();
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
			
			MAX_ROWS = 2000;
			
			Common.MONITORED_VALUES.addIfMissingStatMonitor(ENTRIES_MONITOR);
			Common.MONITORED_VALUES.addIfMissingStatMonitor(INSTANCES_MONITOR);
		}

		static void add(BatchWriteBehindEntry e, PointValueService pointValueService) {
			synchronized (ENTRIES) {
				ENTRIES.push(e);
				ENTRIES_MONITOR.setValue(ENTRIES.size());
				if (ENTRIES.size() > instances.size() * SPAWN_THRESHOLD) {
					if (instances.size() < MAX_INSTANCES) {
						BatchWriteBehind bwb = new BatchWriteBehind(pointValueService);
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

		private final PointValueService pointValueService;

		public BatchWriteBehind(PointValueService pointValueService) {
			this.pointValueService = pointValueService;
		}

		public void execute() {
			
			Profiler profiler = null;
			if (Configurations.getInstance().getCheckPerformance().getConfig()) {
				profiler = new Profiler("BatchWriteBehind");
				profiler.start("execute");
			}
				
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
					ArrayList<Object[]> params = new ArrayList<Object[]>();

					for (int i = 0; i < inserts.length; i++) {
						Object[] args = new Object[] { inserts[i].pointId, inserts[i].dataType, inserts[i].dvalue, inserts[i].time };
						params.add(args);
					}

					// Insert the data
					int retries = 10;
					while (true) {
						try {
							if (Configurations.getInstance().getCheckTransForBatchUpdateInsert().getConfig()) {
								PointValueDAO.getInstance().executeBatchUpdateInsertInTransaction(params);
							} else {
								PointValueDAO.getInstance().executeBatchUpdateInsert(params);
							}
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
				if (Configurations.getInstance().getCheckPerformance().getConfig()) {
					profiler.stop().print();
				}
			}
		}

		public int getPriority() {
			return WorkItem.PRIORITY_HIGH;
		}
	}
	
	public PointValueTime getPointValue(long id) {
		return PointValueDAO.getInstance().getPointValue(id);
	}

	public void updatePointValueAnnotations(int userId) {
		pointValueAnnotationsDAO.update(userId);
	}

	@Override
	public long deletePointValues(int dataPointId) {
		return PointValueDAO.getInstance().deletePointValue(dataPointId);
	}

	@Override
	public long deleteAllPointValue() {
		return PointValueDAO.getInstance().deleteAllPointData();
	}

	@Override
	public long deletePointValuesWithMismatchedType(int dataPointId, int dataType) {
		return PointValueDAO.getInstance().deletePointValuesWithMismatchedType(dataPointId, dataType);
	}
	
}

