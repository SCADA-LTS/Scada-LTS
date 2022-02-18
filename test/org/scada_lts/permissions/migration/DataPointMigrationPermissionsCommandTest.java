package org.scada_lts.permissions.migration;

import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.dao.IUsersProfileDAO;
import org.scada_lts.dao.UserDAO;
import org.scada_lts.mango.adapter.MangoDataPoint;
import org.scada_lts.mango.service.*;
import org.scada_lts.permissions.service.*;
import org.scada_lts.permissions.service.util.PermissionsUtils;
import utils.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(Parameterized.class)
public class DataPointMigrationPermissionsCommandTest {

    private final static int dataPointId1 = 11;
    private final static int dataPointId2 = 22;
    private final static int dataPointId3 = 33;
    private final static int dataPointId4 = 44;
    private final static int dataPointId5 = 55;
    private final static int dataPointId6 = 66;


    @Parameterized.Parameters(name= "{index}: \nfromProfile: {0}, fromUser: {1}, \nfromView1: {2}, fromView2: {3}, permissionView1: {4}, permissionView2: {5}, \nexpected: {6}\n\n")
    public static Object[][] primeNumbers() {
        return new Object[][] {
                new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    new ArrayList<>(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),

            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId4, 2)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId4, 2)).collect(Collectors.toList()),

            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList())
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId4, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId4, 1)).collect(Collectors.toList()),

            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId1, 2)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList())
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList())
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 1)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 2), new DataPointAccess(dataPointId2, 2)).collect(Collectors.toList()),

            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2)).collect(Collectors.toList())
            }, new Object[] {
                    new ArrayList<>(),
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId2, 2)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2)).collect(Collectors.toList())
            },new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    new ArrayList<>(),
                    Stream.of(dataPointId5).collect(Collectors.toList()),
                    Collections.emptyList(),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 1)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    new ArrayList<>(),
                    Stream.of(dataPointId5).collect(Collectors.toList()),
                    Collections.emptyList(),
                    2,
                    1,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 2)).collect(Collectors.toList()),

            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    new ArrayList<>(),
                    Stream.of(dataPointId5, dataPointId4, dataPointId2).collect(Collectors.toList()),
                    Collections.emptyList(),
                    2,
                    1,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 2),
                            new DataPointAccess(dataPointId4, 2)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1),
                            new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    new ArrayList<>(),
                    Stream.of(dataPointId5, dataPointId4, dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId4).collect(Collectors.toList()),
                    2,
                    1,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 2),
                            new DataPointAccess(dataPointId4, 2)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1),
                            new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 2)).collect(Collectors.toList()),
                    Stream.of(dataPointId5, dataPointId4, dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId2).collect(Collectors.toList()),
                    2,
                    1,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 2),
                            new DataPointAccess(dataPointId4, 2)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1),
                            new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId5, dataPointId4, dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId3).collect(Collectors.toList()),
                    2,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 2),
                            new DataPointAccess(dataPointId4, 2)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1),
                            new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId5, dataPointId4, dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId3).collect(Collectors.toList()),
                    2,
                    1,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 1), new DataPointAccess(dataPointId5, 2),
                            new DataPointAccess(dataPointId4, 2)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1),
                            new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId5, dataPointId4, dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId3).collect(Collectors.toList()),
                    1,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 1),
                            new DataPointAccess(dataPointId4, 1)).collect(Collectors.toList()),
            }, new Object[] {
                Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                        new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                Stream.of(new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                Stream.of(dataPointId5, dataPointId4, dataPointId2).collect(Collectors.toList()),
                Stream.of(dataPointId3).collect(Collectors.toList()),
                1,
                2,
                Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                        new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 1),
                        new DataPointAccess(dataPointId4, 1)).collect(Collectors.toList()),
            } , new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId5, dataPointId4, dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId3).collect(Collectors.toList()),
                    2,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 2),
                            new DataPointAccess(dataPointId4, 2)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1), new DataPointAccess(dataPointId4, 1),
                            new DataPointAccess(dataPointId5, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId5, dataPointId4, dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId3).collect(Collectors.toList()),
                    2,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 2),
                            new DataPointAccess(dataPointId4, 2)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1), new DataPointAccess(dataPointId4, 1),
                            new DataPointAccess(dataPointId5, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId3).collect(Collectors.toList()),
                    2,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 1),
                            new DataPointAccess(dataPointId4, 1)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1), new DataPointAccess(dataPointId6, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1), new DataPointAccess(dataPointId4, 1),
                            new DataPointAccess(dataPointId5, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId3).collect(Collectors.toList()),
                    2,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 1),
                            new DataPointAccess(dataPointId4, 1), new DataPointAccess(dataPointId6, 1)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1), new DataPointAccess(dataPointId4, 1), new DataPointAccess(dataPointId6, 1),
                            new DataPointAccess(dataPointId5, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId3).collect(Collectors.toList()),
                    2,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 1),
                            new DataPointAccess(dataPointId4, 1), new DataPointAccess(dataPointId6, 1)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1), new DataPointAccess(dataPointId4, 1),
                            new DataPointAccess(dataPointId5, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId2, dataPointId6).collect(Collectors.toList()),
                    Stream.of(dataPointId3).collect(Collectors.toList()),
                    2,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 1),
                            new DataPointAccess(dataPointId4, 1), new DataPointAccess(dataPointId6, 2)).collect(Collectors.toList()),
            }, new Object[] {
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 1)).collect(Collectors.toList()),
                    Stream.of(new DataPointAccess(dataPointId3, 1), new DataPointAccess(dataPointId4, 1),
                            new DataPointAccess(dataPointId5, 1)).collect(Collectors.toList()),
                    Stream.of(dataPointId2).collect(Collectors.toList()),
                    Stream.of(dataPointId3, dataPointId6).collect(Collectors.toList()),
                    2,
                    2,
                    Stream.of(new DataPointAccess(dataPointId1, 1), new DataPointAccess(dataPointId2, 2),
                            new DataPointAccess(dataPointId3, 2), new DataPointAccess(dataPointId5, 1),
                            new DataPointAccess(dataPointId4, 1), new DataPointAccess(dataPointId6, 2)).collect(Collectors.toList()),
            }
        };
    }

    private List<DataPointAccess> expected;
    private List<DataPointAccess> fromProfile;
    private List<DataPointAccess> fromUser;
    private List<Integer> fromView1;
    private List<Integer> fromView2;
    private int permissionView1;
    private int permissionView2;

    private PermissionsService<DataPointAccess, UsersProfileVO> profilePermissionsService;
    private PermissionsService<DataPointAccess, User> userPermissionsService;

    private User user;

    private MigrationPermissionsService migrationPermissionsService;
    private MigrationDataService migrationDataService;
    private List<User> users;
    private List<View> views;

    private UsersProfileService usersProfileService;

    public DataPointMigrationPermissionsCommandTest(List<DataPointAccess> fromProfile,
                                                    List<DataPointAccess> fromUser,
                                                    List<Integer> fromView1,
                                                    List<Integer> fromView2,
                                                    int permissionView1,
                                                    int permissionView2,
                                                    List<DataPointAccess> expected) {
        this.fromProfile = new ArrayList<>(fromProfile);
        this.fromUser = new ArrayList<>(fromUser);
        this.fromView1 = new ArrayList<>(fromView1);
        this.fromView2 = new ArrayList<>(fromView2);
        this.permissionView1 = permissionView1;
        this.permissionView2 = permissionView2;
        this.expected = new ArrayList<>(expected);
    }

    @Before
    public void config() {
        this.expected.sort(Comparator.comparingInt(DataPointAccess::getDataPointId));

        UsersProfileVO profile = new UsersProfileVO();
        profile.setId(123);
        profile.setXid("UP_TEST_1_");
        profile.setName("profileName");
        profile.setDataPointPermissions(fromProfile);

        int userId = 1234;
        user = TestUtils.newUser(userId);
        user.setUserProfile(profile);
        users = Arrays.asList(user);

        Map<Integer, List<DataPointAccess>> userPermissions = new HashMap<>();
        userPermissions.put(user.getId(), fromUser);

        Map<Integer, UsersProfileVO> userProfiles = new HashMap<>();
        userProfiles.put(user.getId(), profile);

        Map<Integer, UsersProfileVO> profiles = new HashMap<>();
        profiles.put(profile.getId(), profile);

        IUsersProfileDAO usersProfileDAO = new UsersProfileDAOMemory(profiles, userProfiles);

        profilePermissionsService = new DataPointProfilePermissionsService(usersProfileDAO);
        userPermissionsService = new PermissionsServiceUserTestImpl<>(userPermissions);

        PermissionsService<WatchListAccess, User> watchListUserPermissionsService = new PermissionsServiceUserTestImpl<>(new HashMap<>());
        PermissionsService<Integer, User> dataSourceUserPermissionsService = new PermissionsServiceUserTestImpl<>(new HashMap<>());
        PermissionsService<ViewAccess, User> viewUserPermissionsService = new PermissionsServiceUserTestImpl<>(new HashMap<>());

        PermissionsService<WatchListAccess, UsersProfileVO> watchListPermissionsService = new PermissionsServiceProfileTestImpl<>(new HashMap<>());
        PermissionsService<Integer, UsersProfileVO> dataSourcePermissionsService = new PermissionsServiceProfileTestImpl<>(new HashMap<>());
        PermissionsService<ViewAccess, UsersProfileVO> viewPermissionsService = new PermissionsServiceProfileTestImpl<>(new HashMap<>());


        DataPointVO dataPoint1 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint1.setId(dataPointId1);
        VirtualPointLocatorVO virtualPointLocator1 = new VirtualPointLocatorVO();
        virtualPointLocator1.setSettable(true);
        dataPoint1.setPointLocator(virtualPointLocator1);

        DataPointVO dataPoint2 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint2.setId(dataPointId2);
        VirtualPointLocatorVO virtualPointLocator2 = new VirtualPointLocatorVO();
        virtualPointLocator2.setSettable(true);
        dataPoint2.setPointLocator(virtualPointLocator2);

        DataPointVO dataPoint3 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint3.setId(dataPointId3);
        VirtualPointLocatorVO virtualPointLocator3 = new VirtualPointLocatorVO();
        virtualPointLocator3.setSettable(true);
        dataPoint3.setPointLocator(virtualPointLocator3);

        DataPointVO dataPoint4 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint4.setId(dataPointId4);
        VirtualPointLocatorVO virtualPointLocator4 = new VirtualPointLocatorVO();
        virtualPointLocator4.setSettable(true);
        dataPoint4.setPointLocator(virtualPointLocator4);

        DataPointVO dataPoint5 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint5.setId(dataPointId5);
        VirtualPointLocatorVO virtualPointLocator5 = new VirtualPointLocatorVO();
        virtualPointLocator5.setSettable(true);
        dataPoint5.setPointLocator(virtualPointLocator5);

        DataPointVO dataPoint6 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint6.setId(dataPointId6);
        VirtualPointLocatorVO virtualPointLocator6 = new VirtualPointLocatorVO();
        virtualPointLocator6.setSettable(true);
        dataPoint6.setPointLocator(virtualPointLocator6);

        List<DataPointVO> dataPoints = Arrays.asList(dataPoint1, dataPoint2, dataPoint3, dataPoint4, dataPoint5, dataPoint6);

        views = new ArrayList<>();
        views.add(ViewTestUtils.newView(dataPoints, fromView1, new ShareUser(user.getId(), permissionView1)));
        views.add(ViewTestUtils.newView(dataPoints, fromView2, new ShareUser(user.getId(), permissionView2)));

        MangoDataPoint dataPointService = mock(DataPointService.class);
        when(dataPointService.getDataPoints(null, false)).thenReturn(dataPoints);

        UserDAO userDAO = mock(UserDAO.class);
        when(userDAO.getUser(anyInt())).thenReturn(user);

        usersProfileService = new UsersProfileService(usersProfileDAO, userDAO,
                watchListPermissionsService, profilePermissionsService,
                dataSourcePermissionsService, viewPermissionsService);

        migrationPermissionsService = new MigrationPermissionsService(userPermissionsService,
                dataSourceUserPermissionsService, watchListUserPermissionsService, viewUserPermissionsService);

        migrationDataService = new MigrationDataService(dataPoints.stream().collect(Collectors.toMap(DataPointVO::getId, a -> a)),
                        new HashMap<>(),
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
        List<DataPointAccess> dataPointAccesses = userPermissionsService.getPermissions(user);

        UsersProfileVO usersProfileVO = new UsersProfileVO();
        usersProfileVO.setId(user.getUserProfile());

        List<DataPointAccess> dataPointAccessesFromProfile = profilePermissionsService.getPermissions(usersProfileVO);

        List<DataPointAccess> result = new ArrayList<>(PermissionsUtils.mergeDataPointAccesses(dataPointAccesses, dataPointAccessesFromProfile));
        result.sort(Comparator.comparingInt(DataPointAccess::getDataPointId));

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

        List<DataPointAccess> result = profilePermissionsService.getPermissions(usersProfileVO);
        result.sort(Comparator.comparingInt(DataPointAccess::getDataPointId));

        assertEquals(expected, result);
    }
}