export default class ScadaReportInstance {

    constructor(id, name) {
        this.id = id;
        this.name = name;
        this.preventPurge = false;
        this.state;
        this.userId;
        this.fromInception;
        this.toNow;
        
        this.includeEvents;
        this.includeUserComments;
        
        
        this.prettyRecordCount;
        this.prettyReportEndTime;
        this.prettyReportStartTime;
        this.prettyRunDuration;
        this.prettyRunEndTime;
        this.prettyRunStartTime;
        
        this.recordCount;
        this.reportEndTime;
        this.reportStartTime;
        this.runEndTime = 0;
        this.runStartTime = 0;
        this.runDuration = this.runEndTime - this.runStartTime;
    
    }

    tooglePurge() {
        this.preventPurge = !this.preventPurge;
    }

    static fromAPI(api) {
        let rep = new ScadaReportInstance(api.id, api.name);
        rep.preventPurge = api.preventPurge;
        rep.state = api.state;
        rep.userId = api.userId;
        rep.fromInception = api.fromInception;
        rep.toNow = api.toNow;
        rep.includeEvents = api.includeEvents;
        rep.includeUserComments = api.includeUserComments;
        rep.prettyRecordCount = api.prettyRecordCount;
        rep.prettyReportEndTime = api.prettyReportEndTime;
        rep.prettyReportStartTime = api.prettyReportStartTime;
        rep.prettyRunDuration = api.prettyRunDuration;
        rep.prettyRunEndTime = api.prettyRunEndTime;
        rep.prettyRunStartTime = api.prettyRunStartTime;
        rep.recordCount = api.recordCount;
        rep.reportEndTime = api.reportEndTime;
        rep.reportStartTime = api.reportStartTime;
        rep.runEndTime = api.runEndTime;
        rep.runStartTime = api.runStartTime;
        rep.runDuration = rep.runEndTime - rep.runStartTime;
        return rep;
    }
}