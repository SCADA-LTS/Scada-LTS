package org.scada_lts.usersPermissions.controller;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.usersPermissions.service.UserPermissionServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class UserPermissionAPI {

    private static final Log LOG = LogFactory.getLog(UserPermissionAPI.class);

    UserPermissionServiceImpl userPermissionService = new UserPermissionServiceImpl();

    @RequestMapping(value = "/api/permission/setPermissionForDatasource/{entityXid}/{userId}/{permission}", method = RequestMethod.GET)
    public ResponseEntity<String> setPermission(@PathVariable("entityXid") String entityXid, @PathVariable("userId") int userId, @PathVariable("permission") int permission, HttpServletRequest request) {
        LOG.info("/api/permission/set/" + entityXid + "/" + userId + "/" + permission);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                if(user.isAdmin()) {
                    userPermissionService.setUserPermission(userId, entityXid, permission);
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }
                return new ResponseEntity<String>(HttpStatus.OK);
            }
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }


}
