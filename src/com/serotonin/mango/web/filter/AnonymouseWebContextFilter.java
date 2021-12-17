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

import com.serotonin.mango.Common;
import com.serotonin.mango.web.dwr.util.AnonymousUserUtils;
import org.directwebremoting.impl.DefaultWebContextBuilder;
import org.scada_lts.mango.service.UserService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Matthew Lohbihler
 */
public class AnonymouseWebContextFilter implements Filter {
    private final DefaultWebContextBuilder builder = new DefaultWebContextBuilder();
    private ServletContext servletContext;

    @Override
    public void init(FilterConfig config) {
        servletContext = config.getServletContext();
    }

    @Override
    public void destroy() {
        // no op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            AnonymousUserUtils.getUser(new UserService()).ifPresent(user -> {
                request.setAttribute(Common.SESSION_USER, user);
                builder.set((HttpServletRequest) request, (HttpServletResponse) response, null, servletContext, null);
            });
            chain.doFilter(request, response);
        }
        finally {
            builder.unset();
        }
    }
}
