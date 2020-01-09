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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.scada_lts.cache.PointEventDetectorsCache;
import org.scada_lts.dao.model.PointEventDetectorCache;

import org.scada_lts.servicebrokers.ServiceBrokerEventDetector;
import org.scada_lts.servicebrokers.ServiceBrokerEventDetectorImpl;

/**
 * Update data job for event detectors in cache.
 *  
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class UpdateEventDetectors implements StatefulJob {
	
	private static final Log LOG = LogFactory.getLog(UpdateEventDetectors.class);
	private ServiceBrokerEventDetector serviceBrokerEventDetector = new ServiceBrokerEventDetectorImpl();
	private static int a=0;
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		LOG.trace("UpdateEventDetectors");

		if(UpdateEventDetectors.a==0) {
			UpdateEventDetectors.a++;
			List<PointEventDetectorCache> listEventDetector = serviceBrokerEventDetector.getAllEventDetectors();
			if (listEventDetector != null && !listEventDetector.isEmpty())
				PointEventDetectorsCache.reFillMapEventDetectors(listEventDetector);
			else {
				LOG.info(getClass().getName() + " Event Detectors list is null or empty");
			}
		}
	}
}