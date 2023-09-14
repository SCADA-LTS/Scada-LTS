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
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static com.serotonin.mango.Common.SESSION_USER;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationBeans.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class LoggedUsersMultiThreadTest {

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
        TestConcurrentUtils.biConsumer(10, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, httpSession1)
        ));

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
        TestConcurrentUtils.biConsumer(10, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1)
        ));

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
        TestConcurrentUtils.biConsumer(10, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, new MockHttpSession())
        ));

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
        TestConcurrentUtils.biConsumer(10, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1)
        ));

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
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession2)
        ));

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
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, new MockHttpSession())
        ));

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
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1),
                new TestConcurrentUtils.ConsumerAction<>(loggedUsers::updateUser, user),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession2)
        ));

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
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, httpSession1),
                new TestConcurrentUtils.ConsumerAction<>(loggedUsers::updateUser, user),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, httpSession2)
        ));

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
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user2, httpSession2)
        ));

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
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, httpSession1),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user2, httpSession2)
        ));

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_four_addUser_for_two_user_then_logged_two_user() {
        //given:
        User user = TestUtils.newUser(1);
        User user2 = TestUtils.newUser(2);

        //when:
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, new MockHttpSession()),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user2, new MockHttpSession()),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, new MockHttpSession()),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user2, new MockHttpSession())
        ));

        //then:
        Assert.assertEquals(2, loggedUsers.getUserIds().size());
    }

    @Test
    public void when_six_addUser_for_two_user_then_logged_two_user() {
        //given:
        User user = TestUtils.newUser(1);
        User user2 = TestUtils.newUser(2);

        //when:
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, new MockHttpSession()),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user2, new MockHttpSession()),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, new MockHttpSession()),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user2, new MockHttpSession()),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, new MockHttpSession()),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user2, new MockHttpSession())
        ));

        //then:
        Assert.assertEquals(2, loggedUsers.getUserIds().size());
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
        TestConcurrentUtils.biConsumer(10, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, new MockHttpSession())
        ));

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
        TestConcurrentUtils.biConsumer(10, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1)
        ));

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
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession2),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user2, httpSession3)
        ));

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
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession2)
        ));

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
        TestConcurrentUtils.biConsumer(10, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession2)
        ));

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
        TestConcurrentUtils.biConsumer(10, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user2, httpSession3)
        ));

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
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user2, new MockHttpSession())
        ));

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_updateUser_for_two_user_and_other_session_then_logged_any_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Collections.emptySet();
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user2, httpSession2);

        //when:
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user, httpSession1),
                new TestConcurrentUtils.ConsumerAction<>(loggedUsers::updateUser, user),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::removeUser, user2, httpSession2)
        ));

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_updateUser_for_two_user_and_other_session_then_logged_two_user() {
        //given:
        User user = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(345);
        Set<Integer> expected = Set.of(user.getId(), user2.getId());
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();

        //when:
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user, httpSession1),
                new TestConcurrentUtils.ConsumerAction<>(loggedUsers::updateUser, user),
                new TestConcurrentUtils.BiFunctionAction<>(loggedUsers::addUser, user2, httpSession2)
        ));

        //then:
        Assert.assertEquals(expected, loggedUsers.getUserIds());
    }

    @Test
    public void when_updateUser_for_one_user_and_one_session_then_set_user_in_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setFirstName("nonupdated");
        User updatedUser = TestUtils.newUser(123);
        updatedUser.setFirstName("updated");
        HttpSession httpSession1 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);

        //when:
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.ConsumerAction<>(loggedUsers::updateUser, updatedUser)
        ));

        User result1 = (User) httpSession1.getAttribute(SESSION_USER);

        //then:
        Assert.assertEquals(updatedUser.isAdmin(), result1.isAdmin());
    }

    @Test
    public void when_updateUser_for_one_user_and_two_session_then_set_updated_user_in_two_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setFirstName("nonupdated");
        User updatedUser = TestUtils.newUser(123);
        updatedUser.setFirstName("updated");
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);

        //when:
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.ConsumerAction<>(loggedUsers::updateUser, updatedUser)
        ));
        User result1 = (User) httpSession1.getAttribute(SESSION_USER);
        User result2 = (User) httpSession2.getAttribute(SESSION_USER);

        //then:
        Assert.assertEquals(updatedUser.getFirstName(), result1.getFirstName());
        Assert.assertEquals(updatedUser.getFirstName(), result2.getFirstName());
    }

    @Test
    public void when_updateUser_for_one_user_and_two_session_then_no_set_no_updated_user_in_two_session() {
        //given:
        User user = TestUtils.newUser(123);
        user.setFirstName("nonupdated");
        User updatedUser = TestUtils.newUser(123);
        updatedUser.setFirstName("updated");
        HttpSession httpSession1 = new MockHttpSession();
        HttpSession httpSession2 = new MockHttpSession();
        loggedUsers.addUser(user, httpSession1);
        loggedUsers.addUser(user, httpSession2);

        //when:
        TestConcurrentUtils.biConsumer(1, Arrays.asList(
                new TestConcurrentUtils.ConsumerAction<>(loggedUsers::updateUser, updatedUser)
        ));
        User result1 = (User) httpSession1.getAttribute(SESSION_USER);
        User result2 = (User) httpSession2.getAttribute(SESSION_USER);

        //then:
        Assert.assertNotEquals(user.getFirstName(), result1.getFirstName());
        Assert.assertNotEquals(user.getFirstName(), result2.getFirstName());
    }
}