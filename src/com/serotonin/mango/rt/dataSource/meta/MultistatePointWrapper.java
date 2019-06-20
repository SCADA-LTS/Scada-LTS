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

import com.serotonin.mango.rt.dataImage.IDataPoint;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.util.DateUtils;

/**
 * @author Matthew Lohbihler
 */
public class MultistatePointWrapper extends DistinctPointWrapper {
    public MultistatePointWrapper(IDataPoint point, WrapperContext context) {
        super(point, context);
    }

    public int getValue() {
        MangoValue value = getValueImpl();
        if (value == null)
            return 0;
        return value.getIntegerValue();
    }

    @Override
    public String toString() {
        return "{value=" + getValue() + ", ago(periodType, count), past(periodType, count), prev(periodType, count), "
                + "previous(periodType, count)}";
    }

    public int ago(int periodType) {
        return ago(periodType, 1);
    }

    public int ago(int periodType, int count) {
        long from = DateUtils.minus(context.getRuntime(), periodType, count);
        PointValueTime pvt = point.getPointValueBefore(from);
        if (pvt == null)
            return 0;
        return pvt.getIntegerValue();
    }
}
