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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.AjaxFilter;
import org.directwebremoting.AjaxFilterChain;

import com.serotonin.mango.rt.RTException;
import com.serotonin.mango.vo.permission.PermissionException;

/**
 * @author Matthew Lohbihler
 */
public class ExceptionDetectionFilter implements AjaxFilter {
    private static final Log LOG = LogFactory.getLog(ExceptionDetectionFilter.class);

    public Object doFilter(Object obj, Method method, Object[] params, AjaxFilterChain chain) throws Exception {
        try {
            return chain.doFilter(obj, method, params);
        }
        catch (PermissionException e) {
            throw e;
        }
        catch (RTException e) {
            throw e;
        }
        catch (Exception e) {
            Throwable e2 = e;
            if (e2 instanceof InvocationTargetException)
                e2 = ((InvocationTargetException) e).getTargetException();
            LOG.error("DWR invocation exception", e2);

            throw e;
        }
    }
}
