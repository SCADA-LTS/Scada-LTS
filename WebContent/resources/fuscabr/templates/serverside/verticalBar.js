/* @license MIT */

// The maximum and minimum for data point scale
var scale_min = 0;
var scale_max = 100;

// Customize bar
var bar_height = 150;
var bar_width = 20;

var background_color = "#FFFFFF";
var foreground_color = "#12CE00";
var border_color = "#555555";




//
// DON'T CHANGE THE CODE BELOW
//




// Conversion of data point scale to percentage
var percentage = 0;
if (value > scale_max) {
	percentage = 100;
} else if (value < scale_min) {
	percentage = 0;
} else {
	percentage = ((value - scale_min) / (scale_max - scale_min)) * 100;
}

// Return variable
var s = "";

s += "<div style='";
s += 	"border: 1px solid " + border_color + "; ";
s += 	"position: relative; ";
s += 	"background-color: " + background_color + "; ";
s +=	"width: " + bar_width + "px; ";
s +=	"height: " + bar_height + "px; ";
s += "' >";
s += 	"<div style='";
s +=		"background-color: " + foreground_color + "; ";
s +=		"width: 100%; ";
s +=		"height: " + percentage + "%; ";
s +=		"position: absolute; bottom: 0px; left: 0px; ";
s +=	"' ></div>";
s += "</div>";

return s;
