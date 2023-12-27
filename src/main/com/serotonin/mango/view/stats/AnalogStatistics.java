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
package com.serotonin.mango.view.stats;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.mango.rt.dataImage.PointValueTime;

/**
 * @author Matthew Lohbihler
 */
public class AnalogStatistics implements StatisticsGenerator {
    // Calculated values.
    private double minimum = Double.MAX_VALUE;
    private long minTime;
    private double maximum = -Double.MAX_VALUE;
    private long maxTime;
    private double average = 0;
    private double sum;
    private int count;
    private boolean noData = true;
    private long realStart;
    private final long end;

    // State values.
    private long lastTime = -1;
    private long realDuration = -1;
    private Double lastValue;

    public AnalogStatistics(PointValueTime startValue, List<? extends IValueTime> values, long start, long end) {
        this(startValue == null ? null : startValue.getDoubleValue(), values, start, end);
    }

    public AnalogStatistics(Double startValue, List<? extends IValueTime> values, long start, long end) {
        this(startValue, start, end);
        for (IValueTime p : values)
            addValueTime(p);
        done();
    }

    public AnalogStatistics(Double startValue, long start, long end) {
        this.end = end;

        if (startValue != null) {
            minimum = maximum = startValue;
            minTime = maxTime = start;
            lastTime = start;
            lastValue = startValue;
            noData = false;
        }
    }

    public void addValueTime(IValueTime vt) {
        if (vt.getValue() == null)
            return;

        count++;
        noData = false;

        if (lastTime == -1)
            lastTime = vt.getTime();

        if (realDuration == -1) {
            realStart = lastTime;
            realDuration = end - lastTime;
        }

        if (realDuration < 0)
            return;

        if (realDuration == 0) {
            // We assume that this point is the only point in the data set.
            minimum = maximum = average = sum = vt.getValue().getDoubleValue();
            return;
        }

        sum += vt.getValue().getDoubleValue();

        if (lastValue != null) {
            average += lastValue * (((double) (vt.getTime() - lastTime)) / realDuration);
            if (lastValue > maximum) {
                maximum = lastValue;
                maxTime = lastTime;
            }
            if (lastValue < minimum) {
                minimum = lastValue;
                minTime = lastTime;
            }
        }
        lastValue = vt.getValue().getDoubleValue();
        lastTime = vt.getTime();
    }

    public void done() {
        if (lastValue != null) {
            if (realDuration == -1) {
                realStart = lastTime;
                average += lastValue;
            }
            else
                average += lastValue * (((double) (end - lastTime)) / realDuration);

            if (lastValue > maximum) {
                maximum = lastValue;
                maxTime = lastTime;
            }
            if (lastValue < minimum) {
                minimum = lastValue;
                minTime = lastTime;
            }
        }
        else {
            minimum = maximum = 0;
        }
    }

    public double getMinimum() {
        return minimum;
    }

    public long getMinTime() {
        return minTime;
    }

    public double getMaximum() {
        return maximum;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public double getAverage() {
        return average;
    }

    public double getSum() {
        return sum;
    }

    public int getCount() {
        return count;
    }

    public boolean isNoData() {
        return noData;
    }

    public long getRealStart() {
        return realStart;
    }

    public long getEnd() {
        return end;
    }

    public String getHelp() {
        return toString();
    }

    @Override
    public String toString() {
        return "{minimum: " + minimum + ", minTime=" + minTime + ", maximum: " + maximum + ", maxTime=" + maxTime
                + ", average: " + average + ", sum: " + sum + ", count: " + count + ", noData: " + noData
                + ", realStart: " + realStart + ", end: " + end + "}";
    }

    public static void main(String[] args) {
        Double startValue = 10d;
        List<PointValueTime> values = new ArrayList<PointValueTime>();
        values.add(new PointValueTime(11d, 2000));
        values.add(new PointValueTime(12d, 3000));
        values.add(new PointValueTime(7d, 4000));
        values.add(new PointValueTime(13d, 5000));
        values.add(new PointValueTime(18d, 6000));
        values.add(new PointValueTime(14d, 8000));

        System.out.println(new AnalogStatistics(startValue, values, 1000, 10000));
        System.out.println(new AnalogStatistics(startValue, values, 1500, 15000));
        System.out.println(new AnalogStatistics((Double) null, values, 1000, 10000));
        System.out.println(new AnalogStatistics((Double) null, values, 1500, 15000));
        System.out.println(new AnalogStatistics((Double) null, new ArrayList<PointValueTime>(), 1500, 15000));
        System.out.println(new AnalogStatistics(startValue, new ArrayList<PointValueTime>(), 1500, 15000));
    }
}
