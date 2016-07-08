/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.svg.HslColorPicker");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.HslColorPicker");
dojo.require("dojo.math");
dojo.require("dojo.svg");
dojo.require("dojo.gfx.color");
dojo.require("dojo.gfx.color.hsl");
dojo.require("dojo.experimental");
dojo.experimental("dojo.widget.svg.HslColorPicker");
dojo.widget.defineWidget("dojo.widget.svg.HslColorPicker", dojo.widget.HtmlWidget, function () {
	dojo.debug("warning: the HslColorPicker is not a finished widget, and is not yet ready for general use");
	this.filterObject = {};
}, {hue:"0", saturation:"0", light:"0", storedColor:"#0054aa", templateString:"<svg xmlns=\"http://www.w3.org/2000/svg\"\r\n\txmlns:xlink=\"http://www.w3.org/1999/xlink\"\r\n\tversion=\"1.1\" baseProfile=\"full\" width=\"170\" height=\"131\" xmlns:html=\"http://www.w3.org/1999/xhtml\">\r\n\t<defs>\r\n\t<linearGradient id=\"colorGradient\" dojoAttachPoint=\"colorGradientNode\" x1=\"0\" x2=\"131\" y1=\"0\" y2=\"0\" gradientUnits=\"userSpaceOnUse\">\r\n\t\t<stop id=\"leftGradientColor\" dojoAttachPoint=\"leftGradientColorNode\" offset=\"0%\" stop-color=\"#828282\"/>\r\n\t\t<stop id=\"rightGradientColor\" dojoAttachPoint=\"rightGradientColorNode\" offset=\"100%\" stop-color=\"#053fff\"/>\r\n\t</linearGradient>\r\n\t<linearGradient id=\"verticalGradient\" x1=\"0\" x2=\"0\" y1=\"0\" y2=\"131\" gradientUnits=\"userSpaceOnUse\">\r\n\t\t<stop offset=\"0%\" style=\"stop-color:#000000;\"/>\r\n\t\t<stop offset=\"50%\" style=\"stop-color:#000000;stop-opacity:0;\"/>\r\n\t\t<stop offset=\"50%\" style=\"stop-color:#ffffff;stop-opacity:0;\"/>\r\n\t\t<stop offset=\"100%\" style=\"stop-color:#ffffff;\"/>\r\n\t</linearGradient>\r\n\t<linearGradient id=\"sliderGradient\">\r\n\t\t<stop offset=\"0%\" style=\"stop-color:#000000;\"/>\r\n\t\t<stop offset=\"15%\" style=\"stop-color:#ffffff;\"/>\r\n\t\t<stop offset=\"30%\" style=\"stop-color:#000000;\"/>\r\n\t\t<stop offset=\"45%\" style=\"stop-color:#ffffff;\"/>\r\n\t\t<stop offset=\"60%\" style=\"stop-color:#000000;\"/>\r\n\t\t<stop offset=\"75%\" style=\"stop-color:#ffffff;\"/>\r\n\t\t<stop offset=\"90%\" style=\"stop-color:#000000;\"/>\r\n\t</linearGradient>\r\n</defs>\r\n\t<rect x=\"0\" y=\"0\" width=\"131px\" height=\"131px\" fill=\"url(#colorGradient)\"/>\r\n\t<rect x=\"0\" y=\"0\" width=\"131px\" height=\"131px\" style=\"fill:url(#verticalGradient);\"/>\r\n\t<rect id=\"saturationLightSlider\" dojoAttachPoint=\"saturationLightSliderNode\" x=\"100\" y=\"100\" width=\"5px\" height=\"5px\" style=\"stroke:url(#sliderGradient);stroke-width:1px;fill-opacity:0;\"/>\r\n\t<image xlink:href=\"images/hue.png\" dojoAttachPoint=\"hueNode\" x=\"140px\" y=\"0px\" width=\"21px\" height=\"131px\" dojoAttachEvent=\"onclick: onHueClick;\"/>\r\n\t<rect dojoAttachPoint=\"hueSliderNode\" x=\"139px\" y=\"40px\" width=\"24px\" height=\"4px\" style=\"stroke-opacity:1;fill-opacity:0;stroke:black;\"/>\r\n</svg>\r\n", fillInTemplate:function () {
	this.height = "131px";
	this.svgDoc = this.hueNode.ownerDocument;
	this.leftGradientColorNode = this.hueNode.ownerDocument.getElementById("leftGradientColor");
	this.rightGradientColorNode = this.hueNode.ownerDocument.getElementById("rightGradientColor");
	this.hueNode.setAttributeNS(dojo.dom.xmlns.xlink, "href", dojo.uri.moduleUri("dojo.widget", "templates/images/hue.png"));
	var hsl = dojo.gfx.color.hex2hsl(this.storedColor);
	this.hue = hsl[0];
	this.saturation = hsl[1];
	this.light = hsl[2];
	this.setSaturationStopColors();
}, setSaturationStopColors:function () {
	this.leftGradientStopColor = dojo.gfx.color.rgb2hex(this.hsl2rgb(this.hue, 0, 50));
	this.rightGradientStopColor = dojo.gfx.color.rgb2hex(this.hsl2rgb(this.hue, 100, 50));
	this.leftGradientColorNode.setAttributeNS(null, "stop-color", this.leftGradientStopColor);
	this.rightGradientColorNode.setAttributeNS(null, "stop-color", this.rightGradientStopColor);
}, setHue:function (hue) {
	this.hue = hue;
}, setHueSlider:function () {
	this.hueSliderNode.setAttribute("y", parseInt((this.hue / 360) * parseInt(this.height) - 2) + "px");
}, setSaturationLight:function (saturation, light) {
	this.saturation = saturation;
	this.light = light;
}, setSaturationLightSlider:function () {
}, onHueClick:function (evt) {
	var yPosition = parseInt(evt.clientY) - parseInt(evt.target.getAttribute("y"));
	this.setHue(360 - parseInt(yPosition * (360 / parseInt(this.height))));
	this.setSaturationStopColors();
	this.setStoredColor(dojo.gfx.color.hsl2hex(this.hue, this.saturation, this.light));
}, onHueDrag:function (evt) {
}, onSaturationLightClick:function (evt) {
	var xPosition = parseInt(evt.clientX) - parseInt(evt.target.getAttribute("y"));
	var yPosition = parseInt(evt.clientY) - parseInt(evt.target.getAttribute("y"));
	var saturation = parseInt(parseInt(xPosition) * (101 / 106));
	var light = parseInt(parseInt(yPosition) * (101 / 106));
	this.setSaturationLight(saturation, light);
	this.setStoredColor(dojo.gfx.color.hsl2hex(this.hue, this.saturation, this.light));
}, onSaturationLightDrag:function (evt) {
}, getStoredColor:function () {
	return this.storedColor;
}, setStoredColor:function (rgbHexColor) {
	this.storedColor = rgbHexColor;
	dojo.event.topic.publish("/" + this.widgetId + "/setStoredColor", this.filterObject);
}, hsl2rgb:function (hue, saturation, light) {
	function rgb(q1, q2, hue) {
		if (hue > 360) {
			hue = hue - 360;
		}
		if (hue < 0) {
			hue = hue + 360;
		}
		if (hue < 60) {
			return (q1 + (q2 - q1) * hue / 60);
		} else {
			if (hue < 180) {
				return (q2);
			} else {
				if (hue < 240) {
					return (q1 + (q2 - q1) * (240 - hue) / 60);
				} else {
					return (q1);
				}
			}
		}
	}
	this.rgb = rgb;
	if (saturation == 0) {
		return [Math.round(light * 255 / 100), Math.round(light * 255 / 100), Math.round(light * 255 / 100)];
	} else {
		light = light / 100;
		saturation = saturation / 100;
		if ((light) < 0.5) {
			var temp2 = (light) * (1 + saturation);
		} else {
			var temp2 = (light + saturation - (light * saturation));
		}
		var temp1 = 2 * light - temp2;
		var rgbcolor = [];
		rgbcolor[0] = Math.round(rgb(temp1, temp2, parseInt(hue) + 120) * 255);
		rgbcolor[1] = Math.round(rgb(temp1, temp2, hue) * 255);
		rgbcolor[2] = Math.round(rgb(temp1, temp2, parseInt(hue) - 120) * 255);
		return rgbcolor;
	}
}});

