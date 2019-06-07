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
package com.serotonin.mango.web.comparators;

import java.util.ResourceBundle;

import com.serotonin.mango.vo.DataPointVO;

public class DataPointComparator extends BaseComparator<DataPointVO> {
    private static final int SORT_NAME = 1;
    private static final int SORT_DS_NAME = 2;
    private static final int SORT_ENABLED = 4;
    private static final int SORT_DATA_TYPE = 5;
    private static final int SORT_CONFIG = 6;

    private final ResourceBundle bundle;

    public DataPointComparator(ResourceBundle bundle, String sortField, boolean descending) {
        this.bundle = bundle;

        if ("name".equals(sortField))
            sortType = SORT_NAME;
        else if ("dsName".equals(sortField))
            sortType = SORT_DS_NAME;
        else if ("enabled".equals(sortField))
            sortType = SORT_ENABLED;
        else if ("dataType".equals(sortField))
            sortType = SORT_DATA_TYPE;
        else if ("config".equals(sortField))
            sortType = SORT_CONFIG;
        this.descending = descending;
    }

    public int compare(DataPointVO dp1, DataPointVO dp2) {
        int result = 0;
        if (sortType == SORT_NAME)
            result = dp1.getName().compareTo(dp2.getName());
        else if (sortType == SORT_DS_NAME)
            result = dp1.getDataSourceName().compareTo(dp2.getDataSourceName());
        else if (sortType == SORT_ENABLED)
            result = new Boolean(dp1.isEnabled()).compareTo(new Boolean(dp2.isEnabled()));
        else if (sortType == SORT_DATA_TYPE) {
            String s1 = dp1.getDataTypeMessage().getLocalizedMessage(bundle);
            String s2 = dp2.getDataTypeMessage().getLocalizedMessage(bundle);
            result = s1.compareTo(s2);
        }
        else if (sortType == SORT_CONFIG) {
            String s1 = dp1.getConfigurationDescription().getLocalizedMessage(bundle);
            String s2 = dp2.getConfigurationDescription().getLocalizedMessage(bundle);
            result = s1.compareTo(s2);
        }

        if (descending)
            return -result;
        return result;
    }
}
