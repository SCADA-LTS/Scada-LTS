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

import com.serotonin.mango.rt.event.EventInstance;

public class EventInstanceComparator extends BaseComparator<EventInstance> {
    private static final int SORT_ALARM_LEVEL = 1;
    private static final int SORT_ACTIVE_TIME = 2;
    private static final int SORT_MESSAGE = 3;
    private static final int SORT_ID = 4;
    private static final int SORT_RTN_TIME = 5;

    private final ResourceBundle bundle;

    public EventInstanceComparator(ResourceBundle bundle, String sortField, boolean descending) {
        this.bundle = bundle;

        if ("alarmLevel".equals(sortField))
            sortType = SORT_ALARM_LEVEL;
        else if ("time".equals(sortField))
            sortType = SORT_ACTIVE_TIME;
        else if ("msg".equals(sortField))
            sortType = SORT_MESSAGE;
        else if ("id".equals(sortField))
            sortType = SORT_ID;
        else if ("rtntime".equals(sortField))
            sortType = SORT_RTN_TIME;
        this.descending = descending;
    }

    public int compare(EventInstance e1, EventInstance e2) {
        int result = 0;
        if (sortType == SORT_ALARM_LEVEL)
            result = e1.getAlarmLevel() - e2.getAlarmLevel();
        else if (sortType == SORT_ACTIVE_TIME) {
            long diff = e1.getActiveTimestamp() - e2.getActiveTimestamp();
            if (diff < 0)
                result = -1;
            else if (diff > 0)
                result = 1;
        }
        else if (sortType == SORT_MESSAGE) {
            String s1 = e1.getMessage().getLocalizedMessage(bundle);
            String s2 = e2.getMessage().getLocalizedMessage(bundle);
            result = s1.compareTo(s2);
        }
        else if (sortType == SORT_ID)
            result = e1.getId() - e2.getId();
        else if (sortType == SORT_RTN_TIME) {
            long diff = e1.getRtnTimestamp() - e2.getRtnTimestamp();
            if (diff < 0)
                result = -1;
            else if (diff > 0)
                result = 1;
        }

        if (descending)
            return -result;
        return result;
    }

}
