/* @license MIT  */

// Gauge description
var description = "Gauge template";

// Scale settings
var maximum = 100;
var minimum = 0;

// Gauge's width (height is set automatically)
var width = 250;

// Display value format
var preffix = "";
var suffix = "%";
var decimal_places = 0;

// Color settings (background is transparent by default)
var background_color = "";
var text_color = "#000000";
var bar_color = "#12CE00";

// Enable extra bar colors
var enable_extra_colors = false;

// Extra bar colors settings
var extra_colors = [
	{ color: "#FFC200", value: 70 },
	{ color: "#FF6800", value: 80 },
	{ color: "#FF0000", value: 90 }
];




//
// DON'T CHANGE THE CODE BELOW
//




// Check if data point type is compatible
if (getDataPointType(point.id) != "MULTISTATE" && getDataPointType(point.id) != "NUMERIC")
	return "Invalid data point type. Please select a numeric or multistate data point."
	
// Convert angle to radians
var arc_angle = undefined;

if (value > maximum)
	arc_angle = 2 * Math.PI;
else if (value < minimum)
	arc_angle = Math.PI;
else
	arc_angle = Math.PI + ((value - minimum) / (maximum - minimum)) * Math.PI


// Define the bar color
var color = bar_color;
if (enable_extra_colors) {
	extra_colors.sort(function(a, b) {
		return a.value - b.value;
	});

	for (var i in extra_colors) {		
		if ((value.toFixed(decimal_places) >= extra_colors[i].value)) {
			color = extra_colors[i].color;
		}
	}
}

// If not given background color, set it to transparent
if (typeof background_color != "string" || background_color.length == 0) {
	background_color = "rgb(255, 255, 255, 0)";
}

// Auxiliary variables
var height = 0.75 * width;
var centerX = 0.5 * width;
var centerY = 0.625 * width;
var display_value = preffix + value.toFixed(decimal_places) + suffix;
var radius = 0.40 * width;
var descY = centerY - (radius * 1.25);
var descFont = (0.12 * width) + "px sans-serif";
var valueFont = (0.16 * width) + "px sans-serif";
var bar_background_color = "#CCCCCC";
var bar_width = (0.10 * width);

// Return variable
var s = "";

// Create HTML elements
s += "<div>";
s += 	"<canvas id='medidor" + pointComponent.id + "' height='" + height + "' width='" + width + "'>";
s += 		"<center>";
s += 			"<strong>This browser does not support this element.</strong>";
s += 			"<p>" + description + ":" + display_value + "</p>";
s += 		"</center>";
s += 	"</canvas>";
s += "</div>";
	
// Create JavaScript code to draw the gauge
s += "<script>";

// Get the <canvas> element
s += "var canvas = document.getElementById('medidor" + pointComponent.id + "');";
s += "var ctx = canvas.getContext('2d');";
s += "ctx.lineWidth = " + bar_width + ";";

// Draw gauge's background
s += "ctx.fillStyle = '" + background_color + "';";
s += "ctx.fillRect(0, 0, " + width + ", " +  height + ");";

// Draw bar background
s += "ctx.beginPath();";
s += "ctx.strokeStyle = '" + bar_background_color + "';";
s += "ctx.arc(" + centerX + ", " + centerY + ", " + radius + ", Math.PI, 0);";
s += "ctx.stroke();";
s += "ctx.closePath();";

// Draw bar foreground
s += "ctx.beginPath();";
s += "ctx.strokeStyle = '" + color +"';";
s += "ctx.arc(" + centerX + ", " + centerY + ", " + radius + ", Math.PI, " + arc_angle +");";
s += "ctx.stroke();";
s += "ctx.closePath();";

// Draw the texts
s += "ctx.lineWidth = 1;";
s += "ctx.beginPath();";
s += "ctx.fillStyle = '" + text_color + "';";
s += "ctx.textAlign = 'center';";
s += "ctx.font = '" + descFont + "';";
s += "ctx.fillText('" + description + "', " + centerX + ", " + descY + ");";
s += "ctx.font = '" + valueFont + "';";
s += "ctx.fillText('" + display_value + "', " + centerX + ", " + centerY + ");";

s += "</script>";

return s;

function getDataPointType(identifier) {
	var types = {
		0: "UNKNOWN",
		1: "BINARY",
		2: "MULTISTATE",
		3: "NUMERIC",
		4: "ALPHANUMERIC",
		5: "IMAGE"
	}

	var dpDAO = new com.serotonin.mango.db.dao.DataPointDao();
    var dp = dpDAO.getDataPoint(identifier);
	var locator = dp.getPointLocator();
	return types[locator.getDataTypeId()];
}