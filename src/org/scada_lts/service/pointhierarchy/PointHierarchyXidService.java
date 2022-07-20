/*
 * (c) 2018 grzegorz.bylica@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.service.pointhierarchy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.cache.PointHierarchyCache;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.HierarchyDAO;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.scada_lts.dao.pointhierarchy.PointHierarchyXidDAO;
import org.scada_lts.web.mvc.api.dto.FolderPointHierarchy;
import org.scada_lts.web.mvc.api.dto.FolderPointHierarchyExport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
@Service
public class PointHierarchyXidService extends PointHierarchyService {

    private static final Log LOG = LogFactory.getLog(PointHierarchyXidService.class);

    public PointHierarchyXidService(PointHierarchyXidDAO pointHierarchyXidDAO, DataPointDAO dataPointDAO, HierarchyDAO hierarchyDAO) {
        super(pointHierarchyXidDAO, dataPointDAO, hierarchyDAO);
    }

    public List<FolderPointHierarchy> getFolders() {
        return getPointHierarchyDAO().getFolders();
    }

    public boolean movePoint(String xidPoint, String xidFolder) {
        boolean res = false;
        try {
            //TODO use java.utils.Optional
            res = getPointHierarchyDAO().updateParentPoint(xidPoint, xidFolder);
        } catch (Exception e) {
            LOG.error(e);
        }
        return res;
    }

    public boolean moveFolder(String xidFolder, String newParentXidFolder) {
        boolean res = false;
        try {
            //TODO use java.utils.Optional
            res = getPointHierarchyDAO().updateFolder(xidFolder, newParentXidFolder);
        } catch (Exception e) {
            LOG.error(e);
        }
        return res;
    }

    public void folderAdd(FolderPointHierarchy folderPointHierarchy) {
        getPointHierarchyDAO().add(folderPointHierarchy);
    }

    public List<FolderPointHierarchyExport> fillInThePoints(List<FolderPointHierarchy> folders) throws Exception {
        List<FolderPointHierarchy> lfph = folders;
        List<FolderPointHierarchyExport> nlfph = new ArrayList<FolderPointHierarchyExport>();

        for(FolderPointHierarchy fph : lfph) {
            nlfph.add(new FolderPointHierarchyExport(fill(fph)));
        }

        return  nlfph;
    }

    public FolderPointHierarchy folderCheckExist(String xidFolder) throws Exception {

        FolderPointHierarchy fph = getPointHierarchyDAO().folderCheckExist(xidFolder);
        fph = fill(fph);
        return fph;
    }

    public boolean updateNameFolder(String xidFolder, String newName) {
        return getPointHierarchyDAO().updateNameFolder(xidFolder, newName);
    }

    public void cacheRefresh() {
        try {
            PointHierarchyCache.getInstance().updateData();
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public void deleteFolderXid(String xidFolder) {
        getPointHierarchyDAO().deleteFolderXid(xidFolder);
    }

    private FolderPointHierarchy fill(FolderPointHierarchy fph) throws Exception {

        List<PointHierarchyNode> childrens = PointHierarchyCache.getInstance().getOnBaseParentId(fph.getId());

        List<String> pointXids = childrens.stream()
                .filter(
                        f -> !f.isFolder())
                .map(
                        child -> child.getXid())
                .collect(Collectors.toList());


        fph.setPointXids(pointXids);

        return fph;
    }

}
