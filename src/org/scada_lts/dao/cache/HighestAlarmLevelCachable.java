package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.model.UserAlarmLevel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

public interface HighestAlarmLevelCachable {

    String CACHE_ENABLED_KEY = "alarmlevel.highest.cache.enabled";

    @Cacheable(cacheNames = "alarmlevel_highest_by_user", key="#user.id")
    UserAlarmLevel getAlarmLevel(User user);

    @Caching(put = {@CachePut(cacheNames = "alarmlevel_highest_by_user", key = "#user.id", condition = "#alarmLevel != null")})
    UserAlarmLevel putAlarmLevel(User user, UserAlarmLevel alarmLevel);

    @Caching(evict = {@CacheEvict(cacheNames = "alarmlevel_highest_by_user", key = "#user.id")})
    void removeAlarmLevel(User user);

    @Caching(evict = {@CacheEvict(cacheNames = "alarmlevel_highest_by_user", allEntries = true)})
    void resetAlarmLevels();
}
