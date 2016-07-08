/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.RemoteTabController");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.TabContainer");
dojo.require("dojo.event.*");
dojo.deprecated("dojo.widget.RemoteTabController is slated for removal in 0.5; use PageController or TabController instead.", "0.5");
dojo.widget.defineWidget("dojo.widget.RemoteTabController", dojo.widget.TabController, {templateCssString:".dojoRemoteTabController {\r\n\tposition: relative;\r\n}\r\n\r\n.dojoRemoteTab {\r\n\tposition : relative;\r\n\tfloat : left;\r\n\tpadding-left : 9px;\r\n\tborder-bottom : 1px solid #6290d2;\r\n\tbackground : url(images/tab_left.gif) no-repeat left top;\r\n\tcursor: pointer;\r\n\twhite-space: nowrap;\r\n\tz-index: 3;\r\n}\r\n\r\n.dojoRemoteTab div {\r\n\tdisplay : block;\r\n\tpadding : 4px 15px 4px 6px;\r\n\tbackground : url(images/tab_top_right.gif) no-repeat right top;\r\n\tcolor : #333;\r\n\tfont-size : 90%;\r\n}\r\n\r\n.dojoRemoteTabPaneClose {\r\n\tposition : absolute;\r\n\tbottom : 0px;\r\n\tright : 6px;\r\n\theight : 12px;\r\n\twidth : 12px;\r\n\tbackground : url(images/tab_close.gif) no-repeat right top;\r\n}\r\n\r\n.dojoRemoteTabPaneCloseHover {\r\n\tbackground-image : url(images/tab_close_h.gif);\r\n}\r\n\r\n.dojoRemoteTabClose {\r\n\tdisplay : inline-block;\r\n\theight : 12px;\r\n\twidth : 12px;\r\n\tpadding : 0 12px 0 0;\r\n\tmargin : 0 -10px 0 10px;\r\n\tbackground : url(images/tab_close.gif) no-repeat right top;\r\n\tcursor : default;\r\n}\r\n\r\n.dojoRemoteTabCloseHover {\r\n\tbackground-image : url(images/tab_close_h.gif);\r\n}\r\n\r\n.dojoRemoteTab.current {\r\n\tpadding-bottom : 1px;\r\n\tborder-bottom : 0;\r\n\tbackground-position : 0 -150px;\r\n}\r\n\r\n.dojoRemoteTab.current div {\r\n\tpadding-bottom : 5px;\r\n\tmargin-bottom : -1px;\r\n\tbackground-position : 100% -150px;\r\n}\r\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/RemoteTabControl.css"), templateString:"<div dojoAttachPoint=\"domNode\" wairole=\"tablist\"></div>", "class":"dojoRemoteTabController", tabContainer:"", postMixInProperties:function () {
	this.containerId = this.tabContainer;
	dojo.widget.RemoteTabController.superclass.postMixInProperties.apply(this, arguments);
}, fillInTemplate:function () {
	dojo.html.addClass(this.domNode, this["class"]);
	if (this.tabContainer) {
		dojo.addOnLoad(dojo.lang.hitch(this, "setupTabs"));
	}
	dojo.widget.RemoteTabController.superclass.fillInTemplate.apply(this, arguments);
}});

