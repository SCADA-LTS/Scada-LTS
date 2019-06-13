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

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.serotonin.mango.rt.dataImage.IDataPoint;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;

/**
 * @author Matthew Lohbihler
 */
abstract public class AbstractPointWrapper {
    protected IDataPoint point;
    protected WrapperContext context;

    AbstractPointWrapper(IDataPoint point, WrapperContext context) {
        this.point = point;
        this.context = context;
    }

    protected MangoValue getValueImpl() {
        PointValueTime pvt = point.getPointValue();
        if (pvt == null)
            return null;
        return pvt.getValue();
    }

    public long getTime() {
        PointValueTime pvt = point.getPointValue();
        if (pvt == null)
            return -1;
        return pvt.getTime();
    }

    public int getMillis() {
        return getCalendar().get(Calendar.MILLISECOND);
    }

    public int getSecond() {
        return getCalendar().get(Calendar.SECOND);
    }

    public int getMinute() {
        return getCalendar().get(Calendar.MINUTE);
    }

    public int getHour() {
        return getCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public int getDay() {
        return getCalendar().get(Calendar.DATE);
    }

    public int getDayOfWeek() {
        return getCalendar().get(Calendar.DAY_OF_WEEK);
    }

    public int getDayOfYear() {
        return getCalendar().get(Calendar.DAY_OF_YEAR);
    }

    public int getMonth() {
        return getCalendar().get(Calendar.MONTH) + 1;
    }

    public int getYear() {
        return getCalendar().get(Calendar.YEAR);
    }

    private GregorianCalendar getCalendar() {
        long time = getTime();
        if (time == -1)
            return null;
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(time);
        return gc;
    }

    public String getHelp() {
        return toString();
    }
}
