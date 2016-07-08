/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.demoEngine.DemoItem");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.widget.defineWidget("my.widget.demoEngine.DemoItem", dojo.widget.HtmlWidget, {templateString:"<div dojoAttachPoint=\"domNode\">\r\n\t<div dojoAttachPoint=\"summaryBoxNode\">\r\n\t\t<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\r\n\t\t\t<tbody>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td dojoAttachPoint=\"screenshotTdNode\" valign=\"top\" width=\"1%\">\r\n\t\t\t\t\t\t<img dojoAttachPoint=\"thumbnailImageNode\" dojoAttachEvent=\"onclick: onSelectDemo\" />\r\n\t\t\t\t\t</td>\r\n\t\t\t\t\t<td dojoAttachPoint=\"summaryContainerNode\" valign=\"top\">\r\n\t\t\t\t\t\t<h1 dojoAttachPoint=\"nameNode\">\r\n\t\t\t\t\t\t</h1>\r\n\t\t\t\t\t\t<div dojoAttachPoint=\"summaryNode\">\r\n\t\t\t\t\t\t\t<p dojoAttachPoint=\"descriptionNode\"></p>\r\n\t\t\t\t\t\t\t<div dojoAttachPoint=\"viewDemoLinkNode\"><img dojoAttachPoint=\"viewDemoImageNode\"/ dojoAttachEvent=\"onclick: onSelectDemo\"></div>\r\n\t\t\t\t\t\t</div>\r\n\t\t\t\t\t</td>\r\n\t\t\t\t</tr>\r\n\t\t\t</tbody>\r\n\t\t</table>\r\n\t</div>\r\n</div>\r\n", templateCssString:".demoItemSummaryBox {\r\n\tbackground: #efefef;\r\n\tborder:1px solid #dae3ee;\r\n}\r\n\r\n.demoItemScreenshot {\r\n\tpadding:0.65em;\r\n\twidth:175px;\r\n\tborder-right:1px solid #fafafa;\r\n\ttext-align:center;\r\n\tcursor: pointer;\r\n}\r\n\r\n.demoItemWrapper{\r\n\tmargin-bottom:1em;\r\n}\r\n\r\n.demoItemWrapper a:link, .demoItemWrapper a:visited {\r\n\tcolor:#a6238f;\r\n\ttext-decoration:none;\r\n}\r\n\r\n.demoItemSummaryContainer {\r\n\tborder-left:1px solid #ddd;\r\n}\r\n\r\n.demoItemSummaryContainer h1 {\r\n\tbackground-color:#e8e8e8;\r\n\tborder-bottom: 1px solid #e6e6e6;\r\n\tcolor:#738fb9;\r\n\tmargin:1px;\r\n\tpadding:0.5em;\r\n\tfont-family:\"Lucida Grande\", \"Tahoma\", serif;\r\n\tfont-size:1.25em;\r\n\tfont-weight:normal;\r\n}\r\n\r\n.demoItemSummaryContainer h1 .packageSummary {\r\n\tdisplay:block;\r\n\tcolor:#000;\r\n\tfont-size:10px;\r\n\tmargin-top:2px;\r\n}\r\n\r\n.demoItemSummaryContainer .demoItemSummary{\r\n\tpadding:1em;\r\n}\r\n\r\n.demoItemSummaryContainer .demoItemSummary p {\r\n\tfont-size:0.85em;\r\n\tpadding:0;\r\n\tmargin:0;\r\n}\r\n\r\n.demoItemView {\r\n\ttext-align:right;\r\n\tcursor: pointer;\r\n}\r\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "demoEngine/templates/DemoItem.css"), postCreate:function () {
	dojo.html.addClass(this.domNode, this.domNodeClass);
	dojo.html.addClass(this.summaryBoxNode, this.summaryBoxClass);
	dojo.html.addClass(this.screenshotTdNode, this.screenshotTdClass);
	dojo.html.addClass(this.summaryContainerNode, this.summaryContainerClass);
	dojo.html.addClass(this.summaryNode, this.summaryClass);
	dojo.html.addClass(this.viewDemoLinkNode, this.viewDemoLinkClass);
	this.nameNode.appendChild(document.createTextNode(this.name));
	this.descriptionNode.appendChild(document.createTextNode(this.description));
	this.thumbnailImageNode.src = this.thumbnail;
	this.thumbnailImageNode.name = this.name;
	this.viewDemoImageNode.src = this.viewDemoImage;
	this.viewDemoImageNode.name = this.name;
}, onSelectDemo:function () {
}}, "", function () {
	this.demo = "";
	this.domNodeClass = "demoItemWrapper";
	this.summaryBoxNode = "";
	this.summaryBoxClass = "demoItemSummaryBox";
	this.nameNode = "";
	this.thumbnailImageNode = "";
	this.viewDemoImageNode = "";
	this.screenshotTdNode = "";
	this.screenshotTdClass = "demoItemScreenshot";
	this.summaryContainerNode = "";
	this.summaryContainerClass = "demoItemSummaryContainer";
	this.summaryNode = "";
	this.summaryClass = "demoItemSummary";
	this.viewDemoLinkNode = "";
	this.viewDemoLinkClass = "demoItemView";
	this.descriptionNode = "";
	this.name = "Some Demo";
	this.description = "This is the description of this demo.";
	this.thumbnail = "images/test_thumb.gif";
	this.viewDemoImage = "images/viewDemo.png";
});

