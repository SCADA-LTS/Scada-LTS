package org.scada_lts.login;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.integration.CrowdUtils;
import com.serotonin.mango.web.mvc.controller.ScadaLocaleUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.session.HttpSessionListenerImpl;
import org.scada_lts.session.SessionInfo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class AuthenticationUtils {

    private AuthenticationUtils(){}

    private static final Log LOG = LogFactory.getLog(AuthenticationUtils.class);

    public static Authentication authenticate(String username, String password, HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationManager authenticationManager,
                                              MangoUser mangoUser) {
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        try {
            logout(request);
            Authentication result = authenticationManager.authenticate(auth);
            if(result.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(result);
                authenticateLocal(request, response, result, mangoUser);
            }
            return result;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            logout(request);
            return auth;
        }
    }

    public static User authenticateLocal(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication, MangoUser mangoUser) {
        getUser(authentication, mangoUser).ifPresent(user -> {
            authenticateLocal(request, response, authentication, user);
            mangoUser.recordLogin(user.getId());
            SystemEventType.raiseEvent(new SystemEventType(
                    SystemEventType.TYPE_USER_LOGIN, user.getId()), System
                    .currentTimeMillis(), true, new LocalizableMessage(
                    "event.login", user.getUsername()));
        });
        User user = Common.getUser(request);
        if(user == null) {
            throw new IllegalStateException();
        }
        return user;
    }

    public static void authenticateLocal(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication, User user) {
        setRoles(authentication, user);
        crowd(user);
        Common.setUser(request, user);
        putLogOnIpAddr(request);
        ScadaLocaleUtils.setLocaleInSession(request, response);
    }

    private static void setRoles(Authentication authentication, User user) {
        if(authentication.getAuthorities() != null) {
            Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
            user.removeAttribute("roles");
            user.setAttribute("roles", roles.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session != null) {
            session.invalidate();
            SecurityContextHolder.clearContext();
            Common.setUser(request, null);
        }
        if (CrowdUtils.isCrowdEnabled())
            CrowdUtils.logout(request, null);
    }

    private static Optional<User> getUser(Authentication authentication, MangoUser mangoUser) {
        String user = authentication.getName();
        return Optional.ofNullable(mangoUser.getUser(user));
    }

    /**
     * update ip address, when user sign in
     * @param httpRequest
     */
    private static void putLogOnIpAddr(HttpServletRequest httpRequest) {
        final HttpSession session = httpRequest.getSession(false);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) httpRequest
                    .getServletContext().getAttribute(
                            HttpSessionListenerImpl.SERVLET_CONTEXT_MAP);

            if(map != null) {
                SessionInfo sessionInfo = (SessionInfo) map.get(session.getId());
                if (sessionInfo != null && !sessionInfo.haveLogOnIPAddr()) {

                    final String logOnIpAddr = httpRequest.getRemoteHost();
                    sessionInfo.setLogOnIpAddr(logOnIpAddr);
                }
            }
        } catch (Exception e) {
            // no info on tomcat
            LOG.warn("Remote user set in list sessions:", e);
        }
    }

    private static void crowd(User user) {
        if (CrowdUtils.isCrowdEnabled())
            CrowdUtils.setCrowdAuthenticated(user);
    }
}
