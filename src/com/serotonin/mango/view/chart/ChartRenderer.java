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
package com.serotonin.mango.view.chart;

import java.io.Serializable;
import java.util.Map;

import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.vo.DataPointVO;

public interface ChartRenderer extends Serializable {
    public static final int TYPE_NONE = 1;
    public static final int TYPE_TABLE = 2;
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_STATS = 4;

    public String getTypeName();

    public void addDataToModel(Map<String, Object> model, DataPointVO point);

    public ImplDefinition getDef();
}
