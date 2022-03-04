import WatchList from "./WatchListEntry";

export default class WatchListJson {
    constructor() {
        this.id = -1;
        this.xid = "WL_00000";
        this.name = "";
        this.userId = 0;
        this.pointList = [];
        this.watchListUsers = [];
    }

    static map(wl, order = null) {
        if(!wl instanceof WatchList) {
            throw new Error("Not supported Type!")
        }
        
        const json = new WatchListJson();
        json.id = wl.id;
        json.xid = wl.xid;
        json.name = wl.name;
        json.userId = wl.userId;
        if(wl.pointList.length > 0 && wl.pointList[0].id !== undefined) {
            json.pointList = wl.pointList.map(
                (p) => {return {id: p.id, xid: p.xid, name: p.name}});
        } else if (wl.pointList.length > 0 && wl.pointList[0].identifier.id !== undefined) {
            json.pointList = wl.pointList.map(
                (p) => {return {id: p.identifier.id, xid: p.identifier.xid, name: p.identifier.name}});
        }
        if(!!order && order.length > 0) {
            json.pointList = order.map(p => {return {id: p.id, xid: p.xid, name: p.name}});
        }
        return json;
    }
}