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
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.scada_lts.BridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO;
import org.scada_lts.cache.EventDetectorsCache;

import com.serotonin.mango.vo.event.PointEventDetectorVO;

/**
 * Update data job for event detectors in cache.
 *  
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class UpdateEventDetectors  implements StatefulJob {

	private BridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO bridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO = new BridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO();

	private static final Log LOG = LogFactory.getLog(UpdateEventDetectors.class);
	public static boolean updateEventDetector() {

		TreeMap<Integer,List<PointEventDetectorVO>> mapEventDetector = getMapWithEventDetectorsFromCache();



		return true;
	}

	public static boolean removeEventDetectorFromCache(PointEventDetectorVO pointEventDetectorVO, DataPointVO dataPoint) {

		boolean operationAddEventDetector = true;

		TreeMap<Integer,List<PointEventDetectorVO>> mapEventDetector = getMapWithEventDetectorsFromCache();

		List<PointEventDetectorVO> pointEventDetectorVOS = mapEventDetector.get(dataPoint.getId());

		if(pointEventDetectorVOS == null){
			try {
				throw  new Exception("ddddd");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		if( pointEventDetectorVOS.contains(pointEventDetectorVO)) {
			for (PointEventDetectorVO eventDetectorVO:pointEventDetectorVOS) {
				if(eventDetectorVO.getId() == pointEventDetectorVO.getId()) {
					pointEventDetectorVOS.remove(eventDetectorVO);
					break;

				}
			}
		}
		else
			try {
				throw new Exception("EventDetector do not exists in cache, but it should.");
			} catch (Exception e) {
				e.printStackTrace();
			}



		mapEventDetector.remove(dataPoint.getId());
		mapEventDetector.put(dataPoint.getId(),pointEventDetectorVOS);

		return Boolean.TRUE;
	}
	public static boolean updateExistingEventDetectorInCache(PointEventDetectorVO pointEventDetectorVO, DataPointVO dataPoint) {

		boolean operationAddEventDetector = true;

		TreeMap<Integer,List<PointEventDetectorVO>> mapEventDetector = getMapWithEventDetectorsFromCache();
		updateEventDetectorInListOfPointsInCache(mapEventDetector,pointEventDetectorVO,dataPoint);
		/*
		if( mapEventDetector != null) {

			operationAddEventDetector = addNewEventDetectorIntoListOfPointsInCache(mapEventDetector, pointEventDetectorVO, dataPoint);
		}
		else
			operationAddEventDetector = false;
		*/

		return operationAddEventDetector;
	}
	public static boolean addNewEventDetectorIntoCache(PointEventDetectorVO pointEventDetectorVO, DataPointVO dataPoint) {

		boolean operationAddEventDetector = true;

		TreeMap<Integer,List<PointEventDetectorVO>> mapEventDetector = getMapWithEventDetectorsFromCache();

		if( mapEventDetector != null) {

			operationAddEventDetector = addNewEventDetectorIntoListOfPointsInCache(mapEventDetector, pointEventDetectorVO, dataPoint);
		}
		else
			operationAddEventDetector = false;

		return operationAddEventDetector;
	}

	private static TreeMap<Integer,List<PointEventDetectorVO>> getMapWithEventDetectorsFromCache() {

		boolean operationAddEventDetector = true;

		TreeMap<Integer,List<PointEventDetectorVO>> mapEventDetector = null;
		try {
			mapEventDetector = EventDetectorsCache.getInstance().getMapEventDetectors();
		}
		catch (SchedulerException e) {
			LOG.info("Exception appear during get Event Detectors.See details :"+e.getMessage());
			operationAddEventDetector= false;
		}
		catch (IOException e) {
			LOG.info("Exception appear during get Event Detectors.See details :"+e.getMessage());
			operationAddEventDetector= false;
		}

		if(operationAddEventDetector)
			return mapEventDetector;
		else
			return null;
	}
	private static boolean addNewEventDetectorIntoListOfPointsInCache(TreeMap<Integer, List<PointEventDetectorVO>> mapEventDetector, PointEventDetectorVO pointEventDetectorVO, DataPointVO dataPoint) {

		List<PointEventDetectorVO> pointEventDetectorVOS = mapEventDetector.get(dataPoint.getId());

		if(pointEventDetectorVOS == null){
			pointEventDetectorVOS = new ArrayList<PointEventDetectorVO>();
		}

		pointEventDetectorVOS.add(pointEventDetectorVO);
		mapEventDetector.remove(dataPoint.getId());
		mapEventDetector.put(dataPoint.getId(),pointEventDetectorVOS);

		return Boolean.TRUE;
	}
	private static boolean updateEventDetectorInListOfPointsInCache(TreeMap<Integer, List<PointEventDetectorVO>> mapEventDetector, PointEventDetectorVO pointEventDetectorVO, DataPointVO dataPoint) {

		List<PointEventDetectorVO> pointEventDetectorVOS = mapEventDetector.get(dataPoint.getId());

		if(pointEventDetectorVOS == null){
			pointEventDetectorVOS = new ArrayList<PointEventDetectorVO>();
			pointEventDetectorVOS.add(pointEventDetectorVO);
		}
		else
			if( pointEventDetectorVOS.contains(pointEventDetectorVO)) {
				for (PointEventDetectorVO eventDetectorVO:pointEventDetectorVOS) {
					if(eventDetectorVO.getId() == pointEventDetectorVO.getId()) {
						int dd=2;
						eventDetectorVO = pointEventDetectorVO;break;
						//pointEventDetectorVOS.
					}
				}
			}
			else
				try {
					throw new Exception("EventDetector do not exists in cache, but it should.");
				} catch (Exception e) {
					e.printStackTrace();
				}



		mapEventDetector.remove(dataPoint.getId());
		mapEventDetector.put(dataPoint.getId(),pointEventDetectorVOS);

		return Boolean.TRUE;
	}
	@Override
	public void execute(JobExecutionContext arg0)  {
		LOG.trace("UpdateEventDetectors");
		long startTime=System.nanoTime();
		//List<PointEventDetectorCache> listEventDetector = bridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO.getAllPointEventDetectorsByDataPointId();
		//TreeMap<Integer, List<PointEventDetectorVO>> mapEventDetector = bridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO.getMapEventDetectorsForGivenEventDetectorsList(
		//		listEventDetector);
		try {
			TreeMap<Integer,List<PointEventDetectorVO>> mapEventDetector = EventDetectorsCache.getInstance().getMapEventDetectors();
			EventDetectorsCache.getInstance().setMapEventDetectorForDataPoint(mapEventDetector);
		} catch (SchedulerException | IOException e) {
			LOG.error(e);	
		}
		long endTime=System.nanoTime();
		System.out.println("Czas "+(endTime-startTime));
	}
}