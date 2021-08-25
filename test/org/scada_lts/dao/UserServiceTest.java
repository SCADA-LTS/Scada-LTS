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

import org.scada_lts.mango.service.UserService;

import com.serotonin.mango.vo.User;

/**
 * UserService test
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class UserServiceTest extends TestDAO {

	private static final String USERNAME = "nUsername";
	private static final String PASSWORD = "nPassword";
	private static final String EMAIL = "nEmail";
	private static final String PHONE = "45752312";
	private static final boolean ADMIN = false;
	private static final boolean DISABLED = false;
	private static final String HOME_URL = "nUrl";
	private static final int ALARM_EMAIL = 1;
	private static final boolean AUDIT_EVENT = true;

	private static final String SECOND_USERNAME = "sUsername";
	private static final String SECOND_PASSWORD = "sPassword";
	private static final String SECOND_EMAIL = "sEmail";
	private static final String SECOND_PHONE = "123454449";
	private static final boolean SECOND_ADMIN = true;
	private static final boolean SECOND_DISABLED = true;
	private static final String SECOND_HOME_URL = "sUrl";
	private static final int SECOND_ALARM_EMAIL = 3;
	private static final boolean SECOND_AUDIT_EVENT = true;

	private UserService userService = new UserService();
	private User user;
	private User secondUser;

	/*@Before
	public void createAndInsertUsers() {
		DAO.getInstance().getJdbcTemp().update("INSERT INTO dataSources (xid, name, dataSourceType, data) VALUES ('fXid', 'fName', 1, 2)");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO dataSources (xid, name, dataSourceType, data) VALUES ('sXid', 'sName', 1, 2)");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO dataSources (xid, name, dataSourceType, data) VALUES ('nXid', 'nName', 1, 2)");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO dataPoints (xid, dataSourceId, data) VALUES ('T_01',1, 1)");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO dataPoints (xid, dataSourceId, data) VALUES ('T_02',1, 2)");

		user = new User();
		user.setUsername(USERNAME);
		user.setPassword(PASSWORD);
		user.setEmail(EMAIL);
		user.setPhone(PHONE);
		user.setAdmin(ADMIN);
		user.setDisabled(DISABLED);
		user.setHomeUrl(HOME_URL);
		user.setReceiveAlarmEmails(ALARM_EMAIL);
		user.setReceiveOwnAuditEvents(AUDIT_EVENT);

		secondUser = new User();
		secondUser.setUsername(SECOND_USERNAME);
		secondUser.setPassword(SECOND_PASSWORD);
		secondUser.setEmail(SECOND_EMAIL);
		secondUser.setPhone(SECOND_PHONE);
		secondUser.setAdmin(SECOND_ADMIN);
		secondUser.setDisabled(SECOND_DISABLED);
		secondUser.setHomeUrl(SECOND_HOME_URL);
		secondUser.setReceiveAlarmEmails(SECOND_ALARM_EMAIL);
		secondUser.setReceiveOwnAuditEvents(SECOND_AUDIT_EVENT);

		DataPointAccess dataPointPermission = new DataPointAccess();
		dataPointPermission.setDataPointId(1);
		DataPointAccess secondDataPointPermission = new DataPointAccess();
		secondDataPointPermission.setDataPointId(2);
		List<DataPointAccess> dataPointAccesses = new ArrayList<>();
		dataPointAccesses.add(dataPointPermission);

		List<Integer> userIds = IntStream.range(0, 1).map(i -> i + 1).boxed().collect(Collectors.toList());
		user.setDataSourcePermissions(userIds);
		user.setDataPointPermissions(dataPointAccesses);
		secondUser.setDataSourcePermissions(userIds);
		secondUser.setDataPointPermissions(dataPointAccesses);

		userService.insertUser(user);

		dataPointAccesses.add(secondDataPointPermission);
		userIds = IntStream.range(0, 3).map(i -> i + 1).boxed().collect(Collectors.toList());
		user.setDataSourcePermissions(userIds);
	}

	@Test
	public void populateUserPermissionsTest() {
		assertTrue(user.getDataSourcePermissions().size() == 3);
		assertTrue(user.getDataPointPermissions().size() == 2);

		userService.populateUserPermissions(user);
		User userFromDb = userService.getUser(user.getUsername());

		assertTrue(userFromDb.getDataSourcePermissions().size() == 1);
		assertTrue(userFromDb.getDataPointPermissions().size() == 1);
	}

	@Test
	public void saveUserTest() {
		secondUser.setId(Common.NEW_ID);

		//Insert
		List<User> users = userService.getUsers();
		assertTrue(users.size() == 2);
		userService.saveUser(secondUser);
		users = userService.getUsers();
		assertTrue(users.size() == 3);

		//Update
		user.setId(secondUser.getId());
		userService.saveUser(user);
		assertTrue(users.size() == 3);
	}

	@Test
	public void deleteUserTest() {
		List<User> users = userService.getUsers();
		assertTrue(users.size() == 2);
		userService.deleteUser(user.getId());
		users = userService.getUsers();
		assertTrue(users.size() == 1);
	}*/
}
