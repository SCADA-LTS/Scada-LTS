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

import com.serotonin.mango.rt.dataImage.types.MangoValue;

/**
 * @author Matthew Lohbihler
 */
abstract public class AbstractDataQuantizer {
    private final long start;
    private final int buckets;
    private final long duration;
    private final DataQuantizerCallback callback;

    private int periodCounter;
    private double periodFrom;
    private double periodTo;
    private int valueCounter;

    public AbstractDataQuantizer(long start, long end, int buckets, DataQuantizerCallback callback) {
        this.start = start;
        this.buckets = buckets;
        duration = end - start;
        this.callback = callback;

        periodFrom = start;
        calculatePeriodTo();
    }

    private void calculatePeriodTo() {
        periodTo = periodFrom + ((double) duration) / buckets * ++periodCounter;
    }

    public void data(MangoValue value, long time) {
        while (time >= periodTo) {
            done();
            periodFrom = periodTo;
            periodTo = start + ((double) duration) / buckets * ++periodCounter;
        }

        valueCounter++;
        periodData(value);
    }

    public void done() {
        if (valueCounter > 0) {
            callback.quantizedData(donePeriod(valueCounter), (long) ((periodFrom + periodTo) / 2));
            valueCounter = 0;
        }
    }

    abstract protected void periodData(MangoValue value);

    abstract protected MangoValue donePeriod(int valueCounter);
}
