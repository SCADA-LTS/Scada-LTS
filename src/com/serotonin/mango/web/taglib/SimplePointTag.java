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

import javax.servlet.jsp.JspException;

import com.serotonin.mango.view.custom.CustomView;
import com.serotonin.mango.vo.DataPointVO;

/**
 * @author Matthew Lohbihler
 */
public class SimplePointTag extends ViewTagSupport {
    private static final long serialVersionUID = -1;

    private String xid;
    private boolean raw;
    private String disabledValue;
    private boolean time;

    public void setXid(String xid) {
        this.xid = xid;
    }

    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    public void setDisabledValue(String disabledValue) {
        this.disabledValue = disabledValue;
    }

    public void setTime(boolean time) {
        this.time = time;
    }

    @Override
    public int doStartTag() throws JspException {
        // Find the custom view.
        CustomView view = getCustomView();

        // Find the point.
        DataPointVO dataPointVO = getDataPointVO(view, xid);

        // Add the point to the view
        int id = view.addPoint(dataPointVO, raw, disabledValue, time);

        // Add the id for the point to the page context.
        pageContext.setAttribute("componentId", id);

        return EVAL_BODY_INCLUDE;
    }

    @Override
    public void release() {
        super.release();
        xid = null;
        raw = false;
        disabledValue = null;
        time = false;
    }
}
