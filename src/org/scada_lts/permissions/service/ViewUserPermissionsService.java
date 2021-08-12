package org.scada_lts.permissions.service;


import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.ViewDAO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = "viewUserPermissions")
@Service
public class ViewUserPermissionsService implements PermissionsService<ViewAccess, User> {

    private final ViewDAO viewDAO;

    public ViewUserPermissionsService(ViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    @Cacheable(key = "#object.id", unless = "#object == null")
    public List<ViewAccess> getPermissions(User object) {
        return viewDAO.selectViewPermissions(object.getId());
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void addOrUpdatePermissions(User object, List<ViewAccess> toAddOrUpdate) {
        viewDAO.insertPermissions(object.getId(), toAddOrUpdate);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void removePermissions(User object, List<ViewAccess> toRemove) {
        viewDAO.deletePermissions(object.getId(), toRemove);
    }
}
