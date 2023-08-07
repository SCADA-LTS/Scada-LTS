package org.scada_lts.permissions.cache;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.permissions.service.PermissionsService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@CacheConfig(cacheNames = "permission_datapoint_list_by_user")
public class DataPointUserPermissionsServiceWithCache implements PermissionsService<DataPointAccess, User> {

    private final PermissionsService<DataPointAccess, User> service;

    public DataPointUserPermissionsServiceWithCache(PermissionsService<DataPointAccess, User> service) {
        this.service = service;
    }

    @Override
    @Cacheable(key = "#object.id", condition = "#object != null")
    public List<DataPointAccess> getPermissions(User object) {
        return service.getPermissions(object);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void addOrUpdatePermissions(User object, List<DataPointAccess> toAddOrUpdate) {
        service.addOrUpdatePermissions(object, toAddOrUpdate);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void removePermissions(User object, List<DataPointAccess> toRemove) {
        service.removePermissions(object, toRemove);
    }

}
