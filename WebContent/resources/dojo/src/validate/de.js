/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.validate.de");
dojo.require("dojo.validate.common");
dojo.validate.isGermanCurrency = function (value) {
	var flags = {symbol:"\u20ac", placement:"after", signPlacement:"begin", decimal:",", separator:"."};
	return dojo.validate.isCurrency(value, flags);
};

