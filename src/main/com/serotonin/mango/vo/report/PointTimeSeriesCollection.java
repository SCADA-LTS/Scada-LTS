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

import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.serotonin.mango.rt.dataImage.PointValueTime;

/**
 * @author Matthew Lohbihler
 */
public class PointTimeSeriesCollection {
    private TimeSeriesCollection numericTimeSeriesCollection;
    private List<Paint> numericPaint;
    private List<DiscreteTimeSeries> discreteTimeSeriesCollection;

    public void addNumericTimeSeries(TimeSeries numericTimeSeries) {
        addNumericTimeSeries(numericTimeSeries, null);
    }

    public void addNumericTimeSeries(TimeSeries numericTimeSeries, Paint paint) {
        if (numericTimeSeriesCollection == null) {
            numericTimeSeriesCollection = new TimeSeriesCollection();
            numericPaint = new ArrayList<Paint>();
        }
        numericTimeSeriesCollection.addSeries(numericTimeSeries);
        numericPaint.add(paint);
    }

    public void addDiscreteTimeSeries(DiscreteTimeSeries discreteTimeSeries) {
        if (discreteTimeSeriesCollection == null)
            discreteTimeSeriesCollection = new ArrayList<DiscreteTimeSeries>();
        discreteTimeSeriesCollection.add(discreteTimeSeries);
    }

    public boolean hasData() {
        return hasNumericData() || hasDiscreteData();
    }

    public boolean hasNumericData() {
        return numericTimeSeriesCollection != null;
    }

    public boolean hasDiscreteData() {
        return discreteTimeSeriesCollection != null;
    }

    public boolean hasMultiplePoints() {
        int count = 0;
        if (numericTimeSeriesCollection != null)
            count += numericTimeSeriesCollection.getSeriesCount();
        if (discreteTimeSeriesCollection != null)
            count += discreteTimeSeriesCollection.size();
        return count > 1;
    }

    public TimeSeriesCollection getNumericTimeSeriesCollection() {
        return numericTimeSeriesCollection;
    }

    public List<Paint> getNumericPaint() {
        return numericPaint;
    }

    public int getDiscreteValueCount() {
        int count = 0;

        if (discreteTimeSeriesCollection != null) {
            for (DiscreteTimeSeries dts : discreteTimeSeriesCollection)
                count += dts.getDiscreteValueCount();
        }

        return count;
    }

    public TimeSeriesCollection createTimeSeriesCollection(double numericMin, double spacingInterval) {
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

        int intervalIndex = 1;
        for (DiscreteTimeSeries dts : discreteTimeSeriesCollection) {
            TimeSeries ts = new TimeSeries(dts.getName(), null, null, Second.class);

            for (PointValueTime pvt : dts.getValueTimes())
                ImageChartUtils.addSecond(ts, pvt.getTime(), numericMin
                        + (spacingInterval * (dts.getValueIndex(pvt.getValue()) + intervalIndex)));

            timeSeriesCollection.addSeries(ts);

            intervalIndex += dts.getDiscreteValueCount();
        }

        return timeSeriesCollection;
    }

    public int getDiscreteSeriesCount() {
        return discreteTimeSeriesCollection.size();
    }

    public DiscreteTimeSeries getDiscreteTimeSeries(int index) {
        return discreteTimeSeriesCollection.get(index);
    }
}
