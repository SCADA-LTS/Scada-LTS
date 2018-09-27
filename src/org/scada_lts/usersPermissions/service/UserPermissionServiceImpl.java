package org.scada_lts.usersPermissions.service;

import org.scada_lts.usersPermissions.dao.UserPermissionDAO;
import org.scada_lts.usersPermissions.model.UserPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    UserPermissionDAO userPermissionDAO = new UserPermissionDAO();

    @Override
    public UserPermission getUserPermission(String entityXid) {
        return userPermissionDAO.findUserPermissionByEntityXid(entityXid);
    }

    @Override
    public List<UserPermission> getAllUserPermissions(int userId) {
        return userPermissionDAO.getAllUserPermissionsForUser(userId);
    }

    @Override
    public List<UserPermission> getLimitedUserPermissions(int offset, int number, int entityType) {
        if (entityType == UserPermission.UserPermissionEntityType.DATASOURCE.toInt()) {
            return userPermissionDAO.getLimitedUsersPermissionsForDatasource(offset, number);
        } else {
            return null;
        }
    }

    @Override
    public void setUserPermission(int userId, String entityXid, int permission, int entityType) {
        UserPermission userPermission = new UserPermission(entityXid, userId, permission, entityType);
        if(userPermissionDAO.findUserPermissionByUserIdAndEntityXid(userId, entityXid)!=null) {
            userPermissionDAO.update(userPermission);
        } else {
            userPermissionDAO.insert(userPermission);
        }
    }

    @Override
    public boolean hasUserPermission(int userId, String entityXid, int permission) {
        UserPermission userPermission;
        userPermission = userPermissionDAO.findUserPermissionByUserIdAndEntityXid(userId, entityXid);
        if(userPermission!=null) {
            if(userPermission.getPermission()>=permission) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


}
