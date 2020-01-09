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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.PointEventDetectorCache;
import org.scada_lts.quartz.UpdateEventDetectors;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.servicebrokers.ServiceBrokerEventDetector;
import org.scada_lts.servicebrokers.ServiceBrokerEventDetectorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * update Class responsible for collect instances of PointEventDetectorVO via data point id.
 * Class responsible for data buffering of EventDetectors
 *
 * update  mateusz hyski Abil'I.T. development team, sdt@abilit.eu
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class PointEventDetectorsCache {
	public static final Log LOG = LogFactory.getLog(PointEventDetectorsCache.class);
	private static PointEventDetectorsCache instance ;

	private ServiceBrokerEventDetector serviceBrokerEventDetector = new ServiceBrokerEventDetectorImpl();
	private static Map<Integer, List<PointEventDetectorVO>> mapEventDetectors = new TreeMap<Integer, List<PointEventDetectorVO>>();

	private static final Object object = new Object();

	public static PointEventDetectorsCache getInstance()  {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Get EventDetectorsCache instance ");
		}
		if( instance == null ) {
			instance = new PointEventDetectorsCache();
		}
		return instance;
	}
	public int getDataPointIdForGivenEventDetectorXid( StringBuilder xid){
		for(Map.Entry<Integer,List<PointEventDetectorVO>> integerListEntry : getMapEventDetectors().entrySet()) {
			for(PointEventDetectorVO pointEventDetectorVO1 : getMapEventDetectors().get(integerListEntry.getKey())) {
				if( pointEventDetectorVO1.getXid().equals( xid.toString() ) ) {
					return integerListEntry.getKey();
				}
			}
		}
		return -1;
	}
	public int getDataPointId( PointEventDetectorVO pointEventDetectorVO) {
		return getDataPointIdForGivenEventDetectorXid( new StringBuilder( pointEventDetectorVO.getXid() ) );
	}
	public void addEventDetector(int dataPointId,PointEventDetectorVO pointEventDetectorVO) {

		synchronized (object) {
			if (getMapEventDetectors().get(dataPointId) == null) {
				getMapEventDetectors().put(dataPointId, new ArrayList<PointEventDetectorVO>());
			}
			getMapEventDetectors().get(dataPointId).add(pointEventDetectorVO);
		}
	}
	public void removeEventDetector(int dataPointId, int pointEventDetectorId){
		PointEventDetectorVO tempPointEventDetectorVO = null;
		synchronized( object) {
			for(PointEventDetectorVO pointEventDetectorVO :getEventDetectorsForDataPointId( dataPointId )) {
				if(pointEventDetectorVO.getId() == pointEventDetectorId) {
					tempPointEventDetectorVO = pointEventDetectorVO; break;
				}
			}
			removeEventDetector( dataPointId, tempPointEventDetectorVO );
		}
	}
	public void removeEventDetector(int dataPointId,PointEventDetectorVO pointEventDetectorVO) {

		synchronized ( object ) {
			if( doEventDetectorExistInCache( dataPointId, pointEventDetectorVO ) ) {
				getMapEventDetectors().get(dataPointId).remove( pointEventDetectorVO );
			}
			else
				LOG.info("Event Detector "+pointEventDetectorVO.getXid()+"has not been removed because not exist in cache");
		}
	}
	private boolean doEventDetectorExistInCache(int dataPointId, PointEventDetectorVO pointEventDetectorVO) {
		PointEventDetectorVO eventDetectorVOToRemove = null;
		boolean exist = false;
		for(PointEventDetectorVO pointEventDetectorVO1:PointEventDetectorsCache.mapEventDetectors.get(dataPointId)){
			if(pointEventDetectorVO1.getId() == pointEventDetectorVO.getId())
			{
				eventDetectorVOToRemove = pointEventDetectorVO1;
				exist = true;break;
			}
		}
		return exist;
	}
	public void updateEventDetector(int dataPointId,PointEventDetectorVO pointEventDetectorVO) {

		synchronized ( object ) {

			if( doEventDetectorExistInCache(dataPointId,pointEventDetectorVO ) ) {
				getMapEventDetectors().get(dataPointId).remove( pointEventDetectorVO );
				getMapEventDetectors().get( dataPointId ).add( pointEventDetectorVO );
			}
			else
				LOG.info("Event Detector "+pointEventDetectorVO.getXid()+"has not been updated because not exist in cache");
		}
	}
	public void clearCacheUnderDataPoint(int dataPointId) {
		synchronized ( object ) {
			if (getMapEventDetectors().get(dataPointId) != null) {
				getMapEventDetectors().get(dataPointId).clear();
				LOG.info("All PointEventDetector has been removed under dataPoint id = "+dataPointId);
			}
			else {
				if (LOG.isTraceEnabled()) {
					LOG.info("Cache doesn't contain any PointEventDetector for dataPoint with id = "+dataPointId);
				}
			}
		}
	}


	private Map<Integer, List<PointEventDetectorVO>> getMapEventDetectors() {
		return PointEventDetectorsCache.getInstance().mapEventDetectors;
	}

	public List<PointEventDetectorVO> getEventDetectorsForDataPointId(int dataPointid) {
		if(cacheIsEmpty()){
			return new ArrayList<PointEventDetectorVO>();
		}
		List<PointEventDetectorVO> listPointEventDetectorVO = getMapEventDetectors().get( dataPointid );

		return (listPointEventDetectorVO == null)
				?new ArrayList<PointEventDetectorVO>()
				:listPointEventDetectorVO;
	}
	//will be deprecated
	public List<PointEventDetectorVO> getEventDetectors(DataPointVO dp) {
		//return getEventDetectorsForDataPointId(dp.getId());

		if (cacheIsEmpty()) {
			return new ArrayList<PointEventDetectorVO>();
		}
		
		List<PointEventDetectorVO> listPointEventDetectorVO = getMapEventDetectors().get(dp.getId());
		
		if (listPointEventDetectorVO == null) {
			return new ArrayList<PointEventDetectorVO>();
		} else {
			//for ( PointEventDetectorVO pedVO :listPointEventDetectorVO) {
			//	pedVO.njbSetDataPoint(dp);
			//}
			return listPointEventDetectorVO;
		}

	}
	
	/**
	 * Method set ListPointEventDetector
	 * @see UpdateEventDetectors
	 * @param mapEventDetectors
	 */
	public void setMapEventDetectorForDataPoint(Map<Integer, List<PointEventDetectorVO>> mapEventDetectors) {
		synchronized ( object ) {
			PointEventDetectorsCache.getInstance().mapEventDetectors = mapEventDetectors;
		}
	}

	public static void reFillMapEventDetectors(
			final List<PointEventDetectorCache> listEventDetectorCache) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("refillMapEventDetectorForUser");
		}
		synchronized ( object ) {
			if( !listEventDetectorCache.isEmpty()) {
				mapEventDetectors.clear();
				for (PointEventDetectorCache eventDetector : listEventDetectorCache) {
					int key = eventDetector.getDataPointId();
					if (mapEventDetectors.get(key) == null) {
						mapEventDetectors.put(key, new ArrayList<PointEventDetectorVO>());
					}
					mapEventDetectors.get(key).add(eventDetector.getPointEventDetector());
				}
			}
		}
	}
	private boolean cacheIsEmpty(){
		if (LOG.isTraceEnabled()) {
			LOG.trace("getEventDetectors count from buffer");
		}

		if (this.mapEventDetectors.isEmpty()) {
			//LOG.error(new Exception("Cache was empty"));
			LOG.info("Cache was empty");
			return true;
		}
		return false;
	}

	public PointEventDetectorsCache(ServiceBrokerEventDetector serviceBrokerEventDetector) {
		this.serviceBrokerEventDetector = serviceBrokerEventDetector;
	}

	private PointEventDetectorsCache()  {
		if (LOG.isTraceEnabled()) {
		  LOG.trace("Create EventDetectorsCache");
		}

		List<PointEventDetectorCache> allEventDetectorFromDb = serviceBrokerEventDetector.getAllEventDetectors();
		reFillMapEventDetectors(allEventDetectorFromDb);
		runJobsInThreads();
	}

	private void runJobsInThreads()  {
		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
		new ThreadJobs();
	}
	
}
