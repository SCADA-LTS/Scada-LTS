package org.scada_lts.web.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XssFilter extends OncePerRequestFilter {

    private static final Logger LOG = LogManager.getLogger(XssFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String query = request.getQueryString();
        if (query != null && !XssUtils.validateHttpQuery(query)) {
            LOG.error("Potential XSS detected in request. Request URI: {}, Query: {}",
                    request.getRequestURI(), request.getQueryString());

            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Potential XSS detected in the request Query: " + request.getQueryString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        filterChain.doFilter(request, response);
    }
}
