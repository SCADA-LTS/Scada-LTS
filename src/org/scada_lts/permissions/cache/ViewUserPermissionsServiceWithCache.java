package org.scada_lts.permissions.cache;


import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.vo.User;
import org.scada_lts.permissions.service.PermissionsService;
import org.scada_lts.permissions.service.ViewUserPermissionsService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "permission_view_list_by_user")
public class ViewUserPermissionsServiceWithCache implements PermissionsService<ViewAccess, User> {

    private final ViewUserPermissionsService service;

    public ViewUserPermissionsServiceWithCache(ViewUserPermissionsService service) {
        this.service = service;
    }

    @Override
    @Cacheable(key = "#object.id", condition = "#object != null")
    public List<ViewAccess> getPermissions(User object) {
        return service.getPermissions(object);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void addOrUpdatePermissions(User object, List<ViewAccess> toAddOrUpdate) {
        service.addOrUpdatePermissions(object, toAddOrUpdate);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void removePermissions(User object, List<ViewAccess> toRemove) {
        service.removePermissions(object, toRemove);
    }
}
