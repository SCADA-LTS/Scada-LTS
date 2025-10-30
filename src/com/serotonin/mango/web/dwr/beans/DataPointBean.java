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
package com.serotonin.mango.web.dwr.beans;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataPointSaveHandler;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.web.security.EmptyInstance;

import java.util.List;

public class DataPointBean implements EmptyInstance {
    private int id;
    private String xid;
    private String name;
    private boolean settable;
    private int dataType;
    private final LocalizableMessage dataTypeMessage;
    private final String chartColour;

    public DataPointBean(DataPointVO vo) {
        id = vo.getId();
        name = vo.getExtendedName();
        settable = vo.getPointLocator().isSettable();
        dataType = vo.getPointLocator().getDataTypeId();
        dataTypeMessage = vo.getDataTypeMessage();
        chartColour = vo.getChartColour();
        xid = vo.getXid();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSettable() {
        return settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public LocalizableMessage getDataTypeMessage() {
        return dataTypeMessage;
    }

    public String getChartColour() {
        return chartColour;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    @Override
    public DataPointBean newInstanceEmpty() {
        DataPointVO dataPointVO = new DataPointVO(-1, -1, -1);
        dataPointVO.setPointLocator(new PointLocatorVO() {
            @Override
            public int getDataTypeId() {
                return 0;
            }

            @Override
            public LocalizableMessage getDataTypeMessage() {
                return null;
            }

            @Override
            public LocalizableMessage getConfigurationDescription() {
                return null;
            }

            @Override
            public boolean isSettable() {
                return false;
            }

            @Override
            public boolean isRelinquishable() {
                return false;
            }

            @Override
            public PointLocatorRT createRuntime() {
                return null;
            }

            @Override
            public void validate(DwrResponseI18n response) {

            }

            @Override
            public DataPointSaveHandler getDataPointSaveHandler() {
                return null;
            }

            @Override
            public void addProperties(List<LocalizableMessage> list) {

            }

            @Override
            public void addPropertyChanges(List<LocalizableMessage> list, Object o) {

            }
        });
        return new DataPointBean(dataPointVO);
    }
}
