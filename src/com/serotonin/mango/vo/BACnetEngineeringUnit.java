package com.serotonin.mango.vo;

import java.util.HashMap;
import java.util.Map;

public enum BACnetEngineeringUnit {

    METERS_PER_SECOND_PER_SECOND(166, "meters per second per second"),
    SQUARE_METERS(0, "square meters"),
    SQUARE_CENTIMETERS(116, "square centimeters"),
    SQUARE_FEET(1, "square feet"),
    SQUARE_INCHES(115, "square inches"),
    CURRENCY1(105, "currency 1"),
    CURRENCY2(106, "currency 2"),
    CURRENCY3(107, "currency 3"),
    CURRENCY4(108, "currency 4"),
    CURRENCY5(109, "currency 5"),
    CURRENCY6(110, "currency 6"),
    CURRENCY7(111, "currency 7"),
    CURRENCY8(112, "currency 8"),
    CURRENCY9(113, "currency 9"),
    CURRENCY10(114, "currency 10"),
    MILLIAMPERES(2, "milliamperes"),
    AMPERES(3, "amperes"),
    AMPERES_PER_METER(167, "amperes per meter"),
    AMPERES_PER_SQUARE_METER(168, "amperes per square meter"),
    AMPERE_SQUARE_METERS(169, "ampere square meters"),
    FARADS(170, "farads"),
    HENRYS(171, "henrys"),
    OHMS(4, "ohms"),
    OHM_METERS(172, "ohm meters"),
    MILLIOHMS(145, "milliohms"),
    KILOHMS(122, "kilohms"),
    MEGOHMS(123, "megohms"),
    SIEMENS(173, "siemens"),
    SIEMENS_PER_METER(174, "siemens per meter"),
    TESLAS(175, "teslas"),
    VOLTS(5, "volts"),
    MILLIVOLTS(124, "millivolts"),
    KILOVOLTS(6, "kilovolts"),
    MEGAVOLTS(7, "megavolts"),
    VOLT_AMPERES(8, "volt amperes"),
    KILOVOLT_AMPERES(9, "kilovolt amperes"),
    MEGAVOLT_AMPERES(10, "megavolt amperes"),
    VOLT_AMPERES_REACTIVE(11, "volt amperes reactive"),
    KILOVOLT_AMPERES_REACTIVE(12, "kilovolt amperes reactive"),
    MEGAVOLT_AMPERES_REACTIVE(13, "megavolt amperes reactive"),
    VOLTS_PER_DEGREE_KELVIN(176, "volts per degree kelvin"),
    VOLTS_PER_METER(177, "volts per meter"),
    DEGREES_PHASE(14, "degrees phase"),
    POWER_FACTOR(15, "power factor"),
    WEBERS(178, "webers"),
    JOULES(16, "joules"),
    KILOJOULES(17, "kilojoules"),
    KILOJOULES_PER_KILOGRAM(125, "kilojoules per kilogram"),
    MEGAJOULES(126, "megajoules"),
    WATT_HOURS(18, "watt hours"),
    KILOWATT_HOURS(19, "kilowatt hours"),
    MEGAWATT_HOURS(146, "megawatt hours"),
    BTUS(20, "btus"),
    KILO_BTUS(147, "kilo btus"),
    MEGA_BTUS(148, "mega btus"),
    THERMS(21, "therms"),
    TON_HOURS(22, "ton hours"),
    JOULES_PER_KILOGRAM_DRY_AIR(23, "joules per kilogram dry air"),
    KILOJOULES_PER_KILOGRAM_DRY_AIR(149, "kilojoules per kilogram dry air"),
    MEGAJOULES_PER_KILOGRAM_DRY_AIR(150, "megajoules per kilogram dry air"),
    BTUS_PER_POUND_DRY_AIR(24, "btus per pound dry air"),
    BTUS_PER_POUND(117, "btus per pound"),
    JOULES_PER_DEGREE_KELVIN(127, "joules per degree kelvin"),
    KILOJOULES_PER_DEGREE_KELVIN(151, "kilojoules per degree kelvin"),
    MEGAJOULES_PER_DEGREE_KELVIN(152, "megajoules per degree kelvin"),
    JOULES_PER_KILOGRAM_DEGREE_KELVIN(128, "joules per kilogram degree kelvin"),
    NEWTON(153, "newton"),
    CYCLES_PER_HOUR(25, "cycles per hour"),
    CYCLES_PER_MINUTE(26, "cycles per minute"),
    HERTZ(27, "hertz"),
    KILOHERTZ(129, "kilohertz"),
    MEGAHERTZ(130, "megahertz"),
    PER_HOUR(131, "per hour"),
    GRAMS_OF_WATER_PER_KILOGRAM_DRY_AIR(28, "grams of water per kilogram dry air"),
    PERCENT_RELATIVE_HUMIDITY(29, "percent relative humidity"),
    MILLIMETERS(30, "millimeters"),
    CENTIMETERS(118, "centimeters"),
    METERS(31, "meters"),
    INCHES(32, "inches"),
    FEET(33, "feet"),
    CANDELAS(179, "candelas"),
    CANDELAS_PER_SQUARE_METER(180, "candelas per square meter"),
    WATTS_PER_SQUARE_FOOT(34, "watts per square foot"),
    WATTS_PER_SQUARE_METER(35, "watts per square meter"),
    LUMENS(36, "lumens"),
    LUXES(37, "luxes"),
    FOOT_CANDLES(38, "foot candles"),
    KILOGRAMS(39, "kilograms"),
    POUNDS_MASS(40, "pounds mass"),
    TONS(41, "tons"),
    GRAMS_PER_SECOND(154, "grams per second"),
    GRAMS_PER_MINUTE(155, "grams per minute"),
    KILOGRAMS_PER_SECOND(42, "kilograms per second"),
    KILOGRAMS_PER_MINUTE(43, "kilograms per minute"),
    KILOGRAMS_PER_HOUR(44, "kilograms per hour"),
    POUNDS_MASS_PER_SECOND(119, "pounds mass per second"),
    POUNDS_MASS_PER_MINUTE(45, "pounds mass per minute"),
    POUNDS_MASS_PER_HOUR(46, "pounds mass per hour"),
    TONS_PER_HOUR(156, "tons per hour"),
    MILLIWATTS(132, "milliwatts"),
    WATTS(47, "watts"),
    KILOWATTS(48, "kilowatts"),
    MEGAWATTS(49, "megawatts"),
    BTUS_PER_HOUR(50, "btus per hour"),
    KILO_BTUS_PER_HOUR(157, "kilo btus per hour"),
    HORSEPOWER(51, "horsepower"),
    TONS_REFRIGERATION(52, "tons refrigeration"),
    PASCALS(53, "pascals"),
    HECTOPASCALS(133, "hectopascals"),
    KILOPASCALS(54, "kilopascals"),
    MILLIBARS(134, "millibars"),
    BARS(55, "bars"),
    POUNDS_FORCE_PER_SQUARE_INCH(56, "pounds force per square inch"),
    CENTIMETERS_OF_WATER(57, "centimeters of water"),
    INCHES_OF_WATER(58, "inches of water"),
    MILLIMETERS_OF_MERCURY(59, "millimeters of mercury"),
    CENTIMETERS_OF_MERCURY(60, "centimeters of mercury"),
    INCHES_OF_MERCURY(61, "inches of mercury"),
    DEGREES_CELSIUS(62, "degrees celsius"),
    DEGREES_KELVIN(63, "degrees kelvin"),
    DEGREES_KELVIN_PER_HOUR(181, "degrees kelvin per hour"),
    DEGREES_KELVIN_PER_MINUTE(182, "degrees kelvin per minute"),
    DEGREES_FAHRENHEIT(64, "degrees fahrenheit"),
    DEGREE_DAYS_CELSIUS(65, "degree days celsius"),
    DEGREE_DAYS_FAHRENHEIT(66, "degree days fahrenheit"),
    DELTA_DEGREES_FAHRENHEIT(120, "delta degrees fahrenheit"),
    DELTA_DEGREES_KELVIN(121, "delta degrees kelvin"),
    YEARS(67, "years"),
    MONTHS(68, "months"),
    WEEKS(69, "weeks"),
    DAYS(70, "days"),
    HOURS(71, "hours"),
    MINUTES(72, "minutes"),
    SECONDS(73, "seconds"),
    HUNDREDTHS_SECONDS(158, "hundredths seconds"),
    MILLISECONDS(159, "milliseconds"),
    NEWTON_METERS(160, "newton meters"),
    MILLIMETERS_PER_SECOND(161, "millimeters per second"),
    MILLIMETERS_PER_MINUTE(162, "millimeters per minute"),
    METERS_PER_SECOND(74, "meters per second"),
    METERS_PER_MINUTE(163, "meters per minute"),
    METERS_PER_HOUR(164, "meters per hour"),
    KILOMETERS_PER_HOUR(75, "kilometers per hour"),
    FEET_PER_SECOND(76, "feet per second"),
    FEET_PER_MINUTE(77, "feet per minute"),
    MILES_PER_HOUR(78, "miles per hour"),
    CUBIC_FEET(79, "cubic feet"),
    CUBIC_METERS(80, "cubic meters"),
    IMPERIAL_GALLONS(81, "imperial gallons"),
    LITERS(82, "liters"),
    US_GALLONS(83, "us gallons"),
    CUBIC_FEET_PER_SECOND(142, "cubic feet per second"),
    CUBIC_FEET_PER_MINUTE(84, "cubic feet per minute"),
    CUBIC_METERS_PER_SECOND(85, "cubic meters per second"),
    CUBIC_METERS_PER_MINUTE(165, "cubic meters per minute"),
    CUBIC_METERS_PER_HOUR(135, "cubic meters per hour"),
    IMPERIAL_GALLONS_PER_MINUTE(86, "imperial gallons per minute"),
    LITERS_PER_SECOND(87, "liters per second"),
    LITERS_PER_MINUTE(88, "liters per minute"),
    LITERS_PER_HOUR(136, "liters per hour"),
    US_GALLONS_PER_MINUTE(89, "us gallons per minute"),
    DEGREES_ANGULAR(90, "degrees angular"),
    DEGREES_CELSIUS_PER_HOUR(91, "degrees celsius per hour"),
    DEGREES_CELSIUS_PER_MINUTE(92, "degrees celsius per minute"),
    DEGREES_FAHRENHEIT_PER_HOUR(93, "degrees fahrenheit per hour"),
    DEGREES_FAHRENHEIT_PER_MINUTE(94, "degrees fahrenheit per minute"),
    JOULE_SECONDS(183, "joule seconds"),
    KILOGRAMS_PER_CUBIC_METER(186, "kilograms per cubic meter"),
    KILOWATT_HOURS_PER_SQUARE_METER(137, "kilowatt hours per square meter"),
    KILOWATT_HOURS_PER_SQUARE_FOOT(138, "kilowatt hours per square foot"),
    MEGAJOULES_PER_SQUARE_METER(139, "megajoules per square meter"),
    MEGAJOULES_PER_SQUARE_FOOT(140, "megajoules per square foot"),
    NO_UNITS(95, ""), // toString() = ""
    NEWTON_SECONDS(187, "newton seconds"),
    NEWTONS_PER_METER(188, "newtons per meter"),
    PARTS_PER_MILLION(96, "parts per million"),
    PARTS_PER_BILLION(97, "parts per billion"),
    PERCENT(98, "percent"),
    PERCENT_OBSCURATION_PER_FOOT(143, "percent obscuration per foot"),
    PERCENT_OBSCURATION_PER_METER(144, "percent obscuration per meter"),
    PERCENT_PER_SECOND(99, "percent per second"),
    PER_MINUTE(100, "per minute"),
    PER_SECOND(101, "per second"),
    PSI_PER_DEGREE_FAHRENHEIT(102, "psi per degree fahrenheit"),
    RADIANS(103, "radians"),
    RADIANS_PER_SECOND(184, "radians per second"),
    REVOLUTIONS_PER_MINUTE(104, "revolutions per minute"),
    SQUARE_METERS_PER_NEWTON(185, "square meters perNewton"),
    WATTS_PER_METER_PER_DEGREE_KELVIN(189, "watts per meter per degree kelvin"),
    WATTS_PER_SQUARE_METER_DEGREE_KELVIN(141, "watts per square meter degree kelvin");

    private final int code;
    private final String label;

    private static final Map<Integer, BACnetEngineeringUnit> byCodeMap = new HashMap<>();
    static {
        for (BACnetEngineeringUnit eu : values())
            byCodeMap.put(eu.code, eu);
    }

    BACnetEngineeringUnit(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static BACnetEngineeringUnit fromCode(int code) {
        return byCodeMap.getOrDefault(code, NO_UNITS);
    }

    public static BACnetEngineeringUnit fromLabel(String label) {
        for (BACnetEngineeringUnit eu : values()) {
            if (eu.label.equalsIgnoreCase(label)) {
                return eu;
            }
        }
        return NO_UNITS;
    }

    @Override
    public String toString() {
        return label;
    }
}
