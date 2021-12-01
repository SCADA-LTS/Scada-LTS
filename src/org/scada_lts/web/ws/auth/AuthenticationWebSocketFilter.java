package org.scada_lts.web.ws.auth;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationWebSocketFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        User user = Common.getUser((HttpServletRequest) servletRequest);
        if (user != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write("{\"message\": \"bad\"}");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
