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

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

/** 
 * Update data job for unsilenced alarm level in cache.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class ResetCacheHighestAlarmLevel implements StatefulJob {
	
	private static final Log LOG = LogFactory.getLog(ResetCacheHighestAlarmLevel.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		long time = System.currentTimeMillis();
		Common.ctx.getEventManager().resetHighestAlarmLevels();
		LOG.info(ResetCacheHighestAlarmLevel.class.getSimpleName() + " executed: " + (System.currentTimeMillis() - time)/1000.0 + "[ms]");
	}
}
