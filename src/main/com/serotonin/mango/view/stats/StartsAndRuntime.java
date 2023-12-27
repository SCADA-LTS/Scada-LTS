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
public class StartsAndRuntime {
    MangoValue value;
    int starts;
    long runtime;
    double proportion;

    public Object getValue() {
        if (value == null)
            return null;
        return value.getObjectValue();
    }

    public MangoValue getMangoValue() {
        return value;
    }

    public long getRuntime() {
        return runtime;
    }

    public double getProportion() {
        return proportion;
    }

    public double getPercentage() {
        return proportion * 100;
    }

    public int getStarts() {
        return starts;
    }

    void calculateRuntimePercentage(long duration) {
        proportion = ((double) runtime) / duration;
    }

    public String getHelp() {
        return toString();
    }

    @Override
    public String toString() {
        return "{value: " + value + ", starts: " + starts + ", runtime: " + runtime + ", proportion: " + proportion
                + ", percentage: " + getPercentage() + "}";
    }
}
