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

import com.serotonin.InvalidArgumentException;
import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.DataTypes;

/**
 * @author Matthew Lohbihler
 */
abstract public class MangoValue {
    public static MangoValue stringToValue(String valueStr, int dataType) {
        switch (dataType) {
        case DataTypes.BINARY:
            return BinaryValue.parseBinary(valueStr);
        case DataTypes.MULTISTATE:
            return MultistateValue.parseMultistate(valueStr);
        case DataTypes.NUMERIC:
            return NumericValue.parseNumeric(valueStr);
        case DataTypes.IMAGE:
            try {
                return new ImageValue(valueStr);
            }
            catch (InvalidArgumentException e) {
                // no op
            }
            return null;
        case DataTypes.ALPHANUMERIC:
            return new AlphanumericValue(valueStr);
        }
        throw new ShouldNeverHappenException("Invalid data type " + dataType + ". Cannot instantiate MangoValue");
    }

    public static MangoValue objectToValue(Object value) {
        if (value instanceof Boolean)
            return new BinaryValue((Boolean) value);
        if (value instanceof Integer)
            return new MultistateValue((Integer) value);
        if (value instanceof Double)
            return new NumericValue((Double) value);
        if (value instanceof String)
            return new AlphanumericValue((String) value);
        throw new ShouldNeverHappenException("Unrecognized object type " + (value == null ? "null" : value.getClass())
                + ". Cannot instantiate MangoValue");
    }

    abstract public boolean hasDoubleRepresentation();

    abstract public double getDoubleValue();

    abstract public String getStringValue();

    abstract public int getIntegerValue();

    abstract public boolean getBooleanValue();

    abstract public Object getObjectValue();

    abstract public int getDataType();

    abstract public Number numberValue();

    public static Number numberValue(MangoValue value) {
        if (value == null)
            return null;
        return value.numberValue();
    }

    abstract public <T extends MangoValue> int compareTo(T that);
}
