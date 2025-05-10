package com.serotonin.mango.vo;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EngineeringUnitsTypes implements EngineeringUnit {

    DEGREES_ANGULAR(90, "degrees angular", "\u00B0"),
    DEGREES_CELSIUS_PER_HOUR(91, "degrees celsius per hour", "\u00B0C/h"),
    DEGREES_CELSIUS_PER_MINUTE(92, "degrees celsius per minute", "\u00B0C/min"),
    DEGREES_FAHRENHEIT_PER_HOUR(93, "degrees fahrenheit per hour", "\u00B0F/h"),
    DEGREES_FAHRENHEIT_PER_MINUTE(94, "degrees fahrenheit per minute", "\u00B0F/min"),
    JOULE_SECONDS(183, "joule seconds", "J\u00B7s"),
    KILOGRAMS_PER_CUBIC_METER(186, "kilograms per cubic meter", "kg/m\u00B3"),
    KILOWATT_HOURS_PER_SQUARE_METER(137, "kilowatt hours per square meter", "kWh/m\u00B2"),
    KILOWATT_HOURS_PER_SQUARE_FOOT(138, "kilowatt hours per square foot", "kWh/ft\u00B2"),
    MEGAJOULES_PER_SQUARE_METER(139, "megajoules per square meter", "MJ/m\u00B2"),
    MEGAJOULES_PER_SQUARE_FOOT(140, "megajoules per square foot", "MJ/ft\u00B2"),
    NO_UNITS(95, "", "") {
        @Override
        public String getSuffix() {
            return "";
        }
    },
    NEWTON_SECONDS(187, "newton seconds", "N\u00B7s"),
    NEWTONS_PER_METER(188, "newtons per meter", "N/m"),
    PARTS_PER_MILLION(96, "parts per million", "ppm"),
    PARTS_PER_BILLION(97, "parts per billion", "ppb"),
    PERCENT(98, "percent", "%"),
    PERCENT_OBSCURATION_PER_FOOT(143, "percent obscuration per foot", "%/ft"),
    PERCENT_OBSCURATION_PER_METER(144, "percent obscuration per meter", "%/m"),
    PERCENT_PER_SECOND(99, "percent per second", "%/s"),
    PER_MINUTE(100, "per minute", "1/min"),
    PER_SECOND(101, "per second", "1/s"),
    PSI_PER_DEGREE_FAHRENHEIT(102, "psi per degree fahrenheit", "psi/\u00B0F"),
    RADIANS(103, "radians", "rad"),
    RADIANS_PER_SECOND(184, "radians per second", "rad/s"),
    REVOLUTIONS_PER_MINUTE(104, "revolutions per minute", "rpm"),
    SQUARE_METERS_PER_NEWTON(185, "square meters perNewton", "m\u00B2/N"),
    WATTS_PER_METER_PER_DEGREE_KELVIN(189, "watts per meter per degree kelvin", "W/m\u00B7K"),
    WATTS_PER_SQUARE_METER_DEGREE_KELVIN(141, "watts per square meter degree kelvin", "W/m\u00B2\u00B7K");

    private final int value;
    private final String name;
    private final String suffix;

    public static final String KEY = "engUnitGroup.other";

    EngineeringUnitsTypes(int value, String name, String suffix) {
        this.value = value;
        this.name = name;
        this.suffix = suffix;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSuffix() {
        return "[" + suffix + "]";
    }

    @Override
    public String getKey() {
        return "engUnit." + getValue();
    }

    public enum Acceleration implements EngineeringUnit {
        METERS_PER_SECOND_PER_SECOND(166, "meters per second per second", "m/s\u00B2");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.acceleration";

        Acceleration(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Area implements EngineeringUnit {

        SQUARE_METERS(0,"square meters","m\u00B2"),

        SQUARE_CENTIMETERS(116,"square centimeters","cm\u00B2"),

        SQUARE_FEET(1,"square feet","ft\u00B2"),

        SQUARE_INCHES(115,"square inches","in\u00B2");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.area";

        Area(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Currency implements EngineeringUnit {

        CURRENCY1(105, "currency 1", "$"),
        CURRENCY2(106, "currency 2", "\u20AC"),
        CURRENCY3(107, "currency 3", "\u00A3"),
        CURRENCY4(108, "currency 4", "\u00A5"),
        CURRENCY5(109, "currency 5", "\u20BD"),
        CURRENCY6(110, "currency 6", "kr"),
        CURRENCY7(111, "currency 7", "Rs"),
        CURRENCY8(112, "currency 8", "R$"),
        CURRENCY9(113, "currency 9", "\u20A9"),
        CURRENCY10(114, "currency 10", "CHF");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.currency";

        Currency(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Electrical implements EngineeringUnit {
        MILLIAMPERES(2, "milliamperes", "mA"),
        AMPERES(3, "amperes", "A"),
        AMPERES_PER_METER(167, "amperes per meter", "A/m"),
        AMPERES_PER_SQUARE_METER(168, "amperes per square meter", "A/m\u00B2"),
        AMPERE_SQUARE_METERS(169, "ampere square meters", "A\u00B7m\u00B2"),
        FARADS(170, "farads", "F"),
        HENRYS(171, "henrys", "H"),
        OHMS(4, "ohms", "\u03A9"),
        OHM_METERS(172, "ohm meters", "\u03A9\u00B7m"),
        MILLIOHMS(145, "milliohms", "m\u03A9"),
        KILOHMS(122, "kilohms", "k\u03A9"),
        MEGOHMS(123, "megohms", "M\u03A9"),
        SIEMENS(173, "siemens", "S"),
        SIEMENS_PER_METER(174, "siemens per meter", "S/m"),
        TESLAS(175, "teslas", "T"),
        VOLTS(5, "volts", "V"),
        MILLIVOLTS(124, "millivolts", "mV"),
        KILOVOLTS(6, "kilovolts", "kV"),
        MEGAVOLTS(7, "megavolts", "MV"),
        VOLT_AMPERES(8, "volt amperes", "VA"),
        KILOVOLT_AMPERES(9, "kilovolt amperes", "kVA"),
        MEGAVOLT_AMPERES(10, "megavolt amperes", "MVA"),
        VOLT_AMPERES_REACTIVE(11, "volt amperes reactive", "var"),
        KILOVOLT_AMPERES_REACTIVE(12, "kilovolt amperes reactive", "kvar"),
        MEGAVOLT_AMPERES_REACTIVE(13, "megavolt amperes reactive", "Mvar"),
        VOLTS_PER_DEGREE_KELVIN(176, "volts per degree kelvin", "V/K"),
        VOLTS_PER_METER(177, "volts per meter", "V/m"),
        DEGREES_PHASE(14, "degrees phase", "\u00B0"),
        POWER_FACTOR(15, "power factor", "%"),
        WEBERS(178, "webers", "Wb");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.electrical";

        Electrical(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Energy implements EngineeringUnit {
        JOULES(16, "joules", "J"),
        KILOJOULES(17, "kilojoules", "kJ"),
        KILOJOULES_PER_KILOGRAM(125, "kilojoules per kilogram", "kJ/kg"),
        MEGAJOULES(126, "megajoules", "MJ"),
        WATT_HOURS(18, "watt hours", "Wh"),
        KILOWATT_HOURS(19, "kilowatt hours", "kWh"),
        MEGAWATT_HOURS(146, "megawatt hours", "MWh"),
        BTUS(20, "btus", "BTU"),
        KILO_BTUS(147, "kilo btus", "kBTU"),
        MEGA_BTUS(148, "mega btus", "MBTU"),
        THERMS(21, "therms", "therm"),
        TON_HOURS(22, "ton hours", "ton h");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.energy";

        Energy(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Enthalpy implements EngineeringUnit {
        JOULES_PER_KILOGRAM_DRY_AIR(23, "joules per kilogram dry air", "J/kg dry air"),
        KILOJOULES_PER_KILOGRAM_DRY_AIR(149, "kilojoules per kilogram dry air", "kJ/kg dry air"),
        MEGAJOULES_PER_KILOGRAM_DRY_AIR(150, "megajoules per kilogram dry air", "MJ/kg dry air"),
        BTUS_PER_POUND_DRY_AIR(24, "btus per pound dry air", "BTU/lb dry air"),
        BTUS_PER_POUND(117, "btus per pound", "BTU/lb");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.enthalpy";

        Enthalpy(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Entropy implements EngineeringUnit {
        JOULES_PER_DEGREE_KELVIN(127, "joules per degree kelvin", "J/K"),
        KILOJOULES_PER_DEGREE_KELVIN(151, "kilojoules per degree kelvin", "kJ/K"),
        MEGAJOULES_PER_DEGREE_KELVIN(152, "megajoules per degree kelvin", "MJ/K"),
        JOULES_PER_KILOGRAM_DEGREE_KELVIN(128, "joules per kilogram degree kelvin", "J/(kg\u00B7K)");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.entropy";

        Entropy(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Force implements EngineeringUnit {
        NEWTON(153, "newton", "N");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.force";

        Force(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Frequency implements EngineeringUnit {
        CYCLES_PER_HOUR(25, "cycles per hour", "cph"),
        CYCLES_PER_MINUTE(26, "cycles per minute", "cpm"),
        HERTZ(27, "hertz", "Hz"),
        KILOHERTZ(129, "kilohertz", "kHz"),
        MEGAHERTZ(130, "megahertz", "MHz"),
        PER_HOUR(131, "per hour", "1/h");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.frequency";

        Frequency(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Humidity implements EngineeringUnit {
        GRAMS_OF_WATER_PER_KILOGRAM_DRY_AIR(28, "grams of water per kilogram dry air", "g/kg dry air"),
        PERCENT_RELATIVE_HUMIDITY(29, "percent relative humidity", "%");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.humidity";

        Humidity(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Length implements EngineeringUnit {
        MILLIMETERS(30, "millimeters", "mm"),
        CENTIMETERS(118, "centimeters", "cm"),
        METERS(31, "meters", "m"),
        INCHES(32, "inches", "in"),
        FEET(33, "feet", "ft"),
        KILOMETERS(190, "kilometers", "km");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.length";

        Length(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Light implements EngineeringUnit {
        CANDELAS(179, "candelas", "cd"),
        CANDELAS_PER_SQUARE_METER(180, "candelas per square meter", "cd/m\u00B2"),
        WATTS_PER_SQUARE_FOOT(34, "watts per square foot", "W/ft\u00B2"),
        WATTS_PER_SQUARE_METER(35, "watts per square meter", "W/m\u00B2"),
        LUMENS(36, "lumens", "lm"),
        LUXES(37, "luxes", "lx"),
        FOOT_CANDLES(38, "foot candles", "fc");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.light";

        Light(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Mass implements EngineeringUnit {
        KILOGRAMS(39, "kilograms", "kg"),
        POUNDS_MASS(40, "pounds mass", "lb"),
        TONS(41, "tons", "t");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.mass";

        Mass(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum MassFlow implements EngineeringUnit {
        GRAMS_PER_SECOND(154, "grams per second", "g/s"),
        GRAMS_PER_MINUTE(155, "grams per minute", "g/min"),
        KILOGRAMS_PER_SECOND(42, "kilograms per second", "kg/s"),
        KILOGRAMS_PER_MINUTE(43, "kilograms per minute", "kg/min"),
        KILOGRAMS_PER_HOUR(44, "kilograms per hour", "kg/h"),
        POUNDS_MASS_PER_SECOND(119, "pounds mass per second", "lb/s"),
        POUNDS_MASS_PER_MINUTE(45, "pounds mass per minute", "lb/min"),
        POUNDS_MASS_PER_HOUR(46, "pounds mass per hour", "lb/h"),
        TONS_PER_HOUR(156, "tons per hour", "t/h");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.massFlow";

        MassFlow(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Power implements EngineeringUnit {
        MILLIWATTS(132, "milliwatts", "mW"),
        WATTS(47, "watts", "W"),
        KILOWATTS(48, "kilowatts", "kW"),
        MEGAWATTS(49, "megawatts", "MW"),
        BTUS_PER_HOUR(50, "btus per hour", "BTU/h"),
        KILO_BTUS_PER_HOUR(157, "kilo btus per hour", "kBTU/h"),
        HORSEPOWER(51, "horsepower", "hp"),
        TONS_REFRIGERATION(52, "tons refrigeration", "TR");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.power";

        Power(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Pressure implements EngineeringUnit {
        PASCALS(53, "pascals", "Pa"),
        HECTOPASCALS(133, "hectopascals", "hPa"),
        KILOPASCALS(54, "kilopascals", "kPa"),
        MILLIBARS(134, "millibars", "mbar"),
        BARS(55, "bars", "bar"),
        POUNDS_FORCE_PER_SQUARE_INCH(56, "pounds force per square inch", "psi"),
        CENTIMETERS_OF_WATER(57, "centimeters of water", "cm H\u2082O"),
        INCHES_OF_WATER(58, "inches of water", "in H\u2082O"),
        MILLIMETERS_OF_MERCURY(59, "millimeters of mercury", "mmHg"),
        CENTIMETERS_OF_MERCURY(60, "centimeters of mercury", "cmHg"),
        INCHES_OF_MERCURY(61, "inches of mercury", "inHg");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.pressure";

        Pressure(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Temperature implements EngineeringUnit {
        DEGREES_CELSIUS(62, "degrees celsius", "\u00B0C"),
        DEGREES_KELVIN(63, "degrees kelvin", "K"),
        DEGREES_KELVIN_PER_HOUR(181, "degrees kelvin per hour", "K/h"),
        DEGREES_KELVIN_PER_MINUTE(182, "degrees kelvin per minute", "K/min"),
        DEGREES_FAHRENHEIT(64, "degrees fahrenheit", "\u00B0F"),
        DEGREE_DAYS_CELSIUS(65, "degree days celsius", "\u00B0C d"),
        DEGREE_DAYS_FAHRENHEIT(66, "degree days fahrenheit", "\u00B0F d"),
        DELTA_DEGREES_FAHRENHEIT(120, "delta degrees fahrenheit", "\u0394\u00B0F"),
        DELTA_DEGREES_KELVIN(121, "delta degrees kelvin", "\u0394K");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.temperature";

        Temperature(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Time implements EngineeringUnit {
        YEARS(67, "years", "yr"),
        MONTHS(68, "months", "mo"),
        WEEKS(69, "weeks", "wk"),
        DAYS(70, "days", "d"),
        HOURS(71, "hours", "h"),
        MINUTES(72, "minutes", "min"),
        SECONDS(73, "seconds", "s"),
        HUNDREDTHS_SECONDS(158, "hundredths seconds", "cs"),
        MILLISECONDS(159, "milliseconds", "ms");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.time";

        Time(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Torque implements EngineeringUnit {
        NEWTON_METERS(160, "newton meters", "N\u00B7m");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.torque";

        Torque(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Velocity implements EngineeringUnit {
        MILLIMETERS_PER_SECOND(161, "millimeters per second", "mm/s"),
        MILLIMETERS_PER_MINUTE(162, "millimeters per minute", "mm/min"),
        METERS_PER_SECOND(74, "meters per second", "m/s"),
        METERS_PER_MINUTE(163, "meters per minute", "m/min"),
        METERS_PER_HOUR(164, "meters per hour", "m/h"),
        KILOMETERS_PER_HOUR(75, "kilometers per hour", "km/h"),
        FEET_PER_SECOND(76, "feet per second", "ft/s"),
        FEET_PER_MINUTE(77, "feet per minute", "ft/min"),
        MILES_PER_HOUR(78, "miles per hour", "mph");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.velocity";

        Velocity(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum Volume implements EngineeringUnit {
        CUBIC_FEET(79, "cubic feet", "ft\u00B3"),
        CUBIC_METERS(80, "cubic meters", "m\u00B3"),
        IMPERIAL_GALLONS(81, "imperial gallons", "imp gal"),
        LITERS(82, "liters", "L"),
        US_GALLONS(83, "us gallons", "US gal");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.volume";

        Volume(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public enum VolumetricFlow implements EngineeringUnit {
        CUBIC_FEET_PER_SECOND(142, "cubic feet per second", "ft\u00B3/s"),
        CUBIC_FEET_PER_MINUTE(84, "cubic feet per minute", "ft\u00B3/min"),
        CUBIC_METERS_PER_SECOND(85, "cubic meters per second", "m\u00B3/s"),
        CUBIC_METERS_PER_MINUTE(165, "cubic meters per minute", "m\u00B3/min"),
        CUBIC_METERS_PER_HOUR(135, "cubic meters per hour", "m\u00B3/h"),
        IMPERIAL_GALLONS_PER_MINUTE(86, "imperial gallons per minute", "imp gal/min"),
        LITERS_PER_SECOND(87, "liters per second", "L/s"),
        LITERS_PER_MINUTE(88, "liters per minute", "L/min"),
        LITERS_PER_HOUR(136, "liters per hour", "L/h"),
        US_GALLONS_PER_MINUTE(89, "us gallons per minute", "US gal/min");

        private final int value;
        private final String name;
        private final String suffix;

        public static final String KEY = "engUnitGroup.volumetricFlow";

        VolumetricFlow(int value, String name, String suffix) {
            this.value = value;
            this.name = name;
            this.suffix = suffix;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSuffix() {
            return "[" + suffix + "]";
        }

        @Override
        public String getKey() {
            return "engUnit." + getValue();
        }
    }

    public static List<EngineeringUnit> getUnits() {
        return getUnitsGroupByKey().values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static Map<String, List<EngineeringUnit>> getUnitsGroupByKey() {
        Map<String, List<EngineeringUnit>> result = new HashMap<>();

        result.put(EngineeringUnitsTypes.KEY, getOtherUnits());
        result.put(EngineeringUnitsTypes.Acceleration.KEY, getAccelerationUnits());
        result.put(EngineeringUnitsTypes.Area.KEY, getAreaUnits());
        result.put(EngineeringUnitsTypes.Currency.KEY, getCurrencyUnits());
        result.put(EngineeringUnitsTypes.Enthalpy.KEY, getEnthalpyUnits());
        result.put(EngineeringUnitsTypes.Entropy.KEY, getEntropyUnits());
        result.put(EngineeringUnitsTypes.Electrical.KEY, getElectricalUnits());
        result.put(EngineeringUnitsTypes.Humidity.KEY, getHumidityUnits());
        result.put(EngineeringUnitsTypes.Frequency.KEY, getFrequencyUnits());
        result.put(EngineeringUnitsTypes.Length.KEY, getLengthUnits());
        result.put(EngineeringUnitsTypes.Energy.KEY, getEnergyUnits());
        result.put(EngineeringUnitsTypes.Force.KEY, getForceUnits());
        result.put(EngineeringUnitsTypes.Light.KEY, getLightUnits());
        result.put(EngineeringUnitsTypes.Torque.KEY, getTorqueUnits());
        result.put(EngineeringUnitsTypes.Temperature.KEY, getTemperatureUnits());
        result.put(EngineeringUnitsTypes.Volume.KEY, getVolumeUnits());
        result.put(EngineeringUnitsTypes.Velocity.KEY, getVelocityUnits());
        result.put(EngineeringUnitsTypes.Mass.KEY, getMassUnits());
        result.put(EngineeringUnitsTypes.MassFlow.KEY, getMassFlowUnits());
        result.put(EngineeringUnitsTypes.VolumetricFlow.KEY, getVolumetricFlowUnits());
        result.put(EngineeringUnitsTypes.Power.KEY, getPowerUnits());
        result.put(EngineeringUnitsTypes.Pressure.KEY, getPressureUnits());
        result.put(EngineeringUnitsTypes.Time.KEY, getTimeUnits());

        return result;
    }

    public static List<EngineeringUnit> getTemperatureUnits() {
        return  Stream.of(EngineeringUnitsTypes.Temperature.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getTorqueUnits() {
        return  Stream.of(EngineeringUnitsTypes.Torque.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getVolumetricFlowUnits() {
        return  Stream.of(EngineeringUnitsTypes.VolumetricFlow.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getVolumeUnits() {
        return  Stream.of(EngineeringUnitsTypes.Volume.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getMassFlowUnits() {
        return  Stream.of(EngineeringUnitsTypes.MassFlow.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getLengthUnits() {
        return  Stream.of(EngineeringUnitsTypes.Length.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getHumidityUnits() {
        return  Stream.of(EngineeringUnitsTypes.Humidity.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getFrequencyUnits() {
        return  Stream.of(EngineeringUnitsTypes.Frequency.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getEntropyUnits() {
        return  Stream.of(EngineeringUnitsTypes.Entropy.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getEnthalpyUnits() {
        return  Stream.of(EngineeringUnitsTypes.Enthalpy.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getCurrencyUnits() {
        return  Stream.of(EngineeringUnitsTypes.Currency.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getAreaUnits() {
        return  Stream.of(EngineeringUnitsTypes.Area.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getAccelerationUnits() {
        return  Stream.of(EngineeringUnitsTypes.Acceleration.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getPowerUnits() {
        return  Stream.of(EngineeringUnitsTypes.Power.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getForceUnits() {
        return  Stream.of(EngineeringUnitsTypes.Force.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getLightUnits() {
        return  Stream.of(EngineeringUnitsTypes.Light.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getVelocityUnits() {
        return  Stream.of(EngineeringUnitsTypes.Velocity.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getTimeUnits() {
        return  Stream.of(EngineeringUnitsTypes.Time.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getPressureUnits() {
        return  Stream.of(EngineeringUnitsTypes.Pressure.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getElectricalUnits() {
        return  Stream.of(EngineeringUnitsTypes.Electrical.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getMassUnits() {
        return  Stream.of(EngineeringUnitsTypes.Mass.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getEnergyUnits() {
        return  Stream.of(EngineeringUnitsTypes.Energy.values())
                .collect(Collectors.toList());
    }

    public static List<EngineeringUnit> getOtherUnits() {
        return  Stream.of(EngineeringUnitsTypes.values())
                .collect(Collectors.toList());
    }

    public static EngineeringUnit valueOf(int value) {
        return getUnits().stream()
                .filter(unit -> unit.getValue() == value)
                .findAny()
                .orElse(NO_UNITS);
    }
}