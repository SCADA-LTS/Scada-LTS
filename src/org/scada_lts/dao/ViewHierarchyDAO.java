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
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.scada_lts.dao.model.viewshierarchy.ViewInViewHierarchyNode;
import org.scada_lts.exception.ViewHierarchyDaoException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * DAO for Hierarchy View
 *
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Repository
public class ViewHierarchyDAO implements GenericHierarchyDAO<ViewHierarchyNode> {
	
	private static final Log LOG = LogFactory.getLog(ViewHierarchyDAO.class);
	
	private final static int COLUMN_INDEX_ID = 1;
	private final static int COLUMN_INDEX_PARENT_ID = 2;
	private final static int COLUMN_INDEX_NAME = 3;
	
	//views_category_view_hierarchy
	private final static int COLUMN_VIEW_ID = 1;
	private final static int COLUMN_FOLDER_VIEW_HIERARCHY_ID=2;
	
	
	// @formatter:off
		private static final String SQL = "" +
				"call prc_views_hierarchy_select();";
		
		private static final String SQL_VIEW_IN_VIEW_HIERARCHY = "" +
				"call prc_views_category_views_hierarchy_select();";
		
		private static final String SQL_NODE = "" +
				"call prc_views_hierarchy_select_node(?);";
		
		private static final String SQL_ADD = "" +
				"select func_views_hierarchy_add(?,?);";
		
		private static final String SQL_UPDATE = "" +
				"select func_views_hierarchy_update(?,?,?);";
		
		private static final String SQL_MOVE_FOLDER = "" +
				"select func_views_hierarchy_move_folder(?,?);";
		
		private static final String SQL_MOVE_VIEW = "" +
				"select func_views_hierarchy_move_view(?,?);";
		
		private static final String SQL_DELETE_FOLDER = "" +
				"select func_views_hierarchy_folder_delete(?);";
		
		private static final String SQL_DELETE_VIEW = "" +
				"select func_views_hierarchy_view_delete(?);";
		
		private class ViewHierarchyRowMapper implements RowMapper<ViewHierarchyNode> {

			@Override
			public ViewHierarchyNode mapRow(ResultSet rs, int rowNum) throws SQLException {
				ViewHierarchyNode vhn = new ViewHierarchyNode(
						rs.getInt(COLUMN_INDEX_ID),
						rs.getInt(COLUMN_INDEX_PARENT_ID),
						rs.getString(COLUMN_INDEX_NAME)
				);
				return vhn;
			}
		}
		
		private class ViewInViewHierarchyRowMapper implements RowMapper<ViewInViewHierarchyNode> {
			
			@Override
			public ViewInViewHierarchyNode mapRow(ResultSet rs, int rowNum) throws SQLException {
				ViewInViewHierarchyNode vInVhn = new ViewInViewHierarchyNode(
						rs.getLong(COLUMN_VIEW_ID),
						rs.getLong(COLUMN_FOLDER_VIEW_HIERARCHY_ID)
				);
				return vInVhn;
			}
		}

	// @formatter:on
		
	public static final byte ROOT_ID = -1;
		
	/**
	 * 	Return nodes for hierarchy views
	 * @return
	 */
	public List<ViewHierarchyNode> getAll() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			List<ViewHierarchyNode> listViewHierarchyNode = DAO.getInstance().getJdbcTemp().query(SQL, new ViewHierarchyRowMapper());
			return listViewHierarchyNode;
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return null;
    }
	
	/**
	 * 	Return one node for hierarchy views
	 * @return
	 */
	public List<ViewHierarchyNode> getNode(long l) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			List<ViewHierarchyNode> listViewHierarchyNode = DAO.getInstance().getJdbcTemp().query(SQL_NODE, new Object[]{l}, new ViewHierarchyRowMapper());
			return listViewHierarchyNode;
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return null;
    }

	/**
	 * 
	 * @return
	 */
	public List<ViewInViewHierarchyNode> getViewInHierarchyNode() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		try {
			List<ViewInViewHierarchyNode> listViewInViewHierarchyNode = DAO.getInstance().getJdbcTemp().query(SQL_VIEW_IN_VIEW_HIERARCHY, new Object[]{}, new ViewInViewHierarchyRowMapper() );
			return listViewInViewHierarchyNode;
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return null;
	}

	/**
	 * 	Add node for hierarchy views  
	 */
	public int add(ViewHierarchyNode node) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}

		return DAO.getInstance().getJdbcTemp().queryForObject(SQL_ADD, new Object[]{node.getParentId(), node.getName()}, Integer.class);
		
    }
	
	/**
	 * 	Update node for hierarchy views
	 * @return 
	 * @return
	 */
	public int update(ViewHierarchyNode node) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(SQL_UPDATE, new Object[]{node.getId(), node.getParentId(), node.getName()}, Integer.class);
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return ERROR;

    }
	
	/**
	 * 	Move node for hierarchy views
	 * @return 
	 * @return
	 */
	public int moveFolder(int id, int newParentId) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(SQL_MOVE_FOLDER, new Object[]{id,newParentId}, Integer.class);
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return ERROR;
    }
	
	/**
	 * 	Move node for hierarchy views
	 * @return 
	 * @return
	 */
	public int moveView(int id, int newParentId) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(SQL_MOVE_VIEW, new Object[]{id,newParentId}, Integer.class);
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return ERROR;
    }
	
	/**
	 * 	Delete view from hierarchy views
	 * @return 
	 * @return
	 */
	public int delView(int id) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(SQL_DELETE_VIEW, new Object[]{id}, Integer.class);
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return ERROR;
    }
	
	/**
	 * 	Delete folder from hierarchy views
	 * @return 
	 * @return
	 */
	public int delFolder(int id) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(SQL_DELETE_FOLDER, new Object[]{id}, Integer.class);
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return ERROR;
    }
	
}
