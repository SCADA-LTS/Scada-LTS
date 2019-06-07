/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.MonthlyCalendar");
dojo.require("dojo.date.common");
dojo.require("dojo.date.format");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.DatePicker");
dojo.require("dojo.event.*");
dojo.require("dojo.html.*");
dojo.require("dojo.experimental");
dojo.experimental("dojo.widget.MonthlyCalendar");
dojo.widget.defineWidget("dojo.widget.MonthlyCalendar", dojo.widget.DatePicker, {dayWidth:"wide", templateString:"<div class=\"datePickerContainer\" dojoAttachPoint=\"datePickerContainerNode\">\r\n\t<h3 class=\"monthLabel\">\r\n\t<!--\r\n\t<span \r\n\t\tdojoAttachPoint=\"decreaseWeekNode\" \r\n\t\tdojoAttachEvent=\"onClick: onIncrementWeek;\" \r\n\t\tclass=\"incrementControl\">\r\n\t\t<img src=\"${dojoWidgetModuleUri}templates/decrementWeek.gif\" alt=\"&uarr;\" />\r\n\t</span>\r\n\t-->\r\n\t<span \r\n\t\tdojoAttachPoint=\"decreaseMonthNode\" \r\n\t\tdojoAttachEvent=\"onClick: onIncrementMonth;\" class=\"incrementControl\">\r\n\t\t<img src=\"${dojoWidgetModuleUri}templates/decrementMonth.gif\" \r\n\t\t\talt=\"&uarr;\" dojoAttachPoint=\"decrementMonthImageNode\">\r\n\t</span>\r\n\t<span dojoAttachPoint=\"monthLabelNode\" class=\"month\">July</span>\r\n\t<span \r\n\t\tdojoAttachPoint=\"increaseMonthNode\" \r\n\t\tdojoAttachEvent=\"onClick: onIncrementMonth;\" class=\"incrementControl\">\r\n\t\t<img src=\"${dojoWidgetModuleUri}templates/incrementMonth.gif\" \r\n\t\t\talt=\"&darr;\"  dojoAttachPoint=\"incrementMonthImageNode\">\r\n\t</span>\r\n\t<!--\r\n\t\t<span dojoAttachPoint=\"increaseWeekNode\" \r\n\t\t\tdojoAttachEvent=\"onClick: onIncrementWeek;\" \r\n\t\t\tclass=\"incrementControl\">\r\n\t\t\t<img src=\"${dojoWidgetModuleUri}templates/incrementWeek.gif\" \r\n\t\t\talt=\"&darr;\" />\r\n\t\t</span>\r\n\t-->\r\n\t</h3>\r\n\t<table class=\"calendarContainer\">\r\n\t\t<thead>\r\n\t\t\t<tr dojoAttachPoint=\"dayLabelsRow\">\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t</tr>\r\n\t\t</thead>\r\n\t\t<tbody dojoAttachPoint=\"calendarDatesContainerNode\" \r\n\t\t\tdojoAttachEvent=\"onClick: onSetDate;\">\r\n\t\t\t<tr dojoAttachPoint=\"calendarRow0\">\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t</tr>\r\n\t\t\t<tr dojoAttachPoint=\"calendarRow1\">\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t</tr>\r\n\t\t\t<tr dojoAttachPoint=\"calendarRow2\">\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t</tr>\r\n\t\t\t<tr dojoAttachPoint=\"calendarRow3\">\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t</tr>\r\n\t\t\t<tr dojoAttachPoint=\"calendarRow4\">\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t</tr>\r\n\t\t\t<tr dojoAttachPoint=\"calendarRow5\">\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t\t<td></td>\r\n\t\t\t</tr>\r\n\t\t</tbody>\r\n\t</table>\r\n\t<h3 class=\"yearLabel\">\r\n\t\t<span dojoAttachPoint=\"previousYearLabelNode\"\r\n\t\t\tdojoAttachEvent=\"onClick: onIncrementYear;\" class=\"previousYear\"></span>\r\n\t\t<span class=\"selectedYear\" dojoAttachPoint=\"currentYearLabelNode\"></span>\r\n\t\t<span dojoAttachPoint=\"nextYearLabelNode\" \r\n\t\t\tdojoAttachEvent=\"onClick: onIncrementYear;\" class=\"nextYear\"></span>\r\n\t</h3>\r\n</div>\r\n", templateCssString:".datePickerContainer {\r\n\tmargin:0.5em 2em 0.5em 0;\r\n\t/*width:10em;*/\r\n\tfloat:left;\r\n}\r\n\r\n.previousMonth {\r\n\tbackground-color:#bbbbbb;\r\n}\r\n\r\n.currentMonth {\r\n\tbackground-color:#8f8f8f;\r\n}\r\n\r\n.nextMonth {\r\n\tbackground-color:#eeeeee;\r\n}\r\n\r\n.currentDate {\r\n\ttext-decoration:underline;\r\n\tfont-style:italic;\r\n}\r\n\r\n.selectedItem {\r\n\tbackground-color:#3a3a3a;\r\n\tcolor:#ffffff;\r\n}\r\n\r\n.calendarContainer {\r\n\tborder-collapse:collapse;\r\n\tborder-spacing:0;\r\n\tborder-bottom:1px solid #e6e6e6;\r\n\toverflow: hidden;\r\n\ttext-align: right;\r\n}\r\n\r\n.calendarContainer thead{\r\n\tborder-bottom:1px solid #e6e6e6;\r\n}\r\n\r\n.calendarContainer tbody * td {\r\n		height: 100px;\r\n		border: 1px solid gray;\r\n}\r\n\r\n.calendarContainer td {\r\n		width: 100px;\r\n		padding: 2px;\r\n\tvertical-align: top;\r\n}\r\n\r\n.monthLabel {\r\n\tfont-size:0.9em;\r\n\tfont-weight:400;\r\n\tmargin:0;\r\n\ttext-align:center;\r\n}\r\n\r\n.monthLabel .month {\r\n\tpadding:0 0.4em 0 0.4em;\r\n}\r\n\r\n.yearLabel {\r\n\tfont-size:0.9em;\r\n\tfont-weight:400;\r\n\tmargin:0.25em 0 0 0;\r\n\ttext-align:right;\r\n\tcolor:#a3a3a3;\r\n}\r\n\r\n.yearLabel .selectedYear {\r\n\tcolor:#000;\r\n\tpadding:0 0.2em;\r\n}\r\n\r\n.nextYear, .previousYear {\r\n\tcursor:pointer;cursor:hand;\r\n}\r\n\r\n.incrementControl {\r\n\tcursor:pointer;cursor:hand;\r\n\twidth:1em;\r\n}\r\n\r\n.dojoMonthlyCalendarEvent {\r\n\tfont-size:0.7em;\r\n\toverflow: hidden;\r\n\tfont-color: grey;\r\n\twhite-space: nowrap;\r\n\ttext-align: left;\r\n}\r\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/MonthlyCalendar.css"), initializer:function () {
	this.iCalendars = [];
}, addCalendar:function (cal) {
	dojo.debug("Adding Calendar");
	this.iCalendars.push(cal);
	dojo.debug("Starting init");
	this.initUI();
	dojo.debug("done init");
}, createDayContents:function (node, mydate) {
	dojo.html.removeChildren(node);
	node.appendChild(document.createTextNode(mydate.getDate()));
	for (var x = 0; x < this.iCalendars.length; x++) {
		var evts = this.iCalendars[x].getEvents(mydate);
		if ((dojo.lang.isArray(evts)) && (evts.length > 0)) {
			for (var y = 0; y < evts.length; y++) {
				var el = document.createElement("div");
				dojo.html.addClass(el, "dojoMonthlyCalendarEvent");
				el.appendChild(document.createTextNode(evts[y].summary.value));
				el.width = dojo.html.getContentBox(node).width;
				node.appendChild(el);
			}
		}
	}
}, initUI:function () {
	var dayLabels = dojo.date.getNames("days", this.dayWidth, "standAlone", this.lang);
	var dayLabelNodes = this.dayLabelsRow.getElementsByTagName("td");
	for (var i = 0; i < 7; i++) {
		dayLabelNodes.item(i).innerHTML = dayLabels[i];
	}
	this.selectedIsUsed = false;
	this.currentIsUsed = false;
	var currentClassName = "";
	var previousDate = new Date();
	var calendarNodes = this.calendarDatesContainerNode.getElementsByTagName("td");
	var currentCalendarNode;
	previousDate.setHours(8);
	var nextDate = new Date(this.firstSaturday.year, this.firstSaturday.month, this.firstSaturday.date, 8);
	var lastDay = new Date(this.firstSaturday.year, this.firstSaturday.month, this.firstSaturday.date + 42, 8);
	if (this.iCalendars.length > 0) {
		for (var x = 0; x < this.iCalendars.length; x++) {
			this.iCalendars[x].preComputeRecurringEvents(lastDay);
		}
	}
	if (this.firstSaturday.date < 7) {
		var dayInWeek = 6;
		for (var i = this.firstSaturday.date; i > 0; i--) {
			currentCalendarNode = calendarNodes.item(dayInWeek);
			this.createDayContents(currentCalendarNode, nextDate);
			dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "current"));
			dayInWeek--;
			previousDate = nextDate;
			nextDate = this.incrementDate(nextDate, false);
		}
		for (var i = dayInWeek; i > -1; i--) {
			currentCalendarNode = calendarNodes.item(i);
			this.createDayContents(currentCalendarNode, nextDate);
			dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "previous"));
			previousDate = nextDate;
			nextDate = this.incrementDate(nextDate, false);
		}
	} else {
		nextDate.setDate(1);
		for (var i = 0; i < 7; i++) {
			currentCalendarNode = calendarNodes.item(i);
			this.createDayContents(currentCalendarNode, nextDate);
			dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "current"));
			previousDate = nextDate;
			nextDate = this.incrementDate(nextDate, true);
		}
	}
	previousDate.setDate(this.firstSaturday.date);
	previousDate.setMonth(this.firstSaturday.month);
	previousDate.setFullYear(this.firstSaturday.year);
	nextDate = this.incrementDate(previousDate, true);
	var count = 7;
	currentCalendarNode = calendarNodes.item(count);
	while ((nextDate.getMonth() == previousDate.getMonth()) && (count < 42)) {
		this.createDayContents(currentCalendarNode, nextDate);
		dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "current"));
		currentCalendarNode = calendarNodes.item(++count);
		previousDate = nextDate;
		nextDate = this.incrementDate(nextDate, true);
	}
	while (count < 42) {
		this.createDayContents(currentCalendarNode, nextDate);
		dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "next"));
		currentCalendarNode = calendarNodes.item(++count);
		previousDate = nextDate;
		nextDate = this.incrementDate(nextDate, true);
	}
	this.setMonthLabel(this.firstSaturday.month);
	this.setYearLabels(this.firstSaturday.year);
}});
dojo.widget.MonthlyCalendar.util = new function () {
	this.toRfcDate = function (jsDate) {
		if (!jsDate) {
			jsDate = this.today;
		}
		var year = jsDate.getFullYear();
		var month = jsDate.getMonth() + 1;
		if (month < 10) {
			month = "0" + month.toString();
		}
		var date = jsDate.getDate();
		if (date < 10) {
			date = "0" + date.toString();
		}
		return year + "-" + month + "-" + date + "T00:00:00+00:00";
	};
	this.fromRfcDate = function (rfcDate) {
		var tempDate = rfcDate.split("-");
		if (tempDate.length < 3) {
			return new Date();
		}
		return new Date(parseInt(tempDate[0]), (parseInt(tempDate[1], 10) - 1), parseInt(tempDate[2].substr(0, 2), 10));
	};
	this.initFirstSaturday = function (month, year) {
		if (!month) {
			month = this.date.getMonth();
		}
		if (!year) {
			year = this.date.getFullYear();
		}
		var firstOfMonth = new Date(year, month, 1);
		return {year:year, month:month, date:7 - firstOfMonth.getDay()};
	};
};

