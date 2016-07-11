/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.mvc.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

import br.org.scadabr.db.dao.UsersProfileDao;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

public class ViewsController extends ParameterizableViewController {
	private Log LOG = LogFactory.getLog(ViewsController.class);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		ViewDao viewDao = new ViewDao();
		User user = Common.getUser(request);
		UsersProfileDao usersProfiles = new UsersProfileDao();
		LOG.trace("User " + user.getUsername());
		List<IntValuePair> views;

		if (user.isAdmin()) { // Admin user has access to all views
			views = viewDao.getAllViewNames();
			LOG.debug("Views: " + views.size());
			Collections.sort(views, new Comparator<IntValuePair>() {
				@Override
				public int compare(IntValuePair ivp1, IntValuePair ivp2) {

					return ivp1.getValue().compareTo(ivp2.getValue());
				}
			});
			model.put("views", views);

		} else {
			UsersProfileVO userProfile = usersProfiles.getUserProfileByUserId(user.getId());
			if (userProfile != null) {
				LOG.trace("Got UserProfile...");
				views = viewDao.getViewNamesWithReadOrWritePermissions(user.getId(), userProfile.getId());
				LOG.debug("Views: " + views.size());
			} else {
				LOG.trace("Got no UserProfile...");
				views = viewDao.getViewNamesWithReadOrWritePermissions(user.getId(), -1);
				LOG.debug("Views: " + views.size());
			}
			Collections.sort(views, new Comparator<IntValuePair>() {
				@Override
				public int compare(IntValuePair ivp1, IntValuePair ivp2) {

					return ivp1.getValue().compareTo(ivp2.getValue());
				}
			});
			model.put("views", views);
		}

		LOG.trace("CurrentView...");
		// Set the current view.
		View currentView = null;
		String vid = request.getParameter("viewId");
		try {
			currentView = viewDao.getView(Integer.parseInt(vid));
			LOG.trace("CurrentView: " + currentView.getName());
		} catch (NumberFormatException e) {
			// no op
		}

		LOG.trace("get views...");
		if (currentView == null && views.size() > 0)
			currentView = viewDao.getView(views.get(0).getKey());

		if (currentView != null) {
			if (!user.isAdmin()) {
				LOG.trace("Here we are going to ensure...");
				LOG.trace(" User: " + user.getUsername());
				Permissions.ensureViewPermission(user, currentView);
			}
			// Make sure the owner still has permission to all of the points in
			// the view, and that components are
			// otherwise valid.
			currentView.validateViewComponents(false);

			// Add the view to the session for the dwr access stuff.
			model.put("currentView", currentView);
			model.put("owner", currentView.getUserAccess(user) == ShareUser.ACCESS_OWNER);
			user.setView(currentView);

			// Define if user can add new views
			// LOG.error("userAddedViews: " +
			// Common.getEnvironmentProfile().getBoolean("mango.views.useradd",
			// true));
			model.put("userAddedViews", Common.getEnvironmentProfile().getBoolean("mango.views.useradd", true));
		}

		return new ModelAndView(getViewName(), model);
	}
}
