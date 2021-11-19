package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

public interface HighestAlarmLevelCachable {

    String CACHE_ENABLED_KEY = "alarmlevel.highest.cache.enabled";

    @Cacheable(cacheNames = "user_alarmlevel_highest", key="#user.id", condition = "#result != -1")
    int getAlarmLevel(User user);

    @Caching(put = {@CachePut(cacheNames = "user_alarmlevel_highest", key="#user.id")})
    int putAlarmLevel(User user, int alarmLevel);

    @Caching(evict = {@CacheEvict(cacheNames = "user_alarmlevel_highest", key = "#user.id")})
    void removeAlarmLevel(User user);
}
