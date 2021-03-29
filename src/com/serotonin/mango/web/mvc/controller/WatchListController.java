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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scada_lts.permissions.service.GetObjectsWithAccess;
import org.scada_lts.permissions.service.GetWatchListsWithAccess;
import org.scada_lts.permissions.ACLConfig;
import org.scada_lts.permissions.PermissionWatchlistACL;
import org.scada_lts.permissions.model.EntryDto;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.web.i18n.I18NUtils;

public class WatchListController extends ParameterizableViewController {
	public static final String KEY_WATCHLISTS = "watchLists";
	public static final String KEY_SELECTED_WATCHLIST = "selectedWatchList";

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) {
		return new ModelAndView(getViewName(), createModel(request));
	}

	protected Map<String, Object> createModel(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		User user = Common.getUser(request);

		// The user's permissions may have changed since the last session, so
		// make sure the watch lists are correct.
		WatchListDao watchListDao = new WatchListDao();
		List<WatchList> watchLists = new ArrayList<WatchList>();
		List<IntValuePair> vwatchLists = vwatchLists = new ArrayList<IntValuePair>();
		if (!user.isAdmin()) {
//			watchLists = watchListDao.getWatchLists(user.getId(),
//					user.getUserProfile());

			if (ACLConfig.getInstance().isPermissionFromServerAcl()) {
				// ACL start
				watchLists = watchListDao.getWatchLists();
				List<IntValuePair> watchListsIVP = new ArrayList<IntValuePair>();
				for (WatchList wl : watchLists) {
					watchListsIVP.add(new IntValuePair(wl.getId(), wl.getName()));
				}

				Map<Integer, EntryDto> mapToCheckId = PermissionWatchlistACL.getInstance().filter(user.getId());
				for (IntValuePair vwl : watchListsIVP) {
					if (mapToCheckId.get(vwl.getKey()) != null) {
						vwatchLists.add(vwl);
					}
				}
				//watchLists.stream().filter(watchList -> mapToCheckId.get(watchList.getKey()) != null );
				// ACL end;
			} else {
			    GetObjectsWithAccess<WatchList, User> watchListService = new GetWatchListsWithAccess();
				watchLists = watchListService.getObjectsWithAccess(user);
            }
		} else {
			watchLists = watchListDao.getWatchLists();
			for(WatchList wl : watchLists){
				vwatchLists.add(new IntValuePair(wl.getId(), wl.getName()));
			}
		}

		List<IntValuePair> watchListNames = new ArrayList<IntValuePair>();

		if (watchLists.size() == 0) {
			// Add a default watch list if none exist.
			WatchList watchList = new WatchList();
			watchList.setName(I18NUtils.getMessage(
					ControllerUtils.getResourceBundle(request),
					"common.newName"));
			watchLists.add(watchListDao.createNewWatchList(watchList,
					user.getId()));
		}

		int selected = user.getSelectedWatchList();
		boolean found = false;

		if (ACLConfig.getInstance().isPermissionFromServerAcl()) {
			watchListNames = vwatchLists;
		} else {
			watchListNames = new ArrayList<IntValuePair>(
					watchLists.size());
			for (WatchList watchList : watchLists) {
				if (watchList.getId() == selected)
					found = true;
				if (watchList.getUserAccess(user) == ShareUser.ACCESS_OWNER
						|| user.isAdmin()) {
					// If this is the owner or admin, check that the user still has
					// access to
					// the points. If not, remove the
					// unauthorized points, resave, and continue.
					boolean changed = false;
					List<DataPointVO> list = watchList.getPointList();
					List<DataPointVO> copy = new ArrayList<DataPointVO>(list);
					for (DataPointVO point : copy) {
						if (point == null
								|| !Permissions.hasDataPointReadPermission(user,
								point)) {
							list.remove(point);
							changed = true;
						}
					}
					if (changed)
						watchListDao.saveWatchList(watchList);
				}
				watchListNames.add(new IntValuePair(watchList.getId(), watchList
						.getName()));
			}
		}

		if (!found) {
			// The user's default watch list was not found. It was either
			// deleted or unshared from them. Find a new one.
			// The list will always contain at least one, so just use the id of
			// the first in the list.
			selected = watchLists.get(0).getId();
			user.setSelectedWatchList(selected);
			new WatchListDao().saveSelectedWatchList(user.getId(), selected);
		}

		model.put(KEY_WATCHLISTS, watchListNames);

		model.put(KEY_SELECTED_WATCHLIST, selected);

		return model;
	}
}
