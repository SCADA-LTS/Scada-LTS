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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;

public class UserDao {

	private MangoUser userService = new UserService();

	private final Log LOG = LogFactory.getLog(UserDao.class);

	public User getUser(int id) {
		return userService.getUser(id);
	}

	public User getUser(String username) {
		return userService.getUser(username);
	}

	public List<User> getUsers() {
		return userService.getUsers();
	}

	public List<User> getActiveUsers() {
		return userService.getActiveUsers();
	}

	public void populateUserPermissions(User user) {
		userService.populateUserPermissions(user);
	}

	@Deprecated
	public void updateUserHideMenu(final User user) {
		userService.updateUser(user);
	}

	@Deprecated
	public void updateUserScadaTheme(final User user) {
		userService.updateUser(user);
	}


	public void saveUser(final User user) {
		userService.saveUser(user);
	}

	void insertUser(User user) {
		userService.insertUser(user);
	}

	void updateUser(User user) {
		userService.updateUser(user);
	}

	public void deleteUser(final int userId) {
		userService.deleteUser(userId);
	}

	public void recordLogin(int userId) {
		userService.recordLogin(userId);
	}

	public void saveHomeUrl(int userId, String homeUrl) {
		userService.saveHomeUrl(userId, homeUrl);
	}

	//
	//
	// User comments
	//

	public void insertUserComment(int typeId, int referenceId,
			UserComment comment) {
		userService.insertUserComment(typeId, referenceId, comment);
	}

	public List<User> getUsersWithProfile() {
		return userService.getUsersWithProfile();
	}
}
