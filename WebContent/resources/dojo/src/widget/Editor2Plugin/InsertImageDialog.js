/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Editor2Plugin.InsertImageDialog");
dojo.widget.defineWidget("dojo.widget.Editor2InsertImageDialog", dojo.widget.Editor2DialogContent, {templateString:"<table cellspacing=\"1\" cellpadding=\"1\" border=\"0\" width=\"100%\" height=\"100%\">\r\n\t<tr>\r\n\t\t<td>\r\n\t\t\t<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" border=\"0\">\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td width=\"100%\">\r\n\t\t\t\t\t\t<span>URL</span>\r\n\t\t\t\t\t</td>\r\n\t\t\t\t\t<td style=\"display: none\" nowrap=\"nowrap\" rowspan=\"2\">\r\n\t\t\t\t\t\t<!--input id=\"btnBrowse\" onclick=\"BrowseServer();\" type=\"button\" value=\"Browse Server\"/-->\r\n\t\t\t\t\t</td>\r\n\t\t\t\t</tr>\r\n\t\t\t\t<tr>\r\n\t\t\t\t\t<td valign=\"top\">\r\n\t\t\t\t\t\t<input dojoAttachPoint=\"image_src\" style=\"width: 100%\" type=\"text\" />\r\n\t\t\t\t\t</td>\r\n\t\t\t\t</tr>\r\n\t\t\t</table>\r\n\t\t</td>\r\n\t</tr>\r\n\t<tr>\r\n\t\t<td>\r\n\t\t\t<span>Alternative Text</span><br />\r\n\t\t\t<input dojoAttachPoint=\"image_alt\" style=\"width: 100%\" type=\"text\" /><br />\r\n\t\t</td>\r\n\t</tr>\r\n\t<tr>\r\n\t\t<td valign=\"top\">\r\n\t\t\t<table><tr><td>\r\n\t\t\t\t\t\t<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td nowrap=\"nowrap\">\r\n\t\t\t\t\t\t\t\t\t<span>Width</span>&nbsp;</td>\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<input type=\"text\" size=\"3\" dojoAttachPoint=\"image_width\" /></td>\r\n\r\n\t\t\t\t\t\t\t\t<td rowspan=\"2\">\r\n\t\t\t\t\t\t\t\t\t<!--div id=\"btnLockSizes\" class=\"BtnLocked\" onmouseover=\"this.className = (bLockRatio ? 'BtnLocked' : 'BtnUnlocked' ) + ' BtnOver';\"\r\n\t\t\t\t\t\t\t\t\t\tonmouseout=\"this.className = (bLockRatio ? 'BtnLocked' : 'BtnUnlocked' );\" title=\"Lock Sizes\"\r\n\t\t\t\t\t\t\t\t\t\tonclick=\"SwitchLock(this);\">\r\n\t\t\t\t\t\t\t\t\t</div-->\r\n\t\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t\t<td rowspan=\"2\">\r\n\t\t\t\t\t\t\t\t\t<!--div id=\"btnResetSize\" class=\"BtnReset\" onmouseover=\"this.className='BtnReset BtnOver';\"\r\n\t\t\t\t\t\t\t\t\t\tonmouseout=\"this.className='BtnReset';\" title=\"Reset Size\" onclick=\"ResetSizes();\">\r\n\t\t\t\t\t\t\t\t\t</div-->\r\n\t\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t</tr>\r\n\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td nowrap=\"nowrap\">\r\n\t\t\t\t\t\t\t\t\t<span>Height</span>&nbsp;</td>\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<input type=\"text\" size=\"3\" dojoAttachPoint=\"image_height\" /></td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t</table>\r\n\t\t\t\t\t</td><td>\r\n\r\n\t\t\t\t\t\t<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n\t\t\t\t\t\t\t<tr>\r\n\r\n\t\t\t\t\t\t\t\t<td nowrap=\"nowrap\">\r\n\t\t\t\t\t\t\t\t\t<span >HSpace</span>&nbsp;</td>\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<input type=\"text\" size=\"2\" dojoAttachPoint=\"image_hspace\"/></td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td nowrap=\"nowrap\">\r\n\t\t\t\t\t\t\t\t\t<span >VSpace</span>&nbsp;</td>\r\n\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<input type=\"text\" size=\"2\" dojoAttachPoint=\"image_vspace\" /></td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t</table>\r\n\t\t\t\t\t</td></tr>\r\n\t\t\t\t\t<tr><td colspan=\"2\">\r\n\t\t\t\t\t\t<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td nowrap=\"nowrap\">\r\n\t\t\t\t\t\t\t\t\t<span>Border</span>&nbsp;</td>\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<input type=\"text\" size=\"2\" value=\"\" dojoAttachPoint=\"image_border\" /></td>\r\n\t\t\t\t\t\t\t\t<td>&nbsp;&nbsp;&nbsp;</td>\r\n\t\t\t\t\t\t\t\t<td nowrap=\"nowrap\">\r\n\t\t\t\t\t\t\t\t\t<span >Align</span>&nbsp;</td>\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<select dojoAttachPoint=\"image_align\">\r\n\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"\" selected=\"selected\"></option>\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"left\">Left</option>\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"absBottom\">Abs Bottom</option>\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"absMiddle\">Abs Middle</option>\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"baseline\">Baseline</option>\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"bottom\">Bottom</option>\r\n\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"middle\">Middle</option>\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"right\">Right</option>\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"textTop\">Text Top</option>\r\n\t\t\t\t\t\t\t\t\t\t<option value=\"top\">Top</option>\r\n\t\t\t\t\t\t\t\t\t</select>\r\n\t\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t</table>\r\n\t\t\t\t\t</td>\r\n\t\t\t\t</tr></table>\r\n\t\t</td>\r\n\t</tr>\r\n\t<tr><td>\r\n\t\t<table><tr>\r\n\t\t<td><button dojoType='Button' dojoAttachEvent='onClick:ok'>OK</button></td>\r\n\t\t<td><button dojoType='Button' dojoAttachEvent='onClick:cancel'>Cancel</button></td>\r\n\t\t</tr></table>\r\n\t</td></tr>\r\n</table>\r\n", editableAttributes:["src", "alt", "width", "height", "hspace", "vspace", "border", "align"], loadContent:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	this.imageNode = dojo.withGlobal(curInst.window, "getSelectedElement", dojo.html.selection);
	if (!this.imageNode) {
		this.imageNode = dojo.withGlobal(curInst.window, "getAncestorElement", dojo.html.selection, ["img"]);
	}
	var imageAttributes = {};
	this.extraAttribText = "";
	if (this.imageNode) {
		var attrs = this.imageNode.attributes;
		for (var i = 0; i < attrs.length; i++) {
			if (dojo.lang.find(this.editableAttributes, attrs[i].name.toLowerCase()) > -1) {
				imageAttributes[attrs[i].name] = attrs[i].value;
			} else {
				this.extraAttribText += attrs[i].name + "=\"" + attrs[i].value + "\" ";
			}
		}
	}
	for (var i = 0; i < this.editableAttributes.length; ++i) {
		name = this.editableAttributes[i];
		this["image_" + name].value = (imageAttributes[name] == undefined) ? "" : imageAttributes[name];
	}
	return true;
}, ok:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	var insertcmd = curInst.getCommand("inserthtml");
	var option = 0;
	var attstr = "";
	for (var i = 0; i < this.editableAttributes.length; ++i) {
		name = this.editableAttributes[i];
		var value = this["image_" + name].value;
		if (value.length > 0) {
			attstr += name + "=\"" + value + "\" ";
		}
	}
	if (this.imageNode) {
		dojo.withGlobal(curInst.window, "selectElement", dojo.html.selection, [this.imageNode]);
	}
	insertcmd.execute("<img " + attstr + this.extraAttribText + "/>");
	this.cancel();
}});

