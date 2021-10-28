package org.scada_lts.dao;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.UsersProfileEquals;

import java.util.Arrays;


public class NewInstanceUsersProfileTest {

    private UsersProfileVO profileExpected;

    @Before
    public void config() {
        profileExpected = new UsersProfileVO();
        profileExpected.setId(12);
        profileExpected.setName("setUsername");
        profileExpected.setXid("TEST_XID");
        profileExpected.setWatchlistPermissions(Arrays.asList(new WatchListAccess(1, 2)));
        profileExpected.setViewPermissions(Arrays.asList(new ViewAccess(2, 1)));
        profileExpected.setDataSourcePermissions(Arrays.asList(3));
        profileExpected.setDataPointPermissions(Arrays.asList(new DataPointAccess(4, 2)));
    }

    @Test
    public void when_newInstance_then_other_reference() {

        //when:
        UsersProfileVO profileResult = new UsersProfileVO(profileExpected);

        //then:
        Assert.assertNotSame("The instances are identical should be different", profileExpected, profileResult);
    }

    @Test
    public void when_newInstance_then_equals() {
        //given:
        UsersProfileEquals expected = new UsersProfileEquals(profileExpected);

        //when:
        UsersProfileVO profileResult = new UsersProfileVO(profileExpected);
        UsersProfileEquals result = new UsersProfileEquals(profileResult);

        //then:
        Assert.assertEquals(expected, result);
    }
}