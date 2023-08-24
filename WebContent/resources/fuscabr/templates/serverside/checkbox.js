/* @license MIT */

// Checkbox label appearance
var checkbox_label = "Click me!";
var font_size = 11;
var text_color = "#000000";

// This will invert the checkbox logic
// (checked when data point is false and
//  unchecked when it is true)
var inverted_logic = false;




//
// DON'T CHANGE THE CODE BELOW
//




// Check if data point type is compatible
if (getDataPointType(point.id) != "BINARY")
	return "The selected data point is not binary. Please select a binary data point."

var checkbox_id = "checkbox-script" + pointComponent.id;
var logic_value = value;

if (inverted_logic)
    logic_value = !value;

var command = "mango.view.setPoint(" + point.id + ", " + pointComponent.id + ", " + !value + ");";

// Return variable
var s = "";

// Generate HTML code
s += "<span onclick='" + command + "' >";
s +=    "<input class='ptr' type='checkbox' ";
s +=        (logic_value == true) ? "checked" : "";
s +=    ">";
s +=    "<label class='ptr' ";
s +=        "style='color:" + text_color + "; font-size:" + font_size + "px;'";
s +=    ">";
s +=         checkbox_label;
s +=    "</label>";
s += "</span>";

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
