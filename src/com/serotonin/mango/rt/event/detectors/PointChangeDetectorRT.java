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
package com.serotonin.mango.rt.event.detectors;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.web.i18n.LocalizableMessage;

public class PointChangeDetectorRT extends PointEventDetectorRT {
    private MangoValue oldValue;
    private MangoValue newValue;

    public PointChangeDetectorRT(PointEventDetectorVO vo) {
        this.vo = vo;
    }

    @Override
    protected LocalizableMessage getMessage() {
        return new LocalizableMessage("event.detector.changeCount", vo.njbGetDataPoint().getName(),
                formatValue(oldValue), formatValue(newValue));
    }

    private String formatValue(MangoValue value) {
        return vo.njbGetDataPoint().getTextRenderer().getText(value, TextRenderer.HINT_SPECIFIC);
    }

    @Override
    public void pointChanged(PointValueTime oldValue, PointValueTime newValue) {
        this.oldValue = PointValueTime.getValue(oldValue);
        this.newValue = newValue.getValue();
        raiseEvent(newValue.getTime(), createEventContext());
    }

    public boolean isEventActive() {
        return false;
    }
}
