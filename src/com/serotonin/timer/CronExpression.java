package com.serotonin.timer;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Significant portions of this code (i.e. most of it) were lifted from the Quartz scheduling library for Java.
 * 
 * @see http://www.quartz-scheduler.org/api/2.1.0/
 * 
 * @author Matthew Lohbihler (edits to make compatible with this package)
 */
public class CronExpression implements Cloneable {
    protected static final int YEAR_AT_WHICH_TO_GIVE_UP_SCHEDULING = 2299;

    protected static final int SECOND = 0;
    protected static final int MINUTE = 1;
    protected static final int HOUR = 2;
    protected static final int DAY_OF_MONTH = 3;
    protected static final int MONTH = 4;
    protected static final int DAY_OF_WEEK = 5;
    protected static final int YEAR = 6;
    protected static final int ALL_SPEC_INT = 99; // '*'
    protected static final int NO_SPEC_INT = 98; // '?'
    protected static final Integer ALL_SPEC = new Integer(ALL_SPEC_INT);
    protected static final Integer NO_SPEC = new Integer(NO_SPEC_INT);

    protected static final Map<String, Integer> monthMap = new HashMap<String, Integer>(20);
    protected static final Map<String, Integer> dayMap = new HashMap<String, Integer>(60);
    static {
        monthMap.put("JAN", new Integer(0));
        monthMap.put("FEB", new Integer(1));
        monthMap.put("MAR", new Integer(2));
        monthMap.put("APR", new Integer(3));
        monthMap.put("MAY", new Integer(4));
        monthMap.put("JUN", new Integer(5));
        monthMap.put("JUL", new Integer(6));
        monthMap.put("AUG", new Integer(7));
        monthMap.put("SEP", new Integer(8));
        monthMap.put("OCT", new Integer(9));
        monthMap.put("NOV", new Integer(10));
        monthMap.put("DEC", new Integer(11));

        dayMap.put("SUN", new Integer(1));
        dayMap.put("MON", new Integer(2));
        dayMap.put("TUE", new Integer(3));
        dayMap.put("WED", new Integer(4));
        dayMap.put("THU", new Integer(5));
        dayMap.put("FRI", new Integer(6));
        dayMap.put("SAT", new Integer(7));
    }

    private String cronExpression = null;
    private TimeZone timeZone = null;
    protected transient TreeSet<Integer> seconds;
    protected transient TreeSet<Integer> minutes;
    protected transient TreeSet<Integer> hours;
    protected transient TreeSet<Integer> daysOfMonth;
    protected transient TreeSet<Integer> months;
    protected transient TreeSet<Integer> daysOfWeek;
    protected transient TreeSet<Integer> years;

    protected transient boolean lastdayOfWeek = false;
    protected transient int nthdayOfWeek = 0;
    protected transient boolean lastdayOfMonth = false;
    protected transient boolean nearestWeekday = false;
    protected transient boolean expressionParsed = false;

    /**
     * Constructs a new <CODE>CronExpression</CODE> based on the specified parameter.
     * 
     * @param cronExpression
     *            String representation of the cron expression the new object should represent
     * @throws java.text.ParseException
     *             if the string expression cannot be parsed into a valid <CODE>CronExpression</CODE>
     */
    public CronExpression(String cronExpression) throws ParseException {
        if (cronExpression == null) {
            throw new IllegalArgumentException("cronExpression cannot be null");
        }

        this.cronExpression = cronExpression.toUpperCase(Locale.US);

        buildExpression(this.cronExpression);
    }

    /**
     * Indicates whether the given date satisfies the cron expression. Note that milliseconds are ignored, so two Dates
     * falling on different milliseconds of the same second will always have the same result here.
     * 
     * @param date
     *            the date to evaluate
     * @return a boolean indicating whether the given date satisfies the cron expression
     */
    public boolean isSatisfiedBy(Date date) {
        Calendar testDateCal = Calendar.getInstance(getTimeZone());
        testDateCal.setTime(date);
        testDateCal.set(Calendar.MILLISECOND, 0);
        Date originalDate = testDateCal.getTime();

        testDateCal.add(Calendar.SECOND, -1);

        Date timeAfter = getTimeAfter(testDateCal.getTime());

        return ((timeAfter != null) && (timeAfter.equals(originalDate)));
    }

    /**
     * Returns the next date/time <I>after</I> the given date/time which satisfies the cron expression.
     * 
     * @param date
     *            the date/time at which to begin the search for the next valid date/time
     * @return the next valid date/time
     */
    public Date getNextValidTimeAfter(Date date) {
        return getTimeAfter(date);
    }

    /**
     * Returns the next date/time <I>after</I> the given date/time which does <I>not</I> satisfy the expression
     * 
     * @param date
     *            the date/time at which to begin the search for the next invalid date/time
     * @return the next valid date/time
     */
    public Date getNextInvalidTimeAfter(Date date) {
        long difference = 1000;

        // move back to the nearest second so differences will be accurate
        Calendar adjustCal = Calendar.getInstance(getTimeZone());
        adjustCal.setTime(date);
        adjustCal.set(Calendar.MILLISECOND, 0);
        Date lastDate = adjustCal.getTime();

        Date newDate = null;

        // TODO: (QUARTZ-481) IMPROVE THIS! The following is a BAD solution to this problem. Performance will be very
        // bad here, depending on the cron expression. It is, however A solution.

        // keep getting the next included time until it's farther than one second
        // apart. At that point, lastDate is the last valid fire time. We return
        // the second immediately following it.
        while (difference == 1000) {
            newDate = getTimeAfter(lastDate);

            difference = newDate.getTime() - lastDate.getTime();

            if (difference == 1000) {
                lastDate = newDate;
            }
        }

        return new Date(lastDate.getTime() + 1000);
    }

    /**
     * Returns the time zone for which this <code>CronExpression</code> will be resolved.
     */
    public TimeZone getTimeZone() {
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }

        return timeZone;
    }

    /**
     * Sets the time zone for which this <code>CronExpression</code> will be resolved.
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Returns the string representation of the <CODE>CronExpression</CODE>
     * 
     * @return a string representation of the <CODE>CronExpression</CODE>
     */
    @Override
    public String toString() {
        return cronExpression;
    }

    /**
     * Indicates whether the specified cron expression can be parsed into a valid cron expression
     * 
     * @param cronExpression
     *            the expression to evaluate
     * @return a boolean indicating whether the given expression is a valid cron expression
     */
    public static boolean isValidExpression(String cronExpression) {

        try {
            new CronExpression(cronExpression);
        }
        catch (ParseException pe) {
            return false;
        }

        return true;
    }

    // //////////////////////////////////////////////////////////////////////////
    //
    // Expression Parsing Functions
    //
    // //////////////////////////////////////////////////////////////////////////

    protected void buildExpression(String expression) throws ParseException {
        expressionParsed = true;

        try {

            if (seconds == null) {
                seconds = new TreeSet<Integer>();
            }
            if (minutes == null) {
                minutes = new TreeSet<Integer>();
            }
            if (hours == null) {
                hours = new TreeSet<Integer>();
            }
            if (daysOfMonth == null) {
                daysOfMonth = new TreeSet<Integer>();
            }
            if (months == null) {
                months = new TreeSet<Integer>();
            }
            if (daysOfWeek == null) {
                daysOfWeek = new TreeSet<Integer>();
            }
            if (years == null) {
                years = new TreeSet<Integer>();
            }

            int exprOn = SECOND;

            StringTokenizer exprsTok = new StringTokenizer(expression, " \t", false);

            while (exprsTok.hasMoreTokens() && exprOn <= YEAR) {
                String expr = exprsTok.nextToken().trim();

                // throw an exception if L is used with other days of the month
                if (exprOn == DAY_OF_MONTH && expr.indexOf('L') != -1 && expr.length() > 1 && expr.indexOf(",") >= 0) {
                    throw new ParseException(
                            "Support for specifying 'L' and 'LW' with other days of the month is not implemented", -1);
                }
                // throw an exception if L is used with other days of the week
                if (exprOn == DAY_OF_WEEK && expr.indexOf('L') != -1 && expr.length() > 1 && expr.indexOf(",") >= 0) {
                    throw new ParseException(
                            "Support for specifying 'L' with other days of the week is not implemented", -1);
                }

                StringTokenizer vTok = new StringTokenizer(expr, ",");
                while (vTok.hasMoreTokens()) {
                    String v = vTok.nextToken();
                    storeExpressionVals(0, v, exprOn);
                }

                exprOn++;
            }

            if (exprOn <= DAY_OF_WEEK) {
                throw new ParseException("Unexpected end of expression.", expression.length());
            }

            if (exprOn <= YEAR) {
                storeExpressionVals(0, "*", YEAR);
            }

            TreeSet<Integer> dow = getSet(DAY_OF_WEEK);
            TreeSet<Integer> dom = getSet(DAY_OF_MONTH);

            // Copying the logic from the UnsupportedOperationException below
            boolean dayOfMSpec = !dom.contains(NO_SPEC);
            boolean dayOfWSpec = !dow.contains(NO_SPEC);

            if (dayOfMSpec && !dayOfWSpec) {
                // skip
            }
            else if (dayOfWSpec && !dayOfMSpec) {
                // skip
            }
            else {
                throw new ParseException(
                        "Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.", 0);
            }
        }
        catch (ParseException pe) {
            throw pe;
        }
        catch (Exception e) {
            throw new ParseException("Illegal cron expression format (" + e.toString() + ")", 0);
        }
    }

    protected int storeExpressionVals(int pos, String s, int type) throws ParseException {

        int incr = 0;
        int i = skipWhiteSpace(pos, s);
        if (i >= s.length()) {
            return i;
        }
        char c = s.charAt(i);
        if ((c >= 'A') && (c <= 'Z') && (!s.equals("L")) && (!s.equals("LW"))) {
            String sub = s.substring(i, i + 3);
            int sval = -1;
            int eval = -1;
            if (type == MONTH) {
                sval = getMonthNumber(sub) + 1;
                if (sval <= 0) {
                    throw new ParseException("Invalid Month value: '" + sub + "'", i);
                }
                if (s.length() > i + 3) {
                    c = s.charAt(i + 3);
                    if (c == '-') {
                        i += 4;
                        sub = s.substring(i, i + 3);
                        eval = getMonthNumber(sub) + 1;
                        if (eval <= 0) {
                            throw new ParseException("Invalid Month value: '" + sub + "'", i);
                        }
                    }
                }
            }
            else if (type == DAY_OF_WEEK) {
                sval = getDayOfWeekNumber(sub);
                if (sval < 0) {
                    throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", i);
                }
                if (s.length() > i + 3) {
                    c = s.charAt(i + 3);
                    if (c == '-') {
                        i += 4;
                        sub = s.substring(i, i + 3);
                        eval = getDayOfWeekNumber(sub);
                        if (eval < 0) {
                            throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", i);
                        }
                    }
                    else if (c == '#') {
                        try {
                            i += 4;
                            nthdayOfWeek = Integer.parseInt(s.substring(i));
                            if (nthdayOfWeek < 1 || nthdayOfWeek > 5) {
                                throw new Exception();
                            }
                        }
                        catch (Exception e) {
                            throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
                        }
                    }
                    else if (c == 'L') {
                        lastdayOfWeek = true;
                        i++;
                    }
                }

            }
            else {
                throw new ParseException("Illegal characters for this position: '" + sub + "'", i);
            }
            if (eval != -1) {
                incr = 1;
            }
            addToSet(sval, eval, incr, type);
            return (i + 3);
        }

        if (c == '?') {
            i++;
            if ((i + 1) < s.length() && (s.charAt(i) != ' ' && s.charAt(i + 1) != '\t')) {
                throw new ParseException("Illegal character after '?': " + s.charAt(i), i);
            }
            if (type != DAY_OF_WEEK && type != DAY_OF_MONTH) {
                throw new ParseException("'?' can only be specfied for Day-of-Month or Day-of-Week.", i);
            }
            if (type == DAY_OF_WEEK && !lastdayOfMonth) {
                int val = (daysOfMonth.last()).intValue();
                if (val == NO_SPEC_INT) {
                    throw new ParseException("'?' can only be specfied for Day-of-Month -OR- Day-of-Week.", i);
                }
            }

            addToSet(NO_SPEC_INT, -1, 0, type);
            return i;
        }

        if (c == '*' || c == '/') {
            if (c == '*' && (i + 1) >= s.length()) {
                addToSet(ALL_SPEC_INT, -1, incr, type);
                return i + 1;
            }
            else if (c == '/' && ((i + 1) >= s.length() || s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '\t')) {
                throw new ParseException("'/' must be followed by an integer.", i);
            }
            else if (c == '*') {
                i++;
            }
            c = s.charAt(i);
            if (c == '/') { // is an increment specified?
                i++;
                if (i >= s.length()) {
                    throw new ParseException("Unexpected end of string.", i);
                }

                incr = getNumericValue(s, i);

                i++;
                if (incr > 10) {
                    i++;
                }
                if (incr > 59 && (type == SECOND || type == MINUTE)) {
                    throw new ParseException("Increment > 60 : " + incr, i);
                }
                else if (incr > 23 && (type == HOUR)) {
                    throw new ParseException("Increment > 24 : " + incr, i);
                }
                else if (incr > 31 && (type == DAY_OF_MONTH)) {
                    throw new ParseException("Increment > 31 : " + incr, i);
                }
                else if (incr > 7 && (type == DAY_OF_WEEK)) {
                    throw new ParseException("Increment > 7 : " + incr, i);
                }
                else if (incr > 12 && (type == MONTH)) {
                    throw new ParseException("Increment > 12 : " + incr, i);
                }
            }
            else {
                incr = 1;
            }

            addToSet(ALL_SPEC_INT, -1, incr, type);
            return i;
        }
        else if (c == 'L') {
            i++;
            if (type == DAY_OF_MONTH) {
                lastdayOfMonth = true;
            }
            if (type == DAY_OF_WEEK) {
                addToSet(7, 7, 0, type);
            }
            if (type == DAY_OF_MONTH && s.length() > i) {
                c = s.charAt(i);
                if (c == 'W') {
                    nearestWeekday = true;
                    i++;
                }
            }
            return i;
        }
        else if (c >= '0' && c <= '9') {
            int val = Integer.parseInt(String.valueOf(c));
            i++;
            if (i >= s.length()) {
                addToSet(val, -1, -1, type);
            }
            else {
                c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    ValueSet vs = getValue(val, s, i);
                    val = vs.value;
                    i = vs.pos;
                }
                i = checkNext(i, s, val, type);
                return i;
            }
        }
        else {
            throw new ParseException("Unexpected character: " + c, i);
        }

        return i;
    }

    protected int checkNext(int pos, String s, int val, int type) throws ParseException {

        int end = -1;
        int i = pos;

        if (i >= s.length()) {
            addToSet(val, end, -1, type);
            return i;
        }

        char c = s.charAt(pos);

        if (c == 'L') {
            if (type == DAY_OF_WEEK) {
                lastdayOfWeek = true;
            }
            else {
                throw new ParseException("'L' option is not valid here. (pos=" + i + ")", i);
            }
            TreeSet<Integer> set = getSet(type);
            set.add(new Integer(val));
            i++;
            return i;
        }

        if (c == 'W') {
            if (type == DAY_OF_MONTH) {
                nearestWeekday = true;
            }
            else {
                throw new ParseException("'W' option is not valid here. (pos=" + i + ")", i);
            }
            TreeSet<Integer> set = getSet(type);
            set.add(new Integer(val));
            i++;
            return i;
        }

        if (c == '#') {
            if (type != DAY_OF_WEEK) {
                throw new ParseException("'#' option is not valid here. (pos=" + i + ")", i);
            }
            i++;
            try {
                nthdayOfWeek = Integer.parseInt(s.substring(i));
                if (nthdayOfWeek < 1 || nthdayOfWeek > 5) {
                    throw new Exception();
                }
            }
            catch (Exception e) {
                throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
            }

            TreeSet<Integer> set = getSet(type);
            set.add(new Integer(val));
            i++;
            return i;
        }

        if (c == '-') {
            i++;
            c = s.charAt(i);
            int v = Integer.parseInt(String.valueOf(c));
            end = v;
            i++;
            if (i >= s.length()) {
                addToSet(val, end, 1, type);
                return i;
            }
            c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                ValueSet vs = getValue(v, s, i);
                int v1 = vs.value;
                end = v1;
                i = vs.pos;
            }
            if (i < s.length() && ((c = s.charAt(i)) == '/')) {
                i++;
                c = s.charAt(i);
                int v2 = Integer.parseInt(String.valueOf(c));
                i++;
                if (i >= s.length()) {
                    addToSet(val, end, v2, type);
                    return i;
                }
                c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    ValueSet vs = getValue(v2, s, i);
                    int v3 = vs.value;
                    addToSet(val, end, v3, type);
                    i = vs.pos;
                    return i;
                }

                addToSet(val, end, v2, type);
                return i;
            }

            addToSet(val, end, 1, type);
            return i;
        }

        if (c == '/') {
            i++;
            c = s.charAt(i);
            int v2 = Integer.parseInt(String.valueOf(c));
            i++;
            if (i >= s.length()) {
                addToSet(val, end, v2, type);
                return i;
            }
            c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                ValueSet vs = getValue(v2, s, i);
                int v3 = vs.value;
                addToSet(val, end, v3, type);
                i = vs.pos;
                return i;
            }

            throw new ParseException("Unexpected character '" + c + "' after '/'", i);
        }

        addToSet(val, end, 0, type);
        i++;
        return i;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public String getExpressionSummary() {
        StringBuffer buf = new StringBuffer();

        buf.append("seconds: ");
        buf.append(getExpressionSetSummary(seconds));
        buf.append("\n");
        buf.append("minutes: ");
        buf.append(getExpressionSetSummary(minutes));
        buf.append("\n");
        buf.append("hours: ");
        buf.append(getExpressionSetSummary(hours));
        buf.append("\n");
        buf.append("daysOfMonth: ");
        buf.append(getExpressionSetSummary(daysOfMonth));
        buf.append("\n");
        buf.append("months: ");
        buf.append(getExpressionSetSummary(months));
        buf.append("\n");
        buf.append("daysOfWeek: ");
        buf.append(getExpressionSetSummary(daysOfWeek));
        buf.append("\n");
        buf.append("lastdayOfWeek: ");
        buf.append(lastdayOfWeek);
        buf.append("\n");
        buf.append("nearestWeekday: ");
        buf.append(nearestWeekday);
        buf.append("\n");
        buf.append("NthDayOfWeek: ");
        buf.append(nthdayOfWeek);
        buf.append("\n");
        buf.append("lastdayOfMonth: ");
        buf.append(lastdayOfMonth);
        buf.append("\n");
        buf.append("years: ");
        buf.append(getExpressionSetSummary(years));
        buf.append("\n");

        return buf.toString();
    }

    protected String getExpressionSetSummary(Set<Integer> set) {

        if (set.contains(NO_SPEC)) {
            return "?";
        }
        if (set.contains(ALL_SPEC)) {
            return "*";
        }

        StringBuffer buf = new StringBuffer();

        Iterator<Integer> itr = set.iterator();
        boolean first = true;
        while (itr.hasNext()) {
            Integer iVal = itr.next();
            String val = iVal.toString();
            if (!first) {
                buf.append(",");
            }
            buf.append(val);
            first = false;
        }

        return buf.toString();
    }

    protected String getExpressionSetSummary(List<Integer> list) {

        if (list.contains(NO_SPEC)) {
            return "?";
        }
        if (list.contains(ALL_SPEC)) {
            return "*";
        }

        StringBuffer buf = new StringBuffer();

        Iterator<Integer> itr = list.iterator();
        boolean first = true;
        while (itr.hasNext()) {
            Integer iVal = itr.next();
            String val = iVal.toString();
            if (!first) {
                buf.append(",");
            }
            buf.append(val);
            first = false;
        }

        return buf.toString();
    }

    protected int skipWhiteSpace(int i, String s) {
        for (; i < s.length() && (s.charAt(i) == ' ' || s.charAt(i) == '\t'); i++) {
            ;
        }

        return i;
    }

    protected int findNextWhiteSpace(int i, String s) {
        for (; i < s.length() && (s.charAt(i) != ' ' || s.charAt(i) != '\t'); i++) {
            ;
        }

        return i;
    }

    protected void addToSet(int val, int end, int incr, int type) throws ParseException {

        TreeSet<Integer> set = getSet(type);

        if (type == SECOND || type == MINUTE) {
            if ((val < 0 || val > 59 || end > 59) && (val != ALL_SPEC_INT)) {
                throw new ParseException("Minute and Second values must be between 0 and 59", -1);
            }
        }
        else if (type == HOUR) {
            if ((val < 0 || val > 23 || end > 23) && (val != ALL_SPEC_INT)) {
                throw new ParseException("Hour values must be between 0 and 23", -1);
            }
        }
        else if (type == DAY_OF_MONTH) {
            if ((val < 1 || val > 31 || end > 31) && (val != ALL_SPEC_INT) && (val != NO_SPEC_INT)) {
                throw new ParseException("Day of month values must be between 1 and 31", -1);
            }
        }
        else if (type == MONTH) {
            if ((val < 1 || val > 12 || end > 12) && (val != ALL_SPEC_INT)) {
                throw new ParseException("Month values must be between 1 and 12", -1);
            }
        }
        else if (type == DAY_OF_WEEK) {
            if ((val == 0 || val > 7 || end > 7) && (val != ALL_SPEC_INT) && (val != NO_SPEC_INT)) {
                throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
            }
        }

        if ((incr == 0 || incr == -1) && val != ALL_SPEC_INT) {
            if (val != -1) {
                set.add(new Integer(val));
            }
            else {
                set.add(NO_SPEC);
            }

            return;
        }

        int startAt = val;
        int stopAt = end;

        if (val == ALL_SPEC_INT && incr <= 0) {
            incr = 1;
            set.add(ALL_SPEC); // put in a marker, but also fill values
        }

        if (type == SECOND || type == MINUTE) {
            if (stopAt == -1) {
                stopAt = 59;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 0;
            }
        }
        else if (type == HOUR) {
            if (stopAt == -1) {
                stopAt = 23;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 0;
            }
        }
        else if (type == DAY_OF_MONTH) {
            if (stopAt == -1) {
                stopAt = 31;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1;
            }
        }
        else if (type == MONTH) {
            if (stopAt == -1) {
                stopAt = 12;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1;
            }
        }
        else if (type == DAY_OF_WEEK) {
            if (stopAt == -1) {
                stopAt = 7;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1;
            }
        }
        else if (type == YEAR) {
            if (stopAt == -1) {
                stopAt = YEAR_AT_WHICH_TO_GIVE_UP_SCHEDULING;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1970;
            }
        }

        // if the end of the range is before the start, then we need to overflow into
        // the next day, month etc. This is done by adding the maximum amount for that
        // type, and using modulus max to determine the value being added.
        int max = -1;
        if (stopAt < startAt) {
            switch (type) {
            case SECOND:
                max = 60;
                break;
            case MINUTE:
                max = 60;
                break;
            case HOUR:
                max = 24;
                break;
            case MONTH:
                max = 12;
                break;
            case DAY_OF_WEEK:
                max = 7;
                break;
            case DAY_OF_MONTH:
                max = 31;
                break;
            case YEAR:
                throw new IllegalArgumentException("Start year must be less than stop year");
            default:
                throw new IllegalArgumentException("Unexpected type encountered");
            }
            stopAt += max;
        }

        for (int i = startAt; i <= stopAt; i += incr) {
            if (max == -1) {
                // ie: there's no max to overflow over
                set.add(new Integer(i));
            }
            else {
                // take the modulus to get the real value
                int i2 = i % max;

                // 1-indexed ranges should not include 0, and should include their max
                if (i2 == 0 && (type == MONTH || type == DAY_OF_WEEK || type == DAY_OF_MONTH)) {
                    i2 = max;
                }

                set.add(new Integer(i2));
            }
        }
    }

    protected TreeSet<Integer> getSet(int type) {
        switch (type) {
        case SECOND:
            return seconds;
        case MINUTE:
            return minutes;
        case HOUR:
            return hours;
        case DAY_OF_MONTH:
            return daysOfMonth;
        case MONTH:
            return months;
        case DAY_OF_WEEK:
            return daysOfWeek;
        case YEAR:
            return years;
        default:
            return null;
        }
    }

    protected ValueSet getValue(int v, String s, int i) {
        char c = s.charAt(i);
        String s1 = String.valueOf(v);
        while (c >= '0' && c <= '9') {
            s1 += c;
            i++;
            if (i >= s.length()) {
                break;
            }
            c = s.charAt(i);
        }
        ValueSet val = new ValueSet();

        val.pos = (i < s.length()) ? i : i + 1;
        val.value = Integer.parseInt(s1);
        return val;
    }

    protected int getNumericValue(String s, int i) {
        int endOfVal = findNextWhiteSpace(i, s);
        String val = s.substring(i, endOfVal);
        return Integer.parseInt(val);
    }

    protected int getMonthNumber(String s) {
        Integer integer = monthMap.get(s);

        if (integer == null) {
            return -1;
        }

        return integer.intValue();
    }

    protected int getDayOfWeekNumber(String s) {
        Integer integer = dayMap.get(s);

        if (integer == null) {
            return -1;
        }

        return integer.intValue();
    }

    // //////////////////////////////////////////////////////////////////////////
    //
    // Computation Functions
    //
    // //////////////////////////////////////////////////////////////////////////

    protected Date getTimeAfter(Date afterTime) {

        // Computation is based on Gregorian year only.
        Calendar cl = new java.util.GregorianCalendar(getTimeZone());

        // move ahead one second, since we're computing the time *after* the
        // given time
        afterTime = new Date(afterTime.getTime() + 1000);
        // CronTrigger does not deal with milliseconds
        cl.setTime(afterTime);
        cl.set(Calendar.MILLISECOND, 0);

        boolean gotOne = false;
        // loop until we've computed the next time, or we've past the endTime
        while (!gotOne) {

            // if (endTime != null && cl.getTime().after(endTime)) return null;
            if (cl.get(Calendar.YEAR) > 2999) { // prevent endless loop...
                return null;
            }

            SortedSet<Integer> st = null;
            int t = 0;

            int sec = cl.get(Calendar.SECOND);
            int min = cl.get(Calendar.MINUTE);

            // get second.................................................
            st = seconds.tailSet(new Integer(sec));
            if (st != null && st.size() != 0) {
                sec = (st.first()).intValue();
            }
            else {
                sec = (seconds.first()).intValue();
                min++;
                cl.set(Calendar.MINUTE, min);
            }
            cl.set(Calendar.SECOND, sec);

            min = cl.get(Calendar.MINUTE);
            int hr = cl.get(Calendar.HOUR_OF_DAY);
            t = -1;

            // get minute.................................................
            st = minutes.tailSet(new Integer(min));
            if (st != null && st.size() != 0) {
                t = min;
                min = (st.first()).intValue();
            }
            else {
                min = (minutes.first()).intValue();
                hr++;
            }
            if (min != t) {
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, min);
                setCalendarHour(cl, hr);
                continue;
            }
            cl.set(Calendar.MINUTE, min);

            hr = cl.get(Calendar.HOUR_OF_DAY);
            int day = cl.get(Calendar.DAY_OF_MONTH);
            t = -1;

            // get hour...................................................
            st = hours.tailSet(new Integer(hr));
            if (st != null && st.size() != 0) {
                t = hr;
                hr = (st.first()).intValue();
            }
            else {
                hr = (hours.first()).intValue();
                day++;
            }
            if (hr != t) {
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, 0);
                cl.set(Calendar.DAY_OF_MONTH, day);
                setCalendarHour(cl, hr);
                continue;
            }
            cl.set(Calendar.HOUR_OF_DAY, hr);

            day = cl.get(Calendar.DAY_OF_MONTH);
            int mon = cl.get(Calendar.MONTH) + 1;
            // '+ 1' because calendar is 0-based for this field, and we are
            // 1-based
            t = -1;
            int tmon = mon;

            // get day...................................................
            boolean dayOfMSpec = !daysOfMonth.contains(NO_SPEC);
            boolean dayOfWSpec = !daysOfWeek.contains(NO_SPEC);
            if (dayOfMSpec && !dayOfWSpec) { // get day by day of month rule
                st = daysOfMonth.tailSet(new Integer(day));
                if (lastdayOfMonth) {
                    if (!nearestWeekday) {
                        t = day;
                        day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                    }
                    else {
                        t = day;
                        day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

                        java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
                        tcal.set(Calendar.SECOND, 0);
                        tcal.set(Calendar.MINUTE, 0);
                        tcal.set(Calendar.HOUR_OF_DAY, 0);
                        tcal.set(Calendar.DAY_OF_MONTH, day);
                        tcal.set(Calendar.MONTH, mon - 1);
                        tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));

                        int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        int dow = tcal.get(Calendar.DAY_OF_WEEK);

                        if (dow == Calendar.SATURDAY && day == 1) {
                            day += 2;
                        }
                        else if (dow == Calendar.SATURDAY) {
                            day -= 1;
                        }
                        else if (dow == Calendar.SUNDAY && day == ldom) {
                            day -= 2;
                        }
                        else if (dow == Calendar.SUNDAY) {
                            day += 1;
                        }

                        tcal.set(Calendar.SECOND, sec);
                        tcal.set(Calendar.MINUTE, min);
                        tcal.set(Calendar.HOUR_OF_DAY, hr);
                        tcal.set(Calendar.DAY_OF_MONTH, day);
                        tcal.set(Calendar.MONTH, mon - 1);
                        Date nTime = tcal.getTime();
                        if (nTime.before(afterTime)) {
                            day = 1;
                            mon++;
                        }
                    }
                }
                else if (nearestWeekday) {
                    t = day;
                    day = (daysOfMonth.first()).intValue();

                    java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
                    tcal.set(Calendar.SECOND, 0);
                    tcal.set(Calendar.MINUTE, 0);
                    tcal.set(Calendar.HOUR_OF_DAY, 0);
                    tcal.set(Calendar.DAY_OF_MONTH, day);
                    tcal.set(Calendar.MONTH, mon - 1);
                    tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));

                    int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                    int dow = tcal.get(Calendar.DAY_OF_WEEK);

                    if (dow == Calendar.SATURDAY && day == 1) {
                        day += 2;
                    }
                    else if (dow == Calendar.SATURDAY) {
                        day -= 1;
                    }
                    else if (dow == Calendar.SUNDAY && day == ldom) {
                        day -= 2;
                    }
                    else if (dow == Calendar.SUNDAY) {
                        day += 1;
                    }

                    tcal.set(Calendar.SECOND, sec);
                    tcal.set(Calendar.MINUTE, min);
                    tcal.set(Calendar.HOUR_OF_DAY, hr);
                    tcal.set(Calendar.DAY_OF_MONTH, day);
                    tcal.set(Calendar.MONTH, mon - 1);
                    Date nTime = tcal.getTime();
                    if (nTime.before(afterTime)) {
                        day = (daysOfMonth.first()).intValue();
                        mon++;
                    }
                }
                else if (st != null && st.size() != 0) {
                    t = day;
                    day = (st.first()).intValue();
                    // make sure we don't over-run a short month, such as february
                    int lastDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                    if (day > lastDay) {
                        day = (daysOfMonth.first()).intValue();
                        mon++;
                    }
                }
                else {
                    day = (daysOfMonth.first()).intValue();
                    mon++;
                }

                if (day != t || mon != tmon) {
                    cl.set(Calendar.SECOND, 0);
                    cl.set(Calendar.MINUTE, 0);
                    cl.set(Calendar.HOUR_OF_DAY, 0);
                    cl.set(Calendar.DAY_OF_MONTH, day);
                    cl.set(Calendar.MONTH, mon - 1);
                    // '- 1' because calendar is 0-based for this field, and we
                    // are 1-based
                    continue;
                }
            }
            else if (dayOfWSpec && !dayOfMSpec) { // get day by day of week rule
                if (lastdayOfWeek) { // are we looking for the last XXX day of
                    // the month?
                    int dow = (daysOfWeek.first()).intValue(); // desired
                    // d-o-w
                    int cDow = cl.get(Calendar.DAY_OF_WEEK); // current d-o-w
                    int daysToAdd = 0;
                    if (cDow < dow) {
                        daysToAdd = dow - cDow;
                    }
                    if (cDow > dow) {
                        daysToAdd = dow + (7 - cDow);
                    }

                    int lDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

                    if (day + daysToAdd > lDay) { // did we already miss the
                        // last one?
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, 1);
                        cl.set(Calendar.MONTH, mon);
                        // no '- 1' here because we are promoting the month
                        continue;
                    }

                    // find date of last occurance of this day in this month...
                    while ((day + daysToAdd + 7) <= lDay) {
                        daysToAdd += 7;
                    }

                    day += daysToAdd;

                    if (daysToAdd > 0) {
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, day);
                        cl.set(Calendar.MONTH, mon - 1);
                        // '- 1' here because we are not promoting the month
                        continue;
                    }

                }
                else if (nthdayOfWeek != 0) {
                    // are we looking for the Nth XXX day in the month?
                    int dow = (daysOfWeek.first()).intValue(); // desired
                    // d-o-w
                    int cDow = cl.get(Calendar.DAY_OF_WEEK); // current d-o-w
                    int daysToAdd = 0;
                    if (cDow < dow) {
                        daysToAdd = dow - cDow;
                    }
                    else if (cDow > dow) {
                        daysToAdd = dow + (7 - cDow);
                    }

                    boolean dayShifted = false;
                    if (daysToAdd > 0) {
                        dayShifted = true;
                    }

                    day += daysToAdd;
                    int weekOfMonth = day / 7;
                    if (day % 7 > 0) {
                        weekOfMonth++;
                    }

                    daysToAdd = (nthdayOfWeek - weekOfMonth) * 7;
                    day += daysToAdd;
                    if (daysToAdd < 0 || day > getLastDayOfMonth(mon, cl.get(Calendar.YEAR))) {
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, 1);
                        cl.set(Calendar.MONTH, mon);
                        // no '- 1' here because we are promoting the month
                        continue;
                    }
                    else if (daysToAdd > 0 || dayShifted) {
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, day);
                        cl.set(Calendar.MONTH, mon - 1);
                        // '- 1' here because we are NOT promoting the month
                        continue;
                    }
                }
                else {
                    int cDow = cl.get(Calendar.DAY_OF_WEEK); // current d-o-w
                    int dow = (daysOfWeek.first()).intValue(); // desired
                    // d-o-w
                    st = daysOfWeek.tailSet(new Integer(cDow));
                    if (st != null && st.size() > 0) {
                        dow = (st.first()).intValue();
                    }

                    int daysToAdd = 0;
                    if (cDow < dow) {
                        daysToAdd = dow - cDow;
                    }
                    if (cDow > dow) {
                        daysToAdd = dow + (7 - cDow);
                    }

                    int lDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

                    if (day + daysToAdd > lDay) { // will we pass the end of
                        // the month?
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, 1);
                        cl.set(Calendar.MONTH, mon);
                        // no '- 1' here because we are promoting the month
                        continue;
                    }
                    else if (daysToAdd > 0) { // are we swithing days?
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, day + daysToAdd);
                        cl.set(Calendar.MONTH, mon - 1);
                        // '- 1' because calendar is 0-based for this field,
                        // and we are 1-based
                        continue;
                    }
                }
            }
            else { // dayOfWSpec && !dayOfMSpec
                throw new UnsupportedOperationException(
                        "Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
                // TODO:
            }
            cl.set(Calendar.DAY_OF_MONTH, day);

            mon = cl.get(Calendar.MONTH) + 1;
            // '+ 1' because calendar is 0-based for this field, and we are
            // 1-based
            int year = cl.get(Calendar.YEAR);
            t = -1;

            // test for expressions that never generate a valid fire date,
            // but keep looping...
            if (year > YEAR_AT_WHICH_TO_GIVE_UP_SCHEDULING) {
                return null;
            }

            // get month...................................................
            st = months.tailSet(new Integer(mon));
            if (st != null && st.size() != 0) {
                t = mon;
                mon = (st.first()).intValue();
            }
            else {
                mon = (months.first()).intValue();
                year++;
            }
            if (mon != t) {
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, 0);
                cl.set(Calendar.HOUR_OF_DAY, 0);
                cl.set(Calendar.DAY_OF_MONTH, 1);
                cl.set(Calendar.MONTH, mon - 1);
                // '- 1' because calendar is 0-based for this field, and we are
                // 1-based
                cl.set(Calendar.YEAR, year);
                continue;
            }
            cl.set(Calendar.MONTH, mon - 1);
            // '- 1' because calendar is 0-based for this field, and we are
            // 1-based

            year = cl.get(Calendar.YEAR);
            t = -1;

            // get year...................................................
            st = years.tailSet(new Integer(year));
            if (st != null && st.size() != 0) {
                t = year;
                year = (st.first()).intValue();
            }
            else {
                return null; // ran out of years...
            }

            if (year != t) {
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, 0);
                cl.set(Calendar.HOUR_OF_DAY, 0);
                cl.set(Calendar.DAY_OF_MONTH, 1);
                cl.set(Calendar.MONTH, 0);
                // '- 1' because calendar is 0-based for this field, and we are
                // 1-based
                cl.set(Calendar.YEAR, year);
                continue;
            }
            cl.set(Calendar.YEAR, year);

            gotOne = true;
        } // while( !done )

        return cl.getTime();
    }

    /**
     * Advance the calendar to the particular hour paying particular attention to daylight saving problems.
     * 
     * @param cal
     * @param hour
     */
    protected void setCalendarHour(Calendar cal, int hour) {
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        if (cal.get(java.util.Calendar.HOUR_OF_DAY) != hour && hour != 24) {
            cal.set(java.util.Calendar.HOUR_OF_DAY, hour + 1);
        }
    }

    /**
     * NOT YET IMPLEMENTED: Returns the time before the given time that the <code>CronExpression</code> matches.
     */
    protected Date getTimeBefore(@SuppressWarnings("unused") Date endTime) {
        // TODO: implement QUARTZ-423
        return null;
    }

    /**
     * NOT YET IMPLEMENTED: Returns the final time that the <code>CronExpression</code> will match.
     */
    public Date getFinalFireTime() {
        // TODO: implement QUARTZ-423
        return null;
    }

    protected boolean isLeapYear(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0));
    }

    protected int getLastDayOfMonth(int monthNum, int year) {

        switch (monthNum) {
        case 1:
            return 31;
        case 2:
            return (isLeapYear(year)) ? 29 : 28;
        case 3:
            return 31;
        case 4:
            return 30;
        case 5:
            return 31;
        case 6:
            return 30;
        case 7:
            return 31;
        case 8:
            return 31;
        case 9:
            return 30;
        case 10:
            return 31;
        case 11:
            return 30;
        case 12:
            return 31;
        default:
            throw new IllegalArgumentException("Illegal month number: " + monthNum);
        }
    }

    private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        stream.defaultReadObject();
        try {
            buildExpression(cronExpression);
        }
        catch (Exception ignore) {
            // never happens
        }
    }

    @Override
    public Object clone() {
        CronExpression copy = null;
        try {
            copy = new CronExpression(getCronExpression());
            if (getTimeZone() != null)
                copy.setTimeZone((TimeZone) getTimeZone().clone());
        }
        catch (ParseException ex) { // never happens since the source is valid...
            throw new IncompatibleClassChangeError("Not Cloneable.");
        }
        return copy;
    }
}

class ValueSet {
    public int value;

    public int pos;
}
