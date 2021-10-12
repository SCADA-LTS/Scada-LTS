package org.scada_lts.permissions.migration;

import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.dao.*;
import org.scada_lts.mango.service.*;
import org.scada_lts.permissions.service.DataSourceProfilePermissionsService;
import org.scada_lts.permissions.service.PermissionsService;
import utils.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.scada_lts.permissions.service.util.PermissionsUtils.*;


@RunWith(Parameterized.class)
public class DataSourceMigrationPermissionsCommandTest {

    private final static int dataSourceId1 = 11;
    private final static int dataSourceId2 = 22;
    private final static int dataSourceId3 = 33;
    private final static int dataSourceId4 = 44;
    private final static int dataSourceId5 = 55;


    @Parameterized.Parameters(name= "{index}: \nfromProfile: {0}, fromUser: {1}, \nexpected: {2}\n\n")
    public static Object[][] primeNumbers() {
        return new Object[][] {
                new Object[] {
                    Stream.of(dataSourceId1, dataSourceId2, dataSourceId3).collect(Collectors.toList()),
                    new ArrayList<>(),
                    Stream.of(dataSourceId1, dataSourceId2, dataSourceId3).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(dataSourceId1, dataSourceId2, dataSourceId3).collect(Collectors.toList()),
                    Stream.of(dataSourceId4, dataSourceId5).collect(Collectors.toList()),
                    Stream.of(dataSourceId1, dataSourceId2, dataSourceId3, dataSourceId4, dataSourceId5).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(dataSourceId1, dataSourceId2, dataSourceId3).collect(Collectors.toList()),
                    Stream.of(dataSourceId4, dataSourceId5, dataSourceId2).collect(Collectors.toList()),
                    Stream.of(dataSourceId1, dataSourceId2, dataSourceId3, dataSourceId4, dataSourceId5).collect(Collectors.toList()),
            }, new Object[] {
                    new ArrayList<>(),
                    Stream.of(dataSourceId4, dataSourceId5, dataSourceId2).collect(Collectors.toList()),
                    Stream.of(dataSourceId2, dataSourceId4, dataSourceId5).collect(Collectors.toList()),
            }, new Object[] {
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
            }
        };
    }

    private Set<Integer> expected;
    private List<Integer> fromProfile;
    private List<Integer> fromUser;

    private PermissionsService<Integer, UsersProfileVO> profilePermissionsService;
    private PermissionsService<Integer, User> userPermissionsService;

    private User user;

    private MigrationPermissionsService migrationPermissionsService;
    private MigrationDataService migrationDataService;
    private List<User> users;
    private List<View> views;

    private UsersProfileService usersProfileService;

    public DataSourceMigrationPermissionsCommandTest(List<Integer> fromProfile,
                                                     List<Integer> fromUser,
                                                     List<Integer> expected) {
        this.fromProfile = new ArrayList<>(fromProfile);
        this.fromUser = new ArrayList<>(fromUser);
        this.expected = new HashSet<>(expected);
    }

    @Before
    public void config() {

        UsersProfileVO profile = new UsersProfileVO();
        profile.setId(123);
        profile.setXid("UP_TEST_1_");
        profile.setName("profileName");
        profile.setDataSourcePermissions(fromProfile);

        int userId = 1234;
        user = TestUtils.newUser(userId);
        user.setUserProfile(profile);
        users = Arrays.asList(user);

        Map<Integer, List<Integer>> userPermissions = new HashMap<>();
        userPermissions.put(user.getId(), fromUser);

        Map<Integer, UsersProfileVO> userProfiles = new HashMap<>();
        userProfiles.put(user.getId(), profile);

        Map<Integer, UsersProfileVO> profiles = new HashMap<>();
        profiles.put(profile.getId(), profile);

        IUsersProfileDAO usersProfileDAO = new UsersProfileDAOMemory(profiles, userProfiles);

        profilePermissionsService = new DataSourceProfilePermissionsService(usersProfileDAO);
        userPermissionsService = new PermissionsServiceUserTestImpl<>(userPermissions);

        PermissionsService<WatchListAccess, User> watchListUserPermissionsService = new PermissionsServiceUserTestImpl<>(new HashMap<>());
        PermissionsService<DataPointAccess, User> dataPointUserPermissionsService = new PermissionsServiceUserTestImpl<>(new HashMap<>());
        PermissionsService<ViewAccess, User> viewUserPermissionsService = new PermissionsServiceUserTestImpl<>(new HashMap<>());

        PermissionsService<WatchListAccess, UsersProfileVO> watchListPermissionsService = new PermissionsServiceProfileTestImpl<>(new HashMap<>());
        PermissionsService<DataPointAccess, UsersProfileVO> dataPointPermissionsService = new PermissionsServiceProfileTestImpl<>(new HashMap<>());
        PermissionsService<ViewAccess, UsersProfileVO> viewPermissionsService = new PermissionsServiceProfileTestImpl<>(new HashMap<>());

        List<DataSourceVO<?>> dataSources = new ArrayList<>();
        DataSourceVO<VirtualDataSourceVO> ds1 = new VirtualDataSourceVO();
        ds1.setId(dataSourceId1);
        DataSourceVO<VirtualDataSourceVO> ds2 = new VirtualDataSourceVO();
        ds2.setId(dataSourceId2);
        DataSourceVO<VirtualDataSourceVO> ds3 = new VirtualDataSourceVO();
        ds3.setId(dataSourceId3);
        DataSourceVO<VirtualDataSourceVO> ds4 = new VirtualDataSourceVO();
        ds4.setId(dataSourceId4);
        DataSourceVO<VirtualDataSourceVO> ds5 = new VirtualDataSourceVO();
        ds5.setId(dataSourceId5);

        dataSources.add(ds1);
        dataSources.add(ds2);
        dataSources.add(ds3);
        dataSources.add(ds4);
        dataSources.add(ds5);

        views = new ArrayList<>();
        views.add(new View());

        UserDAO userDAO = mock(UserDAO.class);
        when(userDAO.getUser(anyInt())).thenReturn(user);

        usersProfileService = new UsersProfileService(usersProfileDAO, userDAO,
                watchListPermissionsService, dataPointPermissionsService,
                profilePermissionsService, viewPermissionsService);

        migrationPermissionsService = new MigrationPermissionsService(dataPointUserPermissionsService,
                userPermissionsService, watchListUserPermissionsService, viewUserPermissionsService);

        migrationDataService = new MigrationDataService(new HashMap<>(),
                dataSources.stream().collect(Collectors.toMap(DataSourceVO::getId, a -> a)),
                views.stream().collect(Collectors.toMap(View::getId, a -> a)),
                new HashMap<>(), usersProfileService);
    }

    @After
    public void clean() {
        usersProfileService.getUsersProfiles().forEach(a -> usersProfileService.deleteUserProfile(a.getId()));
    }

    @Test
    public void when_execute_for_user_and_profile_accesses_then_same_accesses() {
        //given:
        MigrationPermissions migrationCommand = MigrationPermissions.newMigration(migrationPermissionsService, migrationDataService);

        //when:
        migrationCommand.execute(users);

        //then:
        List<Integer> dataPointAccesses = userPermissionsService.getPermissions(user);

        UsersProfileVO usersProfileVO = new UsersProfileVO();
        usersProfileVO.setId(user.getUserProfile());

        List<Integer> dataPointAccessesFromProfile = profilePermissionsService.getPermissions(usersProfileVO);

        Set<Integer> result = mergeInt(new HashSet<>(dataPointAccesses), new HashSet<>(dataPointAccessesFromProfile));

        assertEquals(expected, result);
    }

    @Test
    public void when_execute_for_user_and_profile_accesses_then_same_profile_accesses() {
        //given:
        MigrationPermissions migrationCommand = MigrationPermissions.newMigration(migrationPermissionsService, migrationDataService);

        //when:
        migrationCommand.execute(users);

        //then:
        UsersProfileVO usersProfileVO = new UsersProfileVO();
        usersProfileVO.setId(user.getUserProfile());

        Set<Integer> result = new HashSet<>(profilePermissionsService.getPermissions(usersProfileVO));

        assertEquals(expected, result);
    }
}