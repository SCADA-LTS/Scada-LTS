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
package com.serotonin.mango.web.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.cache.ObjectCreator;
import com.serotonin.cache.ThreadSafeCache;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.SystemSettingsDao;
import com.serotonin.mango.rt.dataSource.http.HttpReceiverData;
import com.serotonin.mango.rt.dataSource.http.HttpReceiverMulticaster;
import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 */
public class HttpDataSourceServlet extends HttpServlet {
    private static final String DEVICE_ID_KEY = "__device";
    public static final String TIME_OVERRIDE_KEY = "__time";

    public static final ThreadSafeCache<SimpleDateFormat> BASIC_SDF_CACHE = new ThreadSafeCache<SimpleDateFormat>(
            new ObjectCreator<SimpleDateFormat>() {
                public SimpleDateFormat create() {
                    return new SimpleDateFormat("yyyyMMddHHmmss");
                }
            });

    public static final ThreadSafeCache<SimpleDateFormat> TZ_SDF_CACHE = new ThreadSafeCache<SimpleDateFormat>(
            new ObjectCreator<SimpleDateFormat>() {
                public SimpleDateFormat create() {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return sdf;
                }
            });

    private static final String GROUPED_PARAM_KEY_PREFIX = "__point";
    private static final String GROUPED_PARAM_VALUE_PREFIX = "__value";
    private static final String GROUPED_PARAM_TIME_PREFIX = "__time";

    private static final long serialVersionUID = -1;

    private HttpReceiverMulticaster multicaster;

    @Override
    public void init() {
        multicaster = Common.ctx.getHttpReceiverMulticaster();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doImpl(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doImpl(request, response);
    }

    @SuppressWarnings("unchecked")
    private void doImpl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> messages = new LinkedList<String>();

        HttpReceiverData data = new HttpReceiverData();
        data.setRemoteIp(request.getRemoteAddr());

        Enumeration<String> paramNames = request.getParameterNames();
        String key, value;
        while (paramNames.hasMoreElements()) {
            key = paramNames.nextElement();
            value = request.getParameter(key);

            if (TIME_OVERRIDE_KEY.equals(key)) {
                long ts = stringToTime(value);
                if (ts == 0)
                    messages.add("Time override parse error");
                else
                    data.setTime(ts);
            }
            else if (DEVICE_ID_KEY.equals(key))
                data.setDeviceId(value);
            else if (key.startsWith(GROUPED_PARAM_KEY_PREFIX)) {
                // A grouped parameter value.
                String groupId = key.substring(GROUPED_PARAM_KEY_PREFIX.length());

                // There may be multiple values, so get the array version.
                String[] pointNames = request.getParameterValues(key);
                String[] pointValues = request.getParameterValues(GROUPED_PARAM_VALUE_PREFIX + groupId);
                String[] pointTimes = request.getParameterValues(GROUPED_PARAM_TIME_PREFIX + groupId);

                for (int i = 0; i < pointNames.length; i++) {
                    if (pointValues == null || pointValues.length <= i)
                        messages.add("Value not found for grouped point key " + key + ", name=" + pointNames[i]);
                    else {
                        String time = null;
                        if (pointTimes != null && pointTimes.length > i)
                            time = pointTimes[i];
                        addData(data, pointNames[i], pointValues[i], time);
                    }
                }
            }
            else if (key.startsWith(GROUPED_PARAM_VALUE_PREFIX)) {
                // Ignore these. Presumably read in the above clause.
            }
            else if (key.startsWith(GROUPED_PARAM_TIME_PREFIX)) {
                // Ignore these. Presumably read in the above clause.
            }
            else {
                // Must be a single parameter value. There may be multiple values, so get the array version.
                for (String avalue : request.getParameterValues(key))
                    addData(data, key, avalue, null);
            }
        }

        if (data.getTime() == -1)
            data.setTime(System.currentTimeMillis());

        multicaster.multicast(data);

        // Tell the requestor what fields were not used.
        for (String unconsumed : data.getUnconsumedKeys())
            messages.add("Unconsumed key: " + unconsumed);

        // Write the prologue
        response.getWriter().write(SystemSettingsDao.getValue(SystemSettingsDao.HTTPDS_EPILOGUE));

        for (String message : messages) {
            response.getWriter().write(message);
            response.getWriter().write("\r\n");
        }

        // Write the epilogue
        response.getWriter().write(SystemSettingsDao.getValue(SystemSettingsDao.HTTPDS_EPILOGUE));
    }

    private void addData(HttpReceiverData data, String name, String value, String time) {
        long timestamp = 0;
        int atpos = value.lastIndexOf('@');
        if (atpos != -1) {
            // Maybe found a time portion.
            timestamp = stringToTime(value.substring(atpos + 1));
            if (timestamp != 0)
                value = value.substring(0, atpos);
        }

        if (timestamp == 0 && time != null)
            // Check the given date.
            timestamp = stringToTime(time);

        data.addData(name, value, timestamp);
    }

    private long stringToTime(String s) {
        if (StringUtils.isEmpty(s))
            return 0;

        try {
            // Try a basic format.
            return BASIC_SDF_CACHE.getObject().parse(s).getTime();
        }
        catch (ParseException e) {
            // Try the TZ format.
            try {
                return TZ_SDF_CACHE.getObject().parse(s).getTime();
            }
            catch (ParseException e1) {
                // Try UTC
                try {
                    return Long.parseLong(s);
                }
                catch (NumberFormatException e2) {
                    return -1;
                }
            }
        }
    }
}
