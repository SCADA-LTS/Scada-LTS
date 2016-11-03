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
package org.scada_lts.dao;

/**
 * UserDAO test
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class UserDaoTest extends TestDAO {

	private static final String USERNAME = "fUsername";
	private static final String PASSWORD = "fPassword";
	private static final String EMAIL = "fEmail";
	private static final String PHONE = "123456789";
	private static final boolean ADMIN = false;
	private static final boolean DISABLED = false;
	private static final String HOME_URL = "fUrl";
	private static final int ALARM_EMAIL = 2;
	private static final boolean AUDIT_EVENT = false;

	private static final String SECOND_USERNAME = "sUsername";
	private static final String SECOND_PASSWORD = "sPassword";
	private static final String SECOND_EMAIL = "sEmail";
	private static final String SECOND_PHONE = "123454449";
	private static final boolean SECOND_ADMIN = true;
	private static final boolean SECOND_DISABLED = true;
	private static final String SECOND_HOME_URL = "sUrl";
	private static final int SECOND_ALARM_EMAIL = 1;
	private static final boolean SECOND_AUDIT_EVENT = true;

	private static final String UPDATE_USERNAME = "uUsername";
	private static final String UPDATE_PASSWORD = "uPassword";
	private static final String UPDATE_EMAIL = "uEmail";
	private static final String UPDATE_PHONE = "24656789";
	private static final boolean UPDATE_ADMIN = true;
	private static final boolean UPDATE_DISABLED = false;
	private static final String UPDATE_HOME_URL = "uUrl";
	private static final int UPDATE_ALARM_EMAIL = 1;
	private static final boolean UPDATE_AUDIT_EVENT = true;

	private static final int LIST_SIZE = 3;
	private static final int ACTIVE_USERS = 2;

	/*@Test
	public void test() {
		UserDAO userDAO = new UserDAO();

		User user = new User();
		user.setUsername(USERNAME);
		user.setPassword(PASSWORD);
		user.setEmail(EMAIL);
		user.setPhone(PHONE);
		user.setAdmin(ADMIN);
		user.setDisabled(DISABLED);
		user.setHomeUrl(HOME_URL);
		user.setReceiveAlarmEmails(ALARM_EMAIL);
		user.setReceiveOwnAuditEvents(AUDIT_EVENT);

		User secondUser = new User();
		secondUser.setUsername(SECOND_USERNAME);
		secondUser.setPassword(SECOND_PASSWORD);
		secondUser.setEmail(SECOND_EMAIL);
		secondUser.setPhone(SECOND_PHONE);
		secondUser.setAdmin(SECOND_ADMIN);
		secondUser.setDisabled(SECOND_DISABLED);
		secondUser.setHomeUrl(SECOND_HOME_URL);
		secondUser.setReceiveAlarmEmails(SECOND_ALARM_EMAIL);
		secondUser.setReceiveOwnAuditEvents(SECOND_AUDIT_EVENT);

		//Insert objects
		int firstId = userDAO.insert(user);
		int secondId = userDAO.insert(secondUser);
		user.setId(firstId);
		secondUser.setId(secondId);

		//Select single object
		User userSelectId = userDAO.getUser(firstId);
		assertTrue(user.getUsername().equals(userSelectId.getUsername()));
		assertTrue(user.getPassword().equals(userSelectId.getPassword()));
		assertTrue(user.getEmail().equals(userSelectId.getEmail()));
		assertTrue(user.getPhone().equals(userSelectId.getPhone()));
		assertTrue(user.isAdmin() == userSelectId.isAdmin());
		assertTrue(user.isDisabled() == userSelectId.isDisabled());
		assertTrue(user.getHomeUrl().equals(userSelectId.getHomeUrl()));
		assertTrue(user.getReceiveAlarmEmails() == userSelectId.getReceiveAlarmEmails());
		assertTrue(user.isReceiveOwnAuditEvents() == userSelectId.isReceiveOwnAuditEvents());

		User userSelectUsername = userDAO.getUser(secondId);
		assertTrue(secondUser.getUsername().equals(userSelectUsername.getUsername()));
		assertTrue(secondUser.getPassword().equals(userSelectUsername.getPassword()));
		assertTrue(secondUser.getEmail().equals(userSelectUsername.getEmail()));
		assertTrue(secondUser.getPhone().equals(userSelectUsername.getPhone()));
		assertTrue(secondUser.isAdmin() == userSelectUsername.isAdmin());
		assertTrue(secondUser.isDisabled() == userSelectUsername.isDisabled());
		assertTrue(secondUser.getHomeUrl().equals(userSelectUsername.getHomeUrl()));
		assertTrue(secondUser.getReceiveAlarmEmails() == userSelectUsername.getReceiveAlarmEmails());
		assertTrue(secondUser.isReceiveOwnAuditEvents() == userSelectUsername.isReceiveOwnAuditEvents());

		//Select all objects
		List<User> users = userDAO.getUsers();
		//Check size
		assertTrue(users.size() == LIST_SIZE);
		//Check IDs
		assertTrue(users.get(1).getId() == user.getId());
		assertTrue(users.get(2).getId() == secondUser.getId());

		//Select only active users
		List<User> activeUsers = userDAO.getActiveUsers();
		assertTrue(activeUsers.size() == ACTIVE_USERS);

		//Select all IDs
		List<Integer> userIdsFromDb = userDAO.getAll();
		List<Integer> userIds = IntStream.range(0, 3).map(i -> i + 1).boxed().collect(Collectors.toList());
		assertTrue(userIds.size() == userIdsFromDb.size());
		assertTrue(userIds.containsAll(userIdsFromDb));

		//Update
		User userUpdate = new User();
		userUpdate.setId(user.getId());
		userUpdate.setUsername(UPDATE_USERNAME);
		userUpdate.setPassword(UPDATE_PASSWORD);
		userUpdate.setEmail(UPDATE_EMAIL);
		userUpdate.setPhone(UPDATE_PHONE);
		userUpdate.setAdmin(UPDATE_ADMIN);
		userUpdate.setDisabled(UPDATE_DISABLED);
		userUpdate.setHomeUrl(UPDATE_HOME_URL);
		userUpdate.setReceiveAlarmEmails(UPDATE_ALARM_EMAIL);
		userUpdate.setReceiveOwnAuditEvents(UPDATE_AUDIT_EVENT);

		userDAO.update(userUpdate);
		User userUpdateSelect = userDAO.getUser(user.getId());
		assertTrue(userUpdateSelect.getUsername().equals(userUpdate.getUsername()));
		assertTrue(userUpdateSelect.getPassword().equals(userUpdate.getPassword()));
		assertTrue(userUpdateSelect.getEmail().equals(userUpdate.getEmail()));
		assertTrue(userUpdateSelect.getPhone().equals(userUpdate.getPhone()));
		assertTrue(userUpdateSelect.isAdmin() == userUpdate.isAdmin());
		assertTrue(userUpdateSelect.isDisabled() == userUpdate.isDisabled());
		assertTrue(userUpdateSelect.getHomeUrl().equals(userUpdate.getHomeUrl()));
		assertTrue(userUpdateSelect.getReceiveAlarmEmails() == userUpdate.getReceiveAlarmEmails());
		assertTrue(userUpdateSelect.isReceiveOwnAuditEvents() == userUpdate.isReceiveOwnAuditEvents());

		//Update HomeUrl
		userDAO.updateHomeUrl(userUpdateSelect.getId(), userUpdateSelect.getHomeUrl());
		User nextUser =  userDAO.getUser(user.getId());
		assertTrue(nextUser.getHomeUrl().equals(userUpdateSelect.getHomeUrl()));

		//Update login
		userDAO.updateLogin(user.getId());
		userUpdateSelect = userDAO.getUser(user.getId());
		assertTrue(userUpdateSelect.getLastLogin() > 0);

		//Delete
		userDAO.delete(firstId);
		assertTrue(userDAO.getUser(firstId) == null);
	}*/
}
