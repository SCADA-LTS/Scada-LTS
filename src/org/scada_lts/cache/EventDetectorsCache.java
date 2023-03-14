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
package org.scada_lts.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.EventDetectorsCacheDAO;
import org.scada_lts.dao.model.PointEventDetectorCacheEntry;
import org.scada_lts.quartz.UpdateEventDetectors;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

/**
 * Class responsible for data buffering of EventDetectors
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
@Deprecated
public class EventDetectorsCache extends EventDetectorsCacheDAO{
	
	public static final Log LOG = LogFactory.getLog(EventDetectorsCache.class);
	private static EventDetectorsCache instance = null;
	private int countBuffer;
	
	public static EventDetectorsCache getInstance() throws SchedulerException, IOException {
		if (LOG.isTraceEnabled()) {
		  LOG.trace("Get EventDetectorsCache instance ");
		}
		if (instance == null) {
			instance = new EventDetectorsCache();
		}
		return instance;
	}
	
	public List<PointEventDetectorVO> getEventDetectors(DataPointVO dp) {
		countBuffer++;
	
		if (LOG.isTraceEnabled()) {
			LOG.trace("getEventDetectors count from buffer:" + countBuffer);
		}
		
		if (mapEventDetectors == null) {
			LOG.error(new Exception("Cache was null"));
			return new ArrayList<PointEventDetectorVO>();
		}
		
		if (mapEventDetectors.isEmpty()) {
			return new ArrayList<PointEventDetectorVO>();
		}
		
		List<PointEventDetectorVO> listPointEventDetectorVO = mapEventDetectors.get(dp.getId());
		
		if (listPointEventDetectorVO == null) {
			return new ArrayList<PointEventDetectorVO>();
		} else {
			for ( PointEventDetectorVO pedVO :listPointEventDetectorVO) {
				pedVO.njbSetDataPoint(dp);
			}
			return listPointEventDetectorVO;
		}
		
	}
	
	/**
	 * Method reset counter.
	 * @see UpdateEventDetectors
	 */
	public void resetCountBuffer() {
		countBuffer = 0;
	}
	
	/**
	 * Method set ListPointEventDetector
	 * @see UpdateEventDetectors
	 * @param mapEventDetectors
	 */
	public void setMapEventDetectorForDataPoint(TreeMap<Integer, List<PointEventDetectorVO>> mapEventDetectors) {
		this.mapEventDetectors = mapEventDetectors;
	}

	private EventDetectorsCache() throws SchedulerException, IOException {
		if (LOG.isTraceEnabled()) {
		  LOG.trace("Create EventDetectorsCache");
		}
		List<PointEventDetectorCacheEntry> listEventDetector = getAll();
		TreeMap<Integer, List<PointEventDetectorVO>> mapEventDetector = getMapEventDetectors(listEventDetector);
		setMapEventDetectorForDataPoint(mapEventDetector);
		cacheInitialize();
	}
	
	private TreeMap<Integer, List<PointEventDetectorVO>> mapEventDetectors;
	
	private void cacheInitialize() throws SchedulerException, IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
		JobDetail job = new JobDetail();
		job.setName("UpdateEventDetectors");
		job.setJobClass(UpdateEventDetectors.class);

		SimpleTrigger trigger = new SimpleTrigger();
		Date startTime = new Date(System.currentTimeMillis()
				+ ScadaConfig.getInstance().getLong(ScadaConfig.START_UPDATE_EVENT_DETECTORS, 10_000_000));
		if (LOG.isTraceEnabled()) {
			LOG.trace("Quartz - startTime:" + startTime);
		}
		trigger.setStartTime(startTime);
		trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		Long interval = ScadaConfig.getInstance().getLong(ScadaConfig.MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS, 5_000_000);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Quartz - interval:" + interval);
		}
		trigger.setRepeatInterval(interval);
		trigger.setName("Quartz - trigger-UpdateEventDetectors");

		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.scheduleJob(job, trigger);
	}
	
}
