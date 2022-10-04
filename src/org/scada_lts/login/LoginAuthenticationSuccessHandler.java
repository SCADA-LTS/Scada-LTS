package org.scada_lts.login;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.integration.CrowdUtils;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.session.HttpSessionListenerImpl;
import org.scada_lts.session.SessionInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Log LOG = LogFactory.getLog(LoginAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        User user = authLocal(request, authentication);
        crowd(user);
        putLogOnIpAddr(request);
        redirect(request, response, user);
    }

    private User authLocal(HttpServletRequest request, Authentication authentication) {
        getUser(authentication).ifPresent(user -> Common.setUser(request, user));
        User user = Common.getUser(request);
        if(user == null) {
            throw new IllegalStateException();
        }
        return user;
    }

    private void crowd(User user) {
        if (CrowdUtils.isCrowdEnabled())
            CrowdUtils.setCrowdAuthenticated(user);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        if (!StringUtils.isEmpty(user.getHomeUrl()))
            redirectStrategy.sendRedirect(request, response, user.getHomeUrl().startsWith("/") ? user.getHomeUrl() : "/" + user.getHomeUrl());
        else
            redirectStrategy.sendRedirect(request, response, "/watch_list.shtm");
    }

    private Optional<User> getUser(Authentication authentication) {
        String user = authentication.getName();
        MangoUser mangoUser = new UserService();
        return Optional.ofNullable(mangoUser.getUser(user));
    }

    /**
     * update ip address and user name, when user sign in
     * @param httpRequest
     */
    private void putLogOnIpAddr(HttpServletRequest httpRequest) {
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
}
