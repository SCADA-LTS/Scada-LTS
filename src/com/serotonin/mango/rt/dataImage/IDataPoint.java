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

/**
 * @author Matthew Lohbihler
 */
public interface IDataPoint {
    List<PointValueTime> getLatestPointValues(int limit);

    void updatePointValue(PointValueTime newValue);

    void updatePointValue(PointValueTime newValue, boolean async);

    void setPointValue(PointValueTime newValue, SetPointSource source);

    PointValueTime getPointValue();

    PointValueTime getPointValueBefore(long time);

    List<PointValueTime> getPointValues(long since);

    List<PointValueTime> getPointValuesBetween(long from, long to);

    int getDataTypeId();
}
