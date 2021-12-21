package org.scada_lts.mango.service;

import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.UserDAO;
import org.scada_lts.dao.UsersProfileDAO;
import org.scada_lts.permissions.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsersProfileServiceTest {

    private UsersProfileService usersProfileService;
    private UsersProfileDAO usersProfileDAOMock;
    private User user;

    @Before
    public void config() {
        this.usersProfileDAOMock = mock(UsersProfileDAO.class);
        UserDAO userDAOMock = mock(UserDAO.class);

        PermissionsService<WatchListAccess, UsersProfileVO> watchListPermissionsService = mock(PermissionsService.class);
        PermissionsService<DataPointAccess, UsersProfileVO> dataPointPermissionsService = mock(PermissionsService.class);
        PermissionsService<Integer, UsersProfileVO> dataSourcePermissionsService = mock(PermissionsService.class);
        PermissionsService<ViewAccess, UsersProfileVO> viewPermissionsService = mock(PermissionsService.class);

        this.usersProfileService = new UsersProfileService(usersProfileDAOMock, userDAOMock,
                watchListPermissionsService, dataPointPermissionsService, dataSourcePermissionsService, viewPermissionsService);
        this.user = TestUtils.newUser(123);
    }

    @Test
    public void when_getProfileByUser_with_more_one_profiles_then_profile_higher_id() {

        //given:
        UsersProfileVO profile = new UsersProfileVO();
        profile.setId(1);
        UsersProfileVO profile2 = new UsersProfileVO();
        profile2.setId(2);
        UsersProfileVO profile3 = new UsersProfileVO();
        profile3.setId(3);

        List<UsersProfileVO> profiles = new ArrayList<>();
        profiles.add(profile);
        profiles.add(profile2);
        profiles.add(profile3);

        when(usersProfileDAOMock.selectUserProfileByUserId(user.getId())).thenReturn(profiles);

        //when:
        Optional<UsersProfileVO> result = usersProfileService.getProfileByUser(user);

        //then:
        assertTrue(result.isPresent());
        assertEquals(profile3, result.get());
    }

    @Test
    public void when_getProfileByUserId_with_more_one_profiles_then_profile_higher_id() {

        //given:
        UsersProfileVO profile = new UsersProfileVO();
        profile.setId(4);
        UsersProfileVO profile2 = new UsersProfileVO();
        profile2.setId(5);
        UsersProfileVO profile3 = new UsersProfileVO();
        profile3.setId(6);

        List<UsersProfileVO> profiles = new ArrayList<>();
        profiles.add(profile);
        profiles.add(profile2);
        profiles.add(profile3);

        when(usersProfileDAOMock.selectUserProfileByUserId(user.getId())).thenReturn(profiles);

        //when:
        Optional<UsersProfileVO> result = usersProfileService.getProfileByUserId(user.getId());

        //then:
        assertTrue(result.isPresent());
        assertEquals(profile3, result.get());
    }

    @Test
    public void when_getProfileByUserId_with_more_one_profiles_and_null_then_profile_higher_id() {

        //given:
        UsersProfileVO profile = new UsersProfileVO();
        profile.setId(4);
        UsersProfileVO profile3 = new UsersProfileVO();
        profile3.setId(6);

        List<UsersProfileVO> profiles = new ArrayList<>();
        profiles.add(profile);
        profiles.add(null);
        profiles.add(profile3);

        when(usersProfileDAOMock.selectUserProfileByUserId(user.getId())).thenReturn(profiles);

        //when:
        Optional<UsersProfileVO> result = usersProfileService.getProfileByUserId(user.getId());

        //then:
        assertTrue(result.isPresent());
        assertEquals(profile3, result.get());
    }
}