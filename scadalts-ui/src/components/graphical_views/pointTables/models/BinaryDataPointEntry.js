import BaseDataPointEntry from "./BaseDataPointEntry";

export default class BinaryDataPointEntry extends BaseDataPointEntry {

    constructor(pointId, datapointName, pointValues, websocket) {
        super(pointId, datapointName, "binary", websocket);
        this.sum = pointValues.length;
        this.current = pointValues[pointValues.length - 1].value == "true" ? 1 : 0;
        this.lastTimestamp = pointValues[pointValues.length - 1].ts;
    }

    updateValue({value}) {
        this.current = value == "true" ? 1 : 0;
        this.lastTimestamp = new Date().getTime();
        this.sum = this.sum++;
    }
}