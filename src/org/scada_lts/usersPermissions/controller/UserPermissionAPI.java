package org.scada_lts.usersPermissions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.usersPermissions.model.UserPermission;
import org.scada_lts.usersPermissions.service.UserPermissionServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class UserPermissionAPI {

    private static final Log LOG = LogFactory.getLog(UserPermissionAPI.class);

    UserPermissionServiceImpl userPermissionService = new UserPermissionServiceImpl();

    @RequestMapping(value = "/api/userPermission/getAll", method = RequestMethod.GET)
    public ResponseEntity<String> getAllUsersPermissions(@RequestHeader("userId") int userId, HttpServletRequest request) {
        LOG.info("/api/userPermission/getAll");

        try {
            User user = Common.getUser(request);

            if (user != null) {

                List<UserPermission> userPermissions = new ArrayList<>();
                if (user.isAdmin()) {
                    userPermissions = userPermissionService.getAllUserPermissions(userId);
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }

                List<UserPermissionJSON> lst = new ArrayList<UserPermissionJSON>();
                for (UserPermission u:userPermissions) {
                    UserPermissionJSON up = new UserPermissionJSON(u.getId(), u.getUserId(), u.getEntityXid(), u.getPermission(), u.getEntityType());
                    lst.add(up);
                }

                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(lst);

                return new ResponseEntity<String>(json,HttpStatus.OK);
            }

            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/userPermission/getPermissionsForDatasourceLimited", method = RequestMethod.GET)
    public ResponseEntity<String> getPermissionsForDatasourceLimited(@RequestHeader("offset") int offset, @RequestHeader("number") int number, HttpServletRequest request) {
        LOG.info("/api/userPermission/getPermissionsForDatasourceLimited");

        try {
            User user = Common.getUser(request);

            if (user != null) {

                List<UserPermission> userPermissions = new ArrayList<>();
                if (user.isAdmin()) {
                    userPermissions = userPermissionService.getLimitedUserPermissions(offset, number, UserPermission.UserPermissionEntityType.DATASOURCE.toInt());
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }

                List<UserPermissionJSON> lst = new ArrayList<UserPermissionJSON>();
                for (UserPermission u:userPermissions) {
                    UserPermissionJSON up = new UserPermissionJSON(u.getId(), u.getUserId(), u.getEntityXid(), u.getPermission(), u.getEntityType());
                    lst.add(up);
                }

                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(lst);

                return new ResponseEntity<String>(json,HttpStatus.OK);
            }

            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/userPermission/setPermissionForDatasource", method = RequestMethod.POST)
    public ResponseEntity<String> setPermissionForDatasource(@RequestHeader("entityXid") String entityXid, @RequestHeader("userId") int userId, @RequestHeader("permission") int permission, HttpServletRequest request) {
        LOG.info("/api/userPermission/setPermissionForDatasource/");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                if(user.isAdmin()) {
                    userPermissionService.setUserPermission(userId, entityXid, permission, UserPermission.UserPermissionEntityType.DATASOURCE.toInt());
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

    class UserPermissionJSON implements Serializable {
        private long id;
        private int userId;
        private String entityXid;
        private int permission;
        private int entityType;

        public UserPermissionJSON(long id, int userId, String entityXid, int permission, int entityType) {
            this.id = id;
            this.userId = userId;
            this.entityXid = entityXid;
            this.permission = permission;
            this.entityType = entityType;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getEntityXid() {
            return entityXid;
        }

        public void setEntityXid(String entityXid) {
            this.entityXid = entityXid;
        }

        public int getPermission() {
            return permission;
        }

        public void setPermission(int permission) {
            this.permission = permission;
        }

        public int getEntityType() {
            return entityType;
        }

        public void setEntityType(int entityType) {
            this.entityType = entityType;
        }
    }
}
