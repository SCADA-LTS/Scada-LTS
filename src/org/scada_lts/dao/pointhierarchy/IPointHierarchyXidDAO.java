package org.scada_lts.dao.pointhierarchy;

import org.scada_lts.web.mvc.api.dto.FolderPointHierarchy;

import java.util.List;

public interface IPointHierarchyXidDAO extends IPointHierarchyDAO {

    boolean updateNameFolder(String xidFolder, String newName);

    boolean updateParentPoint(String xidPoint, String xidFolder);

    boolean updateParentPoint(String xidPoint, Integer folderId);

    boolean updateFolder(String xidFolder, String newParentXidFolder);

    void add(FolderPointHierarchy folderPointHierarchy);

    FolderPointHierarchy folderCheckExist(String xidFolder);

    List<FolderPointHierarchy> getFolders();

    void deleteFolderXid(String xidFolder);

    String getFolderXid(int folderId);
}

