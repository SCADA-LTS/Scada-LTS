package org.scada_lts.web.security;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XSSFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        request.getParameterMap().forEach((key, values) -> {
            for (String value : values) {
                if(!XSSUtils.validate(value)) {
                    try {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "Potential XSS detected in the request parameter: " + key);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
            }
        });

        filterChain.doFilter(request, response);
    }

}
