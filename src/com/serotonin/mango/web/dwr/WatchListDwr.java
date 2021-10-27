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
package com.serotonin.mango.web.dwr;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContextFactory;
import org.joda.time.DateTime;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.dwr.beans.DataExportDefinition;
import com.serotonin.mango.web.dwr.beans.WatchListState;
import com.serotonin.mango.web.taglib.Functions;
import com.serotonin.util.ArrayUtils;
import com.serotonin.util.ObjectUtils;
import com.serotonin.web.dwr.MethodFilter;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.service.UsersProfileService;

public class WatchListDwr extends BaseDwr {
	public Map<String, Object> init() {
		DataPointDao dataPointDao = new DataPointDao();
		Map<String, Object> data = new HashMap<String, Object>();

		PointHierarchy ph = dataPointDao.getPointHierarchy().copyFoldersOnly();
		User user = Common.getUser();
		List<DataPointVO> points = dataPointDao.getDataPoints(
				DataPointExtendedNameComparator.instance, false);
		for (DataPointVO point : points) {
			if (Permissions.hasDataPointReadPermission(user, point))
				ph.addDataPoint(point.getId(), point.getPointFolderId(),
						point.getExtendedName());
		}

		ph.parseEmptyFolders();

		WatchList watchList = new WatchListDao().getWatchList(user
				.getSelectedWatchList());
		prepareWatchList(watchList, user);
		user.setWatchList(watchList);

		data.put("pointFolder", ph.getRoot());
		data.put("shareUsers", getShareUsers(user));
		data.put("selectedWatchList", getWatchListData(user, watchList));
		data.put("admin", user.isAdmin());
		return data;
	}

	/**
	 * Retrieves point state for all points on the current watch list.
	 * 
	 * @param pointIds
	 * @return
	 */
	public List<WatchListState> getPointData() {
		// Get the watch list from the user's session. It should have been set
		// by the controller.
		return getPointDataImpl(Common.getUser().getWatchList());
	}

	private List<WatchListState> getPointDataImpl(WatchList watchList) {
		if (watchList == null)
			return new ArrayList<WatchListState>();

		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();
		User user = Common.getUser(request);

		RuntimeManager rtm = Common.ctx.getRuntimeManager();

		WatchListState state;
		List<WatchListState> states = new ArrayList<WatchListState>(watchList
				.getPointList().size());
		Map<String, Object> model = new HashMap<String, Object>();
		for (DataPointVO point : watchList.getPointList()) {
			// Create the watch list state.
			state = createWatchListState(request, point, rtm, model, user);
			states.add(state);
		}

		return states;
	}

	public void updateWatchListName(String name) {
		User user = Common.getUser();
		WatchList watchList = user.getWatchList();
		Permissions.ensureWatchListEditPermission(user, watchList);
		watchList.setName(name);
		new WatchListDao().saveWatchList(watchList);
	}

	public IntValuePair addNewWatchList(int copyId) {
		User user = Common.getUser();

		WatchListDao watchListDao = new WatchListDao();
		WatchList watchList;

		if (copyId == Common.NEW_ID) {
			watchList = new WatchList();
			watchList.setName(getMessage("common.newName"));
		} else {
			watchList = new WatchListDao().getWatchList(user
					.getSelectedWatchList());
			watchList.setId(Common.NEW_ID);
			watchList.setName(getMessage(new LocalizableMessage(
					"common.copyPrefix", watchList.getName())));
		}
		watchList.setUserId(user.getId());
		watchList.setXid(watchListDao.generateUniqueXid());

		watchListDao.saveWatchList(watchList);

		user.setSelectedWatchList(watchList.getId());
		user.setWatchList(watchList);

		watchListDao.saveSelectedWatchList(user.getId(), watchList.getId());

		return new IntValuePair(watchList.getId(), watchList.getName());
	}

	public void deleteWatchList(int watchListId) {
		User user = Common.getUser();

		WatchListDao watchListDao = new WatchListDao();
		WatchList watchList = user.getWatchList();
		if (watchList == null || watchListId != watchList.getId())
			watchList = watchListDao.getWatchList(watchListId);

		if (watchList == null)
			// Only one watch list left. Leave it.
			return;

		// Allow the delete.
		if (watchList.getUserAccess(user) == ShareUser.ACCESS_OWNER
				|| user.isAdmin()) {
			watchListDao.deleteWatchList(watchListId);
		} else
			watchListDao.removeUserFromWatchList(watchListId, user.getId());
	}

	public Map<String, Object> setSelectedWatchList(int watchListId) {
		User user = Common.getUser();

		WatchListDao watchListDao = new WatchListDao();
		WatchList watchList = watchListDao.getWatchList(watchListId);

		if(watchList == null)
			return Collections.emptyMap();

//		if (!user.isAdmin())
//			Permissions.ensureWatchListPermission(user, watchList);
		prepareWatchList(watchList, user);

		watchListDao.saveSelectedWatchList(user.getId(), watchList.getId());
		user.setSelectedWatchList(watchListId);

		Map<String, Object> data = getWatchListData(user, watchList);
		// Set the watchlist in the user object after getting the data since it
		// may take a while, and the long poll
		// updates will all be missed in the meantime.
		user.setWatchList(watchList);

		return data;
	}

	public WatchListState addToWatchList(int pointId) {
		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();
		User user = Common.getUser();
		DataPointVO point = new DataPointDao().getDataPoint(pointId);
		if (point == null)
			return null;
		WatchList watchList = user.getWatchList();

		// Check permissions.
		Permissions.ensureDataPointReadPermission(user, point);
		Permissions.ensureWatchListEditPermission(user, watchList);

		// Add it to the watch list.
		watchList.getPointList().add(point);
		new WatchListDao().saveWatchList(watchList);
		updateSetPermission(point, watchList.getUserAccess(user), user.isAdmin());

		// Return the watch list state for it.
		return createWatchListState(request, point,
				Common.ctx.getRuntimeManager(), new HashMap<String, Object>(),
				user);
	}

	public void removeFromWatchList(int pointId) {
		// Remove the point from the user's list.
		User user = Common.getUser();
		WatchList watchList = user.getWatchList();
		Permissions.ensureWatchListEditPermission(user, watchList);
		for (DataPointVO point : watchList.getPointList()) {
			if (point.getId() == pointId) {
				watchList.getPointList().remove(point);
				break;
			}
		}
		new WatchListDao().saveWatchList(watchList);
	}

	public void moveUp(int pointId) {
		User user = Common.getUser();
		WatchList watchList = user.getWatchList();
		Permissions.ensureWatchListEditPermission(user, watchList);
		List<DataPointVO> points = watchList.getPointList();

		DataPointVO point;
		for (int i = 0; i < points.size(); i++) {
			point = points.get(i);
			if (point.getId() == pointId) {
				points.set(i, points.get(i - 1));
				points.set(i - 1, point);
				break;
			}
		}

		new WatchListDao().saveWatchList(watchList);
	}

	public void moveDown(int pointId) {
		User user = Common.getUser();
		WatchList watchList = user.getWatchList();
		Permissions.ensureWatchListEditPermission(user, watchList);
		List<DataPointVO> points = watchList.getPointList();

		DataPointVO point;
		for (int i = 0; i < points.size(); i++) {
			point = points.get(i);
			if (point.getId() == pointId) {
				points.set(i, points.get(i + 1));
				points.set(i + 1, point);
				break;
			}
		}

		new WatchListDao().saveWatchList(watchList);
	}

	/**
	 * Convenience method for creating a populated view state.
	 */
	private WatchListState createWatchListState(HttpServletRequest request,
			DataPointVO pointVO, RuntimeManager rtm, Map<String, Object> model,
			User user) {
		// Get the data point status from the data image.
		DataPointRT point = rtm.getDataPoint(pointVO.getId());

		WatchListState state = new WatchListState();
		state.setId(Integer.toString(pointVO.getId()));

		PointValueTime pointValue = prepareBasePointState(
				Integer.toString(pointVO.getId()), state, pointVO, point, model);
		setEvents(pointVO, user, model);
		if (pointValue != null && pointValue.getValue() instanceof ImageValue) {
			// Text renderers don't help here. Create a thumbnail.
			setImageText(request, state, pointVO, model, pointValue);
		} else
			setPrettyText(state, pointVO, model, pointValue);

		if (pointVO.isSettable())
			setChange(pointVO, state, point, request, model, user);

		if (state.getValue() != null)
			setChart(pointVO, state, request, model);
		setMessages(state, request, "watchListMessages", model);

		return state;
	}

	private void setImageText(HttpServletRequest request, WatchListState state,
			DataPointVO pointVO, Map<String, Object> model,
			PointValueTime pointValue) {
		if (!ObjectUtils.isEqual(pointVO.lastValue(), pointValue)) {
			state.setValue(generateContent(request, "imageValueThumbnail.jsp",
					model));
			if (pointValue != null)
				state.setTime(Functions.getTime(pointValue));
			pointVO.updateLastValue(pointValue);
		}
	}

	/**
	 * Method for creating image charts of the points on the watch list.
	 */
	public String getImageChartData(int[] pointIds, int fromYear,
			int fromMonth, int fromDay, int fromHour, int fromMinute,
			int fromSecond, boolean fromNone, int toYear, int toMonth,
			int toDay, int toHour, int toMinute, int toSecond, boolean toNone,
			int width, int height) {
		DateTime from = createDateTime(fromYear, fromMonth, fromDay, fromHour,
				fromMinute, fromSecond, fromNone);
		DateTime to = createDateTime(toYear, toMonth, toDay, toHour, toMinute,
				toSecond, toNone);

		StringBuilder htmlData = new StringBuilder();
		long now = System.currentTimeMillis();
		htmlData.append("<img src=\"chart/");
		htmlData.append("ft_");
		htmlData.append(now);
		htmlData.append('_');

		if(toNone && fromNone) {
			htmlData.append('0');
			htmlData.append('_');
			htmlData.append(now);
	    }else if(toNone) {             // latest - works
			htmlData.append(from.getMillis());
			htmlData.append('_');
			htmlData.append(now);
		} else if(fromNone) {
			htmlData.append('0');
			htmlData.append('_');
			htmlData.append(to.getMillis());
		} else {
			htmlData.append(from.getMillis());
			htmlData.append('_');
			htmlData.append(to.getMillis());
		}

		boolean pointsFound = false;
		// Add the list of points that are numeric.
		List<DataPointVO> watchList = Common.getUser().getWatchList()
				.getPointList();
		for (DataPointVO dp : watchList) {
			int dtid = dp.getPointLocator().getDataTypeId();
			if ((dtid == DataTypes.NUMERIC || dtid == DataTypes.BINARY || dtid == DataTypes.MULTISTATE)
					&& ArrayUtils.contains(pointIds, dp.getId())) {
				pointsFound = true;
				htmlData.append('_');
				htmlData.append(dp.getId());
			}
		}

		if (!pointsFound)
			// There are no chartable points, so abort the image creation.
			return getMessage("watchlist.noChartables");

		htmlData.append(".png?w=");
		htmlData.append(width);
		htmlData.append("&h=");
		htmlData.append(height);
		htmlData.append("\" alt=\"" + getMessage("common.imageChart") + "\"/>");

		return htmlData.toString();
	}

	private Map<String, Object> getWatchListData(User user, WatchList watchList) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (watchList == null)
			return data;

		List<DataPointVO> points = watchList.getPointList();
		List<Integer> pointIds = new ArrayList<Integer>(points.size());
		for (DataPointVO point : points) {
			if (Permissions.hasDataPointReadPermission(user, point))
				pointIds.add(point.getId());
		}

		data.put("points", pointIds);
		data.put("users", watchList.getWatchListUsers());
		data.put("access", watchList.getUserAccess(user));

		return data;
	}

	private void prepareWatchList(WatchList watchList, User user) {
		int access = ShareUser.ACCESS_NONE;
		if (user.isAdmin()) {
			access = ShareUser.ACCESS_OWNER;
		}

		if(watchList != null) {
			access = watchList.getUserAccess(user);
			User owner = new UserDao().getUser(watchList.getUserId());
			for (DataPointVO point : watchList.getPointList())
				updateSetPermission(point, access, user.isAdmin());
		}
	}

	private void updateSetPermission(DataPointVO point, int access, boolean admin) {
		// Point isn't settable
		if (!point.getPointLocator().isSettable())
			return;

		// Read-only access
		if (access != ShareUser.ACCESS_OWNER && access != ShareUser.ACCESS_SET && !admin)
			return;

		// All good.
		point.setSettable(true);
	}

	//
	// Share users
	//
	@MethodFilter
	public List<ShareUser> addUpdateSharedUser(int userId, int accessType) {
		WatchList watchList = Common.getUser().getWatchList();
		boolean found = false;
		for (ShareUser su : watchList.getWatchListUsers()) {
			if (su.getUserId() == userId) {
				found = true;
				su.setAccessType(accessType);
				break;
			}
		}

		if (!found) {
			ShareUser su = new ShareUser();
			su.setUserId(userId);
			su.setAccessType(accessType);
			watchList.getWatchListUsers().add(su);
		}

		new WatchListDao().saveWatchList(watchList);

		return watchList.getWatchListUsers();
	}

	@MethodFilter
	public List<ShareUser> removeSharedUser(int userId) {
		WatchList watchList = Common.getUser().getWatchList();

		for (ShareUser su : watchList.getWatchListUsers()) {
			if (su.getUserId() == userId) {
				watchList.getWatchListUsers().remove(su);
				break;
			}
		}

		new WatchListDao().saveWatchList(watchList);

		return watchList.getWatchListUsers();
	}

	@MethodFilter
	public void getChartData(int[] pointIds, int fromYear, int fromMonth,
			int fromDay, int fromHour, int fromMinute, int fromSecond,
			boolean fromNone, int toYear, int toMonth, int toDay, int toHour,
			int toMinute, int toSecond, boolean toNone) {
		DateTime from = createDateTime(fromYear, fromMonth, fromDay, fromHour,
				fromMinute, fromSecond, fromNone);
		DateTime to = createDateTime(toYear, toMonth, toDay, toHour, toMinute,
				toSecond, toNone);
		DataExportDefinition def = new DataExportDefinition(pointIds, from, to);
		Common.getUser().setDataExportDefinition(def);
	}
}
