package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.DataPointUserDAO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = "permission_datapoint_list_by_user")
@Service
public class DataPointUserPermissionsService implements PermissionsService<DataPointAccess, User> {

    private final DataPointUserDAO dataPointUserDAO;

    public DataPointUserPermissionsService(DataPointUserDAO dataPointUserDAO) {
        this.dataPointUserDAO = dataPointUserDAO;
    }

    @Override
    @Cacheable(key = "#object.id", unless = "#object == null || #result.isEmpty()")
    public List<DataPointAccess> getPermissions(User object) {
        return dataPointUserDAO.selectDataPointPermissions(object.getId());
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void addOrUpdatePermissions(User object, List<DataPointAccess> toAddOrUpdate) {
        dataPointUserDAO.insertPermissions(object.getId(), toAddOrUpdate);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void removePermissions(User object, List<DataPointAccess> toRemove) {
        dataPointUserDAO.deletePermissions(object.getId(), toRemove);
    }

}
