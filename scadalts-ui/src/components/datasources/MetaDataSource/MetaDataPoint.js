export class MetaDataPoint {

    constructor(datasourceId) {
        this.id = -1;
        this.xid = "RANDOM";
        this.name = "";
        this.description = "";
        this.dataSourceId = datasourceId;
        this.enabled = false;
        this.pointLocator = {
            dataSourceTypeId: 9,
            dataTypeId: 1,
            settable: false,
            context: [],
            script: "",
            updateEvent: "START_OF_WEEK",
            updateCronPattern: "",
            executionDelaySeconds: 10,
            executionDelayPeriodType: "SECONDS"
        }
    }

    initialSetup(id, xid, name, enabled = false, description = '') {
        this.id = id;
        this.xid = xid;
        this.name = name;
        this.enabled = enabled;
        this.description = description;
    }



}

export default MetaDataPoint;