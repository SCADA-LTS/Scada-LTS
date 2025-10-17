/* @license MIT */

/*
 *  This experimental script generates a 
 *  simple form to change current logged
 *  user's password.
 * 
 *  To run it, you need create a exclusive
 *  alphanumeric data point (that can have
 *  data erased).
 *
 *  LIMITATIONS: This script will not work
 *  if the current user doesn't have write
 *  permissions for data point or graphical
 *  view. SENSITIVE DATA (PASSWORDS) MAY
 *  REMAIN STORED IN THE ALPHANUMERIC DATA
 *  POINT.
 */

// Form messages (translate to your language)
var current_password = "Current password:";
var new_password = "New password:";
var confirm_new_password = "Confirm new password:";
var change_button = "Change password";

// Success messages (translate to your language)
var change_success = "Success! Password changed.";

// Error messages (translate to your language)
var err_dont_match = "Error! New password and Confirm new password fields do not match.";
var err_wrong_psswd = "Error! Wrong password in field Current password.";
var err_blocked_psswd = "Error! You cannot use the password 1234!";
var err_empty_fields = "Error! All fields must be filled.";




//
// DON'T CHANGE THE CODE BELOW
//




// Check if data point type is compatible
if (getDataPointType(point.id) != "ALPHANUMERIC")
	return "Invalid data point type. Please select an alphanumeric data point."

// Return variable
var s = ""

// Get HTML code from createHtml() function
s += createHtml();

// Detect a password change request
if (value && value != "1234") {
    if (!changePassword()) {
        s += "<script> setTimeout(alert(\"" + err_wrong_psswd + "\"), 200); </script>"; 
    } else {
        s += "<script> setTimeout(alert(\"" + change_success + "\"), 200); </script>"; 
    }
}

return s;


// This function generates the main form HTML code
function createHtml() {
    var html = "";
    html += "<div id='change-password' class='borderDiv'>"
    html +=    "<label>" + current_password + "</label>";
    html +=    "<input id='current-password' type='password' >";
    html +=    "<label>" + new_password + "</label>";
    html +=    "<input id='new-password' type='password' maxlength='20' >";
    html +=    "<label>" + confirm_new_password + "</label>";
    html +=    "<input id='confirm-new-password' type='password' maxlength='20' >";
    html +=    "<input type='button' value='" + change_button + "' onclick='" + createCommandStr() + "' >";
    html +=    "<span id='password-msg' class='formError'></span>";
    html += "</div>"
    html += "<style>"
    html +=    "#change-password { padding: 3px; }"
    html +=    "#change-password * { display: block; margin: 2px; }"
    html +=    "#change-password input[type='button'] { margin: 2px auto; }"
    html +=    "#change-password input[type='password'] { width: 160px; }"
    html +=    "#change-password label { font-weight: bold; }"
    html +=    "#password-msg { max-width: 160px; }"
    html += "</style>"

    return html;
}

// This function creates Javascript code to validate form
// and set the "password changer" data point 
function createCommandStr() {
    var command = "";
    command += "var currPsswd = $get(\"current-password\");"
    command += "var newPsswd = $get(\"new-password\");"
    command += "var newPsswd2 = $get(\"confirm-new-password\");"
    command += "if (!currPsswd || !newPsswd || !newPsswd2) {";
    command +=      "setTimeout(alert(\"" + err_empty_fields + "\"), 200);";
    command += "} else if (newPsswd != newPsswd2) {";
    command +=      "setTimeout(alert(\"" + err_dont_match + "\"), 200);";
    command += "} else if (newPsswd == \"1234\") {";
    command +=      "setTimeout(alert(\"" + err_blocked_psswd + "\"), 200);";
    command += "} else {"
    command +=      "mango.view.setPoint(" + point.id + ", " + pointComponent.id + ", (currPsswd + \"&&&\" + newPsswd));";
    command += "}";

    return command;
}

// This function changes the user password
function changePassword() {
    var userDAO = new org.scada_lts.mango.service.UserService();
    var userId = new com.serotonin.mango.Common.getUser().getId();
    var user = userDAO.getUser(userId);
    
    var passwords = String(value).split("&&&");
    var currPasswd = new com.serotonin.mango.Common.encrypt(passwords[0]);
    var newPasswd = passwords[1];

    var pvDAO = new org.scada_lts.mango.service.PointValueService();
    try {
        backgroundSetPoint(point.id, "1234");
        // Try to clear point values history (for privacy)
        pvDAO.deletePointValues(point.id);
    } catch (e) {
        s += "<script> mango.view.setPoint(" + point.id + ", " + pointComponent.id + ", \"1234\"); </script>";
    };
    if (user.password.equals(currPasswd)) {
        user.setPassword(new com.serotonin.mango.Common.encrypt(newPasswd));
        userDAO.saveUser(user);
        return 1;
    }

    return 0;
}

// This function tries to set a data point in background.
// It can fail if you don't have necessary permissions.
function backgroundSetPoint(pointId, newValue) {
    var pvDAO = new org.scada_lts.mango.service.PointValueService();
    var lastValue = pvDAO.getLatestPointValue(pointId).value;

    if (typeof newValue == "string")
        lastValue = String(lastValue);
    else if (typeof newValue == "number")
        lastValue = Number(lastValue);

    // Only set point if values differ
    if (lastValue != newValue) {
        // The real component id does not matter in this context
        var componentId = 0;

        var viewDwr = com.serotonin.mango.web.dwr.ViewDwr();
        viewDwr.setPoint(pointId, componentId, newValue);

        // Return 1 if value was changed
        return 1;
    }

    // Return 0 if value was not changed
    return 0;
}

// This function returns the data type of a given data point
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
