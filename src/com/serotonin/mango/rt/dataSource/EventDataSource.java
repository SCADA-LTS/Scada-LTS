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
package com.serotonin.mango.rt.dataSource;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.vo.dataSource.DataSourceVO;

abstract public class EventDataSource extends DataSourceRT {
    protected List<DataPointRT> dataPoints = new ArrayList<DataPointRT>();

    public EventDataSource(DataSourceVO<?> vo) {
        super(vo);
    }

    @Override
    public void addDataPoint(DataPointRT dataPoint) {
        synchronized (pointListChangeLock) {
            // Remove any existing instances of the points.
            dataPoints.remove(dataPoint);
            dataPoints.add(dataPoint);
        }
    }

    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        synchronized (pointListChangeLock) {
            dataPoints.remove(dataPoint);
        }
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        // Typically, event based data sources cannot set point values, so don't make subclasses implement this.
    }
}
