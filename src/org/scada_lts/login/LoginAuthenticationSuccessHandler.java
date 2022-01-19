package org.scada_lts.login;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.integration.CrowdUtils;
import com.serotonin.util.StringUtils;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException {
        getUser(authentication).ifPresent(user -> Common.setUser(request, user));
        User user = Common.getUser(request);
        if(user == null) {
            throw new IllegalStateException();
        }
        if (CrowdUtils.isCrowdEnabled())
            CrowdUtils.setCrowdAuthenticated(user);
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
}
