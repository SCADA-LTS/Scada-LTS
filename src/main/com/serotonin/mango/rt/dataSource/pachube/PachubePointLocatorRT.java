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
package com.serotonin.mango.rt.dataSource.pachube;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.dataSource.pachube.PachubePointLocatorVO;

public class PachubePointLocatorRT extends PointLocatorRT {
    private final int feedId;
    private final String dataStreamId;
    private final int dataTypeId;
    private final String binary0Value;
    private final boolean settable;

    public PachubePointLocatorRT(PachubePointLocatorVO vo) {
        feedId = vo.getFeedId();
        dataStreamId = vo.getDataStreamId();
        dataTypeId = vo.getDataTypeId();
        binary0Value = vo.getBinary0Value();
        settable = vo.isSettable();
    }

    public int getFeedId() {
        return feedId;
    }

    public String getDataStreamId() {
        return dataStreamId;
    }

    @Override
    public boolean isSettable() {
        return settable;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public String getBinary0Value() {
        return binary0Value;
    }
}
