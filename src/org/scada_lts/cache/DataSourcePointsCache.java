package org.scada_lts.cache;

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.DataPointDAO;
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
	
	private Map<Long, List<DataPointVO>> dss = new ConcurrentHashMap<>();

	private DataSourcePointsCache() {
		
	}
	
	public static DataSourcePointsCache getInstance() {
		if (instance==null) {
			instance = new DataSourcePointsCache();
		}
		return instance;
	}

	@Override
	public List<DataPointVO> getDataPoints(Long dataSourceId) {

		LOG.info("I'm using a cache with datasources points");

		if (cacheEnabled) {
			return dss.get(dataSourceId);
		} else {
			throw new RuntimeException("Cache may work only when scada cacheEnabled");
		}
	}

	@Deprecated(since = "2.8.1")
	public void setData(Map<Long, List<DataPointVO>> dss) {
		this.dss = dss;
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
	
	public Map<Long, List<DataPointVO>> composeCashData(List<DataPointVO> dps) {
		
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

	@Override
	public DataPointVO getDataPoint(int dataPointId) {
		if (cacheEnabled) {
			return getPoints(this.dss).stream()
					.filter(point -> point.getId() == dataPointId)
					.findFirst()
					.orElse(null);
		} else {
			return null;
		}
	}

	private void cronInitialize() throws java.io.IOException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
		String cronExpression = ScadaConfig.getInstance().getProperty(ScadaConfig.CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS);
		ApplicationBeans.getBean("updateDataSourcesPointsScheduler", CronTriggerScheduler.class).schedule(cronExpression);
	}

	private static List<DataPointVO> getPoints(Map<Long, List<DataPointVO>> pointsBySource) {
		return pointsBySource.values().stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}
}
