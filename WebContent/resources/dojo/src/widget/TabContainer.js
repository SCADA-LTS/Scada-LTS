/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TabContainer");
dojo.require("dojo.lang.func");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.PageContainer");
dojo.require("dojo.event.*");
dojo.require("dojo.html.selection");
dojo.require("dojo.widget.html.layout");
dojo.widget.defineWidget("dojo.widget.TabContainer", dojo.widget.PageContainer, {labelPosition:"top", closeButton:"none", templateString:null, templateString:"<div id=\"${this.widgetId}\" class=\"dojoTabContainer\">\r\n\t<div dojoAttachPoint=\"tablistNode\"></div>\r\n\t<div class=\"dojoTabPaneWrapper\" dojoAttachPoint=\"containerNode\" dojoAttachEvent=\"onKey\" waiRole=\"tabpanel\"></div>\r\n</div>\r\n", templateCssString:".dojoTabContainer {\r\n\tposition : relative;\r\n}\r\n\r\n.dojoTabPaneWrapper {\r\n\tborder : 1px solid #6290d2;\r\n\t_zoom: 1; /* force IE6 layout mode so top border doesnt disappear */\r\n\tdisplay: block;\r\n\tclear: both;\r\n\toverflow: hidden;\r\n}\r\n\r\n.dojoTabLabels-top {\r\n\tposition : relative;\r\n\ttop : 0px;\r\n\tleft : 0px;\r\n\toverflow : visible;\r\n\tmargin-bottom : -1px;\r\n\twidth : 100%;\r\n\tz-index: 2;\t/* so the bottom of the tab label will cover up the border of dojoTabPaneWrapper */\r\n}\r\n\r\n.dojoTabNoLayout.dojoTabLabels-top .dojoTab {\r\n\tmargin-bottom: -1px;\r\n\t_margin-bottom: 0px; /* IE filter so top border lines up correctly */\r\n}\r\n\r\n.dojoTab {\r\n\tposition : relative;\r\n\tfloat : left;\r\n\tpadding-left : 9px;\r\n\tborder-bottom : 1px solid #6290d2;\r\n\tbackground : url(images/tab_left.gif) no-repeat left top;\r\n\tcursor: pointer;\r\n\twhite-space: nowrap;\r\n\tz-index: 3;\r\n}\r\n\r\n.dojoTab div {\r\n\tdisplay : block;\r\n\tpadding : 4px 15px 4px 6px;\r\n\tbackground : url(images/tab_top_right.gif) no-repeat right top;\r\n\tcolor : #333;\r\n\tfont-size : 90%;\r\n}\r\n\r\n.dojoTab .close {\r\n\tdisplay : inline-block;\r\n\theight : 12px;\r\n\twidth : 12px;\r\n\tpadding : 0 12px 0 0;\r\n\tmargin : 0 -10px 0 10px;\r\n\tcursor : default;\r\n\tfont-size: small;\r\n}\r\n\r\n.dojoTab .closeImage {\r\n\tbackground : url(images/tab_close.gif) no-repeat right top;\r\n}\r\n\r\n.dojoTab .closeHover {\r\n\tbackground-image : url(images/tab_close_h.gif);\r\n}\r\n\r\n.dojoTab.current {\r\n\tpadding-bottom : 1px;\r\n\tborder-bottom : 0;\r\n\tbackground-position : 0 -150px;\r\n}\r\n\r\n.dojoTab.current div {\r\n\tpadding-bottom : 5px;\r\n\tmargin-bottom : -1px;\r\n\tbackground-position : 100% -150px;\r\n}\r\n\r\n/* bottom tabs */\r\n\r\n.dojoTabLabels-bottom {\r\n\tposition : relative;\r\n\tbottom : 0px;\r\n\tleft : 0px;\r\n\toverflow : visible;\r\n\tmargin-top : -1px;\r\n\twidth : 100%;\r\n\tz-index: 2;\r\n}\r\n\r\n.dojoTabNoLayout.dojoTabLabels-bottom {\r\n\tposition : relative;\r\n}\r\n\r\n.dojoTabLabels-bottom .dojoTab {\r\n\tborder-top :  1px solid #6290d2;\r\n\tborder-bottom : 0;\r\n\tbackground : url(images/tab_bot_left.gif) no-repeat left bottom;\r\n}\r\n\r\n.dojoTabLabels-bottom .dojoTab div {\r\n\tbackground : url(images/tab_bot_right.gif) no-repeat right bottom;\r\n}\r\n\r\n.dojoTabLabels-bottom .dojoTab.current {\r\n\tborder-top : 0;\r\n\tbackground : url(images/tab_bot_left_curr.gif) no-repeat left bottom;\r\n}\r\n\r\n.dojoTabLabels-bottom .dojoTab.current div {\r\n\tpadding-top : 4px;\r\n\tbackground : url(images/tab_bot_right_curr.gif) no-repeat right bottom;\r\n}\r\n\r\n/* right-h tabs */\r\n\r\n.dojoTabLabels-right-h {\r\n\toverflow : visible;\r\n\tmargin-left : -1px;\r\n\tz-index: 2;\r\n}\r\n\r\n.dojoTabLabels-right-h .dojoTab {\r\n\tpadding-left : 0;\r\n\tborder-left :  1px solid #6290d2;\r\n\tborder-bottom : 0;\r\n\tbackground : url(images/tab_bot_right.gif) no-repeat right bottom;\r\n\tfloat : none;\r\n}\r\n\r\n.dojoTabLabels-right-h .dojoTab div {\r\n\tpadding : 4px 15px 4px 15px;\r\n}\r\n\r\n.dojoTabLabels-right-h .dojoTab.current {\r\n\tborder-left :  0;\r\n\tborder-bottom :  1px solid #6290d2;\r\n}\r\n\r\n/* left-h tabs */\r\n\r\n.dojoTabLabels-left-h {\r\n\toverflow : visible;\r\n\tmargin-right : -1px;\r\n\tz-index: 2;\r\n}\r\n\r\n.dojoTabLabels-left-h .dojoTab {\r\n\tborder-right :  1px solid #6290d2;\r\n\tborder-bottom : 0;\r\n\tfloat : none;\r\n\tbackground : url(images/tab_top_left.gif) no-repeat left top;\r\n}\r\n\r\n.dojoTabLabels-left-h .dojoTab.current {\r\n\tborder-right : 0;\r\n\tborder-bottom :  1px solid #6290d2;\r\n\tpadding-bottom : 0;\r\n\tbackground : url(images/tab_top_left.gif) no-repeat 0 -150px;\r\n}\r\n\r\n.dojoTabLabels-left-h .dojoTab div {\r\n\tbackground : 0;\r\n\tborder-bottom :  1px solid #6290d2;\r\n}\r\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/TabContainer.css"), selectedTab:"", postMixInProperties:function () {
	if (this.selectedTab) {
		dojo.deprecated("selectedTab deprecated, use selectedChild instead, will be removed in", "0.5");
		this.selectedChild = this.selectedTab;
	}
	if (this.closeButton != "none") {
		dojo.deprecated("closeButton deprecated, use closable='true' on each child instead, will be removed in", "0.5");
	}
	dojo.widget.TabContainer.superclass.postMixInProperties.apply(this, arguments);
}, fillInTemplate:function () {
	this.tablist = dojo.widget.createWidget("TabController", {id:this.widgetId + "_tablist", labelPosition:this.labelPosition, doLayout:this.doLayout, containerId:this.widgetId}, this.tablistNode);
	dojo.widget.TabContainer.superclass.fillInTemplate.apply(this, arguments);
}, postCreate:function (args, frag) {
	dojo.widget.TabContainer.superclass.postCreate.apply(this, arguments);
	this.onResized();
}, _setupChild:function (tab) {
	if (this.closeButton == "tab" || this.closeButton == "pane") {
		tab.closable = true;
	}
	dojo.html.addClass(tab.domNode, "dojoTabPane");
	dojo.widget.TabContainer.superclass._setupChild.apply(this, arguments);
}, onResized:function () {
	if (!this.doLayout) {
		return;
	}
	var labelAlign = this.labelPosition.replace(/-h/, "");
	var children = [{domNode:this.tablist.domNode, layoutAlign:labelAlign}, {domNode:this.containerNode, layoutAlign:"client"}];
	dojo.widget.html.layout(this.domNode, children);
	if (this.selectedChildWidget) {
		var containerSize = dojo.html.getContentBox(this.containerNode);
		this.selectedChildWidget.resizeTo(containerSize.width, containerSize.height);
	}
}, selectTab:function (tab, callingWidget) {
	dojo.deprecated("use selectChild() rather than selectTab(), selectTab() will be removed in", "0.5");
	this.selectChild(tab, callingWidget);
}, onKey:function (e) {
	if (e.keyCode == e.KEY_UP_ARROW && e.ctrlKey) {
		var button = this.correspondingTabButton || this.selectedTabWidget.tabButton;
		button.focus();
		dojo.event.browser.stopEvent(e);
	} else {
		if (e.keyCode == e.KEY_DELETE && e.altKey) {
			if (this.selectedChildWidget.closable) {
				this.closeChild(this.selectedChildWidget);
				dojo.event.browser.stopEvent(e);
			}
		}
	}
}, destroy:function () {
	this.tablist.destroy();
	dojo.widget.TabContainer.superclass.destroy.apply(this, arguments);
}});
dojo.widget.defineWidget("dojo.widget.TabController", dojo.widget.PageController, {templateString:"<div wairole='tablist' dojoAttachEvent='onKey'></div>", labelPosition:"top", doLayout:true, "class":"", buttonWidget:"TabButton", postMixInProperties:function () {
	if (!this["class"]) {
		this["class"] = "dojoTabLabels-" + this.labelPosition + (this.doLayout ? "" : " dojoTabNoLayout");
	}
	dojo.widget.TabController.superclass.postMixInProperties.apply(this, arguments);
}});
dojo.widget.defineWidget("dojo.widget.TabButton", dojo.widget.PageButton, {templateString:"<div class='dojoTab' dojoAttachEvent='onClick'>" + "<div dojoAttachPoint='innerDiv'>" + "<span dojoAttachPoint='titleNode' tabIndex='-1' waiRole='tab'>${this.label}</span>" + "<span dojoAttachPoint='closeButtonNode' class='close closeImage' style='${this.closeButtonStyle}'" + "	dojoAttachEvent='onMouseOver:onCloseButtonMouseOver; onMouseOut:onCloseButtonMouseOut; onClick:onCloseButtonClick'></span>" + "</div>" + "</div>", postMixInProperties:function () {
	this.closeButtonStyle = this.closeButton ? "" : "display: none";
	dojo.widget.TabButton.superclass.postMixInProperties.apply(this, arguments);
}, fillInTemplate:function () {
	dojo.html.disableSelection(this.titleNode);
	dojo.widget.TabButton.superclass.fillInTemplate.apply(this, arguments);
}, onCloseButtonClick:function (evt) {
	evt.stopPropagation();
	dojo.widget.TabButton.superclass.onCloseButtonClick.apply(this, arguments);
}});
dojo.widget.defineWidget("dojo.widget.a11y.TabButton", dojo.widget.TabButton, {imgPath:dojo.uri.moduleUri("dojo.widget", "templates/images/tab_close.gif"), templateString:"<div class='dojoTab' dojoAttachEvent='onClick;onKey'>" + "<div dojoAttachPoint='innerDiv'>" + "<span dojoAttachPoint='titleNode' tabIndex='-1' waiRole='tab'>${this.label}</span>" + "<img class='close' src='${this.imgPath}' alt='[x]' style='${this.closeButtonStyle}'" + "	dojoAttachEvent='onClick:onCloseButtonClick'>" + "</div>" + "</div>"});

