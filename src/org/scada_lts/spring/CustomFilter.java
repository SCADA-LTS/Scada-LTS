package org.scada_lts.spring;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.User;
import com.serotonin.util.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

public class CustomFilter extends UsernamePasswordAuthenticationFilter {

    public CustomFilter (String urlLogin, String method) {
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(urlLogin, method));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        // do your stuff ...
//
//        return super.attemptAuthentication(request, response);
        String username = this.obtainUsername(request);
        String password = this.obtainPassword(request);
        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        this.setDetails(request, authRequest);
        Authentication auth = this.getAuthenticationManager().authenticate(authRequest);
        return auth;
    }

//    private ModelAndView performLogin(HttpServletRequest request, String username) {
//        // Check if the user is already logged in.
//        User user = Common.getUser(request);
//        if (user != null && user.getUsername().equals(username)) {
//            // The user is already logged in. Nothing to do.
//        }
//        else {
//            UserDao userDao = new UserDao();
//            user = userDao.getUser(username);
//
//            // Update the last login time.
//            userDao.recordLogin(user.getId());
//
//            // Add the user object to the session. This indicates to the rest
//            // of the application whether the user is logged in or not.
//            Common.setUser(request, user);
//        }
//
//        if (!StringUtils.isEmpty(user.getHomeUrl()))
//            return new ModelAndView(new RedirectView(user.getHomeUrl()));
//        else
//            return new ModelAndView(new RedirectView("watch_list.shtm"));
//    }
}
