import BasePointModel from '../BasePointModel'
export default class ViewComponentButton extends BasePointModel {
    
    constructor() {
        super('button');
        
        this.whenOffLabel = "OFF";
        this.whenOnLabel = "ON";
        this.height = 50;
        this.width = 100;
        this.script = "var s = '';if (value)  s += \"<input type='button' class='simpleRenderer' value='OFF' onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", false);return false;' style='background-color:;'\/>\"; else s += \"<input type='button' class='simpleRenderer' value='ON' onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", true);return true;' style='background-color:;'\/>\";  return s;";
    }



}

