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

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;


public class DataPointNonSyncRT extends DataPointRT implements IDataPointRT {
    private static final Log LOG = LogFactory.getLog(DataPointNonSyncRT.class);

    // Runtime data.
    private PointValueTime pointValue;

    /**
     * This is the value around which tolerance decisions will be made when
     * determining whether to log numeric values.
     */
    private double toleranceOrigin;

    public DataPointNonSyncRT(DataPointVO vo, PointLocatorRT pointLocator) {
        super(vo, pointLocator);
    }
    public DataPointNonSyncRT(DataPointVO vo, PointLocatorRT pointLocator, int cacheSize, int maxSize) {
        super(vo, pointLocator, cacheSize, maxSize);
    }

    public DataPointNonSyncRT(DataPointVO vo) {
        super(vo);
    }

    @Override
    protected void savePointValue(PointValueTime newValue, SetPointSource source,
                                boolean async) {
        // Null values are not very nice, and since they don't have a specific
        // meaning they are hereby ignored.
        if (newValue == null)
            return;

        // Check the data type of the value against that of the locator, just
        // for fun.
        int valueDataType = DataTypes.getDataType(newValue.getValue());
        if (valueDataType != DataTypes.UNKNOWN
                && valueDataType != getVO().getPointLocator().getDataTypeId())
            // This should never happen, but if it does it can have serious
            // downstream consequences. Also, we need
            // to know how it happened, and the stack trace here provides the
            // best information.
            throw new ShouldNeverHappenException(
                    "Data type mismatch between new value and point locator: newValue="
                            + DataTypes.getDataType(newValue.getValue())
                            + ", locator="
                            + getVO().getPointLocator().getDataTypeId());

        // Check if this value qualifies for discardation.
        if (getVO().isDiscardExtremeValues()
                && DataTypes.getDataType(newValue.getValue()) == DataTypes.NUMERIC) {
            double newd = newValue.getDoubleValue();
            if (newd < getVO().getDiscardLowLimit()
                    || newd > getVO().getDiscardHighLimit())
                // Discard the value
                return;
        }

        if (newValue.getTime() > System.currentTimeMillis()
                + SystemSettingsDAO.getFutureDateLimit()) {
            // Too far future dated. Toss it. But log a message first.
            LOG.warn(
                    "Future dated value detected: pointId=" + getVO().getId()
                            + ", value=" + newValue.getStringValue()
                            + ", type=" + getVO().getPointLocator().getDataTypeId()
                            + ", ts=" + newValue.getTime(), new Exception());
            return;
        }

        PointValueTime oldValue = getOldAndSetNew(newValue);

        boolean backdated = oldValue != null
                && newValue.getTime() < oldValue.getTime();

        // Determine whether the new value qualifies for logging.
        boolean logValue;
        // ... or even saving in the cache.
        boolean saveValue = true;
        switch (getVO().getLoggingType()) {
            case DataPointVO.LoggingTypes.ON_CHANGE:
                if (oldValue == null) {
                    logValue = true;
                    if(newValue.getValue() instanceof NumericValue) {
                        updateToleranceOrigin(newValue, true);
                    }
                } else if (backdated)
                    // Backdated. Ignore it
                    logValue = false;
                else {
                    if (newValue.getValue() instanceof NumericValue) {
                        // Get the new double
                        logValue = updateToleranceOrigin(newValue, false);
                    } else
                        logValue = !ObjectUtils.isEqual(newValue.getValue(), oldValue.getValue());
                }

                saveValue = logValue;
                break;
            case DataPointVO.LoggingTypes.ALL:
                logValue = true;
                break;
            case DataPointVO.LoggingTypes.ON_TS_CHANGE:
                if (oldValue == null)
                    logValue = true;
                else
                    logValue = newValue.getTime() != oldValue.getTime();

                saveValue = logValue;
                break;
            case DataPointVO.LoggingTypes.INTERVAL:
                if (!backdated)
                    intervalSave(newValue);
            default:
                logValue = false;
        }

        if (saveValue){
            this.notifyWebSocketListeners(newValue.getValue().toString());
            getPointValueCache().savePointValueIntoDaoAndCacheUpdate(newValue, source, logValue, async);
        }


        // Ignore historical values.
        if (oldValue == null || newValue.getTime() >= oldValue.getTime()) {
            fireEvents(oldValue, newValue, source != null, false);
        } else
            fireEvents(null, newValue, false, true);
    }

    @Override
    public PointValueTime getPointValue() {
        return getOldAndSetNew(null);
    }

    private PointValueTime getOldAndSetNew(PointValueTime newValue) {
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

    private boolean updateToleranceOrigin(PointValueTime newValue, boolean updateForce) {
        if(updateForce) {
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
        final DataPointNonSyncRT other = (DataPointNonSyncRT) obj;
        return getId() == other.getId();
    }

    @Override
    public String toString() {
        return "DataPointNonSyncRT(id=" + getId() + ", name=" + getVO().getName() + ")";
    }
}
