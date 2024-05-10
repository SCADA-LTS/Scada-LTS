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
import java.util.*;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.serotonin.mango.rt.dataImage.PointValueTime;

/**
 * @author Matthew Lohbihler
 */
public class PointTimeSeriesCollection {

    private Map<Comparable, TimeSeries> numericTimeSeriesCollection;
    private Map<Comparable, DiscreteTimeSeries> discreteTimeSeriesCollection;
    private Map<Comparable, Paint> numericPaint;

    public PointTimeSeriesCollection() {
        this.numericTimeSeriesCollection = new TreeMap<>();
        this.discreteTimeSeriesCollection  = new TreeMap<>();
        this.numericPaint = new TreeMap<>();
    }

    @Deprecated(since = "2.7.8")
    public void addNumericTimeSeries(TimeSeries timeSeries) {
        addNumericTimeSeries(timeSeries, null);
    }

    public void addNumericTimeSeries(TimeSeries timeSeries, Paint paint) {
        numericTimeSeriesCollection.put(timeSeries.getKey(), timeSeries);
        numericPaint.put(timeSeries.getKey(), paint);
    }

    public void addDiscreteTimeSeries(DiscreteTimeSeries discreteTimeSeries) {
        discreteTimeSeriesCollection.put(discreteTimeSeries.getName(), discreteTimeSeries);
    }

    public boolean hasData() {
        return hasNumericData() || hasDiscreteData();
    }

    public boolean hasNumericData() {
        return !numericTimeSeriesCollection.isEmpty();
    }

    public boolean hasDiscreteData() {
        return !discreteTimeSeriesCollection.isEmpty();
    }

    public boolean hasMultiplePoints() {
        int count = 0;
        if (numericTimeSeriesCollection != null)
            count += numericTimeSeriesCollection.size();
        if (discreteTimeSeriesCollection != null)
            count += discreteTimeSeriesCollection.size();
        return count > 1;
    }

    public TimeSeriesCollection getNumericTimeSeriesCollection() {
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        for(TimeSeries timeSeries: getTimeSeriesCollection()) {
            timeSeriesCollection.addSeries(timeSeries);
        }
        return timeSeriesCollection;
    }

    public List<Paint> getNumericPaint() {
        return new ArrayList<>(numericPaint.values());
    }

    public int getDiscreteValueCount() {
        int count = 0;


        for (DiscreteTimeSeries dts : getDiscreteTimeSeriesCollection())
            count += dts.getDiscreteValueCount();


        return count;
    }

    public TimeSeriesCollection createTimeSeriesCollection(double numericMin, double numericMax) {

        double spacingInterval = getDiscreteInterval(numericMin, numericMax);
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

        int intervalIndex = 1;
        for (DiscreteTimeSeries dts : getDiscreteTimeSeriesCollection()) {
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
        return getDiscreteTimeSeriesCollection().size();
    }

    public DiscreteTimeSeries getDiscreteTimeSeries(int index) {
        return getDiscreteTimeSeriesCollection().get(index);
    }

    public double getDiscreteInterval(double numericMin, double numericMax) {
        int discreteValueCount = getDiscreteValueCount();
        return (numericMax - numericMin) / (discreteValueCount + 1);
    }

    private List<DiscreteTimeSeries> getDiscreteTimeSeriesCollection() {
        return new ArrayList<>(discreteTimeSeriesCollection.values());
    }

    private List<TimeSeries> getTimeSeriesCollection() {
        return new ArrayList<>(numericTimeSeriesCollection.values());
    }
}
