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
package com.serotonin.mango.util;

import org.joda.time.DateTime;

import com.serotonin.mango.Common;
import com.serotonin.mango.Common.TimePeriods;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class DateUtils {
    public static long minus(long time, int periodType, int periods) {
        return minus(new DateTime(time), periodType, periods).getMillis();
    }

    public static DateTime minus(DateTime time, int periodType, int periods) {
        return time.minus(Common.getPeriod(periodType, periods));
    }

    public static long truncate(long time, int periodType) {
        return truncateDateTime(new DateTime(time), periodType).getMillis();
    }

    public static DateTime truncateDateTime(DateTime time, int periodType) {
        if (periodType == TimePeriods.SECONDS)
            time = time.minus(time.getMillisOfSecond());
        else if (periodType == TimePeriods.MINUTES) {
            time = time.minus(time.getMillisOfSecond());
            time = time.minus(Common.getPeriod(TimePeriods.SECONDS, time.getSecondOfMinute()));
        }
        else if (periodType == TimePeriods.HOURS) {
            time = time.minus(time.getMillisOfSecond());
            time = time.minus(Common.getPeriod(TimePeriods.SECONDS, time.getSecondOfMinute()));
            time = time.minus(Common.getPeriod(TimePeriods.MINUTES, time.getMinuteOfHour()));
        }
        else if (periodType == TimePeriods.DAYS) {
            time = time.minus(time.getMillisOfDay());
        }
        else if (periodType == TimePeriods.WEEKS) {
            time = time.minus(time.getMillisOfDay());
            time = time.minus(Common.getPeriod(TimePeriods.DAYS, time.getDayOfWeek() - 1));
        }
        else if (periodType == TimePeriods.MONTHS) {
            time = time.minus(time.getMillisOfDay());
            time = time.minus(Common.getPeriod(TimePeriods.DAYS, time.getDayOfMonth() - 1));
        }
        else if (periodType == TimePeriods.YEARS) {
            time = time.minus(time.getMillisOfDay());
            time = time.minus(Common.getPeriod(TimePeriods.DAYS, time.getDayOfYear() - 1));
        }
        return time;
    }

    public static long next(long time, int periodType) {
        return minus(truncateDateTime(new DateTime(time), periodType), periodType, -1).getMillis();
    }

    public static LocalizableMessage getDuration(long duration) {
        if (duration < 1000)
            return new LocalizableMessage("common.duration.millis", duration);

        if (duration < 10000) {
            String s = "" + (duration / 1000) + '.';
            s += (int) (((double) (duration % 1000)) / 10 + 0.5);
            return new LocalizableMessage("common.duration.seconds", s);
        }

        if (duration < 60000) {
            String s = "" + (duration / 1000) + '.';
            s += (int) (((double) (duration % 1000)) / 100 + 0.5);
            return new LocalizableMessage("common.duration.seconds", s);
        }

        // Convert to seconds
        duration /= 1000;

        if (duration < 600)
            return new LocalizableMessage("common.duration.minSec", duration / 60, duration % 60);

        // Convert to minutes
        duration /= 60;

        if (duration < 60)
            return new LocalizableMessage("common.duration.minutes", duration);

        if (duration < 1440)
            return new LocalizableMessage("common.duration.hourMin", duration / 60, duration % 60);

        // Convert to hours
        duration /= 60;

        return new LocalizableMessage("common.duration.hours", duration);
    }

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        System.out.println(new DateTime(time));
        System.out.println(new DateTime(next(time, Common.TimePeriods.SECONDS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.MINUTES)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.HOURS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.DAYS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.WEEKS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.MONTHS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.YEARS)));
        System.out.println();

        time = next(time, Common.TimePeriods.YEARS);
        System.out.println(new DateTime(time));
        System.out.println(new DateTime(next(time, Common.TimePeriods.SECONDS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.MINUTES)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.HOURS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.DAYS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.WEEKS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.MONTHS)));
        System.out.println(new DateTime(next(time, Common.TimePeriods.YEARS)));
    }
}
