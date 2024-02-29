/* @license MIT */

// Configure your selector labels and values
var options = [
	{ label: "Zero", value: 0 },
	{ label: "One", value: 1 },
	{ label: "Two", value: 2 },
	{ label: "Three", value: 3 },
	{ label: "Four", value: 4 },
	{ label: "Five", value: 5 }
]




//
// DON'T CHANGE THE CODE BELOW
//




// Return variable
var s = "";

var command = "if (this.selectedIndex != 0) mango.view.setPoint(" + point.id + ", " + pointComponent.id + ", this.value)";

// Creating HTML drop-down selector
s += "<select onchange='" + command + "'>";

// Neutral option
s += "<option></option>";

// Creating options
for (var i in options) {
	var foo = options[i];
	s+= "<option value='" + foo.value + "' " ;
	if (value == foo.value) {
		s+= " selected ";
	}
	s+= ">" + foo.label + "</option>";
}

s+="</select>";

return s;
