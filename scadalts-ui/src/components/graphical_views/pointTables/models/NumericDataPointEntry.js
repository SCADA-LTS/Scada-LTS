import BaseDataPointEntry from "./BaseDataPointEntry";

export default class NumericDataPointEntry extends BaseDataPointEntry {

    constructor(pointId, datapointName, pointValues, websocket, type) {
        super(pointId, datapointName, type.toLowerCase(), websocket);
        this.max = 0;
        this.min = Infinity;
        this.avg = 0;
        this.sum = 0;
        this.count = 0;
        if(pointValues.length > 0) {
            pointValues.forEach(value => {
                this.addValue(Number(value.value))
            });
        }
        this.current = Number(pointValues[pointValues.length - 1].value);
        this.lastTimestamp = pointValues[pointValues.length - 1].ts;
    }

    addValue(value) {
        this.sum += value;
        this.count++;
        this.min = value < this.min ? value : this.min;
        this.max = value > this.max ? value : this.max;
        this.avg = this.sum / this.count;
    }

    updateValue(data) {
        const value = Number(data.value);
        this.current = value;
        this.lastTimestamp = new Date().getTime();
        this.sum += value;
        this.count++;
        this.min = value < this.min ? value : this.min;
        this.max = value > this.max ? value : this.max;
        this.avg = this.sum / this.count;
    }
}