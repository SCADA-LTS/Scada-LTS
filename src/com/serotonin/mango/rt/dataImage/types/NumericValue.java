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
package com.serotonin.mango.rt.dataImage.types;

import com.serotonin.mango.DataTypes;

/**
 * @author Matthew Lohbihler
 */
public class NumericValue extends MangoValue implements Comparable<NumericValue> {
    public static NumericValue parseNumeric(String s) {
        if (s == null)
            return new NumericValue(0);
        try {
            return new NumericValue(Double.parseDouble(s));
        }
        catch (NumberFormatException e) {
            // no op
        }
        return new NumericValue(0);
    }

    private final double value;

    public NumericValue(double value) {
        this.value = value;
    }

    @Override
    public boolean hasDoubleRepresentation() {
        return true;
    }

    @Override
    public double getDoubleValue() {
        return value;
    }

    public float getFloatValue() {
        return (float) value;
    }

    @Override
    public String getStringValue() {
        return null;
    }

    @Override
    public boolean getBooleanValue() {
        throw new RuntimeException("NumericValue has no boolean value.");
    }

    @Override
    public Object getObjectValue() {
        return value;
    }

    @Override
    public int getIntegerValue() {
        return (int) value;
    }

    @Override
    public Number numberValue() {
        return value;
    }

    @Override
    public int getDataType() {
        return DataTypes.NUMERIC;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final NumericValue other = (NumericValue) obj;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        return true;
    }

    @Override
    public int compareTo(NumericValue that) {
        return Double.compare(value, that.value);
    }

    @Override
    public <T extends MangoValue> int compareTo(T that) {
        return compareTo((NumericValue) that);
    }
}
