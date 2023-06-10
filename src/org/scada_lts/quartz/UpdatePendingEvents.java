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

package org.scada_lts.quartz;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.scada_lts.cache.PendingEventsCache;
import org.scada_lts.mango.service.PendingEventService;

/** 
 * Update data job for pending events in cache.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class UpdatePendingEvents implements StatefulJob {
	
	private static final Log LOG = LogFactory.getLog(UpdatePendingEvents.class);
	private final PendingEventService pendingEventService;

	public UpdatePendingEvents() {
		this.pendingEventService = new PendingEventService();
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		LOG.trace("UpdatePendingEvents");		
		try {
			PendingEventsCache.getInstance().setMapPendingEvents(pendingEventService.getPendingEvents());
			PendingEventsCache.getInstance().resetCountBuffer();
		} catch (SchedulerException | IOException e) {
			LOG.error(e);	
		}
	}

}
