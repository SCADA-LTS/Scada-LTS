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
    public List<UserPermission> getAllUserPermissions() {
        return userPermissionDAO.getAllUsersPermissions();
    }

    @Override
    public void setUserPermission(int userId, String entityXid, int permission) {
        UserPermission userPermission = new UserPermission(entityXid, userId, permission);
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
