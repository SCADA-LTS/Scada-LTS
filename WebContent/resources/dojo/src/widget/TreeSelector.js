/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeSelector");
dojo.require("dojo.widget.HtmlWidget");
dojo.widget.defineWidget("dojo.widget.TreeSelector", dojo.widget.HtmlWidget, function () {
	this.eventNames = {};
	this.listenedTrees = [];
}, {widgetType:"TreeSelector", selectedNode:null, dieWithTree:false, eventNamesDefault:{select:"select", destroy:"destroy", deselect:"deselect", dblselect:"dblselect"}, initialize:function () {
	for (var name in this.eventNamesDefault) {
		if (dojo.lang.isUndefined(this.eventNames[name])) {
			this.eventNames[name] = this.widgetId + "/" + this.eventNamesDefault[name];
		}
	}
}, destroy:function () {
	dojo.event.topic.publish(this.eventNames.destroy, {source:this});
	return dojo.widget.HtmlWidget.prototype.destroy.apply(this, arguments);
}, listenTree:function (tree) {
	dojo.event.topic.subscribe(tree.eventNames.titleClick, this, "select");
	dojo.event.topic.subscribe(tree.eventNames.iconClick, this, "select");
	dojo.event.topic.subscribe(tree.eventNames.collapse, this, "onCollapse");
	dojo.event.topic.subscribe(tree.eventNames.moveFrom, this, "onMoveFrom");
	dojo.event.topic.subscribe(tree.eventNames.removeNode, this, "onRemoveNode");
	dojo.event.topic.subscribe(tree.eventNames.treeDestroy, this, "onTreeDestroy");
	this.listenedTrees.push(tree);
}, unlistenTree:function (tree) {
	dojo.event.topic.unsubscribe(tree.eventNames.titleClick, this, "select");
	dojo.event.topic.unsubscribe(tree.eventNames.iconClick, this, "select");
	dojo.event.topic.unsubscribe(tree.eventNames.collapse, this, "onCollapse");
	dojo.event.topic.unsubscribe(tree.eventNames.moveFrom, this, "onMoveFrom");
	dojo.event.topic.unsubscribe(tree.eventNames.removeNode, this, "onRemoveNode");
	dojo.event.topic.unsubscribe(tree.eventNames.treeDestroy, this, "onTreeDestroy");
	for (var i = 0; i < this.listenedTrees.length; i++) {
		if (this.listenedTrees[i] === tree) {
			this.listenedTrees.splice(i, 1);
			break;
		}
	}
}, onTreeDestroy:function (message) {
	this.unlistenTree(message.source);
	if (this.dieWithTree) {
		this.destroy();
	}
}, onCollapse:function (message) {
	if (!this.selectedNode) {
		return;
	}
	var node = message.source;
	var parent = this.selectedNode.parent;
	while (parent !== node && parent.isTreeNode) {
		parent = parent.parent;
	}
	if (parent.isTreeNode) {
		this.deselect();
	}
}, select:function (message) {
	var node = message.source;
	var e = message.event;
	if (this.selectedNode === node) {
		if (e.ctrlKey || e.shiftKey || e.metaKey) {
			this.deselect();
			return;
		}
		dojo.event.topic.publish(this.eventNames.dblselect, {node:node});
		return;
	}
	if (this.selectedNode) {
		this.deselect();
	}
	this.doSelect(node);
	dojo.event.topic.publish(this.eventNames.select, {node:node});
}, onMoveFrom:function (message) {
	if (message.child !== this.selectedNode) {
		return;
	}
	if (!dojo.lang.inArray(this.listenedTrees, message.newTree)) {
		this.deselect();
	}
}, onRemoveNode:function (message) {
	if (message.child !== this.selectedNode) {
		return;
	}
	this.deselect();
}, doSelect:function (node) {
	node.markSelected();
	this.selectedNode = node;
}, deselect:function () {
	var node = this.selectedNode;
	this.selectedNode = null;
	node.unMarkSelected();
	dojo.event.topic.publish(this.eventNames.deselect, {node:node});
}});

