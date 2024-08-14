package org.scada_lts.web.security;

import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class XSSFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        request.getParameterMap().forEach((key, values) -> {
            for (String value : values) {
                if(!XSSUtils.validate(value)) {
                    throw new InternalServerErrorException(Arrays.asList("Potential XSS detected in the request parameter: " + key), request.getRequestURI());
                }
            }
        });

        filterChain.doFilter(request, response);
    }

}
