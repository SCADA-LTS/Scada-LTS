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
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
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
	
	
	// @formatter:off
		private static final String SQL = "" +
				"call prc_views_hierarchy_select();";
		
		private static final String SQL_NODE = "" +
				"call prc_views_hierarchy_select_node(?);";
		
		private static final String SQL_ADD = "" +
				"select func_views_hierarchy_add(?,?)";
		
		private static final String SQL_UPDATE = "" +
				"select func_views_hierarchy_update(?,?,?);";

		private static final String SQL_MOVE = "" +
				"select func_views_hierarchy_move(?,?);";
		
		private static final String SQL_DELETE = "" +
				"select func_views_hierarchy_delete(?);";
		
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

		
		
	// @formatter:on
		
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
	public List<ViewHierarchyNode> getNode(int parentId) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			List<ViewHierarchyNode> listViewHierarchyNode = DAO.getInstance().getJdbcTemp().query(SQL_NODE, new Object[]{parentId}, new ViewHierarchyRowMapper());
			return listViewHierarchyNode;
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return null;
    }

	
	/**
	 * 	Add node for hierarchy views
	 * @return 
	 * @return
	 */
	public int add(ViewHierarchyNode node) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(SQL_ADD, new Object[]{node.getParentId(), node.getName()}, Integer.class);
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return ERROR;

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
	public int move(int id, int newParentId) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(SQL_MOVE, new Object[]{id,newParentId}, Integer.class);
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
	public int del(int id) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL ViewHierarchyDAO");
		}
		
		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(SQL_DELETE, new Object[]{id}, Integer.class);
		} catch (Exception e) {
			LOG.error(new ViewHierarchyDaoException(e));
		}
		return ERROR;
    }

}
