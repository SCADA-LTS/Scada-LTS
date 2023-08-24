/* @license MIT */

// Slider width (in pixels)
var width = 150;

// Slider scale
var maximum = 10;
var minimum = 0;
var step = 1;

// Show a text with the data point value
var show_value = false;




//
// DON'T CHANGE THE CODE BELOW
//




// Check if data point type is compatible
if (getDataPointType(point.id) != "MULTISTATE" && getDataPointType(point.id) != "NUMERIC")
	return "Invalid data point type. Please select a numeric or multistate data point."

var command = "mango.view.setPoint(" + point.id + ", " + pointComponent.id + ", value);";

// Return variable
var s = "";

s += "<div style='position: relative;'>"

s += "<input type='range' ";
s +=    "style='width:" + width + "px;' ";
s +=    "max='" + maximum + "' ";
s +=    "min='" + minimum + "' ";
s +=    "step='" + step + "' ";
s +=    "onchange='" + command + "' ";
s +=    "value='" + value + "' ";
s += ">"

if (show_value) {
	s += "<span style='position: absolute; top: 50%; transform: translate(0, -50%);' ";
	s += "class='simpleRenderer'>" + value + "</span>";
}
s += "</div>";

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
