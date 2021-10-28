package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.IUsersProfileDAO;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.*;
import utils.UsersProfileDAOMemory;

import java.util.*;

import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.merge;


public class MigrationUpdatePermissionsTest {

    private UsersProfileVO userProfile;

    private User user1;
    private User user2;
    private User user3;
    private User user4;

    private UsersProfileService usersProfileService;
    private Map<Accesses, UsersProfileVO> accessesProfiles;

    @Before
    public void config() {
        accessesProfiles = new HashMap<>();

        userProfile = new UsersProfileVO();
        userProfile.setId(12);
        userProfile.setName("profile_name");
        userProfile.setDataPointPermissions(Arrays.asList(new DataPointAccess(4, 2)));
        Accesses key = new Accesses(userProfile);
        accessesProfiles.put(key, userProfile);

        UsersProfileVO userProfile2 = new UsersProfileVO();
        userProfile2.setId(34);
        userProfile2.setName("profile2_name");
        userProfile2.setDataPointPermissions(Arrays.asList(new DataPointAccess(8, 2)));
        Accesses key2 = new Accesses(userProfile2);
        accessesProfiles.put(key2, userProfile2);

        UsersProfileVO userProfile3 = new UsersProfileVO();
        userProfile3.setId(56);
        userProfile3.setName("profile3_name");
        userProfile3.setDataPointPermissions(Arrays.asList(new DataPointAccess(9, 2)));
        Accesses key3 = new Accesses(userProfile3);
        accessesProfiles.put(key3, userProfile3);

        user1 = new User();
        user1.setId(1);
        user1.setUsername("usr_1");

        user2 = new User();
        user2.setId(2);
        user2.setUsername("usr_2");

        user3 = new User();
        user3.setId(3);
        user3.setUsername("usr_3");

        user4 = new User();
        user4.setId(4);
        user4.setUsername("usr_4");

        Map<Integer, UsersProfileVO> profiles = new HashMap<>();
        profiles.put(userProfile.getId(), userProfile);
        profiles.put(userProfile2.getId(), userProfile2);
        profiles.put(userProfile3.getId(), userProfile3);

        IUsersProfileDAO usersProfileDAO = new UsersProfileDAOMemory(profiles, Arrays.asList(user1, user2, user3, user4));

        PermissionsService<DataPointAccess, UsersProfileVO> profilePermissionsService = new DataPointProfilePermissionsService(usersProfileDAO);
        PermissionsService<WatchListAccess, UsersProfileVO> watchListPermissionsService = new WatchListProfilePermissionsService(usersProfileDAO);
        PermissionsService<Integer, UsersProfileVO> dataSourcePermissionsService = new DataSourceProfilePermissionsService(usersProfileDAO);
        PermissionsService<ViewAccess, UsersProfileVO> viewPermissionsService = new ViewProfilePermissionsService(usersProfileDAO);

        usersProfileService = new UsersProfileService(usersProfileDAO,null, watchListPermissionsService,
                profilePermissionsService, dataSourcePermissionsService, viewPermissionsService);

    }

    @Test
    public void when_updatePermissions_for_users_with_created_profile_then_update_this_profile() {

        //given:
        Accesses key = new Accesses(userProfile);
        user1.setUserProfile(userProfile);
        user2.setUserProfile(userProfile);
        user4.setUserProfile(userProfile);

        UsersProfileVO newAccesses1 = new UsersProfileVO();
        newAccesses1.setWatchlistPermissions(Arrays.asList(new WatchListAccess(1,2)));
        newAccesses1.setViewPermissions(Arrays.asList(new ViewAccess(2, 2)));
        newAccesses1.setDataSourcePermissions(Arrays.asList(1,2,3));
        newAccesses1.setDataPointPermissions(Arrays.asList(new DataPointAccess(3,1)));
        Accesses user1Permissions = new Accesses(newAccesses1);

        UsersProfileVO newAccesses2 = new UsersProfileVO();
        newAccesses2.setWatchlistPermissions(Arrays.asList(new WatchListAccess(6,2)));
        newAccesses2.setViewPermissions(Arrays.asList(new ViewAccess(5, 2)));
        newAccesses2.setDataSourcePermissions(Arrays.asList(4,5));
        newAccesses2.setDataPointPermissions(Arrays.asList(new DataPointAccess(7,1)));
        Accesses user2Permissions = new Accesses(newAccesses2);

        UsersProfileVO newAccesses3 = new UsersProfileVO();
        newAccesses3.setWatchlistPermissions(Arrays.asList(new WatchListAccess(9,2)));
        newAccesses3.setViewPermissions(Arrays.asList(new ViewAccess(10, 2)));
        newAccesses3.setDataSourcePermissions(Arrays.asList(12,13));
        newAccesses3.setDataPointPermissions(Arrays.asList(new DataPointAccess(11,1)));
        Accesses user3Permissions = new Accesses(newAccesses3);

        UsersProfileVO newAccesses4 = new UsersProfileVO();
        newAccesses4.setWatchlistPermissions(Arrays.asList(new WatchListAccess(14,2)));
        newAccesses4.setViewPermissions(Arrays.asList(new ViewAccess(13, 2)));
        newAccesses4.setDataSourcePermissions(Arrays.asList(15,16));
        newAccesses4.setDataPointPermissions(Arrays.asList(new DataPointAccess(12,1)));
        Accesses user4Permissions = new Accesses(newAccesses4);

        Accesses accessesExpected = merge(merge(merge(user1Permissions, key), user2Permissions), user4Permissions);

        UsersProfileVO profileExpected = new UsersProfileVO(userProfile);
        profileExpected.setDataPointPermissions(new ArrayList<>(accessesExpected.getDataPointAccesses()));
        profileExpected.setDataSourcePermissions(new ArrayList<>(accessesExpected.getDataSourceAccesses()));
        profileExpected.setWatchlistPermissions(new ArrayList<>(accessesExpected.getWatchListAccesses()));
        profileExpected.setViewPermissions(new ArrayList<>(accessesExpected.getViewAccesses()));

        //when:
        MigrationPermissionsUtils.updatePermissions(user1, user1Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user2, user2Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user3, user3Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user4, user4Permissions, usersProfileService, accessesProfiles);

        UsersProfileVO result1 = usersProfileService.getProfileByUserId(user1.getId()).get();

        //then:
        Assert.assertNotEquals(userProfile, result1);
        Assert.assertEquals(profileExpected.getDataPointPermissions(), result1.getDataPointPermissions());
        Assert.assertEquals(profileExpected.getDataSourcePermissions(), result1.getDataSourcePermissions());
        Assert.assertEquals(profileExpected.getViewPermissions(), result1.getViewPermissions());
        Assert.assertEquals(profileExpected.getWatchlistPermissions(), result1.getWatchlistPermissions());
    }

    @Test
    public void when_updatePermissions_for_user_without_profile_then_create_profile() {

        //given:
        UsersProfileVO newAccesses1 = new UsersProfileVO();
        newAccesses1.setWatchlistPermissions(Arrays.asList(new WatchListAccess(1,2)));
        newAccesses1.setViewPermissions(Arrays.asList(new ViewAccess(2, 2)));
        newAccesses1.setDataSourcePermissions(Arrays.asList(1,2,3));
        newAccesses1.setDataPointPermissions(Arrays.asList(new DataPointAccess(3,1)));
        Accesses user1Permissions = new Accesses(newAccesses1);

        UsersProfileVO newAccesses2 = new UsersProfileVO();
        newAccesses2.setWatchlistPermissions(Arrays.asList(new WatchListAccess(6,2)));
        newAccesses2.setViewPermissions(Arrays.asList(new ViewAccess(5, 2)));
        newAccesses2.setDataSourcePermissions(Arrays.asList(4,5));
        newAccesses2.setDataPointPermissions(Arrays.asList(new DataPointAccess(7,1)));
        Accesses user2Permissions = new Accesses(newAccesses2);

        UsersProfileVO newAccesses3 = new UsersProfileVO();
        newAccesses3.setWatchlistPermissions(Arrays.asList(new WatchListAccess(9,2)));
        newAccesses3.setViewPermissions(Arrays.asList(new ViewAccess(10, 2)));
        newAccesses3.setDataSourcePermissions(Arrays.asList(12,13));
        newAccesses3.setDataPointPermissions(Arrays.asList(new DataPointAccess(11,1)));
        Accesses user3Permissions = new Accesses(newAccesses3);

        user1.setUserProfile(userProfile);
        user2.setUserProfile(userProfile);
        user3.setUserProfileId(Common.NEW_ID);
        user4.setUserProfile(userProfile);

        //when:
        MigrationPermissionsUtils.updatePermissions(user1, user1Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user2, user2Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user3, user3Permissions, usersProfileService, accessesProfiles);

        //then:
        Optional<UsersProfileVO> profileResult = usersProfileService.getProfileByUserId(user3.getId());
        Assert.assertTrue(profileResult.isPresent());
        Assert.assertNotEquals(profileResult.get(), userProfile);
    }

    @Test
    public void when_updatePermissions_for_user_without_profile_then_create_profile_with_permissions() {

        //given:
        UsersProfileVO newAccesses1 = new UsersProfileVO();
        newAccesses1.setWatchlistPermissions(Arrays.asList(new WatchListAccess(1,2)));
        newAccesses1.setViewPermissions(Arrays.asList(new ViewAccess(2, 2)));
        newAccesses1.setDataSourcePermissions(Arrays.asList(1,2,3));
        newAccesses1.setDataPointPermissions(Arrays.asList(new DataPointAccess(3,1)));
        Accesses user1Permissions = new Accesses(newAccesses1);

        UsersProfileVO newAccesses2 = new UsersProfileVO();
        newAccesses2.setWatchlistPermissions(Arrays.asList(new WatchListAccess(6,2)));
        newAccesses2.setViewPermissions(Arrays.asList(new ViewAccess(5, 2)));
        newAccesses2.setDataSourcePermissions(Arrays.asList(4,5));
        newAccesses2.setDataPointPermissions(Arrays.asList(new DataPointAccess(7,1)));
        Accesses user2Permissions = new Accesses(newAccesses2);

        UsersProfileVO newAccesses3 = new UsersProfileVO();
        newAccesses3.setWatchlistPermissions(Arrays.asList(new WatchListAccess(9,2)));
        newAccesses3.setViewPermissions(Arrays.asList(new ViewAccess(10, 2)));
        newAccesses3.setDataSourcePermissions(Arrays.asList(12,13));
        newAccesses3.setDataPointPermissions(Arrays.asList(new DataPointAccess(11,1)));
        Accesses user3Permissions = new Accesses(newAccesses3);

        user1.setUserProfile(userProfile);
        user2.setUserProfile(userProfile);
        user3.setUserProfileId(Common.NEW_ID);
        user4.setUserProfile(userProfile);

        //when:
        MigrationPermissionsUtils.updatePermissions(user1, user1Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user2, user2Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user3, user3Permissions, usersProfileService, accessesProfiles);

        //then:
        Optional<UsersProfileVO> profileResult = usersProfileService.getProfileByUserId(user3.getId());
        Accesses accessesResult = new Accesses(profileResult.get());
        Assert.assertEquals(user3Permissions.getDataPointAccesses(), accessesResult.getDataPointAccesses());
        Assert.assertEquals(user3Permissions.getDataSourceAccesses(), accessesResult.getDataSourceAccesses());
        Assert.assertEquals(user3Permissions.getViewAccesses(), accessesResult.getViewAccesses());
        Assert.assertEquals(user3Permissions.getWatchListAccesses(), accessesResult.getWatchListAccesses());
    }

    @Test
    public void when_updatePermissions_for_users_with_profile_insufficient_then_create_one_profile_for_all() {

        //given:
        Accesses key = new Accesses(userProfile);
        UsersProfileVO newAccesses1 = new UsersProfileVO();
        newAccesses1.setWatchlistPermissions(Arrays.asList(new WatchListAccess(1,2)));
        newAccesses1.setViewPermissions(Arrays.asList(new ViewAccess(2, 2)));
        newAccesses1.setDataSourcePermissions(Arrays.asList(1,2,3));
        newAccesses1.setDataPointPermissions(Arrays.asList(new DataPointAccess(3,1)));
        Accesses user1Permissions = new Accesses(newAccesses1);

        UsersProfileVO newAccesses2 = new UsersProfileVO();
        newAccesses2.setWatchlistPermissions(Arrays.asList(new WatchListAccess(6,2)));
        newAccesses2.setViewPermissions(Arrays.asList(new ViewAccess(5, 2)));
        newAccesses2.setDataSourcePermissions(Arrays.asList(4,5));
        newAccesses2.setDataPointPermissions(Arrays.asList(new DataPointAccess(7,1)));
        Accesses user2Permissions = new Accesses(newAccesses2);

        UsersProfileVO newAccesses3 = new UsersProfileVO();
        newAccesses3.setWatchlistPermissions(Arrays.asList(new WatchListAccess(9,2)));
        newAccesses3.setViewPermissions(Arrays.asList(new ViewAccess(10, 2)));
        newAccesses3.setDataSourcePermissions(Arrays.asList(12,13));
        newAccesses3.setDataPointPermissions(Arrays.asList(new DataPointAccess(11,1)));
        Accesses user3Permissions = new Accesses(newAccesses3);

        Accesses accessesExpected = merge(merge(user1Permissions, key), user2Permissions);

        UsersProfileVO profileExpected = new UsersProfileVO(userProfile);
        profileExpected.setDataPointPermissions(new ArrayList<>(accessesExpected.getDataPointAccesses()));
        profileExpected.setDataSourcePermissions(new ArrayList<>(accessesExpected.getDataSourceAccesses()));
        profileExpected.setWatchlistPermissions(new ArrayList<>(accessesExpected.getWatchListAccesses()));
        profileExpected.setViewPermissions(new ArrayList<>(accessesExpected.getViewAccesses()));

        user1.setUserProfile(userProfile);
        user2.setUserProfile(userProfile);
        user3.setUserProfileId(Common.NEW_ID);
        user4.setUserProfile(userProfile);

        //when:
        MigrationPermissionsUtils.updatePermissions(user1, user1Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user2, user2Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user3, user3Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user4, user3Permissions, usersProfileService, accessesProfiles);

        Optional<UsersProfileVO> result1 = usersProfileService.getProfileByUserId(user1.getId());
        Optional<UsersProfileVO> result2 = usersProfileService.getProfileByUserId(user2.getId());
        Optional<UsersProfileVO> result3 = usersProfileService.getProfileByUserId(user4.getId());

        //then:
        Assert.assertTrue(result1.isPresent());
        Assert.assertTrue(result2.isPresent());
        Assert.assertTrue(result3.isPresent());
        Assert.assertNotEquals(userProfile, result1.get());
        Assert.assertEquals(result1, result2);
        Assert.assertEquals(result2, result3);
        Assert.assertEquals(result1, result3);
    }

    @Test
    public void when_updatePermissions_for_users_with_profile_sufficient_then_remains_this_profile() {

        //given:
        Accesses key = new Accesses(userProfile);
        UsersProfileVO newAccesses1 = new UsersProfileVO();
        newAccesses1.setDataPointPermissions(Arrays.asList(new DataPointAccess(4, 2)));
        Accesses user1Permissions = new Accesses(newAccesses1);

        UsersProfileVO newAccesses2 = new UsersProfileVO();
        Accesses user2Permissions = new Accesses(newAccesses2);

        UsersProfileVO newAccesses3 = new UsersProfileVO();
        Accesses user3Permissions = new Accesses(newAccesses3);

        Accesses accessesExpected = merge(merge(user1Permissions, key), user2Permissions);

        UsersProfileVO profileExpected = new UsersProfileVO(userProfile);
        profileExpected.setDataPointPermissions(new ArrayList<>(accessesExpected.getDataPointAccesses()));
        profileExpected.setDataSourcePermissions(new ArrayList<>(accessesExpected.getDataSourceAccesses()));
        profileExpected.setWatchlistPermissions(new ArrayList<>(accessesExpected.getWatchListAccesses()));
        profileExpected.setViewPermissions(new ArrayList<>(accessesExpected.getViewAccesses()));

        user1.setUserProfile(userProfile);
        user2.setUserProfile(userProfile);
        user3.setUserProfileId(Common.NEW_ID);
        user4.setUserProfile(userProfile);

        //when:
        MigrationPermissionsUtils.updatePermissions(user1, user1Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user2, user2Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user3, user3Permissions, usersProfileService, accessesProfiles);
        MigrationPermissionsUtils.updatePermissions(user4, user3Permissions, usersProfileService, accessesProfiles);

        UsersProfileVO result1 = usersProfileService.getProfileByUserId(user1.getId()).get();
        UsersProfileVO result2 = usersProfileService.getProfileByUserId(user2.getId()).get();
        UsersProfileVO result3 = usersProfileService.getProfileByUserId(user4.getId()).get();

        //then:
        Assert.assertEquals(userProfile, result1);
        Assert.assertEquals(userProfile, result2);
        Assert.assertEquals(userProfile, result3);
    }
}
