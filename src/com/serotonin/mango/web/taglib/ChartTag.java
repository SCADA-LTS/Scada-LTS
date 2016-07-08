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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.custom.CustomView;
import com.serotonin.mango.view.custom.CustomViewChartPoint;
import com.serotonin.mango.vo.DataPointVO;

/**
 * @author Matthew Lohbihler
 */
public class ChartTag extends ViewTagSupport {
    private static final long serialVersionUID = -1;

    private int duration;
    private String durationType;
    private int width;
    private int height;
    private List<CustomViewChartPoint> points;
    private CustomView view;

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int doStartTag() throws JspException {
        points = new ArrayList<CustomViewChartPoint>();

        // Find the custom view.
        view = getCustomView();

        return EVAL_BODY_INCLUDE;
    }

    void addChartPoint(String xid, String color) throws JspException {
        DataPointVO dataPointVO = getDataPointVO(view, xid);
        points.add(new CustomViewChartPoint(dataPointVO, color));
    }

    @Override
    public int doEndTag() throws JspException {
        int periodType = Common.TIME_PERIOD_CODES.getId(durationType.toUpperCase());
        if (periodType == -1)
            throw new JspException("Invalid durationType. Must be one of " + Common.TIME_PERIOD_CODES.getCodeList());
        long millis = Common.getMillis(periodType, duration);

        // Add the chart to the view
        int id = view.addChart(millis, width, height, points);

        // Add the id for the point to the page context.
        pageContext.setAttribute("componentId", id);

        return EVAL_PAGE;
    }

    @Override
    public void release() {
        super.release();
        duration = 0;
        durationType = null;
        width = 0;
        height = 0;
        view = null;
        points = null;
    }
}
