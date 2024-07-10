/* @license MIT  */

var true_format = {
	text: "Text at true state",
	color: "#15AA1A",
	font_size: "12px",
	bold: false
};

var false_format = {
	text: "Text at false state",
	color: "#FF0000",
	font_size: "12px",
	bold: false
};




//
// DON'T CHANGE THE CODE BELOW
//




// Check if data point type is compatible
if (getDataPointType(point.id) != "BINARY")
	return "The selected data point is not binary. Please select a binary data point."

// Return variable
var s = "";

s += "<span ";

// Explicit "value == true" for clarity reasons
if (value == true) {
	s += "style='color:" + true_format.color + ";";
	s += "font-size:" + true_format.font_size + ";";
	s += "font-weight:"
	s += (true_format.bold) ? "bold;" : "normal;"
	s += "' >" + true_format.text + "</span>";
} else {
	s += "style='color:" + false_format.color + ";";
	s += "font-size:" + false_format.font_size + ";";
	s += "font-weight:"
	s += (false_format.bold) ? "bold;" : "normal;"
	s += "' >" + false_format.text + "</span>";
}

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

	var dpDAO = new org.scada_lts.mango.service.DataPointService();
    var dp = dpDAO.getDataPoint(identifier);
	var locator = dp.getPointLocator();
	return types[locator.getDataTypeId()];
}