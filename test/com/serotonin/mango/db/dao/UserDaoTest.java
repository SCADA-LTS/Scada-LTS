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
import org.scada_lts.mango.service.UserService;

public class UserDaoTest extends AbstractMySQLDependentTest {

	private static final int FIRST = 0;
	private static final int SECOND = 1;
	private static final int THIRD = 2;

	@Test
	public void saveUserShouldSaveUsernamePasswordEmailPhoneAdminDisabledHomeUrlReceiveAlarmEmailsAndReceiveOwnAuditEvents() {
		useScenario(new DatalessDatabaseScenario());
		UserService userService = new UserService();

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
		userService.saveUser(user); // username, password, email, phone, admin,
								// disabled, homeUrl, receiveAlarmEmails,
								// receiveOwnAuditEvents

		User retrievedUser = userService.getUser("anUser");
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
		UserService userService = new UserService();

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

		userService.saveUser(user); // username, password, email, phone, admin,
								// disabled, homeUrl, receiveAlarmEmails,
								// receiveOwnAuditEvents

		User retrievedUser = userService.getUser("anUser");
		assertEquals(retrievedUser.getId(), user.getId());
	}

	@Test
	public void getUserByIdShouldReturnAnUserWithThatId() {
		useScenario(new ScenarioWithAdministrador());
		UserService userService = new UserService();

		User expectedUser = userService.getUser("admin");
		User received = userService.getUser(expectedUser.getId());
		assertNotNull(received);
		assertEquals("admin", received.getUsername());
	}

	@Test
	public void getUsersShouldReturnAnEmptyListIfThereIsNoUsers() {
		useScenario(new DatalessDatabaseScenario());
		UserService userService = new UserService();
		assertTrue(userService.getUsers().isEmpty());
	}

	@Test
	public void getUsersShouldReturnTheAdminUserWhenHeIsTheOnlyUser() {
		useScenario(new ScenarioWithAdministrador());
		UserService userService = new UserService();
		final List<User> users = userService.getUsers();
		assertEquals(1, users.size());
		assertEquals("admin", users.get(FIRST).getUsername());
	}

	@Test
	public void getUsersShouldReturnAllUsersOrderedByUsername() {
		useScenario(new ScenarioWithAdministrador());
		UserService userService = new UserService();
		User joão = createUserWithUserName("joão");
		User daniel = createUserWithUserName("daniel");
		userService.saveUser(joão);
		userService.saveUser(daniel);

		final List<User> users = userService.getUsers();
		assertEquals(3, users.size());
		assertEquals("admin", users.get(FIRST).getUsername());
		assertEquals("daniel", users.get(SECOND).getUsername());
		assertEquals("joão", users.get(THIRD).getUsername());
	}

	@Test
	public void getActiveUsersShouldReturnEmptyListWhenThereIsNoUser() {
		useScenario(new DatalessDatabaseScenario());
		UserService userService = new UserService();
		final List<User> users = userService.getActiveUsers();
		assertTrue(users.isEmpty());
	}

	@Test
	public void getActiveUsersShouldReturnEmptyListWhenThereIsOnlyInactiveUsers() {
		useScenario(new DatalessDatabaseScenario());
		UserService userService = new UserService();
		userService.saveUser(createInactiveUserWithUserName("joão"));
		final List<User> users = userService.getActiveUsers();
		assertTrue(users.isEmpty());
	}

	@Test
	public void getActiveUsersShouldReturnTheAdminUserWhenHeIsTheOnlyActiveUser() {
		useScenario(new ScenarioWithAdministrador());
		UserService userService = new UserService();
		final List<User> users = userService.getActiveUsers();
		assertEquals(1, users.size());
		assertEquals("admin", users.get(FIRST).getUsername());
	}

	@Test
	public void getActiveUsersShouldReturnAllActiveUsers() {
		useScenario(new ScenarioWithAdministrador());
		UserService userService = new UserService();
		User joão = createUserWithUserName("joão");
		User daniel = createUserWithUserName("daniel");
		User pedro = createInactiveUserWithUserName("pedro");
		userService.saveUser(joão);
		userService.saveUser(daniel);
		userService.saveUser(pedro);

		final List<User> users = userService.getActiveUsers();

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
		UserService userService = new UserService();
		User joão = createUserWithUserName("joão");
		userService.saveUser(joão);

		userService.deleteUser(joão.getId());
		assertNull("user does not exists", userService.getUser(joão.getId()));
	}

	@Test
	public void saveHomeUrl() {
		useScenario(new ScenarioWithAdministrador());
		UserService userService = new UserService();
		User joão = createUserWithUserName("joão");
		userService.saveUser(joão);

		userService.saveHomeUrl(joão.getId(), "joão.example.com");

		assertEquals("joão.example.com", userService.getUser(joão.getId())
				.getHomeUrl());
	}

	@Test
	public void recordLogin() {
		useScenario(new ScenarioWithAdministrador());
		UserService userService = new UserService();
		User admin = userService.getUser("admin");

		long before = System.currentTimeMillis();
		userService.recordLogin(admin.getId());
		long after = System.currentTimeMillis();

		long recordedLoginTime = userService.getUser("admin").getLastLogin();
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
