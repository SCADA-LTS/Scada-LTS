/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;
import org.scada_lts.exception.PasswordMismatchException;

import java.util.IllformedLocaleException;
import java.util.List;

/**
 * UserService adapter
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public interface MangoUser {

	User getUser(int id);

	User getUser(String username);

	List<User> getUsers();

	List<User> getUsersWithProfile();

	List<User> getActiveUsers();

	void populateUserPermissions(User user);

	void saveUser(final User user);

	void insertUser(User user);

	@Deprecated
	void updateHideMenu(User user);

	@Deprecated
	void updateScadaTheme(User user);

	void updateUser(User user);

	void deleteUser(final int userId);

	void recordLogin(int userId);

	void saveHomeUrl(int userId, String homeUrl);

	void insertUserComment(int typeId, int referenceId, UserComment comment);

	boolean isUsernameUnique(String username);

	void updateUserProfile(User user);

	void updateUserPassword(int userId, String newPassword);

	void updateUserPassword(int userId, String newPassword, String oldPassword) throws PasswordMismatchException;

	void updateUserLang(int userId, String lang) throws IllformedLocaleException;
}
