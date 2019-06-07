/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.ResizableTextarea");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.LayoutContainer");
dojo.require("dojo.widget.ResizeHandle");
dojo.widget.defineWidget("dojo.widget.ResizableTextarea", dojo.widget.HtmlWidget, {templateString:"<div>\r\n\t<div style=\"border: 2px solid black; width: 90%; height: 200px;\"\r\n\t\tdojoAttachPoint=\"rootLayoutNode\">\r\n\t\t<div dojoAttachPoint=\"textAreaContainerNode\" \r\n\t\t\tstyle=\"border: 0px; margin: 0px; overflow: hidden;\">\r\n\t\t</div>\r\n\t\t<div dojoAttachPoint=\"statusBarContainerNode\" class=\"statusBar\">\r\n\t\t\t<div dojoAttachPoint=\"statusLabelNode\" \r\n\t\t\t\tclass=\"statusPanel\"\r\n\t\t\t\tstyle=\"padding-right: 0px; z-index: 1;\">drag to resize</div>\r\n\t\t\t<div dojoAttachPoint=\"resizeHandleNode\"></div>\r\n\t\t</div>\r\n\t</div>\r\n</div>\r\n", templateCssString:"div.statusBar {\r\n\tbackground-color: ThreeDFace;\r\n\theight: 28px;\r\n\tpadding: 1px;\r\n\toverflow: hidden;\r\n\tfont-size: 12px;\r\n}\r\n\r\ndiv.statusPanel {\r\n\tbackground-color: ThreeDFace;\r\n\tborder: 1px solid;\r\n\tborder-color: ThreeDShadow ThreeDHighlight ThreeDHighlight ThreeDShadow;\r\n\tmargin: 1px;\r\n\tpadding: 2px 6px;\r\n}\r\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/ResizableTextarea.css"), fillInTemplate:function (args, frag) {
	this.textAreaNode = this.getFragNodeRef(frag).cloneNode(true);
	dojo.body().appendChild(this.domNode);
	this.rootLayout = dojo.widget.createWidget("LayoutContainer", {minHeight:50, minWidth:100}, this.rootLayoutNode);
	this.textAreaContainer = dojo.widget.createWidget("LayoutContainer", {layoutAlign:"client"}, this.textAreaContainerNode);
	this.rootLayout.addChild(this.textAreaContainer);
	this.textAreaContainer.domNode.appendChild(this.textAreaNode);
	with (this.textAreaNode.style) {
		width = "100%";
		height = "100%";
	}
	this.statusBar = dojo.widget.createWidget("LayoutContainer", {layoutAlign:"bottom", minHeight:28}, this.statusBarContainerNode);
	this.rootLayout.addChild(this.statusBar);
	this.statusLabel = dojo.widget.createWidget("LayoutContainer", {layoutAlign:"client", minWidth:50}, this.statusLabelNode);
	this.statusBar.addChild(this.statusLabel);
	this.resizeHandle = dojo.widget.createWidget("ResizeHandle", {targetElmId:this.rootLayout.widgetId}, this.resizeHandleNode);
	this.statusBar.addChild(this.resizeHandle);
}});

