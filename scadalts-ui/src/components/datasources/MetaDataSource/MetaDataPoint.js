export class MetaDataPoint {

    constructor(datasourceId) {
        this.id = -1;
        this.xid = "RANDOM";
        this.name = "";
        this.description = "";
        this.datasourceId = datasourceId;
        this.enabled = false;
        this.pointLocator = {
            dataTypeId: 3,
            changeTypeId: 6,
            settable: false,
            script: "",
            updateEvent: "",
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