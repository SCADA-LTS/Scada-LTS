/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

var mango = {};

// last time update

var lasTimeUpdate;

//
// String prototypes
//
String.prototype.startsWith = function(str) {
    if (str.length > this.length)
        return false;
    for (var i=0; i<str.length; i++) {
        if (str.charAt(i) != this.charAt(i))
            return false;
    }
    return true;
}

String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g,"");
}

String.prototype.replaceAll = function(de, para){
	str = this;
    var pos = str.indexOf(de);
    while (pos > -1){
		str = str.replace(de, para);
		pos = str.indexOf(de);
	}
    return (str);
}

//
// Custom exception to string
//
function errorToString(e) {
    try {
        return e.name +": "+ e.message +" ("+ e.fileName +":"+ e.lineNumber +")";
    }
    catch (e2) {
        return e.name +": "+ e.message +" ("+ e.fileName +")";
    }
}

function isBlank(str){
  return (!str || /^\s*$/.test(str));
};
	
function defaultIfBlank(str, defaultStr) {
  return (isBlank(str) ? defaultStr: str);
};


//
// Long poll
//
mango.longPoll = {};
mango.longPoll.pollRequest = {};
mango.longPoll.pollSessionId = Math.round(Math.random() * 1000000000);

mango.longPoll.start = function() {
    MiscDwr.initializeLongPoll(mango.longPoll.pollSessionId, mango.longPoll.pollRequest, mango.longPoll.pollCB);
    dojo.addOnUnload(function() { MiscDwr.terminateLongPoll(mango.longPoll.pollSessionId); });
};

mango.longPoll.poll = function() {
    MiscDwr.doLongPoll(mango.longPoll.pollSessionId, mango.longPoll.pollCB);
}

mango.longPoll.pollCB = function(response) {
    if (response.terminated)
        return;
    
    if (typeof(response.highestUnsilencedAlarmLevel) != "undefined") {
        if (response.highestUnsilencedAlarmLevel > 0) {
            setAlarmLevelImg(response.highestUnsilencedAlarmLevel, $("__header__alarmLevelImg"));
            setAlarmLevelText(response.highestUnsilencedAlarmLevel, $("__header__alarmLevelText"));
            if (!mango.header.evtVisualizer.started)
                mango.header.evtVisualizer.start();
            show("__header__alarmLevelDiv");
            mango.soundPlayer.play("level"+ response.highestUnsilencedAlarmLevel);
        }
        else {
        	mango.header.evtVisualizer.stop();
            hide("__header__alarmLevelDiv");
            mango.soundPlayer.stop();
        }
    }
    if (response.runtime) {
      lasTimeUpdate = response.runtime;
    }
    if (response.watchListStates)
        mango.view.watchList.setData(response.watchListStates);
    
    if (response.pointDetailsState)
        mango.view.pointDetails.setData(response.pointDetailsState);
    
    if (response.viewStates)
        mango.view.setData(response.viewStates);
    
    if (typeof(response.pendingAlarmsContent) != "undefined")
        updatePendingAlarmsContent(response.pendingAlarmsContent);
    
    if (response.customViewStates)
        mango.view.setData(response.customViewStates);

    // Poll again immediately.
    mango.longPoll.poll();
}


//
// Input control
//
function setDisabled(node, disabled) {
    node = getNodeIfString(node);
    if (disabled) {
        node.disabled = true;
        dojo.html.addClass(node, "formDisabled");
    }
    else {
        node.disabled = false;
        dojo.html.removeClass(node, "formDisabled");
    }
}

function dump(o) {
    for (var p in o)
        dojo.debug(p +"="+ o[p]);
}

function contains(arr, e) {
    for (var i=0; i<arr.length; i++) {
        if (arr[i] == e)
            return true;
    }
    return false;
}

// onmouseover and onmouseout betterment.
function isMouseLeaveOrEnter(e, handler) {
	  if (e.type != 'mouseout' && e.type != 'mouseover')
		  return false;
	  var reltg = e.relatedTarget ? e.relatedTarget : e.type == 'mouseout' ? e.toElement : e.fromElement;
	  while (reltg && reltg != handler)
		  reltg = reltg.parentNode;
	  return (reltg != handler);
}      

//
// Common functions (delegates to Dojo functions)
//
function show(node, styleType) {
    if (!styleType)
        styleType = '';
    if (node != null)
        getNodeIfString(node).style.display = styleType;
}

function hide(node) {
    try {
        getNodeIfString(node).style.display = 'none';
    }
    catch (err) {
        throw "hide failed for node "+ node +", "+ err.message;
    }
}

function display(node, showNode, styleType) {
    if (showNode)
        show(node, styleType);
    else
        hide(node);
}

function showMenu(node, xoffset, yoffset) {
    node = getNodeIfString(node);
    var bounds = dojo.html.abs(node.parentNode);
    var anc = findRelativeAncestor(node);
    if (anc) {
        var rbounds = dojo.html.abs(anc);
        bounds.x -= rbounds.x;
        bounds.y -= rbounds.y;
    }
    node.style.left = (bounds.x + xoffset) +"px";
    node.style.top = (bounds.y + yoffset) +"px";
    showLayer(node);
}

function showLayer(node) {
    getNodeIfString(node).style.visibility = "visible";
}

function hideLayer(node) {
    getNodeIfString(node).style.visibility = "hidden";
}

function updatePositionXY(compId) {
    updateXY("static", compId);
    updateXY("settings", compId);
    updateXY("compound", compId);
}

function updateXY(name, compId) {
    var id = compId.substring(1);
    if (document.getElementById(name + id) != null) {
        var pDim = getNodeBounds($(compId));
        $set(name + "PositionX", pDim.x);
        $set(name + "PositionY", pDim.y);
    }
}

function setZIndex(node, amt) {
    node = getNodeIfString(node);
    node.style.zIndex = amt;
}

function findRelativeAncestor(node) {
    var pos;
    while (node = node.parentNode) {
        if (!node.style)
            continue;
        pos = node.style.position;
        if (pos == "relative" || pos == "absolute")
            return node;
    }
    return null;
}


//
// Get the dimensions of an element. Uses Dojo functions. Assumes a content box model, which may not be correct
// in all situations.
//
function getNodeBounds(node) {
    node = getNodeIfString(node);
    var box = dojo.html.getContentBox(node);
    return {
        x : dojo.html.getPixelValue(node, "left", dojo.html.isPositionAbsolute(node)),
        y : dojo.html.getPixelValue(node, "top", dojo.html.isPositionAbsolute(node)),
        w : box.width,
        h : box.height
    };
}

function getAbsoluteNodeBounds(node) {
    var box = dojo.html.getContentBox(node);
    var x = y = 0;
    var tempNode = node;
    while (tempNode) {
        x += tempNode.offsetLeft;
        y += tempNode.offsetTop;
        tempNode = tempNode.offsetParent;
    }
    return {
        x : x,
        y : y,
        w : box.width,
        h : box.height
    };
}

function getY(node)
{
    node = getNodeIfString(node);
    var value = 0;
    while( node != null ) {
        value += node.offsetTop;
        node = node.offsetParent;
    }
    return value;
}

function getX(node)
{
    node = getNodeIfString(node);
    var value = 0;
    while( node != null ) {
        value += node.offsetLeft;
        node = node.offsetParent;
    }
    return value;
}

//
// Class for fading images in and out. Requires Dojo.
//
function IEBlinker(/*element*/node, /*milliseconds*/onTime, /*milliseconds*/offTime) {
    this.target = node;
    this.on = onTime;
    if (!this.on)
        this.on = 700;
    this.off = offTime;
    if (!this.off)
        this.off = 300;
    
    this.state = true;
    this.timeoutId;
    this.started = false;
    
    this.start = function() {
        this.started = true;
        this.timeoutId = null;
        this.state = !this.state;
        if (this.state)
            showLayer(this.target);
        else
            hideLayer(this.target);
        this.timeoutId = setTimeout(dojo.lang.hitch(this, "start"), this.state ? this.on : this.off);
    };
    
    this.stop = function() {
        if (this.started) {
            this.started = false;
            clearTimeout(this.timeoutId);
            this.timeoutId = null;
        }
        showLayer(this.target);
    };
}

function startIEBlinker(/*element*/node, /*milliseconds*/onTime, /*milliseconds*/offTime) {
    node = getNodeIfString(node);
    var blinker = new IEBlinker(node, onTime, offTime);
    if (node.blinker)
        stopIEBlinker(node);
    node.blinker = blinker;
    blinker.start();
}

function hasIEBlinker(node) {
    node = getNodeIfString(node);
    if (node.blinker)
        return true;
    return false;
}

function stopIEBlinker(node) {
    node = getNodeIfString(node);
    var blinker = node.blinker;
    if (blinker) {
        blinker.stop();
        node.blinker = null;
    }
}

function ImageFader(/*element*/imgNode, /*milliseconds*/cycleRate, /*0<float<1*/cycleStep) {
    this.im = imgNode;
    this.rate = cycleRate;
    if (!this.rate)
        this.rate = 100;
    this.step = cycleStep;
    if (!this.step)
        this.step = 0.1;
    
    this.increasing = false;
    this.timeoutId;
    this.started = false;
    
    this.start = function() {
        this.started = true;
        this.timeoutId = null;
        
        var op = dojo.html.getOpacity(this.im, "opacity");
        if (op >= 1)
            this.increasing = false;
        else if (op <= 0)
            this.increasing = true;
    
        if (this.increasing)
            op += this.step;
        else
            op -= this.step;
        
        dojo.html.setOpacity(this.im, op);
        //console.log('setOpacity: ' + op + ' on ' + this.im );
    
        this.timeoutId = setTimeout(dojo.lang.hitch(this, "start"), this.rate);
    };
    
    this.stop = function() {
        if (this.started) {
            this.started = false;
            clearTimeout(this.timeoutId);
            this.timeoutId = null;
        }
        dojo.html.setOpacity(this.im, 1);
    };
}

function startImageFader(node, disableOnclick) {
    if (disableOnclick)
        this.disableOnclick(node);
    
    node = getNodeIfString(node);
    var fader = new ImageFader(node);
    if (node.fader)
        stopImageFader(node);
    node.fader = fader;
    fader.start();
}

function hasImageFader(node) {
    node = getNodeIfString(node);
    if (node.fader)
        return true;
    return false;
}

function stopImageFader(node) {
    enableOnclick(node);
    node = getNodeIfString(node);
    var fader = node.fader;
    if (fader) {
        fader.stop();
        node.fader = null;
    }
}

function disableOnclick(node) {
    node.disabledOnclick = node.onclick;
    node.onclick = null;
}

function enableOnclick(node) {
    if (node.disabledOnclick) {
        node.onclick = node.disabledOnclick;
        node.disabledOnclick = null;;
    }
}

function updateTemplateNode(elem, replaceText) {
    var i;
    for (i=0; i<elem.attributes.length; i++) {
        if (elem.attributes[i].value && elem.attributes[i].value.indexOf('_TEMPLATE_') != -1)
            elem.attributes[i].value = elem.attributes[i].value.replace(/_TEMPLATE_/, replaceText);
    }
    for (var i=0; i<elem.childNodes.length; i++) {
        if (elem.childNodes[i].attributes)
            updateTemplateNode(elem.childNodes[i], replaceText);
    }
}

function getElementsByMangoName(node, mangoName, result) {
    if (!result)
        result = new Array();
    if (node.mangoName == mangoName)
        result[result.length] = node;
    for (var i=0; i<node.childNodes.length; i++)
        getElementsByMangoName(node.childNodes[i], mangoName, result);
    return result;
}

function createFromTemplate(templateId, id, parentId) {
    var content = $(templateId).cloneNode(true);
    updateTemplateNode(content, id);
    content.mangoId = id;
    $(parentId).appendChild(content);
    show(content);
    return content;
}

function getMangoId(node) {
    while (!(node.mangoId))
        node = node.parentNode;
    return node.mangoId;
}

function setAlarmLevelImg(alarmLevel, imgNode) {
    if (alarmLevel == 0)
        updateImg(imgNode, "images/flag_green.png", mango.i18n["common.alarmLevel.none"], false);
    else if (alarmLevel == 1)
        updateImg(imgNode, "images/flag_blue.png", mango.i18n["common.alarmLevel.info"], true);
    else if (alarmLevel == 2)
        updateImg(imgNode, "images/flag_yellow.png", mango.i18n["common.alarmLevel.urgent"], true);
    else if (alarmLevel == 3)
        updateImg(imgNode, "images/flag_orange.png", mango.i18n["common.alarmLevel.critical"], true);
    else if (alarmLevel == 4)
        updateImg(imgNode, "images/flag_red.png", mango.i18n["common.alarmLevel.lifeSafety"], true);
    else
        updateImg(imgNode, "(unknown)", "(unknown)", true);
}

function setAlarmLevelText(alarmLevel, textNode) {
    if (alarmLevel == 0)
        textNode.innerHTML = "";
    else if (alarmLevel == 1)
        textNode.innerHTML = mango.i18n["common.alarmLevel.info"];
    else if (alarmLevel == 2)
        textNode.innerHTML = mango.i18n["common.alarmLevel.urgent"];
    else if (alarmLevel == 3)
        textNode.innerHTML = mango.i18n["common.alarmLevel.critical"];
    else if (alarmLevel == 4)
        textNode.innerHTML = mango.i18n["common.alarmLevel.lifeSafety"];
    else
        textNode.innerHTML = "Unknown alarm level: "+ alarmLevel;
}

function setUserImg(admin, disabled, imgNode) {
    if (disabled)
        updateImg(imgNode, "images/user_disabled.png", mango.i18n["common.disabled"], true);
    else if (admin)
        updateImg(imgNode, "images/user_suit.png", mango.i18n["common.administrator"], true);
    else
        updateImg(imgNode, "images/user_green.png", mango.i18n["common.user"], true);
}

function setScheduledEventImg(disabled, imgNode) {
    if (disabled)
        updateImg(imgNode, "images/clock_disabled.png", mango.i18n["js.disabledSe"], true);
    else
        updateImg(imgNode, "images/clock.png", mango.i18n["scheduledEvents.se"], true);
}

function setCompoundEventImg(disabled, imgNode) {
    if (disabled)
        updateImg(imgNode, "images/multi_bell_disabled.png", mango.i18n["js.disabledCed"], true);
    else
        updateImg(imgNode, "images/multi_bell.png", mango.i18n["compoundDetectors.compoundEventDetector"], true);
}

function setDataSourceStatusImg(enabled, imgNode) {
    if (enabled)
        updateImg(imgNode, "images/database_go.png", mango.i18n["common.enabledToggle"], true);
    else
        updateImg(imgNode, "images/database_stop.png", mango.i18n["common.disabledToggle"], true);
}

function setDataPointStatusImg(enabled, imgNode) {
    if (enabled)
        updateImg(imgNode, "images/brick_go.png", mango.i18n["common.enabledToggle"], true);
    else
        updateImg(imgNode, "images/brick_stop.png", mango.i18n["common.disabledToggle"], true);
}

function setPublisherStatusImg(enabled, imgNode) {
    if (enabled)
        updateImg(imgNode, "images/transmit_go.png", mango.i18n["common.enabledToggle"], true);
    else
        updateImg(imgNode, "images/transmit_stop.png", mango.i18n["common.disabledToggle"], true);
}

function setPointLinkImg(disabled, imgNode) {
    if (disabled)
        updateImg(imgNode, "images/link_break.png", mango.i18n["js.disabledPointLink"], true);
    else
        updateImg(imgNode, "images/link.png", mango.i18n["pointLinks.pointLink"], true);
}

function updateImg(imgNode, src, text, visible, styleType) {
    if (visible) {
        if (imgNode != null) {
            imgNode = getNodeIfString(imgNode);
            show(imgNode, styleType);
            if (src)
                imgNode.src = src;
            if (text) {
                imgNode.title = text;
                imgNode.alt = text;
            }
        }
    }
    else
        hide(imgNode);
}

// For panels that default as displayed
function togglePanelVisibility(img, panelId, visTitle, invisTitle) {
    var visible = true;
    if (!img.minimized)
        visible = false;
    togglePanelVisibilityImpl(img, panelId, visTitle, invisTitle, visible);
}

// For panels that default as hidden
function togglePanelVisibility2(img, panelId, visTitle, invisTitle) {
    var visible = true;
    if (img.minimized == false)
        visible = false;
    togglePanelVisibilityImpl(img, panelId, visTitle, invisTitle, visible);
}

function togglePanelVisibilityImpl(img, panelId, visTitle, invisTitle, visible) {
    if (!visible) {
        img.src = "images/arrow_out.png";
        img.alt = invisTitle || mango.i18n["common.maximize"];
        img.title = invisTitle || mango.i18n["common.maximize"];
        dojo.html.hide(panelId);
        img.minimized = true;
    }
    else {
        img.src = "images/arrow_in.png";
        img.alt = visTitle || mango.i18n["common.minimize"];
        img.title = visTitle || mango.i18n["common.minimize"];
        dojo.html.show(panelId);
        img.minimized = false;
    }
}

function $get(comp) {
    return dwr.util.getValue(comp);
}

function $set(comp, value) {
    return dwr.util.setValue(comp, value);
}

function getSelectionRange(node) {
    node.focus();
    if (typeof node.selectionStart != "undefined")
        // FF
        return { start: node.selectionStart, end: node.selectionEnd };
    if (!document.selection)
        return { start: 0, end: 0 };
    
    // IE
    var range = document.selection.createRange();
    var rangeCopy = range.duplicate();
    rangeCopy.moveToElementText(tt);
    rangeCopy.setEndPoint('EndToEnd', range);
    var start = rangeCopy.text.length - range.text.length;
    return { start: start, end: start + range.text.length };
}

function setSelectionRange(node, start, end) {
    if (node.setSelectionRange) {
        node.setSelectionRange(start, end);
        node.focus();
    }
    else {
        var range = node.createTextRange();
        range.move('character', start);
        range.moveEnd('character', end - start);
        range.select();
    }
}

function insertIntoTextArea(node, text) {
    if (document.selection) {
        // IE
        node.focus();
        document.selection.createRange().text = text;
    }
    else {
        var oldScrollTop = node.scrollTop;
        var range = getSelectionRange(node);
        var value = node.value;
        value = value.substring(0, range.start) + text + value.substring(range.end);
        node.value = value;
        node.setSelectionRange(range.start + text.length, range.start + text.length);
        node.scrollTop = oldScrollTop;
    }
}

// Convenience method. Returns the first element in the given array that has an id property the same as the given id.
function getElement(arr, id, idName) {
    if (!idName)
        idName = "id";
    for (var i=0; i<arr.length; i++) {
        if (arr[i][idName] == id)
            return arr[i];
    }
    return null;
}

function getElements(arr, id, idName) {
	var elements = [];
	
	if (!idName)
        idName = "id";
    for (var i=0; i<arr.length; i++) {
        if (arr[i][idName].indexOf(id) != -1){
            elements.push(arr[i]);
        }
    }
    return elements;
}

function updateElement(arr, id, key, value, dobreak) {
    for (var i=0; i<arr.length; i++) {
        if (arr[i].id == id) {
            arr[i][key] = value;
            if (dobreak)
                return;
        }
    }
}

function removeElement(arr, id) {
    for (var i=arr.length-1; i>=0; i--) {
        if (arr[i].id == id)
            arr.splice(i, 1);
    }
}

function showMessage(node, msg) {
    node = getNodeIfString(node);
    if (msg) {
        show(node);
        node.innerHTML = msg;
    }
    else
        hide(node);
}
  
function getNodeIfString(node) {
    if (typeof(node) == "string")
        return $(node);
    return node;
}

function escapeQuotes(str) {
    if (!str)
        return "";
    return str.replace(/\'/g,"\\'");
}

function escapeDQuotes(str) {
    if (!str)
        return "";
    return str.replace(/\"/g,"\\\"");
}

function encodeQuotes(str) {
    if (!str)
        return "";
    return str.replace(/\'/g,"%27").replace(/\"/g,"%22");
}

function encodeHtml(str) {
    if (!str)
        return "";
    str = str.replace(/&/g,"&amp;");
    return str.replace(/</g,"&lt;");
}

function appendNewElement(/*string*/type, /*node*/parent) {
    var node = document.createElement(type);
    parent.appendChild(node);
    return node;
}

function writeImage(id, src, png, title, onclick) {
    var result = '<img class="ptr"';
    if (id)
        result += ' id="'+ id +'"';
    if (src)
        result += ' src="'+ src +'"';
    if (png && !src)
        result += ' src="images/'+ png +'.png"';
    if (title)
        result += ' alt="'+ title +'" title="'+ title +'"';
    result += ' onclick="'+ onclick +'"/>';
    return result;
}

function writeImageSQuote(id, src, png, title, onclick) {
    var result = "<img class='ptr'";
    if (id)
        result += " id='"+ id +"'";
    if (src)
        result += " src='"+ src +"'";
    if (png && !src)
        result += " src='images/"+ png +".png'";
    if (title)
        result += " alt='"+ title +"' title='"+ title +"'";
    result += " onclick='"+ onclick +"'/>";
    return result;
}

function hideContextualMessages(parent) {
    parent = getNodeIfString(parent);
    var nodes = dojo.html.getElementsByClass("ctxmsg", parent);
    for (var i=0; i<nodes.length; i++)
        dojo.html.hide(nodes[i]);
}

function hideGenericMessages(genericMessageNode) {
    dwr.util.removeAllRows(genericMessageNode);
}

function createContextualMessageNode(field, fieldId) {
    field = getNodeIfString(field);
    var node = document.createElement("div");
    node.id = fieldId +"Ctxmsg";
    node.className = "ctxmsg formError";
    dojo.html.hide(node);
    
    var next = field.nextSibling;
    if (next)
        next.parentNode.insertBefore(node, next);
    else
        field.parentNode.appendChild(node);
    return node;
}

function showDwrMessages(/*DwrResponseI18n.messages*/messages, /*tbody*/genericMessageNode) {
    var m, field, node, next;
    var genericMessages = new Array();
    for (var i=0; i<messages.length; i++) {
        m = messages[i];
        if (m.contextKey) {
            node = $(m.contextKey +"Ctxmsg");
            if (!node) {
                field = $(m.contextKey);
                if (field)
                    node = createContextualMessageNode(field, m.contextKey);
                else
                    alert("No contextual field found for key "+ m.contextKey);
            }
            
            if (node) {
                node.innerHTML = m.contextualMessage;
                dojo.html.show(node);
            }
        }
        else
            genericMessages[genericMessages.length] = m.genericMessage;
    }
    
    if (genericMessages.length > 0) {
        if (!genericMessageNode)
            alert("generic messages node not defined");
        genericMessageNode = getNodeIfString(genericMessageNode);
        dwr.util.removeAllRows(genericMessageNode);
        dwr.util.addRows(genericMessageNode, genericMessages, [ function(data) { return data; } ],
            {
                cellCreator:function(options) {
                    var td = document.createElement("td");
                    td.className = "formError";
                    return td;
                }
            });
        dojo.html.show(genericMessageNode);
    }
}

function setDateRange(data) {
    $set("fromYear", data.fromYear);
    $set("fromMonth", data.fromMonth);
    $set("fromDay", data.fromDay);
    $set("fromHour", data.fromHour);
    $set("fromMinute", data.fromMinute);
    $set("fromSecond", data.fromMinute);
    
    $set("toYear", data.toYear);
    $set("toMonth", data.toMonth);
    $set("toDay", data.toDay);
    $set("toHour", data.toHour);
    $set("toMinute", data.toMinute);
    $set("toSecond", data.toSecond);
    updateDateRange();
}

function updateDateRange() {
    var inception = $get("fromNone");
    setDisabled("fromYear", inception);
    setDisabled("fromMonth", inception);
    setDisabled("fromDay", inception);
    setDisabled("fromHour", inception);
    setDisabled("fromMinute", inception);
    setDisabled("fromSecond", inception);
    setDisabled("fromNone", false);
    
    var now = $get("toNone");
    setDisabled("toYear", now);
    setDisabled("toMonth", now);
    setDisabled("toDay", now);
    setDisabled("toHour", now);
    setDisabled("toMinute", now);
    setDisabled("toSecond", now);
    setDisabled("toNone", false);
}

function toggleSilence(eventId) {
    MiscDwr.toggleSilence(eventId, function(response) { setSilenced(response.data.eventId, response.data.silenced); });
}

function setSilenced(eventId, silenced) {
    var imgNode = $("silenceImg"+ eventId);
    if (silenced)
        updateImg(imgNode, "images/sound_mute.png", mango.i18n["events.unsilence"], true, "inline");
    else
        updateImg(imgNode, "images/sound_none.png", mango.i18n["events.silence"], true, "inline");
}

function setUserMuted(muted) {
    mango.soundPlayer.setMute(muted);
    var imgNode = $("userMutedImg");
    if (imgNode != null) {
        if (muted)
            updateImg(imgNode, "images/sound_mute.png", mango.i18n["header.unmute"], true, "inline");
        else
            updateImg(imgNode, "images/sound_none.png", mango.i18n["header.mute"], true, "inline");
    }
}

function ackEvent(eventId) {
    hide("silenceImg"+ eventId);
    var imgNode = $("ackImg"+ eventId);
    updateImg(imgNode, "images/tick_off.png", mango.i18n["events.acknowledged"], true, "inline");
    imgNode.onclick = function() {};
    dojo.html.removeClass(imgNode, "ptr");
    MiscDwr.acknowledgeEvent(eventId);
}

//
///
/// Sharing (views and watch lists)
///
//
mango.share = {};
mango.share.dwr = null;
mango.share.users = [];
mango.share.addUserToShared = function() {
    var userId = $get("allShareUsersList");
    if (userId)
        mango.share.dwr.addUpdateSharedUser(userId, 1/* ShareUser.ACCESS_READ */, mango.share.writeSharedUsers);
}

mango.share.updateUserAccess = function(sel, userId) {
    mango.share.dwr.addUpdateSharedUser(userId, $get(sel), mango.share.writeSharedUsers);
}

mango.share.removeFromSharedUsers = function(userId) {
    mango.share.dwr.removeSharedUser(userId, mango.share.writeSharedUsers);
}

mango.share.writeSharedUsers = function(sharedUsers) {
    dwr.util.removeAllRows("sharedUsersTable");
    if (sharedUsers.length == 0) {
        show("sharedUsersTableEmpty");
        hide("sharedUsersTableHeaders");
    }
    else {
        hide("sharedUsersTableEmpty");
        show("sharedUsersTableHeaders");
        dwr.util.addRows("sharedUsersTable", sharedUsers,
            [
                function(data) { return getElement(mango.share.users, data.userId).username; },
                function(data) {
                    var s = '<select onchange="mango.share.updateUserAccess(this, '+ data.userId +')">';
                    s += '<option value="1"'; // ShareUser.ACCESS_READ
                    if (data.accessType == 1) // ShareUser.ACCESS_READ
                        s += ' selected="selected"';
                    s += '>'+ mango.i18n["common.access.read"] +'</option>';
                    
                    s += '<option value="2"'; // ShareUser.ACCESS_SET
                    if (data.accessType == 2) // ShareUser.ACCESS_SET
                        s += ' selected="selected"';
                    s += '>'+ mango.i18n["common.access.set"] +'</option>';
                    
                    s += '</select>';
                    return s;
                },
                function(data) {
                    return "<img src='images/bullet_delete.png' class='ptr' "+
                            "onclick='mango.share.removeFromSharedUsers("+ data.userId +")'/>";
                }
            ],
            {
                rowCreator:function(options) {
                    var tr = document.createElement("tr");
                    tr.className = "smRow"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                    return tr;
                }
            }
        );
    }
    mango.share.updateUserList(sharedUsers);
}

mango.share.updateUserList = function(sharedUsers) {
    dwr.util.removeAllOptions("allShareUsersList");
    var availUsers = [];
    for (var i=0; i<mango.share.users.length; i++) {
        var found = false;
        for (var j=0; j<sharedUsers.length; j++) {
            if (sharedUsers[j].userId == mango.share.users[i].id) {
                found = true;
                break;
            }
        }
        
        if (!found)
            availUsers.push(mango.share.users[i]);
    }
    dwr.util.addOptions("allShareUsersList", availUsers, "id", "username");
}

function createValidationMessage(node, message) {
    return {contextKey:node, contextualMessage:message};
}



function updateChartComparatorComponent(idPrefix, width, height) {
	var fromDate = $get(idPrefix+"_fromDate1");
	var toDate = $get(idPrefix+"_toDate1");
	var fromDate2 = $get(idPrefix+"_fromDate2");
	var toDate2 = $get(idPrefix+"_toDate2");
	
	var fromDateMatch = fromDate.match(/^(\d\d)\/(\d\d)\/(\d{4}) (\d\d):(\d\d):(\d\d)$/);
	var toDateMatch = toDate.match(/^(\d\d)\/(\d\d)\/(\d{4}) (\d\d):(\d\d):(\d\d)$/);
	var fromDateMatch2 = fromDate2.match(/^(\d\d)\/(\d\d)\/(\d{4}) (\d\d):(\d\d):(\d\d)$/);
	var toDateMatch2 = toDate2.match(/^(\d\d)\/(\d\d)\/(\d{4}) (\d\d):(\d\d):(\d\d)$/);
	
	
	if(fromDateMatch == null || toDateMatch == null || fromDateMatch2 == null || toDateMatch2 == null) {
		alert("Data inválida! (dd/MM/yyyy hh:mm:ss)");
		return;
	}
	
	var dps = new Array();
	
	var dp1 = $get(idPrefix+"_dp1");
	var dp2 = $get(idPrefix+"_dp2");
	var dp3 = $get(idPrefix+"_dp3");
	var dp4 = $get(idPrefix+"_dp4");
	
	if(dp1 > 0)
		dps.push(dp1);
	if(dp2 > 0)
		dps.push(dp2);
	if(dp3 > 0)
		dps.push(dp3);
	if(dp4 > 0)
		dps.push(dp4);
	
	//alert(fromDate +" to " +toDate +" (" +dp1 +"," +dp2 +","+dp3+","+dp4+")");
	if(dps.length == 0) {
		alert('Selecione pelo menos um datapoint!');
		return;
	}
	
	ViewDwr.getChartData(dps,fromDate,toDate, fromDate2, toDate2,width,height,
			function(response) {	
//			alert(response[0]);
			$(idPrefix+"_chart1").src = response[0];
			$(idPrefix+"_chart2").src = response[1];
	});
	
	//alert(dps.length);
	
}


