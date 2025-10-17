import BaseViewModel from "../BaseViewModel";
export default class ViewComponentAlarmList extends BaseViewModel {
    
    constructor() {
        super('alarmlist');
        this.hideAckColumn = false;
        this.hideAlarmLevelColumn = false;
        this.hideIdColumn = false;
        this.hideInactivityColumn = false;
        this.hideTimestampColumn = false;
        this.maxListSize = 5;
        this.minAlarmLevel = 0;
        this.width = 400;
    }



}

