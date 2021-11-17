export class GraphicalViewItem {

    constructor(username) {
        this.user = username;
        this.id = -1;
        this.xid = "GV_000000";
        this.name = "";
        this.backgroundFilename = null;
        this.anonymousAccess = "NONE";
        this.viewComponents = [];
        this.sharingUsers = [];
        this.resolution = "R1024x768";
    }

}

export default GraphicalViewItem;