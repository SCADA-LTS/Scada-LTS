import BaseImageSetModel from '../BaseImageSetModel';
export default class ViewComponentBinaryGraphic extends BaseImageSetModel {
    
    constructor() {
        super('binaryGraphic');
        this.oneImage = 0;
        this.zeroImage = 1;
    }

}

