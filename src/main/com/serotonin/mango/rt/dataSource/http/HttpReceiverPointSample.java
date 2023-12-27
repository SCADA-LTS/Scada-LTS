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
package com.serotonin.mango.rt.dataSource.http;

import com.serotonin.web.taglib.DateFunctions;

/**
 * @author Matthew Lohbihler
 */
public class HttpReceiverPointSample {
    private final String key;
    private final String value;
    private final long time;

    public HttpReceiverPointSample(String key, String value, long time) {
        this.key = key;
        this.value = value;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }

    public String getPrettyTime() {
        if (time == 0)
            return null;
        return DateFunctions.getTime(time);
    }
}
