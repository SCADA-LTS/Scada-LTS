/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.math");
dojo.math.degToRad = function (x) {
	return (x * Math.PI) / 180;
};
dojo.math.radToDeg = function (x) {
	return (x * 180) / Math.PI;
};
dojo.math.factorial = function (n) {
	if (n < 1) {
		return 0;
	}
	var retVal = 1;
	for (var i = 1; i <= n; i++) {
		retVal *= i;
	}
	return retVal;
};
dojo.math.permutations = function (n, k) {
	if (n == 0 || k == 0) {
		return 1;
	}
	return (dojo.math.factorial(n) / dojo.math.factorial(n - k));
};
dojo.math.combinations = function (n, r) {
	if (n == 0 || r == 0) {
		return 1;
	}
	return (dojo.math.factorial(n) / (dojo.math.factorial(n - r) * dojo.math.factorial(r)));
};
dojo.math.bernstein = function (t, n, i) {
	return (dojo.math.combinations(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i));
};
dojo.math.gaussianRandom = function () {
	var k = 2;
	do {
		var i = 2 * Math.random() - 1;
		var j = 2 * Math.random() - 1;
		k = i * i + j * j;
	} while (k >= 1);
	k = Math.sqrt((-2 * Math.log(k)) / k);
	return i * k;
};
dojo.math.mean = function () {
	var array = dojo.lang.isArray(arguments[0]) ? arguments[0] : arguments;
	var mean = 0;
	for (var i = 0; i < array.length; i++) {
		mean += array[i];
	}
	return mean / array.length;
};
dojo.math.round = function (number, places) {
	if (!places) {
		var shift = 1;
	} else {
		var shift = Math.pow(10, places);
	}
	return Math.round(number * shift) / shift;
};
dojo.math.sd = dojo.math.standardDeviation = function () {
	var array = dojo.lang.isArray(arguments[0]) ? arguments[0] : arguments;
	return Math.sqrt(dojo.math.variance(array));
};
dojo.math.variance = function () {
	var array = dojo.lang.isArray(arguments[0]) ? arguments[0] : arguments;
	var mean = 0, squares = 0;
	for (var i = 0; i < array.length; i++) {
		mean += array[i];
		squares += Math.pow(array[i], 2);
	}
	return (squares / array.length) - Math.pow(mean / array.length, 2);
};
dojo.math.range = function (a, b, step) {
	if (arguments.length < 2) {
		b = a;
		a = 0;
	}
	if (arguments.length < 3) {
		step = 1;
	}
	var range = [];
	if (step > 0) {
		for (var i = a; i < b; i += step) {
			range.push(i);
		}
	} else {
		if (step < 0) {
			for (var i = a; i > b; i += step) {
				range.push(i);
			}
		} else {
			throw new Error("dojo.math.range: step must be non-zero");
		}
	}
	return range;
};

