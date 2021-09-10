package org.scada_lts.permissions.service;


import com.serotonin.mango.vo.User;
import org.scada_lts.dao.DataSourceDAO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = "permissions_user_datasource")
@Service
public class DataSourceUserPermissionsService implements PermissionsService<Integer, User> {

    private final DataSourceDAO dataSourceDAO;

    public DataSourceUserPermissionsService(DataSourceDAO dataSourceDAO) {
        this.dataSourceDAO = dataSourceDAO;
    }

    @Override
    @Cacheable(key = "#object.id", unless = "#object == null || #result.isEmpty()")
    public List<Integer> getPermissions(User object) {
        return dataSourceDAO.selectDataSourcePermissions(object.getId());
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void addOrUpdatePermissions(User object, List<Integer> toAddOrUpdate) {
        dataSourceDAO.insertPermissions(object.getId(), toAddOrUpdate);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void removePermissions(User object, List<Integer> toRemove) {
        dataSourceDAO.deletePermissions(object.getId(), toRemove);
    }
}
