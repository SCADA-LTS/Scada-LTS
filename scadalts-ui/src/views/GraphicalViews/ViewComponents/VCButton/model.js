export default class ViewComponentButton {
    
    constructor() {
        this.index = -1;
        this.type = 'BUTTON';
        this.dataPointXid = '';
        this.bkgdColorOverride = '';
        this.displayControls = false;
        this.nameOverride = '';
        this.settableOverride = false;
        this.whenOffLabel = "OFF";
        this.whenOnLabel = "ON";
        this.height = 50;
        this.width = 100;
        this.x = 10;
        this.y = 10;
        this.z = 2;
        this.script = "var s = '';if (value)  s += \"<input type='button' class='simpleRenderer' value='OFF' onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", false);return false;' style='background-color:;'\/>\"; else s += \"<input type='button' class='simpleRenderer' value='ON' onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", true);return true;' style='background-color:;'\/>\";  return s;";
    }



}

