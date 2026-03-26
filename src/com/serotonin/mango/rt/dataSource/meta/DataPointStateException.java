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
package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.web.mvc.controller.ScadaLocaleUtils;
import com.serotonin.web.i18n.LocalizableException;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Matthew Lohbihler
 */
public class DataPointStateException extends LocalizableException {
    static final long serialVersionUID = -1;

    private final int dataPointId;
    private final String dataPointXid;
    private final String dataPointName;
    private final ResourceBundle resourceBundle;

    public DataPointStateException(int dataPointId, LocalizableMessage message, ResourceBundle resourceBundle) {
        super(message);
        this.dataPointId = dataPointId;
        this.dataPointXid = "";
        this.dataPointName = "";
        this.resourceBundle = resourceBundle;
    }

    public DataPointStateException(int dataPointId, String dataPointXid, String dataPointName,
                                   LocalizableMessage message, ResourceBundle resourceBundle) {
        super(message);
        this.dataPointId = dataPointId;
        this.dataPointXid = dataPointXid;
        this.dataPointName = dataPointName;
        this.resourceBundle = resourceBundle;
    }

    public int getDataPointId() {
        return dataPointId;
    }

    public String getDataPointXid() {
        return dataPointXid;
    }

    public String getDataPointName() {
        return dataPointName;
    }

    @Override
    public String getMessage() {
        Locale en = Locale.ENGLISH;
        ResourceBundle resourceBundleEn = ScadaLocaleUtils.getResourceBundleByLocale(en);
        return super.getLocalizableMessage().getLocalizedMessage(resourceBundleEn);
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizableMessage().getLocalizedMessage(resourceBundle);
    }

    public static DataPointStateException newInstance(IntValuePair contextEntry, LocalizableMessage message, DataPointRT point, ResourceBundle resourceBundle) {
        if(point == null || point.getVO() == null) {
            return new DataPointStateException(contextEntry.getKey(), message, resourceBundle);
        }
        DataPointVO dataPoint = point.getVO();
        return new DataPointStateException(contextEntry.getKey(), dataPoint.getXid(), dataPoint.getExtendedName(), message, resourceBundle);
    }
}
