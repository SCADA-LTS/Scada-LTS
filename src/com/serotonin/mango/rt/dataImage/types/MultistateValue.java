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
public class MultistateValue extends MangoValue implements Comparable<MultistateValue> {
    public static MultistateValue parseMultistate(String s) {
        if (s == null)
            return new MultistateValue(0);
        return new MultistateValue(Integer.parseInt(s));
    }

    private final int value;

    public MultistateValue(int value) {
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

    @Override
    public String getStringValue() {
        return null;
    }

    @Override
    public boolean getBooleanValue() {
        throw new RuntimeException("MultistateValue has no boolean value.");
    }

    @Override
    public Object getObjectValue() {
        return value;
    }

    @Override
    public int getIntegerValue() {
        return value;
    }

    @Override
    public Number numberValue() {
        return value;
    }

    @Override
    public int getDataType() {
        return DataTypes.MULTISTATE;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
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
        final MultistateValue other = (MultistateValue) obj;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public int compareTo(MultistateValue that) {
        return (value < that.value ? -1 : (value == that.value ? 0 : 1));
    }

    @Override
    public <T extends MangoValue> int compareTo(T that) {
        return compareTo((MultistateValue) that);
    }
}
