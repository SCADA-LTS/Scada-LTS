package org.scada_lts.permissions.cache;


import com.serotonin.mango.vo.User;
import org.scada_lts.permissions.service.PermissionsService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@CacheConfig(cacheNames = "permission_datasource_list_by_user")
public class DataSourceUserPermissionsServiceWithCache implements PermissionsService<Integer, User> {

    private final PermissionsService<Integer, User> service;

    public DataSourceUserPermissionsServiceWithCache(PermissionsService<Integer, User> service) {
        this.service = service;
    }

    @Override
    @Cacheable(key = "#object.id", condition = "#object != null")
    public List<Integer> getPermissions(User object) {
        return service.getPermissions(object);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void addOrUpdatePermissions(User object, List<Integer> toAddOrUpdate) {
        service.addOrUpdatePermissions(object, toAddOrUpdate);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void removePermissions(User object, List<Integer> toRemove) {
        service.removePermissions(object, toRemove);
    }
}
