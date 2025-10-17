import BaseViewModel from "./BaseViewModel";

export default class PointViewModel extends BaseViewModel {
    
    constructor(defName) {
        super(defName);
        this.dataPointId = -1;
        this.dataPointXid = '';
        this.bkgdColorOverride = '';
        this.displayControls = false;
        this.displayPointName = false;
        this.nameOverride = '';
        this.name = '';
        this.settableOverride = false;
        this.styleAttribute = null;
    }
}

