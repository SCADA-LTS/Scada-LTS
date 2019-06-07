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
import org.scada_lts.dao.PendingEventsDAO;
import org.scada_lts.quartz.UpdatePendingEvents;

import com.serotonin.mango.rt.event.EventInstance;

/** 
 * Class responsible for buffering of PendingEvents
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class PendingEventsCache extends PendingEventsDAO{
	
	private static final Log LOG = LogFactory.getLog(PendingEventsCache.class);
	private static PendingEventsCache instance = null;
	private int countBuffer;
	
	public static PendingEventsCache getInstance() throws SchedulerException, IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Get PendingEventsCache instance ");
		}
		if (instance == null) {
			instance = new PendingEventsCache();
		}
		return instance;
	}
	
	/**
	 * Return List of EventInstance base on userId
	 * TODO Ultimately move to interface mango in package org.scada_lts.mango.adapter
	 * @param userId
	 * @return 
	 */
	public List<EventInstance> getPendingEvents(int userId) {
		
		countBuffer++;
		if (LOG.isTraceEnabled()) {
			LOG.trace("getPendingEvents count from buffer:" + countBuffer);
		}
		
		if (mapPendingEvents == null) {
			LOG.error(new Exception("Error cache PendingEvents - null cache"));
			return new ArrayList<EventInstance>();
		}
		
		if (mapPendingEvents.isEmpty()) {
			return new ArrayList<EventInstance>();
		}
		
		List<EventInstance> listEventInstance = mapPendingEvents.get(userId);
		if (listEventInstance == null) {
			return new ArrayList<EventInstance>();
		} 
		return listEventInstance;
	}
	
	/**
	 * Reset counter
	 * @see UpdatePendingEvents
	 */
	public void resetCountBuffer() {
		countBuffer = 0;
	}
	
	/**
	 * Method set map of pending events
	 * @see UpdatePendingEvents
	 * @param mapPendingEvents
	 */
	public void setMapPendingEvents(TreeMap<Integer, List<EventInstance>> mapPendingEvents) {
		this.mapPendingEvents = mapPendingEvents;
	}

	
	private PendingEventsCache() throws SchedulerException, IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Create PendingEventsCache");
		}
		mapPendingEvents = getPendingEvents();
		cacheInitialize();
	}
	
	private TreeMap<Integer, List<EventInstance>> mapPendingEvents;
	
	private void cacheInitialize() throws SchedulerException, IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
		JobDetail job = new JobDetail();
		job.setName("UpdatePendingEvents");
		job.setJobClass(UpdatePendingEvents.class);

		SimpleTrigger trigger = new SimpleTrigger();
		Date startTime = new Date(System.currentTimeMillis()
				+ ScadaConfig.getInstance().getLong(ScadaConfig.START_UPDATE_PENDING_EVENTS, 10_000_000));
		if (LOG.isTraceEnabled()) {
			LOG.trace("Quartz - startTime:" + startTime);
		}
		trigger.setStartTime(startTime);
		trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		Long interval = ScadaConfig.getInstance().getLong(ScadaConfig.MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS, 5_000_000);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Quartz - interval:" + interval);
		}
		trigger.setRepeatInterval(interval);
		trigger.setName("Quartz - trigger-UpdatePendingEvents");

		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.scheduleJob(job, trigger);
	}

}
