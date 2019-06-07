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
package com.serotonin.mango.web.dwr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContextFactory;
import org.joda.time.DateTime;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.util.DateUtils;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.mango.web.dwr.beans.EventExportDefinition;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.dwr.MethodFilter;
import com.serotonin.web.i18n.LocalizableMessage;

public class EventsDwr extends BaseDwr {
	private static final int PAGE_SIZE = 50;
	private static final int PAGINATION_RADIUS = 3;

	public static final String STATUS_ALL = "*";
	public static final String STATUS_ACTIVE = "A";
	public static final String STATUS_RTN = "R";
	public static final String STATUS_NORTN = "N";

	public static final int DATE_RANGE_TYPE_NONE = 1;
	public static final int DATE_RANGE_TYPE_RELATIVE = 2;
	public static final int DATE_RANGE_TYPE_SPECIFIC = 3;

	public static final int RELATIVE_DATE_TYPE_PREVIOUS = 1;
	public static final int RELATIVE_DATE_TYPE_PAST = 2;

	public DwrResponseI18n searchOld(int eventId, int eventSourceType,
			String status, int alarmLevel, String keywordStr, int maxResults) {

		DwrResponseI18n response = new DwrResponseI18n();
		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();
		User user = Common.getUser(request);

		String[] keywordArr = keywordStr.split("\\s+");
		List<String> keywords = new ArrayList<String>();
		for (String s : keywordArr) {
			if (!StringUtils.isEmpty(s))
				keywords.add(s);
		}

		if (keywords.isEmpty())
			keywordArr = null;
		else {
			keywordArr = new String[keywords.size()];
			keywords.toArray(keywordArr);
		}

		List<EventInstance> results = new EventDao().searchOld(eventId,
				eventSourceType, status, alarmLevel, keywordArr, maxResults,
				user.getId(), getResourceBundle());

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("events", results);
		model.put("showControls", false);

		response.addData("content",
				generateContent(request, "eventList.jsp", model));
		response.addData("resultCount", new LocalizableMessage(
				"events.search.resultCount", results.size()));

		return response;
	}

	@MethodFilter
	public DwrResponseI18n search(int eventId, int eventSourceType,
			String status, int alarmLevel, String keywordStr,
			int dateRangeType, int relativeDateType, int previousPeriodCount,
			int previousPeriodType, int pastPeriodCount, int pastPeriodType,
			boolean fromNone, int fromYear, int fromMonth, int fromDay,
			int fromHour, int fromMinute, int fromSecond, boolean toNone,
			int toYear, int toMonth, int toDay, int toHour, int toMinute,
			int toSecond, int page, Date date) {

		System.out.println("search INICIO");

		DwrResponseI18n response = new DwrResponseI18n();
		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();
		User user = Common.getUser(request);

		System.out.println("search Aqui 1");

		int from = PAGE_SIZE * page;
		int to = from + PAGE_SIZE;

		System.out.println(date.toString());

		// This date is for the "jump to date" functionality. The date is set
		// for the top of the day, which will end up
		// excluding all of the events for that day. So, // we need to add 1 day
		// to it.
		if (date != null)
			date = DateUtils.minus(new DateTime(date.getTime()),
					Common.TimePeriods.DAYS, -1).toDate();

		LongPair dateRange = getDateRange(dateRangeType, relativeDateType,
				previousPeriodCount, previousPeriodType, pastPeriodCount,
				pastPeriodType, fromNone, fromYear, fromMonth, fromDay,
				fromHour, fromMinute, fromSecond, toNone, toYear, toMonth,
				toDay, toHour, toMinute, toSecond);

		EventDao eventDao = new EventDao();
		List<EventInstance> results = eventDao.search(eventId, eventSourceType,
				status, alarmLevel, getKeywords(keywordStr), dateRange.getL1(),
				dateRange.getL2(), user.getId(), getResourceBundle(), from, to,
				date);

		Map<String, Object> model = new HashMap<String, Object>();
		int searchRowCount = eventDao.getSearchRowCount();
		int pages = (int) Math.ceil(((double) searchRowCount) / PAGE_SIZE);

		if (date != null) {
			int startRow = eventDao.getStartRow();
			if (startRow == -1)
				page = pages - 1;
			else
				page = eventDao.getStartRow() / PAGE_SIZE;
		}

		if (pages > 1) {
			model.put("displayPagination", true);

			if (page - PAGINATION_RADIUS > 1)
				model.put("leftEllipsis", true);
			else
				model.put("leftEllipsis", false);

			int linkFrom = page + 1 - PAGINATION_RADIUS;
			if (linkFrom < 2)
				linkFrom = 2;
			model.put("linkFrom", linkFrom);
			int linkTo = page + 1 + PAGINATION_RADIUS;
			if (linkTo >= pages)
				linkTo = pages - 1;
			model.put("linkTo", linkTo);

			if (page + PAGINATION_RADIUS < pages - 2)
				model.put("rightEllipsis", true);
			else
				model.put("rightEllipsis", false);

			model.put("numberOfPages", pages);
		} else
			model.put("displayPagination", false);

		model.put("events", results);
		model.put("page", page);
		model.put("pendingEvents", false);

		response.addData("content",
				generateContent(request, "eventList.jsp", model));
		response.addData("resultCount", new LocalizableMessage(
				"events.search.resultCount", searchRowCount));

		return response;
	}

	@MethodFilter
	public void exportEvents(int eventId, int eventSourceType, String status,
			int alarmLevel, String keywordStr, int dateRangeType,
			int relativeDateType, int previousPeriodCount,
			int previousPeriodType, int pastPeriodCount, int pastPeriodType,
			boolean fromNone, int fromYear, int fromMonth, int fromDay,
			int fromHour, int fromMinute, int fromSecond, boolean toNone,
			int toYear, int toMonth, int toDay, int toHour, int toMinute,
			int toSecond) {
		User user = Common.getUser();
		LongPair dateRange = getDateRange(dateRangeType, relativeDateType,
				previousPeriodCount, previousPeriodType, pastPeriodCount,
				pastPeriodType, fromNone, fromYear, fromMonth, fromDay,
				fromHour, fromMinute, fromSecond, toNone, toYear, toMonth,
				toDay, toHour, toMinute, toSecond);

		EventExportDefinition def = new EventExportDefinition(eventId,
				eventSourceType, status, alarmLevel, getKeywords(keywordStr),
				dateRange.getL1(), dateRange.getL2(), user.getId());

		Common.getUser().setEventExportDefinition(def);
	}

	private String[] getKeywords(String keywordStr) {
		String[] keywordArr = keywordStr.split("\\s+");
		List<String> keywords = new ArrayList<String>();
		for (String s : keywordArr) {
			if (!StringUtils.isEmpty(s))
				keywords.add(s);
		}

		if (keywords.isEmpty())
			keywordArr = null;
		else {
			keywordArr = new String[keywords.size()];
			keywords.toArray(keywordArr);
		}

		return keywordArr;
	}

	private LongPair getDateRange(int dateRangeType, int relativeDateType,
			int previousPeriodCount, int previousPeriodType,
			int pastPeriodCount, int pastPeriodType, boolean fromNone,
			int fromYear, int fromMonth, int fromDay, int fromHour,
			int fromMinute, int fromSecond, boolean toNone, int toYear,
			int toMonth, int toDay, int toHour, int toMinute, int toSecond) {
		LongPair range = new LongPair(-1, -1);

		if (dateRangeType == DATE_RANGE_TYPE_RELATIVE) {
			if (relativeDateType == RELATIVE_DATE_TYPE_PREVIOUS) {
				DateTime dt = DateUtils.truncateDateTime(new DateTime(),
						previousPeriodType);
				range.setL2(dt.getMillis());
				dt = DateUtils.minus(dt, previousPeriodType,
						previousPeriodCount);
				range.setL1(dt.getMillis());
			} else {
				DateTime dt = new DateTime();
				range.setL2(dt.getMillis());
				dt = DateUtils.minus(dt, pastPeriodType, pastPeriodCount);
				range.setL1(dt.getMillis());
			}
		} else if (dateRangeType == DATE_RANGE_TYPE_SPECIFIC) {
			if (!fromNone) {
				DateTime dt = new DateTime(fromYear, fromMonth, fromDay,
						fromHour, fromMinute, fromSecond, 0);
				range.setL1(dt.getMillis());
			}

			if (!toNone) {
				DateTime dt = new DateTime(toYear, toMonth, toDay, toHour,
						toMinute, toSecond, 0);
				range.setL2(dt.getMillis());
			}
		}

		return range;
	}
}
