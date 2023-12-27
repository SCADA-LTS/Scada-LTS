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
package org.scada_lts.dao.pointhierarchy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.web.mvc.api.dto.FolderPointHierarchy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
public class PointHierarchyXidDAO extends PointHierarchyDAO {

    private static final String ROOT = "_";
    private static final int ROOT_PARENT_ID = 0;

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";
    private static final String COLUMN_NAME_PARENT_ID = "parentId";
    private static final String COLUMN_NAME_PARENT_XID = "parentXid";
    private static final String COLUMN_NAME_NAME = "name";

    private static final Log LOG = LogFactory.getLog(PointHierarchyXidDAO.class);

    private static final String SELECT_POINT_ID =
            "SELECT id FROM dataPoints WHERE xid=?";

    private static final String SELECT_FOLDER_ID =
            "SELECT id FROM pointHierarchy WHERE xid=?";

    private static final String SELECT_FOLDER_XID =
            "SELECT xid FROM pointHierarchy WHERE id=?";

    private static final String UPDATE_FOLDER_NAME_BASE_ON_XID =
            "UPDATE pointHierarchy SET name=? WHERE xid=?";

    // @formatter:off
    private static final String SELECT_FOLDER =
                "SELECT " +
                    COLUMN_NAME_NAME + ", " +
                    COLUMN_NAME_ID + ", " +
                    COLUMN_NAME_XID + ", " +
                    COLUMN_NAME_PARENT_ID + ", " +
                        "(SELECT " +
                            COLUMN_NAME_XID + " " +
                        "FROM pointHierarchy " +
                        "WHERE id=" + "ph."+ COLUMN_NAME_PARENT_ID + ") as " + COLUMN_NAME_PARENT_XID + " " +
                "FROM pointHierarchy ph WHERE xid=?";

    private static final String SELECT_FOLER_ID_BASE_ON_XID =
            "SELECT " +
                    COLUMN_NAME_ID + " " +
            "FROM pointHierarchy ph WHERE xid=?";

    // @formatter:on

    // @formatter:off
    private static final String SELECT_FOLDERS =
                "SELECT " +
                    COLUMN_NAME_NAME + ", " +
                    COLUMN_NAME_ID + ", " +
                    COLUMN_NAME_XID + ", " +
                    COLUMN_NAME_PARENT_ID + ", " +
                        "(SELECT " +
                            COLUMN_NAME_XID + " " +
                        "FROM pointHierarchy " +
                        "WHERE id=" + "ph."+ COLUMN_NAME_PARENT_ID + ") as " + COLUMN_NAME_PARENT_XID + " " +
                "FROM pointHierarchy ph";
    // @formatter:on


    private static final String UPDATE_FOLDER_HIERARCHY_XID =
            "UPDATE "
                    + "pointHierarchy SET xid=func_gen_xid_point_hierarchy(id) WHERE id=?";

    private static final String DELETE_FOLDER_HIERARCHY_XID =
            "DELETE FROM pointHierarchy WHERE xid=?";

    private static final String INSERT_FOLDER_HIERARCHY_XID = ""
            + "INSERT INTO pointHierarchy (parentId, name, xid) values (?,?,?)";



    private class FolderPointHierarchyRowMapper implements RowMapper<FolderPointHierarchy> {

        @Override
        public FolderPointHierarchy mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            FolderPointHierarchy fph = new FolderPointHierarchy();
            fph.setId(resultSet.getInt(COLUMN_NAME_ID));
            fph.setXid(resultSet.getString(COLUMN_NAME_XID));
            fph.setParentId(resultSet.getInt(COLUMN_NAME_PARENT_ID));
            fph.setName(resultSet.getString(COLUMN_NAME_NAME));
            fph.setParentXid(( resultSet.getString(COLUMN_NAME_PARENT_XID)));

            return fph;
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED,
            rollbackFor = SQLException.class)
    public boolean updateNameFolder(String xidFolder, String newName) {
        int rows = DAO.getInstance().getJdbcTemp().update(UPDATE_FOLDER_NAME_BASE_ON_XID, new Object[]{newName, xidFolder});
        PointHierarchyDAO.cachedPointHierarchy = null;
        return rows > 0;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED,
            rollbackFor = SQLException.class)
    public boolean updateParentPoint(String xidPoint, String xidFolder) {
        int id = DAO.getInstance().getJdbcTemp().queryForObject(SELECT_POINT_ID, new Object[]{xidPoint}, Integer.class);
        int parentId = ROOT_PARENT_ID;
        if (!xidFolder.equals(ROOT)) {
            parentId = DAO.getInstance().getJdbcTemp().queryForObject(SELECT_FOLDER_ID, new Object[]{xidFolder}, Integer.class);
        }
        return updateParentIdDataPoint(id, parentId);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED,
            rollbackFor = SQLException.class)
    public boolean updateParentPoint(String xidPoint, Integer idFolder) {
        int id = DAO.getInstance().getJdbcTemp().queryForObject(SELECT_POINT_ID, new Object[]{xidPoint}, Integer.class);

        return updateParentIdDataPoint(id, idFolder);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED,
            rollbackFor = SQLException.class)
    public boolean updateFolder(String xidFolder, String newParentXidFolder) {

        int id = DAO.getInstance().getJdbcTemp().queryForObject(SELECT_FOLER_ID_BASE_ON_XID, new Object[]{xidFolder}, Integer.class);
        int newParentId = 0;
        try {
            newParentId = DAO.getInstance().getJdbcTemp().queryForObject(SELECT_FOLER_ID_BASE_ON_XID, new Object[]{newParentXidFolder}, Integer.class);
        } catch(EmptyResultDataAccessException e) {
            newParentId = 0;
        }
        return updateParentId(id, newParentId);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    public void add(FolderPointHierarchy folderPointHierarchy) {
        int parentId = 0;
        if (folderPointHierarchy.getParentXid() != null) {
            try {
                parentId = DAO.getInstance().getJdbcTemp().queryForObject(
                        SELECT_FOLDER_ID,
                        new Object[]{folderPointHierarchy.getParentXid()},
                        Integer.class);
            } catch (EmptyResultDataAccessException e) {
                // do not do anything
            }
        }
        DAO.getInstance().getJdbcTemp().update(INSERT_FOLDER_HIERARCHY_XID, new Object[]{parentId, folderPointHierarchy.getName(), folderPointHierarchy.getXid()});
        Integer idFolder = DAO.getInstance().getId();
        for(String xidPoint : folderPointHierarchy.getPointXids()) {
            updateParentPoint(xidPoint, idFolder);
        }
    }

    public FolderPointHierarchy folderCheckExist(String xidFolder) {
        try {
            //TODO use java.utils.Optional
            FolderPointHierarchy fph = DAO.getInstance().getJdbcTemp().queryForObject(SELECT_FOLDER, new Object[]{xidFolder}, new FolderPointHierarchyRowMapper());
            return fph;
        } catch (EmptyResultDataAccessException e) {
            LOG.trace(e);
        }
        //TODO Optional
        return null;
    }

    public List<FolderPointHierarchy> getFolders() {
        try {
            //TODO use java.utils.Optional
            List<FolderPointHierarchy> fph = DAO.getInstance().getJdbcTemp().query(SELECT_FOLDERS, new Object[]{}, new FolderPointHierarchyRowMapper());
            return fph;
        } catch (EmptyResultDataAccessException e) {
            LOG.trace(e);
        }
        //TODO Optional
        return null;
    }



    public void deleteFolderXid(String xidFolder) {
        DAO.getInstance().getJdbcTemp().update(DELETE_FOLDER_HIERARCHY_XID, new Object[]{xidFolder});
    }

    public String getFolderXid(int folderId) {
        return DAO.getInstance().getJdbcTemp().queryForObject(SELECT_FOLDER_XID, new Object[]{folderId}, String.class);
    }


}
