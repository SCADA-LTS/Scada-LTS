export class SnmpDataPoint {

    constructor(datasourceId) {
        this.id = -1;
        this.xid = "RANDOM SNMP";
        this.name = "";
        this.description = "";
        this.datasourceId = datasourceId;
        this.enabled = false;
        this.pointLocator = {
            oid: "",
            dataTypeId: 3,
            binary0Value: "0",
            setType: 0,
            trapOnly: false,
            settable: false,
            relinquishable: false,
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

export default SnmpDataPoint;