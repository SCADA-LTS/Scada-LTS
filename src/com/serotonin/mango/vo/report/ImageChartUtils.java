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

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.*;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.TextAnchor;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.io.StreamUtils;
import org.scada_lts.dao.SystemSettingsDAO;
import com.serotonin.mango.util.mindprod.StripEntities;
import com.serotonin.util.StringUtils;
import org.scada_lts.mango.service.SystemSettingsService;

import static org.scada_lts.serorepl.utils.StringUtils.truncate;

/**
 * @author Matthew Lohbihler
 */
public final class ImageChartUtils {

    private ImageChartUtils() {}

    private static final Log LOG = LogFactory.getLog(ImageChartUtils.class);

    private static final int NUMERIC_DATA_INDEX = 0;
    private static final int DISCRETE_DATA_INDEX = 1;

    public static void writeChart(PointTimeSeriesCollection pointTimeSeriesCollection, OutputStream out, int width,
            int height) throws IOException {
        writeChart(pointTimeSeriesCollection, pointTimeSeriesCollection.hasMultiplePoints(), out, width, height);
    }

    public static byte[] getChartData(PointTimeSeriesCollection pointTimeSeriesCollection, int width, int height) {
        return getChartData(pointTimeSeriesCollection, pointTimeSeriesCollection.hasMultiplePoints(), width, height);
    }

    public static byte[] getChartData(PointTimeSeriesCollection pointTimeSeriesCollection, boolean showLegend,
            int width, int height) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeChart(pointTimeSeriesCollection, showLegend, out, width, height);
            return out.toByteArray();
        }
        catch (IOException e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    public static void writeChart(PointTimeSeriesCollection pointTimeSeriesCollection, boolean showLegend,
            OutputStream out, int width, int height) throws IOException {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null, null, null, showLegend, false, false);
        chart.setBackgroundPaint(SystemSettingsDAO.getColour(SystemSettingsDAO.CHART_BACKGROUND_COLOUR));

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(SystemSettingsDAO.getColour(SystemSettingsDAO.PLOT_BACKGROUND_COLOUR));
        Color gridlines = SystemSettingsDAO.getColour(SystemSettingsDAO.PLOT_GRIDLINE_COLOUR);
        plot.setDomainGridlinePaint(gridlines);
        plot.setRangeGridlinePaint(gridlines);

        double numericMin = 0;
        double numericMax = 1;
        if (pointTimeSeriesCollection.hasNumericData()) {
            XYLineAndShapeRenderer numericRenderer = new XYLineAndShapeRenderer(true, false);
            TimeSeriesCollection timeSeriesCollection = pointTimeSeriesCollection.getNumericTimeSeriesCollection();

            plot.setRenderer(NUMERIC_DATA_INDEX, numericRenderer);
            plot.setDataset(NUMERIC_DATA_INDEX, timeSeriesCollection);

            for (int i = 0; i < pointTimeSeriesCollection.getNumericPaint().size(); i++) {
                Paint paint = pointTimeSeriesCollection.getNumericPaint().get(i);
                if (paint != null)
                    numericRenderer.setSeriesPaint(i, paint, false);
            }

            numericMin = plot.getRangeAxis().getLowerBound();
            numericMax = plot.getRangeAxis().getUpperBound();

            if (!pointTimeSeriesCollection.hasMultiplePoints()) {
                // If this chart displays a single point, check if there should be a range description.
                TimeSeries timeSeries = pointTimeSeriesCollection.getNumericTimeSeriesCollection().getSeries(0);
                String desc = timeSeries.getRangeDescription();
                if (!StringUtils.isEmpty(desc)) {
                    // Replace any HTML entities with Java equivalents
                    desc = StripEntities.stripHTMLEntities(desc, ' ');
                    plot.getRangeAxis().setLabel(desc);
                }
            }
        }
        else
            plot.getRangeAxis().setVisible(false);

        if (pointTimeSeriesCollection.hasDiscreteData()) {
            XYStepRenderer discreteRenderer = new XYStepRenderer();
            TimeSeriesCollection timeSeriesCollection = pointTimeSeriesCollection.createTimeSeriesCollection(numericMin, numericMax);

            plot.setRenderer(DISCRETE_DATA_INDEX, discreteRenderer, false);
            plot.setDataset(DISCRETE_DATA_INDEX, timeSeriesCollection);

            // Plot the data
            int discreteValueCount = pointTimeSeriesCollection.getDiscreteValueCount();
            int discreteSeriesCount = pointTimeSeriesCollection.getDiscreteSeriesCount();
            double interval = pointTimeSeriesCollection.getDiscreteInterval(numericMin, numericMax);

            // Add the value annotations.
            double annoX = plot.getDomainAxis().getLowerBound();
            int intervalIndex = 1;
            for (int i = 0; i < discreteSeriesCount; i++) {
                DiscreteTimeSeries dts = pointTimeSeriesCollection.getDiscreteTimeSeries(i);
                if (dts.getPaint() != null)
                    discreteRenderer.setSeriesPaint(i, dts.getPaint());

                for (int j = 0; j < dts.getDiscreteValueCount(); j++) {
                    XYTextAnnotation anno = new XYTextAnnotation(" " + dts.getValueText(j), annoX, numericMin
                            + (interval * (j + intervalIndex)));
                    if (!pointTimeSeriesCollection.hasNumericData() && intervalIndex + j == discreteValueCount)
                        // This prevents the top label from getting cut off
                        anno.setTextAnchor(TextAnchor.TOP_LEFT);
                    else
                        anno.setTextAnchor(TextAnchor.BOTTOM_LEFT);
                    anno.setPaint(discreteRenderer.lookupSeriesPaint(i));
                    plot.addAnnotation(anno);
                }

                intervalIndex += dts.getDiscreteValueCount();
            }
        }

        if(showLegend && chart.getLegend() != null) {
            LegendItemCollection legendItemCollection = getLegendItemCollectionSort(chart);
            if(legendItemCollection != null)
                plot.setFixedLegendItems(legendItemCollection);
        }


        // Return the image.
        ChartUtilities.writeChartAsPNG(out, chart, width, height);
    }

    public static void writeChart(HttpServletResponse response, byte[] chartData) throws IOException {
        response.setContentType(getContentType());
        StreamUtils.transfer(new ByteArrayInputStream(chartData), response.getOutputStream());
    }

    public static String getContentType() {
        return "image/x-png";
    }

    public static void addSecond(TimeSeries timeSeries, long time, Number value) {
        try {
            timeSeries.add(new Second(new Date(time)), value);
        }
        catch (SeriesException e) { /* duplicate Second. Ignore. */
        }
    }

    public static int calculateHeightChart(List<ReportChartCreator.PointStatistics> pointStatistics, int imageHeightPixels,
                                           int pointLabelHeightInLegendPixels, int lineLengthInLegendLimit, int dataPointExtendedNameLengthLimit) {
        int linesNumber = calculateLinesNumber(toListNames(pointStatistics), lineLengthInLegendLimit, dataPointExtendedNameLengthLimit);
        return imageHeightPixels - pointLabelHeightInLegendPixels + ((linesNumber + 1) * pointLabelHeightInLegendPixels);
    }

    public static int calculateLinesNumber(List<String> pointStatisticsNames, int lineLengthInLegendLimit, int dataPointExtendedNameLengthLimit) {
        if(pointStatisticsNames.isEmpty() || (pointStatisticsNames.size() == 1 && pointStatisticsNames.get(0) == null)) {
            return 1;
        }
        StringBuilder subLegend = new StringBuilder();
        int linesNumber = 1;
        String split = " ** ";
        for (int i = 0; i < pointStatisticsNames.size(); i++) {
            String name = pointStatisticsNames.get(i);
            if (name == null || name.isEmpty())
                continue;
            name = truncate(split + truncate(name, "", dataPointExtendedNameLengthLimit), "", lineLengthInLegendLimit);
            if (subLegend.length() + name.length() > lineLengthInLegendLimit) {
                LOG.debug(subLegend);
                subLegend.delete(0, subLegend.length());
                linesNumber++;
            }
            subLegend.append(name);
            if(i == pointStatisticsNames.size() - 1) {
                LOG.debug(subLegend);
                subLegend.delete(0, subLegend.length());
            }
        }
        return linesNumber;
    }

    public static String calculatePointNameForReport(String extendedName) {
        SystemSettingsService settings = new SystemSettingsService();
        int dataPointExtendedNameLengthInReportsLimit = settings.getDataPointExtendedNameLengthInReportsLimit();
        if(extendedName.length() > dataPointExtendedNameLengthInReportsLimit) {
            return truncate(extendedName, "...", dataPointExtendedNameLengthInReportsLimit);
        }
        return extendedName;
    }

    private static List<String> toListNames(List<ReportChartCreator.PointStatistics> pointStatistics) {
        return pointStatistics.stream().map(a -> a.getName().toString()).collect(Collectors.toList());
    }

    private static LegendItemCollection getLegendItemCollectionSort(JFreeChart chart) {
        LegendTitle legend = chart.getLegend();
        LegendItemSource[] legendItemSource = legend.getSources();
        if(legendItemSource.length > 0) {
            LegendItemCollection legendItemCollections = legendItemSource[0].getLegendItems();
            Iterator iterator = legendItemCollections.iterator();
            List<LegendItem> legendItems = new ArrayList<>();
            while (iterator.hasNext()) {
                LegendItem legendItem = (LegendItem) iterator.next();
                legendItems.add(legendItem);
            }
            legendItems.sort(Comparator.comparing(LegendItem::getSeriesKey));
            LegendItemCollection legendItemCollection = new LegendItemCollection();
            for (LegendItem legendItem : legendItems) {
                legendItemCollection.add(legendItem);
            }
            return legendItemCollection;
        }
        return null;
    }
}
