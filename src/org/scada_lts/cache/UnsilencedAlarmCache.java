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
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.UnsilencedAlarmDAO;
import org.scada_lts.dao.model.UnsilencedAlarmLevelCache;
import org.scada_lts.quartz.UpdateUnsilencedAlarmLevel;
import org.scada_lts.service.UserHighestAlarmLevelService;

/** 
 * Class responsible for buffering data of UnsilencedAlarm
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class UnsilencedAlarmCache extends UnsilencedAlarmDAO {

	private static final Log LOG = LogFactory.getLog(UnsilencedAlarmCache.class);
	private static UnsilencedAlarmCache instance = null;
	private int countBuffer;
	
	private TreeMap<Integer, Integer> mapUnsilencedAlarmLevelForUser;

	public static UnsilencedAlarmCache getInstance() throws SchedulerException, IOException {
		LOG.trace("Get UnsilencedAlarmCached instance ");
		if (instance == null) {
			instance = new UnsilencedAlarmCache();
		}
		return instance;
	}

	/**
	 * Result highest unsilenced alarm level for user. 
	 * @param userId
	 * @return
	 */
	public int getHighestUnsilencedAlarmLevel(int userId) {
		countBuffer++;
		LOG.trace("HighestUnsilencedAlarmLevel count from buffer:" + countBuffer);
		if (mapUnsilencedAlarmLevelForUser.isEmpty()) {
			return -1;
		}
		Integer highestUnsilencedAlarmLevel = mapUnsilencedAlarmLevelForUser.get(userId);
		if (highestUnsilencedAlarmLevel == null) {
			return 0;
		} else {
			return highestUnsilencedAlarmLevel;
		}
	};

	/**
	 * Set unsilenced alarm level.
	 * @param mapUnsilencedAlarmLevelForUser
	 */
	public void setMapUnsilencedAlarmLevelForUser(TreeMap<Integer, Integer> mapUnsilencedAlarmLevelForUser) {
		this.mapUnsilencedAlarmLevelForUser = mapUnsilencedAlarmLevelForUser;
		UserHighestAlarmLevelService.getInstance().updateUserAlarmLevels(mapUnsilencedAlarmLevelForUser);
	}

	/**
	 * Reset counter
	 */
	public void resetCountBuffer() {
		countBuffer = 0;
	}
	
	private UnsilencedAlarmCache() throws SchedulerException, IOException {
		LOG.trace("Create UnsilencedAlarmCached");
		List<UnsilencedAlarmLevelCache> listUnsilencedAlarmLevel = getAll();
		TreeMap<Integer, Integer> mapUnsilencedAlarmLevel = getMapUnsilencedAlarmLevelForUser(listUnsilencedAlarmLevel);
		setMapUnsilencedAlarmLevelForUser(mapUnsilencedAlarmLevel);
		cacheInitialize();
	}

	private void cacheInitialize() throws SchedulerException, IOException {
		LOG.trace("cacheInitialize");
		JobDetail job = new JobDetail();
		job.setName("UpdateUnsilencedAlarmLevel");
		job.setJobClass(UpdateUnsilencedAlarmLevel.class);

		SimpleTrigger trigger = new SimpleTrigger();
		Date startTime = new Date(System.currentTimeMillis()
				+ ScadaConfig.getInstance().getLong(ScadaConfig.START_UPDATE_UNSILENCED_ALARM_LEVEL, 10_000_000));
		LOG.trace("Quartz - startTime:" + startTime);
		trigger.setStartTime(startTime);
		trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		Long interval = ScadaConfig.getInstance().getLong(ScadaConfig.MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL, 5_000_000);
		LOG.trace("Quartz - interval:" + interval);
		trigger.setRepeatInterval(interval);
		trigger.setName("Quartz - trigger-UpdateUnsilencedAlarmLevel");

		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.scheduleJob(job, trigger);
	}

}
