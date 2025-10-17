import BaseViewModel from "../BaseViewModel";
export default class ViewComponentLink extends BaseViewModel {
    
    constructor() {
        super('link');
        this.content = '[LINK Component]';
        this.link = '';
        this.text = 'Example Text';
    }

}

