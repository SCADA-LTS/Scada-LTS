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
package org.scada_lts.mango.service;

/** 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.*;
import org.scada_lts.dao.model.IdName;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.permissions.service.GetShareUsers;
import org.scada_lts.permissions.service.ViewGetShareUsers;
import org.scada_lts.utils.ApplicationBeans;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;

@Service
public class ViewService {
	
	private Log LOG = LogFactory.getLog(ViewService.class);
	private ViewDAO viewDAO;
	private static Map<Integer, List<IdName>> usersPermissions = new HashMap<Integer, List<IdName>>();
	private GetShareUsers<View> viewGetShareUsers;
	private UsersProfileService usersProfileService;

	public ViewService() {
		this.viewDAO = ApplicationBeans.getBean("viewDAO", ViewDAO.class);
		this.viewGetShareUsers = ApplicationBeans.getViewGetShareUsersBean();
		this.usersProfileService = ApplicationBeans.getUsersProfileService();
	}

	public ViewService(ViewDAO viewDAO, ViewGetShareUsers viewGetShareUsers, UsersProfileService usersProfileService) {
		this.viewDAO = viewDAO;
		this.viewGetShareUsers = viewGetShareUsers;
		this.usersProfileService = usersProfileService;
	}
	
	public List<View> getViews() {
		List<View> views = viewDAO.findAll();
		for (View view: views) {
			view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		}
		return views;
	}

	public List<View> getViews(int userId, int userProfileId) {
		List<View> views = viewDAO.filtered(ViewDAO.VIEW_FILTERED_BASE_ON_ID, " order by name ", new Object[]{userId, userId, ShareUser.ACCESS_NONE, userProfileId}, ViewDAO.NO_LIMIT);
		for (View view: views) {
			view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		}
		return views;
	}
	
	public List<IdName> getViewNames(int userId, int userProfileId) {
		return viewDAO.getViewNames(userId, userProfileId);
	}
	
	public List<IdName> getAllViewNames() {
		return viewDAO.getAllViewNames();
	}
	
	public List<IdName> getViewNamesWithReadOrWritePermissions(
			int userId, int userProfileId) {
		List<IdName> allPermissions = usersPermissions.get(userId);
		if (allPermissions == null) {
			allPermissions = updateViewUsersPermissions(userId, userProfileId);
		}
		return allPermissions;
	}
	
	private List<IdName> updateViewUsersPermissions(int userId,
			int userProfileId) {
		
		List<IdName> allPermissions;
		allPermissions = viewDAO.getViewNames(userId, userProfileId);

		User user = new UserDao().getUser(userId);

		for (Iterator<IdName> iterator = allPermissions.iterator(); iterator.hasNext();) {

			IdName idDaViewComView = (IdName) iterator.next();

			View view = viewDAO.findById(new Object[] {idDaViewComView.getId()});

			if (view.getUserAccess(user) == ShareUser.ACCESS_NONE) {
				iterator.remove();
			}
		}
		usersPermissions.put(userId, allPermissions);
		return allPermissions;
	}
	
	public View getView(int id) {
		View view = viewDAO.findById(new Object[] { id });
		if(view != null)
			view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		return view;
	}
	
	public View getViewByXid(String xid) {
		return viewDAO.findByXId(new Object[] {xid});
	}
	
	public View getView(String name) {
		View view = viewDAO.getView(name);
		
		if (view == null) {
			return null;
		}
		view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		return view;
	}

	public List<ShareUser> getShareUsers(View view) {
		return viewGetShareUsers.getShareUsersWithProfile(view);
	}

	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(View.XID_PREFIX, "mangoViews");
	}
	
	public boolean isXidUnique(String xid, int exludeId) {
		return DAO.getInstance().isXidUnique(xid, exludeId, "mangoViews");
	}
	
	public void saveView(final View view) {
		LOG.debug("View name: " + view.getName());
		if (view.getId() == Common.NEW_ID) {
			viewDAO.create(view);
		} else {
			viewDAO.update(view);
		}

		//sharing an object doesn't work
		//saveViewUsers(view);

		//TODO why don't update
		usersPermissions.clear();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void removeView(final int viewId) {
		viewDAO.deleteViewForUser(viewId);
		View v = new View();
		v.setId(viewId);
		viewDAO.delete(v);
		usersProfileService.updateViewPermissions();
	}

	
	private void saveViewUsers(final View view) {
		// Delete anything that is currently there.
		viewDAO.deleteViewForUser(view.getId()); 

		viewDAO.batchUpdateInfoUsers(view);
		
		// Update cache
		List<ShareUser> shareUsers = view.getViewUsers();
		for (Iterator<ShareUser> iterator = shareUsers.iterator(); iterator.hasNext();) {
			ShareUser shareUser = iterator.next();
			usersPermissions.remove(shareUser.getUserId());
			// updateViewUsersPermissions(shareUser.getUserId());
		}
	}	
	
	public void removeUserFromView(int viewId, int userId) {
		viewDAO.deleteViewForUser(viewId, userId);
		usersProfileService.updateViewPermissions();
	}


	public List<ScadaObjectIdentifier> getSimpleViews() {
		return viewDAO.selectViewIdentifiers();
	}
}
