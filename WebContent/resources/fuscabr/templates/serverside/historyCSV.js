/* @license MIT */

// Button label
var label = "Download point history";

// The name of CSV file to download
var filename = "Point History.csv";

// Data points included in history (ID or XID)
var points  = [ point.id ]; 

// History period (select time unit above)
var time_period = 2;

// History time unit:
// 0 -> minutes
// 1 -> hours
// 2 -> days
var time_unit = 1;

// Change SimpleDateFormat time pattern (advanced!)
var time_pattern = "yyyy-MM-dd KK:mm a";




//
// DON'T CHANGE THE CODE BELOW
//




// Convert time to milliseconds
time_unit = time_unit > 2 ? 2 : time_unit;
var units = [ 60000, 3600000, 43200000];
var startTime = new Date().getTime() - (time_period * units[time_unit]);

// Create onclick command
var command = "";
command += "var element = document.createElement(\"a\");";
command += "element.setAttribute(\"href\", \"data:text/plain;charset=utf-8,\" + \"" + createFinalCSV() + "\");";
command += "element.setAttribute(\"download\", \"" + filename + "\");";
command += "element.style.display = \"none\";";
command += "document.body.appendChild(element);";
command += "element.click();";
command += "document.body.removeChild(element);";

// Return variable
var s = "";

// Create HTML download button
s += "<input type='button' value='" + label + "' ";
s += "onclick='" + command + "'>";

return s;

//
////  Functions
//

// Create final CSV file content
function createFinalCSV() {
	var csv = encodeURIComponent("Data source,Data point,Value,Time") + "%0A";
    
    for (var i in points) {
	    var pointInfo = getDataPointInfo(points[i]);
	    csv += createCSVHistory(pointInfo, startTime);
    }
    
    return csv;
}

// Create CSV history for each data point specified
function createCSVHistory(point, startTime) {
	var pvDAO = new com.serotonin.mango.db.dao.PointValueDao();
	var history = pvDAO.getPointValues(point.id, startTime);
	var sdf = new java.text.SimpleDateFormat(time_pattern);

    var length = history.size() - 1;
	var csv = "";
	
	for (var i = length; i >= 0; i--) {
	    var data = point.source + "," + point.name + "," + history.get(i).value + "," + sdf.format(history.get(i).time);
	    csv += encodeURIComponent(data) + "%0A";
    }
    
    return csv;
}

// Get data point information (id, xid, point name and data source name)
function getDataPointInfo(identifier) {
    var dpDAO = new org.scada_lts.mango.service.DataPointService();
    var dp = dpDAO.getDataPoint(identifier);

    var pointId = dp.getId();
    var pointXid = String(dp.getXid());
    var pointName = String(dp.getName());
    var sourceName = String(dp.getDataSourceName());
    return { id: pointId, xid: pointXid, name: pointName, source: sourceName };
}