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
package com.serotonin.mango.rt.dataSource.internal;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.vo.dataSource.internal.InternalDataSourceVO;
import com.serotonin.mango.vo.dataSource.internal.InternalPointLocatorVO;
import com.serotonin.monitor.IntegerMonitor;

/**
 * @author Matthew Lohbihler
 */
public class InternalDataSourceRT extends PollingDataSource {
    public InternalDataSourceRT(InternalDataSourceVO vo) {
        super(vo);
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    public void doPoll(long time) {
        for (DataPointRT dataPoint : dataPoints) {
            InternalPointLocatorRT locator = dataPoint.getPointLocator();

            String monitorId = InternalPointLocatorVO.MONITOR_NAMES[locator.getPointLocatorVO().getAttributeId()];
            // They are all integer monitors so far, so this is fine.
            IntegerMonitor m = (IntegerMonitor) Common.MONITORED_VALUES.getValueMonitor(monitorId);
            if (m != null)
                dataPoint.updatePointValue(new PointValueTime((double) m.getValue(), time));
        }
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        // no op
    }
}
