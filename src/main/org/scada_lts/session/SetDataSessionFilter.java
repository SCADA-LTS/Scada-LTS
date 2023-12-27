package org.scada_lts.session;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.scada_lts.login.AuthenticationUtils;
import org.scada_lts.mango.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SetDataSessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !isAnonymousUser(authentication)) {
                User user = Common.getUser(req);
                if (user == null) {
                    UserService userService = new UserService();
                    user = userService.getUser(authentication.getName());
                    AuthenticationUtils.authenticateLocal(req, res, authentication, user);
                }
            }
        }
        chain.doFilter(request, response);
    }

    private static boolean isAnonymousUser(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ANONYMOUS".equals(a.getAuthority()));
    }

}