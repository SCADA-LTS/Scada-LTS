/* @license MIT */

// The button label
var label = "Pulse button";

// Appearance settings
var height = 40;
var width = 90;

// The pulse value on click (binary)
var value_on_click = true;

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
var pc_id = pointComponent.id;
var command = "";

// This command sets the point to the "value on click" and after re-sets
// to the "value on click" logically inverted
command += "var setPoint = ViewDwr.setViewPoint;"
command += "if (window.location.pathname.includes(&quot;views.shtm&quot;)) {";
command +=      "show(c" + pc_id + "Changing);";
command +=      "setPoint(" + pc_id + ", " + value_on_click + ", function() {";
command +=          "setPoint(" + pc_id + ", " + !value_on_click + ", function() {";
command +=              "hide(c" + pc_id + "Changing);";
command +=          "});";
command +=      "});";
command += "}";
command += "else if (window.location.pathname.includes(&quot;public_view.htm&quot;)) {";
command +=      "setPoint = ViewDwr.setViewPointAnon;";
command +=      "show(c" + pc_id + "Changing);";
command +=      "setPoint(mango.view.anon.viewId, " + pc_id + ", " + value_on_click + ", function() {";
command +=          "setPoint(mango.view.anon.viewId, " + pc_id + ", " + value_on_click + ", function() {";
command +=              "hide(c" + pc_id + "Changing);";
command +=          "});";
command +=      "});";
command += "}";


if (enable_confirm_prompt)
	command = confirm + "{" + command + "}";

// Return variable
var s = "";

s += "<input type='button' ";
s +=     "onclick='" + command + "' ";
s +=     "style='height:" + height + "px; width:"+ width + "px;' ";
s +=     "value='" + label + "' ";
s += ">";

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
