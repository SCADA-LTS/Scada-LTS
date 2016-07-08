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
package com.serotonin.mango.rt.dataSource.meta;

import java.util.List;

import com.serotonin.mango.rt.dataImage.IDataPoint;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.util.DateUtils;
import com.serotonin.mango.view.stats.StartsAndRuntimeList;

/**
 * @author Matthew Lohbihler
 */
abstract public class DistinctPointWrapper extends AbstractPointWrapper {
    public DistinctPointWrapper(IDataPoint point, WrapperContext context) {
        super(point, context);
    }

    public StartsAndRuntimeList past(int periodType) {
        return past(periodType, 1);
    }

    public StartsAndRuntimeList past(int periodType, int count) {
        long to = context.getRuntime();
        long from = DateUtils.minus(to, periodType, count);
        return getStats(from, to);
    }

    public StartsAndRuntimeList prev(int periodType) {
        return previous(periodType, 1);
    }

    public StartsAndRuntimeList prev(int periodType, int count) {
        return previous(periodType, count);
    }

    public StartsAndRuntimeList previous(int periodType) {
        return previous(periodType, 1);
    }

    public StartsAndRuntimeList previous(int periodType, int count) {
        long to = DateUtils.truncate(context.getRuntime(), periodType);
        long from = DateUtils.minus(to, periodType, count);
        return getStats(from, to);
    }

    private StartsAndRuntimeList getStats(long from, long to) {
        PointValueTime start = point.getPointValueBefore(from);
        List<PointValueTime> values = point.getPointValuesBetween(from, to);
        StartsAndRuntimeList stats = new StartsAndRuntimeList(start, values, from, to);
        return stats;
    }
}
