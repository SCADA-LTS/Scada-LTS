import ImageSetViewModel from "../BaseImageSetModel";

export default class ViewComponentMultistateGraphic extends ImageSetViewModel {
    
    constructor() {
        super('multistateGraphic');
        this.imageStateList = [];
        this.defaultImage = 0;
    }

}

