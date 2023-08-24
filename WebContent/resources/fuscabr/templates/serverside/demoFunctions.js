/* @license MIT  */

/* 
 * This is a set of useful functions to
 * include in your own server-side scripts.
 */


return "You have selected a demo server-side script."

// This function gets the data type of a given
// data point, converting a number constant to
// a string with data type
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


// This function returns a simple string for the set point.
// This string needs to be included in your script's return
// variable, within an HTML event or within a <script> tag.
// This function is simplest and safer than backgroundSetPoint()
// To run it you must have write permission for the Graphical
// View.
function setPoint(pointIdentifier, value) {
    var dpDAO = new com.serotonin.mango.db.dao.DataPointDao();
    var pointId = dpDAO.getDataPoint(pointIdentifier).getId();
    return "mango.view.setPoint(" + pointId + "," + pointComponent.id + "," + value + ");";
}


// This function tries to set a data point in background
// You must have write permission for the data point to
// run this function.
// CAUTION! Incorrect uses can lead to infinite writing
// loops. Use this function only if you really need.
function backgroundSetPoint(pointId, newValue) {
    var pvDAO = new com.serotonin.mango.db.dao.PointValueDao();
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


// This function returns the current user name
function getUsername() {
    var user = new com.serotonin.mango.Common().getUser();
    return String(user.getUsername());
}

// This function returns an object with useful informations
// about current user (name, id, phone, e-mail)
function getUserInfo() {
    var user = new com.serotonin.mango.Common().getUser();
    var name = String(user.getUsername());
    var id = user.getId();
    var email = String(user.getEmail());
    var phone = String(user.getPhone());
    return { name: name, id: id, email: email, phone: phone };
}

// This function returns an object with useful data point
// informations (id, xid, point name and data source name)
function getDataPointInfo(identifier) {
    var dpDAO = new com.serotonin.mango.db.dao.DataPointDao();
    var dp = dpDAO.getDataPoint(identifier);

    var pointId = dp.getId();
    var pointXid = String(dp.getXid());
    var pointName = String(dp.getName());
    var sourceName = String(dp.getDataSourceName());
    return { id: pointId, xid: pointXid, name: pointName, source: sourceName };
}


// This function is a tiny variation of getDataPointInfo()
// that returns only the data point ID (useful to get an ID
// from a XID)
function getDataPointId(identifier) {
    var dpDAO = new com.serotonin.mango.db.dao.DataPointDao();
    var dp = dpDAO.getDataPoint(identifier);
    return dp.getId();
}