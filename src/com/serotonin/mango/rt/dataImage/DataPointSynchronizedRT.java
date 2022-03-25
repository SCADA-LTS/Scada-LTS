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

import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.DataPointVO;

public class DataPointSynchronizedRT extends DataPointRT implements IDataPointRT {

    private PointValueTime pointValue;

    public DataPointSynchronizedRT(DataPointVO vo, PointLocatorRT pointLocator) {
        super(vo, pointLocator);
    }

    public DataPointSynchronizedRT(DataPointVO vo, PointLocatorRT pointLocator, int cacheSize, int maxSize) {
        super(vo, pointLocator, cacheSize, maxSize);
    }

    public DataPointSynchronizedRT(DataPointVO vo) {
        super(vo);
    }

    @Override
    protected synchronized PointValueTime getOldAndSetNew(PointValueTime newValue) {
        if(newValue == null) {
            return pointValue;
        }
        PointValueTime oldValue = pointValue;
        pointValue = newValue;
        return oldValue;
    }
}
