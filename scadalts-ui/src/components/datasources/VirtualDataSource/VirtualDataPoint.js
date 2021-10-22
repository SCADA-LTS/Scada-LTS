export class ScadaVirtualDataPoint {

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
            alternateBooleanChange: {
                startValue: true,
            },
            brownianChange: {
                startValue: "",
                min: 0.0,
                max: 0.0,
                maxChange: 0.0
            },
            incrementAnalogChange: {
                startValue: "",
                min: 0.0,
                max: 0.0,
                change: 0.0,
                roll: false
            },
            incrementMultistateChange: {
                startValue: "",
                values: [],
                roll: false
            },
            noChange: {
                startValue: ""
            },
            randomAnalogChange: {
                startValue: 12,
                min: 10.0,
                max: 100.0
            },
            randomBooleanChange: {
                startValue: true
            },
            randomMultistateChange: {
                startValue: "",
                values: []
            },
            analogAttractorChange: {
                startValue: "",
                maxChange: 0.0,
                volatility: 0.0,
                attractionPointId: 0
            }
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

export default ScadaVirtualDataPoint;