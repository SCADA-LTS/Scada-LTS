export class WatchListPointHierarchyNode {

    constructor(pointHierarchyItem, watchList) {
        this.id = pointHierarchyItem.folder ? `f${pointHierarchyItem.key}` : `p${pointHierarchyItem.key}`;
        this.name = pointHierarchyItem.title;
        if(!!pointHierarchyItem.pointHierarchyDataSource) {
            this.dataSource = pointHierarchyItem.pointHierarchyDataSource.name;
        }
        this.xid = pointHierarchyItem.xid;
        this.folder =  pointHierarchyItem.folder;
        if(pointHierarchyItem.folder) {
            this.children = [];
        } else {
            if(!!watchList) {
                this.selected = !!watchList.pointList.find(p => p.identifier.id === pointHierarchyItem.key);
            } else {
                this.selected = false;
            }
        }
    }
}

export default WatchListPointHierarchyNode;