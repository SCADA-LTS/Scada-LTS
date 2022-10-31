package br.org.scadabr.db.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.db.AbstractMySQLDependentTest;
import br.org.scadabr.db.dao.mocks.MockDataPointDao;
import br.org.scadabr.db.dao.mocks.MockDataSourceDao;
import br.org.scadabr.db.dao.mocks.MockViewDao;
import br.org.scadabr.db.dao.mocks.MockWatchlistDao;
import br.org.scadabr.db.scenarios.DatalessDatabaseScenario;
import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.mango.service.ViewService;

public class UsersProfileDaoTest extends AbstractMySQLDependentTest {

	private static final int FIRST = 0;
	private static final int SECOND = 1;

	@Test
	public void getUsersProfilesShouldReturnAnEmptyListIfThereIsNoUsers() {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();
		assertTrue(dao.getUsersProfiles().isEmpty());
	}

	@Test
	public void getUsersProfilesShouldReturnAnListWithAllUserProfiles()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());

		UsersProfileService dao = new UsersProfileService();

		String name = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);
		dao.saveUsersProfile(profile);

		String name2 = "name2";
		UsersProfileVO profile2 = new UsersProfileVO();
		profile2.setName(name2);
		dao.saveUsersProfile(profile2);

		assertEquals(2, dao.getUsersProfiles().size());
		assertEquals(name, dao.getUsersProfiles().get(FIRST).getName());
		assertEquals(name2, dao.getUsersProfiles().get(SECOND).getName());
	}

	@Test
	public void saveUsersProfileShouldSaveName() throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		String name = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);
		dao.saveUsersProfile(profile);

		UsersProfileVO retrievedUser = dao.getUserProfileByName(name);
		assertEquals(name, retrievedUser.getName());
	}

	@Test(expected = DAOException.class)
	public void saveUsersProfileWithoutNameShouldFail() throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		UsersProfileVO profile = new UsersProfileVO();
		dao.saveUsersProfile(profile);
	}

	@Test(expected = DAOException.class)
	public void saveUsersProfileWithDuplicatedNameShouldFail()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		String name = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);
		dao.saveUsersProfile(profile);

		UsersProfileVO profile2 = new UsersProfileVO();
		profile2.setName(name);
		dao.saveUsersProfile(profile2);
	}

	@Test
	public void getByIdShouldReturnProfileWithThatID() throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		String name = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);
		dao.saveUsersProfile(profile);

		UsersProfileVO retrievedUser = dao.getUserProfileById(profile.getId());
		assertEquals(name, retrievedUser.getName());
	}

	@Test
	public void saveUsersProfileShouldSaveDataSourcePermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");

		String name = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);
		List<Integer> permissions = new ArrayList<Integer>();
		Integer dsId = ds.getId();
		permissions.add(dsId);
		profile.setDataSourcePermissions(permissions);
		dao.saveUsersProfile(profile);

		UsersProfileVO retrievedUser = dao.getUserProfileByName(name);
		assertEquals(dsId, retrievedUser.getDataSourcePermissions().get(FIRST));
		assertEquals(1, retrievedUser.getDataSourcePermissions().size());
	}

	@Test
	public void saveUsersProfileShouldSaveDataPointPermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		String name = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");

		DataPointVO dp = new MockDataPointDao().insertDataPoint("dpname",
				ds.getId());

		List<DataPointAccess> permissions = new ArrayList<DataPointAccess>();
		DataPointAccess permissionForDp = new DataPointAccess();
		permissionForDp.setDataPointId(dp.getId());
		permissionForDp.setPermission(DataPointAccess.SET);
		permissions.add(permissionForDp);
		profile.setDataPointPermissions(permissions);
		dao.saveUsersProfile(profile);

		UsersProfileVO retrievedUser = dao.getUserProfileByName(name);
		assertEquals(dp.getId(),
				retrievedUser.getDataPointPermissions().get(FIRST)
						.getDataPointId());
		assertEquals(DataPointAccess.SET, retrievedUser
				.getDataPointPermissions().get(FIRST).getPermission());
		assertEquals(1, retrievedUser.getDataPointPermissions().size());
	}

	@Test
	public void getByProfileByUserIdShouldReturnProfile() throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		String name = "profile1";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);
		dao.saveUsersProfile(profile);

		User user = TestUtils.createUser();

		profile.apply(user);

		dao.updateUsersProfile(user, profile);

		UsersProfileVO retrievedProfile = dao.getUserProfileByUserId(user
				.getId());
		assertEquals(profile.getId(), retrievedProfile.getId());
	}

	@Test
	public void updateUserProfileShoudUpdateUserProfileForLastAppliedUser()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");
		dao.saveUsersProfile(profile);

		UsersProfileVO profile2 = new UsersProfileVO();
		profile2.setName("profile2");
		dao.saveUsersProfile(profile2);

		User user = TestUtils.createUser();

		profile.apply(user);
		dao.updateUsersProfile(user, profile);

		profile2.apply(user);
		dao.updateUsersProfile(user, profile2);

		UsersProfileVO retrievedProfile = dao.getUserProfileByUserId(user
				.getId());
		assertEquals(profile2.getId(), retrievedProfile.getId());
	}

	@Test
	public void removeUserUserProfileShoudResetUserProfileForTheUser()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());

		UsersProfileService dao = new UsersProfileService();

		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile5");
		dao.saveUsersProfile(profile);

		User user = TestUtils.createUser();

		profile.apply(user);
		dao.saveUsersProfile(profile);

		dao.resetUserProfile(user);

		assertEquals(Common.NEW_ID, user.getUserProfile());
	}

	@Test
	public void saveProfileShoudUpdateProfileName() throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");
		dao.saveUsersProfile(profile);

		profile.setName("profile2");
		dao.saveUsersProfile(profile);

		UsersProfileVO retrievedProfile = dao.getUserProfileById(profile
				.getId());
		assertEquals("profile2", retrievedProfile.getName());
	}

	@Test(expected = DAOException.class)
	public void saveProfileShoudNotUpdateProfileNameIfNameAlredyExists()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");
		dao.saveUsersProfile(profile);

		UsersProfileVO profile2 = new UsersProfileVO();
		profile.setName("profile2");
		dao.saveUsersProfile(profile2);

		profile.setName("profile2");
		dao.saveUsersProfile(profile);
	}

	@Test
	public void saveProfileShoudNotThrowExceptionWithTheProfileNameHasNotChanged()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");
		dao.saveUsersProfile(profile);

		dao.saveUsersProfile(profile);

		UsersProfileVO retrievedProfile = dao.getUserProfileById(profile
				.getId());
		assertEquals("profile1", retrievedProfile.getName());
	}

	@Test
	public void saveProfileShoudUpdateDataSourcePermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");

		String name = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);
		List<Integer> permissions = new ArrayList<Integer>();
		Integer dsId = ds.getId();
		permissions.add(dsId);
		profile.setDataSourcePermissions(permissions);
		dao.saveUsersProfile(profile);

		List<Integer> newpermissions = new ArrayList<Integer>();
		profile.setDataSourcePermissions(newpermissions);
		dao.saveUsersProfile(profile);

		UsersProfileVO retrievedUserProfile = dao.getUserProfileById(profile
				.getId());
		assertTrue(retrievedUserProfile.getDataSourcePermissions().isEmpty());
	}

	@Test
	public void saveProfileShoudUpdateDataPointPermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		String name = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);

		dao.saveUsersProfile(profile);

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");
		DataPointVO dp = new MockDataPointDao().insertDataPoint("dpname",
				ds.getId());

		List<DataPointAccess> newPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess permissionForDp = new DataPointAccess();
		permissionForDp.setDataPointId(dp.getId());
		permissionForDp.setPermission(DataPointAccess.SET);
		newPermissions.add(permissionForDp);
		profile.setDataPointPermissions(newPermissions);

		dao.saveUsersProfile(profile);

		UsersProfileVO retrievedUser = dao.getUserProfileByName(name);
		assertEquals(dp.getId(),
				retrievedUser.getDataPointPermissions().get(FIRST)
						.getDataPointId());
		assertEquals(DataPointAccess.SET, retrievedUser
				.getDataPointPermissions().get(FIRST).getPermission());
		assertEquals(1, retrievedUser.getDataPointPermissions().size());
	}

	@Test
	public void saveProfileShoudUpdateDataSourcePermissionsForUsersWithThatProfile()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());

		UsersProfileService dao = new UsersProfileService();

		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");
		dao.saveUsersProfile(profile);

		User user = TestUtils.createUser();

		profile.apply(user);
		dao.updateUsersProfile(user, profile);

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");

		List<Integer> newpermissions = new ArrayList<Integer>();
		Integer dsId = ds.getId();
		newpermissions.add(dsId);
		profile.setDataSourcePermissions(newpermissions);
		dao.saveUsersProfile(profile);

		User retrievedUser = new UserDao().getUser(user.getId());
		assertEquals(dsId, retrievedUser.getDataSourcePermissions().get(FIRST));
		assertEquals(1, retrievedUser.getDataSourcePermissions().size());
	}

	@Test
	public void saveUsersProfileShouldSaveWatchlistsPermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		User user = TestUtils.createUser();
		MockWatchlistDao mockWatchlistDao = new MockWatchlistDao();
		WatchList wl = mockWatchlistDao.createNewWatchList("wl", 1,
				user.getId());

		String profileName = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(profileName);

		List<WatchListAccess> permissions = new ArrayList<WatchListAccess>();
		permissions.add(new WatchListAccess(wl.getId(), ShareUser.ACCESS_SET));
		profile.setWatchlistPermissions(permissions);
		dao.saveUsersProfile(profile);
		//dao.setWatchlistDao(mockWatchlistDao);

		UsersProfileVO retrievedProfile = dao.getUserProfileByName(profileName);
		assertEquals(wl.getId(), retrievedProfile.getWatchlistPermissions()
				.get(FIRST).getId());
		assertEquals(ShareUser.ACCESS_SET, retrievedProfile
				.getWatchlistPermissions().get(FIRST).getPermission());
		assertEquals(1, retrievedProfile.getWatchlistPermissions().size());
	}

	@Test
	public void getUsersProfileShouldPopulateWatchlists() throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		User user = TestUtils.createUser();
		MockWatchlistDao watchListDao = new MockWatchlistDao();
		WatchList wl = watchListDao.createNewWatchList("wl", 1, user.getId());

		String profileName = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(profileName);

		List<WatchListAccess> permissions = new ArrayList<WatchListAccess>();
		permissions.add(new WatchListAccess(wl.getId(), ShareUser.ACCESS_SET));
		profile.setWatchlistPermissions(permissions);
		dao.saveUsersProfile(profile);
		//dao.setWatchlistDao(watchListDao);

		UsersProfileVO retrievedProfile = dao.getUserProfileByName(profileName);

		assertEquals(wl.getId(),
				retrievedProfile.retrieveWatchlists().get(FIRST).getId());
		assertEquals(1, retrievedProfile.getWatchlistPermissions().size());
	}

	@Test
	public void updateUserProfileShouldUpdateWatchlistsPermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		User user = TestUtils.createUser();

		UsersProfileService dao = new UsersProfileService();
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");

		WatchList wl = new MockWatchlistDao().createNewWatchList("wl", 1,
				user.getId());

		List<WatchList> watchlists = new ArrayList<WatchList>();
		watchlists.add(wl);

		List<WatchListAccess> permissions = new ArrayList<WatchListAccess>();
		permissions.add(new WatchListAccess(wl.getId(), ShareUser.ACCESS_SET));
		profile.setWatchlistPermissions(permissions);

		profile.defineWatchlists(watchlists);

		WatchListDao mockWatchListDao = mock(WatchListDao.class);
		//dao.setWatchlistDao(mockWatchListDao);
		dao.saveUsersProfile(profile);

		profile.apply(user);
		dao.updateUsersProfile(user, profile);

		verify(mockWatchListDao).saveWatchList(
				profile.retrieveWatchlists().get(FIRST));
	}

	@Test
	public void saveUsersProfileShouldSaveViewsPermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		UsersProfileService dao = new UsersProfileService();

		User user = TestUtils.createUser();
		MockViewDao mockViewDao = new MockViewDao();
		View view = mockViewDao.createView("view", 1, user.getId());

		String profileName = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(profileName);

		List<ViewAccess> permissions = new ArrayList<ViewAccess>();
		permissions.add(new ViewAccess(view.getId(), ShareUser.ACCESS_SET));
		profile.setViewPermissions(permissions);
		dao.saveUsersProfile(profile);

		UsersProfileVO retrievedProfile = dao.getUserProfileByName(profileName);
		assertEquals(view.getId(),
				retrievedProfile.getViewPermissions().get(FIRST).getId());
		assertEquals(ShareUser.ACCESS_SET, retrievedProfile
				.getViewPermissions().get(FIRST).getPermission());
		assertEquals(1, retrievedProfile.getViewPermissions().size());
	}

	@Test
	public void getUsersProfileShouldPopulateViews() throws DAOException {
		useScenario(new DatalessDatabaseScenario());
        UsersProfileService dao = new UsersProfileService();

		User user = TestUtils.createUser();
		MockViewDao mockViewDao = new MockViewDao();
		View view = mockViewDao.createView("view", 1, user.getId());

		String profileName = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(profileName);

		List<ViewAccess> permissions = new ArrayList<ViewAccess>();
		permissions.add(new ViewAccess(view.getId(), ShareUser.ACCESS_SET));
		profile.setViewPermissions(permissions);
		dao.saveUsersProfile(profile);
		//dao.setViewDao(mockViewDao);

		UsersProfileVO retrievedProfile = dao.getUserProfileByName(profileName);

		assertEquals(view.getId(), retrievedProfile.retrieveViews().get(FIRST)
				.getId());
		assertEquals(1, retrievedProfile.retrieveViews().size());
	}

	@Test
	public void updateUserProfileShouldUpdateViewsPermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());
		User user = TestUtils.createUser();

		UsersProfileService dao = new UsersProfileService();
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView("view", 1, user.getId());

		List<View> views = new ArrayList<View>();
		views.add(view);

		List<ViewAccess> permissions = new ArrayList<ViewAccess>();
		permissions.add(new ViewAccess(view.getId(), ShareUser.ACCESS_SET));
		profile.setViewPermissions(permissions);

		profile.defineViews(views);

		ViewService mockViewDao = mock(ViewService.class);
		//dao.setViewDao(mockViewDao);
		dao.saveUsersProfile(profile);

		profile.apply(user);
		dao.updateUsersProfile(user, profile);

		verify(mockViewDao).saveView(profile.retrieveViews().get(FIRST));
	}

	@Test
	public void updateUserProfileShouldUpdateViewsPermissionsForAllUsersAssociatedWithThatProfile()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());

		User user = TestUtils.createUser();
		User user2 = TestUtils.createUser();

		UsersProfileService dao = new UsersProfileService();
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView("view", 1, user.getId());

		List<ShareUser> viewUsers = new ArrayList<ShareUser>();
		ShareUser user1oldpermissions = new ShareUser();
		user1oldpermissions.setAccessType(ShareUser.ACCESS_READ);
		user1oldpermissions.setUserId(user.getId());
		viewUsers.add(user1oldpermissions);
		view.setViewUsers(viewUsers);

		List<View> views = new ArrayList<View>();
		views.add(view);

		List<ViewAccess> newPermissions = new ArrayList<ViewAccess>();
		newPermissions.add(new ViewAccess(view.getId(), ShareUser.ACCESS_SET));
		profile.setViewPermissions(newPermissions);
		profile.defineViews(views);

		ViewService mockViewDao = mock(ViewService.class);
		//dao.setViewDao(mockViewDao);
		dao.saveUsersProfile(profile);

		List<Integer> users = new ArrayList<Integer>();
		users.add(user.getId());
		users.add(user2.getId());
		profile.defineUsers(users);
		profile.apply(user2);
		dao.updateUsersProfile(user2, profile);

		assertEquals(user.getId(), view.getViewUsers().get(FIRST).getUserId());
		assertEquals(ShareUser.ACCESS_SET, view.getViewUsers().get(FIRST)
				.getAccessType());
	}

	@Test
	public void applyShoudIncludePermissionsForUsersThatDidNotHaveAccessForANewView()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());

		User user = TestUtils.createUser();
		User user2 = TestUtils.createUser();

		UsersProfileService dao = new UsersProfileService();
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView("view", 1, user2.getId());

		ViewService mockViewDao = mock(ViewService.class);
		//dao.setViewDao(mockViewDao);
		dao.saveUsersProfile(profile);

		UsersProfileVO retrieviedUserProfile = dao.getUserProfileById(profile
				.getId());

		List<ViewAccess> newViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newPermission = new ViewAccess();
		newPermission.setId(view.getId());
		newPermission.setPermission(ShareUser.ACCESS_READ);
		newViewPermissions.add(newPermission);
		retrieviedUserProfile.setViewPermissions(newViewPermissions);

		retrieviedUserProfile.apply(user);
		dao.updateUsersProfile(user, retrieviedUserProfile);

		assertEquals(user.getId(),
				retrieviedUserProfile.retrieveViews().get(FIRST).getViewUsers()
						.get(FIRST).getUserId());
		assertEquals(ShareUser.ACCESS_READ, retrieviedUserProfile
				.retrieveViews().get(FIRST).getViewUsers().get(FIRST)
				.getAccessType());
	}

	@Test
	public void applyShoudIncludePermissionsForUsersThatDidNotHaveAccessForANewWatchlist()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());

		User user = TestUtils.createUser();
		User user2 = TestUtils.createUser();

		UsersProfileService dao = new UsersProfileService();
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("profile1");

		MockWatchlistDao wlDao = new MockWatchlistDao();

		WatchList wl = new MockWatchlistDao().createNewWatchList("wl", 1,
				user.getId());

		ViewService mockViewDao = mock(ViewService.class);
		//dao.setViewDao(mockViewDao);
		dao.saveUsersProfile(profile);

		UsersProfileVO retrieviedUserProfile = dao.getUserProfileById(profile
				.getId());

		List<WatchListAccess> watchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess newPermission = new WatchListAccess();
		newPermission.setId(wl.getId());
		newPermission.setPermission(ShareUser.ACCESS_READ);
		watchlistPermissions.add(newPermission);
		retrieviedUserProfile.setWatchlistPermissions(watchlistPermissions);

		retrieviedUserProfile.apply(user);
		dao.updateUsersProfile(user, retrieviedUserProfile);

		assertEquals(user.getId(), retrieviedUserProfile.retrieveWatchlists()
				.get(FIRST).getWatchListUsers().get(FIRST).getUserId());
		assertEquals(ShareUser.ACCESS_READ, retrieviedUserProfile
				.retrieveWatchlists().get(FIRST).getWatchListUsers().get(FIRST)
				.getAccessType());
	}
}
