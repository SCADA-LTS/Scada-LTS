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
public class BinaryValue extends MangoValue implements Comparable<BinaryValue> {
    public static final BinaryValue ZERO = new BinaryValue(false);
    public static final BinaryValue ONE = new BinaryValue(true);

    public static BinaryValue parseBinary(String s) {
        if (s == null || "0".equals(s))
            return ZERO;
        if ("1".equals(s) || Boolean.parseBoolean(s))
            return ONE;
        return ZERO;
    }

    private final boolean value;

    public BinaryValue(boolean value) {
        this.value = value;
    }

    @Override
    public boolean hasDoubleRepresentation() {
        return true;
    }

    @Override
    public double getDoubleValue() {
        return value ? 1 : 0;
    }

    @Override
    public String getStringValue() {
        return null;
    }

    @Override
    public boolean getBooleanValue() {
        return value;
    }

    @Override
    public Object getObjectValue() {
        return value;
    }

    @Override
    public int getIntegerValue() {
        return value ? 1 : 0;
    }

    @Override
    public int getDataType() {
        return DataTypes.BINARY;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public Number numberValue() {
        throw new RuntimeException("BinaryValue has no Number value.");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (value ? 1231 : 1237);
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
        final BinaryValue other = (BinaryValue) obj;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public int compareTo(BinaryValue that) {
        return (that.value == value ? 0 : (value ? 1 : -1));
    }

    @Override
    public <T extends MangoValue> int compareTo(T that) {
        return compareTo((BinaryValue) that);
    }
}
