package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.model.PointEventDetectorCacheEntry;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

public interface PointEventDetectorCacheable {

    @Cacheable(cacheNames = "point_event_detector_list", key="'point_event_detectors'")
    List<PointEventDetectorCacheEntry> findAll();

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list", allEntries = true)
    })
    int insert(PointEventDetectorVO pointEventDetector);

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list", allEntries = true)
    })
    void update(PointEventDetectorVO pointEventDetector);

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list", allEntries = true)
    })
    void updateWithType(PointEventDetectorVO pointEventDetector);

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list", allEntries = true)
    })
    void delete(int dataPointId, int pointEventDetectorId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "point_event_detector_list", allEntries = true)
    })
    void deleteWithId(String dataPointIds);
}
