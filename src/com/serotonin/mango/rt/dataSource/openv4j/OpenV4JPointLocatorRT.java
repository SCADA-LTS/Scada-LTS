/*
 *   Mango - Open Source M2M - http://mango.serotoninsoftware.com
 *   Copyright (C) 2010 Arne Pl\u00f6se
 *   @author Arne Pl\u00f6se
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.dataSource.openv4j;

import net.sf.openv4j.DataPoint;
import com.serotonin.mango.vo.dataSource.openv4j.OpenV4JPointLocatorVO;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;

// No need to encapsulate as string like vo
public class OpenV4JPointLocatorRT extends PointLocatorRT {
    private final OpenV4JPointLocatorVO vo;
    private final DataPoint dataPoint;

    public OpenV4JPointLocatorRT(OpenV4JPointLocatorVO vo) {
        this.vo = vo;
        dataPoint = DataPoint.valueOf(vo.getDataPointName());
    }

    @Override
    public boolean isSettable() {
        return vo.isSettable();
    }

    /**
     * @return the vo
     */
    public OpenV4JPointLocatorVO getVo() {
        return vo;
    }

    /**
     * @return the dataPoint
     */
    public DataPoint getDataPoint() {
        return dataPoint;
    }
}
