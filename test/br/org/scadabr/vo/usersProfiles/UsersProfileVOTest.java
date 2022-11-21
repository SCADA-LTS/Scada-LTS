package br.org.scadabr.vo.usersProfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import br.org.scadabr.db.dao.mocks.MockDataPointDao;
import br.org.scadabr.db.dao.mocks.MockDataSourceDao;
import br.org.scadabr.db.dao.mocks.MockViewDao;
import br.org.scadabr.db.dao.mocks.MockWatchlistDao;
import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;

import com.serotonin.json.JsonArray;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonNumber;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonString;
import com.serotonin.json.JsonWriter;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.web.dwr.EmportDwr;

public class UsersProfileVOTest {//extends AbstractMySQLDependentTest {

	private static final int FIRST = 0;
	private static final int SECOND = 1;

	@Test
	public void userProfileHasAName() {
		String name = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(name);
		assertEquals(name, profile.getName());
	}

	@Test
	public void userProfileHasID() {
		int id = 1;
		UsersProfileVO profile = new UsersProfileVO();
		profile.setId(id);
		assertEquals(id, profile.getId());
	}

	@Test
	public void userProfileHasDataSourcePermissionsIds() {
		UsersProfileVO profile = new UsersProfileVO();
		List<Integer> dataSourcePermissions = new ArrayList<Integer>();
		dataSourcePermissions.add(1);
		dataSourcePermissions.add(2);
		profile.setDataSourcePermissions(dataSourcePermissions);
		assertEquals(dataSourcePermissions, profile.getDataSourcePermissions());
	}

	@Test
	public void userProfileHasDataPointPermissions() {
		UsersProfileVO profile = new UsersProfileVO();

		List<DataPointAccess> dataPointPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess accessPermissions = new DataPointAccess();
		accessPermissions.setDataPointId(1);
		accessPermissions.setPermission(DataPointAccess.READ);
		dataPointPermissions.add(accessPermissions);

		profile.setDataPointPermissions(dataPointPermissions);
		assertEquals(dataPointPermissions, profile.getDataPointPermissions());
	}

	@Test
	public void userProfileHasPermitedWatchlists() {
		UsersProfileVO profile = new UsersProfileVO();
		List<WatchListAccess> watchlistPermissions = new ArrayList<WatchListAccess>();
		watchlistPermissions.add(new WatchListAccess(1, ShareUser.ACCESS_SET));
		profile.setWatchlistPermissions(watchlistPermissions);
		assertEquals(watchlistPermissions, profile.getWatchlistPermissions());
	}

	@Test
	public void userProfileHasPermitedViews() {
		UsersProfileVO profile = new UsersProfileVO();
		List<ViewAccess> viewPermissions = new ArrayList<ViewAccess>();
		viewPermissions.add(new ViewAccess(1, ShareUser.ACCESS_SET));
		profile.setViewPermissions(viewPermissions);
		assertEquals(viewPermissions, profile.getViewPermissions());
	}

	@Test
	public void applyShoudUpdateUserDatasourcePermissions() {
		UsersProfileVO profile = new UsersProfileVO();
		List<Integer> profileDataSourcePermissions = new ArrayList<Integer>();
		profileDataSourcePermissions.add(1);
		profileDataSourcePermissions.add(2);
		profile.setDataSourcePermissions(profileDataSourcePermissions);

		List<Integer> userDataSourcePermissions = new ArrayList<Integer>();

		User user = new User();
		user.setDataSourcePermissions(userDataSourcePermissions);

		profile.apply(user);
		assertEquals(profileDataSourcePermissions,
				user.getDataSourcePermissions());
	}

	@Test
	public void applyShoudUpdateUserDatapointPermissions() {
		UsersProfileVO profile = new UsersProfileVO();

		List<DataPointAccess> profileDataPointPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess accessPermissions = new DataPointAccess();
		accessPermissions.setDataPointId(1);
		accessPermissions.setPermission(DataPointAccess.READ);
		profileDataPointPermissions.add(accessPermissions);

		profile.setDataPointPermissions(profileDataPointPermissions);

		List<DataPointAccess> userDataPointPermissions = new ArrayList<DataPointAccess>();

		User user = new User();
		user.setDataPointPermissions(userDataPointPermissions);

		profile.apply(user);
		assertEquals(profileDataPointPermissions,
				user.getDataPointPermissions());
	}

	@Test
	public void applyShoudOverwriteUserDatasourcePermissions() {
		UsersProfileVO profile = new UsersProfileVO();
		List<Integer> profileDataSourcePermissions = new ArrayList<Integer>();
		profileDataSourcePermissions.add(1);
		profileDataSourcePermissions.add(2);
		profile.setDataSourcePermissions(profileDataSourcePermissions);

		List<Integer> userDataSourcePermissions = new ArrayList<Integer>();
		userDataSourcePermissions.add(1);
		userDataSourcePermissions.add(3);

		User user = new User();
		user.setDataSourcePermissions(userDataSourcePermissions);

		profile.apply(user);
		assertEquals(profileDataSourcePermissions,
				user.getDataSourcePermissions());
	}

	@Test
	public void applyShoudOverwriteUserDatapointPermissions() {
		UsersProfileVO profile = new UsersProfileVO();

		List<DataPointAccess> profileDataPointPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess accessPermissions = new DataPointAccess();
		accessPermissions.setDataPointId(1);
		accessPermissions.setPermission(DataPointAccess.READ);
		profileDataPointPermissions.add(accessPermissions);

		profile.setDataPointPermissions(profileDataPointPermissions);

		List<DataPointAccess> userDataPointPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess userAccessPermissions = new DataPointAccess();
		accessPermissions.setDataPointId(1);
		accessPermissions.setPermission(DataPointAccess.SET);
		profileDataPointPermissions.add(userAccessPermissions);

		User user = new User();
		user.setDataPointPermissions(userDataPointPermissions);

		profile.apply(user);
		assertEquals(profileDataPointPermissions,
				user.getDataPointPermissions());
	}

	@Test
	public void applyShoudUpdateLastAppliedUser() {
		UsersProfileVO profile = new UsersProfileVO();
		User user = new User();
		profile.apply(user);
		assertEquals(user, profile.retrieveLastAppliedUser());
	}

	@Test
	public void applyShoudUpdateUserProfle() {
		UsersProfileVO profile = new UsersProfileVO();
		User user = new User();
		profile.apply(user);
		assertEquals(profile.getId(), user.getUserProfile());
	}

	@Test
	public void applyShoudOverwriteWatchlistUserSharePermissions() {
		User user = new User();
		user.setId(1);
		List<ShareUser> oldUserPermissions = new ArrayList<ShareUser>();

		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		oldUserPermissions.add(permission);

		WatchList watchlist = new WatchList();
		watchlist.setWatchListUsers(oldUserPermissions);
		watchlist.setId(34);

		List<WatchList> watchlists = new ArrayList<WatchList>();
		watchlists.add(watchlist);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineWatchlists(watchlists);

		List<WatchListAccess> newWatchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess newWatchlistAccessPermission = new WatchListAccess();
		newWatchlistAccessPermission.setId(watchlist.getId());
		newWatchlistAccessPermission.setPermission(ShareUser.ACCESS_SET);
		newWatchlistPermissions.add(newWatchlistAccessPermission);

		profile.setWatchlistPermissions(newWatchlistPermissions);

		profile.apply(user);

		assertEquals(ShareUser.ACCESS_SET,
				watchlist.getWatchListUsers().get(FIRST).getAccessType());
		assertEquals(user.getId(), watchlist.getWatchListUsers().get(FIRST)
				.getUserId());
	}

	@Test
	public void applyShoudIncludePermissionsForUsersThatDidNotHaveAccessToTheWatchlist() {
		User user = new User();
		user.setId(1);

		User user2 = new User();
		user.setId(2);

		List<ShareUser> oldUserPermissions = new ArrayList<ShareUser>();
		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		oldUserPermissions.add(permission);

		WatchList watchlist = new WatchList();
		watchlist.setWatchListUsers(oldUserPermissions);
		watchlist.setId(34);

		List<WatchList> watchlists = new ArrayList<WatchList>();
		watchlists.add(watchlist);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineWatchlists(watchlists);

		List<WatchListAccess> newWatchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess newWatchlistAccessPermission = new WatchListAccess();
		newWatchlistAccessPermission.setId(watchlist.getId());
		newWatchlistAccessPermission.setPermission(ShareUser.ACCESS_SET);
		newWatchlistPermissions.add(newWatchlistAccessPermission);

		profile.setWatchlistPermissions(newWatchlistPermissions);

		profile.apply(user2);

		assertEquals(ShareUser.ACCESS_SET,
				watchlist.getWatchListUsers().get(SECOND).getAccessType());
		assertEquals(user2.getId(), watchlist.getWatchListUsers().get(SECOND)
				.getUserId());
	}

	@Test
	//@Ignore("")
	public void applyShoudNotUpdateWatchlistPermissionsForUsersThatDontHaveThisProfile() {
		User user = new User();
		user.setId(1);

		User user2 = new User();
		user2.setId(2);

		List<ShareUser> permissions = new ArrayList<ShareUser>();
		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		permissions.add(permission);

		ShareUser permission2 = new ShareUser();
		permission2.setUserId(user2.getId());
		permission2.setAccessType(ShareUser.ACCESS_READ);
		permissions.add(permission2);

		WatchList watchlist = new WatchList();
		watchlist.setWatchListUsers(permissions);
		watchlist.setId(34);

		List<WatchList> watchlists = new ArrayList<WatchList>();
		watchlists.add(watchlist);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineWatchlists(watchlists);

		List<WatchListAccess> newWatchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess newWatchlistAccessPermission = new WatchListAccess();
		newWatchlistAccessPermission.setId(watchlist.getId());
		newWatchlistAccessPermission.setPermission(ShareUser.ACCESS_SET);
		newWatchlistPermissions.add(newWatchlistAccessPermission);

		profile.setWatchlistPermissions(newWatchlistPermissions);
		profile.apply(user2);

		assertEquals(ShareUser.ACCESS_READ,
				watchlist.getWatchListUsers().get(FIRST).getAccessType());
		assertEquals(user.getId(), watchlist.getWatchListUsers().get(FIRST)
				.getUserId());

		assertEquals(ShareUser.ACCESS_SET,
				watchlist.getWatchListUsers().get(SECOND).getAccessType());
		assertEquals(user2.getId(), watchlist.getWatchListUsers().get(SECOND)
				.getUserId());
	}

	@Test
	public void applyShoudUpdateWatchlistPermissionsForUsersThatHaveThisProfile() {
		User user = new User();
		user.setId(1);

		User user2 = new User();
		user2.setId(2);

		List<ShareUser> permissions = new ArrayList<ShareUser>();
		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		permissions.add(permission);

		ShareUser permission2 = new ShareUser();
		permission2.setUserId(user2.getId());
		permission2.setAccessType(ShareUser.ACCESS_READ);
		permissions.add(permission2);

		WatchList watchlist = new WatchList();
		watchlist.setWatchListUsers(permissions);
		watchlist.setId(34);

		List<WatchList> watchlists = new ArrayList<WatchList>();
		watchlists.add(watchlist);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineWatchlists(watchlists);

		List<WatchListAccess> newWatchlistPermissions = new ArrayList<WatchListAccess>();
		WatchListAccess newWatchlistAccessPermission = new WatchListAccess();
		newWatchlistAccessPermission.setId(watchlist.getId());
		newWatchlistAccessPermission.setPermission(ShareUser.ACCESS_SET);
		newWatchlistPermissions.add(newWatchlistAccessPermission);

		profile.setWatchlistPermissions(newWatchlistPermissions);
		List<Integer> users = new ArrayList<Integer>();
		users.add(user.getId());
		users.add(user2.getId());
		profile.defineUsers(users);
		profile.apply(user2);

		assertEquals(ShareUser.ACCESS_READ,
				watchlist.getWatchListUsers().get(FIRST).getAccessType());
		assertEquals(user.getId(), watchlist.getWatchListUsers().get(FIRST)
				.getUserId());

		assertEquals(ShareUser.ACCESS_SET,
				watchlist.getWatchListUsers().get(SECOND).getAccessType());
		assertEquals(user2.getId(), watchlist.getWatchListUsers().get(SECOND)
				.getUserId());
	}

	@Test
	public void applyShoudOverwriteViewUserSharePermissions() {
		User user = new User();
		user.setId(1);
		List<ShareUser> oldUserPermissions = new ArrayList<ShareUser>();

		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		oldUserPermissions.add(permission);

		View view = new View();
		view.setViewUsers(oldUserPermissions);
		view.setId(34);

		List<View> views = new ArrayList<View>();
		views.add(view);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineViews(views);

		List<ViewAccess> newViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newViewAccessPermission = new ViewAccess();
		newViewAccessPermission.setId(view.getId());
		newViewAccessPermission.setPermission(ShareUser.ACCESS_SET);
		newViewPermissions.add(newViewAccessPermission);

		profile.setViewPermissions(newViewPermissions);

		profile.apply(user);

		assertEquals(ShareUser.ACCESS_SET, view.getViewUsers().get(FIRST)
				.getAccessType());
		assertEquals(user.getId(), view.getViewUsers().get(FIRST).getUserId());
	}

	@Test
	public void applyShoudIncludePermissionsForUsersThatDidNotHaveAccessToTheView() {
		User user = new User();
		user.setId(1);

		User user2 = new User();
		user.setId(2);

		List<ShareUser> oldUserPermissions = new ArrayList<ShareUser>();
		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		oldUserPermissions.add(permission);

		View view = new View();
		view.setViewUsers(oldUserPermissions);
		view.setId(34);

		List<View> views = new ArrayList<View>();
		views.add(view);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineViews(views);

		List<ViewAccess> newViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newViewAccessPermission = new ViewAccess();
		newViewAccessPermission.setId(view.getId());
		newViewAccessPermission.setPermission(ShareUser.ACCESS_SET);
		newViewPermissions.add(newViewAccessPermission);

		profile.setViewPermissions(newViewPermissions);

		profile.apply(user2);

		assertEquals(ShareUser.ACCESS_SET, view.getViewUsers().get(SECOND)
				.getAccessType());
		assertEquals(user2.getId(), view.getViewUsers().get(SECOND).getUserId());
	}

	@Test
	public void applyShoudNotUpdateViewPermissionsForUsersThatDontHaveThisProfile() {
		User user = new User();
		user.setId(1);

		User user2 = new User();
		user2.setId(2);

		List<ShareUser> permissions = new ArrayList<ShareUser>();
		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		permissions.add(permission);

		ShareUser permission2 = new ShareUser();
		permission2.setUserId(user2.getId());
		permission2.setAccessType(ShareUser.ACCESS_READ);
		permissions.add(permission2);

		View view = new View();
		view.setViewUsers(permissions);
		view.setId(34);

		List<View> views = new ArrayList<View>();
		views.add(view);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineViews(views);

		List<ViewAccess> newViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newViewAccessPermission = new ViewAccess();
		newViewAccessPermission.setId(view.getId());
		newViewAccessPermission.setPermission(ShareUser.ACCESS_SET);
		newViewPermissions.add(newViewAccessPermission);

		profile.setViewPermissions(newViewPermissions);

		profile.apply(user2);

		assertEquals(ShareUser.ACCESS_READ, view.getViewUsers().get(FIRST)
				.getAccessType());
		assertEquals(user.getId(), view.getViewUsers().get(FIRST).getUserId());

		assertEquals(ShareUser.ACCESS_SET, view.getViewUsers().get(SECOND)
				.getAccessType());
		assertEquals(user2.getId(), view.getViewUsers().get(SECOND).getUserId());
	}

	@Test
	public void applyShoudUpdateViewPermissionsForUsersThatHaveThisProfile() {
		User user = new User();
		user.setId(1);

		User user2 = new User();
		user2.setId(2);

		List<ShareUser> permissions = new ArrayList<ShareUser>();
		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		permissions.add(permission);

		ShareUser permission2 = new ShareUser();
		permission2.setUserId(user2.getId());
		permission2.setAccessType(ShareUser.ACCESS_READ);
		permissions.add(permission2);

		View view = new View();
		view.setViewUsers(permissions);
		view.setId(34);

		List<View> views = new ArrayList<View>();
		views.add(view);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineViews(views);

		List<ViewAccess> newViewPermissions = new ArrayList<ViewAccess>();
		ViewAccess newViewAccessPermission = new ViewAccess();
		newViewAccessPermission.setId(view.getId());
		newViewAccessPermission.setPermission(ShareUser.ACCESS_SET);
		newViewPermissions.add(newViewAccessPermission);

		profile.setViewPermissions(newViewPermissions);

		List<Integer> users = new ArrayList<Integer>();
		users.add(user.getId());
		users.add(user2.getId());
		profile.defineUsers(users);
		profile.apply(user2);

		assertEquals(ShareUser.ACCESS_READ, view.getViewUsers().get(FIRST)
				.getAccessType());
		assertEquals(user.getId(), view.getViewUsers().get(FIRST).getUserId());

		assertEquals(ShareUser.ACCESS_SET, view.getViewUsers().get(SECOND)
				.getAccessType());
		assertEquals(user2.getId(), view.getViewUsers().get(SECOND).getUserId());
	}

	@Test
	public void applyShoudIncludeNonePermissionsForUsersIfWatchlistPermissionsListIsEmpty() {
		User user = new User();
		user.setId(1);

		User user2 = new User();
		user2.setId(2);

		List<ShareUser> oldUserPermissions = new ArrayList<ShareUser>();
		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		oldUserPermissions.add(permission);

		WatchList watchlist = new WatchList();
		watchlist.setWatchListUsers(oldUserPermissions);
		watchlist.setId(34);

		List<WatchList> watchlists = new ArrayList<WatchList>();
		watchlists.add(watchlist);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineWatchlists(watchlists);

		List<WatchListAccess> newWatchlistPermissions = new ArrayList<WatchListAccess>();
		profile.setWatchlistPermissions(newWatchlistPermissions);

		List<Integer> users = new ArrayList<Integer>();
		users.add(user.getId());
		users.add(user2.getId());
		profile.defineUsers(users);
		profile.apply(user);

		assertEquals(ShareUser.ACCESS_NONE,
				watchlist.getWatchListUsers().get(FIRST).getAccessType());
		assertEquals(user.getId(), watchlist.getWatchListUsers().get(FIRST)
				.getUserId());

		assertEquals(ShareUser.ACCESS_NONE,
				watchlist.getWatchListUsers().get(SECOND).getAccessType());
		assertEquals(user2.getId(), watchlist.getWatchListUsers().get(SECOND)
				.getUserId());
	}

	@Test
	public void applyShoudIncludeNonePermissionsForUsersIfViewPermissionsListIsEmpty() {
		User user = new User();
		user.setId(1);

		User user2 = new User();
		user2.setId(2);

		List<ShareUser> oldUserPermissions = new ArrayList<ShareUser>();
		ShareUser permission = new ShareUser();
		permission.setUserId(user.getId());
		permission.setAccessType(ShareUser.ACCESS_READ);
		oldUserPermissions.add(permission);

		View view = new View();
		view.setViewUsers(oldUserPermissions);
		view.setId(34);

		List<View> views = new ArrayList<View>();
		views.add(view);

		UsersProfileVO profile = new UsersProfileVO();
		profile.defineViews(views);

		List<ViewAccess> newViewPermissions = new ArrayList<ViewAccess>();
		profile.setViewPermissions(newViewPermissions);

		List<Integer> users = new ArrayList<Integer>();
		users.add(user.getId());
		users.add(user2.getId());
		profile.defineUsers(users);
		profile.apply(user);

		assertEquals(ShareUser.ACCESS_NONE, view.getViewUsers().get(FIRST)
				.getAccessType());
		assertEquals(user.getId(), view.getViewUsers().get(FIRST).getUserId());

		assertEquals(ShareUser.ACCESS_NONE, view.getViewUsers().get(SECOND)
				.getAccessType());
		assertEquals(user2.getId(), view.getViewUsers().get(SECOND).getUserId());
	}

	@Test
	public void serializeShouldGenerateJsonWithUserProfileNameIdAndXid() {
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName("name");
		profile.setXid("profileXid");

		List<UsersProfileVO> profiles = new ArrayList<UsersProfileVO>();
		profiles.add(profile);

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put(EmportDwr.USERS_PROFILES, profiles);

		JsonWriter writer = new JsonWriter();
		writer.setPrettyOutput(true);

		String saida = "";

		try {
			saida = writer.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonReader reader = new JsonReader(saida);
		try {
			JsonObject raiz = (JsonObject) reader.inflate();
			JsonArray profilejson = (JsonArray) raiz.getValue("usersProfiles");
			JsonObject o = (JsonObject) profilejson.getElements().get(FIRST);

			JsonString name = (JsonString) o.getProperties().get("name");
			assertEquals(profile.getName(), name.getValue());

			JsonString xid = (JsonString) o.getProperties().get("xid");
			assertEquals(profile.getXid(), xid.getValue());

			JsonNumber id = (JsonNumber) o.getProperties().get("id");
			assertEquals(new Integer(profile.getId()), id.getIntValue());

		} catch (JsonException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void serializeShouldGenerateJsonWithDataSourcePermission() {
		UsersProfileVO profile = new UsersProfileVO();
		int dataSourceId = 1;
		List<Integer> dataSourcePermissions = new ArrayList<Integer>();
		dataSourcePermissions.add(dataSourceId);
		profile.setDataSourcePermissions(dataSourcePermissions);

		List<UsersProfileVO> profiles = new ArrayList<UsersProfileVO>();
		profiles.add(profile);

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put(EmportDwr.USERS_PROFILES, profiles);

		JsonWriter writer = new JsonWriter();
		writer.setPrettyOutput(true);

		String saida = "";

		try {
			saida = writer.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonReader reader = new JsonReader(saida);
		try {
			JsonObject raiz = (JsonObject) reader.inflate();
			JsonArray profilejson = (JsonArray) raiz.getValue("usersProfiles");
			JsonObject o = (JsonObject) profilejson.getElements().get(FIRST);

			assertTrue(o.getProperties().containsKey("dataSourcePermissions"));
			JsonArray dataSourcePermissionsJson = (JsonArray) o.getProperties()
					.get("dataSourcePermissions");
			dataSourcePermissionsJson.getElements().get(FIRST)
					.equals(dataSourceId);

		} catch (JsonException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Ignore
	public void serializeShouldGenerateJsonWithDataPointPermission() {
		//useScenario(new DatalessDatabaseScenario());

		UsersProfileVO profile = new UsersProfileVO();

		DataSourceVO ds = new MockDataSourceDao().insertDataSource("dsname");
		String dpXid = "dpXid";
		DataPointVO dp = new MockDataPointDao().insertDataPoint(dpXid,
				ds.getId());

		int dataPointId = dp.getId();
		List<DataPointAccess> dataPointPermissions = new ArrayList<DataPointAccess>();
		DataPointAccess accessPermissions = new DataPointAccess();
		accessPermissions.setDataPointId(dataPointId);
		accessPermissions.setPermission(DataPointAccess.READ);
		dataPointPermissions.add(accessPermissions);

		profile.setDataPointPermissions(dataPointPermissions);

		List<UsersProfileVO> profiles = new ArrayList<UsersProfileVO>();
		profiles.add(profile);

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put(EmportDwr.USERS_PROFILES, profiles);

		JsonWriter writer = new JsonWriter();
		writer.setPrettyOutput(true);

		String saida = "";

		try {
			saida = writer.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonReader reader = new JsonReader(saida);
		try {
			JsonObject raiz = (JsonObject) reader.inflate();
			JsonArray profilejson = (JsonArray) raiz.getValue("usersProfiles");
			JsonObject o = (JsonObject) profilejson.getElements().get(FIRST);

			assertTrue(o.getProperties().containsKey("dataPointPermissions"));
			JsonArray dataPointPermissionsJson = (JsonArray) o.getProperties()
					.get("dataPointPermissions");
			JsonObject permission = (JsonObject) dataPointPermissionsJson
					.getElements().get(FIRST);
			assertEquals(dpXid,
					((JsonString) permission.getProperties()
							.get("dataPointXid")).getValue());
			assertEquals("READ",
					((JsonString) permission.getProperties().get("permission"))
							.getValue());

		} catch (JsonException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Ignore
	public void serializeShouldGenerateJsonWithViewPermission() {
		//useScenario(new DatalessDatabaseScenario());

		User user = TestUtils.createUser();

		MockViewDao mockViewDao = new MockViewDao();
		String viewXid = "viewXid";
		View view = mockViewDao.createView(viewXid, 1, user.getId());

		String profileName = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(profileName);

		List<ViewAccess> permissions = new ArrayList<ViewAccess>();
		permissions.add(new ViewAccess(view.getId(), ShareUser.ACCESS_SET));
		profile.setViewPermissions(permissions);

		List<UsersProfileVO> profiles = new ArrayList<UsersProfileVO>();
		profiles.add(profile);

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put(EmportDwr.USERS_PROFILES, profiles);

		JsonWriter writer = new JsonWriter();
		writer.setPrettyOutput(true);

		String saida = "";

		try {
			saida = writer.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonReader reader = new JsonReader(saida);
		try {
			JsonObject raiz = (JsonObject) reader.inflate();
			JsonArray profilejson = (JsonArray) raiz.getValue("usersProfiles");
			JsonObject o = (JsonObject) profilejson.getElements().get(FIRST);

			assertTrue(o.getProperties().containsKey("viewPermissions"));
			JsonArray viewPermissionsJson = (JsonArray) o.getProperties().get(
					"viewPermissions");
			JsonObject permission = (JsonObject) viewPermissionsJson
					.getElements().get(FIRST);
			assertEquals(viewXid,
					((JsonString) permission.getProperties().get("viewXid"))
							.getValue());
			assertEquals("SET",
					((JsonString) permission.getProperties().get("permission"))
							.getValue());

		} catch (JsonException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Ignore
	public void serializeShouldGenerateJsonWithWatchlistdPermission() {
		//useScenario(new DatalessDatabaseScenario());
		User user = TestUtils.createUser();

		String profileName = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(profileName);

		String watchlistXid = "wlXid";
		WatchList wl = new MockWatchlistDao().createNewWatchList(watchlistXid,
				1, user.getId());

		List<WatchList> watchlists = new ArrayList<WatchList>();
		watchlists.add(wl);

		List<WatchListAccess> permissions = new ArrayList<WatchListAccess>();
		permissions.add(new WatchListAccess(wl.getId(), ShareUser.ACCESS_SET));
		profile.setWatchlistPermissions(permissions);
		profile.defineWatchlists(watchlists);

		List<UsersProfileVO> profiles = new ArrayList<UsersProfileVO>();
		profiles.add(profile);

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put(EmportDwr.USERS_PROFILES, profiles);

		JsonWriter writer = new JsonWriter();
		writer.setPrettyOutput(true);

		String saida = "";

		try {
			saida = writer.write(data);
			System.out.println(saida);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonReader reader = new JsonReader(saida);
		try {
			JsonObject raiz = (JsonObject) reader.inflate();
			JsonArray profilejson = (JsonArray) raiz.getValue("usersProfiles");
			JsonObject o = (JsonObject) profilejson.getElements().get(FIRST);

			assertTrue(o.getProperties().containsKey("watchlistPermissions"));
			JsonArray viewPermissionsJson = (JsonArray) o.getProperties().get(
					"watchlistPermissions");
			JsonObject permission = (JsonObject) viewPermissionsJson
					.getElements().get(FIRST);
			assertEquals(watchlistXid, ((JsonString) permission.getProperties()
					.get("watchlistXid")).getValue());
			assertEquals("SET",
					((JsonString) permission.getProperties().get("permission"))
							.getValue());

		} catch (JsonException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Ignore
	public void serializeShouldGenerateJsonWithAppliedUsersIds() {
		//useScenario(new DatalessDatabaseScenario());
		User user = TestUtils.createUser();

		String profileName = "name";
		UsersProfileVO profile = new UsersProfileVO();
		profile.setName(profileName);

		List<Integer> usersIds = new ArrayList<Integer>();
		usersIds.add(user.getId());

		profile.defineUsers(usersIds);

		List<UsersProfileVO> profiles = new ArrayList<UsersProfileVO>();
		profiles.add(profile);

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put(EmportDwr.USERS_PROFILES, profiles);

		JsonWriter writer = new JsonWriter();
		writer.setPrettyOutput(true);

		String saida = "";

		try {
			saida = writer.write(data);
			System.out.println(saida);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonReader reader = new JsonReader(saida);
		try {
			JsonObject raiz = (JsonObject) reader.inflate();
			JsonArray profilejson = (JsonArray) raiz.getValue("usersProfiles");
			JsonObject o = (JsonObject) profilejson.getElements().get(FIRST);

			assertTrue(o.getProperties().containsKey("usersIds"));
			JsonArray usersIdsJson = (JsonArray) o.getProperties().get(
					"usersIds");
			usersIdsJson.getElements().get(FIRST).equals(user.getId());

		} catch (JsonException e) {
			e.printStackTrace();
			fail();
		}

	}
}
