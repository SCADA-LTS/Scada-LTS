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

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.convert.IdNameToIntValuePair;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.permissions.service.GetObjectsWithAccess;
import org.scada_lts.permissions.service.GetViewsWithAccess;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.serotonin.mango.util.ViewControllerUtils.getViewCurrent;

public class ViewsController extends ParameterizableViewController {
	private Log LOG = LogFactory.getLog(ViewsController.class);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		ViewService viewService = new ViewService();
		User user = Common.getUser(request);
		List<IntValuePair> views;

		if (user.isAdmin()) { // Admin user has access to all views
			views = IdNameToIntValuePair.convert(viewService.getAllViewNames());
			Comparator<IntValuePair> comp = (IntValuePair prev, IntValuePair next) -> {
			    return prev.getValue().compareTo(next.getValue());
			};
			Collections.sort(views, comp);
			if(LOG.isDebugEnabled()) LOG.debug("Views: " + views.size());
			model.put("views", views);
		} else {
		    GetObjectsWithAccess<View, User> service = new GetViewsWithAccess();
			views = service.getObjectIdentifiersWithAccess(user).stream()
					.map(a -> new IntValuePair(a.getId(), a.getName()))
					.collect(Collectors.toList());

			/* ** Disable ACL **
			// ACL start
			views = viewDao.getAllViewNames();
			Map<Integer, EntryDto> mapToCheckId = PermissionViewACL.getInstance().filter(user.getId());
			List<IntValuePair> vviews = new ArrayList<IntValuePair>();
			for (IntValuePair vp: views) {
				if (mapToCheckId.get(vp.getKey())!=null) {
					vviews.add(vp);
				}
			}
			//views.stream().filter(view -> mapToCheckId.get(view.getKey()) != null );
			// ACL end;
			*/

			Comparator<IntValuePair> comp = (IntValuePair prev, IntValuePair next) -> {
			    return prev.getValue().compareTo(next.getValue());
			};
			Collections.sort(views, comp);
			if(LOG.isDebugEnabled()) LOG.debug("Views: " + views.size());
			model.put("views", views);
		}

		// Set the current view.
		View currentView = getViewCurrent(request, viewService);

		if (currentView == null && !views.isEmpty())
			currentView = viewService.getView(views.get(0).getKey());

		if (currentView != null) {
			if (!user.isAdmin())
				Permissions.ensureViewPermission(user, currentView);

			// Make sure the owner still has permission to all of the points in
			// the view, and that components are
			// otherwise valid.
			currentView.validateViewComponents(user, currentView);

			// Add the view to the session for the dwr access stuff.
			model.put("currentView", currentView);
			model.put("owner",
					currentView.getUserAccess(user) == ShareUser.ACCESS_OWNER);
			user.setView(currentView);
		}

		return new ModelAndView(getViewName(), model);
	}
}
