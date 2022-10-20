import BasePointModel from "./BasePointModel";

export default class ImageSetViewModel extends BasePointModel {
    
    constructor(defName) {
        super(defName);
        this.imageSetId = "unavailable";
        this.displayText = false;
    }
}
