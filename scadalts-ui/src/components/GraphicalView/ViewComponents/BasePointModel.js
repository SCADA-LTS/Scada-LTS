import BaseViewModel from "./BaseViewModel";

export default class PointViewModel extends BaseViewModel {
    
    constructor(defName) {
        super(defName);
        this.dataPointId = -1;
        this.bkgdColorOverride = '';
        this.displayControls = false;
        this.displayPointName = false;
        this.nameOverride = '';
        this.settableOverride = false;
        this.styleAttribute = null;
    }
}

