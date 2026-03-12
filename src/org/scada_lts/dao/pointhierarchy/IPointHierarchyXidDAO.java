package org.scada_lts.dao.pointhierarchy;

public interface IPointHierarchyXidDAO extends org.scada_lts.dao.pointhierarchy.IPointHierarchyDAO {
    public abstract java.util.List<org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode> getPointsHierarchy();
    public abstract com.serotonin.mango.vo.DataPointVO getPointsHierarchy(int arg0);
    public abstract java.util.Map<java.lang.Integer, java.util.List<com.serotonin.mango.vo.hierarchy.PointFolder>> getFolderList();
    public abstract boolean updateTitle(int arg0, java.lang.String arg1);
    public abstract boolean updateParentIdDataPoint(int arg0, int arg1);
    public abstract boolean updateParentId(int arg0, int arg1);
    public abstract int insert(int arg0, java.lang.String arg1);
    public abstract int insert(int arg0, int arg1, java.lang.String arg2);
    public abstract void delete();
    public abstract boolean deleteFolder(int arg0, int arg1);
    public abstract boolean updateNameFolder(java.lang.String arg0, java.lang.String arg1);
    public abstract boolean updateParentPoint(java.lang.String arg0, java.lang.String arg1);
    public abstract boolean updateParentPoint(java.lang.String arg0, java.lang.Integer arg1);
    public abstract boolean updateFolder(java.lang.String arg0, java.lang.String arg1);
    public abstract void add(org.scada_lts.web.mvc.api.dto.FolderPointHierarchy arg0);
    public abstract org.scada_lts.web.mvc.api.dto.FolderPointHierarchy folderCheckExist(java.lang.String arg0);
    public abstract java.util.List<org.scada_lts.web.mvc.api.dto.FolderPointHierarchy> getFolders();
    public abstract void deleteFolderXid(java.lang.String arg0);
    public abstract java.lang.String getFolderXid(int arg0);
}

