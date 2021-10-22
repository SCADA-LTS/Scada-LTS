/**
 * Data Source object to be handled by UI
 * @typedef {Object} GenericDataSource
 * @property {Number} 	id
 * @property {String} 	xid
 * @property {Boolean} 	enabled
 * @property {String} 	name
 * @property {Number} 	type
 * @property {String} 	connection
 * @property {String} 	description
 * @property {Number} 	maxAlarmLevel
 * @property {Array} 	datasourceEvents
 * @property {Array}	datapoints
 * @property {Number} 	updatePeriod
 * @property {Number} 	updatePeriodType
 * @property {Boolean}	loading
 */
export class GenericDataSource {

    constructor(name, type = 1, xid = 'DS_12312', id = -1) {
        this.id = id;
        this.xid = xid;
        this.enabled = false;
        this.name = name;
        this.type = type;
        this.connection = '5 minutes';
        this.description = 'Example DS Description';
        this.maxAlarmLevel = 0;     //Max Level of active alarm
        this.datasourceEvents = []; // Array of DataSource Alarm Events
        this.datapoints = [];
        this.updatePeriod = 5;
        this.updatePeriodType = 1;
        this.loading = false;
        this.loadingDetails = false;
    }

    /**
     * Load DataSource data from a JSON object
     * received from the REST API server.
     * 
     * @param {Object} json - DataSource JSON object
     */
    loadDataFromJson(json) {
        this.id = json.id;
        this.xid = json.xid;
        this.enabled = json.enabled;
        this.name = json.name;
        this.type = json.type;
        this.connection = !!json.connection ? json.connection : '';
        this.description = !!json.description ? json.description : '';
        this.maxAlarmLevel = !!json.maxAlarmLevel ? json.maxAlarmLevel : 0;
        this.updatePeriod = !!json.updatePeriod ? json.updatePeriod : 5;
        this.updatePeriodType = !!json.updatePeriodType ? json.updatePeriodType : 1;
    }

}

export default GenericDataSource;