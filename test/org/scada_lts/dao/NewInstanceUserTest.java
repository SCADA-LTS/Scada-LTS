package org.scada_lts.dao;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.UserNonAttributesEquals;

import java.util.Arrays;


public class NewInstanceUserTest {

    private User userExpected;

    @Before
    public void config() {
        userExpected = new User();
        userExpected.setId(12);
        userExpected.setUsername("setUsername");
        userExpected.setUserProfileId(123);
        userExpected.setPassword("pass123");
        userExpected.setEmail("email@test.com");
        userExpected.setLastLogin(4321);
        userExpected.setPhone("123456789");
        userExpected.setAdmin(true);
        userExpected.setDisabled(true);
        userExpected.setSelectedWatchList(321);
        userExpected.setHomeUrl("http://test:1111");
        userExpected.setHideMenu(true);
        userExpected.setTheme("Modern");
        userExpected.setReceiveAlarmEmails(21);
        userExpected.setReceiveOwnAuditEvents(true);
        userExpected.setUserProfileId(23);

        userExpected.setDataSourceProfilePermissions(Arrays.asList(1, 2));
        userExpected.setDataPointProfilePermissions(Arrays.asList(new DataPointAccess(1, 2)));
        userExpected.setDataSourcePermissions(Arrays.asList(2, 3));
        userExpected.setDataPointPermissions(Arrays.asList(new DataPointAccess(4, 2)));
        userExpected.setViewProfilePermissions(Arrays.asList(new ViewAccess(5, 2)));
        userExpected.setWatchListProfilePermissions(Arrays.asList(new WatchListAccess(6, 2)));
    }

    @Test
    public void when_newInstance_then_other_reference() {

        //when:
        User userResult = new User(userExpected);

        //then:
        Assert.assertNotSame("The instances are identical should be different", userExpected, userResult);
    }

    @Test
    public void when_newInstance_then_equals() {

        //given:
        UserNonAttributesEquals expected = new UserNonAttributesEquals(userExpected);

        //when:
        User userResult = new User(userExpected);
        UserNonAttributesEquals result = new UserNonAttributesEquals(userResult);

        //then:
        Assert.assertEquals(expected, result);
    }
}