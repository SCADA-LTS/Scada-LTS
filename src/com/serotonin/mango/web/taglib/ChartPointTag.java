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
import javax.servlet.jsp.tagext.TagSupport;

import com.serotonin.InvalidArgumentException;
import com.serotonin.util.ColorUtils;
import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 */
public class ChartPointTag extends TagSupport {
    private static final long serialVersionUID = -1;

    private String xid;
    private String color;

    public void setXid(String xid) {
        this.xid = xid;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int doStartTag() throws JspException {
        ChartTag chartTag = (ChartTag) findAncestorWithClass(this, ChartTag.class);
        if (chartTag == null)
            throw new JspException("chartPoint tags must be used within a chart tag");

        // Validate the colour.
        try {
            if (!StringUtils.isEmpty(color))
                ColorUtils.toColor(color);
        }
        catch (InvalidArgumentException e) {
            throw new JspException("Invalid color '" + color + "'");
        }

        chartTag.addChartPoint(xid, color);

        return EVAL_BODY_INCLUDE;
    }

    @Override
    public void release() {
        super.release();
        xid = null;
        color = null;
    }
}
