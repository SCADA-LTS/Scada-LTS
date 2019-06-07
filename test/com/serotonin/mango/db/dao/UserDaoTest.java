package com.serotonin.mango.db.dao;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import br.org.scadabr.db.AbstractMySQLDependentTest;
import br.org.scadabr.db.scenarios.DatalessDatabaseScenario;
import br.org.scadabr.db.scenarios.ScenarioWithAdministrador;

import com.serotonin.mango.vo.User;

public class UserDaoTest extends AbstractMySQLDependentTest {

	private static final int FIRST = 0;
	private static final int SECOND = 1;
	private static final int THIRD = 2;

	@Test
	public void saveUserShouldSaveUsernamePasswordEmailPhoneAdminDisabledHomeUrlReceiveAlarmEmailsAndReceiveOwnAuditEvents() {
		useScenario(new DatalessDatabaseScenario());
		UserDao userDao = new UserDao();

		User user = new User();
		user.setUsername("anUser");
		user.setPassword("password");
		user.setEmail("An email");
		user.setPhone("phone");
		user.setAdmin(true);
		user.setDisabled(false);
		user.setHomeUrl("url");
		user.setReceiveAlarmEmails(1);
		user.setReceiveOwnAuditEvents(true);
		userDao.saveUser(user); // username, password, email, phone, admin,
								// disabled, homeUrl, receiveAlarmEmails,
								// receiveOwnAuditEvents

		User retrievedUser = userDao.getUser("anUser");
		assertEquals("anUser", retrievedUser.getUsername());
		assertEquals("password", retrievedUser.getPassword());
		assertEquals("An email", retrievedUser.getEmail());
		assertEquals("phone", retrievedUser.getPhone());
		assertTrue(retrievedUser.isAdmin());
		assertFalse(retrievedUser.isDisabled());
		assertEquals("url", retrievedUser.getHomeUrl());
		assertEquals(1, retrievedUser.getReceiveAlarmEmails());
		assertTrue(retrievedUser.isReceiveOwnAuditEvents());
	}

	@Test
	public void saveUserShouldUpdateId() {
		useScenario(new DatalessDatabaseScenario());
		UserDao userDao = new UserDao();

		User user = new User();
		user.setUsername("anUser");
		user.setPassword("password");
		user.setEmail("An email");
		user.setPhone("phone");
		user.setAdmin(true);
		user.setDisabled(false);
		user.setHomeUrl("url");
		user.setReceiveAlarmEmails(1);
		user.setReceiveOwnAuditEvents(true);

		userDao.saveUser(user); // username, password, email, phone, admin,
								// disabled, homeUrl, receiveAlarmEmails,
								// receiveOwnAuditEvents

		User retrievedUser = userDao.getUser("anUser");
		assertEquals(retrievedUser.getId(), user.getId());
	}

	@Test
	public void getUserByIdShouldReturnAnUserWithThatId() {
		useScenario(new ScenarioWithAdministrador());
		UserDao userDao = new UserDao();

		User expectedUser = userDao.getUser("admin");
		User received = userDao.getUser(expectedUser.getId());
		assertNotNull(received);
		assertEquals("admin", received.getUsername());
	}

	@Test
	public void getUsersShouldReturnAnEmptyListIfThereIsNoUsers() {
		useScenario(new DatalessDatabaseScenario());
		UserDao userDao = new UserDao();
		assertTrue(userDao.getUsers().isEmpty());
	}

	@Test
	public void getUsersShouldReturnTheAdminUserWhenHeIsTheOnlyUser() {
		useScenario(new ScenarioWithAdministrador());
		UserDao userDao = new UserDao();
		final List<User> users = userDao.getUsers();
		assertEquals(1, users.size());
		assertEquals("admin", users.get(FIRST).getUsername());
	}

	@Test
	public void getUsersShouldReturnAllUsersOrderedByUsername() {
		useScenario(new ScenarioWithAdministrador());
		UserDao userDao = new UserDao();
		User joão = createUserWithUserName("joão");
		User daniel = createUserWithUserName("daniel");
		userDao.saveUser(joão);
		userDao.saveUser(daniel);

		final List<User> users = userDao.getUsers();
		assertEquals(3, users.size());
		assertEquals("admin", users.get(FIRST).getUsername());
		assertEquals("daniel", users.get(SECOND).getUsername());
		assertEquals("joão", users.get(THIRD).getUsername());
	}

	@Test
	public void getActiveUsersShouldReturnEmptyListWhenThereIsNoUser() {
		useScenario(new DatalessDatabaseScenario());
		UserDao userDao = new UserDao();
		final List<User> users = userDao.getActiveUsers();
		assertTrue(users.isEmpty());
	}

	@Test
	public void getActiveUsersShouldReturnEmptyListWhenThereIsOnlyInactiveUsers() {
		useScenario(new DatalessDatabaseScenario());
		UserDao userDao = new UserDao();
		userDao.saveUser(createInactiveUserWithUserName("joão"));
		final List<User> users = userDao.getActiveUsers();
		assertTrue(users.isEmpty());
	}

	@Test
	public void getActiveUsersShouldReturnTheAdminUserWhenHeIsTheOnlyActiveUser() {
		useScenario(new ScenarioWithAdministrador());
		UserDao userDao = new UserDao();
		final List<User> users = userDao.getActiveUsers();
		assertEquals(1, users.size());
		assertEquals("admin", users.get(FIRST).getUsername());
	}

	@Test
	public void getActiveUsersShouldReturnAllActiveUsers() {
		useScenario(new ScenarioWithAdministrador());
		UserDao userDao = new UserDao();
		User joão = createUserWithUserName("joão");
		User daniel = createUserWithUserName("daniel");
		User pedro = createInactiveUserWithUserName("pedro");
		userDao.saveUser(joão);
		userDao.saveUser(daniel);
		userDao.saveUser(pedro);

		final List<User> users = userDao.getActiveUsers();

		Set<String> retrievedActiveUsernames = new HashSet<String>();
		for (User u : users) {
			retrievedActiveUsernames.add(u.getUsername());
		}

		Set<String> expected = new HashSet<String>();
		expected.add("admin");
		expected.add("joão");
		expected.add("daniel");
		assertEquals(expected, retrievedActiveUsernames);
	}

	@Test
	public void deleteUser() {
		useScenario(new ScenarioWithAdministrador());
		UserDao userDao = new UserDao();
		User joão = createUserWithUserName("joão");
		userDao.saveUser(joão);

		userDao.deleteUser(joão.getId());
		assertNull("user does not exists", userDao.getUser(joão.getId()));
	}

	@Test
	public void saveHomeUrl() {
		useScenario(new ScenarioWithAdministrador());
		UserDao userDao = new UserDao();
		User joão = createUserWithUserName("joão");
		userDao.saveUser(joão);

		userDao.saveHomeUrl(joão.getId(), "joão.example.com");

		assertEquals("joão.example.com", userDao.getUser(joão.getId())
				.getHomeUrl());
	}

	@Test
	public void recordLogin() {
		useScenario(new ScenarioWithAdministrador());
		UserDao userDao = new UserDao();
		User admin = userDao.getUser("admin");

		long before = System.currentTimeMillis();
		userDao.recordLogin(admin.getId());
		long after = System.currentTimeMillis();

		long recordedLoginTime = userDao.getUser("admin").getLastLogin();
		assertTrue(before <= recordedLoginTime);
		assertTrue(recordedLoginTime <= after);
	}

	private User createUserWithUserName(String username) {
		User user = new User();
		user.setUsername(username);
		user.setEmail(username);

		user.setPassword("password");
		user.setPhone("phone");
		user.setReceiveAlarmEmails(1);
		user.setReceiveOwnAuditEvents(true);

		return user;
	}

	private User createInactiveUserWithUserName(String username) {
		User user = createUserWithUserName(username);
		user.setDisabled(true);
		return user;
	}

}
