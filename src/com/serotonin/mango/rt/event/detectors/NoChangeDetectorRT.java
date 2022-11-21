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
import com.serotonin.mango.util.PointEventDetectorUtils;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class NoChangeDetectorRT extends DifferenceDetectorRT {
    private MangoValue newValue;

    public NoChangeDetectorRT(PointEventDetectorVO vo) {
        this.vo = vo;
    }

    @Override
    public void pointChanged(PointValueTime oldValue, PointValueTime newValue) {
        pointData();
        this.newValue = newValue.getValue();
    }

    @Override
    public LocalizableMessage getMessage() {
        String description = PointEventDetectorUtils.getDescription(vo);
        String eventRendererText = (vo.njbGetDataPoint().getEventTextRenderer() == null) ? "" : vo.njbGetDataPoint().getEventTextRenderer().getText(newValue);
        return new LocalizableMessage("event.detector.noChange", vo.njbGetDataPoint().getName(),
                getDurationDescription(), description, eventRendererText);
    }

    @Override
    protected LocalizableMessage getShortMessage() {
        return getMessage();
    }
}
