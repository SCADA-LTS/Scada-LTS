export class WatchListPoint {

    constructor(id = -1, xid = "DP_123", name = "DataPoint", type=3, enabled = false, settable = false, description = "") {
        this.id = id;
        this.xid = xid;
        this.name = name;
        this.type = type;
        this.enabled = enabled;
        this.settable = settable;
        this.description = description;
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
    }

    /**
     * Construct WatchListPoint from multiple JSON objects
     * 
     * @param {Object} dataPoint 
     * @param {Object} pointValue 
     * @param {Object} pointEvents 
     * @returns 
     */
    createWatchListPoint(dataPoint, pointValue, pointEvents) {
        this.id = dataPoint.id;
        this.xid = dataPoint.xid;
        this.name = dataPoint.name;
        this.type = dataPoint.pointLocator.dataTypeId;
        this.enabled = dataPoint.enabled;
        this.settable = dataPoint.pointLocator.settable;
        this.description = dataPoint.description;
        this.value = pointValue.value;
        this.timestamp = new Date(pointValue.ts).toLocaleTimeString();
        this.events = pointEvents;
        this.textRenderer = {
            type: dataPoint.textRenderer.typeName,
            format: dataPoint.textRenderer.format,
            suffix: dataPoint.textRenderer.suffix,
            rangeValues: dataPoint.textRenderer.rangeValues,
            conversionExponent: dataPoint.textRenderer.conversionExponent,
        };
        return this;
    }

}

export default WatchListPoint;