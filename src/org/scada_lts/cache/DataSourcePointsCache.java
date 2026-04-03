package org.scada_lts.cache;

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.quartz.CronTriggerScheduler;
import org.scada_lts.web.beans.ApplicationBeans;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class DataSourcePointsCache implements IDataPointsCacheWhenStart {

	private static final Log LOG = LogFactory.getLog(DataSourcePointsCache.class);
	
	private boolean cacheEnabled = false;
	
	private static DataSourcePointsCache instance = null;
	private final DataPointService dataPointService = new DataPointService();
	
	private Map<Integer, List<DataPointVO>> dss = new ConcurrentHashMap<>();

	private DataSourcePointsCache() {
		
	}
	
	public static DataSourcePointsCache getInstance() {
		if (instance==null) {
			instance = new DataSourcePointsCache();
		}
		return instance;
	}

	@Override
	@Deprecated(since = "2.8.1")
	public List<DataPointVO> getDataPoints(Long dataSourceId) {

		LOG.info("I'm using a cache with datasources points");

		if (cacheEnabled) {
			return dss.get(dataSourceId != null ? dataSourceId.intValue() : -1);
		} else {
			throw new RuntimeException("Cache may work only when scada cacheEnabled");
		}
	}

	@Override
	public List<DataPointVO> getDataPoints(int dataSourceId) {

		LOG.info("I'm using a cache with datasources points");

		if (cacheEnabled) {
			return dss.get(dataSourceId);
		} else {
			return dataPointService.getDataPoints(dataSourceId, null);
		}
	}

	@Deprecated(since = "2.8.1")
	public void setData(Map<Long, List<DataPointVO>> dss) {
		this.dss = dss.entrySet().stream().collect(Collectors.toMap(a -> a.getKey().intValue(), Map.Entry::getValue));
	}

	@Override
	public void cacheFinalized() {
		try {
			if (ScadaConfig.getInstance().getBoolean(ScadaConfig.USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY, false)) {
				cronInitialize();
				cacheEnabled = true;
			} else {
				cacheEnabled = false;
				instance = null;
			}
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	@Override
	public void cacheInitialize() {
		
		List<DataPointVO> dps = dataPointService.getDataPoints(null, true);
		
		dss = composeCashData(dps);
		
		cacheEnabled = true;
	}

	@Deprecated(since = "2.8.1")
	public Map<Long, List<DataPointVO>> composeCashDataOld(List<DataPointVO> dps) {
		
		Map<Long, List<DataPointVO>> dss = new ConcurrentHashMap<>();
		if (dps != null && !dps.isEmpty()) {
			for (DataPointVO dp : dps) {
				List<DataPointVO> cacheDs = dss.get((long)dp.getDataSourceId()); 
				if (cacheDs==null) {
					cacheDs = new CopyOnWriteArrayList<>();
					cacheDs.add(dp);
					dss.put((long) dp.getDataSourceId(), cacheDs);			
				} else {
					cacheDs.add(dp);
				}
			}
		}
		return dss;
	}

	public Map<Integer, List<DataPointVO>> composeCashData(List<DataPointVO> dps) {

		Map<Integer, List<DataPointVO>> dss = new ConcurrentHashMap<>();
		if (dps != null && !dps.isEmpty()) {
			for (DataPointVO dp : dps) {
				List<DataPointVO> cacheDs = dss.get(dp.getDataSourceId());
				if (cacheDs==null) {
					cacheDs = new CopyOnWriteArrayList<>();
					cacheDs.add(dp);
					dss.put(dp.getDataSourceId(), cacheDs);
				} else {
					cacheDs.add(dp);
				}
			}
		}
		return dss;
	}

	@Override
	public DataPointVO getDataPoint(int dataPointId) {
		if (cacheEnabled) {
			return getPoints(this.dss).stream()
					.filter(point -> point.getId() == dataPointId)
					.findFirst()
					.orElse(null);
		} else {
			return dataPointService.getDataPoint(dataPointId);
		}
	}

	@Override
	public DataPointVO getDataPoint(String dataPointXid) {
		if (cacheEnabled) {
			return getPoints(this.dss).stream()
					.filter(point -> point.getXid().equals(dataPointXid))
					.findFirst()
					.orElse(null);
		} else {
			return dataPointService.getDataPoint(dataPointXid);
		}
	}

	private void cronInitialize() throws java.io.IOException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
		String cronExpression = ScadaConfig.getInstance().getProperty(ScadaConfig.CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS);
		ApplicationBeans.getBean("updateDataSourcesPointsScheduler", CronTriggerScheduler.class).schedule(cronExpression);
	}

	private static List<DataPointVO> getPoints(Map<Integer, List<DataPointVO>> pointsBySource) {
		return pointsBySource.values().stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}
}
