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
package com.serotonin.mango.web.dwr.util;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.AjaxFilter;
import org.directwebremoting.AjaxFilterChain;

import com.serotonin.mango.vo.permission.PermissionException;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.web.dwr.MethodFilter;

/**
 * @author Matthew Lohbihler
 */
public class LoggedInAjaxMethodFilter implements AjaxFilter {
    private static final Log LOG = LogFactory.getLog(LoggedInAjaxFilter.class);

    public Object doFilter(Object obj, Method method, Object[] params, AjaxFilterChain chain) throws Exception {
        LOG.debug("Running LoggedInAjaxFilter, hash=" + hashCode());

        if (method.isAnnotationPresent(MethodFilter.class)) {
            LOG.debug("Method filter found. We should check if the user is logged in");
            try {
                Permissions.ensureValidUser();
            }
            catch (PermissionException e) {
                LOG.error("Permission exception while checking method " + method);
                throw e;
            }
        }

        return chain.doFilter(obj, method, params);
    }
}
