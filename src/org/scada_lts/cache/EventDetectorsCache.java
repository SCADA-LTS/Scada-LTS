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
import org.scada_lts.BridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.model.PointEventDetectorCache;
import org.scada_lts.quartz.UpdateEventDetectors;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

/**
 * Class responsible for data buffering of EventDetectors
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class EventDetectorsCache {

	private BridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO bridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO = new BridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO();

	public static final Log LOG = LogFactory.getLog(EventDetectorsCache.class);
	private static EventDetectorsCache instance = null;
	private TreeMap<Integer, List<PointEventDetectorVO>> mapEventDetectors;

	public static EventDetectorsCache getInstance() throws SchedulerException, IOException {
		if (LOG.isTraceEnabled()) {
		  LOG.trace("Get EventDetectorsCache instance ");
		}
		if (instance == null) {
			instance = new EventDetectorsCache();
		}
		return instance;
	}
	public List<PointEventDetectorVO> getEventDetectorsForDataPointVO(DataPointVO dp) {
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
			return listPointEventDetectorVO;
		}
	}
	public List<PointEventDetectorVO> getEventDetectors(DataPointVO dp) {

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
	 * Method set ListPointEventDetector
	 * @see UpdateEventDetectors
	 * @param mapEventDetectors
	 */
	public void setMapEventDetectorForDataPoint(TreeMap<Integer, List<PointEventDetectorVO>> mapEventDetectors) {
		this.mapEventDetectors = mapEventDetectors;
	}

	public TreeMap<Integer, List<PointEventDetectorVO>> getMapEventDetectors() {
		return mapEventDetectors;
	}

	private EventDetectorsCache() throws SchedulerException, IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("create eventdetectorscache");
		}
 		List<PointEventDetectorCache> listEventDetector = bridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO.getAllPointEventDetectorsByDataPointId();
		TreeMap<Integer, List<PointEventDetectorVO>> mapEventDetector = bridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO.getMapEventDetectorsForGivenEventDetectorsList(listEventDetector);
		setMapEventDetectorForDataPoint(mapEventDetector);
		cacheInitialize();
	}
	
	private void cacheInitialize() throws SchedulerException, IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
		new CacheInitializator();
	}
	
}
