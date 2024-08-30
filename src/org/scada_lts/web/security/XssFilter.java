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

        String queryString = request.getQueryString();
        if (queryString != null && !XssUtils.validateHttpQuery(queryString)) {
            LOG.warn("Potential XSS detected in request. Request URI: {}, Query: {}",
                    request.getRequestURI(), queryString);

            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Potential XSS detected in the request Query: " + queryString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        filterChain.doFilter(request, response);
    }
}
