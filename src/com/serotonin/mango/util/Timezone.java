package com.serotonin.mango.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;

/**
 * Generate all existed time zone and convert local time, date time, 
 * timestamp to the user's current time instead of the server's localtime 
 *  
 * @author Smart e-Tech
 * @category Timezone
 * @version 1
 *  
 */

public class Timezone{

	Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();

	public enum OffsetBase {
	    GMT, UTC
	}

	public List<String> getTimeZoneList(OffsetBase base) {	 

	    LocalDateTime now = LocalDateTime.now();
	    return ZoneId.getAvailableZoneIds()
	    		.stream().map(ZoneId::of)
	    		.sorted(ZoneComparator)
	    		.map(id -> String.format( "(%s%s) %s",base, getOffset(now, id) ,id.getId()))
    			.collect(Collectors.toList());
	}

	public List<String> getTimeZoneValue(OffsetBase base) { 

	    LocalDateTime now = LocalDateTime.now();
	    return ZoneId.getAvailableZoneIds()
	    		.stream()
	    		.map(ZoneId::of)
	    		.sorted(ZoneComparator)
				.map(id -> String.format( "%s%s",base, getOffset(now, id)))
				.collect(Collectors.toList());
	}
	
	Comparator<ZoneId> ZoneComparator = new Comparator<ZoneId>() {
		@Override
		public int compare(ZoneId zoneId1, ZoneId zoneId2) {
			 LocalDateTime now = LocalDateTime.now();
		        ZoneOffset offset1 = now.atZone(zoneId1).getOffset();
		        ZoneOffset offset2 = now.atZone(zoneId2).getOffset();

		        return offset1.compareTo(offset2);
		}
	};

	private static String getOffset(LocalDateTime dateTime, ZoneId id) {

		return dateTime.atZone(id)
				.getOffset()
				.getId()
				.replace("Z", "+00:00");
	}

	public static  TimeZone createTimezone(String id) {
		String myIdTimeZone = id;
		if (id == null || (id != null && id.equals(""))) {
			myIdTimeZone = getTimezoneSystem();
		}
		TimeZone timezone = TimeZone.getDefault();
		timezone.setID(myIdTimeZone);
		return timezone;

	}
	
	public static  String getTimezoneSystem() {
        LocalDateTime now = LocalDateTime.now();
        return "UTC "+getOffset(now,ZoneId.systemDefault());
	}


	public static LocalDateTime getCurrentDateTimeWithOffset(String id){

        ZoneOffset zoneOffset = ZoneOffset.of(id.substring(3,id.length()));       
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.ofOffset("UTC", zoneOffset));       
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");      
        localDateTime.format(formatter);
        return localDateTime;
    }

	public static  long getCurrentMillisWithOffset(String idzone){

        ZoneOffset zoneOffset = ZoneOffset.of(idzone.substring(3,idzone.length()));
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.ofOffset("UTC", zoneOffset));

        return localDateTime.toInstant(zoneOffset).toEpochMilli();
    }


	public static long convertMillisTodate (long millis,String idzone){   //DateFunctions.

    	ZoneOffset zoneOffset = ZoneOffset.of(idzone.substring(3,idzone.length()));
    	LocalDateTime localDateTime = LocalDateTime
    			.ofInstant(Instant.ofEpochMilli(millis),ZoneId.ofOffset("UTC", zoneOffset));

        return Timestamp.valueOf(localDateTime).getTime();
	}

	public static String convertMillisTotime (String time,String idzone) {

		ZoneOffset zoneOffset = ZoneOffset.of(idzone.substring(3,idzone.length()));
		ZoneId zoneId=ZoneId.ofOffset("GMT", zoneOffset);

		if((time!=null )&&(time.length()==8)){

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			LocalTime dateTime = LocalTime.parse(time); ///

			// Convert Local Time to UTC (Works Fine)
			sdf.setTimeZone(TimeZone.getTimeZone(zoneId));

			Calendar calendar = Calendar.getInstance();
			calendar.clear();

			//assuming year/month/date information is not important
			calendar.set(0, 0, 0, dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());

			Date localTime = calendar.getTime();

			@SuppressWarnings("deprecation")
			Date gmtTime = new Date(sdf.format(localTime));

			DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");  

			return dateFormat.format(gmtTime);

		}
		else 
			return "";

		}

    public static Date convertDateToZoneId(Date date,String id) {

    	LocalDateTime localDateTime = LocalDateTime
    			.ofInstant(date.toInstant(),ZoneId
    					.ofOffset("UTC", ZoneOffset
    							.of(id.substring(3,id.length()))));
    	return Date
    			.from(localDateTime
    					.toInstant(ZoneId
    							.systemDefault()
    							.getRules()
    							.getOffset(localDateTime)));

  	}



    public static long convertDateToZoneIdLong(Date date, String id) { 

        ZoneId zoneId=ZoneId.ofOffset("UTC", ZoneOffset.of(id.substring(3,id.length())));
    	LocalDateTime localDateTime =LocalDateTime.ofInstant(date.toInstant(),zoneId);

    	return localDateTime.toLocalTime().toNanoOfDay();	
  	}

	// 
	public static long getTimezoneUserLong(User user) { 

		String id = user.getTimezoneId();
		if ((id.length()!=0))
			return convertMillisTodate(System.currentTimeMillis(), id);
		else
			return System.currentTimeMillis();
	}

	public static long getTimezoneUserLong(User user,long millis) { 

		String idzone = user.getTimezoneId();
		
		if ((idzone.length()!=0)&&(millis>0))
			return convertMillisTodate(millis, idzone);

		else
			return millis;

	}

	public static Date getTimezoneUserDate(User user,Date date) { 

		String id = user != null
				?user.getTimezoneId()
				:TimeZone.getDefault().getID();

		if ((id.length()!=0))
			return convertDateToZoneId(date,id);

		else
			return date;
	}



	public static Date getTimezoneSystemDate(Date date) {
		if ((date!=null))
			return convertDateToZoneId(date,TimeZone.getDefault().getID());
		else
			return date; 

	}

	public static long getTimezoneSystemLong(User user) {
		user.getTimezoneId();
		String id = user != null
				?user.getTimezoneId()
				:TimeZone.getDefault().getID();
		if ((id.length()!=0))
			return convertMillisTodate(System.currentTimeMillis(), id);

		else
			return System.currentTimeMillis();
	}

public static Date getTimezoneSystemDate(Date date,User user) throws ParseException {
		String id = user != null
				?user.getTimezoneId()
				:TimeZone.getDefault().getID();
				
		if ((date!=null))
			return convertDateToZoneIdSyteme(user.getZone(),id,date);
		else
			return date; 
		
	}

	@SuppressWarnings("deprecation")
	public static Date convertDateToZoneIdSyteme(String zoneFrom,String zoneto,Date date) throws ParseException {

	     
  	  	String DATE_FORMAT = "dd-MM-yyyy hh:mm:ss a";
  	
  	    SimpleDateFormat sdfdate = new SimpleDateFormat(DATE_FORMAT);
    	String dateInString = sdfdate.format(date);
        LocalDateTime ldt = LocalDateTime.parse(dateInString, DateTimeFormatter.ofPattern(DATE_FORMAT));

        ZoneId FormZoneId = ZoneId.of(zoneFrom);
        ZoneId toZoneId = ZoneId.of(zoneto);
    
   
        ZonedDateTime FromDateTime = ldt.atZone(FormZoneId);
        ZonedDateTime toDateTime = FromDateTime.withZoneSameInstant(toZoneId);
        return new Date(toDateTime.getYear(),toDateTime.getMonthValue(),toDateTime.getDayOfMonth(),toDateTime.getHour(),toDateTime.getMinute(),toDateTime.getSecond());
	}

    public static long getTimezoneUserLong(User user,Date date) {
		String id = user.getTimezoneId();

		if ((id.length()!=0))
			return convertDateToZoneIdLong(date,id);

		else 
			return System.currentTimeMillis();
    }

    public static String  getTimezoneUserString(User user,String time) { 

    	String id = user.getTimezoneId();

    	if ((id.length()!=0))
    		return convertMillisTotime(time,id);
    	else
  			return time;
  	}
    public static Date getDate(int year, int month, int day ,int hour ,int min,int sec) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1); // in the API calendar  the first month of year is  Jan =0 / Dec = 11   
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, sec);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}