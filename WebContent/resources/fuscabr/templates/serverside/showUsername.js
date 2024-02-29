/* @license MIT */

// Appearance settings
var preffix = "Username:";
var suffix = "";
var font_size = 12;




//
// DON'T CHANGE THE CODE BELOW
//




// This function gets the current user's name
function getUsername() {
	var user = new com.serotonin.mango.Common().getUser();
	var username = String(user.getUsername());
	return username;
}

// Return variable
var s = "";

s += "<span style='font-size: " + font_size + "' >";
s += preffix + " " + getUsername() + " " + suffix;
s += "</span>";

return s;
