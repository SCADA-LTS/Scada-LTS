/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.DatePicker");
dojo.require("dojo.date.common");
dojo.require("dojo.date.format");
dojo.require("dojo.date.serialize");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.event.*");
dojo.require("dojo.dom");
dojo.require("dojo.html.style");
dojo.widget.defineWidget("dojo.widget.DatePicker", dojo.widget.HtmlWidget, {value:"", name:"", displayWeeks:6, adjustWeeks:false, startDate:"1492-10-12", endDate:"2941-10-12", weekStartsOn:"", staticDisplay:false, dayWidth:"narrow", classNames:{previous:"previousMonth", disabledPrevious:"previousMonthDisabled", current:"currentMonth", disabledCurrent:"currentMonthDisabled", next:"nextMonth", disabledNext:"nextMonthDisabled", currentDate:"currentDate", selectedDate:"selectedDate"}, templateString:"<div class=\"datePickerContainer\" dojoAttachPoint=\"datePickerContainerNode\">\r\n\t<table cellspacing=\"0\" cellpadding=\"0\" class=\"calendarContainer\">\r\n\t\t<thead>\r\n\t\t\t<tr>\r\n\t\t\t\t<td class=\"monthWrapper\" valign=\"top\">\r\n\t\t\t\t\t<table class=\"monthContainer\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t<td class=\"monthCurve monthCurveTL\" valign=\"top\"></td>\r\n\t\t\t\t\t\t\t<td class=\"monthLabelContainer\" valign=\"top\">\r\n\t\t\t\t\t\t\t\t<span dojoAttachPoint=\"increaseWeekNode\" \r\n\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementWeek;\" \r\n\t\t\t\t\t\t\t\t\tclass=\"incrementControl increase\">\r\n\t\t\t\t\t\t\t\t\t<img src=\"${dojoWidgetModuleUri}templates/images/incrementMonth.png\" \r\n\t\t\t\t\t\t\t\t\talt=\"&darr;\" style=\"width:7px;height:5px;\" />\r\n\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t<span \r\n\t\t\t\t\t\t\t\t\tdojoAttachPoint=\"increaseMonthNode\" \r\n\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementMonth;\" class=\"incrementControl increase\">\r\n\t\t\t\t\t\t\t\t\t<img src=\"${dojoWidgetModuleUri}templates/images/incrementMonth.png\" \r\n\t\t\t\t\t\t\t\t\t\talt=\"&darr;\"  dojoAttachPoint=\"incrementMonthImageNode\">\r\n\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t<span \r\n\t\t\t\t\t\t\t\t\tdojoAttachPoint=\"decreaseWeekNode\" \r\n\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementWeek;\" \r\n\t\t\t\t\t\t\t\t\tclass=\"incrementControl decrease\">\r\n\t\t\t\t\t\t\t\t\t<img src=\"${dojoWidgetModuleUri}templates/images/decrementMonth.png\" alt=\"&uarr;\" style=\"width:7px;height:5px;\" />\r\n\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t<span \r\n\t\t\t\t\t\t\t\t\tdojoAttachPoint=\"decreaseMonthNode\" \r\n\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementMonth;\" class=\"incrementControl decrease\">\r\n\t\t\t\t\t\t\t\t\t<img src=\"${dojoWidgetModuleUri}templates/images/decrementMonth.png\" \r\n\t\t\t\t\t\t\t\t\t\talt=\"&uarr;\" dojoAttachPoint=\"decrementMonthImageNode\">\r\n\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t<span dojoAttachPoint=\"monthLabelNode\" class=\"month\"></span>\r\n\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t<td class=\"monthCurve monthCurveTR\" valign=\"top\"></td>\r\n\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t</table>\r\n\t\t\t\t</td>\r\n\t\t\t</tr>\r\n\t\t</thead>\r\n\t\t<tbody>\r\n\t\t\t<tr>\r\n\t\t\t\t<td colspan=\"3\">\r\n\t\t\t\t\t<table class=\"calendarBodyContainer\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n\t\t\t\t\t\t<thead>\r\n\t\t\t\t\t\t\t<tr dojoAttachPoint=\"dayLabelsRow\">\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t</thead>\r\n\t\t\t\t\t\t<tbody dojoAttachPoint=\"calendarDatesContainerNode\" \r\n\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: _handleUiClick;\">\r\n\t\t\t\t\t\t\t<tr dojoAttachPoint=\"calendarWeekTemplate\">\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t</tbody>\r\n\t\t\t\t\t</table>\r\n\t\t\t\t</td>\r\n\t\t\t</tr>\r\n\t\t</tbody>\r\n\t\t<tfoot>\r\n\t\t\t<tr>\r\n\t\t\t\t<td colspan=\"3\" class=\"yearWrapper\">\r\n\t\t\t\t\t<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"yearContainer\">\r\n\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t<td class=\"curveBL\" valign=\"top\"></td>\r\n\t\t\t\t\t\t\t<td valign=\"top\">\r\n\t\t\t\t\t\t\t\t<h3 class=\"yearLabel\">\r\n\t\t\t\t\t\t\t\t\t<span dojoAttachPoint=\"previousYearLabelNode\"\r\n\t\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementYear;\" class=\"previousYear\"></span>\r\n\t\t\t\t\t\t\t\t\t<span class=\"selectedYear\" dojoAttachPoint=\"currentYearLabelNode\"></span>\r\n\t\t\t\t\t\t\t\t\t<span dojoAttachPoint=\"nextYearLabelNode\" \r\n\t\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementYear;\" class=\"nextYear\"></span>\r\n\t\t\t\t\t\t\t\t</h3>\r\n\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t<td class=\"curveBR\" valign=\"top\"></td>\r\n\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t</table>\r\n\t\t\t\t</td>\r\n\t\t\t</tr>\r\n\t\t</tfoot>\r\n\t</table>\r\n</div>\r\n", templateCssString:".datePickerContainer {\r\n\twidth:164px; /* needed for proper user styling */\r\n}\r\n\r\n.calendarContainer {\r\n/*\tborder:1px solid #566f8f;*/\r\n}\r\n\r\n.calendarBodyContainer {\r\n\twidth:100%; /* needed for the explode effect (explain?) */\r\n\tbackground: #7591bc url(\"images/dpBg.gif\") top left repeat-x;\r\n}\r\n\r\n.calendarBodyContainer thead tr td {\r\n\tcolor:#293a4b;\r\n\tfont:bold 0.75em Helvetica, Arial, Verdana, sans-serif;\r\n\ttext-align:center;\r\n\tpadding:0.25em;\r\n\tbackground: url(\"images/dpHorizLine.gif\") bottom left repeat-x;\r\n}\r\n\r\n.calendarBodyContainer tbody tr td {\r\n\tcolor:#fff;\r\n\tfont:bold 0.7em Helvetica, Arial, Verdana, sans-serif;\r\n\ttext-align:center;\r\n\tpadding:0.4em;\r\n\tbackground: url(\"images/dpVertLine.gif\") top right repeat-y;\r\n\tcursor:pointer;\r\n\tcursor:hand;\r\n}\r\n\r\n\r\n.monthWrapper {\r\n\tpadding-bottom:2px;\r\n\tbackground: url(\"images/dpHorizLine.gif\") bottom left repeat-x;\r\n}\r\n\r\n.monthContainer {\r\n\twidth:100%;\r\n}\r\n\r\n.monthLabelContainer {\r\n\ttext-align:center;\r\n\tfont:bold 0.75em Helvetica, Arial, Verdana, sans-serif;\r\n\tbackground: url(\"images/dpMonthBg.png\") repeat-x top left !important;\r\n\tcolor:#293a4b;\r\n\tpadding:0.25em;\r\n}\r\n\r\n.monthCurve {\r\n\twidth:12px;\r\n}\r\n\r\n.monthCurveTL {\r\n\tbackground: url(\"images/dpCurveTL.png\") no-repeat top left !important;\r\n}\r\n\r\n.monthCurveTR {\r\n\t\tbackground: url(\"images/dpCurveTR.png\") no-repeat top right !important;\r\n}\r\n\r\n\r\n.yearWrapper {\r\n\tbackground: url(\"images/dpHorizLineFoot.gif\") top left repeat-x;\r\n\tpadding-top:2px;\r\n}\r\n\r\n.yearContainer {\r\n\twidth:100%;\r\n}\r\n\r\n.yearContainer td {\r\n\tbackground:url(\"images/dpYearBg.png\") top left repeat-x;\r\n}\r\n\r\n.yearContainer .yearLabel {\r\n\tmargin:0;\r\n\tpadding:0.45em 0 0.45em 0;\r\n\tcolor:#fff;\r\n\tfont:bold 0.75em Helvetica, Arial, Verdana, sans-serif;\r\n\ttext-align:center;\r\n}\r\n\r\n.curveBL {\r\n\tbackground: url(\"images/dpCurveBL.png\") bottom left no-repeat !important;\r\n\twidth:9px !important;\r\n\tpadding:0;\r\n\tmargin:0;\r\n}\r\n\r\n.curveBR {\r\n\tbackground: url(\"images/dpCurveBR.png\") bottom right no-repeat !important;\r\n\twidth:9px !important;\r\n\tpadding:0;\r\n\tmargin:0;\r\n}\r\n\r\n\r\n.previousMonth {\r\n\tbackground-color:#6782a8 !important;\r\n}\r\n\r\n.previousMonthDisabled {\r\n\tbackground-color:#a4a5a6 !important;\r\n\tcursor:default !important\r\n}\r\n.currentMonth {\r\n}\r\n\r\n.currentMonthDisabled {\r\n\tbackground-color:#bbbbbc !important;\r\n\tcursor:default !important\r\n}\r\n.nextMonth {\r\n\tbackground-color:#6782a8 !important;\r\n}\r\n.nextMonthDisabled {\r\n\tbackground-color:#a4a5a6 !important;\r\n\tcursor:default !important;\r\n}\r\n\r\n.currentDate {\r\n\ttext-decoration:underline;\r\n\tfont-style:italic;\r\n}\r\n\r\n.selectedDate {\r\n\tbackground-color:#fff !important;\r\n\tcolor:#6782a8 !important;\r\n}\r\n\r\n.yearLabel .selectedYear {\r\n\tpadding:0.2em;\r\n\tbackground-color:#9ec3fb !important;\r\n}\r\n\r\n.nextYear, .previousYear {\r\n\tcursor:pointer;cursor:hand;\r\n\tpadding:0;\r\n}\r\n\r\n.nextYear {\r\n\tmargin:0 0 0 0.55em;\r\n}\r\n\r\n.previousYear {\r\n\tmargin:0 0.55em 0 0;\r\n}\r\n\r\n.incrementControl {\r\n\tcursor:pointer;cursor:hand;\r\n\twidth:1em;\r\n}\r\n\r\n.increase {\r\n\tfloat:right;\r\n}\r\n\r\n.decrease {\r\n\tfloat:left;\r\n}\r\n\r\n.lastColumn {\r\n\tbackground-image:none !important;\r\n}\r\n\r\n\r\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/DatePicker.css"), postMixInProperties:function () {
	dojo.widget.DatePicker.superclass.postMixInProperties.apply(this, arguments);
	if (!this.weekStartsOn) {
		this.weekStartsOn = dojo.date.getFirstDayOfWeek(this.lang);
	}
	this.today = new Date();
	this.today.setHours(0, 0, 0, 0);
	if (typeof (this.value) == "string" && this.value.toLowerCase() == "today") {
		this.value = new Date();
	} else {
		if (this.value && (typeof this.value == "string") && (this.value.split("-").length > 2)) {
			this.value = dojo.date.fromRfc3339(this.value);
			this.value.setHours(0, 0, 0, 0);
		}
	}
}, fillInTemplate:function (args, frag) {
	dojo.widget.DatePicker.superclass.fillInTemplate.apply(this, arguments);
	var source = this.getFragNodeRef(frag);
	dojo.html.copyStyle(this.domNode, source);
	this.weekTemplate = dojo.dom.removeNode(this.calendarWeekTemplate);
	this._preInitUI(this.value ? this.value : this.today, false, true);
	var dayLabels = dojo.lang.unnest(dojo.date.getNames("days", this.dayWidth, "standAlone", this.lang));
	if (this.weekStartsOn > 0) {
		for (var i = 0; i < this.weekStartsOn; i++) {
			dayLabels.push(dayLabels.shift());
		}
	}
	var dayLabelNodes = this.dayLabelsRow.getElementsByTagName("td");
	for (i = 0; i < 7; i++) {
		dayLabelNodes.item(i).innerHTML = dayLabels[i];
	}
	if (this.value) {
		this.setValue(this.value);
	}
}, getValue:function () {
	return dojo.date.toRfc3339(new Date(this.value), "dateOnly");
}, getDate:function () {
	return this.value;
}, setValue:function (rfcDate) {
	this.setDate(rfcDate);
}, setDate:function (dateObj) {
	if (dateObj == "") {
		this.value = "";
		this._preInitUI(this.curMonth, false, true);
	} else {
		if (typeof dateObj == "string") {
			this.value = dojo.date.fromRfc3339(dateObj);
			this.value.setHours(0, 0, 0, 0);
		} else {
			this.value = new Date(dateObj);
			this.value.setHours(0, 0, 0, 0);
		}
	}
	if (this.selectedNode != null) {
		dojo.html.removeClass(this.selectedNode, this.classNames.selectedDate);
	}
	if (this.clickedNode != null) {
		dojo.debug("adding selectedDate");
		dojo.html.addClass(this.clickedNode, this.classNames.selectedDate);
		this.selectedNode = this.clickedNode;
	} else {
		this._preInitUI(this.value, false, true);
	}
	this.clickedNode = null;
	this.onValueChanged(this.value);
}, _preInitUI:function (dateObj, initFirst, initUI) {
	if (typeof (this.startDate) == "string") {
		this.startDate = dojo.date.fromRfc3339(this.startDate);
	}
	if (typeof (this.endDate) == "string") {
		this.endDate = dojo.date.fromRfc3339(this.endDate);
	}
	this.startDate.setHours(0, 0, 0, 0);
	this.endDate.setHours(24, 0, 0, -1);
	if (dateObj < this.startDate || dateObj > this.endDate) {
		dateObj = new Date((dateObj < this.startDate) ? this.startDate : this.endDate);
	}
	this.firstDay = this._initFirstDay(dateObj, initFirst);
	this.selectedIsUsed = false;
	this.currentIsUsed = false;
	var nextDate = new Date(this.firstDay);
	var tmpMonth = nextDate.getMonth();
	this.curMonth = new Date(nextDate);
	this.curMonth.setDate(nextDate.getDate() + 6);
	this.curMonth.setDate(1);
	if (this.displayWeeks == "" || this.adjustWeeks) {
		this.adjustWeeks = true;
		this.displayWeeks = Math.ceil((dojo.date.getDaysInMonth(this.curMonth) + this._getAdjustedDay(this.curMonth)) / 7);
	}
	var days = this.displayWeeks * 7;
	if (dojo.date.diff(this.startDate, this.endDate, dojo.date.dateParts.DAY) < days) {
		this.staticDisplay = true;
		if (dojo.date.diff(nextDate, this.endDate, dojo.date.dateParts.DAY) > days) {
			this._preInitUI(this.startDate, true, false);
			nextDate = new Date(this.firstDay);
		}
		this.curMonth = new Date(nextDate);
		this.curMonth.setDate(nextDate.getDate() + 6);
		this.curMonth.setDate(1);
		var curClass = (nextDate.getMonth() == this.curMonth.getMonth()) ? "current" : "previous";
	}
	if (initUI) {
		this._initUI(days);
	}
}, _initUI:function (days) {
	dojo.dom.removeChildren(this.calendarDatesContainerNode);
	for (var i = 0; i < this.displayWeeks; i++) {
		this.calendarDatesContainerNode.appendChild(this.weekTemplate.cloneNode(true));
	}
	var nextDate = new Date(this.firstDay);
	this._setMonthLabel(this.curMonth.getMonth());
	this._setYearLabels(this.curMonth.getFullYear());
	var calendarNodes = this.calendarDatesContainerNode.getElementsByTagName("td");
	var calendarRows = this.calendarDatesContainerNode.getElementsByTagName("tr");
	var currentCalendarNode;
	for (i = 0; i < days; i++) {
		currentCalendarNode = calendarNodes.item(i);
		currentCalendarNode.innerHTML = nextDate.getDate();
		currentCalendarNode.setAttribute("djDateValue", nextDate.valueOf());
		var curClass = (nextDate.getMonth() != this.curMonth.getMonth() && Number(nextDate) < Number(this.curMonth)) ? "previous" : (nextDate.getMonth() == this.curMonth.getMonth()) ? "current" : "next";
		var mappedClass = curClass;
		if (this._isDisabledDate(nextDate)) {
			var classMap = {previous:"disabledPrevious", current:"disabledCurrent", next:"disabledNext"};
			mappedClass = classMap[curClass];
		}
		dojo.html.setClass(currentCalendarNode, this._getDateClassName(nextDate, mappedClass));
		if (dojo.html.hasClass(currentCalendarNode, this.classNames.selectedDate)) {
			this.selectedNode = currentCalendarNode;
		}
		nextDate = dojo.date.add(nextDate, dojo.date.dateParts.DAY, 1);
	}
	this.lastDay = dojo.date.add(nextDate, dojo.date.dateParts.DAY, -1);
	this._initControls();
}, _initControls:function () {
	var d = this.firstDay;
	var d2 = this.lastDay;
	var decWeek, incWeek, decMonth, incMonth, decYear, incYear;
	decWeek = incWeek = decMonth = incMonth = decYear = incYear = !this.staticDisplay;
	with (dojo.date.dateParts) {
		var add = dojo.date.add;
		if (decWeek && add(d, DAY, (-1 * (this._getAdjustedDay(d) + 1))) < this.startDate) {
			decWeek = decMonth = decYear = false;
		}
		if (incWeek && d2 > this.endDate) {
			incWeek = incMonth = incYear = false;
		}
		if (decMonth && add(d, DAY, -1) < this.startDate) {
			decMonth = decYear = false;
		}
		if (incMonth && add(d2, DAY, 1) > this.endDate) {
			incMonth = incYear = false;
		}
		if (decYear && add(d2, YEAR, -1) < this.startDate) {
			decYear = false;
		}
		if (incYear && add(d, YEAR, 1) > this.endDate) {
			incYear = false;
		}
	}
	function enableControl(node, enabled) {
		dojo.html.setVisibility(node, enabled ? "" : "hidden");
	}
	enableControl(this.decreaseWeekNode, decWeek);
	enableControl(this.increaseWeekNode, incWeek);
	enableControl(this.decreaseMonthNode, decMonth);
	enableControl(this.increaseMonthNode, incMonth);
	enableControl(this.previousYearLabelNode, decYear);
	enableControl(this.nextYearLabelNode, incYear);
}, _incrementWeek:function (evt) {
	var d = new Date(this.firstDay);
	switch (evt.target) {
	  case this.increaseWeekNode.getElementsByTagName("img").item(0):
	  case this.increaseWeekNode:
		var tmpDate = dojo.date.add(d, dojo.date.dateParts.WEEK, 1);
		if (tmpDate < this.endDate) {
			d = dojo.date.add(d, dojo.date.dateParts.WEEK, 1);
		}
		break;
	  case this.decreaseWeekNode.getElementsByTagName("img").item(0):
	  case this.decreaseWeekNode:
		if (d >= this.startDate) {
			d = dojo.date.add(d, dojo.date.dateParts.WEEK, -1);
		}
		break;
	}
	this._preInitUI(d, true, true);
}, _incrementMonth:function (evt) {
	var d = new Date(this.curMonth);
	var tmpDate = new Date(this.firstDay);
	switch (evt.currentTarget) {
	  case this.increaseMonthNode.getElementsByTagName("img").item(0):
	  case this.increaseMonthNode:
		tmpDate = dojo.date.add(tmpDate, dojo.date.dateParts.DAY, this.displayWeeks * 7);
		if (tmpDate < this.endDate) {
			d = dojo.date.add(d, dojo.date.dateParts.MONTH, 1);
		} else {
			var revertToEndDate = true;
		}
		break;
	  case this.decreaseMonthNode.getElementsByTagName("img").item(0):
	  case this.decreaseMonthNode:
		if (tmpDate > this.startDate) {
			d = dojo.date.add(d, dojo.date.dateParts.MONTH, -1);
		} else {
			var revertToStartDate = true;
		}
		break;
	}
	if (revertToStartDate) {
		d = new Date(this.startDate);
	} else {
		if (revertToEndDate) {
			d = new Date(this.endDate);
		}
	}
	this._preInitUI(d, false, true);
}, _incrementYear:function (evt) {
	var year = this.curMonth.getFullYear();
	var tmpDate = new Date(this.firstDay);
	switch (evt.target) {
	  case this.nextYearLabelNode:
		tmpDate = dojo.date.add(tmpDate, dojo.date.dateParts.YEAR, 1);
		if (tmpDate < this.endDate) {
			year++;
		} else {
			var revertToEndDate = true;
		}
		break;
	  case this.previousYearLabelNode:
		tmpDate = dojo.date.add(tmpDate, dojo.date.dateParts.YEAR, -1);
		if (tmpDate > this.startDate) {
			year--;
		} else {
			var revertToStartDate = true;
		}
		break;
	}
	var d;
	if (revertToStartDate) {
		d = new Date(this.startDate);
	} else {
		if (revertToEndDate) {
			d = new Date(this.endDate);
		} else {
			d = new Date(year, this.curMonth.getMonth(), 1);
		}
	}
	this._preInitUI(d, false, true);
}, onIncrementWeek:function (evt) {
	evt.stopPropagation();
	if (!this.staticDisplay) {
		this._incrementWeek(evt);
	}
}, onIncrementMonth:function (evt) {
	evt.stopPropagation();
	if (!this.staticDisplay) {
		this._incrementMonth(evt);
	}
}, onIncrementYear:function (evt) {
	evt.stopPropagation();
	if (!this.staticDisplay) {
		this._incrementYear(evt);
	}
}, _setMonthLabel:function (monthIndex) {
	this.monthLabelNode.innerHTML = dojo.date.getNames("months", "wide", "standAlone", this.lang)[monthIndex];
}, _setYearLabels:function (year) {
	var y = year - 1;
	var that = this;
	function f(n) {
		that[n + "YearLabelNode"].innerHTML = dojo.date.format(new Date(y++, 0), {formatLength:"yearOnly", locale:that.lang});
	}
	f("previous");
	f("current");
	f("next");
}, _getDateClassName:function (date, monthState) {
	var currentClassName = this.classNames[monthState];
	if ((!this.selectedIsUsed && this.value) && (Number(date) == Number(this.value))) {
		currentClassName = this.classNames.selectedDate + " " + currentClassName;
		this.selectedIsUsed = true;
	}
	if ((!this.currentIsUsed) && (Number(date) == Number(this.today))) {
		currentClassName = currentClassName + " " + this.classNames.currentDate;
		this.currentIsUsed = true;
	}
	return currentClassName;
}, onClick:function (evt) {
	dojo.event.browser.stopEvent(evt);
}, _handleUiClick:function (evt) {
	var eventTarget = evt.target;
	if (eventTarget.nodeType != dojo.dom.ELEMENT_NODE) {
		eventTarget = eventTarget.parentNode;
	}
	dojo.event.browser.stopEvent(evt);
	this.selectedIsUsed = this.todayIsUsed = false;
	if (dojo.html.hasClass(eventTarget, this.classNames["disabledPrevious"]) || dojo.html.hasClass(eventTarget, this.classNames["disabledCurrent"]) || dojo.html.hasClass(eventTarget, this.classNames["disabledNext"])) {
		return;
	}
	this.clickedNode = eventTarget;
	this.setDate(new Date(Number(dojo.html.getAttribute(eventTarget, "djDateValue"))));
}, onValueChanged:function (date) {
}, _isDisabledDate:function (dateObj) {
	if (dateObj < this.startDate || dateObj > this.endDate) {
		return true;
	}
	return this.isDisabledDate(dateObj, this.lang);
}, isDisabledDate:function (dateObj, locale) {
	return false;
}, _initFirstDay:function (dateObj, adj) {
	var d = new Date(dateObj);
	if (!adj) {
		d.setDate(1);
	}
	d.setDate(d.getDate() - this._getAdjustedDay(d, this.weekStartsOn));
	d.setHours(0, 0, 0, 0);
	return d;
}, _getAdjustedDay:function (dateObj) {
	var days = [0, 1, 2, 3, 4, 5, 6];
	if (this.weekStartsOn > 0) {
		for (var i = 0; i < this.weekStartsOn; i++) {
			days.unshift(days.pop());
		}
	}
	return days[dateObj.getDay()];
}, destroy:function () {
	dojo.widget.DatePicker.superclass.destroy.apply(this, arguments);
	dojo.html.destroyNode(this.weekTemplate);
}});

