export default class WatchList {
    
    constructor() {
        this.userId = 0;
        this.id = -1;
        this.xid = "WL_00000";
        this.name = "";
        this.pointList = [];
        this.accessType = 1;
        this.horizontal = true;
        this.biggerChart = false;
        this.pointOrder = [];
        this.user = null;
    }

    saveDetails() {
        const data = {
            horizontal: this.horizontal,
            biggerChart: this.biggerChart,
        };
        localStorage.setItem(`MWLD_${this.id}`, JSON.stringify(data));
    }
    loadDetails() {
        let data = JSON.parse(localStorage.getItem(`MWLD_${this.id}`));
        if(!!data) {
            this.horizontal = data.horizontal;
            this.biggerChart = data.biggerChart;
        }
    }

    static create (json, userData) {
        let wl = new WatchList();
        wl.userId = json.userId;
        wl.id = json.id;
        wl.xid = json.xid;
        wl.name = json.name;
        wl.pointList = json.pointList;
        wl.accessType = json.accessType;
        wl.pointOrder = WatchList.setPointOrder(wl);
        wl.user = userData || [];
        wl.loadDetails();
        return wl;
    }

    static setPointOrder(wl) {
        let orderMap = new Map();
        for(let i=0; i < wl.pointList.length; i++) {
            orderMap.set(`${wl.pointList[i].identifier.id}`, i)
        }
        return orderMap;
    }


}