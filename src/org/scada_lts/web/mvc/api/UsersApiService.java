package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.dao.error.EntityNotUniqueException;
import org.scada_lts.exception.PasswordMismatchException;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.mvc.api.exceptions.*;
import org.scada_lts.web.mvc.api.user.UserInfo;
import org.scada_lts.web.mvc.api.user.UserInfoSimple;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ApiUtils.setPassword;
import static org.scada_lts.utils.ApiUtils.validate;
import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;

public class UsersApiService implements CrudService<UserInfo>, GetIdentifiers<UserInfoSimple> {

    private final UserService userService;

    public UsersApiService(UserService userService) {
        this.userService = userService;
    }

    public boolean isUnique(HttpServletRequest request, String username) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            return userService.isUsernameUnique(username);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    @Override
    public UserInfo create(HttpServletRequest request, UserInfo userInfo) {
        User userToSave = userInfo.toUser();
        userToSave.setLang(SystemSettingsDAO.getValue(SystemSettingsDAO.LANGUAGE, "en"));
        DwrResponseI18n response = validate(userToSave);
        if(response.getHasMessages())
            throw new BadRequestException(response, request.getRequestURI());
        try {
            userService.saveUser(userToSave);
        } catch (EntityNotUniqueException ex) {
            throw new ConflictException(ex, request.getRequestURI());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new UserInfo(userToSave);
    }

    @Override
    public UserInfo update(HttpServletRequest request, UserInfo userInfo) {
        User user = Common.getUser(request);
        User userToSave;
        if(user.isAdmin()) {
            userToSave = userInfo.toUser();
        } else if (userInfo.getId() != user.getId()) {
            throw new ForbiddenException("No access.", request.getRequestURI());
        } else {
            userToSave = userInfo.toUserNonAdmin(user);
        }
        userToSave.setLang(user.getLang());
        DwrResponseI18n response = validate(userToSave);
        if(response.getHasMessages())
            throw new BadRequestException(response, request.getRequestURI());
        try {
            setPassword(userToSave, userService);
            userService.saveUser(userToSave);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        if(userInfo.getId() == user.getId()) {
            Common.updateUserInSession(request, userToSave);
        }
        return new UserInfo(user);
    }

    @Override
    public UserInfo delete(HttpServletRequest request, String xid, Integer id) {
        checkIfNonAdminThenUnauthorized(request);
        User user;
        try {
            user = userService.getUser(id);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
        if(user == null)
            throw new NotFoundException("User not exists.", request.getRequestURI());
        try {
            userService.deleteUser(id);
            return new UserInfo(user);
        } catch (Exception e) {
            throw new InternalServerErrorException(e, request.getRequestURI());
        }
    }

    @Override
    public List<UserInfoSimple> getIdentifiers(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<User> users = userService.getUsers();
            return users.stream()
                    .map(UserInfoSimple::new)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    @Override
    public UserInfo read(HttpServletRequest request, String xid, Integer id) {
        User logged = Common.getUser(request);
        if(!logged.isAdmin() && logged.getId() != id)
            throw new UnauthorizedException(request.getRequestURI());
        User user;
        try {
            user = userService.getUser(id);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        if(user == null) {
            throw new NotFoundException("User with id not exists: " + id, request.getRequestURI());
        }
        return new UserInfo(user);
    }

    @Override
    public List<UserInfo> readAll(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        try {
            List<User> users = userService.getUsers();
            return users.stream()
                    .map(UserInfo::new)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    public Map<String, Object> updateUserPassword(HttpServletRequest request, Map<String, Object> jsonBodyRequest) {
        Map<String, Object> result = new HashMap<>();
        String password = (String) jsonBodyRequest.get("password");
        Integer userId = (Integer) jsonBodyRequest.get("userId");
        if(userId == null)
            throw new BadRequestException("userId is null. ", request.getRequestURI());
        if(StringUtils.isEmpty(password))
            throw new BadRequestException("password empty. ", request.getRequestURI());

        User user = Common.getUser(request);
        if (user.isAdmin()) {
            try {
                userService.updateUserPassword(
                        userId,
                        password
                );
            } catch (PasswordMismatchException e1) {
                result.put("status", "failed");
                result.put("description", "validation.password.wrong");
                return result;
            } catch (Exception e) {
                throw new InternalServerErrorException(e, request.getRequestURI());
            }
        } else if(userId != user.getId()) {
            throw new ForbiddenException("Not access.", request.getRequestURI());
        } else {
            String current = (String) jsonBodyRequest.get("current");
            if(StringUtils.isEmpty(current))
                throw new BadRequestException("current empty.", request.getRequestURI());
            try {
                userService.updateUserPassword(
                        user.getId(),
                        password,
                        current
                );
            } catch (PasswordMismatchException e1) {
                result.put("status", "failed");
                result.put("description", "validation.password.wrong");
                return result;
            } catch (Exception e) {
                throw new InternalServerErrorException(e, request.getRequestURI());
            }
        }
        result.put("status", "ok");
        return result;
    }
}
