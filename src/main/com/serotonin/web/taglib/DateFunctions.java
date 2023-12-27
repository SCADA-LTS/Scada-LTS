package com.serotonin.web.taglib;

import com.serotonin.util.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateFunctions {
    public static DateTimeFormatter dtfFullMinute = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
    public static DateTimeFormatter dtfFullSecond = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
    public static DateTimeFormatter dtfLong = DateTimeFormat.forPattern("yyyy/MM/dd");
    public static DateTimeFormatter dtfMed = DateTimeFormat.forPattern("MMM dd HH:mm");
    public static DateTimeFormatter dtfShort = DateTimeFormat.forPattern("HH:mm:ss");

    public DateFunctions() {
    }

    public static String getTime(long time) {
        DateTime valueTime = new DateTime(time);
        DateTime now = new DateTime();
        if (valueTime.getYear() != now.getYear()) {
            return dtfLong.print(valueTime);
        } else {
            return valueTime.getMonthOfYear() == now.getMonthOfYear() && valueTime.getDayOfMonth() == now.getDayOfMonth() ? dtfShort.print(valueTime) : dtfMed.print(valueTime);
        }
    }

    public static String getFullSecondTime(long time) {
        return dtfFullSecond.print(new DateTime(time));
    }

    public static String getFullMinuteTime(long time) {
        return dtfFullMinute.print(new DateTime(time));
    }

    public static String getDuration(long duration) {
        return StringUtils.durationToString(duration);
    }
}
