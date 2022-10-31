package org.scada_lts.dao.cache;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.UserComment;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

public interface UserCommentCachable {

    String CACHE_ENABLED_KEY = "usercomment.cache.enabled";

    @Cacheable(cacheNames = "comment_list_by_pointid", key = "#p0.id")
    List<UserComment> findByDataPoint(DataPointVO point);

    @Cacheable(cacheNames = "comment_list_by_eventid", key = "#p0.id")
    List<UserComment> findByEvent(EventInstance event);

    @Caching(evict = {
            @CacheEvict(cacheNames = "comment_list_by_pointid", key = "#p0")
    })
    void removeByDataPoint(int pointId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "comment_list_by_eventid", key = "#p0")
    })
    void removeByEvent(long eventId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "comment_list_by_pointid", allEntries = true),
            @CacheEvict(cacheNames = "comment_list_by_eventid", allEntries = true)
    })
    void removeAll();
}
