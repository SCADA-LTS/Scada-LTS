export class WatchListPoint {

    constructor(id = -1, xid = "DP_123", name = "DataPoint", type=3, enabled = false, settable = false, description = "") {
        this.id = id;
        this.xid = xid;
        this.name = name;
        this.type = type;
        this.enabled = enabled;
        this.settable = settable;
        this.description = description;
        this.dataSourceName = "";
        this.dataSourceEnabled = true;
        this.order = -1;
        this.value = 0;
        this.timestamp = 0;
        this.events = [];
        this.textRenderer = {
            type: '',
            format: '',
            suffix: '',
            rangeValues: [],
            conversionExponent: 0,
        };
        this.wsEnable = null;
        this.wsValue = null;
    }

    /**
     * Construct WatchListPoint from multiple JSON objects
     * 
     * @param {Object} dataPoint 
     * @param {Object} pointValue 
     * @param {Object} pointEvents 
     * @returns 
     */
    createWatchListPoint(dataPoint, pointValue, pointEvents, dataSourceStatus, order = -1, permission = 1) {
        this.id = dataPoint.id;
        this.xid = dataPoint.xid;
        this.name = dataPoint.name;
        this.type = dataPoint.pointLocator.dataTypeId;
        this.enabled = dataPoint.enabled;
        this.settable = dataPoint.pointLocator.settable;
        if(this.settable) { 
            this.settable = permission === 2 || permission === 3; 
        }
        this.description = dataPoint.description;
        this.dataSourceName = dataPoint.dataSourceName;
        this.dataSourceEnabled = dataSourceStatus.enabled;
        this.order = order;
        this.value = pointValue.value;
        this.timestamp = new Date(pointValue.ts).toLocaleTimeString();
        this.events = pointEvents;
        this.textRenderer = {
            type: dataPoint.textRenderer.typeName,
            format: dataPoint.textRenderer.format,
            suffix: dataPoint.textRenderer.suffix,
            rangeValues: dataPoint.textRenderer.rangeValues,
            conversionExponent: dataPoint.textRenderer.conversionExponent,
            oneColour: dataPoint.textRenderer.oneColour,
            oneLabel: dataPoint.textRenderer.oneLabel,
            zeroColour: dataPoint.textRenderer.zeroColour,
            zeroLabel: dataPoint.textRenderer.zeroLabel,
            multistateValues: dataPoint.textRenderer.multistateValues,
        };
        return this;
    }    

}

export default WatchListPoint;