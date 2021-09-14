package br.org.scadabr.vo.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.web.dwr.EmportDwr;
import com.serotonin.mango.web.dwr.beans.ImportTask;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.scada_lts.mango.service.UsersProfileService;

public class UsersProfileImporterTest extends AbstractMySQLDependentTest {

	private static final String PROFILE_NAME = "profileName";
	private static final String PROFILE_XID = "UP_1234";
	private static final String DATA_POINT_XID = "DP_1234";
	private static final String VIEW_XID = "V_1234";
	private static final String VIEW2_XID = "V_12345";
	private static final String WATCHLIST_XID = "V_1234";
	private static final String WATCHLIST2_XID = "V_12345";
	private static final int FIRST = 0;
	private static final int SECOND = 1;

	private UsersProfileService usersProfileService = new UsersProfileService();

	private String getJson(UsersProfileVO exportedUsersProfile) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put(EmportDwr.USERS_PROFILES, exportedUsersProfile);

		JsonWriter writer = new JsonWriter();
		writer.setPrettyOutput(true);

		String output = "";

		try {
			output = writer.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	@Test
	public void importNewUsersProfileWithoutPermissionsShoudCreateUsersProfile()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setXid(PROFILE_XID);
		exportedUsersProfile.setName(PROFILE_NAME);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		UsersProfileVO retrievedUsersProfile = usersProfileService
				.getUserProfileByXid(exportedUsersProfile.getXid());

		assertEquals(exportedUsersProfile.getXid(),
				retrievedUsersProfile.getXid());
		assertEquals(exportedUsersProfile.getName(),
				retrievedUsersProfile.getName());
	}

	@Test
	public void importNewUsersProfileWithDataSourcePermissionsShoudCreateUsersProfileWithDataSourcePermissions()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);
		List<Integer> permissions = new ArrayList<Integer>();
		Integer dsId = ds.getId();
		permissions.add(dsId);
		exportedUsersProfile.setDataSourcePermissions(permissions);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		UsersProfileVO retrievedProfile = usersProfileService.getUserProfileByXid(PROFILE_XID);
		assertEquals(dsId,
				retrievedProfile.getDataSourcePermissions().get(FIRST));
		assertEquals(1, retrievedProfile.getDataSourcePermissions().size());
	}

	@Test
	public void importNewUsersProfileWithDataPointPermissionsShoudCreateUsersProfileWithDataPointPermissions()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		DataPointVO dp = new MockDataPointDao().insertDataPoint("dpname",
				ds.getId());
		dp.setXid(DATA_POINT_XID);

		List<DataPointAccess> permissions = new ArrayList<DataPointAccess>();
		DataPointAccess permissionForDp = new DataPointAccess();
		permissionForDp.setDataPointId(dp.getId());
		permissionForDp.setPermission(DataPointAccess.SET);
		permissions.add(permissionForDp);
		exportedUsersProfile.setDataPointPermissions(permissions);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		UsersProfileVO retrievedProfile = usersProfileService.getUserProfileByXid(PROFILE_XID);
		assertEquals(dp.getId(), retrievedProfile.getDataPointPermissions()
				.get(FIRST).getDataPointId());
		assertEquals(DataPointAccess.SET, retrievedProfile
				.getDataPointPermissions().get(FIRST).getPermission());
		assertEquals(1, retrievedProfile.getDataPointPermissions().size());
	}

	@Test
	public void importNewUsersProfileWithViewPermissionsShoudCreateUsersProfileWithViewPermissions()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		User user = TestUtils.createUser();

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView("view", 1, user.getId());

		List<ViewAccess> newViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newPermission = new ViewAccess();
		newPermission.setId(view.getId());
		newPermission.setPermission(ShareUser.ACCESS_READ);
		newViewPermissions.add(newPermission);
		exportedUsersProfile.setViewPermissions(newViewPermissions);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		UsersProfileVO retrievedProfile = usersProfileService.getUserProfileByXid(PROFILE_XID);
		assertEquals(view.getId(),
				retrievedProfile.getViewPermissions().get(FIRST).getId());
		assertEquals(ShareUser.ACCESS_READ, retrievedProfile
				.getViewPermissions().get(FIRST).getPermission());
		assertEquals(1, retrievedProfile.getViewPermissions().size());
	}

	@Test
	public void importNewUsersProfileWithWatchlistPermissionsShoudCreateUsersProfileWithWatchlistPermissions()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		User user = TestUtils.createUser();

		MockWatchlistDao mockWatchlistDao = new MockWatchlistDao();
		WatchList watchlist = mockWatchlistDao.createNewWatchList("wl", 1,
				user.getId());

		List<WatchListAccess> watchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess newPermission = new WatchListAccess();
		newPermission.setId(watchlist.getId());
		newPermission.setPermission(ShareUser.ACCESS_READ);
		watchlistPermissions.add(newPermission);
		exportedUsersProfile.setWatchlistPermissions(watchlistPermissions);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		UsersProfileVO retrievedProfile = usersProfileService.getUserProfileByXid(PROFILE_XID);
		assertEquals(watchlist.getId(), retrievedProfile
				.getWatchlistPermissions().get(FIRST).getId());
		assertEquals(ShareUser.ACCESS_READ, retrievedProfile
				.getWatchlistPermissions().get(FIRST).getPermission());
		assertEquals(1, retrievedProfile.getWatchlistPermissions().size());
	}

	@Test
	public void importNewUsersProfileWithDataSourcePermissionsAndAUserShoudCreateUsersProfileWithDataSourcePermissionsAppliedToTheUser()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);
		List<Integer> profileDataSourcePermissions = new ArrayList<Integer>();
		Integer dsId = ds.getId();
		profileDataSourcePermissions.add(dsId);
		exportedUsersProfile
				.setDataSourcePermissions(profileDataSourcePermissions);

		User user = TestUtils.createUser();

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		UsersProfileVO retrievedProfile = usersProfileService.getUserProfileByXid(PROFILE_XID);
		User retrievedUser = new UserDao().getUser(user.getId());

		assertEquals(profileDataSourcePermissions,
				retrievedUser.getDataSourcePermissions());
	}

	@Test
	public void importNewUsersProfileWithDataPointPermissionsAndAUserShoudCreateUsersProfileWithDataSourcePermissionsAppliedToTheUser()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");

		DataPointVO dp = new MockDataPointDao().insertDataPoint("dpname",
				ds.getId());
		dp.setXid(DATA_POINT_XID);

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		List<DataPointAccess> profileDataPointPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess accessPermissions = new DataPointAccess();
		accessPermissions.setDataPointId(dp.getId());
		accessPermissions.setPermission(DataPointAccess.READ);
		profileDataPointPermissions.add(accessPermissions);

		exportedUsersProfile
				.setDataPointPermissions(profileDataPointPermissions);

		User user = TestUtils.createUser();

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());

		assertEquals(profileDataPointPermissions.get(FIRST).getDataPointId(),
				retrievedUser.getDataPointPermissions().get(FIRST)
						.getDataPointId());
		assertEquals(profileDataPointPermissions.get(FIRST).getPermission(),
				retrievedUser.getDataPointPermissions().get(FIRST)
						.getPermission());
	}

	@Test
	public void importNewUsersProfileWithViewPermissionsAndAUserShoudCreateUsersProfileWithViewPermissionsAppliedToTheUser()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView("view", 1, owner.getId());

		List<ViewAccess> profileViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newPermission = new ViewAccess();
		newPermission.setId(view.getId());
		newPermission.setPermission(ShareUser.ACCESS_READ);
		profileViewPermissions.add(newPermission);
		exportedUsersProfile.setViewPermissions(profileViewPermissions);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());
		View retrievedView = new ViewDao().getViews().get(FIRST);

		assertEquals(retrievedUser.getId(),
				retrievedView.getViewUsers().get(FIRST).getUserId());
		assertEquals(profileViewPermissions.get(FIRST).getPermission(),
				retrievedView.getViewUsers().get(FIRST).getAccessType());
	}

	@Test
	public void importNewUsersProfileWithWatchlistPermissionsAndAUserShoudCreateUsersProfileWithWatchlistPermissionsAppliedToTheUser()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();

		MockWatchlistDao mockWatchlistDao = new MockWatchlistDao();
		WatchList watchlist = mockWatchlistDao.createNewWatchList("wl", 1,
				owner.getId());

		List<WatchListAccess> profileWatchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess newPermission = new WatchListAccess();
		newPermission.setId(watchlist.getId());
		newPermission.setPermission(ShareUser.ACCESS_READ);
		profileWatchlistPermissions.add(newPermission);
		exportedUsersProfile
				.setWatchlistPermissions(profileWatchlistPermissions);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());
		WatchList retrievedWatchlist = new WatchListDao()
				.getWatchList(watchlist.getId());

		assertEquals(retrievedUser.getId(), retrievedWatchlist
				.getWatchListUsers().get(FIRST).getUserId());
		assertEquals(profileWatchlistPermissions.get(FIRST).getPermission(),
				retrievedWatchlist.getWatchListUsers().get(FIRST)
						.getAccessType());
	}

	@Test
	public void importNewUsersProfileWithViewPermissionsAndAUserWithConflitantViewShoudCreateUsersProfileWithNewViewPermissionsAppliedToTheUser()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView(VIEW_XID, 1, owner.getId());

		ShareUser oldPermission = new ShareUser();
		oldPermission.setUserId(user.getId());
		oldPermission.setAccessType(ShareUser.ACCESS_SET);
		view.getViewUsers().add(oldPermission);
		viewDao.saveView(view);

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		List<ViewAccess> profileViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newPermission = new ViewAccess();
		newPermission.setId(view.getId());
		newPermission.setPermission(ShareUser.ACCESS_READ);
		profileViewPermissions.add(newPermission);
		exportedUsersProfile.setViewPermissions(profileViewPermissions);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());
		View retrievedView = new ViewDao().getViews().get(FIRST);

		assertEquals(retrievedUser.getId(),
				retrievedView.getViewUsers().get(FIRST).getUserId());
		assertEquals(profileViewPermissions.get(FIRST).getPermission(),
				retrievedView.getViewUsers().get(FIRST).getAccessType());

	}

	@Test
	public void importNewUsersProfileWithViewPermissionsAndAUserWithAdditionalViewShoudCreateUsersProfileWithNewViewPermissionsAppliedToTheUserAndMantainOldPermissions()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();
		User user2 = TestUtils.createUser();

		MockViewDao viewDao = new MockViewDao();

		View view = viewDao.createView(VIEW_XID, 1, owner.getId());
		View view2 = viewDao.createView(VIEW2_XID, 2, owner.getId());

		ShareUser oldPermission = new ShareUser();
		oldPermission.setUserId(user.getId());
		oldPermission.setAccessType(ShareUser.ACCESS_SET);
		view.getViewUsers().add(oldPermission);
		viewDao.saveView(view);

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		List<ViewAccess> profileViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newPermission = new ViewAccess();
		newPermission.setId(view2.getId());
		newPermission.setPermission(ShareUser.ACCESS_SET);
		profileViewPermissions.add(newPermission);
		exportedUsersProfile.setViewPermissions(profileViewPermissions);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		usersIds.add(user2.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());
		View retrievedView1 = new ViewDao().getViews().get(FIRST);
		View retrievedView2 = new ViewDao().getViews().get(SECOND);

		assertEquals(retrievedUser.getId(),
				retrievedView1.getViewUsers().get(FIRST).getUserId());
		assertEquals(oldPermission.getAccessType(), retrievedView1
				.getViewUsers().get(FIRST).getAccessType());

		assertEquals(retrievedUser.getId(),
				retrievedView2.getViewUsers().get(FIRST).getUserId());
		assertEquals(profileViewPermissions.get(FIRST).getPermission(),
				retrievedView2.getViewUsers().get(FIRST).getAccessType());

	}

	@Test
	public void importNewUsersProfileWithWatchlistPermissionsAndAUserWithAdditionalViewShoudCreateUsersProfileWithNewViewPermissionsAppliedToTheUserAndMantainOldPermissions()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();
		User user2 = TestUtils.createUser();

		WatchListDao watchlistDao = new WatchListDao();

		WatchList watchlist = new WatchList();
		watchlist.setUserId(owner.getId());
		watchlist.setName("wl");
		watchlist.setXid(WATCHLIST_XID);
		ShareUser oldPermission = new ShareUser();
		oldPermission.setUserId(user.getId());
		oldPermission.setAccessType(ShareUser.ACCESS_SET);
		watchlist.getWatchListUsers().add(oldPermission);
		watchlistDao.saveWatchList(watchlist);

		WatchList watchlist2 = new WatchList();
		watchlist2.setUserId(owner.getId());
		watchlist2.setName("wl2");
		watchlist2.setXid(WATCHLIST2_XID);
		watchlistDao.saveWatchList(watchlist2);

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		List<WatchListAccess> profileWatchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess newPermission = new WatchListAccess();
		newPermission.setId(watchlist2.getId());
		newPermission.setPermission(ShareUser.ACCESS_SET);
		profileWatchlistPermissions.add(newPermission);
		exportedUsersProfile
				.setWatchlistPermissions(profileWatchlistPermissions);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		usersIds.add(user2.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());

		WatchList retrievedWatchlist1 = watchlistDao.getWatchList(watchlist
				.getId());
		WatchList retrievedWatchlist2 = watchlistDao.getWatchList(watchlist2
				.getId());

		assertEquals(retrievedUser.getId(), retrievedWatchlist1
				.getWatchListUsers().get(FIRST).getUserId());
		assertEquals(oldPermission.getAccessType(), retrievedWatchlist1
				.getWatchListUsers().get(FIRST).getAccessType());

		assertEquals(retrievedUser.getId(), retrievedWatchlist1
				.getWatchListUsers().get(FIRST).getUserId());
		assertEquals(profileWatchlistPermissions.get(FIRST).getPermission(),
				retrievedWatchlist2.getWatchListUsers().get(FIRST)
						.getAccessType());
	}

	@Test
	public void importNewUsersProfileWithDatasourcePermissionsAndAUserWithAdditionalDatasourceShoudCreateUsersProfileWithNewDatasourcePermissionsAppliedToTheUserAndMantainOldPermissions()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		User user = TestUtils.createUser();
		User user2 = TestUtils.createUser();

		DataSourceVO ds1 = new MockDataSourceDao().insertDataSource("dsname1");
		DataSourceVO ds2 = new MockDataSourceDao().insertDataSource("dsname2");

		List<Integer> oldDataSourcePermissions = new ArrayList<Integer>();
		oldDataSourcePermissions.add(ds1.getId());
		user.setDataSourcePermissions(oldDataSourcePermissions);
		new UserDao().saveUser(user);

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		List<Integer> permissions = new ArrayList<Integer>();
		permissions.add(ds2.getId());
		exportedUsersProfile.setDataSourcePermissions(permissions);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		usersIds.add(user2.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());
		User retrievedUser2 = new UserDao().getUser(user2.getId());

		assertTrue(ds2.getId() == retrievedUser.getDataSourcePermissions().get(
				FIRST));
		assertTrue(ds1.getId() == retrievedUser.getDataSourcePermissions().get(
				SECOND));

		assertEquals(2, retrievedUser.getDataSourcePermissions().size());

		assertTrue(ds2.getId() == retrievedUser2.getDataSourcePermissions()
				.get(FIRST));

		assertEquals(1, retrievedUser2.getDataSourcePermissions().size());
	}

	@Test
	public void importNewUsersProfileWithDatapointPermissionsAndAUserWithAdditionalDatapointShoudCreateUsersProfileWithNewDatapointPermissionsAppliedToTheUserAndMantainOldPermissions()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		User user = TestUtils.createUser();
		User user2 = TestUtils.createUser();

		DataSourceVO ds1 = new MockDataSourceDao().insertDataSource("dsname1");

		DataPointVO dp1 = new MockDataPointDao().insertDataPoint("dpname",
				ds1.getId());
		dp1.setXid(DATA_POINT_XID);

		DataPointVO dp2 = new MockDataPointDao().insertDataPoint("dpname2",
				ds1.getId());
		dp2.setXid(DATA_POINT_XID + "1");

		List<DataPointAccess> oldDataPointPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess oldDataPointPermission = new DataPointAccess();
		oldDataPointPermission.setDataPointId(dp1.getId());
		oldDataPointPermission.setPermission(DataPointAccess.SET);
		oldDataPointPermissions.add(oldDataPointPermission);
		user.setDataPointPermissions(oldDataPointPermissions);
		new UserDao().saveUser(user);

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		List<DataPointAccess> newPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess newDataPointPermission = new DataPointAccess();
		newDataPointPermission.setDataPointId(dp2.getId());
		newDataPointPermission.setPermission(DataPointAccess.SET);
		newPermissions.add(newDataPointPermission);
		exportedUsersProfile.setDataPointPermissions(newPermissions);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		usersIds.add(user2.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());
		User retrievedUser2 = new UserDao().getUser(user2.getId());

		assertTrue(dp2.getId() == retrievedUser.getDataPointPermissions()
				.get(FIRST).getDataPointId());
		assertTrue(newDataPointPermission.getPermission() == retrievedUser
				.getDataPointPermissions().get(FIRST).getPermission());

		assertTrue(dp1.getId() == retrievedUser.getDataPointPermissions()
				.get(SECOND).getDataPointId());
		assertTrue(oldDataPointPermission.getPermission() == retrievedUser
				.getDataPointPermissions().get(SECOND).getPermission());

		assertEquals(2, retrievedUser.getDataPointPermissions().size());

		assertTrue(dp2.getId() == retrievedUser2.getDataPointPermissions()
				.get(FIRST).getDataPointId());
		assertTrue(newDataPointPermission.getPermission() == retrievedUser2
				.getDataPointPermissions().get(FIRST).getPermission());

		assertEquals(1, retrievedUser2.getDataPointPermissions().size());
	}

	@Test
	public void importUsersProfileShouldOverwriteUsersViewPermissionsFromOldAppliedProfile()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView(VIEW_XID, 1, owner.getId());
		View view2 = viewDao.createView(VIEW2_XID, 2, owner.getId());

		UsersProfileVO oldProfile = new UsersProfileVO();
		oldProfile.setName("oldProfile");
		oldProfile.setXid("oldXid");
		List<ViewAccess> viewPermissions = new ArrayList<ViewAccess>();
		viewPermissions.add(new ViewAccess(view.getId(), ViewAccess.SET));
		viewPermissions.add(new ViewAccess(view2.getId(), ViewAccess.SET));
		oldProfile.setViewPermissions(viewPermissions);
		usersProfileService.saveUsersProfile(oldProfile);

		oldProfile.apply(user);
		usersProfileService.updateUsersProfile(user, oldProfile);

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		List<ViewAccess> profileViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newPermission = new ViewAccess();
		newPermission.setId(view.getId());
		newPermission.setPermission(ShareUser.ACCESS_READ);
		profileViewPermissions.add(newPermission);
		exportedUsersProfile.setViewPermissions(profileViewPermissions);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());
		View retrievedView = new ViewDao().getViews().get(FIRST);

		assertEquals(retrievedUser.getId(),
				retrievedView.getViewUsers().get(FIRST).getUserId());
		assertEquals(profileViewPermissions.get(FIRST).getPermission(),
				retrievedView.getViewUsers().get(FIRST).getAccessType());
	}

	@Test
	public void importExistingUsersProfileWithViewPermissionsShoudUpdateProfileAndOverwriteUsersPermissions()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();
		User user2 = TestUtils.createUser();

		UsersProfileVO oldUsersProfile = new UsersProfileVO();
		oldUsersProfile.setName(PROFILE_NAME);
		oldUsersProfile.setXid(PROFILE_XID);

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView(VIEW_XID, 1, owner.getId());
		List<ViewAccess> oldProfileViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess oldViewPermission = new ViewAccess();
		oldViewPermission.setId(view.getId());
		oldViewPermission.setPermission(ShareUser.ACCESS_READ);
		oldProfileViewPermissions.add(oldViewPermission);
		oldUsersProfile.setViewPermissions(oldProfileViewPermissions);

		MockWatchlistDao mockWatchlistDao = new MockWatchlistDao();
		WatchList watchlist = mockWatchlistDao.createNewWatchList(
				WATCHLIST_XID, 1, owner.getId());
		List<WatchListAccess> oldProfileWatchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess oldWatchlistPermission = new WatchListAccess();
		oldWatchlistPermission.setId(watchlist.getId());
		oldWatchlistPermission.setPermission(ShareUser.ACCESS_READ);
		oldProfileWatchlistPermissions.add(oldWatchlistPermission);
		oldUsersProfile.setWatchlistPermissions(oldProfileWatchlistPermissions);

		oldUsersProfile.apply(user);
		oldUsersProfile.apply(user2);

		UserDao userDao = new UserDao();
		userDao.saveUser(user);
		userDao.saveUser(user2);
		usersProfileService.saveUsersProfile(oldUsersProfile);

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(PROFILE_NAME);
		exportedUsersProfile.setXid(PROFILE_XID);

		List<ViewAccess> newProfileViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newPermission = new ViewAccess();
		newPermission.setId(view.getId());
		newPermission.setPermission(ShareUser.ACCESS_SET);
		newProfileViewPermissions.add(newPermission);
		exportedUsersProfile.setViewPermissions(newProfileViewPermissions);

		List<WatchListAccess> newProfileWatchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess newWatchlistPermission = new WatchListAccess();
		newWatchlistPermission.setId(watchlist.getId());
		newWatchlistPermission.setPermission(ShareUser.ACCESS_SET);
		newProfileWatchlistPermissions.add(newWatchlistPermission);
		exportedUsersProfile
				.setWatchlistPermissions(newProfileWatchlistPermissions);

		DataSourceVO datasource = new MockDataSourceDao()
				.insertDataSource("dsname1");
		List<Integer> newDataSourcePermissions = new ArrayList<Integer>();
		newDataSourcePermissions.add(datasource.getId());
		exportedUsersProfile.setDataSourcePermissions(newDataSourcePermissions);

		DataPointVO datapoint = new MockDataPointDao().insertDataPoint(
				"dpname", datasource.getId());
		datapoint.setXid(DATA_POINT_XID);
		List<DataPointAccess> newDatapointPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess permissionForDp = new DataPointAccess();
		permissionForDp.setDataPointId(datapoint.getId());
		permissionForDp.setPermission(DataPointAccess.SET);
		newDatapointPermissions.add(permissionForDp);
		exportedUsersProfile.setDataPointPermissions(newDatapointPermissions);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());
		usersIds.add(user2.getId());
		exportedUsersProfile.defineUsers(usersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		User retrievedUser = new UserDao().getUser(user.getId());
		User retrievedUser2 = new UserDao().getUser(user2.getId());
		View retrievedView = new ViewDao().getViews().get(FIRST);
		WatchList retrievedWatchlist = mockWatchlistDao.getWatchList(watchlist
				.getId());
		UsersProfileVO retrievedProfile = usersProfileService
				.getUserProfileByXid(PROFILE_XID);

		assertEquals(exportedUsersProfile.getXid(), retrievedProfile.getXid());
		assertEquals(exportedUsersProfile.getName(), retrievedProfile.getName());
		assertEquals(exportedUsersProfile.getViewPermissions().size(),
				retrievedProfile.getViewPermissions().size());

		assertEquals(exportedUsersProfile.getViewPermissions().get(FIRST)
				.getId(), retrievedProfile.getViewPermissions().get(FIRST)
				.getId());

		assertEquals(exportedUsersProfile.getViewPermissions().get(FIRST)
				.getPermission(),
				retrievedProfile.getViewPermissions().get(FIRST)
						.getPermission());

		assertEquals(retrievedUser.getId(),
				retrievedView.getViewUsers().get(FIRST).getUserId());

		assertEquals(retrievedUser2.getId(),
				retrievedView.getViewUsers().get(SECOND).getUserId());

		assertEquals(newProfileViewPermissions.get(FIRST).getPermission(),
				retrievedView.getViewUsers().get(FIRST).getAccessType());

		assertEquals(retrievedUser.getId(), retrievedWatchlist
				.getWatchListUsers().get(FIRST).getUserId());

		assertEquals(retrievedUser2.getId(), retrievedWatchlist
				.getWatchListUsers().get(SECOND).getUserId());

		assertEquals(exportedUsersProfile.getWatchlistPermissions().get(FIRST)
				.getPermission(), retrievedProfile.getWatchlistPermissions()
				.get(FIRST).getPermission());

		assertTrue(datasource.getId() == retrievedUser
				.getDataSourcePermissions().get(FIRST));
		assertEquals(1, retrievedUser.getDataSourcePermissions().size());

		assertEquals(datapoint.getId(), retrievedProfile
				.getDataPointPermissions().get(FIRST).getDataPointId());
		assertEquals(DataPointAccess.SET, retrievedProfile
				.getDataPointPermissions().get(FIRST).getPermission());
		assertEquals(1, retrievedProfile.getDataPointPermissions().size());

		assertEquals(datapoint.getId(), retrievedUser.getDataPointPermissions()
				.get(FIRST).getDataPointId());
		assertEquals(newDatapointPermissions.get(FIRST).getPermission(),
				retrievedUser.getDataPointPermissions().get(FIRST)
						.getPermission());
	}

	@Test
	public void importUsersProfileWithSameNameAndDistinctXidShouldCreateNewProfileWithSameName()
			throws JsonException, DAOException {
		useScenario(new DatalessDatabaseScenario());

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView(VIEW_XID, 1, owner.getId());

		String oldProfileXid = "oldXid";
		String sameName = "profileWithSameName";

		UsersProfileVO oldProfile = new UsersProfileVO();
		oldProfile.setName(sameName);
		oldProfile.setXid(oldProfileXid);
		List<ViewAccess> oldViewPermissions = new ArrayList<ViewAccess>();
		oldViewPermissions.add(new ViewAccess(view.getId(), ViewAccess.SET));
		oldProfile.setViewPermissions(oldViewPermissions);
		usersProfileService.saveUsersProfile(oldProfile);

		oldProfile.apply(user);
		usersProfileService.updateUsersProfile(user, oldProfile);

		UsersProfileVO exportedUsersProfile = new UsersProfileVO();
		exportedUsersProfile.setName(sameName);
		exportedUsersProfile.setXid(PROFILE_XID);

		List<ViewAccess> profileViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newPermission = new ViewAccess();
		newPermission.setId(view.getId());
		newPermission.setPermission(ShareUser.ACCESS_READ);
		profileViewPermissions.add(newPermission);
		exportedUsersProfile.setViewPermissions(profileViewPermissions);

		List<Integer> emptyUsersIds = new ArrayList<Integer>();
		exportedUsersProfile.defineUsers(emptyUsersIds);

		String output = getJson(exportedUsersProfile);

		JsonReader reader = new JsonReader(output);
		JsonObject profileJson = new JsonObject(reader);
		DwrResponseI18n response = mock(DwrResponseI18n.class);
		ImportTask task = mock(ImportTask.class);

		UsersProfileImporter importer = new UsersProfileImporter();
		importer.importUsersProfile(profileJson.getJsonObject("usersProfiles"),
				response, reader, task);

		UsersProfileVO retrievedOldProfile = usersProfileService
				.getUserProfileByXid(oldProfileXid);

		UsersProfileVO retrievedImportedProfile = usersProfileService
				.getUserProfileByXid(PROFILE_XID);

		assertEquals(retrievedOldProfile.getName(),
				retrievedImportedProfile.getName());
		assertEquals(2, usersProfileService.getUsersProfiles().size());
	}
}
