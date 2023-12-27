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

import java.util.ArrayList;
import java.util.List;

import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 */
public class HttpReceiverData {
    private String remoteIp;
    private String deviceId;
    private long time = -1;
    private final List<HttpReceiverPointSample> data = new ArrayList<HttpReceiverPointSample>();
    private final List<String> unconsumedKeys = new ArrayList<String>();

    public List<HttpReceiverPointSample> getData() {
        return data;
    }

    public void addData(String key, String value, long time) {
        // Protect against XSS attacks.
        value = StringUtils.escapeLT(value);
        data.add(new HttpReceiverPointSample(key, value, time));
        unconsumedKeys.add(key);
    }

    public void consume(String key) {
        unconsumedKeys.remove(key);
    }

    public List<String> getUnconsumedKeys() {
        return unconsumedKeys;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
