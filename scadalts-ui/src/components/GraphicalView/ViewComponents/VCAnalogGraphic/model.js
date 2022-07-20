import BaseImageSetModel from '../BaseImageSetModel';
export default class ViewComponentAnalogGraphic extends BaseImageSetModel {
    
    constructor() {
        super('analogGraphic');
        this.min = 0;
        this.max = 100;        
    }

}

