package org.scada_lts.usersPermissions.service;

import org.scada_lts.usersPermissions.dao.UserPermissionDAO;
import org.scada_lts.usersPermissions.model.UserPermission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
@Service
public class UserPermissionServiceImpl implements UserPermissionService {

//    @Resource
    UserPermissionDAO userPermissionDAO = new UserPermissionDAO();

    @Override
    public UserPermission getUserPermission(String entityXid) {
        return userPermissionDAO.findUserPermissionByEntityXid(entityXid);
    }

    @Override
    public void createUserPermission(int userId, String entityXid, int permission) {

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
