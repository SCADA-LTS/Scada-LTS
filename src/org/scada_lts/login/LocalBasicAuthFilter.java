package org.scada_lts.login;

import com.serotonin.mango.vo.User;
import org.scada_lts.mango.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.scada_lts.login.AuthenticationUtils.authenticateLocal;

public class LocalBasicAuthFilter extends BasicAuthenticationFilter {

    public LocalBasicAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, Authentication authentication) {
        authenticateLocal(request, response, authentication, new UserService());
    }
}
