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
package com.serotonin.mango.rt.dataSource.virtual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.vo.dataSource.virtual.AnalogAttractorChangeVO;

public class AnalogAttractorChangeRT extends ChangeTypeRT {
    private static Log log = LogFactory.getLog(AnalogAttractorChangeRT.class);

    private final AnalogAttractorChangeVO vo;

    public AnalogAttractorChangeRT(AnalogAttractorChangeVO vo) {
        this.vo = vo;
    }

    @Override
    public MangoValue change(MangoValue currentValue) {
        double current = currentValue.getDoubleValue();

        // Get the value we're attracted to.
        DataPointRT point = Common.ctx.getRuntimeManager().getDataPoint(vo.getAttractionPointId());
        if (point == null) {
            if (log.isDebugEnabled())
                log.debug("Attraction point is not enabled");
            // Point is not currently active.
            return new NumericValue(current);
        }

        MangoValue attractorValue = PointValueTime.getValue(point.getPointValue());
        if (attractorValue == null) {
            if (log.isDebugEnabled())
                log.debug("Attraction point has not vaue");
            return new NumericValue(current);
        }

        double attraction = attractorValue.getDoubleValue();

        // Move half the distance toward the attractor...
        double change = (attraction - current) / 2;

        // ... subject to the maximum change allowed...
        if (change < 0 && -change > vo.getMaxChange())
            change = -vo.getMaxChange();
        else if (change > vo.getMaxChange())
            change = vo.getMaxChange();

        // ... and a random fluctuation.
        change += RANDOM.nextDouble() * vo.getVolatility() * 2 - vo.getVolatility();

        if (log.isDebugEnabled())
            log.debug("attraction=" + attraction + ", change=" + change);

        return new NumericValue(current + change);
    }
}
