export default class ScadaReportForm {

    constructor(name, id = -1) {
        this.id = id;
        this.name = name;
        this.points = [];
		this.relativeRangePreviousQuantity = 0;
		this.dateRangeType = 1;
		this.includeUserComments = false;
		this.schedule = false;    	
		this.points = [];
		this.includeEvents = 1;
		this.relativeDateType = 1;
		this.previousPeriodCount = 1;
        this.previousPeriodType = 2;
		this.pastPeriodCount = 0;
		this.pastPeriodType = 0;
		this.fromNone = false;
		this.fromYear = 0;
        this.fromMonth = 0;
		this.fromDay = 0;
		this.fromHour = 0;
		this.fromMinute = 0;
		this.toNone = false;
		this.toYear = 0;
		this.toMonth = 0;
        this.toDay = 0;
		this.toHour = 0;
		this.toMinute = 0;
		this.schedule = false;
		this.schedulePeriod = 0;
		this.runDelayMinutes = 0;
        this.scheduleCron = '';
		this.email = false;
		this.includeData = false;
		this.zipData = false;
        this.recipients = [];//List<RecipientListEntryBean> 
    }
}