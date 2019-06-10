/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.require("dojo.crypto");
dojo.provide("dojo.crypto.SHA1");
dojo.require("dojo.experimental");
dojo.experimental("dojo.crypto.SHA1");
dojo.crypto.SHA1 = new function () {
	var chrsz = 8;
	var mask = (1 << chrsz) - 1;
	function toWord(s) {
		var wa = [];
		for (var i = 0; i < s.length * chrsz; i += chrsz) {
			wa[i >> 5] |= (s.charCodeAt(i / chrsz) & mask) << (i % 32);
		}
		return wa;
	}
	function toString(wa) {
		var s = [];
		for (var i = 0; i < wa.length * 32; i += chrsz) {
			s.push(String.fromCharCode((wa[i >> 5] >>> (i % 32)) & mask));
		}
		return s.join("");
	}
	function toHex(wa) {
		var h = "0123456789abcdef";
		var s = [];
		for (var i = 0; i < wa.length * 4; i++) {
			s.push(h.charAt((wa[i >> 2] >> ((i % 4) * 8 + 4)) & 15) + h.charAt((wa[i >> 2] >> ((i % 4) * 8)) & 15));
		}
		return s.join("");
	}
	function toBase64(wa) {
		var p = "=";
		var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		var s = [];
		for (var i = 0; i < wa.length * 4; i += 3) {
			var t = (((wa[i >> 2] >> 8 * (i % 4)) & 255) << 16) | (((wa[i + 1 >> 2] >> 8 * ((i + 1) % 4)) & 255) << 8) | ((wa[i + 2 >> 2] >> 8 * ((i + 2) % 4)) & 255);
			for (var j = 0; j < 4; j++) {
				if (i * 8 + j * 6 > wa.length * 32) {
					s.push(p);
				} else {
					s.push(tab.charAt((t >> 6 * (3 - j)) & 63));
				}
			}
		}
		return s.join("");
	}
	function add(x, y) {
		var l = (x & 65535) + (y & 65535);
		var m = (x >> 16) + (y >> 16) + (l >> 16);
		return (m << 16) | (l & 65535);
	}
	function r(x, n) {
		return (x << n) | (x >>> (32 - n));
	}
	function f(u, v, w) {
		return ((u & v) | (~u & w));
	}
	function g(u, v, w) {
		return ((u & v) | (u & w) | (v & w));
	}
	function h(u, v, w) {
		return (u ^ v ^ w);
	}
	function fn(i, u, v, w) {
		if (i < 20) {
			return f(u, v, w);
		}
		if (i < 40) {
			return h(u, v, w);
		}
		if (i < 60) {
			return g(u, v, w);
		}
		return h(u, v, w);
	}
	function cnst(i) {
		if (i < 20) {
			return 1518500249;
		}
		if (i < 40) {
			return 1859775393;
		}
		if (i < 60) {
			return -1894007588;
		}
		return -899497514;
	}
	function core(x, len) {
		x[len >> 5] |= 128 << (24 - len % 32);
		x[((len + 64 >> 9) << 4) + 15] = len;
		var w = [];
		var a = 1732584193;
		var b = -271733879;
		var c = -1732584194;
		var d = 271733878;
		var e = -1009589776;
		for (var i = 0; i < x.length; i += 16) {
			var olda = a;
			var oldb = b;
			var oldc = c;
			var oldd = d;
			var olde = e;
			for (var j = 0; j < 80; j++) {
				if (j < 16) {
					w[j] = x[i + j];
				} else {
					w[j] = r(w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16], 1);
				}
				var t = add(add(r(a, 5), fn(j, b, c, d)), add(add(e, w[j]), cnst(j)));
				e = d;
				d = c;
				c = r(b, 30);
				b = a;
				a = t;
			}
			a = add(a, olda);
			b = add(b, oldb);
			c = add(c, oldc);
			d = add(d, oldd);
			e = add(e, olde);
		}
		return [a, b, c, d, e];
	}
	function hmac(data, key) {
		var wa = toWord(key);
		if (wa.length > 16) {
			wa = core(wa, key.length * chrsz);
		}
		var l = [], r = [];
		for (var i = 0; i < 16; i++) {
			l[i] = wa[i] ^ 909522486;
			r[i] = wa[i] ^ 1549556828;
		}
		var h = core(l.concat(toWord(data)), 512 + data.length * chrsz);
		return core(r.concat(h), 640);
	}
	this.compute = function (data, outputType) {
		var out = outputType || dojo.crypto.outputTypes.Base64;
		switch (out) {
		  case dojo.crypto.outputTypes.Hex:
			return toHex(core(toWord(data), data.length * chrsz));
		  case dojo.crypto.outputTypes.String:
			return toString(core(toWord(data), data.length * chrsz));
		  default:
			return toBase64(core(toWord(data), data.length * chrsz));
		}
	};
	this.getHMAC = function (data, key, outputType) {
		var out = outputType || dojo.crypto.outputTypes.Base64;
		switch (out) {
		  case dojo.crypto.outputTypes.Hex:
			return toHex(hmac(data, key));
		  case dojo.crypto.outputTypes.String:
			return toString(hmac(data, key));
		  default:
			return toBase64(hmac(data, key));
		}
	};
}();

