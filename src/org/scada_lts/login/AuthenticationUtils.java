package org.scada_lts.login;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.integration.CrowdUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.session.HttpSessionListenerImpl;
import org.scada_lts.session.SessionInfo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;

public class AuthenticationUtils {

    private static final Log LOG = LogFactory.getLog(AuthenticationUtils.class);

    public static Authentication authenticate(String username, String password, HttpServletRequest request,
                                              AuthenticationManager authenticationManager, MangoUser mangoUser) {
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        try {
            logout(request);
            Authentication result = authenticationManager.authenticate(auth);
            if(result.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(result);
                authenticateLocal(request, result, mangoUser);
            }
            return result;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            logout(request);
            return auth;
        }
    }

    public static User authenticateLocal(HttpServletRequest request, Authentication authentication, MangoUser mangoUser) {
        getUser(authentication, mangoUser).ifPresent(user -> {
            Common.setUser(request, user);
            putLogOnIpAddr(request);
            crowd(user);
            mangoUser.recordLogin(user.getId());
        });
        User user = Common.getUser(request);
        if(user == null) {
            throw new IllegalStateException();
        }
        return user;
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
