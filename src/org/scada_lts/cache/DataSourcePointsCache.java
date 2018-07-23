package org.scada_lts.cache;

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.DataPointDAO;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class DataSourcePointsCache implements IDataPointsCacheWhenStart {

	private static final Log LOG = LogFactory.getLog(DataSourcePointsCache.class);
	
	private boolean cacheEnabled = false;
	
	private static DataSourcePointsCache instance = null;
	
	private Map<Long, List<DataPointVO>> dss = new TreeMap<>();
	
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
		} catch (IOException | SchedulerException | ParseException e) {
			LOG.error(e);
		}
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	@Override
	public void cacheInitialize() {
		
		List<DataPointVO> dps = new DataPointDAO().getDataPoints();
		
		dss = composeCashData(dps);
		
		cacheEnabled = true;
	}
	
	public Map<Long, List<DataPointVO>> composeCashData(List<DataPointVO> dps) {
		
		Map<Long, List<DataPointVO>> dss = new TreeMap<>();
		if (dps != null && dps.size()>0) {
			for (DataPointVO dp : dps) {
				List<DataPointVO> cacheDs = dss.get((long)dp.getDataSourceId()); 
				if (cacheDs==null) {
					cacheDs = new ArrayList<>();
					cacheDs.add(dp);
					dss.put((long) dp.getDataSourceId(), cacheDs);			
				} else {
					cacheDs.add(dp);
				}
			}
		}
		return dss;
	}

	private void cronInitialize() throws java.io.IOException, SchedulerException, ParseException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}

		JobDetail job = new JobDetail();
		job.setName("UpdateDataSourcesPoints");
		job.setJobClass(UpdateDataSourcesPoints.class);


		CronTrigger trigger = new CronTrigger();
		trigger.setName("Quartz - trigger-DataSourcePointsCache");
		String cronExpression = ScadaConfig.getInstance().getProperty(ScadaConfig.CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS);
		trigger.setCronExpression(cronExpression);//"0 15 1 ? * *"
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.scheduleJob(job, trigger);

	}

}
