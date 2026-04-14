package org.scada_lts.utils;

import org.scada_lts.web.beans.ApplicationBeans;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CacheUtils {

    public static <K,V> List<V> getAllValues(String cacheName, Comparator<V> comparator) {
        CacheManager cacheManager = ApplicationBeans.getBean("ehcache", CacheManager.class);
        Cache<K, V> cache = cacheManager.getCache(cacheName);
        return StreamSupport.stream(cache.spliterator(), false)
                .filter(Objects::nonNull)
                .filter(entry -> entry.getValue() != null)
                .map(Cache.Entry::getValue)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public static <K,V> List<K> getAllKeys(String cacheName, Comparator<K> comparator) {
        CacheManager cacheManager = ApplicationBeans.getBean("ehcache", CacheManager.class);
        Cache<K, V> cache = cacheManager.getCache(cacheName);
        return StreamSupport.stream(cache.spliterator(), false)
                .filter(Objects::nonNull)
                .filter(entry -> entry.getKey() != null)
                .map(Cache.Entry::getKey)
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
