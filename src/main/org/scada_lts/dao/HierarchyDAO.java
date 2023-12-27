/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyDataSource;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.scada_lts.exception.HierarchyDaoException;
import org.springframework.jdbc.core.RowMapper;

/**
 * DAO for Hierarchy data Points (folders).
 *
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class HierarchyDAO {

    private static final Log LOG = LogFactory.getLog(HierarchyDAO.class);

    private final static int COLUMN_INDEX_ID = 1;
    private final static int COLUMN_INDEX_XID = 2;
    private final static int COLUMN_INDEX_PARENT_ID = 3;
    private final static int COLUMN_INDEX_NAME = 4;

    private final static boolean IS_FOLDER = true;
    private final static PointHierarchyDataSource INFO_DATA_SOURCE = null;


    // @formatter:off
		private static final String SQL = "" +
				"select "
				    + "ph.id, "
					+ "ph.xid, "
					+ "ph.parentId, "
				    + "ph.name "
	          + "from "
	            	+ "pointHierarchy ph "
			  + "order by ph.id";
	          //+ "order by ph.parentId";
	// @formatter:on

    /**
     * Return folders for data points
     *
     * @return
     */
    public List<PointHierarchyNode> getHierarchy() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("SQL HierarchyDAO");
        }

        try {
            List<PointHierarchyNode> listHierarchyNode = DAO.getInstance().getJdbcTemp().query(SQL, new RowMapper<PointHierarchyNode>() {

                @Override
                public PointHierarchyNode mapRow(ResultSet rs, int rownumber) throws SQLException {


                    PointHierarchyNode phn = new PointHierarchyNode(
                            rs.getInt(COLUMN_INDEX_ID),
                            rs.getString(COLUMN_INDEX_XID),
                            rs.getInt(COLUMN_INDEX_PARENT_ID),
                            rs.getString(COLUMN_INDEX_NAME),
                            IS_FOLDER,
                            INFO_DATA_SOURCE
                    );

                    return phn;
                }
            });

            return listHierarchyNode;
        } catch (Exception e) {
            LOG.error(new HierarchyDaoException(e));
        }
        return null;
    }

}
