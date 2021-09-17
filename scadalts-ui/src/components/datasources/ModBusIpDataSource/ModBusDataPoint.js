export class ModBusDataPoint {

    constructor(datasourceId) {
        this.id = -1;
        this.xid = "RANDOM",
        this.name = "",
        this.description = "",
        this.datasourceId = datasourceId;
        this.enabled = false;
        this.pointLocator = {
            range: 1,
            modbusDataType: 1,
            slaveId: 1,
            slaveMonitor: false,
            socketMonitor: false,
            offset: 0,
            bit: 0,
            registerCount: 0,
            charset: "ASCII",
            settableOverride: true,
            multiplier: 1,
            additive: 0,
            dataTypeId: 3,
            settable: true,
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

export default ModBusDataPoint;