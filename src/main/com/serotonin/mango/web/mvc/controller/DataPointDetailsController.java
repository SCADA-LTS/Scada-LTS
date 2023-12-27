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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scada_lts.mango.adapter.MangoEvent;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.mango.service.ViewService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.chart.ImageChartRenderer;
import com.serotonin.mango.view.chart.ImageFlipbookRenderer;
import com.serotonin.mango.view.chart.TableChartRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.StringUtils;

public class DataPointDetailsController extends ParameterizableViewController {
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		User user = Common.getUser(request);

		int id;
		DataPointDao dataPointDao = new DataPointDao();
		String idStr = request.getParameter("dpid");
		DataPointVO point = null;

		if (StringUtils.isEmpty(idStr)) {
			// Check for pedid (point event detector id)
			String pedStr = request.getParameter("pedid");
			if (pedStr == null) {
				// Check if an XID was provided.
				String xid = request.getParameter("dpxid");
				if (xid == null)
					throw new ShouldNeverHappenException(
							"One of dpid, dpxid, or pedid must be provided for this view");

				model.put("currentXid", xid);
				point = dataPointDao.getDataPoint(xid);
				id = point == null ? -1 : point.getId();
			} else {
				int pedid = Integer.parseInt(pedStr);
				id = dataPointDao.getDataPointIdFromDetectorId(pedid);
			}
		} else
			id = Integer.parseInt(idStr);

		// Put the point in the model.
		if (point == null)
			point = dataPointDao.getDataPoint(id);

		if (point != null) {
			Permissions.ensureDataPointReadPermission(user, point);

			model.put("point", point);

			// Get the views for this user that contain this point.
			List<View> userViews = new ViewService().getViews(user.getId(),
					user.getUserProfile());
			List<View> views = new LinkedList<View>();
			for (View view : userViews) {
				view.validateViewComponents(false);
				if (view.containsValidVisibleDataPoint(id))
					views.add(view);
			}
			model.put("views", views);

			// Get the users that have access to this point.
			List<User> allUsers = new UserDao().getUsers();
			List<Map<String, Object>> users = new LinkedList<Map<String, Object>>();
			Map<String, Object> userData;
			int accessType;
			for (User mangoUser : allUsers) {
				accessType = Permissions.getDataPointAccessType(mangoUser,
						point);
				if (accessType != Permissions.DataPointAccessTypes.NONE) {
					userData = new HashMap<String, Object>();
					userData.put("user", mangoUser);
					userData.put("accessType", accessType);
					users.add(userData);
				}
			}
			model.put("users", users);

			// Determine whether the link to edit the point should be displayed
			model.put("pointEditor", user.isAdmin());

			MangoEvent eventService = new EventService();
			// Put the events in the model.
			model.put("events",
					eventService.getEventsForDataPoint(id, user.getId()));

			// Put the default history table count into the model. Default to
			// 10.
			int historyLimit = 10;
			if (point.getChartRenderer() instanceof TableChartRenderer)
				historyLimit = ((TableChartRenderer) point.getChartRenderer())
						.getLimit();
			else if (point.getChartRenderer() instanceof ImageFlipbookRenderer)
				historyLimit = ((ImageFlipbookRenderer) point
						.getChartRenderer()).getLimit();
			model.put("historyLimit", historyLimit);

			// Determine our image chart rendering capabilities.
			if (ImageChartRenderer.getDefinition().supports(
					point.getPointLocator().getDataTypeId())) {
				// This point can render an image chart. Carry on...
				int periodType = Common.TimePeriods.DAYS;
				int periodCount = 1;
				if (point.getChartRenderer() instanceof ImageChartRenderer) {
					ImageChartRenderer r = (ImageChartRenderer) point
							.getChartRenderer();
					periodType = r.getTimePeriod();
					periodCount = r.getNumberOfPeriods();
				}
				model.put("periodType", periodType);
				model.put("periodCount", periodCount);
			}

			// Determine out flipbook rendering capabilities
			if (ImageFlipbookRenderer.getDefinition().supports(
					point.getPointLocator().getDataTypeId()))
				model.put("flipbookLimit", 10);

			model.put("currentXid", point.getXid());
		}

		// Set the point in the session for the dwr.
		user.setEditPoint(point);

		// Find accessible points for the goto list
		ControllerUtils.addPointListDataToModel(user, id, model);

		return new ModelAndView(getViewName(), model);
	}
}
