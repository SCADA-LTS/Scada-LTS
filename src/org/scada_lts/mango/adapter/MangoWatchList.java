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
package org.scada_lts.mango.adapter;

import java.util.List;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

/** 
 * Base on the WatchListDao
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public interface MangoWatchList {

	String generateUniqueXid();
	
	boolean isXidUnique(String xid, int excludeId);

	List<WatchList> getWatchLists(int userId, int userProfile);

	List<WatchList> getWatchLists();

	WatchList getWatchList(int watchListId);

	void populateWatchlistData(List<WatchList> watchLists);

	void populateWatchlistData(WatchList watchList);

	WatchList getWatchList(String xid);

	void saveSelectedWatchList(int userId, int watchListId);

	WatchList createNewWatchList(WatchList watchList, int userId);

	void saveWatchList(WatchList watchList);

	List<WatchList> getWatchListsWithAccess(User user);

	List<ScadaObjectIdentifier> getWatchListIdentifiersWithAccess(User user);
}