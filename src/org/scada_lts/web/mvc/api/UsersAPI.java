package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.error.EntityNotUniqueException;
import org.scada_lts.exception.PasswordMismatchException;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.mvc.api.json.JsonUser;
import org.scada_lts.web.mvc.api.json.JsonUserInfo;
import org.scada_lts.web.mvc.api.json.JsonUserPassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ApiUtils.validateUser;
import static org.scada_lts.utils.ApiUtils.validateUserCreate;

/**
 *
 * @author Radoslaw Jajko rjajko@softq.pl
 */
@Controller
@RequestMapping("/api/users")
public class UsersAPI {

    private static final Log LOG = LogFactory.getLog(UsersAPI.class);

    private UserService userService;

    public UsersAPI(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<JsonUserInfo>> getAll(HttpServletRequest request) {
        LOG.info("/api/users/");

        try {
            User user = Common.getUser(request);
            if(user != null) {
                return new ResponseEntity<>(userService.getUsers().stream()
                        .map(JsonUserInfo::new)
                        .collect(Collectors.toList()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<JsonUser> getUserDetails(
            @PathVariable("id") Integer userId,
            HttpServletRequest request) {
        LOG.info("/api/users/"+userId);
        try {
            User user = Common.getUser(request);
            if(user != null) {
                return new ResponseEntity<>(new JsonUser(userService.getUser(userId)), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/validate")
    public ResponseEntity<Map<String, Object>> isUsernameUnique(
            @RequestParam String username,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("unique", userService.isUsernameUnique(username));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/")
    public ResponseEntity<JsonUser> createUser(
            @RequestBody JsonUserPassword jsonUser,
            HttpServletRequest request
            ) {
        try {
            User user = Common.getUser(request);
            if (user != null) {
                User userToSave = jsonUser.mapToUser();
                if(!validateUserCreate(userToSave))
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                userService.saveUser(userToSave);
                return new ResponseEntity<>(new JsonUser(userToSave), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (EntityNotUniqueException e1) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/")
    public ResponseEntity<String> updateUserDetails(
            @RequestBody JsonUser jsonUser,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                User userToSave = jsonUser.mapToUser();
                if(!validateUser(userToSave, userService))
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                userService.saveUser(userToSave);
                if(jsonUser.getId() == user.getId()) {
                    Common.setUser(request, userToSave);
                }

                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/password")
    public ResponseEntity<Map<String, Object>> updateUserPassword(
            @RequestBody Map<String, Object> jsonBodyRequest,
            HttpServletRequest request
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String password = (String) jsonBodyRequest.get("password");
                Integer userId = (Integer) jsonBodyRequest.get("userId");
                if(userId == null || StringUtils.isEmpty(password))
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                if (user.isAdmin()) {
                    userService.updateUserPassword(
                            userId,
                            password
                    );
                } else {
                    String current = (String) jsonBodyRequest.get("current");
                    if(StringUtils.isEmpty(password) || StringUtils.isEmpty(current))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    userService.updateUserPassword(
                            user.getId(),
                            password,
                            current
                    );
                }
                result.put("status", "ok");
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (PasswordMismatchException e1) {
            result.put("status", "failed");
            result.put("description", "validation.password.wrong");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable("id") Integer userId,
            HttpServletRequest request) {
        LOG.info("/api/users/"+userId);
        try {
            User user = Common.getUser(request);
            if(user != null) {
                userService.deleteUser(userId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
