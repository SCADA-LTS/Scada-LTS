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

    @Cacheable(cacheNames = "comment_event_list", key = "'eventUserComments'")
    List<UserComment> findByEventAll();

    @Caching(evict = {
            @CacheEvict(cacheNames = "comment_list_by_pointid", key = "#p0")
    })
    void removeByDataPoint(int pointId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "comment_list_by_eventid", key = "#p0"),
            @CacheEvict(cacheNames = "comment_event_list", allEntries = true)
    })
    void removeByEvent(int eventId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "comment_list_by_pointid", allEntries = true),
            @CacheEvict(cacheNames = "comment_list_by_eventid", allEntries = true),
            @CacheEvict(cacheNames = "comment_event_list", allEntries = true)
    })
    void removeAll();
}
