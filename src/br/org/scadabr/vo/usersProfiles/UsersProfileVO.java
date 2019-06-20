package br.org.scadabr.vo.usersProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.org.scadabr.vo.permission.Permission;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.DataPointAccess;

public class UsersProfileVO implements Cloneable, JsonSerializable {

	public static final String XID_PREFIX = "UP_";

	private String name;

	private int id = Common.NEW_ID;

	private List<Integer> dataSourcePermissions;

	private List<DataPointAccess> dataPointPermissions;

	private List<WatchListAccess> watchlistPermissions;

	private List<ViewAccess> viewPermissions;

	private String xid;

	private User lastAppliedUser = null;

	private List<WatchList> watchlists;

	private List<Integer> usersIds;

	private List<View> views;

	public UsersProfileVO() {
		name = "";
		dataSourcePermissions = new ArrayList<Integer>();
		dataPointPermissions = new ArrayList<DataPointAccess>();
		watchlistPermissions = new ArrayList<WatchListAccess>();
		viewPermissions = new ArrayList<ViewAccess>();
		watchlists = new ArrayList<WatchList>();
		views = new ArrayList<View>();
		usersIds = new ArrayList<Integer>();
	}

	public String getXid() {
		return xid;
	}

	public void setXid(String xid) {
		this.xid = xid;
	}

	public String getName() {
		return name;
	}

	public void setName(String username) {
		this.name = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Integer> getDataSourcePermissions() {
		return dataSourcePermissions;
	}

	public void setDataSourcePermissions(List<Integer> dataSourcePermissions) {
		this.dataSourcePermissions = dataSourcePermissions;
	}

	public List<DataPointAccess> getDataPointPermissions() {
		return dataPointPermissions;
	}

	public void setDataPointPermissions(
			List<DataPointAccess> dataPointPermissions) {
		this.dataPointPermissions = dataPointPermissions;
	}

	public List<WatchListAccess> getWatchlistPermissions() {
		return watchlistPermissions;
	}

	public void setWatchlistPermissions(
			List<WatchListAccess> watchlistPermissions) {
		this.watchlistPermissions = watchlistPermissions;
	}

	public void setViewPermissions(List<ViewAccess> viewPermissions) {
		this.viewPermissions = viewPermissions;
	}

	public List<ViewAccess> getViewPermissions() {
		return this.viewPermissions;
	}

	// some getters/setters were renamed to retrieve/define to avoid Serotonin
	// JSON writer implementation to find them
	public User retrieveLastAppliedUser() {
		return lastAppliedUser;
	}

	public void defineWatchlists(List<WatchList> watchlists) {
		this.watchlists = watchlists;
	}

	public List<WatchList> retrieveWatchlists() {
		return this.watchlists;
	}

	public void defineViews(List<View> views) {
		this.views = views;
	}

	public List<View> retrieveViews() {
		return this.views;
	}

	public void defineUsers(List<Integer> users) {
		this.usersIds = users;
	}

	public void apply(User user) {
		user.setDataSourcePermissions(dataSourcePermissions);
		user.setDataPointPermissions(dataPointPermissions);
		applyWatchlistPermissions(user);
		applyViewPermissions(user);
		user.setUserProfile(this);
		lastAppliedUser = user;
	}

	private void applyWatchlistPermissions(User user) {

		if (this.watchlistPermissions.isEmpty()) {
			List<ShareUser> nonePermissions = new ArrayList<ShareUser>();
			for (Integer userId : this.usersIds) {
				ShareUser newUserPermissions = new ShareUser();
				newUserPermissions.setAccessType(ShareUser.ACCESS_NONE);
				newUserPermissions.setUserId(userId);
				nonePermissions.add(newUserPermissions);
			}

			for (WatchList watchList : this.watchlists) {
				watchList.setWatchListUsers(nonePermissions);
			}
		}

		else {

			for (WatchList watchList : this.watchlists) {

				Map<Integer, ShareUser> oldPermissionsByUserId = new HashMap<Integer, ShareUser>();
				Map<Integer, Permission> newPermissionsByWatchListId = new HashMap<Integer, Permission>();

				for (Permission newPermission : this.watchlistPermissions) {
					newPermissionsByWatchListId.put(newPermission.getId(),
							newPermission);
				}

				if (!newPermissionsByWatchListId.containsKey(watchList.getId())) {
					WatchListAccess noneAccess = new WatchListAccess(
							watchList.getId(), ShareUser.ACCESS_NONE);
					this.watchlistPermissions.add(noneAccess);
					newPermissionsByWatchListId.put(noneAccess.getId(),
							noneAccess);
				}

				for (ShareUser oldPermission : watchList.getWatchListUsers()) {
					oldPermissionsByUserId.put(oldPermission.getUserId(),
							oldPermission);

					if ((oldPermission.getUserId() == user.getId() || this.usersIds
							.contains(oldPermission.getUserId()))
							&& newPermissionsByWatchListId
									.containsKey(watchList.getId())) {
						oldPermission.setAccessType(newPermissionsByWatchListId
								.get(watchList.getId()).getPermission());
					}
				}

				if (!oldPermissionsByUserId.containsKey(user.getId())) {
					ShareUser newUserPermissions = new ShareUser();
					newUserPermissions
							.setAccessType(newPermissionsByWatchListId.get(
									watchList.getId()).getPermission());
					newUserPermissions.setUserId(user.getId());
					watchList.getWatchListUsers().add(newUserPermissions);
				}

			}
		}

	}

	private void applyViewPermissions(User user) {

		if (this.viewPermissions.isEmpty()) {
			List<ShareUser> nonePermissions = new ArrayList<ShareUser>();
			for (Integer userId : this.usersIds) {
				ShareUser newUserPermissions = new ShareUser();
				newUserPermissions.setAccessType(ShareUser.ACCESS_NONE);
				newUserPermissions.setUserId(userId);
				nonePermissions.add(newUserPermissions);
			}

			for (View view : this.views) {
				view.setViewUsers(nonePermissions);
			}
		}

		else {

			for (View view : this.views) {

				Map<Integer, ShareUser> oldPermissionsByUserId = new HashMap<Integer, ShareUser>();
				Map<Integer, Permission> newPermissionsByViewId = new HashMap<Integer, Permission>();

				for (Permission newPermission : this.viewPermissions) {
					newPermissionsByViewId.put(newPermission.getId(),
							newPermission);
				}

				if (!newPermissionsByViewId.containsKey(view.getId())) {
					ViewAccess noneAccess = new ViewAccess(view.getId(),
							ShareUser.ACCESS_NONE);
					this.viewPermissions.add(noneAccess);
					newPermissionsByViewId.put(noneAccess.getId(), noneAccess);
				}

				for (ShareUser oldPermission : view.getViewUsers()) {
					oldPermissionsByUserId.put(oldPermission.getUserId(),
							oldPermission);

					if ((oldPermission.getUserId() == user.getId() || this.usersIds
							.contains(oldPermission.getUserId()))
							&& newPermissionsByViewId.containsKey(view.getId())) {
						oldPermission.setAccessType(newPermissionsByViewId.get(
								view.getId()).getPermission());
					}
				}

				if (!oldPermissionsByUserId.containsKey(user.getId())) {
					ShareUser newUserPermissions = new ShareUser();
					newUserPermissions.setAccessType(newPermissionsByViewId
							.get(view.getId()).getPermission());
					newUserPermissions.setUserId(user.getId());
					view.getViewUsers().add(newUserPermissions);
				}

			}
		}
	}

	public void jsonDeserialize(JsonReader reader, JsonObject profileJson)
			throws JsonException {
		// Note: data source permissions are explicitly deserialized by the
		// import/export because the data sources and
		// points need to be certain to exist before we can resolve the xids.
	}

	public void jsonSerialize(Map<String, Object> map) {
		map.put("dataSourcePermissions", dataSourcePermissions);
		map.put("dataPointPermissions", dataPointPermissions);
		map.put("viewPermissions", viewPermissions);
		map.put("watchlistPermissions", watchlistPermissions);
		map.put("usersIds", usersIds);
	}
}
