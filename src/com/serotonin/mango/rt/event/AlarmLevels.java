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
package com.serotonin.mango.rt.event;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.web.i18n.LocalizableMessage;

public class AlarmLevels {
    public static final int NONE = 0;
    public static final int INFORMATION = 1;
    public static final int URGENT = 2;
    public static final int CRITICAL = 3;
    public static final int LIFE_SAFETY = 4;

    public static final String NONE_DESCRIPTION = "common.alarmLevel.none";
    public static final String INFORMATION_DESCRIPTION = "common.alarmLevel.info";
    public static final String URGENT_DESCRIPTION = "common.alarmLevel.urgent";
    public static final String CRITICAL_DESCRIPTION = "common.alarmLevel.critical";
    public static final String LIFE_SAFETY_DESCRIPTION = "common.alarmLevel.lifeSafety";

    public static final ExportCodes CODES = new ExportCodes();
    static {
        CODES.addElement(NONE, "NONE");
        CODES.addElement(INFORMATION, "INFORMATION");
        CODES.addElement(URGENT, "URGENT");
        CODES.addElement(CRITICAL, "CRITICAL");
        CODES.addElement(LIFE_SAFETY, "LIFE_SAFETY");
    }

    public static String getAlarmLevelDescription(int alarmLevel) {
        switch (alarmLevel) {
        case NONE:
            return NONE_DESCRIPTION;
        case INFORMATION:
            return INFORMATION_DESCRIPTION;
        case URGENT:
            return URGENT_DESCRIPTION;
        case CRITICAL:
            return CRITICAL_DESCRIPTION;
        case LIFE_SAFETY:
            return LIFE_SAFETY_DESCRIPTION;
        }
        throw new ShouldNeverHappenException("(unknown level " + alarmLevel + ")");
    }

    public static LocalizableMessage getAlarmLevelMessage(int alarmLevel) {
        return new LocalizableMessage(getAlarmLevelDescription(alarmLevel));
    }
}
