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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DataPointsCacheWhenStart implements DataSourcePointsCache {

	private static final Log LOG = LogFactory.getLog(DataSourcePointsCache.class);
	
	private boolean cacheEnabled = false;
	private final DataPointService dataPointService;

	private final ReentrantReadWriteLock dataSourcesLock = new ReentrantReadWriteLock(true);
	
	private Map<Integer, List<DataPointVO>> dataSources = new ConcurrentHashMap<>();

	public DataPointsCacheWhenStart() {
		this.dataPointService = new DataPointService();
	}

	@Override
	public List<DataPointVO> getDataPoints(int dataSourceId) {

		LOG.info("I'm using a cache with datasources points");

		dataSourcesLock.readLock().lock();
		try {
			if (cacheEnabled) {
				return dataSources.get(dataSourceId);
			}
		} finally {
			dataSourcesLock.readLock().unlock();
		}
		return dataPointService.getDataPoints(dataSourceId, null);
	}

	@Override
	public void cacheFinalized() {
		dataSourcesLock.writeLock().lock();
		try {
			if (ScadaConfig.getInstance().getBoolean(ScadaConfig.USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY, false)) {
				cronInitialize();
				cacheEnabled = true;
			} else {
				cacheEnabled = false;
			}
			LOG.info("Cache data points finalized");
		} catch (IOException e) {
			LOG.error(e);
		} finally {
			dataSourcesLock.writeLock().unlock();
		}
	}

	@Override
	public boolean isCacheEnabled() {
		dataSourcesLock.readLock().lock();
		try {
			return cacheEnabled;
		} finally {
			dataSourcesLock.readLock().unlock();
		}
	}

	@Override
	public void cacheInitialize() {
		
		List<DataPointVO> dps = dataPointService.getDataPoints(null, true);

		dataSourcesLock.writeLock().lock();
		try {
			dataSources = composeCacheData(dps);
			cacheEnabled = true;
		} finally {
			dataSourcesLock.writeLock().unlock();
			LOG.info("Cache data points initialized");
		}
	}

	@Override
	public DataPointVO getDataPoint(int dataPointId) {
		dataSourcesLock.readLock().lock();
		try {
			if (cacheEnabled) {
				return getPoints(this.dataSources).stream()
						.filter(point -> point.getId() == dataPointId)
						.findFirst()
						.orElse(null);
			}
		} finally {
			dataSourcesLock.readLock().unlock();
		}
		return dataPointService.getDataPoint(dataPointId);
	}

	@Override
	public DataPointVO getDataPoint(String dataPointXid) {
		dataSourcesLock.readLock().lock();
		try {
			if (cacheEnabled) {
				return getPoints(this.dataSources).stream()
						.filter(point -> point.getXid().equals(dataPointXid))
						.findFirst()
						.orElse(null);
			}
		} finally {
			dataSourcesLock.readLock().unlock();
		}
		return dataPointService.getDataPoint(dataPointXid);
	}

	@Override
	public int size() {
		dataSourcesLock.readLock().lock();
		try {
			return dataSources.size();
		} finally {
			dataSourcesLock.readLock().unlock();
		}
	}

	@Override
	public void cacheDestroy() {
		dataSourcesLock.writeLock().lock();
		try {
			dataSources.clear();
			dataSources = null;
			cacheEnabled = false;
		} finally {
			dataSourcesLock.writeLock().unlock();
			LOG.info("Cache data points destroyed");
		}
	}

	private void cronInitialize() throws java.io.IOException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
		String cronExpression = ScadaConfig.getInstance().getProperty(ScadaConfig.CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS);
		ApplicationBeans.getBean("updateDataSourcesPointsScheduler", CronTriggerScheduler.class).schedule(cronExpression);
		LOG.info("Cache data points scheduler initialized");
	}

	private Map<Integer, List<DataPointVO>> composeCacheData(List<DataPointVO> dps) {

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

	private static List<DataPointVO> getPoints(Map<Integer, List<DataPointVO>> pointsBySource) {
		return pointsBySource.values().stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}
}
