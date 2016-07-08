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
package com.serotonin.mango.web.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.view.custom.CustomView;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.permission.Permissions;

/**
 * @author Matthew Lohbihler
 */
abstract public class ViewTagSupport extends TagSupport {
    private static final long serialVersionUID = -1;

    protected CustomView getCustomView() throws JspException {
        CustomView view = Common.getCustomView((HttpServletRequest) pageContext.getRequest());
        if (view == null)
            throw new JspException("No custom view in session. Use the init tag before defining points");
        return view;
    }

    protected DataPointVO getDataPointVO(CustomView view, String xid) throws JspException {
        // Find the point.
        DataPointVO dataPointVO = new DataPointDao().getDataPoint(xid);
        if (dataPointVO == null)
            throw new JspException("Point with XID '" + xid + "' not found");

        // Check that the authorizing user has access to the point.
        Permissions.ensureDataPointReadPermission(view.getAuthorityUser(), dataPointVO);

        return dataPointVO;
    }
}
