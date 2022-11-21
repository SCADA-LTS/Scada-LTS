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

import java.sql.SQLException;
import java.util.List;

import org.scada_lts.dao.DAO;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.mango.adapter.MangoWatchList;
import org.scada_lts.permissions.service.GetShareUsers;
import org.scada_lts.utils.ApplicationBeans;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.WatchList;

/** 
 * Base on the WatchListDao
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
@Service
public class WatchListService implements MangoWatchList {

	private WatchListDAO watchListDAO;
	private GetShareUsers<WatchList> getShareUsers;
	private UsersProfileService usersProfileService;

	public WatchListService() {
		this.watchListDAO = ApplicationBeans.getBean("watchListDAO", WatchListDAO.class);
		this.getShareUsers = ApplicationBeans.getWatchListGetShareUsersBean();
		this.usersProfileService = ApplicationBeans.getUsersProfileService();
	}

	public WatchListService(WatchListDAO watchListDAO, GetShareUsers<WatchList> getShareUsers, UsersProfileService usersProfileService) {
		this.watchListDAO = watchListDAO;
		this.getShareUsers = getShareUsers;
		this.usersProfileService = usersProfileService;
	}

	@Override
	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(WatchList.XID_PREFIX, "watchLists");
	}

	public boolean isXidUnique(String xid, int excludeId) {
		return DAO.getInstance().isXidUnique(xid, excludeId,"watchLists");
	}

	@Override
	public List<WatchList> getWatchLists(final int userId, int userProfile) {
		return watchListDAO.filtered(WatchListDAO.WATCH_LIST_FILTER_BASE_ON_USER_ID_USER_PROFILE_ORDERY_BY_NAME, new Object[]{userId, userProfile, userId}, WatchListDAO.NO_LIMIT);
	}

	@Override
	public List<WatchList> getWatchLists() {
		return watchListDAO.findAll();
	}

	@Override
	public WatchList getWatchList(int watchListId) {
		return watchListDAO.findById(new Object[] {watchListId});
	}

	@Override
	public void populateWatchlistData(List<WatchList> watchLists) {
		for (WatchList watchList : watchLists)
			populateWatchlistData(watchList);
	}

	@Override
	public void populateWatchlistData(WatchList watchList) {
		if (watchList == null)
			return;

		// Get the points for each of the watch lists.
		List<Integer> pointIds = watchListDAO.getPointsWatchList(watchList.getId());

		List<DataPointVO> points = watchList.getPointList();
		DataPointDao dataPointDao = new DataPointDao();
		for (Integer pointId : pointIds)
			points.add(dataPointDao.getDataPoint(pointId));

		setWatchListUsers(watchList);
	}

	private void setWatchListUsers(WatchList watchList) {
		List<ShareUser> watchListUsers = getShareUsers.getShareUsersWithProfile(watchList);
		watchList.setWatchListUsers(watchListUsers);
	}

	@Override
	public WatchList getWatchList(String xid) {
		return watchListDAO.findByXId(xid);
	}

	@Override
	public void saveSelectedWatchList(int userId, int watchListId) {
		watchListDAO.updateUsers(userId, watchListId);
	}

	@Override
	public WatchList createNewWatchList(WatchList watchList, int userId) {
		watchList.setUserId(userId);
		String guxid = generateUniqueXid();
		watchList.setXid(guxid);

		int id = (Integer) watchListDAO.create(watchList)[0];
		watchList.setId(id);
		return watchList;
	}

	@Override
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void saveWatchList(final WatchList watchList) {

		if (watchList.getId() == Common.NEW_ID) {
			int id = (int) watchListDAO.create(watchList)[0];
			watchList.setId(id);
		} else {
			watchListDAO.update(watchList);
		}

		watchListDAO.deleteWatchListPoints(watchList.getId());

		watchListDAO.addPointsForWatchList(watchList);
        //sharing an object doesn't work
		//saveWatchListUsers(watchList);
	}

	void saveWatchListUsers(final WatchList watchList) {
		// Delete anything that is currently there.
		watchListDAO.deleteWatchListUsers(watchList.getId());

        // Add in all of the entries.
		watchListDAO.addWatchListUsers(watchList);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void deleteWatchList(int watchListId) {
		watchListDAO.deleteWatchListPoints(watchListId);
		watchListDAO.deleteWatchList(watchListId);
		usersProfileService.updateWatchlistPermissions();
		//TODO check why don't delete watch list for users
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void removeUserFromWatchList(int watchListId, int userId) {
		watchListDAO.deleteUserFromWatchList(watchListId, userId);
		usersProfileService.updateWatchlistPermissions();
	}

}
