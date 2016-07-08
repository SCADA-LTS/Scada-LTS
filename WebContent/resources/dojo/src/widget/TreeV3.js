/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeV3");
dojo.require("dojo.widget.TreeWithNode");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.TreeNodeV3");
dojo.widget.defineWidget("dojo.widget.TreeV3", [dojo.widget.HtmlWidget, dojo.widget.TreeWithNode], function () {
	this.eventNames = {};
	this.DndAcceptTypes = [];
	this.actionsDisabled = [];
	this.listeners = [];
	this.tree = this;
}, {DndMode:"", defaultChildWidget:null, defaultChildTitle:"New Node", eagerWidgetInstantiation:false, eventNamesDefault:{afterTreeCreate:"afterTreeCreate", beforeTreeDestroy:"beforeTreeDestroy", beforeNodeDestroy:"beforeNodeDestroy", afterChangeTree:"afterChangeTree", afterSetFolder:"afterSetFolder", afterUnsetFolder:"afterUnsetFolder", beforeMoveFrom:"beforeMoveFrom", beforeMoveTo:"beforeMoveTo", afterMoveFrom:"afterMoveFrom", afterMoveTo:"afterMoveTo", afterAddChild:"afterAddChild", afterDetach:"afterDetach", afterExpand:"afterExpand", beforeExpand:"beforeExpand", afterSetTitle:"afterSetTitle", afterCollapse:"afterCollapse", beforeCollapse:"beforeCollapse"}, classPrefix:"Tree", style:"", allowAddChildToLeaf:true, unsetFolderOnEmpty:true, DndModes:{BETWEEN:1, ONTO:2}, DndAcceptTypes:"", templateCssString:"/* indent for all tree children excepts root */\r\n.TreeNode {\r\n	background-image : url('../templates/images/TreeV3/i.gif');\r\n	background-position : top left;\r\n	background-repeat : repeat-y;\r\n	margin-left: 19px;\r\n	zoom: 1;\r\n}\r\n.TreeIsRoot {\r\n	margin-left: 0;\r\n}\r\n \r\n/* left vertical line (grid) for all nodes */\r\n.TreeIsLast {\r\n	background-image: url('../templates/images/TreeV3/i_half.gif');\r\n	background-repeat : no-repeat;\r\n}\r\n \r\n.TreeExpandOpen .TreeExpand {\r\n	background-image: url('../templates/images/TreeV3/expand_minus.gif');\r\n}\r\n \r\n/* closed is higher priority than open */\r\n.TreeExpandClosed .TreeExpand {\r\n	background-image: url('../templates/images/TreeV3/expand_plus.gif');\r\n}\r\n \r\n/* highest priority */\r\n.TreeExpandLeaf .TreeExpand {\r\n	background-image: url('../templates/images/TreeV3/expand_leaf.gif');\r\n}\r\n\r\n/* \r\nshould always override any expand setting, but do not touch children.\r\nif I add .TreeExpand .TreeExpandLoading same time and put it to top/bottom, then it will take precedence over +- for all descendants or always fail\r\nso I have to remove TreeExpand and process this one specifically\r\n*/\r\n\r\n.TreeExpandLoading   {\r\n	width: 18px;\r\n	height: 18px;\r\n	float: left;\r\n	display: inline;\r\n	background-repeat : no-repeat;\r\n	background-image: url('../templates/images/TreeV3/expand_loading.gif');\r\n}\r\n \r\n.TreeContent {\r\n	min-height: 18px;\r\n	min-width: 18px;\r\n	margin-left:18px;\r\n	cursor: default;\r\n	/* can't make inline - multiline bugs */\r\n}\r\n\r\n.TreeIEContent {\r\n\theight: 18px;\r\n}\r\n \r\n.TreeExpand {\r\n	width: 18px;\r\n	height: 18px;\r\n	float: left;\r\n	display: inline;\r\n	background-repeat : no-repeat;\r\n}\r\n \r\n/* same style as IE selection */\r\n.TreeNodeEmphasized {\r\n	background-color: Highlight;\r\n	color: HighlightText;\r\n}\r\n \r\n.TreeContent .RichTextEditable, .TreeContent .RichTextEditable iframe {\r\n	  background-color: #ffc;\r\n	  color: black;\r\n}\r\n\r\n/* don't use :focus due to opera's lack of support on div's */\r\n.TreeLabelFocused {\r\n	  outline: 1px invert dotted;\r\n}\r\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/TreeV3.css"), templateString:"<div style=\"${this.style}\">\n</div>", isExpanded:true, isTree:true, createNode:function (data) {
	data.tree = this.widgetId;
	if (data.widgetName) {
		return dojo.widget.createWidget(data.widgetName, data);
	} else {
		if (this.defaultChildWidget.prototype.createSimple) {
			return this.defaultChildWidget.prototype.createSimple(data);
		} else {
			var ns = this.defaultChildWidget.prototype.ns;
			var wt = this.defaultChildWidget.prototype.widgetType;
			return dojo.widget.createWidget(ns + ":" + wt, data);
		}
	}
}, makeNodeTemplate:function () {
	var domNode = document.createElement("div");
	dojo.html.setClass(domNode, this.classPrefix + "Node " + this.classPrefix + "ExpandLeaf " + this.classPrefix + "ChildrenNo");
	this.nodeTemplate = domNode;
	var expandNode = document.createElement("div");
	var clazz = this.classPrefix + "Expand";
	if (dojo.render.html.ie) {
		clazz = clazz + " " + this.classPrefix + "IEExpand";
	}
	dojo.html.setClass(expandNode, clazz);
	this.expandNodeTemplate = expandNode;
	var labelNode = document.createElement("span");
	dojo.html.setClass(labelNode, this.classPrefix + "Label");
	this.labelNodeTemplate = labelNode;
	var contentNode = document.createElement("div");
	var clazz = this.classPrefix + "Content";
	if (dojo.render.html.ie && !dojo.render.html.ie70) {
		clazz = clazz + " " + this.classPrefix + "IEContent";
	}
	dojo.html.setClass(contentNode, clazz);
	this.contentNodeTemplate = contentNode;
	domNode.appendChild(expandNode);
	domNode.appendChild(contentNode);
	contentNode.appendChild(labelNode);
}, makeContainerNodeTemplate:function () {
	var div = document.createElement("div");
	div.style.display = "none";
	dojo.html.setClass(div, this.classPrefix + "Container");
	this.containerNodeTemplate = div;
}, actions:{ADDCHILD:"ADDCHILD"}, getInfo:function () {
	var info = {widgetId:this.widgetId, objectId:this.objectId};
	return info;
}, adjustEventNames:function () {
	for (var name in this.eventNamesDefault) {
		if (dojo.lang.isUndefined(this.eventNames[name])) {
			this.eventNames[name] = this.widgetId + "/" + this.eventNamesDefault[name];
		}
	}
}, adjustDndMode:function () {
	var _this = this;
	var DndMode = 0;
	dojo.lang.forEach(this.DndMode.split(";"), function (elem) {
		var mode = _this.DndModes[dojo.string.trim(elem).toUpperCase()];
		if (mode) {
			DndMode = DndMode | mode;
		}
	});
	this.DndMode = DndMode;
}, destroy:function () {
	dojo.event.topic.publish(this.tree.eventNames.beforeTreeDestroy, {source:this});
	return dojo.widget.HtmlWidget.prototype.destroy.apply(this, arguments);
}, initialize:function (args) {
	this.domNode.widgetId = this.widgetId;
	for (var i = 0; i < this.actionsDisabled.length; i++) {
		this.actionsDisabled[i] = this.actionsDisabled[i].toUpperCase();
	}
	if (!args.defaultChildWidget) {
		this.defaultChildWidget = dojo.widget.TreeNodeV3;
	} else {
		this.defaultChildWidget = dojo.lang.getObjPathValue(args.defaultChildWidget);
	}
	this.adjustEventNames();
	this.adjustDndMode();
	this.makeNodeTemplate();
	this.makeContainerNodeTemplate();
	this.containerNode = this.domNode;
	dojo.html.setClass(this.domNode, this.classPrefix + "Container");
	var _this = this;
	dojo.lang.forEach(this.listeners, function (elem) {
		var t = dojo.lang.isString(elem) ? dojo.widget.byId(elem) : elem;
		t.listenTree(_this);
	});
}, postCreate:function () {
	dojo.event.topic.publish(this.eventNames.afterTreeCreate, {source:this});
}, move:function (child, newParent, index) {
	if (!child.parent) {
		dojo.raise(this.widgetType + ": child can be moved only while it's attached");
	}
	var oldParent = child.parent;
	var oldTree = child.tree;
	var oldIndex = child.getParentIndex();
	var newTree = newParent.tree;
	var newParent = newParent;
	var newIndex = index;
	var message = {oldParent:oldParent, oldTree:oldTree, oldIndex:oldIndex, newParent:newParent, newTree:newTree, newIndex:newIndex, child:child};
	dojo.event.topic.publish(oldTree.eventNames.beforeMoveFrom, message);
	dojo.event.topic.publish(newTree.eventNames.beforeMoveTo, message);
	this.doMove.apply(this, arguments);
	dojo.event.topic.publish(oldTree.eventNames.afterMoveFrom, message);
	dojo.event.topic.publish(newTree.eventNames.afterMoveTo, message);
}, doMove:function (child, newParent, index) {
	child.doDetach();
	newParent.doAddChild(child, index);
}, toString:function () {
	return "[" + this.widgetType + " ID:" + this.widgetId + "]";
}});

