/*
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc. All rights reserved.
    @author Matthew Lohbihler
 */
package com.serotonin.mango.web.servlet;

import com.serotonin.InvalidArgumentException;
import com.serotonin.db.MappedRowCallback;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.stats.*;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.mango.vo.report.DiscreteTimeSeries;
import com.serotonin.mango.vo.report.ImageChartUtils;
import com.serotonin.mango.vo.report.PointTimeSeriesCollection;
import com.serotonin.mango.vo.report.SeriesIdentifier;
import com.serotonin.sync.Synchronizer;
import com.serotonin.util.StringUtils;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.utils.ColorUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AsyncImageChartServlet extends BaseInfoServlet {
    private static final long serialVersionUID = -1;

    private final DataPointService dataPointService = new DataPointService();
    private final PointValueService pointValueService = new PointValueService();

    /**
     * @TODO(security): Validate the point access against the user. If anonymous, make sure the view allows public
     *                  access to the point. (Need to add view id.)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String imageInfo = request.getPathInfo();
        byte[] data = getImageData(imageInfo, request);
        if (data != null)
            ImageChartUtils.writeChart(response, data);
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
            Synchronizer<PointDataRetriever> tasks = new Synchronizer<PointDataRetriever>();
            List<Integer> dataPointIds = new ArrayList<Integer>();
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

                    dataPointIds.add(dataPointId);
                    PointDataRetriever pdr = new PointDataRetriever(dataPointId, colour, width * 10,
                            dataPointService, pointValueService);
                    tasks.addTask(pdr);
                }
            }

            if (tasks.getSize() == 0)
                return null;

            if (from == -1 && to == -1) {
                LongPair sae = pointValueService.getStartAndEndTime(dataPointIds);
                from = sae.getL1();
                to = sae.getL2();
            }
            else if (from == -1)
                from = pointValueService.getStartTime(dataPointIds);
            else if (to == -1)
                to = pointValueService.getEndTime(dataPointIds);

            for (PointDataRetriever pdr : tasks.getTasks())
                pdr.setRange(from, to);

            tasks.executeAndWait(Common.timer.getExecutorService());

            PointTimeSeriesCollection ptsc = new PointTimeSeriesCollection();
            for (PointDataRetriever pdr : tasks.getTasks())
                pdr.addToCollection(ptsc);

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

    static class PointDataRetriever implements Runnable, MappedRowCallback<PointValueTime>, DataQuantizerCallback {
        private final int dataPointId;
        private Color colour;
        private final int imageWidth;
        private long from;
        private long to;
        private TimeSeries ts;
        private AbstractDataQuantizer quantizer;
        private DiscreteTimeSeries dts;
        private final DataPointService dataPointService;
        private final PointValueService pointValueService;

        public PointDataRetriever(int dataPointId, Color colour, int imageWidth, DataPointService dataPointService,
                                  PointValueService pointValueService) {
            this.dataPointId = dataPointId;
            this.colour = colour;
            this.imageWidth = imageWidth;
            this.dataPointService = dataPointService;
            this.pointValueService = pointValueService;
        }

        public void setRange(long from, long to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            DataPointVO dp = dataPointService.getDataPoint(dataPointId);
            try {
                if (colour == null && !StringUtils.isEmpty(dp.getChartColour()))
                    colour = ColorUtils.toColor(dp.getChartColour());
            }
            catch (InvalidArgumentException e) {
                // no op
            }

            int dataType = dp.getPointLocator().getDataTypeId();
            SeriesIdentifier seriesIdentifier = new SeriesIdentifier(dp.getId(), dp.getExtendedName());

            if (dataType == DataTypes.NUMERIC) {
                ts = new TimeSeries(seriesIdentifier, null, null, Second.class);
                quantizer = new NumericDataQuantizer(from, to, imageWidth, this);
                loadData(quantizer, pointValueService, dataPointId, from, to);
            }
            else if (dataType == DataTypes.MULTISTATE) {
                quantizer = new MultistateDataQuantizer(from, to, imageWidth, this);
                dts = new DiscreteTimeSeries(seriesIdentifier, dp.getTextRenderer(), colour);
                loadData(quantizer, pointValueService, dataPointId, from, to);
            }
            else if (dataType == DataTypes.BINARY) {
                quantizer = new BinaryDataQuantizer(from, to, imageWidth, this);
                dts = new DiscreteTimeSeries(seriesIdentifier, dp.getTextRenderer(), colour);
                loadData(quantizer, pointValueService, dataPointId, from, to);
            }
        }

        @Override
        public void row(PointValueTime pvt, int rowNum) {
            if (quantizer != null)
                quantizer.data(pvt.getValue(), pvt.getTime());
        }

        @Override
        public void quantizedData(MangoValue value, long time) {
            if (ts != null)
                ImageChartUtils.addSecond(ts, time, MangoValue.numberValue(value));
            else if (dts != null)
                dts.addValueTime(new PointValueTime(value, time));
        }

        public void addToCollection(PointTimeSeriesCollection ptsc) {
            if (ts != null)
                ptsc.addNumericTimeSeries(ts, colour);
            else
                ptsc.addDiscreteTimeSeries(dts);
        }

        private static void loadData(AbstractDataQuantizer quantizer, PointValueService pointValueService,
                                     int dataPointId, long from, long to) {
            List<PointValueTime> data = pointValueService.getPointValuesBetween(dataPointId, from, to);
            for (PointValueTime pv : data) {
                quantizer.data(pv.getValue(), pv.getTime());
            }
        }
    }
}
