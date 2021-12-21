export class PointHierarchyNode {

    constructor(pointHierarchyItem) {
        this.id = pointHierarchyItem.folder ? `f${pointHierarchyItem.key}` : `p${pointHierarchyItem.key}`;
        this.name = pointHierarchyItem.title;
        this.parentId = pointHierarchyItem.parentId;
        if(!!pointHierarchyItem.pointHierarchyDataSource) {
            this.dataSource = pointHierarchyItem.pointHierarchyDataSource.name;
        }
        this.xid = pointHierarchyItem.xid;
        this.folder =  pointHierarchyItem.folder;
        if(this.folder) {
            this.open = false;
        }
        if(pointHierarchyItem.folder) {
            this.children = [];
        }
    }
}

export default PointHierarchyNode;