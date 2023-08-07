package com.serotonin.mango.web.dwr.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.PermissionException;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.scada_lts.login.AuthenticationUtils;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public final class AnonymousUserUtils {

    private final AuthenticationManager authenticationManager;

    public AnonymousUserUtils() {
        this.authenticationManager = ApplicationBeans.getBean("authenticationManager", AuthenticationManager.class);
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public static Optional<User> getUser(UserService userService, HttpServletRequest request) {
        User user = Common.getUser(request);
        if (user == null) {
            user = getAnonymousUser(userService);
        }
        return Optional.ofNullable(user);
    }

    public static void authenticateAnonymousUser(User user, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticateAnonymousUser(user, request, response, new AnonymousUserUtils());
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new PermissionException("Not logged!", user);
        }
    }

    public static Optional<HttpServletRequest> getRequest() {
        WebContext webContext = WebContextFactory.get();
        if(webContext == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(webContext.getHttpServletRequest());
    }

    public static Optional<HttpServletResponse> getResponse() {
        WebContext webContext = WebContextFactory.get();
        if(webContext == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(webContext.getHttpServletResponse());
    }
    private static User getAnonymousUser(UserService userService) {
        return userService.getUser("anonymous-user");
    }

    private static Authentication authenticateAnonymousUser(User user, HttpServletRequest request, HttpServletResponse response,
                                                            AnonymousUserUtils anonymousUserUtils) {
        if (Common.getUser(request) == null) {
            return AuthenticationUtils.authenticate(user.getUsername(), "anonymous", request, response,
                    anonymousUserUtils.getAuthenticationManager(), new UserService());
        }
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(securityContext != null) {
            return securityContext.getAuthentication();
        }
        return null;
    }
}
