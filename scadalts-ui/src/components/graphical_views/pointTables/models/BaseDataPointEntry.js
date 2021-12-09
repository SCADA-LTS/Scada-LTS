export default class BaseDataPointEntry {

    constructor(pointId, datapointName, datapointType, websocket) {
        this.id = pointId;
        this.name = datapointName;
        this.type = datapointType;
        this.current = 0;
        this.lastTimestamp = null;
        this.wsValueConnection = websocket;
        if(!!this.wsValueConnection) {
            this.subscribe();
        }
    }

    subscribe() {
        this.wsValueConnection.subscribe(
            `/topic/datapoint/${this.id}/value`,
            (data) => {this.updateValue(JSON.parse(data.body))}
        );
    }

    unsubscribe() {
        this.wsValueConnection.unsubscribe()
    }

    updateValue(data) {
        throw new Error("updateValue method must be implemented in child class");        
    }
}