/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.undo.Manager");
dojo.require("dojo.lang.common");
dojo.undo.Manager = function (parent) {
	this.clear();
	this._parent = parent;
};
dojo.extend(dojo.undo.Manager, {_parent:null, _undoStack:null, _redoStack:null, _currentManager:null, canUndo:false, canRedo:false, isUndoing:false, isRedoing:false, onUndo:function (manager, item) {
}, onRedo:function (manager, item) {
}, onUndoAny:function (manager, item) {
}, onRedoAny:function (manager, item) {
}, _updateStatus:function () {
	this.canUndo = this._undoStack.length > 0;
	this.canRedo = this._redoStack.length > 0;
}, clear:function () {
	this._undoStack = [];
	this._redoStack = [];
	this._currentManager = this;
	this.isUndoing = false;
	this.isRedoing = false;
	this._updateStatus();
}, undo:function () {
	if (!this.canUndo) {
		return false;
	}
	this.endAllTransactions();
	this.isUndoing = true;
	var top = this._undoStack.pop();
	if (top instanceof dojo.undo.Manager) {
		top.undoAll();
	} else {
		top.undo();
	}
	if (top.redo) {
		this._redoStack.push(top);
	}
	this.isUndoing = false;
	this._updateStatus();
	this.onUndo(this, top);
	if (!(top instanceof dojo.undo.Manager)) {
		this.getTop().onUndoAny(this, top);
	}
	return true;
}, redo:function () {
	if (!this.canRedo) {
		return false;
	}
	this.isRedoing = true;
	var top = this._redoStack.pop();
	if (top instanceof dojo.undo.Manager) {
		top.redoAll();
	} else {
		top.redo();
	}
	this._undoStack.push(top);
	this.isRedoing = false;
	this._updateStatus();
	this.onRedo(this, top);
	if (!(top instanceof dojo.undo.Manager)) {
		this.getTop().onRedoAny(this, top);
	}
	return true;
}, undoAll:function () {
	while (this._undoStack.length > 0) {
		this.undo();
	}
}, redoAll:function () {
	while (this._redoStack.length > 0) {
		this.redo();
	}
}, push:function (undo, redo, description) {
	if (!undo) {
		return;
	}
	if (this._currentManager == this) {
		this._undoStack.push({undo:undo, redo:redo, description:description});
	} else {
		this._currentManager.push.apply(this._currentManager, arguments);
	}
	this._redoStack = [];
	this._updateStatus();
}, concat:function (manager) {
	if (!manager) {
		return;
	}
	if (this._currentManager == this) {
		for (var x = 0; x < manager._undoStack.length; x++) {
			this._undoStack.push(manager._undoStack[x]);
		}
		if (manager._undoStack.length > 0) {
			this._redoStack = [];
		}
		this._updateStatus();
	} else {
		this._currentManager.concat.apply(this._currentManager, arguments);
	}
}, beginTransaction:function (description) {
	if (this._currentManager == this) {
		var mgr = new dojo.undo.Manager(this);
		mgr.description = description ? description : "";
		this._undoStack.push(mgr);
		this._currentManager = mgr;
		return mgr;
	} else {
		this._currentManager = this._currentManager.beginTransaction.apply(this._currentManager, arguments);
	}
}, endTransaction:function (flatten) {
	if (this._currentManager == this) {
		if (this._parent) {
			this._parent._currentManager = this._parent;
			if (this._undoStack.length == 0 || flatten) {
				var idx = dojo.lang.find(this._parent._undoStack, this);
				if (idx >= 0) {
					this._parent._undoStack.splice(idx, 1);
					if (flatten) {
						for (var x = 0; x < this._undoStack.length; x++) {
							this._parent._undoStack.splice(idx++, 0, this._undoStack[x]);
						}
						this._updateStatus();
					}
				}
			}
			return this._parent;
		}
	} else {
		this._currentManager = this._currentManager.endTransaction.apply(this._currentManager, arguments);
	}
}, endAllTransactions:function () {
	while (this._currentManager != this) {
		this.endTransaction();
	}
}, getTop:function () {
	if (this._parent) {
		return this._parent.getTop();
	} else {
		return this;
	}
}});

