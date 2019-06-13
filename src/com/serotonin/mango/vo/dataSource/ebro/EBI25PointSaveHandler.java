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
package com.serotonin.mango.vo.dataSource.ebro;

import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.rt.dataSource.ebro.EBI25Constants;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataPointSaveHandler;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.web.dwr.beans.EBI25InterfaceUpdater;

/**
 * @author Matthew Lohbihler
 */
public class EBI25PointSaveHandler implements DataPointSaveHandler {
    @Override
    public void handleSave(DataPointVO point) {
        // The limit point event detectors may have changed. Ensure that the locator limits and the values on the
        // device all match.
        EBI25DataSourceVO ds = (EBI25DataSourceVO) new DataSourceDao().getDataSource(point.getDataSourceId());
        EBI25PointLocatorVO locator = point.getPointLocator();

        PointEventDetectorVO ped;
        ped = EBI25Constants.findDetector(point.getEventDetectors(), true);
        if (ped != null)
            locator.setHighLimit(ped.getLimit());

        ped = EBI25Constants.findDetector(point.getEventDetectors(), false);
        if (ped != null)
            locator.setLowLimit(ped.getLimit());

        EBI25InterfaceUpdater updater = new EBI25InterfaceUpdater();
        updater.updateLogger(ds.getHost(), ds.getPort(), ds.getTimeout(), ds.getRetries(), locator);
    }
}
