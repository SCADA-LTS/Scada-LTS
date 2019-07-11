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
package com.serotonin.mango.rt.dataImage;

import java.util.List;

import com.serotonin.mango.Common;
import org.scada_lts.mango.service.ServiceInstances;

/**
 * @author Matthew Lohbihler
 */
public class PointValueFacade {
    private final int dataPointId;
    private final DataPointRT point;

    public PointValueFacade(int dataPointId) {
        this.dataPointId = dataPointId;
        point = Common.ctx.getRuntimeManager().getDataPoint(dataPointId);
    }

    public List<PointValueTime> getPointValues(long since) {
        if (point != null)
            return point.getPointValues(since);
        return ServiceInstances.PointValueService.getPointValues(dataPointId, since);
    }

    public PointValueTime getPointValueBefore(long time) {
        if (point != null)
            return point.getPointValueBefore(time);
        return ServiceInstances.PointValueService.getPointValueBefore(dataPointId, time);
    }

    public PointValueTime getPointValueAt(long time) {
        if (point != null)
            return point.getPointValueAt(time);
        return ServiceInstances.PointValueService.getPointValueAt(dataPointId, time);
    }

    public PointValueTime getPointValue() {
        if (point != null)
            return point.getPointValue();
        return ServiceInstances.PointValueService.getLatestPointValue(dataPointId);
    }

    public List<PointValueTime> getPointValuesBetween(long from, long to) {
        if (point != null)
            return point.getPointValuesBetween(from, to);
        return ServiceInstances.PointValueService.getPointValuesBetween(dataPointId, from, to);
    }

    public List<PointValueTime> getLatestPointValues(int limit) {
        if (point != null)
            return point.getLatestPointValues(limit);
        return ServiceInstances.PointValueService.getLatestPointValues(dataPointId, limit);
    }
}
