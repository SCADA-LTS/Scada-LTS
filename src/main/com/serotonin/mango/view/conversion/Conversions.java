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
package com.serotonin.mango.view.conversion;

import java.util.HashMap;
import java.util.Map;

import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;

/**
 * @author Matthew Lohbihler
 * 
 */
public class Conversions {
    private static final Map<ConversionType, Conversion> availableConversions = new HashMap<ConversionType, Conversion>();

    private static final LinearConversion DEGREES_CELSIUS_TO_DEGREES_FAHRENHEIT = new LinearConversion(1.8, 32);
    private static final LinearConversion DEGREES_FAHRENHEIT_TO_DEGREES_CELSIUS = DEGREES_CELSIUS_TO_DEGREES_FAHRENHEIT
            .getInverse();

    static {
        availableConversions.put(
                new ConversionType(EngineeringUnits.degreesFahrenheit, EngineeringUnits.degreesCelsius),
                DEGREES_FAHRENHEIT_TO_DEGREES_CELSIUS);
        availableConversions.put(
                new ConversionType(EngineeringUnits.degreesCelsius, EngineeringUnits.degreesFahrenheit),
                DEGREES_CELSIUS_TO_DEGREES_FAHRENHEIT);
    }

    public static Conversion getConversion(EngineeringUnits from, EngineeringUnits to) {
        return getConversion(from.intValue(), to.intValue());
    }

    public static Conversion getConversion(Integer from, Integer to) {
        return availableConversions.get(new ConversionType(from, to));
    }

    public static MangoValue convert(EngineeringUnits from, EngineeringUnits to, MangoValue value) {
        return convert(from.intValue(), to.intValue(), value);
    }

    public static MangoValue convert(Integer from, Integer to, MangoValue value) {
        double d = convert(from, to, value.getDoubleValue());
        return new NumericValue(d);
    }

    public static double convert(EngineeringUnits from, EngineeringUnits to, double value) {
        return convert(from.intValue(), to.intValue(), value);
    }

    public static double convert(Integer from, Integer to, double value) {
        Conversion conversion = getConversion(from, to);
        if (conversion == null)
            return Double.NaN;
        return conversion.convert(value);
    }

    public static double fahrenheitToCelsius(double fahrenheit) {
        return DEGREES_FAHRENHEIT_TO_DEGREES_CELSIUS.convert(fahrenheit);
    }

    public static double celsiusToFahrenheit(double celsius) {
        return DEGREES_CELSIUS_TO_DEGREES_FAHRENHEIT.convert(celsius);
    }

    static class ConversionType {
        private final Integer from;
        private final Integer to;

        public ConversionType(EngineeringUnits from, EngineeringUnits to) {
            this(from.intValue(), to.intValue());
        }

        public ConversionType(Integer from, Integer to) {
            this.from = from;
            this.to = to;
        }

        public Integer getFrom() {
            return from;
        }

        public Integer getTo() {
            return to;
        }
    }

    private Conversions() {
        // Static methods only
    }
}
