/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeContextMenu");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.Menu2");
dojo.widget.defineWidget("dojo.widget.TreeContextMenu", dojo.widget.PopupMenu2, function () {
	this.listenedTrees = [];
}, {open:function (x, y, parentMenu, explodeSrc) {
	var result = dojo.widget.PopupMenu2.prototype.open.apply(this, arguments);
	dojo.event.topic.publish(this.eventNames.open, {menu:this});
	return result;
}, listenTree:function (tree) {
	var nodes = tree.getDescendants();
	for (var i = 0; i < nodes.length; i++) {
		if (!nodes[i].isTreeNode) {
			continue;
		}
		this.bindDomNode(nodes[i].labelNode);
	}
	var _this = this;
	dojo.event.topic.subscribe(tree.eventNames.createDOMNode, this, "onCreateDOMNode");
	dojo.event.topic.subscribe(tree.eventNames.moveFrom, this, "onMoveFrom");
	dojo.event.topic.subscribe(tree.eventNames.moveTo, this, "onMoveTo");
	dojo.event.topic.subscribe(tree.eventNames.removeNode, this, "onRemoveNode");
	dojo.event.topic.subscribe(tree.eventNames.addChild, this, "onAddChild");
	dojo.event.topic.subscribe(tree.eventNames.treeDestroy, this, "onTreeDestroy");
	this.listenedTrees.push(tree);
}, unlistenTree:function (tree) {
	dojo.event.topic.unsubscribe(tree.eventNames.createDOMNode, this, "onCreateDOMNode");
	dojo.event.topic.unsubscribe(tree.eventNames.moveFrom, this, "onMoveFrom");
	dojo.event.topic.unsubscribe(tree.eventNames.moveTo, this, "onMoveTo");
	dojo.event.topic.unsubscribe(tree.eventNames.removeNode, this, "onRemoveNode");
	dojo.event.topic.unsubscribe(tree.eventNames.addChild, this, "onAddChild");
	dojo.event.topic.unsubscribe(tree.eventNames.treeDestroy, this, "onTreeDestroy");
	for (var i = 0; i < this.listenedTrees.length; i++) {
		if (this.listenedTrees[i] === tree) {
			this.listenedTrees.splice(i, 1);
			break;
		}
	}
}, onTreeDestroy:function (message) {
	this.unlistenTree(message.source);
}, bindTreeNode:function (node) {
	var _this = this;
	dojo.lang.forEach(node.getDescendants(), function (e) {
		_this.bindDomNode(e.labelNode);
	});
}, unBindTreeNode:function (node) {
	var _this = this;
	dojo.lang.forEach(node.getDescendants(), function (e) {
		_this.unBindDomNode(e.labelNode);
	});
}, onCreateDOMNode:function (message) {
	this.bindTreeNode(message.source);
}, onMoveFrom:function (message) {
	if (!dojo.lang.inArray(this.listenedTrees, message.newTree)) {
		this.unBindTreeNode(message.child);
	}
}, onMoveTo:function (message) {
	if (dojo.lang.inArray(this.listenedTrees, message.newTree)) {
		this.bindTreeNode(message.child);
	}
}, onRemoveNode:function (message) {
	this.unBindTreeNode(message.child);
}, onAddChild:function (message) {
	if (message.domNodeInitialized) {
		this.bindTreeNode(message.child);
	}
}});
dojo.widget.defineWidget("dojo.widget.TreeMenuItem", dojo.widget.MenuItem2, {treeActions:"", initialize:function (args, frag) {
	this.treeActions = this.treeActions.split(",");
	for (var i = 0; i < this.treeActions.length; i++) {
		this.treeActions[i] = this.treeActions[i].toUpperCase();
	}
}, getTreeNode:function () {
	var menu = this;
	while (!(menu instanceof dojo.widget.TreeContextMenu)) {
		menu = menu.parent;
	}
	var source = menu.getTopOpenEvent().target;
	while (!source.getAttribute("treeNode") && source.tagName != "body") {
		source = source.parentNode;
	}
	if (source.tagName == "body") {
		dojo.raise("treeNode not detected");
	}
	var treeNode = dojo.widget.manager.getWidgetById(source.getAttribute("treeNode"));
	return treeNode;
}, menuOpen:function (message) {
	var treeNode = this.getTreeNode();
	this.setDisabled(false);
	var _this = this;
	dojo.lang.forEach(_this.treeActions, function (action) {
		_this.setDisabled(treeNode.actionIsDisabled(action));
	});
}, toString:function () {
	return "[" + this.widgetType + " node " + this.getTreeNode() + "]";
}});

