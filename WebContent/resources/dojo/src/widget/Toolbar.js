/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Toolbar");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.style");
dojo.widget.defineWidget("dojo.widget.ToolbarContainer", dojo.widget.HtmlWidget, {isContainer:true, templateString:"<div class=\"toolbarContainer\" dojoAttachPoint=\"containerNode\"></div>", templateCssString:".toolbarContainer {\r\n\tborder-bottom : 0;\r\n\tbackground-color : #def;\r\n\tcolor : ButtonText;\r\n\tfont : Menu;\r\n\tbackground-image: url(images/toolbar-bg.gif);\r\n}\r\n\r\n.toolbar {\r\n\tpadding : 2px 4px;\r\n\tmin-height : 26px;\r\n\t_height : 26px;\r\n}\r\n\r\n.toolbarItem {\r\n\tfloat : left;\r\n\tpadding : 1px 2px;\r\n\tmargin : 0 2px 1px 0;\r\n\tcursor : pointer;\r\n}\r\n\r\n.toolbarItem.selected, .toolbarItem.down {\r\n\tmargin : 1px 1px 0 1px;\r\n\tpadding : 0px 1px;\r\n\tborder : 1px solid #bbf;\r\n\tbackground-color : #fafaff;\r\n}\r\n\r\n.toolbarButton img {\r\n\tvertical-align : bottom;\r\n}\r\n\r\n.toolbarButton span {\r\n\tline-height : 16px;\r\n\tvertical-align : middle;\r\n}\r\n\r\n.toolbarButton.hover {\r\n\tpadding : 0px 1px;\r\n\tborder : 1px solid #99c;\r\n}\r\n\r\n.toolbarItem.disabled {\r\n\topacity : 0.3;\r\n\tfilter : alpha(opacity=30);\r\n\tcursor : default;\r\n}\r\n\r\n.toolbarSeparator {\r\n\tcursor : default;\r\n}\r\n\r\n.toolbarFlexibleSpace {\r\n}\r\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/Toolbar.css"), getItem:function (name) {
	if (name instanceof dojo.widget.ToolbarItem) {
		return name;
	}
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.Toolbar) {
			var item = child.getItem(name);
			if (item) {
				return item;
			}
		}
	}
	return null;
}, getItems:function () {
	var items = [];
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.Toolbar) {
			items = items.concat(child.getItems());
		}
	}
	return items;
}, enable:function () {
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.Toolbar) {
			child.enable.apply(child, arguments);
		}
	}
}, disable:function () {
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.Toolbar) {
			child.disable.apply(child, arguments);
		}
	}
}, select:function (name) {
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.Toolbar) {
			child.select(arguments);
		}
	}
}, deselect:function (name) {
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.Toolbar) {
			child.deselect(arguments);
		}
	}
}, getItemsState:function () {
	var values = {};
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.Toolbar) {
			dojo.lang.mixin(values, child.getItemsState());
		}
	}
	return values;
}, getItemsActiveState:function () {
	var values = {};
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.Toolbar) {
			dojo.lang.mixin(values, child.getItemsActiveState());
		}
	}
	return values;
}, getItemsSelectedState:function () {
	var values = {};
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.Toolbar) {
			dojo.lang.mixin(values, child.getItemsSelectedState());
		}
	}
	return values;
}});
dojo.widget.defineWidget("dojo.widget.Toolbar", dojo.widget.HtmlWidget, {isContainer:true, templateString:"<div class=\"toolbar\" dojoAttachPoint=\"containerNode\" unselectable=\"on\" dojoOnMouseover=\"_onmouseover\" dojoOnMouseout=\"_onmouseout\" dojoOnClick=\"_onclick\" dojoOnMousedown=\"_onmousedown\" dojoOnMouseup=\"_onmouseup\"></div>", _getItem:function (node) {
	var start = new Date();
	var widget = null;
	while (node && node != this.domNode) {
		if (dojo.html.hasClass(node, "toolbarItem")) {
			var widgets = dojo.widget.manager.getWidgetsByFilter(function (w) {
				return w.domNode == node;
			});
			if (widgets.length == 1) {
				widget = widgets[0];
				break;
			} else {
				if (widgets.length > 1) {
					dojo.raise("Toolbar._getItem: More than one widget matches the node");
				}
			}
		}
		node = node.parentNode;
	}
	return widget;
}, _onmouseover:function (e) {
	var widget = this._getItem(e.target);
	if (widget && widget._onmouseover) {
		widget._onmouseover(e);
	}
}, _onmouseout:function (e) {
	var widget = this._getItem(e.target);
	if (widget && widget._onmouseout) {
		widget._onmouseout(e);
	}
}, _onclick:function (e) {
	var widget = this._getItem(e.target);
	if (widget && widget._onclick) {
		widget._onclick(e);
	}
}, _onmousedown:function (e) {
	var widget = this._getItem(e.target);
	if (widget && widget._onmousedown) {
		widget._onmousedown(e);
	}
}, _onmouseup:function (e) {
	var widget = this._getItem(e.target);
	if (widget && widget._onmouseup) {
		widget._onmouseup(e);
	}
}, addChild:function (item, pos, props) {
	var widget = dojo.widget.ToolbarItem.make(item, null, props);
	var ret = dojo.widget.Toolbar.superclass.addChild.call(this, widget, null, pos, null);
	return ret;
}, push:function () {
	for (var i = 0; i < arguments.length; i++) {
		this.addChild(arguments[i]);
	}
}, getItem:function (name) {
	if (name instanceof dojo.widget.ToolbarItem) {
		return name;
	}
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.ToolbarItem && child._name == name) {
			return child;
		}
	}
	return null;
}, getItems:function () {
	var items = [];
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.ToolbarItem) {
			items.push(child);
		}
	}
	return items;
}, getItemsState:function () {
	var values = {};
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.ToolbarItem) {
			values[child._name] = {selected:child._selected, enabled:!child.disabled};
		}
	}
	return values;
}, getItemsActiveState:function () {
	var values = this.getItemsState();
	for (var item in values) {
		values[item] = values[item].enabled;
	}
	return values;
}, getItemsSelectedState:function () {
	var values = this.getItemsState();
	for (var item in values) {
		values[item] = values[item].selected;
	}
	return values;
}, enable:function () {
	var items = arguments.length ? arguments : this.children;
	for (var i = 0; i < items.length; i++) {
		var child = this.getItem(items[i]);
		if (child instanceof dojo.widget.ToolbarItem) {
			child.enable(false, true);
		}
	}
}, disable:function () {
	var items = arguments.length ? arguments : this.children;
	for (var i = 0; i < items.length; i++) {
		var child = this.getItem(items[i]);
		if (child instanceof dojo.widget.ToolbarItem) {
			child.disable();
		}
	}
}, select:function () {
	for (var i = 0; i < arguments.length; i++) {
		var name = arguments[i];
		var item = this.getItem(name);
		if (item) {
			item.select();
		}
	}
}, deselect:function () {
	for (var i = 0; i < arguments.length; i++) {
		var name = arguments[i];
		var item = this.getItem(name);
		if (item) {
			item.disable();
		}
	}
}, setValue:function () {
	for (var i = 0; i < arguments.length; i += 2) {
		var name = arguments[i], value = arguments[i + 1];
		var item = this.getItem(name);
		if (item) {
			if (item instanceof dojo.widget.ToolbarItem) {
				item.setValue(value);
			}
		}
	}
}});
dojo.widget.defineWidget("dojo.widget.ToolbarItem", dojo.widget.HtmlWidget, {templateString:"<span unselectable=\"on\" class=\"toolbarItem\"></span>", _name:null, getName:function () {
	return this._name;
}, setName:function (value) {
	return (this._name = value);
}, getValue:function () {
	return this.getName();
}, setValue:function (value) {
	return this.setName(value);
}, _selected:false, isSelected:function () {
	return this._selected;
}, setSelected:function (is, force, preventEvent) {
	if (!this._toggleItem && !force) {
		return;
	}
	is = Boolean(is);
	if (force || !this.disabled && this._selected != is) {
		this._selected = is;
		this.update();
		if (!preventEvent) {
			this._fireEvent(is ? "onSelect" : "onDeselect");
			this._fireEvent("onChangeSelect");
		}
	}
}, select:function (force, preventEvent) {
	return this.setSelected(true, force, preventEvent);
}, deselect:function (force, preventEvent) {
	return this.setSelected(false, force, preventEvent);
}, _toggleItem:false, isToggleItem:function () {
	return this._toggleItem;
}, setToggleItem:function (value) {
	this._toggleItem = Boolean(value);
}, toggleSelected:function (force) {
	return this.setSelected(!this._selected, force);
}, isEnabled:function () {
	return !this.disabled;
}, setEnabled:function (is, force, preventEvent) {
	is = Boolean(is);
	if (force || this.disabled == is) {
		this.disabled = !is;
		this.update();
		if (!preventEvent) {
			this._fireEvent(this.disabled ? "onDisable" : "onEnable");
			this._fireEvent("onChangeEnabled");
		}
	}
	return !this.disabled;
}, enable:function (force, preventEvent) {
	return this.setEnabled(true, force, preventEvent);
}, disable:function (force, preventEvent) {
	return this.setEnabled(false, force, preventEvent);
}, toggleEnabled:function (force, preventEvent) {
	return this.setEnabled(this.disabled, force, preventEvent);
}, _icon:null, getIcon:function () {
	return this._icon;
}, setIcon:function (value) {
	var icon = dojo.widget.Icon.make(value);
	if (this._icon) {
		this._icon.setIcon(icon);
	} else {
		this._icon = icon;
	}
	var iconNode = this._icon.getNode();
	if (iconNode.parentNode != this.domNode) {
		if (this.domNode.hasChildNodes()) {
			this.domNode.insertBefore(iconNode, this.domNode.firstChild);
		} else {
			this.domNode.appendChild(iconNode);
		}
	}
	return this._icon;
}, _label:"", getLabel:function () {
	return this._label;
}, setLabel:function (value) {
	var ret = (this._label = value);
	if (!this.labelNode) {
		this.labelNode = document.createElement("span");
		this.domNode.appendChild(this.labelNode);
	}
	this.labelNode.innerHTML = "";
	this.labelNode.appendChild(document.createTextNode(this._label));
	this.update();
	return ret;
}, update:function () {
	if (this.disabled) {
		this._selected = false;
		dojo.html.addClass(this.domNode, "disabled");
		dojo.html.removeClass(this.domNode, "down");
		dojo.html.removeClass(this.domNode, "hover");
	} else {
		dojo.html.removeClass(this.domNode, "disabled");
		if (this._selected) {
			dojo.html.addClass(this.domNode, "selected");
		} else {
			dojo.html.removeClass(this.domNode, "selected");
		}
	}
	this._updateIcon();
}, _updateIcon:function () {
	if (this._icon) {
		if (this.disabled) {
			this._icon.disable();
		} else {
			if (this._cssHover) {
				this._icon.hover();
			} else {
				if (this._selected) {
					this._icon.select();
				} else {
					this._icon.enable();
				}
			}
		}
	}
}, _fireEvent:function (evt) {
	if (typeof this[evt] == "function") {
		var args = [this];
		for (var i = 1; i < arguments.length; i++) {
			args.push(arguments[i]);
		}
		this[evt].apply(this, args);
	}
}, _onmouseover:function (e) {
	if (this.disabled) {
		return;
	}
	dojo.html.addClass(this.domNode, "hover");
	this._fireEvent("onMouseOver");
}, _onmouseout:function (e) {
	dojo.html.removeClass(this.domNode, "hover");
	dojo.html.removeClass(this.domNode, "down");
	if (!this._selected) {
		dojo.html.removeClass(this.domNode, "selected");
	}
	this._fireEvent("onMouseOut");
}, _onclick:function (e) {
	if (!this.disabled && !this._toggleItem) {
		this._fireEvent("onClick");
	}
}, _onmousedown:function (e) {
	if (e.preventDefault) {
		e.preventDefault();
	}
	if (this.disabled) {
		return;
	}
	dojo.html.addClass(this.domNode, "down");
	if (this._toggleItem) {
		if (this.parent.preventDeselect && this._selected) {
			return;
		}
		this.toggleSelected();
	}
	this._fireEvent("onMouseDown");
}, _onmouseup:function (e) {
	dojo.html.removeClass(this.domNode, "down");
	this._fireEvent("onMouseUp");
}, onClick:function () {
}, onMouseOver:function () {
}, onMouseOut:function () {
}, onMouseDown:function () {
}, onMouseUp:function () {
}, fillInTemplate:function (args, frag) {
	if (args.name) {
		this._name = args.name;
	}
	if (args.selected) {
		this.select();
	}
	if (args.disabled) {
		this.disable();
	}
	if (args.label) {
		this.setLabel(args.label);
	}
	if (args.icon) {
		this.setIcon(args.icon);
	}
	if (args.toggleitem || args.toggleItem) {
		this.setToggleItem(true);
	}
}});
dojo.widget.ToolbarItem.make = function (wh, whIsType, props) {
	var item = null;
	if (wh instanceof Array) {
		item = dojo.widget.createWidget("ToolbarButtonGroup", props);
		item.setName(wh[0]);
		for (var i = 1; i < wh.length; i++) {
			item.addChild(wh[i]);
		}
	} else {
		if (wh instanceof dojo.widget.ToolbarItem) {
			item = wh;
		} else {
			if (wh instanceof dojo.uri.Uri) {
				item = dojo.widget.createWidget("ToolbarButton", dojo.lang.mixin(props || {}, {icon:new dojo.widget.Icon(wh.toString())}));
			} else {
				if (whIsType) {
					item = dojo.widget.createWidget(wh, props);
				} else {
					if (typeof wh == "string" || wh instanceof String) {
						switch (wh.charAt(0)) {
						  case "|":
						  case "-":
						  case "/":
							item = dojo.widget.createWidget("ToolbarSeparator", props);
							break;
						  case " ":
							if (wh.length == 1) {
								item = dojo.widget.createWidget("ToolbarSpace", props);
							} else {
								item = dojo.widget.createWidget("ToolbarFlexibleSpace", props);
							}
							break;
						  default:
							if (/\.(gif|jpg|jpeg|png)$/i.test(wh)) {
								item = dojo.widget.createWidget("ToolbarButton", dojo.lang.mixin(props || {}, {icon:new dojo.widget.Icon(wh.toString())}));
							} else {
								item = dojo.widget.createWidget("ToolbarButton", dojo.lang.mixin(props || {}, {label:wh.toString()}));
							}
						}
					} else {
						if (wh && wh.tagName && /^img$/i.test(wh.tagName)) {
							item = dojo.widget.createWidget("ToolbarButton", dojo.lang.mixin(props || {}, {icon:wh}));
						} else {
							item = dojo.widget.createWidget("ToolbarButton", dojo.lang.mixin(props || {}, {label:wh.toString()}));
						}
					}
				}
			}
		}
	}
	return item;
};
dojo.widget.defineWidget("dojo.widget.ToolbarButtonGroup", dojo.widget.ToolbarItem, {isContainer:true, templateString:"<span unselectable=\"on\" class=\"toolbarButtonGroup\" dojoAttachPoint=\"containerNode\"></span>", defaultButton:"", postCreate:function () {
	for (var i = 0; i < this.children.length; i++) {
		this._injectChild(this.children[i]);
	}
}, addChild:function (item, pos, props) {
	var widget = dojo.widget.ToolbarItem.make(item, null, dojo.lang.mixin(props || {}, {toggleItem:true}));
	var ret = dojo.widget.ToolbarButtonGroup.superclass.addChild.call(this, widget, null, pos, null);
	this._injectChild(widget);
	return ret;
}, _injectChild:function (widget) {
	dojo.event.connect(widget, "onSelect", this, "onChildSelected");
	dojo.event.connect(widget, "onDeselect", this, "onChildDeSelected");
	if (widget._name == this.defaultButton || (typeof this.defaultButton == "number" && this.children.length - 1 == this.defaultButton)) {
		widget.select(false, true);
	}
}, getItem:function (name) {
	if (name instanceof dojo.widget.ToolbarItem) {
		return name;
	}
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.ToolbarItem && child._name == name) {
			return child;
		}
	}
	return null;
}, getItems:function () {
	var items = [];
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.ToolbarItem) {
			items.push(child);
		}
	}
	return items;
}, onChildSelected:function (e) {
	this.select(e._name);
}, onChildDeSelected:function (e) {
	this._fireEvent("onChangeSelect", this._value);
}, enable:function (force, preventEvent) {
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.ToolbarItem) {
			child.enable(force, preventEvent);
			if (child._name == this._value) {
				child.select(force, preventEvent);
			}
		}
	}
}, disable:function (force, preventEvent) {
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.ToolbarItem) {
			child.disable(force, preventEvent);
		}
	}
}, _value:"", getValue:function () {
	return this._value;
}, select:function (name, force, preventEvent) {
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (child instanceof dojo.widget.ToolbarItem) {
			if (child._name == name) {
				child.select(force, preventEvent);
				this._value = name;
			} else {
				child.deselect(true, true);
			}
		}
	}
	if (!preventEvent) {
		this._fireEvent("onSelect", this._value);
		this._fireEvent("onChangeSelect", this._value);
	}
}, setValue:this.select, preventDeselect:false});
dojo.widget.defineWidget("dojo.widget.ToolbarButton", dojo.widget.ToolbarItem, {fillInTemplate:function (args, frag) {
	dojo.widget.ToolbarButton.superclass.fillInTemplate.call(this, args, frag);
	dojo.html.addClass(this.domNode, "toolbarButton");
	if (this._icon) {
		this.setIcon(this._icon);
	}
	if (this._label) {
		this.setLabel(this._label);
	}
	if (!this._name) {
		if (this._label) {
			this.setName(this._label);
		} else {
			if (this._icon) {
				var src = this._icon.getSrc("enabled").match(/[\/^]([^\.\/]+)\.(gif|jpg|jpeg|png)$/i);
				if (src) {
					this.setName(src[1]);
				}
			} else {
				this._name = this._widgetId;
			}
		}
	}
}});
dojo.widget.defineWidget("dojo.widget.ToolbarDialog", dojo.widget.ToolbarButton, {fillInTemplate:function (args, frag) {
	dojo.widget.ToolbarDialog.superclass.fillInTemplate.call(this, args, frag);
	dojo.event.connect(this, "onSelect", this, "showDialog");
	dojo.event.connect(this, "onDeselect", this, "hideDialog");
}, showDialog:function (e) {
	dojo.lang.setTimeout(dojo.event.connect, 1, document, "onmousedown", this, "deselect");
}, hideDialog:function (e) {
	dojo.event.disconnect(document, "onmousedown", this, "deselect");
}});
dojo.widget.defineWidget("dojo.widget.ToolbarMenu", dojo.widget.ToolbarDialog, {});
dojo.widget.ToolbarMenuItem = function () {
};
dojo.widget.defineWidget("dojo.widget.ToolbarSeparator", dojo.widget.ToolbarItem, {templateString:"<span unselectable=\"on\" class=\"toolbarItem toolbarSeparator\"></span>", defaultIconPath:new dojo.uri.moduleUri("dojo.widget", "templates/buttons/sep.gif"), fillInTemplate:function (args, frag, skip) {
	dojo.widget.ToolbarSeparator.superclass.fillInTemplate.call(this, args, frag);
	this._name = this.widgetId;
	if (!skip) {
		if (!this._icon) {
			this.setIcon(this.defaultIconPath);
		}
		this.domNode.appendChild(this._icon.getNode());
	}
}, _onmouseover:null, _onmouseout:null, _onclick:null, _onmousedown:null, _onmouseup:null});
dojo.widget.defineWidget("dojo.widget.ToolbarSpace", dojo.widget.ToolbarSeparator, {fillInTemplate:function (args, frag, skip) {
	dojo.widget.ToolbarSpace.superclass.fillInTemplate.call(this, args, frag, true);
	if (!skip) {
		dojo.html.addClass(this.domNode, "toolbarSpace");
	}
}});
dojo.widget.defineWidget("dojo.widget.ToolbarSelect", dojo.widget.ToolbarItem, {templateString:"<span class=\"toolbarItem toolbarSelect\" unselectable=\"on\"><select dojoAttachPoint=\"selectBox\" dojoOnChange=\"changed\"></select></span>", fillInTemplate:function (args, frag) {
	dojo.widget.ToolbarSelect.superclass.fillInTemplate.call(this, args, frag, true);
	var keys = args.values;
	var i = 0;
	for (var val in keys) {
		var opt = document.createElement("option");
		opt.setAttribute("value", keys[val]);
		opt.innerHTML = val;
		this.selectBox.appendChild(opt);
	}
}, changed:function (e) {
	this._fireEvent("onSetValue", this.selectBox.value);
}, setEnabled:function (is, force, preventEvent) {
	var ret = dojo.widget.ToolbarSelect.superclass.setEnabled.call(this, is, force, preventEvent);
	this.selectBox.disabled = this.disabled;
	return ret;
}, _onmouseover:null, _onmouseout:null, _onclick:null, _onmousedown:null, _onmouseup:null});
dojo.widget.Icon = function (enabled, disabled, hovered, selected) {
	if (!arguments.length) {
		throw new Error("Icon must have at least an enabled state");
	}
	var states = ["enabled", "disabled", "hovered", "selected"];
	var currentState = "enabled";
	var domNode = document.createElement("img");
	this.getState = function () {
		return currentState;
	};
	this.setState = function (value) {
		if (dojo.lang.inArray(states, value)) {
			if (this[value]) {
				currentState = value;
				var img = this[currentState];
				if ((dojo.render.html.ie55 || dojo.render.html.ie60) && img.src && img.src.match(/[.]png$/i)) {
					domNode.width = img.width || img.offsetWidth;
					domNode.height = img.height || img.offsetHeight;
					domNode.setAttribute("src", dojo.uri.moduleUri("dojo.widget", "templates/images/blank.gif").uri);
					domNode.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + img.src + "',sizingMethod='image')";
				} else {
					domNode.setAttribute("src", img.src);
				}
			}
		} else {
			throw new Error("Invalid state set on Icon (state: " + value + ")");
		}
	};
	this.setSrc = function (state, value) {
		if (/^img$/i.test(value.tagName)) {
			this[state] = value;
		} else {
			if (typeof value == "string" || value instanceof String || value instanceof dojo.uri.Uri) {
				this[state] = new Image();
				this[state].src = value.toString();
			}
		}
		return this[state];
	};
	this.setIcon = function (icon) {
		for (var i = 0; i < states.length; i++) {
			if (icon[states[i]]) {
				this.setSrc(states[i], icon[states[i]]);
			}
		}
		this.update();
	};
	this.enable = function () {
		this.setState("enabled");
	};
	this.disable = function () {
		this.setState("disabled");
	};
	this.hover = function () {
		this.setState("hovered");
	};
	this.select = function () {
		this.setState("selected");
	};
	this.getSize = function () {
		return {width:domNode.width || domNode.offsetWidth, height:domNode.height || domNode.offsetHeight};
	};
	this.setSize = function (w, h) {
		domNode.width = w;
		domNode.height = h;
		return {width:w, height:h};
	};
	this.getNode = function () {
		return domNode;
	};
	this.getSrc = function (state) {
		if (state) {
			return this[state].src;
		}
		return domNode.src || "";
	};
	this.update = function () {
		this.setState(currentState);
	};
	for (var i = 0; i < states.length; i++) {
		var arg = arguments[i];
		var state = states[i];
		this[state] = null;
		if (!arg) {
			continue;
		}
		this.setSrc(state, arg);
	}
	this.enable();
};
dojo.widget.Icon.make = function (a, b, c, d) {
	for (var i = 0; i < arguments.length; i++) {
		if (arguments[i] instanceof dojo.widget.Icon) {
			return arguments[i];
		}
	}
	return new dojo.widget.Icon(a, b, c, d);
};
dojo.widget.defineWidget("dojo.widget.ToolbarColorDialog", dojo.widget.ToolbarDialog, {palette:"7x10", fillInTemplate:function (args, frag) {
	dojo.widget.ToolbarColorDialog.superclass.fillInTemplate.call(this, args, frag);
	this.dialog = dojo.widget.createWidget("ColorPalette", {palette:this.palette});
	this.dialog.domNode.style.position = "absolute";
	dojo.event.connect(this.dialog, "onColorSelect", this, "_setValue");
}, _setValue:function (color) {
	this._value = color;
	this._fireEvent("onSetValue", color);
}, showDialog:function (e) {
	dojo.widget.ToolbarColorDialog.superclass.showDialog.call(this, e);
	var abs = dojo.html.getAbsolutePosition(this.domNode, true);
	var y = abs.y + dojo.html.getBorderBox(this.domNode).height;
	this.dialog.showAt(abs.x, y);
}, hideDialog:function (e) {
	dojo.widget.ToolbarColorDialog.superclass.hideDialog.call(this, e);
	this.dialog.hide();
}});

