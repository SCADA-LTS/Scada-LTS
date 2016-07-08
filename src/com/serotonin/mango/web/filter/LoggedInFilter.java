/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.integration.CrowdUtils;

abstract public class LoggedInFilter implements Filter {
    private final Log LOGGER = LogFactory.getLog(LoggedInFilter.class);

    private String forwardUrl;

    public void init(FilterConfig config) {
        forwardUrl = config.getInitParameter("forwardUrl");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // Assume an http request.
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        boolean loggedIn = true;

        User user = Common.getUser(request);
        if (!checkAccess(user))
            loggedIn = false;

        if (loggedIn && CrowdUtils.isCrowdEnabled()) {
            if (CrowdUtils.isCrowdAuthenticated(user))
                // The user may not have been authenticated by Crowd, so only check with Crowd if it was.
                loggedIn = CrowdUtils.isAuthenticated(request, response);
        }

        if (!loggedIn) {
            LOGGER.info("Denying access to secure page for session id " + request.getSession().getId() + ", uri="
                    + request.getRequestURI());
            response.sendRedirect(request.getContextPath() + forwardUrl);
            //request.getRequestDispatcher(forwardUrl).forward(request, response);
            return;
        }

        // Continue with the chain.
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
        // no op
    }

    abstract protected boolean checkAccess(User user);
}
