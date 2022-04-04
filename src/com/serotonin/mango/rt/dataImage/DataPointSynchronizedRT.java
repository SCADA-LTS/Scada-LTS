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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.DataPointVO;

public class DataPointSynchronizedRT extends DataPointNonSyncRT implements IDataPointRT {

    private static final Log LOG = LogFactory.getLog(DataPointSynchronizedRT.class);

    private PointValueTime pointValue;

    /**
     * This is the value around which tolerance decisions will be made when
     * determining whether to log numeric values.
     */
    private double toleranceOrigin;

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
    public synchronized PointValueTime getOldAndSetNew(PointValueTime newValue) {
        if(newValue == null) {
            return pointValue;
        }
        if(pointValue == null || newValue.getTime() >= pointValue.getTime()) {
            PointValueTime oldValue = pointValue;
            pointValue = newValue;
            return oldValue;
        }
        return pointValue;
    }

    @Override
    public synchronized boolean updateToleranceOrigin(PointValueTime newValue, boolean forceSet) {
        if(forceSet) {
            this.toleranceOrigin = newValue.getDoubleValue();
            return true;
        }
        boolean logValue;
        double newd = newValue.getDoubleValue();
        // See if the new value is outside of the tolerance.
        double diff = this.toleranceOrigin - newd;
        if (diff < 0)
            diff = -diff;

        if (diff > getVO().getTolerance()) {
            this.toleranceOrigin = newd;
            logValue = true;
        } else
            logValue = false;
        return logValue;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + getId();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DataPointSynchronizedRT other = (DataPointSynchronizedRT) obj;
        if (getId() != other.getId())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DataPointSynchronizedRT(id=" + getId() + ", name=" + getVO().getName() + ")";
    }
}
