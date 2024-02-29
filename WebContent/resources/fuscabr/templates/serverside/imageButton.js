/* @license MIT  */

// Button image when value is true
var image_true = "graphics/Botao-3D/botao10.png";

// Button image when value is false
var image_false = "graphics/Botao-3D/botao20.png";

// Display a confirmation prompt before change value
var enable_confirm_prompt = false;
var confirm_message = "Change value?";




//
// DON'T CHANGE THE CODE BELOW
//




// Check if data point type is compatible
if (getDataPointType(point.id) != "BINARY")
	return "The selected data point is not binary. Please select a binary data point."

var confirm = "if (window.confirm(&quot;" + confirm_message + "&quot;)) ";
var command = "mango.view.setPoint(" + point.id + ", " + pointComponent.id + ", " + !value + ")";
if (enable_confirm_prompt)
	command = confirm + command;

// Return variable
var s = "";

if (value)
	s += "<img class='ptr' src='" + image_true + "' onclick='" + command + "' >";
else
	s += "<img class='ptr' src='" + image_false + "' onclick='" + command + "' >";

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
