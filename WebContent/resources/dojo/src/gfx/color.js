/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.gfx.color");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.array");
dojo.gfx.color.Color = function (r, g, b, a) {
	if (dojo.lang.isArray(r)) {
		this.r = r[0];
		this.g = r[1];
		this.b = r[2];
		this.a = r[3] || 1;
	} else {
		if (dojo.lang.isString(r)) {
			var rgb = dojo.gfx.color.extractRGB(r);
			this.r = rgb[0];
			this.g = rgb[1];
			this.b = rgb[2];
			this.a = g || 1;
		} else {
			if (r instanceof dojo.gfx.color.Color) {
				this.r = r.r;
				this.b = r.b;
				this.g = r.g;
				this.a = r.a;
			} else {
				this.r = r;
				this.g = g;
				this.b = b;
				this.a = a;
			}
		}
	}
};
dojo.gfx.color.Color.fromArray = function (arr) {
	return new dojo.gfx.color.Color(arr[0], arr[1], arr[2], arr[3]);
};
dojo.extend(dojo.gfx.color.Color, {toRgb:function (includeAlpha) {
	if (includeAlpha) {
		return this.toRgba();
	} else {
		return [this.r, this.g, this.b];
	}
}, toRgba:function () {
	return [this.r, this.g, this.b, this.a];
}, toHex:function () {
	return dojo.gfx.color.rgb2hex(this.toRgb());
}, toCss:function () {
	return "rgb(" + this.toRgb().join() + ")";
}, toString:function () {
	return this.toHex();
}, blend:function (color, weight) {
	var rgb = null;
	if (dojo.lang.isArray(color)) {
		rgb = color;
	} else {
		if (color instanceof dojo.gfx.color.Color) {
			rgb = color.toRgb();
		} else {
			rgb = new dojo.gfx.color.Color(color).toRgb();
		}
	}
	return dojo.gfx.color.blend(this.toRgb(), rgb, weight);
}});
dojo.gfx.color.named = {white:[255, 255, 255], black:[0, 0, 0], red:[255, 0, 0], green:[0, 255, 0], lime:[0, 255, 0], blue:[0, 0, 255], navy:[0, 0, 128], gray:[128, 128, 128], silver:[192, 192, 192]};
dojo.gfx.color.blend = function (a, b, weight) {
	if (typeof a == "string") {
		return dojo.gfx.color.blendHex(a, b, weight);
	}
	if (!weight) {
		weight = 0;
	}
	weight = Math.min(Math.max(-1, weight), 1);
	weight = ((weight + 1) / 2);
	var c = [];
	for (var x = 0; x < 3; x++) {
		c[x] = parseInt(b[x] + ((a[x] - b[x]) * weight));
	}
	return c;
};
dojo.gfx.color.blendHex = function (a, b, weight) {
	return dojo.gfx.color.rgb2hex(dojo.gfx.color.blend(dojo.gfx.color.hex2rgb(a), dojo.gfx.color.hex2rgb(b), weight));
};
dojo.gfx.color.extractRGB = function (color) {
	var hex = "0123456789abcdef";
	color = color.toLowerCase();
	if (color.indexOf("rgb") == 0) {
		var matches = color.match(/rgba*\((\d+), *(\d+), *(\d+)/i);
		var ret = matches.splice(1, 3);
		return ret;
	} else {
		var colors = dojo.gfx.color.hex2rgb(color);
		if (colors) {
			return colors;
		} else {
			return dojo.gfx.color.named[color] || [255, 255, 255];
		}
	}
};
dojo.gfx.color.hex2rgb = function (hex) {
	var hexNum = "0123456789ABCDEF";
	var rgb = new Array(3);
	if (hex.indexOf("#") == 0) {
		hex = hex.substring(1);
	}
	hex = hex.toUpperCase();
	if (hex.replace(new RegExp("[" + hexNum + "]", "g"), "") != "") {
		return null;
	}
	if (hex.length == 3) {
		rgb[0] = hex.charAt(0) + hex.charAt(0);
		rgb[1] = hex.charAt(1) + hex.charAt(1);
		rgb[2] = hex.charAt(2) + hex.charAt(2);
	} else {
		rgb[0] = hex.substring(0, 2);
		rgb[1] = hex.substring(2, 4);
		rgb[2] = hex.substring(4);
	}
	for (var i = 0; i < rgb.length; i++) {
		rgb[i] = hexNum.indexOf(rgb[i].charAt(0)) * 16 + hexNum.indexOf(rgb[i].charAt(1));
	}
	return rgb;
};
dojo.gfx.color.rgb2hex = function (r, g, b) {
	if (dojo.lang.isArray(r)) {
		g = r[1] || 0;
		b = r[2] || 0;
		r = r[0] || 0;
	}
	var ret = dojo.lang.map([r, g, b], function (x) {
		x = new Number(x);
		var s = x.toString(16);
		while (s.length < 2) {
			s = "0" + s;
		}
		return s;
	});
	ret.unshift("#");
	return ret.join("");
};

