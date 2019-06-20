package br.org.scadabr.vo.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.db.dao.UsersProfileDao;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

import com.serotonin.json.JsonArray;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonValue;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.web.dwr.beans.ImportTask;
import com.serotonin.web.dwr.DwrResponseI18n;

public class UsersProfileImporter {

	private Map<Integer, ShareUser> oldViewPermissions;
	private Map<Integer, ShareUser> oldWatchlistPermissions;
	private List<Integer> oldDataSourcePermissions;
	private ArrayList<DataPointAccess> oldDataPointPermissions;
	private UsersProfileDao usersProfileDao;

	public UsersProfileImporter() {
		usersProfileDao = new UsersProfileDao();
		oldViewPermissions = new HashMap<Integer, ShareUser>();
		oldWatchlistPermissions = new HashMap<Integer, ShareUser>();
	}

	public void importUsersProfile(JsonObject profileJson,
			DwrResponseI18n response, JsonReader reader, ImportTask task)
			throws DAOException, JsonException {

		UsersProfileVO newProfile = new UsersProfileVO();
		newProfile.setXid(profileJson.getString("xid"));
		newProfile.setName(profileJson.getString("name"));

		newProfile
				.setDataSourcePermissions(getDataSourcePermissions(profileJson));
		newProfile.setDataPointPermissions(getDataPointPermissions(profileJson,
				reader));
		newProfile.setViewPermissions(getViewPermissions(profileJson, reader));
		newProfile.setWatchlistPermissions(getWatchlistPermissions(profileJson,
				reader));

		createOrUpdateProfile(newProfile);

		UserDao usersDao = new UserDao();
		UsersProfileVO savedProfile = usersProfileDao
				.getUserProfileByXid(newProfile.getXid());

		for (User user : getUsers(profileJson)) {
			copyUsersOldAdditionalPermissions(user, savedProfile);
			savedProfile.apply(user);
			usersDao.saveUser(user);
			usersProfileDao.updateUsersProfile(savedProfile);
			restoreUsersOldAdditionalPermissions(user, savedProfile);
		}

	}

	private void createOrUpdateProfile(UsersProfileVO profile)
			throws DAOException {

		if (usersProfileDao.userProfileExists(profile.getXid())) {
			UsersProfileVO savedProfile = usersProfileDao
					.getUserProfileByXid(profile.getXid());
			profile.setId(savedProfile.getId());
			usersProfileDao.updateProfile(profile);
		} else {
			usersProfileDao.saveUsersProfileWithoutNameConstraint(profile);
		}

	}

	private void copyUsersOldAdditionalPermissions(User user,
			UsersProfileVO profile) {

		copyViewPermissions(user, profile);
		copyWatchlistPermissions(user, profile);
		copyDatasourcePermissions(user, profile);
		copyDatapointPermissions(user, profile);
	}

	private void copyDatasourcePermissions(User user, UsersProfileVO profile) {
		oldDataSourcePermissions = new ArrayList<Integer>();

		for (Integer datasourceId : user.getDataSourcePermissions()) {
			oldDataSourcePermissions.add(datasourceId);
		}
	}

	private void copyDatapointPermissions(User user, UsersProfileVO profile) {
		oldDataPointPermissions = new ArrayList<DataPointAccess>();

		for (DataPointAccess oldPermission : user.getDataPointPermissions()) {
			oldDataPointPermissions.add(oldPermission);
		}
	}

	private void copyViewPermissions(User user, UsersProfileVO profile) {
		for (View view : profile.retrieveViews()) {

			for (ShareUser oldPermission : view.getViewUsers()) {

				if (user.getId() == oldPermission.getUserId()) {
					ShareUser oldPermissionCopy = copyPermission(view.getId(),
							oldPermission);
					oldViewPermissions.put(view.getId(), oldPermissionCopy);
				}

			}

		}
	}

	private void copyWatchlistPermissions(User user, UsersProfileVO profile) {
		for (WatchList watchlist : profile.retrieveWatchlists()) {

			for (ShareUser oldPermission : watchlist.getWatchListUsers()) {

				if (user.getId() == oldPermission.getUserId()) {
					ShareUser oldPermissionCopy = copyPermission(
							watchlist.getId(), oldPermission);
					oldWatchlistPermissions.put(watchlist.getId(),
							oldPermissionCopy);
				}

			}

		}
	}

	private ShareUser copyPermission(int id, ShareUser oldPermission) {
		ShareUser oldPermissionCopy = new ShareUser();
		oldPermissionCopy.setUserId(oldPermission.getUserId());
		oldPermissionCopy.setAccessType(oldPermission.getAccessType());
		return oldPermissionCopy;
	}

	private void restoreUsersOldAdditionalPermissions(User user,
			UsersProfileVO profile) {
		restoreOldViewPermissions(profile);
		restoreOldWatchlistPermissions(profile);
		restoreOldDatasourcePermissions(profile, user);
		restoreOldDatapointPermissions(profile, user);
	}

	private void restoreOldViewPermissions(UsersProfileVO profile) {
		for (Integer viewId : oldViewPermissions.keySet()) {

			boolean shouldUpdateView = false;
			for (ViewAccess newViewPermissions : profile.getViewPermissions()) {
				if (newViewPermissions.getId() == viewId
						&& newViewPermissions.getPermission() == ShareUser.ACCESS_NONE) {

					shouldUpdateView = true;

					ViewDao viewDao = new ViewDao();
					View viewToUpdate = viewDao.getView(viewId);

					for (Iterator<ShareUser> iterator = viewToUpdate
							.getViewUsers().iterator(); iterator.hasNext();) {
						ShareUser viewPermission = iterator.next();

						if (viewPermission.getUserId() == oldViewPermissions
								.get(viewId).getUserId()) {
							int oldPermission = oldViewPermissions.get(viewId)
									.getAccessType();

							if (viewPermission.getAccessType() == ShareUser.ACCESS_NONE) {
								viewPermission.setAccessType(oldPermission);
							}
						}

					}

					if (shouldUpdateView) {
						viewDao.saveView(viewToUpdate);
					}

				}
			}
		}
	}

	private void restoreOldWatchlistPermissions(UsersProfileVO profile) {
		for (Integer watchlistId : oldWatchlistPermissions.keySet()) {

			boolean shouldUpdateWatchlist = false;
			for (WatchListAccess newWatchlistPermissions : profile
					.getWatchlistPermissions()) {
				if (newWatchlistPermissions.getId() == watchlistId
						&& newWatchlistPermissions.getPermission() == ShareUser.ACCESS_NONE) {

					shouldUpdateWatchlist = true;

					WatchListDao watchlistDao = new WatchListDao();
					WatchList watchlistToUpdate = watchlistDao
							.getWatchList(watchlistId);

					for (Iterator<ShareUser> iterator = watchlistToUpdate
							.getWatchListUsers().iterator(); iterator.hasNext();) {
						ShareUser watchlistPermission = iterator.next();

						if (watchlistPermission.getUserId() == oldWatchlistPermissions
								.get(watchlistId).getUserId()) {
							int oldPermission = oldWatchlistPermissions.get(
									watchlistId).getAccessType();

							if (watchlistPermission.getAccessType() == ShareUser.ACCESS_NONE) {
								watchlistPermission
										.setAccessType(oldPermission);
							}
						}

					}

					if (shouldUpdateWatchlist) {
						watchlistDao.saveWatchList(watchlistToUpdate);
					}

				}
			}
		}
	}

	private void restoreOldDatasourcePermissions(UsersProfileVO profile,
			User user) {

		for (int dataSourceId : oldDataSourcePermissions) {

			boolean dataSourceFound = false;
			for (int profileDataSourcePermission : profile
					.getDataSourcePermissions()) {

				if (profileDataSourcePermission == dataSourceId) {
					dataSourceFound = true;
				}
			}

			if (!dataSourceFound) {
				List<Integer> newDataSourcePermissions = new ArrayList<Integer>();
				newDataSourcePermissions
						.addAll(user.getDataSourcePermissions());
				newDataSourcePermissions.add(dataSourceId);
				user.setDataSourcePermissions(newDataSourcePermissions);
				new UserDao().saveUser(user);
			}

		}

	}

	private void restoreOldDatapointPermissions(UsersProfileVO profile,
			User user) {

		for (DataPointAccess oldPermission : oldDataPointPermissions) {

			boolean dataPointFound = false;
			for (DataPointAccess newDataPointPermission : profile
					.getDataPointPermissions()) {

				if (newDataPointPermission.getDataPointId() == oldPermission
						.getDataPointId()) {
					dataPointFound = true;
				}
			}

			if (!dataPointFound) {
				List<DataPointAccess> newDataPointPermissions = new ArrayList<DataPointAccess>();
				newDataPointPermissions.addAll(user.getDataPointPermissions());
				newDataPointPermissions.add(oldPermission);
				user.setDataPointPermissions(newDataPointPermissions);
				new UserDao().saveUser(user);
			}

		}

	}

	private List<Integer> getDataSourcePermissions(JsonObject profileJson)
			throws JsonException {
		List<Integer> dataSourcePermissions = new ArrayList<Integer>();

		JsonArray jsonDataSources = profileJson
				.getJsonArray("dataSourcePermissions");

		if (jsonDataSources != null) {
			for (JsonValue jv : jsonDataSources.getElements()) {
				int id = jv.toJsonNumber().getIntValue();
				dataSourcePermissions.add(id);
			}
		}
		return dataSourcePermissions;
	}

	private List<DataPointAccess> getDataPointPermissions(
			JsonObject profileJson, JsonReader reader) throws JsonException {
		JsonArray jsonPoints = profileJson.getJsonArray("dataPointPermissions");
		List<DataPointAccess> dataPointPermissions = new ArrayList<DataPointAccess>();
		List<Integer> permittedPoints = new ArrayList<Integer>();

		for (JsonValue jv : jsonPoints.getElements()) {
			DataPointAccess access = reader.readPropertyValue(jv,
					DataPointAccess.class, null);

			if (!permittedPoints.contains(access.getDataPointId())) {
				dataPointPermissions.add(access);
				permittedPoints.add(access.getDataPointId());
			}

		}
		return dataPointPermissions;
	}

	private List<ViewAccess> getViewPermissions(JsonObject profileJson,
			JsonReader reader) throws JsonException {
		List<ViewAccess> viewPermissions = new ArrayList<ViewAccess>();
		List<Integer> permittedViews = new ArrayList<Integer>();

		JsonArray viewsJson = profileJson.getJsonArray("viewPermissions");

		for (JsonValue jv : viewsJson.getElements()) {
			ViewAccess access = reader.readPropertyValue(jv, ViewAccess.class,
					null);
			if (!permittedViews.contains(access.getId())) {
				viewPermissions.add(access);
				permittedViews.add(access.getId());
			}
		}

		return viewPermissions;
	}

	private List<WatchListAccess> getWatchlistPermissions(
			JsonObject profileJson, JsonReader reader) throws JsonException {

		List<WatchListAccess> watchlistPermissions = new ArrayList<WatchListAccess>();
		List<Integer> permittedWatchlist = new ArrayList<Integer>();

		JsonArray viewsJson = profileJson.getJsonArray("watchlistPermissions");

		for (JsonValue jv : viewsJson.getElements()) {
			WatchListAccess access = reader.readPropertyValue(jv,
					WatchListAccess.class, null);
			if (!permittedWatchlist.contains(access.getId())) {
				watchlistPermissions.add(access);
				permittedWatchlist.add(access.getId());
			}
		}

		return watchlistPermissions;
	}

	private List<User> getUsers(JsonObject profileJson) throws JsonException {
		List<User> users = new ArrayList<User>();
		JsonArray jsonUsersIds = profileJson.getJsonArray("usersIds");

		for (JsonValue jv : jsonUsersIds.getElements()) {
			int userid = jv.toJsonNumber().getIntValue();
			User user = new UserDao().getUser(userid);
			users.add(user);
		}

		return users;
	}
}
