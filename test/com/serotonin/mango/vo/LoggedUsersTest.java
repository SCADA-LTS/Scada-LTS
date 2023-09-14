package com.serotonin.mango.vo;

import br.org.scadabr.db.utils.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.login.ILoggedUsers;
import org.scada_lts.login.LoggedUsers;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.mock.web.MockHttpSession;
import utils.TestConcurrentUtils;

import javax.servlet.http.HttpSession;
import java.util.*;

import static com.serotonin.mango.Common.SESSION_USER;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationBeans.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class LoggedUsersTest {

    private final ILoggedUsers loggedUsers = new LoggedUsers();

    @Before
    public void config() {
        mockStatic(ApplicationBeans.class);
        when(ApplicationBeans.getLoggedUsersBean()).thenReturn(loggedUsers);
    }

    @Test
    public void when_addUser_for_one_user_then_logged_one_user() {
        //given:
        User user = TestUtils.newUser(123);
        Set<Integer> expected = Set.of(user.getId());
        HttpSession httpSession1 = new MockHttpSession();

        //when:
        loggedUsers.addUser(user, httpSession1);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_one_removeUser_for_one_user_and_same_session_as_addUser_then_logged_any_user() {
        //given:
        User user = TestUtils.newUser(123);
        Set<Integer> expected = Collections.emptySet();
        HttpSession httpSession1 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);

        //when:
        loggedUsers.removeUser(user, httpSession1);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_one_removeUser_for_one_user_and_other_session_as_addUser_then_logged_one_user() {
        //given:
        User user = TestUtils.newUser(123);
        Set<Integer> expected = Set.of(user.getId());
        HttpSession httpSession1 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);

        //when:
        loggedUsers.removeUser(user, new MockHttpSession());

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_one_removeUser_for_one_user_and_two_session_as_addUser_then_logged_one_user() {
        //given:
        User user = TestUtils.newUser(123);
        Set<Integer> expected = Set.of(user.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);

        //when:
        loggedUsers.removeUser(user, httpSession1);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_two_removeUser_for_one_user_and_two_session_as_addUser_then_logged_any_user() {
        //given:
        User user = TestUtils.newUser(123);
        Set<Integer> expected = Collections.emptySet();
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);

        //when:
        loggedUsers.removeUser(user, httpSession1);
        loggedUsers.removeUser(user, httpSession2);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_two_removeUser_for_one_user_and_one_session_as_addUser_then_logged_one_user() {
        //given:
        User user = TestUtils.newUser(123);
        Set<Integer> expected = Set.of(user.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);

        //when:
        loggedUsers.removeUser(user, httpSession1);
        loggedUsers.removeUser(user, new MockHttpSession());

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_updateUser_for_one_user_and_other_session_then_logged_any_user() {
        //given:
        User user = TestUtils.newUser(123);
        Set<Integer> expected = Collections.emptySet();
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);

        //when:
        loggedUsers.removeUser(user, httpSession1);
        loggedUsers.updateUser(user);
        loggedUsers.removeUser(user, httpSession2);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_updateUser_for_one_user_and_other_session_then_logged_one_user() {
        //given:
        User user = TestUtils.newUser(123);
        Set<Integer> expected = Set.of(user.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();

        //when:
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.updateUser(user);
        loggedUsers.addUser(user, httpSession2);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_two_removeUser_for_two_user_and_same_session_as_addUser_then_logged_any_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Collections.emptySet();
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user2, httpSession2);

        //when:
        loggedUsers.removeUser(user, httpSession1);
        loggedUsers.removeUser(user2, httpSession2);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_addUser_for_two_user_then_logged_two_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Set.of(user.getId(), user2.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();

        //when:
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user2, httpSession2);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_two_removeUser_for_two_user_and_other_session_as_addUser_then_logged_two_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Set.of(user.getId(), user2.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user2, httpSession2);

        //when:
        loggedUsers.removeUser(user, new MockHttpSession());

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_two_removeUser_for_two_user_and_two_session_as_addUser_then_logged_one_user() {

        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Set.of(user2.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user2, httpSession2);

        //when:
        loggedUsers.removeUser(user, httpSession1);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_three_removeUser_for_two_user_and_three_session_as_addUser_then_logged_any_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Collections.emptySet();
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        HttpSession httpSession3 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);
        loggedUsers.addUser(user2, httpSession3);

        //when:
        loggedUsers.removeUser(user, httpSession1);
        loggedUsers.removeUser(user, httpSession2);
        loggedUsers.removeUser(user2, httpSession3);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_three_removeUser_for_two_user_and_two_session_as_addUser_then_logged_one_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Set.of(user2.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        HttpSession httpSession3 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);
        loggedUsers.addUser(user2, httpSession3);

        //when:
        loggedUsers.removeUser(user, httpSession1);
        loggedUsers.removeUser(user, httpSession2);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_three_removeUser_for_two_user_and_one_session_as_addUser_then_logged_two_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Set.of(user.getId(), user2.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        HttpSession httpSession3 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);
        loggedUsers.addUser(user2, httpSession3);

        //when:
        loggedUsers.removeUser(user, httpSession2);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_three_removeUser_for_two_user_and_one_session_as_addUser_then_logged_one_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Set.of(user.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        HttpSession httpSession3 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);
        loggedUsers.addUser(user2, httpSession3);

        //when:
        loggedUsers.removeUser(user2, httpSession3);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_two_removeUser_for_two_user_and_one_session_as_addUser_then_logged_one_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Set.of(user2.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user2, httpSession2);

        //when:
        loggedUsers.removeUser(user, httpSession1);
        loggedUsers.removeUser(user2, new MockHttpSession());

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_updateUser_and_two_removeUser_for_two_user_then_logged_any_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Collections.emptySet();
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user2, httpSession2);

        //when:
        loggedUsers.updateUser(user);
        loggedUsers.removeUser(user, httpSession1);
        loggedUsers.removeUser(user2, httpSession2);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_addUser_and_updateUser_and_addUser_for_two_user_then_logged_two_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Set.of(user.getId(), user2.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();

        //when:
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.updateUser(user);
        loggedUsers.addUser(user2, httpSession2);

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_updateUser_for_updated_user_and_two_session_then_set_updated_user_in_two_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);
        User updatedUser = TestUtils.newUser(123);
        updatedUser.setAdmin(true);
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);

        //when:
        loggedUsers.updateUser(updatedUser);
        User result1 = (User) httpSession1.getAttribute(SESSION_USER);
        User result2 = (User) httpSession2.getAttribute(SESSION_USER);

        //then:
        Assert.assertEquals(updatedUser.isAdmin(), result1.isAdmin());
        Assert.assertEquals(updatedUser.isAdmin(), result1.isAdmin());
    }

    @Test
    public void when_updateUser_for_two_updated_user_and_two_session_then_set_second_updated_user_in_two_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);
        user.setFirstName("");
        User firstUpdatedUser = TestUtils.newUser(123);
        firstUpdatedUser.setAdmin(true);
        User secondUpdatedUser = TestUtils.newUser(123);
        secondUpdatedUser.setFirstName("firstName");
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);

        //when:
        loggedUsers.updateUser(firstUpdatedUser);
        loggedUsers.updateUser(secondUpdatedUser);
        User result1 = (User) httpSession1.getAttribute(SESSION_USER);
        User result2 = (User) httpSession2.getAttribute(SESSION_USER);

        //then:
        Assert.assertEquals(secondUpdatedUser.getFirstName(), result1.getFirstName());
        Assert.assertEquals(secondUpdatedUser.getFirstName(), result2.getFirstName());
    }

    @Test
    public void when_updateUser_for_updated_user_then_set_updated_user_in_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);
        User updatedUser = TestUtils.newUser(123);
        updatedUser.setAdmin(true);
        HttpSession httpSession1 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);

        //when:
        loggedUsers.updateUser(updatedUser);
        User result1 = (User) httpSession1.getAttribute(SESSION_USER);

        //then:
        Assert.assertEquals(updatedUser.isAdmin(), result1.isAdmin());
    }

    @Test
    public void when_updateUser_for_updated_user_then_not_set_nonupdated_user_in_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);
        User updatedUser = TestUtils.newUser(123);
        updatedUser.setAdmin(true);
        HttpSession httpSession1 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);

        //when:
        loggedUsers.updateUser(updatedUser);
        User result1 = (User) httpSession1.getAttribute(SESSION_USER);

        //then:
        Assert.assertNotEquals(user.isAdmin(), result1.isAdmin());
    }

    @Test
    public void when_updateUser_for_two_updated_user_then_set_second_updated_user_in_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);
        user.setFirstName("");
        User fisrtUpdatedUser = TestUtils.newUser(123);
        fisrtUpdatedUser.setAdmin(true);
        User secondUpdatedUser = TestUtils.newUser(123);
        secondUpdatedUser.setFirstName("firstName");
        HttpSession httpSession1 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);

        //when:
        loggedUsers.updateUser(fisrtUpdatedUser);
        loggedUsers.updateUser(secondUpdatedUser);
        User result1 = (User) httpSession1.getAttribute(SESSION_USER);

        //then:
        Assert.assertEquals(secondUpdatedUser.getFirstName(), result1.getFirstName());
    }

    @Test
    public void when_updateUser_for_two_updated_user_then_set_not_first_updated_user_in_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);
        user.setFirstName("");
        User firstUpdatedUser = TestUtils.newUser(123);
        firstUpdatedUser.setAdmin(true);
        User secondUpdatedUser = TestUtils.newUser(123);
        secondUpdatedUser.setFirstName("firstName");
        HttpSession httpSession1 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);

        //when:
        loggedUsers.updateUser(firstUpdatedUser);
        loggedUsers.updateUser(secondUpdatedUser);
        User result = (User) httpSession1.getAttribute(SESSION_USER);

        //then:
        Assert.assertNotEquals(firstUpdatedUser.getFirstName(), result.getFirstName());
    }

    @Test
    public void when_updateUser_for_updated_user_then_not_set_nonupdated_user_in_two_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);
        User updatedUser = TestUtils.newUser(123);
        updatedUser.setAdmin(true);
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);

        //when:
        loggedUsers.updateUser(updatedUser);
        User result1 = (User) httpSession1.getAttribute(SESSION_USER);
        User result2 = (User) httpSession2.getAttribute(SESSION_USER);

        //then:
        Assert.assertNotEquals(user.isAdmin(), result1.isAdmin());
        Assert.assertNotEquals(user.isAdmin(), result2.isAdmin());
    }
}