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
package com.serotonin.mango.vo.report;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import com.serotonin.InvalidArgumentException;
import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.ReportDao;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.view.stats.AbstractDataQuantizer;
import com.serotonin.mango.view.stats.AnalogStatistics;
import com.serotonin.mango.view.stats.BinaryDataQuantizer;
import com.serotonin.mango.view.stats.DataQuantizerCallback;
import com.serotonin.mango.view.stats.MultistateDataQuantizer;
import com.serotonin.mango.view.stats.NumericDataQuantizer;
import com.serotonin.mango.view.stats.StartsAndRuntime;
import com.serotonin.mango.view.stats.StartsAndRuntimeList;
import com.serotonin.mango.view.stats.StatisticsGenerator;
import com.serotonin.mango.view.stats.ValueChangeCounter;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.web.email.MessageFormatDirective;
import com.serotonin.mango.web.email.UsedImagesDirective;
import com.serotonin.util.ColorUtils;
import com.serotonin.web.taglib.DateFunctions;

import freemarker.template.Template;

/**
 * @author Matthew Lohbihler
 */
public class ReportChartCreator {
    static final Log LOG = LogFactory.getLog(ReportChartCreator.class);

    private static final String IMAGE_SERVLET = "reportImageChart/";
    /**
     * This image width is specifically chosen such that the report will print on a single page width in landscape.
     */
    private static final int IMAGE_WIDTH = 930;
    private static final int IMAGE_HEIGHT = 400;
    public static final String IMAGE_CONTENT_ID = "reportChart.png";

    public static final int POINT_IMAGE_WIDTH = 440;
    public static final int POINT_IMAGE_HEIGHT = 250; // 340

    String inlinePrefix;
    private String html;
    private List<String> inlineImageList;
    private byte[] imageData;
    private String chartName;
    private File exportFile;
    private File eventFile;
    private File commentFile;
    private List<PointStatistics> pointStatistics;

    final ResourceBundle bundle;

    public ReportChartCreator(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Uses the given parameters to create the data for the fields of this class. Once the content has been created the
     * getters for the fields can be used to retrieve.
     * 
     * @param reportInstance
     * @param reportDao
     * @param inlinePrefix
     *            if this is non-null, it implies that the content should be inline.
     * @param createExportFile
     */
    public void createContent(ReportInstance reportInstance, ReportDao reportDao, String inlinePrefix,
            boolean createExportFile) {
        this.inlinePrefix = inlinePrefix;

        reportInstance.setBundle(bundle);

        // Use a stream handler to get the report data from the database.
        StreamHandler handler = new StreamHandler(reportInstance.getReportStartTime(),
                reportInstance.getReportEndTime(), IMAGE_WIDTH, createExportFile, bundle);
        // Process the report content with the handler.
        reportDao.reportInstanceData(reportInstance.getId(), handler);

        pointStatistics = handler.getPointStatistics();
        UsedImagesDirective inlineImages = new UsedImagesDirective();

        // Prepare the model for the content rendering.
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("fmt", new MessageFormatDirective(bundle));
        model.put("img", inlineImages);
        model.put("instance", reportInstance);
        model.put("points", pointStatistics);
        model.put("inline", inlinePrefix == null ? "" : "cid:");

        model.put("ALPHANUMERIC", DataTypes.ALPHANUMERIC);
        model.put("BINARY", DataTypes.BINARY);
        model.put("MULTISTATE", DataTypes.MULTISTATE);
        model.put("NUMERIC", DataTypes.NUMERIC);
        model.put("IMAGE", DataTypes.IMAGE);

        // Create the individual point charts
        for (PointStatistics pointStat : pointStatistics) {
            PointTimeSeriesCollection ptsc = new PointTimeSeriesCollection();

            if (pointStat.getNumericTimeSeries() != null)
                ptsc.addNumericTimeSeries(pointStat.getNumericTimeSeries(), pointStat.getNumericTimeSeriesColor());
            else if (pointStat.getDiscreteTimeSeries() != null)
                ptsc.addDiscreteTimeSeries(pointStat.getDiscreteTimeSeries());

            if (ptsc.hasData()) {
                if (inlinePrefix != null)
                    model.put("chartName", inlinePrefix + pointStat.getChartName());
                pointStat.setImageData(ImageChartUtils.getChartData(ptsc, POINT_IMAGE_WIDTH, POINT_IMAGE_HEIGHT));
            }
        }

        PointTimeSeriesCollection ptsc = handler.getPointTimeSeriesCollection();
        if (ptsc.hasData()) {
            if (inlinePrefix != null)
                model.put("chartName", inlinePrefix + IMAGE_CONTENT_ID);
            else {
                chartName = "r" + reportInstance.getId() + ".png";
                // The path comes from the servlet path definition in web.xml.
                model.put("chartName", IMAGE_SERVLET + chartName);
            }

            imageData = ImageChartUtils.getChartData(ptsc, true, IMAGE_WIDTH, IMAGE_HEIGHT);
        }

        List<EventInstance> events = null;
        if (reportInstance.getIncludeEvents() != ReportVO.EVENTS_NONE) {
            events = reportDao.getReportInstanceEvents(reportInstance.getId());
            model.put("includeEvents", true);
            model.put("events", events);
        }
        else
            model.put("includeEvents", false);

        List<ReportUserComment> comments = null;
        if (reportInstance.isIncludeUserComments()) {
            comments = reportDao.getReportInstanceUserComments(reportInstance.getId());

            // Only provide the list of point comments to the report. The event comments have already be correlated
            // into the events list.
            List<ReportUserComment> pointComments = new ArrayList<ReportUserComment>();
            for (ReportUserComment c : comments) {
                if (c.getCommentType() == UserComment.TYPE_POINT)
                    pointComments.add(c);
            }

            model.put("includeUserComments", true);
            model.put("userComments", pointComments);
        }
        else
            model.put("includeUserComments", false);

        // Create the template.
        Template template;
        try {
            template = Common.ctx.getFreemarkerConfig().getTemplate("report/reportChart.ftl");
        }
        catch (IOException e) {
            // Couldn't load the template?
            throw new ShouldNeverHappenException(e);
        }

        // Create the content from the template.
        StringWriter writer = new StringWriter();
        try {
            template.process(model, writer);
        }
        catch (Exception e) {
            // Couldn't process the template?
            throw new ShouldNeverHappenException(e);
        }

        // Save the content
        html = writer.toString();
        inlineImageList = inlineImages.getImageList();

        // Save the export file (if any)
        exportFile = handler.exportFile;

        if (createExportFile && events != null) {
            try {
                eventFile = File.createTempFile("tempEventCSV", ".csv");
                new EventCsvStreamer(new PrintWriter(new FileWriter(eventFile)), events, bundle);
            }
            catch (IOException e) {
                LOG.error("Failed to create temp event file", e);
            }
        }

        if (createExportFile && comments != null) {
            try {
                commentFile = File.createTempFile("tempCommentCSV", ".csv");
                new UserCommentCsvStreamer(new PrintWriter(new FileWriter(commentFile)), comments, bundle);
            }
            catch (IOException e) {
                LOG.error("Failed to create temp comment file", e);
            }
        }
    }

    public String getHtml() {
        return html;
    }

    public List<String> getInlineImageList() {
        return inlineImageList;
    }

    public String getChartName() {
        return chartName;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public File getExportFile() {
        return exportFile;
    }

    public File getEventFile() {
        return eventFile;
    }

    public File getCommentFile() {
        return commentFile;
    }

    public List<PointStatistics> getPointStatistics() {
        return pointStatistics;
    }

    public class PointStatistics {
        private final int reportPointId;
        private String name;
        private int dataType;
        private String dataTypeDescription;
        private String startValue;
        private TextRenderer textRenderer;
        private StatisticsGenerator stats;
        private TimeSeries numericTimeSeries;
        private Color numericTimeSeriesColor;
        private DiscreteTimeSeries discreteTimeSeries;
        private byte[] imageData;

        public PointStatistics(int reportPointId) {
            this.reportPointId = reportPointId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDataType() {
            return dataType;
        }

        public void setDataType(int dataType) {
            this.dataType = dataType;
        }

        public String getDataTypeDescription() {
            return dataTypeDescription;
        }

        public void setDataTypeDescription(String dataTypeDescription) {
            this.dataTypeDescription = dataTypeDescription;
        }

        public String getStartValue() {
            return startValue;
        }

        public void setStartValue(String startValue) {
            this.startValue = startValue;
        }

        public StatisticsGenerator getStats() {
            return stats;
        }

        public void setStats(StatisticsGenerator stats) {
            this.stats = stats;
        }

        public TextRenderer getTextRenderer() {
            return textRenderer;
        }

        public void setTextRenderer(TextRenderer textRenderer) {
            this.textRenderer = textRenderer;
        }

        public TimeSeries getNumericTimeSeries() {
            return numericTimeSeries;
        }

        public void setNumericTimeSeries(TimeSeries numericTimeSeries) {
            this.numericTimeSeries = numericTimeSeries;
        }

        public Color getNumericTimeSeriesColor() {
            return numericTimeSeriesColor;
        }

        public void setNumericTimeSeriesColor(Color numericTimeSeriesColor) {
            this.numericTimeSeriesColor = numericTimeSeriesColor;
        }

        public DiscreteTimeSeries getDiscreteTimeSeries() {
            return discreteTimeSeries;
        }

        public void setDiscreteTimeSeries(DiscreteTimeSeries discreteTimeSeries) {
            this.discreteTimeSeries = discreteTimeSeries;
        }

        public byte[] getImageData() {
            return imageData;
        }

        public void setImageData(byte[] imageData) {
            this.imageData = imageData;
        }

        public String getAnalogMinimum() {
            return textRenderer.getText(((AnalogStatistics) stats).getMinimum(), TextRenderer.HINT_FULL);
        }

        public String getAnalogMinTime() {
            return DateFunctions.getFullMinuteTime(((AnalogStatistics) stats).getMinTime());
        }

        public String getAnalogMaximum() {
            return textRenderer.getText(((AnalogStatistics) stats).getMaximum(), TextRenderer.HINT_FULL);
        }

        public String getAnalogMaxTime() {
            return DateFunctions.getFullMinuteTime(((AnalogStatistics) stats).getMaxTime());
        }

        public String getAnalogAverage() {
            return textRenderer.getText(((AnalogStatistics) stats).getAverage(), TextRenderer.HINT_FULL);
        }

        public String getAnalogSum() {
            return textRenderer.getText(((AnalogStatistics) stats).getSum(), TextRenderer.HINT_FULL);
        }

        public String getAnalogCount() {
            return Integer.toString(((AnalogStatistics) stats).getCount());
        }

        public List<StartsAndRuntimeWrapper> getStartsAndRuntimes() {
            List<StartsAndRuntime> original = ((StartsAndRuntimeList) stats).getData();
            List<StartsAndRuntimeWrapper> result = new ArrayList<StartsAndRuntimeWrapper>(original.size());
            for (StartsAndRuntime sar : original)
                result.add(new StartsAndRuntimeWrapper(sar, textRenderer));
            return result;
        }

        public String getValueChangeCount() {
            return Integer.toString(((ValueChangeCounter) stats).getChangeCount());
        }

        public boolean isChartData() {
            return numericTimeSeries != null || discreteTimeSeries != null;
        }

        public String getChartPath() {
            if (inlinePrefix != null)
                return inlinePrefix + getChartName();
            return IMAGE_SERVLET + getChartName();
        }

        public String getChartName() {
            return "reportPointChart" + reportPointId + ".png";
        }
    }

    public static class StartsAndRuntimeWrapper {
        private static DecimalFormat percFormat = new DecimalFormat("0.#%");
        private final StartsAndRuntime sar;
        private final TextRenderer textRenderer;

        public StartsAndRuntimeWrapper(StartsAndRuntime sar, TextRenderer textRenderer) {
            this.sar = sar;
            this.textRenderer = textRenderer;
        }

        public String getValue() {
            return textRenderer.getText(sar.getMangoValue(), TextRenderer.HINT_FULL);
        }

        public String getStarts() {
            return Integer.toString(sar.getStarts());
        }

        public String getRuntime() {
            return percFormat.format(sar.getProportion());
        }
    }

    class StreamHandler implements ReportDataStreamHandler, DataQuantizerCallback {
        private final long start;
        private final long end;
        private final int imageWidth;

        File exportFile;
        private ReportCsvStreamer reportCsvStreamer;

        private final List<PointStatistics> pointStatistics;
        private final PointTimeSeriesCollection pointTimeSeriesCollection;

        private PointStatistics point;
        private TimeSeries numericTimeSeries;
        private DiscreteTimeSeries discreteTimeSeries;
        private AbstractDataQuantizer quantizer;

        public StreamHandler(long start, long end, int imageWidth, boolean createExportFile, ResourceBundle bundle) {
            pointStatistics = new ArrayList<PointStatistics>();
            pointTimeSeriesCollection = new PointTimeSeriesCollection();

            this.start = start;
            this.end = end;
            this.imageWidth = imageWidth * 10;
            try {
                if (createExportFile) {
                    exportFile = File.createTempFile("tempCSV", ".csv");
                    reportCsvStreamer = new ReportCsvStreamer(new PrintWriter(new FileWriter(exportFile)), bundle);
                }
            }
            catch (IOException e) {
                LOG.error("Failed to create temp file", e);
            }
        }

        public List<PointStatistics> getPointStatistics() {
            return pointStatistics;
        }

        public PointTimeSeriesCollection getPointTimeSeriesCollection() {
            return pointTimeSeriesCollection;
        }

        public void startPoint(ReportPointInfo pointInfo) {
            donePoint();

            point = new PointStatistics(pointInfo.getReportPointId());
            point.setName(pointInfo.getExtendedName());
            point.setDataType(pointInfo.getDataType());
            point.setDataTypeDescription(DataTypes.getDataTypeMessage(pointInfo.getDataType()).getLocalizedMessage(
                    bundle));
            point.setTextRenderer(pointInfo.getTextRenderer());
            if (pointInfo.getStartValue() != null)
                point.setStartValue(pointInfo.getTextRenderer().getText(pointInfo.getStartValue(),
                        TextRenderer.HINT_FULL));
            pointStatistics.add(point);

            Color colour = null;
            try {
                if (pointInfo.getColour() != null)
                    colour = ColorUtils.toColor("#" + pointInfo.getColour());
            }
            catch (InvalidArgumentException e) {
                // Should never happen, but leave the color null in case it does.
            }

            if (pointInfo.getDataType() == DataTypes.NUMERIC) {
                point.setStats(new AnalogStatistics(pointInfo.getStartValue() == null ? null : pointInfo
                        .getStartValue().getDoubleValue(), start, end));
                quantizer = new NumericDataQuantizer(start, end, imageWidth, this);

                discreteTimeSeries = null;
                numericTimeSeries = new TimeSeries(pointInfo.getExtendedName(), null, null, Second.class);
                numericTimeSeries.setRangeDescription(point.getTextRenderer().getMetaText());
                point.setNumericTimeSeries(numericTimeSeries);
                point.setNumericTimeSeriesColor(colour);
                if (pointInfo.isConsolidatedChart())
                    pointTimeSeriesCollection.addNumericTimeSeries(numericTimeSeries, colour);
            }
            else if (pointInfo.getDataType() == DataTypes.MULTISTATE) {
                point.setStats(new StartsAndRuntimeList(pointInfo.getStartValue(), start, end));
                quantizer = new MultistateDataQuantizer(start, end, imageWidth, this);

                discreteTimeSeries = new DiscreteTimeSeries(pointInfo.getExtendedName(), pointInfo.getTextRenderer(),
                        colour);
                point.setDiscreteTimeSeries(discreteTimeSeries);
                if (pointInfo.isConsolidatedChart())
                    pointTimeSeriesCollection.addDiscreteTimeSeries(discreteTimeSeries);
                numericTimeSeries = null;
            }
            else if (pointInfo.getDataType() == DataTypes.BINARY) {
                point.setStats(new StartsAndRuntimeList(pointInfo.getStartValue(), start, end));
                quantizer = new BinaryDataQuantizer(start, end, imageWidth, this);

                discreteTimeSeries = new DiscreteTimeSeries(pointInfo.getExtendedName(), pointInfo.getTextRenderer(),
                        colour);
                point.setDiscreteTimeSeries(discreteTimeSeries);
                if (pointInfo.isConsolidatedChart())
                    pointTimeSeriesCollection.addDiscreteTimeSeries(discreteTimeSeries);
                numericTimeSeries = null;
            }
            else if (pointInfo.getDataType() == DataTypes.ALPHANUMERIC) {
                point.setStats(new ValueChangeCounter(pointInfo.getStartValue()));
                quantizer = null;

                discreteTimeSeries = null;
                numericTimeSeries = null;
            }
            else if (pointInfo.getDataType() == DataTypes.IMAGE) {
                point.setStats(new ValueChangeCounter(pointInfo.getStartValue()));
                quantizer = null;

                discreteTimeSeries = null;
                numericTimeSeries = null;
            }

            if (reportCsvStreamer != null)
                reportCsvStreamer.startPoint(pointInfo);
        }

        public void pointData(ReportDataValue rdv) {
            if (quantizer != null)
                quantizer.data(rdv.getValue(), rdv.getTime());
            point.getStats().addValueTime(rdv);
            if (reportCsvStreamer != null)
                reportCsvStreamer.pointData(rdv);
        }

        private void donePoint() {
            if (quantizer != null)
                quantizer.done();
            if (point != null)
                point.getStats().done();
        }

        public void done() {
            donePoint();
            if (reportCsvStreamer != null)
                reportCsvStreamer.done();
        }

        // Callback
        public void quantizedData(MangoValue value, long time) {
            if (numericTimeSeries != null)
                ImageChartUtils.addSecond(numericTimeSeries, time, MangoValue.numberValue(value));
            else if (discreteTimeSeries != null)
                discreteTimeSeries.addValueTime(new PointValueTime(value, time));
        }
    }
}
