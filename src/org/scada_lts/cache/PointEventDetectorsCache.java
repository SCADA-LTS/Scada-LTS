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
import org.scada_lts.mango.service.CacheStatus;
import org.scada_lts.quartz.UpdateEventDetectors;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.servicebrokers.ServiceBrokerEventDetector;
import org.scada_lts.servicebrokers.ServiceBrokerEventDetectorImpl;

import java.util.*;

/**
 * update Class responsible for collect instances of PointEventDetectorVO via data point id.
 * Class responsible for data buffering of EventDetectors
 *
 * update  mateusz hyski Abil'I.T. development team, sdt@abilit.eu
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class PointEventDetectorsCache{

	public static final Log LOG = LogFactory.getLog(PointEventDetectorsCache.class);
	private static PointEventDetectorsCache instance ;

	private static Map<Integer, List<PointEventDetectorVO>> mapEventDetectors = new TreeMap<Integer, List<PointEventDetectorVO>>();

	private static final Object object = new Object();

	public PointEventDetectorsCache(Object o) {
	}
	private ServiceBrokerEventDetector serviceBrokerEventDetector;
	private Map<OPERATION,List<PointEventDetectorVO>> map = new HashMap<OPERATION,List<PointEventDetectorVO>>();
	{
		serviceBrokerEventDetector = new ServiceBrokerEventDetectorImpl();
	}
	/**
	 * Update Event Detectors in database by
	 * @see org.scada_lts.servicebrokers.ServiceBrokerEventDetector
	 * ServiceBrokerEventDetector and adds also updated instance of PointEventDetectors into collection
	 * which will be forwarded to method which update Event Detectors in cache.
	 * Collection which will forwarder is pointEventDetectorToUpdateInDb
	 *
	 * @param fromCacheOrDb
	 * @param dataPoint
	 * @param pointEventDetectorToUpdateInDb
	 */
	public List<PointEventDetectorVO> updateEventDetectorsByServiceBroker(
			List<PointEventDetectorVO> fromCacheOrDb,
			DataPointVO dataPoint,
			List<PointEventDetectorVO> pointEventDetectorToUpdateInDb) {
		for (PointEventDetectorVO pointEventDetector: fromCacheOrDb) {
			for(PointEventDetectorVO pointEventDetectorVOFromDataPoint :dataPoint.getEventDetectors() ){
				if(pointEventDetectorVOFromDataPoint.getId() == pointEventDetector.getId()) {
					if (pointEventDetector.getId() > 0) {
						serviceBrokerEventDetector.updateEventDetectors(pointEventDetector);
						pointEventDetectorToUpdateInDb.add(pointEventDetector);
					}
				}
			}
		}
		return pointEventDetectorToUpdateInDb;
	}

	/**
	 * Delete Event Detectors from database and adds also instance of PointEventDetectors into collection which
	 * will be forwarder to method which remove all those PointEventDetectors from cache.
	 *
	 * @param pointEventDetectorToDeleteFromDb
	 * @param dataPoint
	 * @param pointEventDetectorsFromDataPoint
	 */
	public List<PointEventDetectorVO> deleteEventDetectorsByServiceBroker(
			List<PointEventDetectorVO> pointEventDetectorToDeleteFromDb,
			DataPointVO dataPoint,
			List<PointEventDetectorVO> pointEventDetectorsFromDataPoint) {
		List<PointEventDetectorVO> pointEventDetectorVOS = new ArrayList<PointEventDetectorVO>();
		for (PointEventDetectorVO eventDetectorVOFromCacheOrD : pointEventDetectorToDeleteFromDb) {
			serviceBrokerEventDetector.deleteEventDetector(dataPoint,eventDetectorVOFromCacheOrD);
			//remove from cache
			pointEventDetectorVOS.add(eventDetectorVOFromCacheOrD);
		}
		return pointEventDetectorVOS;
	}

	/**
	 * that method adds also instance of PointEventDetectors into collection which
	 * will be forwarder to method which put all new PointEventDetectors into cache.
	 * Collection which will forwarder is pointEventDetectorToInsertToDb.
	 *
	 * @param pointEventDetectorsFromDataPoint
	 * @param pointEventDetectorToInsertToDb
	 */
	public List<PointEventDetectorVO>  insertEventDetectorByServiceBroker(
			List<PointEventDetectorVO> pointEventDetectorsFromDataPoint,
			List<PointEventDetectorVO> pointEventDetectorToInsertToDb
	){
		int idForNewEventDetectorInstanceInCache=-100;

		pointEventDetectorToInsertToDb.clear();
		for (PointEventDetectorVO pointEventDetectorVO : pointEventDetectorsFromDataPoint) {
			if (pointEventDetectorVO.getId() < 0 && !pointEventDetectorToInsertToDb.contains(pointEventDetectorVO)) {
				int newId = (CacheStatus.isEnable)
						?idForNewEventDetectorInstanceInCache
						:serviceBrokerEventDetector.insertEventDetector(pointEventDetectorVO);
				pointEventDetectorVO.setId(newId);
				pointEventDetectorToInsertToDb.add(pointEventDetectorVO);
				continue;
			}
		}
		return pointEventDetectorToInsertToDb;
	}

	public static PointEventDetectorsCache getInstance()  {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Get EventDetectorsCache instance ");
		}
		if( instance == null ) {
			instance = new PointEventDetectorsCache();
		}
		return instance;
	}


	public void insertDeleteOrUpdateEventDetectors(DataPointVO dataPoint) {
		Object object = new Object();
		synchronized ( object) {

			//List<PointEventDetectorVO> fromCacheOrDb = (CacheStatus.isEnable)
			//		?getEventDetectors(dataPoint)
			//		:serviceBrokerEventDetector.getEventDetectorsForGivenDataPointId( dataPoint );
			List<PointEventDetectorVO>
					pointEventDetectorsFromDataPoint = new ArrayList<PointEventDetectorVO>(dataPoint.getEventDetectors()),
					pointEventDetectorsFromCache = new ArrayList<PointEventDetectorVO>(getEventDetectors(dataPoint)),
					//fromDatabase = serviceBrokerEventDetector.getEventDetectorsForGivenDataPointId( dataPoint ),
					pointEventDetectorToInsertToDb = new ArrayList<PointEventDetectorVO>(),
					pointEventDetectorToUpdateInDb = new ArrayList<PointEventDetectorVO>(),
					pointEventDetectorToDeleteFromDb = new ArrayList<PointEventDetectorVO>();

			List<Integer> idsPointEventDetectorsExistingInDatabase = new ArrayList<Integer>(),
					idsPointEventDetectorsExistingInDataPoint = new ArrayList<Integer>();

			map.put(
					OPERATION.UPDATE,
					updateEventDetectorsByServiceBroker(pointEventDetectorsFromCache,dataPoint,pointEventDetectorToUpdateInDb));

			for (PointEventDetectorVO pointEventDetectorVO : pointEventDetectorsFromCache) {
				idsPointEventDetectorsExistingInDatabase.add(pointEventDetectorVO.getId());
			}

			for (PointEventDetectorVO pointEventDetectorVO : pointEventDetectorsFromDataPoint) {
				idsPointEventDetectorsExistingInDataPoint.add(pointEventDetectorVO.getId());
			}
			idsPointEventDetectorsExistingInDatabase.removeAll(idsPointEventDetectorsExistingInDataPoint);

			for (PointEventDetectorVO pointEventDetectorVO : pointEventDetectorsFromCache) {
				if (idsPointEventDetectorsExistingInDatabase.contains(pointEventDetectorVO.getId())) {
					pointEventDetectorToDeleteFromDb.add(pointEventDetectorVO);
				}
			}


			if (pointEventDetectorToDeleteFromDb.size() != 0) {
				map.put(
						OPERATION.REMOVE,
						deleteEventDetectorsByServiceBroker(pointEventDetectorToDeleteFromDb, dataPoint,pointEventDetectorsFromDataPoint));
			}
			map.put(
					OPERATION.INSERT,
					insertEventDetectorByServiceBroker(pointEventDetectorsFromDataPoint, pointEventDetectorToInsertToDb));

			int dataPointId = dataPoint.getId();

			updateInCacheOrInsertIntoCache(
					OPERATION.UPDATE,
					map.get(OPERATION.UPDATE),
					dataPointId );

			updateInCacheOrInsertIntoCache(
					OPERATION.INSERT,
					map.get(OPERATION.INSERT),
					dataPointId );
		}
	}




	public void updateInCacheOrInsertIntoCache(OPERATION operation, List<PointEventDetectorVO> pointEventDetectorVOS, int dataPointId){

		if (! pointEventDetectorVOS.isEmpty() ) {
			for (PointEventDetectorVO pointEventDetectorVO : pointEventDetectorVOS) {
				switch(operation)
				{
				case UPDATE :
					updateEventDetector(dataPointId, pointEventDetectorVO);
					break;
				case INSERT:
					addEventDetector(dataPointId, pointEventDetectorVO);
					break;
				case REMOVE:
					removeEventDetector(dataPointId,pointEventDetectorVO);
					break;
				}
			}
		}
		else {
			LOG.info("Given collection forwarded to update in , insert into, or remove from cache is empty.");
		}

	}

	public int getDataPointIdForGivenEventDetectorId( int eventDetectorId){
		for(Map.Entry<Integer,List<PointEventDetectorVO>> integerListEntry : getMapEventDetectors().entrySet()) {
			for(PointEventDetectorVO pointEventDetectorVO1 : getMapEventDetectors().get(integerListEntry.getKey())) {
				if( pointEventDetectorVO1.getId() == eventDetectorId ) {
					return integerListEntry.getKey();
				}
			}
		}
		return -1;
	}
	public int getDataPointIdForGivenEventDetectorXid( StringBuilder xid){
		for(Map.Entry<Integer,List<PointEventDetectorVO>> integerListEntry : getMapEventDetectors().entrySet()) {
			for(PointEventDetectorVO pointEventDetectorVO1 : getPointEventDetectorsForGivenDataPointId( integerListEntry.getKey() )) {
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
	public void addNewEventDetector(int dataPointId,PointEventDetectorVO pointEventDetectorVO){
		synchronized (object) {
			if (getMapEventDetectors().get(dataPointId) == null) {
				getMapEventDetectors().put(dataPointId, new ArrayList<PointEventDetectorVO>());
			}
			getEventDetectorsForDataPointId(dataPointId).add(pointEventDetectorVO);
			LOG.info("Event Detector with id="+pointEventDetectorVO.getId()+" has been added to cache");
		}
	}
	public void addEventDetector(int dataPointId,PointEventDetectorVO pointEventDetectorVO) {

		synchronized (object) {
			if (getMapEventDetectors().get(dataPointId) == null) {
				getMapEventDetectors().put(dataPointId, new ArrayList<PointEventDetectorVO>());
			}
			getEventDetectorsForDataPointId(dataPointId).add(pointEventDetectorVO);
			LOG.info("Event Detector with id="+pointEventDetectorVO.getId()+" has been added to cache");
		}
	}
	public void removeEventDetectorFromCache(int dataPointId, int pointEventDetectorId){
		//ok
		PointEventDetectorVO tempPointEventDetectorVO = null;
		synchronized( object) {
			//first - get Event Detector from cache
			for(PointEventDetectorVO eventDetectorVOFromCache :getEventDetectorsForDataPointId( dataPointId )) {
				if(eventDetectorVOFromCache.getId() == pointEventDetectorId) {
					tempPointEventDetectorVO = eventDetectorVOFromCache; break;
				}
			}
			//second - remove it
			if ( tempPointEventDetectorVO != null ) {
				justRemove( dataPointId, tempPointEventDetectorVO);
			}
			else
				LOG.info("Event Detector with id="+pointEventDetectorId+" has not been removed because not exist in cache");
		}
	}

	/**
	 * Remmove Event Detector given dataPointId and pointEventDetectorId
	 * @param dataPointId
	 * @param pointEventDetectorId
	 */
	public void removeEventDetector(int dataPointId, int pointEventDetectorId){
		removeEventDetectorFromCache(dataPointId,pointEventDetectorId);
	}

	/**
	 * Remove Event Detector given dataPointId and PointEventDetectorVO instance
	 * @param dataPointId
	 * @param pointEventDetectorVO
	 * @return boolean
	 */
	public boolean removeEventDetector(int dataPointId,PointEventDetectorVO pointEventDetectorVO) {
		boolean result;
		synchronized ( object ) {
			if( doEventDetectorExistInCache( dataPointId, pointEventDetectorVO ) ) {
				justRemove( dataPointId, pointEventDetectorVO);
				//LOG info is in justRemove method
				result = true;
			}
			else {
				result = false;
				LOG.info("Event Detector " + pointEventDetectorVO.getXid() + "has not been removed because not exist in cache");
			}
		}
		return result;
	}
	public void updateEventDetector(int dataPointId,PointEventDetectorVO pointEventDetectorVO) {
		synchronized ( object ) {

			if( doEventDetectorExistInCache(dataPointId,pointEventDetectorVO ) ) {
				justRemove(dataPointId,pointEventDetectorVO);
				getMapEventDetectors().get( dataPointId ).add( pointEventDetectorVO );
				LOG.info("Event Detector " + pointEventDetectorVO.getXid() + "has been updated in cache");
			}
			else {
				LOG.info("Event Detector " + pointEventDetectorVO.getXid() + "has not been updated because it is't exist in cache");
			}
		}
	}


	private void justRemove(int dataPointId, PointEventDetectorVO pointEventDetectorVO){
		for(PointEventDetectorVO pointEventDetectorVO1 : getPointEventDetectorsForGivenDataPointId( dataPointId )) {
			if( pointEventDetectorVO1.getId() == pointEventDetectorVO.getId()){
				getMapEventDetectors().get(dataPointId).remove(pointEventDetectorVO1);
				LOG.info("Event Detector " + pointEventDetectorVO.getXid() + "has been removed from cache");
				break;
			}
		}
	}
	//ok
	private boolean doEventDetectorExistInCache(int dataPointId, PointEventDetectorVO pointEventDetectorVO) {
		boolean exist = false;
		for(PointEventDetectorVO pointEventDetectorVO1:getPointEventDetectorsForGivenDataPointId(dataPointId)){
			if(pointEventDetectorVO1.getId() == pointEventDetectorVO.getId())
			{
				exist = true;break;
			}
		}
		return exist;
	}
	public void clearCacheUnderDataPoint(int dataPointId) {
		synchronized ( object ) {
			if (getMapEventDetectors().get(dataPointId) != null) {
				getMapEventDetectors().get(dataPointId).clear();
				LOG.info("All instances PointEventDetector has been removed under dataPointId = "+dataPointId);
			}
			else {
				if (LOG.isTraceEnabled()) {
					LOG.info("Cache doesn't contain any PointEventDetector for dataPoint with id = "+dataPointId);
				}
			}
		}
	}
	private List<PointEventDetectorVO> getPointEventDetectorsForGivenDataPointId( int dataPointId) {
		return getMapEventDetectors().get( dataPointId );
	}
	private Map<Integer, List<PointEventDetectorVO>> getMapEventDetectors() {
		return PointEventDetectorsCache.getInstance().mapEventDetectors;
	}

	public List<PointEventDetectorVO> getEventDetectorsForDataPointId(int dataPointid) {
		if(cacheIsEmpty()){
			return new ArrayList<PointEventDetectorVO>();
		}
		List<PointEventDetectorVO> listPointEventDetectorVO = getPointEventDetectorsForGivenDataPointId( dataPointid );

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
		
		List<PointEventDetectorVO> listPointEventDetectorVO = getPointEventDetectorsForGivenDataPointId( dp.getId() );
		
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


	private PointEventDetectorsCache()  {
		if (LOG.isTraceEnabled()) {
		  LOG.trace("Create EventDetectorsCache");
		}

		boolean junitTest=false;
		if(junitTest) {

			List<PointEventDetectorCache> allEventDetectorFromDb = new ServiceBrokerEventDetectorImpl().getAllEventDetectors();
			reFillMapEventDetectors(allEventDetectorFromDb);
			runJobsInThreads();
		}


	}

	private void runJobsInThreads()  {
		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
		new ThreadJobs();
	}
	
}
