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
package com.serotonin.mango.db.dao;

import java.util.List;

import org.scada_lts.mango.service.WatchListService;

import com.serotonin.mango.vo.WatchList;

/**
 * @author Matthew Lohbihler
 */
public class WatchListDao  {
	
	private WatchListService watchListService = new WatchListService();
	
	public String generateUniqueXid() {
		return watchListService.generateUniqueXid();
	}

	public boolean isXidUnique(String xid, int excludeId){
		return watchListService.isXidUnique(xid, excludeId);
	};

	/**
	 * Note: this method only returns basic watchlist information. No data
	 * points or share users.
	 * 
	 * @param userProfile
	 */
	public List<WatchList> getWatchLists(final int userId, int userProfile) {
		return watchListService.getWatchLists(userId, userProfile);
	}

	/**
	 * Note: this method only returns basic watchlist information. No data
	 * points or share users.
	 */
	public List<WatchList> getWatchLists() {
		return watchListService.getWatchLists();
	}

	public WatchList getWatchList(int watchListId) {
		WatchList watchList = watchListService.getWatchList(watchListId);
		populateWatchlistData(watchList);
		return watchList;
	}

	public void populateWatchlistData(List<WatchList> watchLists) {
		watchListService.populateWatchlistData(watchLists);
	}

	public void populateWatchlistData(WatchList watchList) {
		watchListService.populateWatchlistData(watchList);
	}

	/**
	 * Note: this method only returns basic watchlist information. No data
	 * points or share users.
	 */
	public WatchList getWatchList(String xid) {
		return watchListService.getWatchList(xid);
	}

	public void saveSelectedWatchList(int userId, int watchListId) {
		watchListService.saveSelectedWatchList(userId, watchListId);
	}

	public WatchList createNewWatchList(WatchList watchList, int userId) {
		return watchListService.createNewWatchList(watchList, userId);
	}

	public void saveWatchList(final WatchList watchList) {
		watchListService.saveWatchList(watchList);
	}

	public void deleteWatchList(final int watchListId) {
		watchListService.deleteWatchList(watchListId);
	}

	public void removeUserFromWatchList(int watchListId, int userId) {
		watchListService.removeUserFromWatchList(watchListId, userId);
	}
	
}
