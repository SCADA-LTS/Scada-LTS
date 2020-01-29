package org.scada_lts.cache;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.CacheStatus;
import org.scada_lts.servicebrokers.ServiceBrokerEventDetector;
import org.scada_lts.servicebrokers.ServiceBrokerEventDetectorImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * That class werify, do, in database, appear instances of Event Detector
 * and verify, do, in cache, instance of event detector appears.
 *
 * @author Mateusz Hyski Abil'I.T. development team, sdt@abilit.eu
 */
public class ComparatorEventDetectorInstanceInCacheOrInDB {

    public static final Log LOG = LogFactory.getLog(ComparatorEventDetectorInstanceInCacheOrInDB.class);
    private static ComparatorEventDetectorInstanceInCacheOrInDB instance ;

    private static Map<Integer, List<PointEventDetectorVO>> mapEventDetectors = new TreeMap<Integer, List<PointEventDetectorVO>>();

    private static final Object object = new Object();

    public static ComparatorEventDetectorInstanceInCacheOrInDB getInstance()  {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Get EventDetectorsCache instance ");
        }
        if( instance == null ) {
            instance = new ComparatorEventDetectorInstanceInCacheOrInDB();
        }
        return instance;
    }
    private Map<OPERATION,List<PointEventDetectorVO>> map = new HashMap<OPERATION,List<PointEventDetectorVO>>();

    private ServiceBrokerEventDetector serviceBrokerEventDetector;

    {
        serviceBrokerEventDetector = new ServiceBrokerEventDetectorImpl();
    }

    /**
     * That method allows to change in flow "cache-database" in meaning, bridge or broker
     * to use another implementations of databases and so on.
     * @param serviceBrokerEventDetector
     */
    public void changeServiceBroker(ServiceBrokerEventDetector serviceBrokerEventDetector) {
        this.serviceBrokerEventDetector = serviceBrokerEventDetector;
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

    public void insertDeleteOrUpdateEventDetectors(DataPointVO dataPoint) {
        Object object = new Object();
        synchronized ( object) {

            //List<PointEventDetectorVO> fromCacheOrDb = (CacheStatus.isEnable)
            //		?getEventDetectors(dataPoint)
            //		:serviceBrokerEventDetector.getEventDetectorsForGivenDataPointId( dataPoint );

            List<PointEventDetectorVO>
                    pointEventDetectorsFromDataPoint = new ArrayList<PointEventDetectorVO>(dataPoint.getEventDetectors()),
                    pointEventDetectorsFromCache = new ArrayList<PointEventDetectorVO>(
                            PointEventDetectorsCache.getInstance().getEventDetectors(dataPoint)),
                    //fromDatabase = serviceBrokerEventDetector.getEventDetectorsForGivenDataPointId( dataPoint ),
                    pointEventDetectorToInsertToDb = new ArrayList<PointEventDetectorVO>(),
                    pointEventDetectorToUpdateInDb = new ArrayList<PointEventDetectorVO>(),
                    pointEventDetectorToDeleteFromDb = new ArrayList<PointEventDetectorVO>();

            List<Integer> idsPointEventDetectorsExistingInDatabase = new ArrayList<Integer>(),
                    idsPointEventDetectorsExistingInDataPoint = new ArrayList<Integer>();

            map.put(
                    OPERATION.UPDATE,
                    updateEventDetectorsByServiceBroker(/*fromCacheOrDb*/pointEventDetectorsFromCache,dataPoint,pointEventDetectorToUpdateInDb));

            for (PointEventDetectorVO pointEventDetectorVO : /*fromDatabase*/pointEventDetectorsFromCache) {
                idsPointEventDetectorsExistingInDatabase.add(pointEventDetectorVO.getId());
            }

            for (PointEventDetectorVO pointEventDetectorVO : pointEventDetectorsFromDataPoint) {
                idsPointEventDetectorsExistingInDataPoint.add(pointEventDetectorVO.getId());
            }
            idsPointEventDetectorsExistingInDatabase.removeAll(idsPointEventDetectorsExistingInDataPoint);

            for (PointEventDetectorVO pointEventDetectorVO : pointEventDetectorsFromCache/*fromDatabase*/) {
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

            //update or insert
            int dataPointId = dataPoint.getId();

            PointEventDetectorsCache.getInstance().updateInCacheOrInsertIntoCache(
                    OPERATION.UPDATE,
                    map.get(OPERATION.UPDATE),
                    dataPointId );

            PointEventDetectorsCache.getInstance().updateInCacheOrInsertIntoCache(
                    OPERATION.INSERT,
                    map.get(OPERATION.INSERT),
                    dataPointId );
        }
    }
}
