package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

public interface PointEventDetectorCacheable {

    String CACHE_ENABLED_KEY = "eventdetector.cache.enabled";

    @Caching(cacheable = {
            @Cacheable(cacheNames = "point_event_detector_list_by_data_point_id", key = "#dataPoint.id",
                    condition = "#dataPoint != null")
    })
    List<PointEventDetectorVO> selectPointEventDetectors(DataPointVO dataPoint);

    @Caching(cacheable = {
            @Cacheable(cacheNames = "point_event_detector", key = "#p0", unless = "#result == null")
    })
    PointEventDetectorVO selectPointEventDetector(int pointEventDetectorId);

    @Caching(cacheable = {
            @Cacheable(cacheNames = "point_event_detector_by_xid_and_data_point_id", key = "#p0 + '-' + #p1", unless = "#result == null || #result.id == -1",
                    condition = "#p0 != null")
    })
    PointEventDetectorVO selectPointEventDetector(String pointEventDetectorXid, int dataPointId);

    @Caching(cacheable = {
            @Cacheable(cacheNames = "data_point_id_by_point_event_detector_id", key = "#p0", unless = "#result == 0")
    })
    int selectDataPointIdByEventDetectorId(int pointEventDetectorId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list_by_data_point_id", key = "#p0"),
            @CacheEvict(cacheNames = "point_event_detector", key = "#pointEventDetector.id",
                    condition = "#pointEventDetector != null"),
            @CacheEvict(cacheNames = "point_event_detector_by_xid_and_data_point_id", key = "#pointEventDetector.xid + '-' + #p0",
                    condition = "#pointEventDetector != null"),
            @CacheEvict(cacheNames = "data_point_id_by_point_event_detector_id", key = "#pointEventDetector.id",
                    condition = "#pointEventDetector != null")
    })
    int insert(int dataPointId, PointEventDetectorVO pointEventDetector);

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list_by_data_point_id", key = "#p0"),
            @CacheEvict(cacheNames = "point_event_detector", key = "#pointEventDetector.id",
                    condition = "#pointEventDetector != null"),
            @CacheEvict(cacheNames = "point_event_detector_by_xid_and_data_point_id", key = "#pointEventDetector.xid + '-' + #p0",
                    condition = "#pointEventDetector != null"),
            @CacheEvict(cacheNames = "data_point_id_by_point_event_detector_id", key = "#pointEventDetector.id",
                    condition = "#pointEventDetector != null")
    })
    void update(int dataPointId, PointEventDetectorVO pointEventDetector);

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list_by_data_point_id", key = "#p0"),
            @CacheEvict(cacheNames = "point_event_detector", key = "#pointEventDetector.id",
                    condition = "#pointEventDetector != null"),
            @CacheEvict(cacheNames = "point_event_detector_by_xid_and_data_point_id", key = "#pointEventDetector.xid + '-' + #p0",
                    condition = "#pointEventDetector != null"),
            @CacheEvict(cacheNames = "data_point_id_by_point_event_detector_id", key = "#pointEventDetector.id",
                    condition = "#pointEventDetector != null")
    })
    void updateWithType(int dataPointId, PointEventDetectorVO pointEventDetector);

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list_by_data_point_id", key = "#p0"),
            @CacheEvict(cacheNames = "point_event_detector", key = "#pointEventDetector.id",
                    condition = "#pointEventDetector != null"),
            @CacheEvict(cacheNames = "point_event_detector_by_xid_and_data_point_id", key = "#pointEventDetector.xid + '-' + #p0",
                    condition = "#pointEventDetector != null"),
            @CacheEvict(cacheNames = "data_point_id_by_point_event_detector_id", key = "#pointEventDetector.id",
                    condition = "#pointEventDetector != null")
    })
    void delete(int dataPointId, PointEventDetectorVO pointEventDetector);

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list_by_data_point_id", allEntries = true),
            @CacheEvict(cacheNames = "point_event_detector", allEntries = true),
            @CacheEvict(cacheNames = "point_event_detector_by_xid_and_data_point_id", allEntries = true),
            @CacheEvict(cacheNames = "data_point_id_by_point_event_detector_id", allEntries = true)
    })
    default void deleteWithId(String dataPointIds) {}
}
