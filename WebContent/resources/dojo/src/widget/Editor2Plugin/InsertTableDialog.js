/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Editor2Plugin.InsertTableDialog");
dojo.widget.defineWidget("dojo.widget.Editor2InsertTableDialog", dojo.widget.Editor2DialogContent, {templateString:"<div>\r\n<table cellSpacing=\"1\" cellPadding=\"1\" width=\"100%\" border=\"0\">\r\n\t<tr>\r\n\t\t<td valign=\"top\">\r\n\t\t\t<table cellSpacing=\"0\" cellPadding=\"0\" border=\"0\">\r\n\t\t\t\t<tr>\r\n\r\n\t\t\t\t\t<td><span>Rows</span>:</td>\r\n\t\t\t\t\t<td>&nbsp;<input dojoAttachPoint=\"table_rows\" type=\"text\" maxLength=\"3\" size=\"2\" value=\"3\"></td>\r\n\t\t\t\t</tr>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td><span>Columns</span>:</td>\r\n\t\t\t\t\t<td>&nbsp;<input dojoAttachPoint=\"table_cols\" type=\"text\" maxLength=\"2\" size=\"2\" value=\"2\"></td>\r\n\t\t\t\t</tr>\r\n\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td>&nbsp;</td>\r\n\t\t\t\t\t<td>&nbsp;</td>\r\n\t\t\t\t</tr>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td><span>Border size</span>:</td>\r\n\t\t\t\t\t<td>&nbsp;<INPUT dojoAttachPoint=\"table_border\" type=\"text\" maxLength=\"2\" size=\"2\" value=\"1\"></td>\r\n\t\t\t\t</tr>\r\n\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td><span>Alignment</span>:</td>\r\n\t\t\t\t\t<td>&nbsp;<select dojoAttachPoint=\"table_align\">\r\n\t\t\t\t\t\t\t<option value=\"\" selected>&lt;Not set&gt;</option>\r\n\t\t\t\t\t\t\t<option value=\"left\">Left</option>\r\n\t\t\t\t\t\t\t<option value=\"center\">Center</option>\r\n\t\t\t\t\t\t\t<option value=\"right\">Right</option>\r\n\t\t\t\t\t\t</select></td>\r\n\t\t\t\t</tr>\r\n\t\t\t</table>\r\n\t\t</td>\r\n\t\t<td>&nbsp;&nbsp;&nbsp;</td>\r\n\t\t<td align=\"right\" valign=\"top\">\r\n\t\t\t<table cellSpacing=\"0\" cellPadding=\"0\" border=\"0\">\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td><span>Width</span>:</td>\r\n\t\t\t\t\t<td>&nbsp;<input dojoAttachPoint=\"table_width\" type=\"text\" maxLength=\"4\" size=\"3\"></td>\r\n\t\t\t\t\t<td>&nbsp;<select dojoAttachPoint=\"table_widthtype\">\r\n\t\t\t\t\t\t\t<option value=\"percent\" selected>percent</option>\r\n\t\t\t\t\t\t\t<option value=\"pixels\">pixels</option>\r\n\t\t\t\t\t\t</select></td>\r\n\r\n\t\t\t\t</tr>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td><span>Height</span>:</td>\r\n\t\t\t\t\t<td>&nbsp;<INPUT dojoAttachPoint=\"table_height\" type=\"text\" maxLength=\"4\" size=\"3\"></td>\r\n\t\t\t\t\t<td>&nbsp;<span>pixels</span></td>\r\n\t\t\t\t</tr>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td>&nbsp;</td>\r\n\t\t\t\t\t<td>&nbsp;</td>\r\n\t\t\t\t\t<td>&nbsp;</td>\r\n\t\t\t\t</tr>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td nowrap><span>Cell spacing</span>:</td>\r\n\t\t\t\t\t<td>&nbsp;<input dojoAttachPoint=\"table_cellspacing\" type=\"text\" maxLength=\"2\" size=\"2\" value=\"1\"></td>\r\n\t\t\t\t\t<td>&nbsp;</td>\r\n\r\n\t\t\t\t</tr>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td nowrap><span>Cell padding</span>:</td>\r\n\t\t\t\t\t<td>&nbsp;<input dojoAttachPoint=\"table_cellpadding\" type=\"text\" maxLength=\"2\" size=\"2\" value=\"1\"></td>\r\n\t\t\t\t\t<td>&nbsp;</td>\r\n\t\t\t\t</tr>\r\n\t\t\t</table>\r\n\t\t</td>\r\n\t</tr>\r\n</table>\r\n<table cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\">\r\n\t<tr>\r\n\t\t<td nowrap><span>Caption</span>:</td>\r\n\t\t<td>&nbsp;</td>\r\n\t\t<td width=\"100%\" nowrap>&nbsp;\r\n\t\t\t<input dojoAttachPoint=\"table_caption\" type=\"text\" style=\"WIDTH: 90%\"></td>\r\n\t</tr>\r\n\t<tr>\r\n\t\t<td nowrap><span>Summary</span>:</td>\r\n\t\t<td>&nbsp;</td>\r\n\t\t<td width=\"100%\" nowrap>&nbsp;\r\n\t\t\t<input dojoAttachPoint=\"table_summary\" type=\"text\" style=\"WIDTH: 90%\"></td>\r\n\t</tr>\r\n</table>\r\n<table><tr>\r\n<td><button dojoType='Button' dojoAttachEvent='onClick:ok'>Ok</button></td>\r\n<td><button dojoType='Button' dojoAttachEvent='onClick:cancel'>Cancel</button></td>\r\n</tr></table>\r\n</div>\r\n", editableAttributes:["summary", "height", "cellspacing", "cellpadding", "border", "align"], loadContent:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	curInst.saveSelection();
	this.tableNode = dojo.withGlobal(curInst.window, "getSelectedElement", dojo.html.selection);
	if (!this.tableNode || this.tableNode.tagName.toLowerCase() != "table") {
		this.tableNode = dojo.withGlobal(curInst.window, "getAncestorElement", dojo.html.selection, ["table"]);
	}
	var tableAttributes = {};
	this.extraAttribText = "";
	if (this.tableNode) {
		this["table_rows"].value = this.tableNode.rows.length;
		this["table_rows"].disabled = true;
		this["table_cols"].value = this.tableNode.rows[0].cells.length;
		this["table_cols"].disabled = true;
		if (this.tableNode.caption) {
			this["table_caption"].value = this.tableNode.caption.innerHTML;
		} else {
			this["table_caption"].value = "";
		}
		var width = this.tableNode.style.width || this.tableNode.width;
		if (width) {
			this["table_width"].value = parseInt(width);
			if (width.indexOf("%") > -1) {
				this["table_widthtype"].value = "percent";
			} else {
				this["table_widthtype"].value = "pixels";
			}
		} else {
			this["table_width"].value = "100";
		}
		var height = this.tableNode.style.height || this.tableNode.height;
		if (height) {
			this["table_height"].value = parseInt(width);
		} else {
			this["table_height"].value = "";
		}
		var attrs = this.tableNode.attributes;
		for (var i = 0; i < attrs.length; i++) {
			if (dojo.lang.find(this.editableAttributes, attrs[i].name.toLowerCase()) > -1) {
				tableAttributes[attrs[i].name] = attrs[i].value;
			} else {
				this.extraAttribText += attrs[i].name + "=\"" + attrs[i].value + "\" ";
			}
		}
	} else {
		this["table_rows"].value = 3;
		this["table_rows"].disabled = false;
		this["table_cols"].value = 2;
		this["table_cols"].disabled = false;
		this["table_width"].value = 100;
		this["table_widthtype"].value = "percent";
		this["table_height"].value = "";
	}
	for (var i = 0; i < this.editableAttributes.length; ++i) {
		name = this.editableAttributes[i];
		this["table_" + name].value = (tableAttributes[name] == undefined) ? "" : tableAttributes[name];
		if (name == "height" && tableAttributes[name] != undefined) {
			this["table_" + name].value = tableAttributes[name];
		}
	}
	return true;
}, ok:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	var args = {};
	args["rows"] = this["table_rows"].value;
	args["cols"] = this["table_cols"].value;
	args["caption"] = this["table_caption"].value;
	args["tableattrs"] = "";
	if (this["table_widthtype"].value == "percent") {
		args["tableattrs"] += "width=\"" + this["table_width"].value + "%\" ";
	} else {
		args["tableattrs"] += "width=\"" + this["table_width"].value + "px\" ";
	}
	for (var i = 0; i < this.editableAttributes.length; ++i) {
		var name = this.editableAttributes[i];
		var value = this["table_" + name].value;
		if (value.length > 0) {
			args["tableattrs"] += name + "=\"" + value + "\" ";
		}
	}
	if (!args["tableattrs"]) {
		args["tableattrs"] = "";
	}
	if (dojo.render.html.ie && !this["table_border"].value) {
		args["tableattrs"] += "class=\"dojoShowIETableBorders\" ";
	}
	var html = "<table " + args["tableattrs"] + ">";
	if (args["caption"]) {
		html += "<caption>" + args["caption"] + "</caption>";
	}
	var outertbody = "<tbody>";
	if (this.tableNode) {
		var tbody = this.tableNode.getElementsByTagName("tbody")[0];
		outertbody = tbody.outerHTML;
		if (!outertbody) {
			var cnode = tbody.cloneNode(true);
			var tmpnode = tbody.ownerDocument.createElement("div");
			tmpnode.appendChild(cnode);
			outertbody = tmpnode.innerHTML;
		}
		dojo.withGlobal(curInst.window, "selectElement", dojo.html.selection, [this.tableNode]);
	} else {
		var cols = "<tr>";
		for (var i = 0; i < +args.cols; i++) {
			cols += "<td></td>";
		}
		cols += "</tr>";
		for (var i = 0; i < args.rows; i++) {
			outertbody += cols;
		}
		outertbody += "</tbody>";
	}
	html += outertbody + "</table>";
	curInst.restoreSelection();
	curInst.execCommand("inserthtml", html);
	this.cancel();
}});

