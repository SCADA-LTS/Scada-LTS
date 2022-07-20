package org.scada_lts.web.mvc.api;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.mango.service.UsersProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/api/userProfiles")
public class UserProfilesAPI {

    private static final Log LOG = LogFactory.getLog(UserProfilesAPI.class);
    UsersProfileService usersProfileService = new UsersProfileService();

    @GetMapping(value = "/")
    public ResponseEntity<List<ScadaObjectIdentifier>> getAllProfiles(
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                return new ResponseEntity<>(usersProfileService.getAllUserProfiles(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UsersProfileVO> getUserProfile(
            @PathVariable("id") Integer profileId,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                return new ResponseEntity<>(usersProfileService.getUserProfileById(profileId), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/generateXid")
    public ResponseEntity<String> getUniqueXid(HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                return new ResponseEntity<>(usersProfileService.generateUniqueXid(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/")
    public ResponseEntity<String> createUserProfile(
            @RequestBody UsersProfileVO usersProfile,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                usersProfileService.saveUsersProfile(usersProfile);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (DAOException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/")
    public ResponseEntity<String> updateUserProfile(
            @RequestBody UsersProfileVO usersProfile,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                usersProfileService.updateProfile(usersProfile);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteUserProfile(
            @PathVariable("id") Integer userProfileId,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                usersProfileService.deleteUserProfile(userProfileId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
