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
package com.serotonin.mango.view.custom;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.web.dwr.BaseDwr;
import com.serotonin.mango.web.dwr.beans.CustomComponentState;

/**
 * @author Matthew Lohbihler
 */
public class CustomViewPoint extends CustomViewComponent {
    private final DataPointVO dataPointVO;
    private final boolean raw;
    private final String disabledValue;
    private final boolean time;

    public CustomViewPoint(int id, DataPointVO dataPointVO, boolean raw, String disabledValue, boolean time) {
        super(id);
        this.dataPointVO = dataPointVO;
        this.raw = raw;
        if (disabledValue == null)
            this.disabledValue = "";
        else
            this.disabledValue = disabledValue;
        this.time = time;
    }

    // public DataPointVO getDataPointVO() {
    // return dataPointVO;
    // }
    //
    // public boolean isRaw() {
    // return raw;
    // }
    //
    // public String getDisabledValue() {
    // return disabledValue;
    // }
    //
    // public boolean isTime() {
    // return time;
    // }

    @Override
    protected void createStateImpl(RuntimeManager rtm, HttpServletRequest request, CustomComponentState state) {
        String value;
        DataPointRT dataPointRT = rtm.getDataPoint(dataPointVO.getId());
        if (dataPointRT == null)
            value = disabledValue;
        else {
            PointValueTime pvt = dataPointRT.getPointValue();

            if (pvt != null && pvt.getValue() instanceof ImageValue) {
                // Text renderers don't help here. Create a thumbnail.
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("point", dataPointVO);
                model.put("pointValue", pvt);
                value = BaseDwr.generateContent(request, "imageValueThumbnail.jsp", model);
            }
            else {
                int hint = raw ? TextRenderer.HINT_RAW : TextRenderer.HINT_FULL;
                value = dataPointVO.getTextRenderer().getText(pvt, hint);
                if (pvt != null && time)
                    state.setTime(pvt.getTime());
            }
        }
        state.setValue(value);
    }
}
