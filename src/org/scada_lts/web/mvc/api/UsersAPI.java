package org.scada_lts.web.mvc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.mvc.api.user.UserInfo;
import org.scada_lts.web.mvc.api.user.UserInfoSimple;
import org.scada_lts.web.mvc.api.user.UserInfoPassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Radoslaw Jajko rjajko@softq.pl
 */
@Controller
@RequestMapping("/api/users")
public class UsersAPI {

    private static final Log LOG = LogFactory.getLog(UsersAPI.class);

    private final UsersApiService usersApiService;

    public UsersAPI() {
        this.usersApiService = new UsersApiService(new UserService());
    }

    public UsersAPI(UsersApiService usersApiService) {
        this.usersApiService = usersApiService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<UserInfoSimple>> getAll(HttpServletRequest request) {
        LOG.debug("/api/users/");
        List<UserInfoSimple> response = usersApiService.getIdentifiers(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserInfo> getUserDetails(@PathVariable("id") Integer userId, HttpServletRequest request) {
        LOG.debug("/api/users/"+userId);
        UserInfo response = usersApiService.read(request, null, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/validate")
    public ResponseEntity<Map<String, Object>> isUsernameUnique(@RequestParam String username, HttpServletRequest request) {
        LOG.debug(  "/api/users/validate");
        Map<String, Object> response = new HashMap<>();
        boolean isUnique = usersApiService.isUnique(request, username);
        response.put("unique", isUnique);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    public ResponseEntity<UserInfo> createUser(@RequestBody UserInfoPassword jsonUser, HttpServletRequest request) {
        UserInfo response = usersApiService.create(request, jsonUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/")
    public ResponseEntity<String> updateUserDetails(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        usersApiService.update(request, userInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/password")
    public ResponseEntity<Map<String, Object>> updateUserPassword(@RequestBody Map<String, Object> jsonBodyRequest, HttpServletRequest request) {
        Map<String, Object> response = usersApiService.updateUserPassword(request, jsonBodyRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Integer userId, HttpServletRequest request) {
        LOG.debug("/api/users/"+userId);
        usersApiService.delete(request, null, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
