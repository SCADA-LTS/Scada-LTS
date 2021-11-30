export default class BaseViewModel {
    
    constructor(defName) {
        this.index = -1;
        this.defName = defName;
        this.id = -1;
        this.idSuffix = null;
        this.compoundComponent = false;
        this.customComponent = false;
        this.valid = true;
        this.visible = true;
        this.style = null;
        this.x = 10;
        this.y = 10;
        this.z = 2;
    }

    setStyle() {
        this.style = `position:absolute;left:${this.x}px;top:${this.y}px;z-index:${this.z};`;
    }

}

