package br.org.scadabr.vo.usersProfiles;

import java.util.*;
import java.util.stream.Collectors;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;

import com.serotonin.json.*;
import com.serotonin.mango.Common;
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
		user.setDataSourceProfilePermissions(dataSourcePermissions);
		user.setDataPointProfilePermissions(dataPointPermissions);
		user.setWatchListProfilePermissions(watchlistPermissions);
		user.setViewProfilePermissions(viewPermissions);
		user.setUserProfile(this);
		lastAppliedUser = user;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UsersProfileVO)) return false;
		UsersProfileVO that = (UsersProfileVO) o;
		return getId() == that.getId();
	}

	@Override
	public int hashCode() {

		return Objects.hash(getId());
	}

	@Override
	public String toString() {
		return "\nUsersProfileVO{" +
				"name='" + name + '\'' +
				", id=" + id +
				", dataSourcePermissions=" + dataSourcePermissions +
				", dataPointPermissions=" + dataPointPermissions +
				", watchlistPermissions=" + watchlistPermissions +
				", viewPermissions=" + viewPermissions +
				", xid='" + xid + '\'' +
				", lastAppliedUser=" + lastAppliedUserId() +
				", watchlists=" + watchListsIds(watchlists) +
				", usersIds=" + usersIds +
				", views=" + viewsIds(views) +
				"}";
	}

    private Object lastAppliedUserId() {
        return lastAppliedUser == null ?  "null" : lastAppliedUser.getId();
    }

    private List<String> watchListsIds(List<WatchList> watchLists) {
		if(watchLists != null && !watchLists.isEmpty()) {
			return watchLists.stream().filter(Objects::nonNull)
                    .map(WatchList::getId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private List<String> viewsIds(List<View> views) {
		if(views != null && !views.isEmpty()) {
			return views.stream().filter(Objects::nonNull)
                    .map(View::getId)
					.map(String::valueOf)
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
}
