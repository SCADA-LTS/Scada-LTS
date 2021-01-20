package com.serotonin.mango.util;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.joda.time.DateTime;

public final class IntervalUtil {

    private IntervalUtil() {}

    public static boolean isActiveByInterval(MailingList mailingList, EventInstance event) {
        DateTime dateTime = new DateTime(event.getActiveTimestamp());
        return !mailingList.getInactiveIntervals().contains(getIntervalIdAt(dateTime));
    }

    public static boolean isActiveByInterval(MailingList mailingList, DateTime fireTime) {
        return !mailingList.getInactiveIntervals().contains(getIntervalIdAt(fireTime));
    }

    public static int getIntervalIdAt(DateTime dt) {
        int interval = 0;
        interval += dt.getMinuteOfHour() / 15;
        interval += dt.getHourOfDay() * 4;
        interval += (dt.getDayOfWeek() - 1) * 96;
        return interval;
    }
}
