export class GraphicalViewItem {

    constructor(username) {
        this.id = -1;
        this.xid = "GV_000000";
        this.name = "";
        this.backgroundFilename = null;
        this.width = 1024;
        this.height = 768;
        this.resolution = 2;
        this.userId = username;
        this.viewComponents = [];
        this.anonymousAccess = 0;
        this.viewUsers = [];
        this.modificationTime = new Date().getTime();
        this.new = true;
    }

}

export default GraphicalViewItem;