package org.scada_lts.login;

import com.serotonin.mango.vo.User;
import com.serotonin.util.StringUtils;
import org.scada_lts.mango.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.scada_lts.login.AuthenticationUtils.authenticateLocal;

public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        User user = authenticateLocal(request, authentication, new UserService());
        redirect(request, response, user);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        if (!StringUtils.isEmpty(user.getHomeUrl()))
            redirectStrategy.sendRedirect(request, response, user.getHomeUrl().startsWith("/") ? user.getHomeUrl() : "/" + user.getHomeUrl());
        else
            redirectStrategy.sendRedirect(request, response, "/watch_list.shtm");
    }

}
