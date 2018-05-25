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
import org.scada_lts.dao.pointhierarchy.PointHierarchyDAO;
import org.scada_lts.dao.pointhierarchy.PointHierarchyXidDAO;
import org.scada_lts.web.mvc.api.dto.FolderPointHierarchy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
@Service
public class PointHierarchyXidService extends PointHierarchyService {

    private static final Log LOG = LogFactory.getLog(PointHierarchyXidService.class);

    @Resource
    private PointHierarchyXidDAO pointHierarchyXidDAO;

    public boolean movePoint(String xidPoint, String xidFolder) {
        boolean res = false;
        try {
            res = pointHierarchyXidDAO.updateParent(xidPoint, xidFolder);
        } catch (Exception e) {
            LOG.error(e);
        }
        return res;
    }

    public boolean moveFolder(String xidFolder, String newParentXidFolder) {
        boolean res = false;
        try {
            res = pointHierarchyXidDAO.updateFolder(xidFolder, newParentXidFolder);
        } catch (Exception e) {
            LOG.error(e);
        }
        return res;
    }

    public void folderAdd(FolderPointHierarchy folderPointHierarchy) {
        pointHierarchyXidDAO.add(folderPointHierarchy);
    }

    public boolean folderCheckExist(String xidFolder) {
        return pointHierarchyXidDAO.folderCheckExist(xidFolder);
    }

    public void cacheRefresh() {
        PointHierarchyDAO.cachedPointHierarchy = null;
    }

    public void deleteFolderXid(String xidFolder) {
        pointHierarchyXidDAO.deleteFolderXid(xidFolder);
    }
}
