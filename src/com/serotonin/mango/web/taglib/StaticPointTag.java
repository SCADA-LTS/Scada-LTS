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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.view.custom.CustomView;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.web.dwr.BaseDwr;

/**
 * @author Matthew Lohbihler
 */
public class StaticPointTag extends ViewTagSupport {
    private static final long serialVersionUID = -1;

    private String xid;
    private boolean raw;
    private String disabledValue;

    public void setXid(String xid) {
        this.xid = xid;
    }

    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    public void setDisabledValue(String disabledValue) {
        this.disabledValue = disabledValue;
    }

    @Override
    public int doStartTag() throws JspException {
        // Find the custom view.
        CustomView view = getCustomView();

        // Find the point.
        DataPointVO dataPointVO = getDataPointVO(view, xid);

        // Write the value into the page.
        JspWriter out = pageContext.getOut();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        DataPointRT dataPointRT = Common.ctx.getRuntimeManager().getDataPoint(dataPointVO.getId());
        if (dataPointRT == null)
            write(out, disabledValue);
        else {
            PointValueTime pvt = dataPointRT.getPointValue();

            if (pvt != null && pvt.getValue() instanceof ImageValue) {
                // Text renderers don't help here. Create a thumbnail.
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("point", dataPointVO);
                model.put("pointValue", pvt);
                write(out, BaseDwr.generateContent(request, "imageValueThumbnail.jsp", model));
            }
            else {
                int hint = raw ? TextRenderer.HINT_RAW : TextRenderer.HINT_FULL;
                write(out, dataPointVO.getTextRenderer().getText(pvt, hint));
            }
        }

        return EVAL_BODY_INCLUDE;
    }

    private void write(JspWriter out, String content) throws JspException {
        try {
            out.append(content);
        }
        catch (IOException e) {
            throw new JspException(e);
        }
    }

    @Override
    public void release() {
        super.release();
        xid = null;
        raw = false;
        disabledValue = null;
    }
}
