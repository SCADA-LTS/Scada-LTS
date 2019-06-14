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
package com.serotonin.mango.vo.dataSource;

import java.io.Serializable;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.util.ChangeComparableObject;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

public interface PointLocatorVO extends Serializable, ChangeComparableObject {
    /**
     * One of the com.serotonin.mango.DataTypes
     */
    public int getDataTypeId();

    /**
     * The text representation of the data type
     */
    public LocalizableMessage getDataTypeMessage();

    /**
     * An arbitrary description of the point location configuration for human consumption.
     */
    public LocalizableMessage getConfigurationDescription();

    /**
     * Can the value be set in the data source?
     */
    public boolean isSettable();

    /**
     * Supplemental to being settable, can the set value be relinquished?
     */
    public boolean isRelinquishable();

    /**
     * Create a runtime version of the locator
     */
    public PointLocatorRT createRuntime();

    /**
     * Validate. What else?
     */
    public void validate(DwrResponseI18n response);

    public DataPointSaveHandler getDataPointSaveHandler();
}
