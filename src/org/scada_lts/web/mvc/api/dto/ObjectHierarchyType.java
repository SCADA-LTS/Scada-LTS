package org.scada_lts.web.mvc.api.dto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.pointhierarchy.PointHierarchyXidDAO;

public enum ObjectHierarchyType {

    FOLDER {
        @Override
        public boolean move(String moveObjectXid, String destinationFolderXid, PointHierarchyXidDAO pointHierarchyXidDAO) {
            boolean res = false;
            try {
                res = pointHierarchyXidDAO.updateFolder(moveObjectXid, destinationFolderXid);
            } catch (Exception e) {
                LOG.error(e);
            }
            return res;
        }
    },
    POINT {
        @Override
        public boolean move(String moveObjectXid, String destinationFolderXid, PointHierarchyXidDAO pointHierarchyXidDAO) {
            boolean res = false;
            try {
                res = pointHierarchyXidDAO.updateParentPoint(moveObjectXid, destinationFolderXid);
            } catch (Exception e) {
                LOG.error(e);
            }
            return res;
        }
    };

    private static final Log LOG = LogFactory.getLog(ObjectHierarchyType.class);

    public abstract boolean move(String moveObjectXid, String destinationFolderXid, PointHierarchyXidDAO pointHierarchyXidDAO);

}
