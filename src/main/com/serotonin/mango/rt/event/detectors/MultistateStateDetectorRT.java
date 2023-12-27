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
import com.serotonin.mango.util.PointEventDetectorUtils;
import com.serotonin.mango.view.event.NoneEventRenderer;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.web.i18n.LocalizableMessage;

public class MultistateStateDetectorRT extends StateDetectorRT {
    public MultistateStateDetectorRT(PointEventDetectorVO vo) {
        this.vo = vo;
    }

    @Override
    public LocalizableMessage getMessage() {
        String name = vo.njbGetDataPoint().getName();
        String prettyText = vo.njbGetDataPoint().getTextRenderer().getText(vo.getMultistateState(),
                TextRenderer.HINT_SPECIFIC);
        LocalizableMessage durationDescription = getDurationDescription();
        String description = PointEventDetectorUtils.getDescription(vo);
        String eventRendererText = (vo.njbGetDataPoint().getEventTextRenderer() == null) ? "" : vo.njbGetDataPoint().getEventTextRenderer().getText(vo.getMultistateState());

        if (durationDescription == null)
            return new LocalizableMessage("event.detector.state", name, prettyText, description,
                    eventRendererText);
        return new LocalizableMessage("event.detector.periodState", name, prettyText, durationDescription,
                description, eventRendererText);
    }

    @Override
    protected LocalizableMessage getShortMessage() {
        if (vo.njbGetDataPoint().getEventTextRenderer() != null &&
                !vo.njbGetDataPoint().getEventTextRenderer().getTypeName().equals(NoneEventRenderer.TYPE_NAME) &&
                vo.njbGetDataPoint().getEventTextRenderer().getText(vo.getMultistateState()) != null &&
                (!vo.njbGetDataPoint().getEventTextRenderer().getText(vo.getMultistateState()).equals(""))) {
            String eventRendererText = vo.njbGetDataPoint().getEventTextRenderer().getText(vo.getMultistateState());
            return new LocalizableMessage("event.detector.shortMessage", vo.njbGetDataPoint().getName(),
                    eventRendererText);
        } else {
            return getMessage();
        }
    }

    @Override
    protected boolean stateDetected(PointValueTime newValue) {
        int newMultistate = newValue.getIntegerValue();
        return newMultistate == vo.getMultistateState();
    }
}
