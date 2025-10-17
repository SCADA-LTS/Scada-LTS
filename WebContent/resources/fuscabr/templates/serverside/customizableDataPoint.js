/* @license MIT  */

// Label describing data point
var point_label = "Data Point";

// Data point text format
var preffix = "";
var suffix = "";
var decimal_places = 0;
var font_size = 14;
var use_bold = false;

// Data point style
var border_color = "#888888";
var background_color = "#FFFFFF";
var label_color = "#000000";
var values_color = "#444444";

// Minimum size of data point
var min_width = 70;
var min_height = 50;

// Show label and value in a single line
var single_line_mode = false;




//
// DON'T CHANGE THE CODE BELOW
//




var display_value = preffix + value.toFixed(decimal_places) + suffix;
var flex_direction = single_line_mode ? "row" : "column";
var font_weight = use_bold ? "bold" : "normal";
var div_id = "data-point" + pointComponent.id;

// Return variable
var s = "";

// Create HTML
s += "<div id='" + div_id + "'>";
s += 	"<span style='color: " + label_color + "'>" + point_label + "</span>";
s += 	"<span style='color: " + values_color + "'>" + display_value + "</span>";
s += "</div>";

// Create CSS style
s += "<style>";
s += 	"#" + div_id + " {";
s += 		"min-height: " + min_height + "px;";
s +=	 	"min-width: " + min_width + "px;";
s += 		"background: " + background_color + ";";
s += 		"border: 1px solid;";
s += 		"border-color: " + border_color + ";";
s += 		"font-family: Arial, Helvetica, sans-serif;";
s += 		"font-weight: " + font_weight + ";";
s += 		"font-size: " + font_size + "px;";
s += 		"display: flex;";
s += 		"flex-direction: " +  flex_direction + ";";
s += 		"justify-content: center;";
s += 		"align-items: center;";
s += 	"}";
s += 	"#" + div_id + " span {";
s += 		"margin: 0px 2px;";
s += 	"}";
s += "</style>";

return s;
