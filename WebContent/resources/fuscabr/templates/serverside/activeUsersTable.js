/* @license MIT  */

/* Important: note that this script generates an
 * table with all active ScadaBR users. However,
 * "active users" does not mean "online users".
 */

// Appearance settings
var header_color = "#009933";
var header_text_color = "white";
var font_size = 12;
var table_width = 600;

// Enable/disable columns displayed in table
var show_id = false;
var show_email = true;
var show_telephone = true;
var show_admin = true;
var show_last_login = true;

// Table texts (translate to your language)
var id_header = "ID";
var email_header = "E-mail";
var telephone_header = "Telephone";
var admin_header = "Is admin?"
var admin_yes = "Yes";
var admin_no = "No";
var last_login_header = "Last login";
var last_login_never = "Never";
var user_header = "User";

// You can enable this feature to use apply an external
// CSS to table. THIS WILL DISABLE BUILT-IN CSS!
var enable_external_css = false;
var css_class = "myTable";


// Change SimpleDateFormat date pattern (advanced!)
var date_pattern = "KK:mm a yyyy-MM-dd";




//
// DON'T CHANGE THE CODE BELOW
//




// Return variable
var s = "";

// Use built-in css if enabled
if (!enable_external_css) {
	css_class = "active-users-table" + pointComponent.id;
	s += createCSS();
}

// Generate users table
s += createTable();


return s;




// This function creates an HTML table with active users
function createTable() {
	var users = new com.serotonin.mango.db.dao.UserDao().getActiveUsers();
	var time = new java.text.SimpleDateFormat(date_pattern);
	var body = "";
	var header = "";
	var size = users.size();

	for (var i = 0; i < size; i++) {
		var thisUser = users.get(i);
				
		header = "<tr>";
		body += "<tr>";
		
		if (show_id) {
			header += "<th>" + id_header + "</th>";
			body += "<td>" + thisUser.id + "</td>";
		}
		
		header += "<th>" + user_header + "</th>";
		body += "<td>" + thisUser.username + "</td>";
		
		if (show_email) {
			header += "<th>" + email_header + "</th>";
			body += "<td>" + thisUser.email + "</td>";
		}
		if (show_telephone) {
			header += "<th>" + telephone_header + "</th>";
			body += "<td>" + thisUser.phone + "</td>";
		}
		if (show_admin) {
			header += "<th>" + admin_header + "</th>";
			if (thisUser.admin) {
				body += "<td>" + admin_yes + "</td>";
			} else {
				body += "<td>" + admin_no + "</td>";
			}
		}
		if (show_last_login) {
			header += "<th>" + last_login_header + "</th>";
			if (!thisUser.isFirstLogin()) {
				body += "<td>" + time.format(thisUser.lastLogin) + "</td>";
			} else {
				body += "<td>" + last_login_never + "</td>";
			}
		}
		header += "</tr>";
		body += "</tr>";
	}

	return "<table class='" + css_class + "'>" + header + body + "</table>";
}

// This function generates an built-in basic CSS style to tables
function createCSS() {
	var css = "";
	
	css += "<style>";
	
	css += "." + css_class + "{";
	css += 		"font-family: Arial, Helvetica, sans-serif;";
	css += 		"font-size: " + font_size + "px;";
	css += 		"border-collapse: collapse;";
	css += 		"width: " + table_width + "px;"
	css += "}";
	
	css += "." + css_class + " td, ." + css_class + " tr {";
	css += 		"border: 1px solid #CACACA;";
	css += 		"padding: 6px;";
	css += "}";
	
	css += "." + css_class + " tr:nth-child(odd) { background-color: #F0F0F0; }";
	css += "." + css_class + " tr:nth-child(even) { background-color: #FFFFFF; }";
	css += "." + css_class + " tr:hover { background-color: #D3D3D3; }";
	
	css += "." + css_class + " th {";
	css += 		"padding: 6px;";
	css += 		"text-align: left;";
	css += 		"background-color: " + header_color + ";";
	css += 		"border: 1px solid " + header_color + ";";
	css += 		"color: " + header_text_color + ";";
	css += "}";
	
	css += "</style>";
	
	return css;
}
