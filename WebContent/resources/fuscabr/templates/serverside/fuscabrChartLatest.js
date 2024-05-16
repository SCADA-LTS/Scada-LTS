/* @license MIT  */

// How many values will be included in the chart?
var reading_number = 10;

// Points to be included in chart (ID or XID)
var reading_points = [ point.id ];

// Color of fill in each series in the graph
// (the position of each color corresponds
// to a point in "reading_array" variable) 
var fill_colors = [ '#FF9421CC' ];

// Color of stroke in each series in the graph
// (the position of each color corresponds
// to a point in "reading_array" variable) 
var stroke_colors = [ '#FF9421CC' ];

// Description of each series in the graph
// (each description corresponds to a point
// in "reading_array" variable) 
var descriptions = [ 'Series 1' ];

// Customize chart size (in pixels)
var height = 300;
var width = 400;

// Configure chart title
var show_title = true;
var title = 'My FUScaBR Chart';

// Define chart type:
// 0 -> Line chart
// 1 -> Bar chart (vertical)
// 2 -> Bar chart (horizontal)
var graphic_type = 0;

// Start Y-axis always at 0
var begin_Y_at_zero = true;

// Enable chart animations
var enable_animations = true;




//
// DON'T CHANGE THE CODE BELOW
//




var invalid_message = "is not a numeric, multistate or binary datapoint";
var invalid_html = "";

// Get datapoint identifiers (ID/XID)
function getDataPointIds(identifier) {
    var dpDAO = new org.scada_lts.mango.service.DataPointService();
    var dp = dpDAO.getDataPoint(identifier);

    var point_id = dp.getId();
    var point_xid = String(dp.getXid());
    return { id: point_id, xid: point_xid };
}

// Get data point type
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

// Get data points' latest "n" values
function readPoints(id) {
	var val = new com.serotonin.mango.db.dao.PointValueDao();
	return val.getLatestPointValues(id, reading_number);
}

// Create an empty JSON array
function emptyJSONArray() {
	var empty_array = '[';
	
	for (var i = 0; i < reading_number; i++) {
		empty_array += '""';
		if (i != (reading_number - 1)) {
			empty_array += ',';
		}
	}
	
	empty_array += ']';
	return empty_array;
}

// Create a JSON array with a point value history
function createDataArray(obj, is_binary) {
	var foo = '[';
	var size = obj.size() - 1;

	for (var i = size; i >= 0; i--) {
		var value = obj.get(i).value;
		if (is_binary)
			value = String(value) == "true" ? 1 : 0;

		foo += '"' + value + '"';
		
		if (i != 0) {
			foo += ',';
		}
	}
	
	foo += ']';
	return foo;
}

// Create a JSON object compatible with Chart.js "datasets"
function createJSONDatasets() {
	var size = reading_points.length;
	var foo = '[';
	
	for (var i = 0; i < size; i++) {
		var is_binary = false;
		var dp_id = getDataPointIds(reading_points[i]).id;
		var dp_type = getDataPointType(dp_id);
		var point_values = readPoints(dp_id);
		
		// Don't include non numeric datapoints in array
		if (dp_type == "BINARY") {
			is_binary = true;
		} else if (dp_type != "NUMERIC" && dp_type != "MULTISTATE") {
			invalid_html += descriptions[i] + ": " + invalid_message + "<br>";
			continue;
		}
		
		if (foo != '[')
			foo += ',';

		var reading_array = createDataArray(point_values, is_binary);
		foo +=	'{';
		foo +=		'"label":"' + descriptions[i] + '",';
		foo +=		'"data":' + reading_array + ',';
		foo +=		'"backgroundColor":"' + fill_colors[i] + '",';
		foo +=		'"borderColor":"' + stroke_colors[i] + '"';
		foo +=	'}';
	}
	
	foo += ']';
	return foo;
}


// Create a JSON string that can be interpreted
// by FUScaBR library
function createFinalJSON() {
	var types = ['line', 'bar', 'horizontalBar'];
	var index = (graphic_type > 2) ? 0 : graphic_type;
	
	var foo = '';
	
	foo +=	'{';
	// Specific FUScaBR options
	foo +=		'"height":"' + height + '",';
	foo +=		'"width":"' + width + '",';
	foo +=		'"beginAtZero":' + begin_Y_at_zero + ',';
	foo +=		'"timeBased":false,';
	foo +=		'"animations":' + enable_animations + ',';
	foo +=		'"showTitle":' + show_title + ',';
	foo +=		'"title":"' + title + '",';
	// Chart.js options
	foo +=		'"type":"' + types[index] + '",';
	foo +=		'"data":{';
	foo +=			'"labels":' + emptyJSONArray() + ',';
	foo +=			'"datasets":' + createJSONDatasets();
	foo +=		'}';
	foo +=	'}';
	
	return foo;
}

var s = "<input class='fuscabr-chart-data' type='hidden' value='" + createFinalJSON() + "' >";
if (invalid_html.length)
	s += invalid_html;

/* Uncomment the line below to show the generated JSON code */
//s += "<div style='width: 500px;'>" + createFinalJSON() + "</div>";

return s;
