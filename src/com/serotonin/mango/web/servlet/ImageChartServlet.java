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

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import com.serotonin.InvalidArgumentException;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.dataImage.PointValueFacade;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.report.DiscreteTimeSeries;
import com.serotonin.mango.vo.report.ImageChartUtils;
import com.serotonin.mango.vo.report.PointTimeSeriesCollection;
import com.serotonin.util.ColorUtils;
import com.serotonin.util.StringUtils;

public class ImageChartServlet extends BaseInfoServlet {
    private static final long serialVersionUID = -1;
    private static final long CACHE_PURGE_INTERVAL = 1000 * 60 * 10; // 10 minutes

    private long lastCachePurgeTime = 0;
    private final Map<String, CacheElement> cachedImages = new ConcurrentHashMap<String, CacheElement>();

    /**
     * @TODO(security): Validate the point access against the user. If anonymous, make sure the view allows public
     *                  access to the point. (Need to add view id.)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String imageInfo = request.getPathInfo();

        CacheElement ce = cachedImages.get(imageInfo);
        byte[] data;
        if (ce == null) {
            data = getImageData(imageInfo, request);
            if (data == null)
                return;
            cachedImages.put(imageInfo, new CacheElement(data));
        }
        else
            data = ce.getData();

        ImageChartUtils.writeChart(response, data);

        tryCachePurge();
    }

    private byte[] getImageData(String imageInfo, HttpServletRequest request) throws IOException {
        // The imageInfo contains the timestamp of the last point value, the data point id, and the duration of the
        // chart. The intention is to create a name for the virtual image such that the browser will cache the data
        // and only come here when the data has change. The format of the name is:
        // /{last timestamp}_{duration}_{data point id[|colour]}[_{data point id[|colour]} ...].png
        //
        // From-to charts can also be requested. They are distinguishable by their starting "ft_". Complete format is:
        // /ft_{last timestamp}_{from millis}_{to millis}_{data point id[|colour]}[_{data point id[|colour]} ...].png
        //
        // Width and height can also be added to the image name in case they change dynamically. Add them to the data
        // point id list but prepend them with w and h: "_w500_h250.png"
        //
        // Hex colour definitions need to be prefixed with '0x' instead of '#'.
        try {
            // Remove the / and the .png
            imageInfo = imageInfo.substring(1, imageInfo.length() - 4);

            // Split by underscore.
            String[] imageBits = imageInfo.split("_");

            // Get the data.
            long from, to;
            int pointIdStart;
            if (imageBits[0].equals("ft")) {
                from = Long.parseLong(imageBits[2]);
                to = Long.parseLong(imageBits[3]);
                pointIdStart = 4;
            }
            else {
                from = System.currentTimeMillis() - Long.parseLong(imageBits[1]);
                to = -1;
                pointIdStart = 2;
            }

            int width = getIntRequestParameter(request, "w", 200);
            int height = getIntRequestParameter(request, "h", 100);

            // Create the datasets
            PointTimeSeriesCollection ptsc = new PointTimeSeriesCollection();
            for (int i = pointIdStart; i < imageBits.length; i++) {
                if (imageBits[i].startsWith("w"))
                    width = StringUtils.parseInt(imageBits[i].substring(1), width);
                else if (imageBits[i].startsWith("h"))
                    height = StringUtils.parseInt(imageBits[i].substring(1), height);
                else {
                    String dataPointStr = imageBits[i];
                    Color colour = null;
                    int dataPointId;

                    int pipe = dataPointStr.indexOf('|');
                    if (pipe == -1)
                        dataPointId = Integer.parseInt(dataPointStr);
                    else {
                        try {
                            String colourStr = dataPointStr.substring(pipe + 1);
                            if (colourStr.startsWith("0x"))
                                colourStr = "#" + colourStr.substring(2);
                            colour = ColorUtils.toColor(colourStr);
                        }
                        catch (InvalidArgumentException e) {
                            throw new IOException(e);
                        }
                        dataPointId = Integer.parseInt(dataPointStr.substring(0, pipe));
                    }

                    // Get the data.
                    PointValueFacade pointValueFacade = new PointValueFacade(dataPointId);
                    List<PointValueTime> data;
                    if (from == -1 && to == -1)
                        data = pointValueFacade.getPointValues(0);
                    else if (from == -1)
                        data = pointValueFacade.getPointValuesBetween(0, to);
                    else if (to == -1)
                        data = pointValueFacade.getPointValues(from);
                    else
                        data = pointValueFacade.getPointValuesBetween(from, to);

                    DataPointVO dp = new DataPointDao().getDataPoint(dataPointId);
                    if (dp == null || dp.getName() == null)
                        ; // no op
                    else if (dp.getPointLocator().getDataTypeId() == DataTypes.NUMERIC) {
                        TimeSeries ts = new TimeSeries(dp.getName(), null, null, Second.class);
                        for (PointValueTime pv : data)
                            ImageChartUtils.addSecond(ts, pv.getTime(), pv.getValue().numberValue());
                        ptsc.addNumericTimeSeries(ts, colour);
                    }
                    else {
                        DiscreteTimeSeries ts = new DiscreteTimeSeries(dp.getName(), dp.getTextRenderer(), colour);
                        for (PointValueTime pv : data)
                            ts.addValueTime(pv);
                        ptsc.addDiscreteTimeSeries(ts);
                    }
                }
            }

            return ImageChartUtils.getChartData(ptsc, width, height);
        }
        catch (StringIndexOutOfBoundsException e) {
            // no op
        }
        catch (NumberFormatException e) {
            // no op
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // no op
        }

        return null;
    }

    private void tryCachePurge() {
        long now = System.currentTimeMillis();
        if (lastCachePurgeTime + CACHE_PURGE_INTERVAL < now) {
            List<String> toBePurged = new ArrayList<String>();
            for (String key : cachedImages.keySet()) {
                CacheElement ce = cachedImages.get(key);
                if (ce.getLastAccessTime() + CACHE_PURGE_INTERVAL < now)
                    toBePurged.add(key);
            }

            for (String key : toBePurged)
                cachedImages.remove(key);

            lastCachePurgeTime = System.currentTimeMillis();
        }
    }

    class CacheElement {
        private long lastAccessTime;
        private final byte[] data;

        CacheElement(byte[] data) {
            this.data = data;
            lastAccessTime = System.currentTimeMillis();
        }

        byte[] getData() {
            lastAccessTime = System.currentTimeMillis();
            return data;
        }

        long getLastAccessTime() {
            return lastAccessTime;
        }
    }
}
