/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import br.org.scadabr.vo.permission.ViewAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.IdName;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifierRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;


/**
 * View DAO
 *
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
@Repository
public class ViewDAO implements GenericDAO<View> {
	
	private Log LOG = LogFactory.getLog(ViewDAO.class);
	
	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_DATA = "data";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_BACKGROUND = "background";
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final String COLUMN_NAME_ANONYMOUS_ACCESS = "anonymousAccess";
	private static final String COLUMN_NAME_WIDTH = "width";
	private static final String COLUMN_NAME_HEIGHT = "height";
	private static final String COLUMN_NAME_MODIFICATION_TIME = "modification_time";
	 
	//mangoViewUsers
	private static final String COLUMN_NAME_MVU_USER_ID = "userid";
	private static final String COLUMN_NAME_MVU_ACCESS_TYPE = "accessType";
	private static final String COLUMN_NAME_MVU_VIEW_ID = "mangoViewId";
	
	private static final int COLUMN_INDEX_MVU_VIEW_ID = 1;
	private static final int COLUMN_INDEX_MVU_USER_ID = 2;
	private static final int COLUMN_INDEX_MVU_ACCESS_TYPE = 3;
	
	//userProfile
	private static final String COLUMN_NAME_UP_VIEW_ID = "viewId";
	private static final String COLUMN_NAME_UP_USER_PRFILE_ID = "userProfileId";
	private static final String COLUMN_NAME_UP_PERMISSION = "permission";

	// @formatter:off
	private static final String VIEW_SELECT = ""
			+"select "
				+ COLUMN_NAME_DATA+", "
				+ COLUMN_NAME_ID+", "
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_NAME+", "
				+ COLUMN_NAME_BACKGROUND+", "
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_ANONYMOUS_ACCESS+", "
				+ COLUMN_NAME_WIDTH+", "
				+ COLUMN_NAME_HEIGHT+", "
				+ COLUMN_NAME_MODIFICATION_TIME+" "
			+ "from "
				+ "mangoViews";

    private static final String VIEW_IDENTIFIER_SELECT = ""
            +"select "
            + COLUMN_NAME_ID+", "
            + COLUMN_NAME_XID+", "
            + COLUMN_NAME_NAME+" "
            + "from "
            + "mangoViews";
	
	private static final String VIEW_FILTER_BASE_ON_ID=""
			 +COLUMN_NAME_ID+"=?";
	
	private static final String VIEW_FILTER_BASE_ON_XID=""
			 +COLUMN_NAME_XID+"=?";
	
	private static final String VIEW_FILTER_BASE_ON_NAME=""
			 +COLUMN_NAME_NAME+"=?";
	
	private static final String VIEW_INSERT = ""
			+"insert mangoViews ("
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_NAME+", "
				+ COLUMN_NAME_BACKGROUND+", "
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_ANONYMOUS_ACCESS+", "
				+ COLUMN_NAME_DATA+","
				+ COLUMN_NAME_HEIGHT+", "
				+ COLUMN_NAME_WIDTH+") "
			+ "values (?,?,?,?,?,?,?,?)";
	
	private static final String VIEW_UPDATE = ""
			+"update mangoViews set "
				+ COLUMN_NAME_XID+"=?, "
				+ COLUMN_NAME_NAME+"=?, "
				+ COLUMN_NAME_BACKGROUND+"=?, "
				+ COLUMN_NAME_ANONYMOUS_ACCESS+"=?, "
				+ COLUMN_NAME_DATA+"=?, "
				+ COLUMN_NAME_HEIGHT+"=?, "
				+ COLUMN_NAME_WIDTH+"=?, "
			    + COLUMN_NAME_MODIFICATION_TIME+"=CURRENT_TIMESTAMP "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	private static final String VIEW_DELETE = ""
			+"delete "
				+ "from "
					+ "mangoViews "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	private static final String VIEW_SELECT_ID_NAME = ""
			+ "select "
				+ COLUMN_NAME_ID+", "
				+ COLUMN_NAME_NAME+" "
			+ "from "
				+ "mangoViews";
	
	
	//mangoviewUsers
	private static final String VIEW_USER_BASE_ON_VIEW_ID = ""
			+"select "
				+ COLUMN_NAME_MVU_VIEW_ID+", "
				+ COLUMN_NAME_MVU_USER_ID+", "
				+ COLUMN_NAME_MVU_ACCESS_TYPE+" "
			+ "from "
				+ "mangoViewUsers "
			+ "where "
				+ COLUMN_NAME_MVU_VIEW_ID+"=?";

	private static final String VIEW_USER_BASE_ON_USER_ID = ""
			+"select "
			+ COLUMN_NAME_MVU_VIEW_ID+", "
			+ COLUMN_NAME_MVU_ACCESS_TYPE+" "
			+ "from "
			+ "mangoViewUsers "
			+ "where "
			+ COLUMN_NAME_MVU_USER_ID+"=?";
	
	public static final String VIEW_FILTERED_BASE_ON_ID = ""
			+ COLUMN_NAME_USER_ID+"=? or "
			+ "id in (select "+COLUMN_NAME_MVU_VIEW_ID+" from mangoViewUsers where "+COLUMN_NAME_MVU_USER_ID+"=? and "+COLUMN_NAME_MVU_ACCESS_TYPE+">?) or "
			+ "id in (select "+COLUMN_NAME_UP_VIEW_ID+" from viewUsersProfiles where "+COLUMN_NAME_UP_USER_PRFILE_ID+"=?)";

	public static final String VIEW_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID = ""
			+ COLUMN_NAME_USER_ID+"=? or "
			+ "id in (select "+COLUMN_NAME_MVU_VIEW_ID+" from mangoViewUsers where "+COLUMN_NAME_MVU_USER_ID+"=? and "+COLUMN_NAME_MVU_ACCESS_TYPE+">?) or "
			+ "id in (select "+COLUMN_NAME_UP_VIEW_ID+" from viewUsersProfiles where "+COLUMN_NAME_UP_USER_PRFILE_ID+"=? and "+COLUMN_NAME_UP_PERMISSION+">?)";

	private static final String VIEW_USER_DELETE = ""
			+ "delete "
				+ "from "
					+ "mangoViewUsers "
			+ "where "
				+ COLUMN_NAME_MVU_VIEW_ID+"=?";
	
	private static final String VIEW_USER_INSERT = ""
			+"insert "
				+ "mangoViewUsers ("
					+ COLUMN_NAME_MVU_VIEW_ID+", "
					+ COLUMN_NAME_MVU_USER_ID+", "
					+ COLUMN_NAME_MVU_ACCESS_TYPE+") "
				+ "values (?,?,?)";

	private static final String VIEW_USER_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE = ""
			+"insert "
			+ "mangoViewUsers ("
			+ COLUMN_NAME_MVU_VIEW_ID+", "
			+ COLUMN_NAME_MVU_USER_ID+", "
			+ COLUMN_NAME_MVU_ACCESS_TYPE+") "
			+ "values (?,?,?) ON DUPLICATE KEY UPDATE " +
			COLUMN_NAME_MVU_ACCESS_TYPE + "=?";


	private static final String VIEW_USER_DELETE_BASE_ON_VIEW_ID_USER_ID=""
			+"delete "
				+ "from "
					+ "mangoViewUsers "
			+ "where "
				+ COLUMN_NAME_MVU_VIEW_ID+"=? and "
				+ COLUMN_NAME_MVU_USER_ID+"=?";

	private static final String SHARE_USERS_BY_USERS_PROFILE_AND_VIEW_ID = "" +
			"select " +
			"uup." + COLUMN_NAME_USER_ID + ", " +
			"vup." + COLUMN_NAME_UP_PERMISSION + " " +
			"from " +
			"usersUsersProfiles uup " +
			"left join " +
			"viewUsersProfiles vup " +
			"on " +
			"vup." + COLUMN_NAME_UP_USER_PRFILE_ID + "=uup." + COLUMN_NAME_UP_USER_PRFILE_ID + " " +
			"where " +
			"vup." + COLUMN_NAME_UP_VIEW_ID + "=?;";

	private static final String VIEW_IDENTIFIER_SELECT_ORDER_BY_NAME = ""
			+ VIEW_IDENTIFIER_SELECT + " "
			+ "order by "+COLUMN_NAME_NAME;

	// @formatter:on
	
	// RowMapper
	class ViewRowMapper implements RowMapper<View> {
		public View mapRow(ResultSet rs, int rowNum) throws SQLException {
			View v;
			Blob blob = rs.getBlob(COLUMN_NAME_DATA);
			if (blob == null) {
				// This can happen during upgrade
				v = new View();
			} else {
				v = (View) new SerializationData().readObject(blob.getBinaryStream());
			}
			
			v.setId(rs.getInt(COLUMN_NAME_ID));
			v.setXid(rs.getString(COLUMN_NAME_XID));
			v.setName(rs.getString(COLUMN_NAME_NAME));
			v.setBackgroundFilename(rs.getString(COLUMN_NAME_BACKGROUND));
			v.setUserId(rs.getInt(COLUMN_NAME_USER_ID));
			v.setAnonymousAccess(rs.getInt(COLUMN_NAME_ANONYMOUS_ACCESS));
			v.setWidth(rs.getInt(COLUMN_NAME_WIDTH));
			v.setHeight(rs.getInt(COLUMN_NAME_HEIGHT));
			v.setModificationTime(rs.getTimestamp(COLUMN_NAME_MODIFICATION_TIME).getTime());
			
			return v;
		}
	}
	
	//RowMapper for ShareUser
	class ViewUserRowMapper implements RowMapper<ShareUser> {
		public ShareUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ShareUser vu = new ShareUser();
			vu.setUserId(rs.getInt(COLUMN_NAME_MVU_USER_ID));
			vu.setAccessType(rs.getInt(COLUMN_NAME_MVU_ACCESS_TYPE));
			return vu;
		}
	}
	
	//RowMapper for IdName
	class IdNameRowMapper implements RowMapper<IdName> {
		public IdName mapRow(ResultSet rs, int rowNum) throws SQLException {
			IdName idName = new IdName();
			idName.setId(rs.getInt(COLUMN_NAME_ID));
			idName.setName(rs.getString(COLUMN_NAME_NAME));
			return idName;
		}
	}
	@Override
	public List<View> findAllWithUserName(){
		return null;
	}
	@Override
	public List<View> findAll() {
		return (List<View>) DAO.getInstance().getJdbcTemp().query(VIEW_SELECT, new Object[]{}, new ViewRowMapper() );
	}

	@Override
	public View findById(Object[] pk) {
		try {
			return (View) DAO.getInstance().getJdbcTemp().queryForObject(VIEW_SELECT+ " where " + VIEW_FILTER_BASE_ON_ID, pk , new ViewRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public View findByXId(Object[] pk) {
		try { 
			return (View) DAO.getInstance().getJdbcTemp().queryForObject(VIEW_SELECT+ " where " + VIEW_FILTER_BASE_ON_XID, pk , new ViewRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	//TO rewrite order for example Object[] with column to order.
	public List<View> filtered(String filter, String order, Object[] argsFilter, long limit) {
		
		String myLimit="";
		Object[] args;
		if (limit != NO_LIMIT) {
			myLimit = LIMIT+" ? ";
			args = DAO.getInstance().appendValue(argsFilter, String.valueOf(limit));
		} else {
			args=argsFilter;
		}
	
		return (List<View>) DAO.getInstance().getJdbcTemp().query(VIEW_SELECT+" where "+ filter + order + myLimit, args, new ViewRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public Object[] create(final View entity) {
		
		if (LOG.isTraceEnabled()) {
			  LOG.trace(entity);
		}
			
		KeyHolder keyHolder = new GeneratedKeyHolder();
					
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(VIEW_INSERT, Statement.RETURN_GENERATED_KEYS);
						 				new ArgumentPreparedStatementSetter( new Object[] { 
						 						entity.getXid(),
						 						entity.getName(),
						 						entity.getBackgroundFilename(),
						 						entity.getUserId(),
						 						entity.getAnonymousAccess(),
						 						new SerializationData().writeObject(entity),
						 						entity.getHeight(),
						 						entity.getWidth()
						 				}).setValues(ps);
						 				return ps;
						 			}
					}, keyHolder);
					
			entity.setId(keyHolder.getKey().intValue());		
			return new Object[] {keyHolder.getKey().intValue()};
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public void update(View entity) {
		
		DAO.getInstance().getJdbcTemp().update(VIEW_UPDATE, new Object[]{
				entity.getXid(),
				entity.getName(),
				entity.getBackgroundFilename(),
				entity.getAnonymousAccess(),
				new SerializationData().writeObject(entity),
				entity.getHeight(),
				entity.getWidth(),
				entity.getId()
		});
			
	}

	@Override
	public void delete(View entity) {
		DAO.getInstance().getJdbcTemp().update(VIEW_DELETE, new Object[] { entity.getId() });		
	}

	public List<ShareUser> getShareUsers(int mangoViewId) {
		return (List<ShareUser>) DAO.getInstance().getJdbcTemp().query(VIEW_USER_BASE_ON_VIEW_ID, new Object[] {mangoViewId}, new ViewUserRowMapper());
	}
	
	public List<IdName> getViewNames(int userId, int userProfileId) {
		return DAO.getInstance().getJdbcTemp().query(VIEW_SELECT_ID_NAME + " where " + VIEW_FILTERED_BASE_ON_ID, new Object[] { userId, userId, ShareUser.ACCESS_NONE, userProfileId },new IdNameRowMapper());
	}
	
	public List<IdName> getAllViewNames() {
		return DAO.getInstance().getJdbcTemp().query(VIEW_SELECT_ID_NAME , new Object[] {  },new IdNameRowMapper());
	}
	
	public View getView(String name) {
		return DAO.getInstance().getJdbcTemp().queryForObject(VIEW_SELECT + " where " + VIEW_FILTER_BASE_ON_NAME, new Object[] {name}, new ViewRowMapper());
	}

	//TODO rewrite
	@Override
	public List<View> filtered(String filter, Object[] argsFilter, long limit) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteViewForUser(int viewId) {
		DAO.getInstance().getJdbcTemp().update(VIEW_USER_DELETE, new Object[]{viewId});
	}
	
	public void batchUpdateInfoUsers(final View view) {
		DAO.getInstance().getJdbcTemp().batchUpdate(VIEW_USER_INSERT, new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return view.getViewUsers().size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				ShareUser vu = view.getViewUsers().get(i);
				ps.setInt(COLUMN_INDEX_MVU_VIEW_ID, view.getId());
				ps.setInt(COLUMN_INDEX_MVU_USER_ID, vu.getUserId());
				ps.setInt(COLUMN_INDEX_MVU_ACCESS_TYPE, vu.getAccessType());
			}
		});
	}
	
	public void deleteViewForUser(int viewId, int userId) {
		DAO.getInstance().getJdbcTemp().update(VIEW_USER_DELETE_BASE_ON_VIEW_ID_USER_ID, new Object[]{viewId, userId});
	}

	public List<ViewAccess> selectViewPermissions(final int userId) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("selectViewPermissions(final int userId) userId:" + userId);
		}

		return DAO.getInstance().getJdbcTemp().query(VIEW_USER_BASE_ON_USER_ID, new Object[]{userId}, (rs, rowNum) -> {
			ViewAccess viewAccess = new ViewAccess();
			viewAccess.setId(rs.getInt(COLUMN_NAME_MVU_VIEW_ID));
			viewAccess.setPermission(rs.getInt(COLUMN_NAME_MVU_ACCESS_TYPE));
			return viewAccess;
		});

	}

	public int[] insertPermissions(final int userId, final List<ViewAccess> toInsert) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("insertPermissions(final User user, final List<WatchListAccess> toInsert) user:" + userId + "");
		}

		int[] argTypes = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER };

		List<Object[]> batchArgs = toInsert.stream()
				.map(a -> new Object[] {a.getId(), userId, a.getPermission(), a.getPermission()})
				.collect(Collectors.toList());

		return DAO.getInstance().getJdbcTemp()
				.batchUpdate(VIEW_USER_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE, batchArgs, argTypes);
	}

	public int[] deletePermissions(final int userId, final List<ViewAccess> toDelete) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deletePermissions(final int userId, final List<WatchListAccess> toDelete) user:" + userId);
		}

		int[] argTypes = {Types.INTEGER, Types.INTEGER };

		List<Object[]> batchArgs = toDelete.stream()
				.map(a -> new Object[] {a.getId(), userId})
				.collect(Collectors.toList());

		return DAO.getInstance().getJdbcTemp()
				.batchUpdate(VIEW_USER_DELETE_BASE_ON_VIEW_ID_USER_ID, batchArgs, argTypes);
	}

	public List<View> selectViewWithAccess(int userId, int profileId) {
		return DAO.getInstance().getJdbcTemp().query(VIEW_SELECT + " where " + VIEW_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID,
				new Object[] { userId, userId, ShareUser.ACCESS_NONE, profileId ,ShareUser.ACCESS_NONE},
				new ViewRowMapper());
	}

	public List<ScadaObjectIdentifier> selectViewIdentifiersWithAccess(int userId, int profileId) {
		return DAO.getInstance().getJdbcTemp().query(VIEW_IDENTIFIER_SELECT + " where " + VIEW_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID,
                new Object[] { userId, userId, ShareUser.ACCESS_NONE, profileId, ShareUser.ACCESS_NONE},
                new ScadaObjectIdentifierRowMapper.Builder()
						.idColumnName(COLUMN_NAME_ID)
						.xidColumnName(COLUMN_NAME_XID)
						.nameColumnName(COLUMN_NAME_NAME)
						.build());
	}

    public List<ScadaObjectIdentifier> selectViewIdentifiers() {
        return DAO.getInstance().getJdbcTemp().query(VIEW_IDENTIFIER_SELECT_ORDER_BY_NAME, new Object[]{},
				new ScadaObjectIdentifierRowMapper.Builder()
						.idColumnName(COLUMN_NAME_ID)
						.xidColumnName(COLUMN_NAME_XID)
						.nameColumnName(COLUMN_NAME_NAME)
						.build());
    }

	public List<ShareUser> selectViewShareUsers(int viewId) {
		if (LOG.isTraceEnabled())
			LOG.trace("selectViewShareUsers(int viewId) viewId:" + viewId);
		try {
			return DAO.getInstance().getJdbcTemp().query(SHARE_USERS_BY_USERS_PROFILE_AND_VIEW_ID,
					new Object[]{viewId},
					ShareUserRowMapper.defaultName());
		} catch (EmptyResultDataAccessException ex) {
			return Collections.emptyList();
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			return Collections.emptyList();
		}
	}
}
