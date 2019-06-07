/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.date.supplemental");
dojo.date.getFirstDayOfWeek = function (locale) {
	var firstDay = {mv:5, ae:6, af:6, bh:6, dj:6, dz:6, eg:6, er:6, et:6, iq:6, ir:6, jo:6, ke:6, kw:6, lb:6, ly:6, ma:6, om:6, qa:6, sa:6, sd:6, so:6, tn:6, ye:6, as:0, au:0, az:0, bw:0, ca:0, cn:0, fo:0, ge:0, gl:0, gu:0, hk:0, ie:0, il:0, is:0, jm:0, jp:0, kg:0, kr:0, la:0, mh:0, mo:0, mp:0, mt:0, nz:0, ph:0, pk:0, sg:0, th:0, tt:0, tw:0, um:0, us:0, uz:0, vi:0, za:0, zw:0, et:0, mw:0, ng:0, tj:0, gb:0, sy:4};
	locale = dojo.hostenv.normalizeLocale(locale);
	var country = locale.split("-")[1];
	var dow = firstDay[country];
	return (typeof dow == "undefined") ? 1 : dow;
};
dojo.date.getWeekend = function (locale) {
	var weekendStart = {eg:5, il:5, sy:5, "in":0, ae:4, bh:4, dz:4, iq:4, jo:4, kw:4, lb:4, ly:4, ma:4, om:4, qa:4, sa:4, sd:4, tn:4, ye:4};
	var weekendEnd = {ae:5, bh:5, dz:5, iq:5, jo:5, kw:5, lb:5, ly:5, ma:5, om:5, qa:5, sa:5, sd:5, tn:5, ye:5, af:5, ir:5, eg:6, il:6, sy:6};
	locale = dojo.hostenv.normalizeLocale(locale);
	var country = locale.split("-")[1];
	var start = weekendStart[country];
	var end = weekendEnd[country];
	if (typeof start == "undefined") {
		start = 6;
	}
	if (typeof end == "undefined") {
		end = 0;
	}
	return {start:start, end:end};
};
dojo.date.isWeekend = function (dateObj, locale) {
	var weekend = dojo.date.getWeekend(locale);
	var day = (dateObj || new Date()).getDay();
	if (weekend.end < weekend.start) {
		weekend.end += 7;
		if (day < weekend.start) {
			day += 7;
		}
	}
	return day >= weekend.start && day <= weekend.end;
};

