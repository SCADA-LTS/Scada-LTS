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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.mango.service.PendingEventService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.quartz.SimpleTriggerScheduler;
import org.scada_lts.quartz.UpdatePendingEvents;

import com.serotonin.mango.rt.event.EventInstance;
import org.scada_lts.web.beans.ApplicationBeans;

/** 
 * Class responsible for buffering of PendingEvents
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class PendingEventsCache {
	
	private static final Log LOG = LogFactory.getLog(PendingEventsCache.class);
	private static PendingEventsCache instance = null;
	private int countBuffer;
	private Map<Integer, List<EventInstance>> mapPendingEvents;
	private final PendingEventService eventService;
	private final SimpleTriggerScheduler scheduler;
	private final SystemSettingsService systemSettingsService;

	public static PendingEventsCache getInstance() throws IOException {
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
			return Collections.emptyList();
		}
		
		if (mapPendingEvents.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<EventInstance> listEventInstance = mapPendingEvents.get(userId);
		if (listEventInstance == null) {
			return Collections.emptyList();
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
	public void setMapPendingEvents(Map<Integer, List<EventInstance>> mapPendingEvents) {
		this.mapPendingEvents = mapPendingEvents;
	}

	
	private PendingEventsCache() throws IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Create PendingEventsCache");
		}
		eventService = new PendingEventService();
		mapPendingEvents = eventService.getPendingEvents();
		scheduler = ApplicationBeans.getBean("updatePendingEventsScheduler", SimpleTriggerScheduler.class);
		systemSettingsService = new SystemSettingsService();

		cacheInitialize();
	}

	private void cacheInitialize() throws IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
		Date startTime = new Date(System.currentTimeMillis()
				+ ScadaConfig.getInstance().getLong(ScadaConfig.START_UPDATE_PENDING_EVENTS, 10_000_000));
		Long interval = ScadaConfig.getInstance().getLong(ScadaConfig.MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS, 5_000_000);
		scheduler.schedule(startTime, interval);
	}

	public void startUpdate() {
		scheduler.start();
	}

	public void stopUpdate() {
		scheduler.stop();
	}

	public void resetUpdate() {
		boolean cacheEnable = systemSettingsService.getMiscSettings().isEventPendingCacheEnabled();
		if(cacheEnable)
			startUpdate();
		else
			stopUpdate();
	}
}
