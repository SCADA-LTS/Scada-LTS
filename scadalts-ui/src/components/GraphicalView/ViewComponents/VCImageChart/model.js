import BaseViewModel from "../BaseViewModel";
export default class ViewComponentImageChart extends BaseViewModel {

    constructor() {
        super('imageChart');
        this.children = {
            point1: "DP_741583",
            point2: null,
            point3: null,
            point4: null,
            point5: null,
            point6: null,
            point7: null,
            point8: null,
            point9: null,
            point10: null,
        };
        this.durationType = 4;
        this.durationPeriods = 1;
        this.height = 300;
        this.name = "pol";
        this.width = 500;
        
    }



}